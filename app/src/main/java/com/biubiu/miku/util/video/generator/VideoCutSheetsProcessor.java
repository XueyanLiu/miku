package com.biubiu.miku.util.video.generator;

import android.util.Log;

import com.biubiu.miku.util.video.VideoUtils;
import com.biubiu.miku.util.video.ffmpeg.FFmpegUtils;
import com.biubiu.miku.util.video.task.VideoContentTaskManager;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VideoCutSheetsProcessor {
  public static final int MAX_SHEET_DURATION = 6000;

  public static void process(String originVideoPath,
      VideoCutSheetsProcessCallback videoCutSheetsProcessCallback) {
    long duration = VideoUtils.getMediaDuration(originVideoPath);
    int sheepCount = (int) (Math.ceil(duration / (float) MAX_SHEET_DURATION));
    List<String> videoSheetFilePathList = new ArrayList<>();
    List<String> cutVideoList = new ArrayList<>();
    List<CutVideoData> cutVideoDataList = new ArrayList<>();
    for (int i = 0; i < sheepCount; i++) {
      int startPosition;
      int sheepDuration;
      String sheepDestinationVideoFilePath =
          originVideoPath.substring(
              0, originVideoPath.lastIndexOf(".")) + "_sheep_" + i
              + System.currentTimeMillis()
              + ".mp4";
      cutVideoList.add(sheepDestinationVideoFilePath);
      if (i == 0) {
        startPosition = 0;
      } else {
        startPosition = i * MAX_SHEET_DURATION + 1;
      }
      if (i < sheepCount - 1) {
        sheepDuration = MAX_SHEET_DURATION;
      } else {
        sheepDuration = (int) duration - (sheepCount - 1) * MAX_SHEET_DURATION;
      }
      cutVideoDataList
          .add(new CutVideoData(sheepDestinationVideoFilePath, startPosition, sheepDuration));
    }
    Map<CutVideoData, Integer> threadCounts = new HashMap<>();
    for (int i = 0; i < cutVideoDataList.size(); i++) {
      threadCounts.put(cutVideoDataList.get(i),
          FFmpegUtils.BEST_THREAD_COUNT / cutVideoDataList.size());
    }
    if (FFmpegUtils.BEST_THREAD_COUNT % cutVideoDataList.size() != 0) {
      int surplusThreadCountSize = FFmpegUtils.BEST_THREAD_COUNT % cutVideoDataList.size();
      Collections.sort(cutVideoDataList,
          (lhs, rhs) -> lhs.getDuration() - rhs.getDuration());
      for (int i = 0; i < surplusThreadCountSize; i++) {
        threadCounts.put(cutVideoDataList.get(i),
            threadCounts.get(cutVideoDataList.get(i)) + 1);
      }
    }
    for (CutVideoData cutVideoData : cutVideoDataList) {
      FFmpegUtils.cutMedia(originVideoPath, cutVideoData.getVideoPath(),
          cutVideoData.getStartPosition(),
          cutVideoData.getDuration(),
          threadCounts.get(cutVideoData), new FFmpegExecuteResponseHandler() {
            @Override
            public void onSuccess(String message) {
              Log.e("cutMediaSheep",
                  "onSuccess：" + cutVideoData.getVideoPath() + " \n" + message);
              videoSheetFilePathList.add(cutVideoData.getVideoPath());
              Log.e("cutMediaSheep",
                  "onSuccess：" + "  videoSheetFilePathList.length:" + videoSheetFilePathList.size()
                      + "   sheepCount:" + sheepCount);
              if (videoSheetFilePathList.size() == sheepCount) {
                if (videoCutSheetsProcessCallback != null) {
                  videoCutSheetsProcessCallback.success(cutVideoList);
                }
              }
            }

            @Override
            public void onProgress(String message) {
              Log.e("cutMediaSheep",
                  "onProgress：" + cutVideoData.getVideoPath() + " \n" + message);
            }

            @Override
            public void onFailure(String message) {
              Log.e("cutMediaSheep",
                  "onFailure：" + cutVideoData.getVideoPath() + " \n" + message);
            }

            @Override
            public void onStart() {
              Log.e("cutMediaSheep", "onStart");
            }

            @Override
            public void onFinish() {
              Log.e("cutMediaSheep", "onFinish");
            }
          });
    }
  }

  private static void cutSheetMedia(String originVideoPath, int sheetCount, long videoDuration,
                                    int sheetNum, List<String> videoSheetFilePathList) {
    int startPosition;
    int sheetDuration;
    if (sheetNum == 0) {
      startPosition = 0;
    } else {
      startPosition = sheetNum * MAX_SHEET_DURATION + 1;
    }
    if (sheetNum < sheetCount - 1) {
      sheetDuration = MAX_SHEET_DURATION;
    } else {
      sheetDuration = (int) videoDuration - (sheetCount - 1) * MAX_SHEET_DURATION;
    }
    String sheetDestinationVideoFilePath =
        originVideoPath.substring(
            0, originVideoPath.lastIndexOf(".")) + "_sheep_" + sheetNum
            + System.currentTimeMillis()
            + ".mp4";
    FFmpegUtils.cutMedia(originVideoPath, sheetDestinationVideoFilePath, startPosition,
        sheetDuration, new FFmpegExecuteResponseHandler() {
          @Override
          public void onSuccess(String message) {
            Log.e("cutMediaSheep",
                "onSuccess：" + sheetDestinationVideoFilePath + "  \n" + message);
            if (sheetNum < sheetCount - 2) {
              videoSheetFilePathList.add(sheetDestinationVideoFilePath);
              cutSheetMedia(originVideoPath, sheetCount, videoDuration, sheetNum + 1,
                  videoSheetFilePathList);
            } else {
              VideoContentTaskManager.getInstance().getCurrentContentTask()
                  .setVideoSheetFilePathList(videoSheetFilePathList);
            }
          }

          @Override
          public void onProgress(String message) {
            Log.e("cutMediaSheep",
                "onProgress：" + sheetDestinationVideoFilePath + "  \n" + message);
          }

          @Override
          public void onFailure(String message) {
            Log.e("cutMediaSheep",
                "onFailure：" + sheetDestinationVideoFilePath + "  \n" + message);
          }

          @Override
          public void onStart() {
            Log.e("cutMediaSheep", "onStart");
          }

          @Override
          public void onFinish() {
            Log.e("cutMediaSheep", "onFinish");
          }
        });
  }

  static class CutVideoData {
    private String videoPath;
    private int startPosition;
    private int duration;

    public CutVideoData(String videoPath, int startPosition, int duration) {
      this.videoPath = videoPath;
      this.startPosition = startPosition;
      this.duration = duration;
    }

    public String getVideoPath() {
      return videoPath;
    }

    public int getDuration() {
      return duration;
    }

    public int getStartPosition() {
      return startPosition;
    }
  }

  public interface VideoCutSheetsProcessCallback {
    void success(List<String> videoSheetFilePathList);

    void failure(String message);
  }
}
