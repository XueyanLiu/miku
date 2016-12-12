package com.biubiu.miku.util.video.generator;

import android.util.Log;

import com.biubiu.miku.model.Post;
import com.biubiu.miku.model.PostVideo;
import com.biubiu.miku.util.video.action.ActionContent;
import com.biubiu.miku.util.video.ffmpeg.FFmpegUtils;
import com.biubiu.miku.util.video.task.VideoContentTask;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;

public class VideoProcessControler {

  public static void process(VideoContentTask videoContentTask,
      VideoProcessCallback videoProcessCallback,
      PostVideo postVideo,
      Post post,
      int position) {
    processTask(videoContentTask, videoProcessCallback, postVideo, post, position);
  }

  static void processTask(VideoContentTask videoContentTask,
      VideoProcessCallback videoProcessCallback, PostVideo postVideo, Post post, int position) {
    if (videoContentTask.getRecordActionLocationTasks() != null
        && !videoContentTask.getRecordActionLocationTasks().isEmpty()
        && videoContentTask.getRecordActionLocationTasks().size() > position) {
      ActionContent actionContent =
          videoContentTask.getRecordActionLocationTasks().get(position).getActionContent();
      switch (actionContent.getActionType()) {
        case MUSIC:
          new MixBackgroundMusicGenerator(videoProcessCallback, postVideo)
              .generate(videoContentTask, post, position);
          break;
        case FILTER:
          new AddFilterGenerator(videoProcessCallback, postVideo).generate(videoContentTask, post,
              position);
          break;
        case MONTAGE:
          new MontageGenerator(videoProcessCallback, postVideo).generate(videoContentTask, post,
              position);
          break;
        case CHAT_BOX:
          new DrawChatBoxGenerator(videoProcessCallback, postVideo).generate(videoContentTask, post,
              position);
          break;
        case RUN_MAN:
          new DrawRunManGenerator(videoProcessCallback, postVideo).generate(videoContentTask, post,
              position);
          break;
        case STICKER:
          new DrawStickerGenerator(videoProcessCallback, postVideo).generate(videoContentTask, post,
              position);
          break;
        case SUBTITLE:
          new DrawSubtitleGenerator(videoProcessCallback, postVideo).generate(videoContentTask,
              post, position);
          break;
        case VIDEO_TAG:
          new DrawVideoTagGenerator(videoProcessCallback, postVideo).generate(videoContentTask,
              post, position);
          break;
      }
    } else {
      if (videoContentTask.getVideoMetaData() == null
          || videoContentTask.getVideoMetaData().getRotation() == 0) {
        videoProcessCallback.success(videoContentTask.getId(),
            videoContentTask.getProcessVideoFilePath());
      } else {
        changeVideoMetaData(videoContentTask, videoProcessCallback);
      }
    }
  }

  private static void changeVideoMetaData(final VideoContentTask videoContentTask,
      final VideoProcessCallback videoProcessCallback) {
    String destinationVideoFilePath =
        videoContentTask.getProcessVideoFilePath().substring(
            0, videoContentTask.getProcessVideoFilePath().lastIndexOf(".")) + "_changeMetaData"
            + System.currentTimeMillis()
            + ".mp4";
    FFmpegUtils.changeVideoRotationMetaData(videoContentTask.getProcessVideoFilePath(),
        videoContentTask.getVideoMetaData().getRotation(), destinationVideoFilePath,
        new FFmpegExecuteResponseHandler() {
          @Override
          public void onSuccess(String message) {
            Log.e("changeVideoMetaData",
                "success：" + message + "   path:" + destinationVideoFilePath);
            videoContentTask.setProcessVideoFilePath(destinationVideoFilePath);
            videoProcessCallback.success(videoContentTask.getId(),
                videoContentTask.getProcessVideoFilePath());
          }

          @Override
          public void onProgress(String message) {
            Log.e("changeVideoMetaData", "onProgress：" + message);
          }

          @Override
          public void onFailure(String message) {
            Log.e("changeVideoMetaData", "onFailure：" + message);
            videoProcessCallback.success(videoContentTask.getId(),
                videoContentTask.getProcessVideoFilePath());
          }

          @Override
          public void onStart() {
            Log.e("changeVideoMetaData", "onStart");
          }

          @Override
          public void onFinish() {
            Log.e("changeVideoMetaData", "onFinish");
          }
        });
  }
}
