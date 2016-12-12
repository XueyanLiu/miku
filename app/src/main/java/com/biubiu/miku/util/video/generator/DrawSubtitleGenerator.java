package com.biubiu.miku.util.video.generator;

import android.util.Log;

import com.biubiu.miku.model.Post;
import com.biubiu.miku.model.PostVideo;
import com.biubiu.miku.util.video.action.RecordActionLocationTask;
import com.biubiu.miku.util.video.ffmpeg.FFmpegUtils;
import com.biubiu.miku.util.video.task.VideoContentTask;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;

public class DrawSubtitleGenerator extends VideoActionGenerator {

  public DrawSubtitleGenerator(VideoProcessCallback videoProcessCallback, PostVideo postVideo) {
    super(videoProcessCallback, postVideo);
  }

  @Override
  public void generate(VideoContentTask videoContentTask, Post post,
                       int actionLocationTaskPosition) {
    RecordActionLocationTask recordActionLocationTask =
        videoContentTask.getRecordActionLocationTasks().get(actionLocationTaskPosition);
    if (recordActionLocationTask != null) {
      drawSubtitle(videoContentTask, recordActionLocationTask,
          videoContentTask.getProcessVideoFilePath(),
          new VideoProcessCallback() {
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

  private void drawSubtitle(VideoContentTask videoContentTask,
      RecordActionLocationTask recordActionLocationTask, String processVideoPath,
      VideoProcessCallback videoProcessCallback) {
    String destinationVideoFilePath =
        processVideoPath.substring(
            0, processVideoPath.lastIndexOf(".")) + "_overlaysubtitle"
            + System.currentTimeMillis()
            + ".mp4";
    FFmpegUtils.overlaySubtitles(processVideoPath, recordActionLocationTask,
        destinationVideoFilePath,
        new FFmpegExecuteResponseHandler() {
          @Override
          public void onSuccess(String message) {
            Log.e("overlaySubtitles", "success：" + message);
            videoProcessCallback.success(videoContentTask.getId(), destinationVideoFilePath);
          }

          @Override
          public void onProgress(String message) {
            Log.e("overlaySubtitles", "progress：" + message);
          }

          @Override
          public void onFailure(String message) {
            Log.e("overlaySubtitles", "failure：" + message);
            videoProcessCallback.failure(message);
          }

          @Override
          public void onStart() {
            Log.e("overlaySubtitles", "start");
          }

          @Override
          public void onFinish() {
            Log.e("overlaySubtitles", "finish");
          }
        });
  }
}
