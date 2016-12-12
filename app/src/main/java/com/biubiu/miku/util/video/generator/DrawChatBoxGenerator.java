package com.biubiu.miku.util.video.generator;

import android.media.MediaMetadataRetriever;
import android.util.Log;

import com.biubiu.miku.model.Post;
import com.biubiu.miku.model.PostVideo;
import com.biubiu.miku.util.video.action.RecordActionLocationTask;
import com.biubiu.miku.util.video.ffmpeg.FFmpegUtils;
import com.biubiu.miku.util.video.task.VideoContentTask;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;

// TODO:YANBINGWU 与videoTag相关逻辑合并
public class DrawChatBoxGenerator extends VideoActionGenerator {

  public DrawChatBoxGenerator(VideoProcessCallback videoProcessCallback, PostVideo postVideo) {
    super(videoProcessCallback, postVideo);
  }

  @Override
  public void generate(VideoContentTask videoContentTask, Post post, int actionLocationTaskPosition) {
    RecordActionLocationTask recordActionLocationTask =
        videoContentTask.getRecordActionLocationTasks().get(actionLocationTaskPosition);
    if (recordActionLocationTask != null) {
      drawChatBox(videoContentTask, recordActionLocationTask,
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

  private void drawChatBox(VideoContentTask videoContentTask,
                           RecordActionLocationTask recordActionLocationTask, String processVideoPath,
                           VideoProcessCallback videoProcessCallback) {
    MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
    mediaMetadataRetriever.setDataSource(processVideoPath);
    int rotation =
        Integer.parseInt(mediaMetadataRetriever
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));
    mediaMetadataRetriever.release();
    String destinationVideoFilePath =
        processVideoPath.substring(
            0, processVideoPath.lastIndexOf(".")) + "_overlayChatBox"
            + ".mp4";
    FFmpegUtils.overlayChatBox(processVideoPath, recordActionLocationTask,
        destinationVideoFilePath,
        rotation, new FFmpegExecuteResponseHandler() {
          @Override
          public void onSuccess(String message) {
            Log.e("drawChatBox", "success：" + message);
            videoProcessCallback.success(videoContentTask.getId(), destinationVideoFilePath);
          }

          @Override
          public void onProgress(String message) {
            Log.e("drawChatBox", "progress：" + message);
          }

          @Override
          public void onFailure(String message) {
            Log.e("drawChatBox", "failure：" + message);
            videoProcessCallback.failure(message);
          }

          @Override
          public void onStart() {
            Log.e("drawChatBox", "start");
          }

          @Override
          public void onFinish() {
            Log.e("drawChatBox", "finish");
          }
        });
  }
}
