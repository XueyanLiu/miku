package com.biubiu.miku.widget.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

import com.biubiu.miku.R;
import com.biubiu.miku.util.AddTaskListener;
import com.biubiu.miku.util.camera.FilterTools;
import com.biubiu.miku.util.sound.SoundPoolPlayer;
import com.biubiu.miku.util.video.ConcatMediaCallback;
import com.biubiu.miku.util.video.VideoUtils;
import com.biubiu.miku.util.video.action.filter.FilterData;
import com.biubiu.miku.util.video.action.filter.FilterTheme;
import com.biubiu.miku.util.video.action.filter.FilterThemeType;
import com.biubiu.miku.util.video.ffmpeg.FFmpegUtils;
import com.biubiu.miku.util.video.task.VideoContentTask;
import com.biubiu.miku.util.video.task.VideoContentTaskManager;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ChooseFilterView extends LinearLayout{

  private float videoRatioWH;
  private String videoFilePath;
  private LinearLayout topListView;
  private FilterTheme selectFilterTheme;
  private List<NewFilterThemeItemView> filterThemeItemViewList = new ArrayList<>();
  private FilterThemeType selectFilterThemeType = FilterThemeType.ORIGIN;
  private PreviewVideoView previewVideoView;
  private String selectFilterVideoPath;
  private List<AddTaskListener> addTaskListeners = new ArrayList<>();
  private boolean isProgress;

  public ChooseFilterView(Context context) {
    super(context);
  }

  public ChooseFilterView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public ChooseFilterView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public void setPreVideoView(PreviewVideoView previewVideoView) {
    this.previewVideoView = previewVideoView;
  }

  {
    videoRatioWH =
        VideoContentTaskManager.getInstance().getCurrentContentTask().getVideoRatioWH();
    videoFilePath =
        VideoContentTaskManager.getInstance().getCurrentContentTask().getVideoPath();
    setOrientation(HORIZONTAL);
    setFilterThemeLayoutData(videoRatioWH);
  }

  private void setFilterThemeLayoutData(float videoRatioWH) {
    String coverPath = VideoUtils
        .getVideoCover(videoFilePath, VideoUtils.FILTER_COVER_NAME);
    List<FilterData> filterDataList = FilterTools.getFilterDataList();
    topListView = new LinearLayout(getContext());
    topListView.setOrientation(LinearLayout.HORIZONTAL);
    addView(topListView);
      for (int i = 0; i < filterDataList.size(); i++) {
        FilterData filterData = filterDataList.get(i);
        NewFilterThemeItemView filterThemeItemView = new NewFilterThemeItemView(getContext());
        filterThemeItemView.setFilterTheme(new FilterTheme(filterData, coverPath));
        filterThemeItemView.setOnClickListener(v -> {
          if (!isProgress) {
            isProgress = true;
            SoundPoolPlayer.getInstance().playSound(R.raw.click);
            setFilterThemeItemStatus(filterThemeItemViewList.indexOf(filterThemeItemView));
          }
        });
        filterThemeItemViewList.add(filterThemeItemView);
        topListView.addView(filterThemeItemView);
    }
    setFilterThemeItemStatus(FilterTools.filterIndexOf(
        VideoContentTaskManager.getInstance().getCurrentContentTask().getFilterThemeType()));
  }

  private void setFilterThemeItemStatus(int position) {
    FilterThemeType filterThemeType = selectFilterThemeType;
    for (int i = 0; i < filterThemeItemViewList.size(); i++) {
      NewFilterThemeItemView filterThemeItemView = filterThemeItemViewList.get(i);
      if (i == position) {
        filterThemeItemView.setSelected(true);
        selectFilterTheme = filterThemeItemViewList.get(position).getFilterTheme();
        filterThemeType = selectFilterTheme.getFilterData().getFilterThemeType();
      } else {
        filterThemeItemView.setSelected(false);
      }
    }
    if (selectFilterThemeType != filterThemeType) {
      selectFilterThemeType = filterThemeType;
      VideoContentTask.ProcessState cutSheetsState = VideoContentTaskManager.getInstance()
          .getCurrentContentTask().getVideoCutSheetsProcessState();
      switch (cutSheetsState) {
        case SUCCESS:
          setFilterThemeTypeVideo();
          break;
        case FAILURE:
          break;
        case PROGRESS:
          VideoContentTaskManager.getInstance().getCurrentContentTask()
              .addOnVideoCutSheetsStateChangeListener(
                  (processState, videoSheetFilePathList) -> {
                    switch (processState) {
                      case SUCCESS:
                        setFilterThemeTypeVideo();
                        break;
                    }
                  });
          break;
        case IDLE:
          VideoContentTaskManager.getInstance().getCurrentContentTask().cutSheets();
          VideoContentTaskManager.getInstance().getCurrentContentTask()
              .addOnVideoCutSheetsStateChangeListener(
                  (processState, videoSheetFilePathList) -> {
                    switch (processState) {
                      case SUCCESS:
                        setFilterThemeTypeVideo();
                        break;
                      case FAILURE:
                        break;
                      case PROGRESS:
                        break;
                    }
                  });
          break;
      }
    } else {
      isProgress = false;
    }
  }

  private void setFilterThemeTypeVideo() {
    String videoFilterThemeVideoPath = getFilterThemeVideoPath(
        VideoContentTaskManager.getInstance().getCurrentContentTask().getVideoPath(),
        selectFilterTheme.getFilterData().getFilterThemeType());
    if (!new File(videoFilterThemeVideoPath).exists()) {
      if (selectFilterTheme.getFilterData().getFilterThemeType() != FilterThemeType.ORIGIN) {
        if (addTaskListeners != null && !addTaskListeners.isEmpty()) {
          for (int i = addTaskListeners.size() - 1; i >= 0; i--) {
            addTaskListeners.get(i).onProgress();
          }
        }
        List<String> filterSheetFilePaths = new ArrayList<>();
        List<String> tempSheetFilePaths = new ArrayList<>();
        List<String> videoSheetFilePaths = VideoContentTaskManager.getInstance()
            .getCurrentContentTask().getVideoSheetFilePathList();
        for (String videoSheetFilePath : videoSheetFilePaths) {
          String destinationVideoFilePath =
              videoSheetFilePath.substring(
                  0, videoSheetFilePath.lastIndexOf(".")) + "_filter"
                  + selectFilterTheme.getFilterData().getFilterThemeType().toString()
                  + ".mp4";
          filterSheetFilePaths.add(destinationVideoFilePath);
          if (selectFilterTheme.getFilterData().getFilterThemeType() != FilterThemeType.CHAPLIN) {
            FFmpegUtils.addCurvesFilter(videoSheetFilePath, FilterTools.getFilterCurves(selectFilterTheme.getFilterData().getFilterThemeType()), destinationVideoFilePath, new FFmpegExecuteResponseHandler() {
              @Override
              public void onSuccess(String message) {
                Log.e("addFilter", "onSuccess:" + message);
                tempSheetFilePaths.add(destinationVideoFilePath);
                if (filterSheetFilePaths.size() == videoSheetFilePaths.size()) {
                  VideoUtils.concatIdenticalVideoList(filterSheetFilePaths, new File(videoSheetFilePath).getParent(), videoFilterThemeVideoPath, new ConcatMediaCallback() {
                    @Override
                    public void success(String outputFilePath) {
                      Log.e("concatFilter", "outputFilePath:" + outputFilePath);
                      selectFilterVideoPath = outputFilePath;
                      if (previewVideoView != null) {
                        previewVideoView.replay(selectFilterVideoPath);
                        isProgress = false;
                      }
                      if (addTaskListeners != null && !addTaskListeners.isEmpty()) {
                        for (int i = addTaskListeners.size() - 1; i >= 0; i--) {
                          addTaskListeners.get(i).onSuccess();
                        }
                      }
                    }

                    @Override
                    public void failure(Throwable throwable) {
                    }
                  });
                }
              }

              @Override
              public void onProgress(String message) {
                Log.e("addFilter", "onProgress:" + destinationVideoFilePath + "   " + message);
              }

              @Override
              public void onFailure(String message) {
                Log.e("addFilter", "onFailure:" + message);
                if (addTaskListeners != null && !addTaskListeners.isEmpty()) {
                  for (int i = addTaskListeners.size() - 1; i >= 0; i--) {
                    addTaskListeners.get(i).onFail();
                  }
                }
              }

              @Override
              public void onStart() {
                Log.e("addFilter", "onStart");
              }

              @Override
              public void onFinish() {
                Log.e("addFilter", "onFinish");
              }
            });
          } else {
            FFmpegUtils.addBlackWhiteFilter(videoSheetFilePath,
                destinationVideoFilePath, new FFmpegExecuteResponseHandler() {
                  @Override
                  public void onSuccess(String message) {
                    Log.e("addFilter", "onSuccess:" + message);
                    tempSheetFilePaths.add(destinationVideoFilePath);
                    if (filterSheetFilePaths.size() == videoSheetFilePaths.size()) {
                      VideoUtils.concatIdenticalVideoList(filterSheetFilePaths,
                          new File(videoSheetFilePath).getParent(), videoFilterThemeVideoPath,
                          new ConcatMediaCallback() {
                            @Override
                            public void success(String outputFilePath) {
                              Log.e("concatFilter", "outputFilePath:" + outputFilePath);
                              selectFilterVideoPath = outputFilePath;
                              if (previewVideoView != null) {
                                isProgress = false;
                                previewVideoView.replay(selectFilterVideoPath);
                              }

                              if (addTaskListeners != null && !addTaskListeners.isEmpty()) {
                                for (int i = addTaskListeners.size() - 1; i >= 0; i--) {
                                  addTaskListeners.get(i).onSuccess();
                                }
                              }
                            }

                            @Override
                            public void failure(Throwable throwable) {
                            }
                          });
                    }
                  }

                  @Override
                  public void onProgress(String message) {
                    Log.e("addFilter",
                        "onProgress:" + destinationVideoFilePath + "   " + message);
                  }

                  @Override
                  public void onFailure(String message) {
                    Log.e("addFilter", "onFailure:" + message);
                    if (addTaskListeners != null && !addTaskListeners.isEmpty()) {
                      for (int i = addTaskListeners.size() - 1; i >= 0; i--) {
                        addTaskListeners.get(i).onFail();
                      }
                    }
                  }

                  @Override
                  public void onStart() {
                    Log.e("addFilter", "onStart");
                  }

                  @Override
                  public void onFinish() {
                    Log.e("addFilter", "onFinish");
                  }
                });
          }
        }
      } else {
        if (previewVideoView != null) {
          selectFilterVideoPath = VideoContentTaskManager.getInstance().getCurrentContentTask()
              .getProcessVideoFilePath();
          previewVideoView.replay(selectFilterVideoPath);
          isProgress = false;
        }
      }
    } else {
      selectFilterVideoPath = videoFilterThemeVideoPath;
      if (previewVideoView != null) {
        previewVideoView.replay(selectFilterVideoPath);
        isProgress = false;
      }
    }
    VideoContentTaskManager.getInstance().getCurrentContentTask()
        .setFilterThemeType(selectFilterThemeType);
    VideoContentTaskManager.getInstance().getCurrentContentTask()
        .setFilterVideoFilePath(selectFilterVideoPath);
  }

  private String getFilterThemeVideoPath(String videoPath, FilterThemeType filterThemeType) {
    String filterVideoDirectory = new File(videoPath).getParent();
    String filterVideoName = filterThemeType.name() + ".mp4";
    return filterVideoDirectory.endsWith(File.separator) ? filterVideoDirectory
        + filterVideoName : filterVideoDirectory + File.separator + filterVideoName;
  }

  public void addTaskListener(AddTaskListener listener) {
    if (addTaskListeners == null) {
      addTaskListeners = new ArrayList<>();
    }
    if (listener != null) {
      addTaskListeners.add(listener);
    }
  }

  public void removeTaskListener(AddTaskListener listener) {
    addTaskListeners.remove(listener);
  }

}
