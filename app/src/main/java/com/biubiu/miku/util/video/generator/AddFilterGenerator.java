package com.biubiu.miku.util.video.generator;

import android.util.Log;

import com.biubiu.miku.model.Post;
import com.biubiu.miku.model.PostVideo;
import com.biubiu.miku.util.ThreadPool;
import com.biubiu.miku.util.camera.FilterTools;
import com.biubiu.miku.util.video.action.filter.FilterThemeType;
import com.biubiu.miku.util.video.ffmpeg.FFmpegUtils;
import com.biubiu.miku.util.video.task.VideoContentTask;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;

import java.io.File;
import java.util.List;

public class AddFilterGenerator extends VideoActionGenerator {

  public AddFilterGenerator(VideoProcessCallback videoProcessCallback, PostVideo postVideo) {
    super(videoProcessCallback, postVideo);
  }

  @Override
  public void generate(VideoContentTask videoContentTask, Post post,
      int actionLocationTaskPosition) {
    FilterThemeType filterThemeType = videoContentTask.getFilterThemeType();
    if (filterThemeType != null && filterThemeType != FilterThemeType.ORIGIN) {
      addFilter(videoContentTask, new VideoProcessCallback() {
        @Override
        public void success(long id, String videoPath) {
          videoContentTask.setProcessVideoFilePath(videoPath);
          nextGenerate(videoContentTask, post, actionLocationTaskPosition + 1);
        }

        @Override
        public void failure(String message) {
          nextGenerate(videoContentTask, post, actionLocationTaskPosition + 1);
        }
      });
    } else {
      nextGenerate(videoContentTask, post, actionLocationTaskPosition + 1);
    }
  }

  private void addFilter(VideoContentTask videoContentTask,
      VideoProcessCallback videoProcessCallback) {
    String srcVideoPath = videoContentTask.getProcessVideoFilePath();
    String destinationVideoFilePath =
        srcVideoPath.substring(
            0, srcVideoPath.lastIndexOf(".")) + "_filter"
            + System.currentTimeMillis()
            + ".mp4";
    if (videoContentTask.getFilterThemeType() != FilterThemeType.CHAPLIN) {
      List<String> videoSheetPathList = videoContentTask.getVideoSheetFilePathList();
      long duration = videoContentTask.getVideoMetaData().getDuration();
      FFmpegUtils.addCurvesFilter(videoContentTask.getProcessVideoFilePath(),
          FilterTools.getFilterCurves(videoContentTask.getFilterThemeType()),
          destinationVideoFilePath, new FFmpegExecuteResponseHandler() {
            @Override
            public void onSuccess(String message) {
              Log.e("addFilter", "onSuccess:" + message);
              ThreadPool.getInstance().execute(() -> {
                File videoTmpFile = new File(srcVideoPath);
                if (videoTmpFile.isFile()) {
                  videoTmpFile.delete();
                }
              });
              videoProcessCallback.success(videoContentTask.getId(), destinationVideoFilePath);
            }

            @Override
            public void onProgress(String message) {
              Log.e("addFilter",
                  "onProgress:" + destinationVideoFilePath + "   " + message);
            }

            @Override
            public void onFailure(String message) {
              Log.e("addFilter", "onFailure:" + message);
              ThreadPool.getInstance().execute(() -> {
                File videoTmpFile = new File(destinationVideoFilePath);
                if (videoTmpFile.isFile()) {
                  videoTmpFile.delete();
                }
              });
              videoProcessCallback.success(videoContentTask.getId(), srcVideoPath);
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
      FFmpegUtils.addBlackWhiteFilter(videoContentTask.getProcessVideoFilePath(),
          destinationVideoFilePath,
          new FFmpegExecuteResponseHandler() {
            @Override
            public void onSuccess(String message) {
              Log.e("addFilter", "onSuccess:" + message);
              ThreadPool.getInstance().execute(() -> {
                File videoTmpFile = new File(srcVideoPath);
                if (videoTmpFile.isFile()) {
                  videoTmpFile.delete();
                }
              });
              videoProcessCallback.success(videoContentTask.getId(), destinationVideoFilePath);
            }

            @Override
            public void onProgress(String message) {
              Log.e("addFilter", "onProgress:" + message);
            }

            @Override
            public void onFailure(String message) {
              Log.e("addFilter", "onFailure:" + message);
              ThreadPool.getInstance().execute(() -> {
                File videoTmpFile = new File(destinationVideoFilePath);
                if (videoTmpFile.isFile()) {
                  videoTmpFile.delete();
                }
              });
              videoProcessCallback.success(videoContentTask.getId(), srcVideoPath);
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
}
