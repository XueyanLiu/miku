package com.biubiu.miku.util.video.generator;

import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.biubiu.miku.model.Post;
import com.biubiu.miku.model.PostVideo;
import com.biubiu.miku.util.FileUtils;
import com.biubiu.miku.util.ThreadPool;
import com.biubiu.miku.util.video.VideoUtils;
import com.biubiu.miku.util.video.action.ActionImageData;
import com.biubiu.miku.util.video.action.RecordActionLocationTask;
import com.biubiu.miku.util.video.action.videoTag.DefaultVideoTagManager;
import com.biubiu.miku.util.video.action.videoTag.VideoTagContent;
import com.biubiu.miku.util.video.ffmpeg.FFmpegUtils;
import com.biubiu.miku.util.video.task.VideoContentTask;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;

import java.io.File;
import java.util.List;

public class DrawVideoTagGenerator extends VideoActionGenerator {

  public DrawVideoTagGenerator(VideoProcessCallback videoProcessCallback, PostVideo postVideo) {
    super(videoProcessCallback, postVideo);
  }

  @Override
  public void generate(VideoContentTask videoContentTask, Post post, int actionLocationTaskPosition) {
    RecordActionLocationTask recordActionLocationTask =
        videoContentTask.getRecordActionLocationTasks().get(actionLocationTaskPosition);
    if (recordActionLocationTask != null) {
      ThreadPool.getInstance().execute(() -> {
        VideoTagContent videoTagContent = (VideoTagContent) recordActionLocationTask.getActionContent();
        if (!TextUtils.isEmpty(videoTagContent.getResPrefix())) {
          String prefix = videoTagContent.getResPrefix();
          if (prefix.equals("classic_left") || prefix.equals("classic_right")
              || prefix.equals("upper") || prefix.equals("lower")
              || prefix.equals("white_left") || prefix.equals("white_right")
              || prefix.equals("cool_left") || prefix.equals("cool_right")) {
            FileUtils.mkdir(FileUtils.FILE_BLACK_POINT + "/videotag");
            File blackPointFile = new File(FileUtils.FILE_BLACK_POINT + "/videotag");
            int number = blackPointFile.listFiles().length;
            if (number != DefaultVideoTagManager.BLACK_POINT_NUM + 1) {
              if (number != 0) {
                FileUtils.deleteFile(blackPointFile);
                FileUtils.mkdir(FileUtils.FILE_BLACK_POINT);
              }
              List<String> videoTagImagePathList = videoTagContent.getVideoTagImageData().getVideoTagImagePathList();
              for (String videoTagFrameName : videoTagImagePathList) {
                File videoTagFrameImageFile = new File(FileUtils.FILE_BLACK_POINT + "/" + videoTagFrameName);
                ActionImageData actionImageData = VideoUtils.saveVideoTagFrameToFile(
                    videoTagContent.getVideoTagImageData().getSourceType(), videoTagFrameName,
                    videoTagFrameImageFile.getParent(), videoContentTask.getProcessVideoFilePath(),
                    videoTagFrameImageFile.getName());
                videoTagContent.getVideoTagFrameImagePathMap().put(videoTagFrameName, actionImageData);
              }
            } else {
              float proportion = VideoUtils.getVideoProportion(videoTagContent.getVideoTagImageData().getSourceType(), videoContentTask.getProcessVideoFilePath());
              List<String> videoTagImagePathList = videoTagContent.getVideoTagImageData().getVideoTagImagePathList();
              for (String videoTagFrameName : videoTagImagePathList) {
                videoTagContent.getVideoTagFrameImagePathMap().put(videoTagFrameName, new ActionImageData(proportion, FileUtils.FILE_BLACK_POINT + "/" + videoTagFrameName));
              }
            }
          }
        }
        new Handler(Looper.getMainLooper()).post(() ->
            drawVideoTag(videoContentTask, recordActionLocationTask,
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
                }));
      });
    } else {
      nextGenerate(videoContentTask, post, actionLocationTaskPosition + 1);
    }
  }

  private void drawVideoTag(VideoContentTask videoContentTask,
                            RecordActionLocationTask recordActionLocationTask,
                            String processVideoPath,
                            VideoProcessCallback videoProcessCallback) {
    MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
    mediaMetadataRetriever.setDataSource(processVideoPath);
    int rotation =
        Integer.parseInt(mediaMetadataRetriever
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));
    mediaMetadataRetriever.release();
    ThreadPool.getInstance().execute(() -> {
      String destinationVideoFilePath =
          processVideoPath.substring(
              0, processVideoPath.lastIndexOf(".")) + "_overlayVideoTag"
              + ".mp4";
      FFmpegUtils.overlayNormalVideoTag(processVideoPath, recordActionLocationTask,
          destinationVideoFilePath, rotation, new FFmpegExecuteResponseHandler() {
            @Override
            public void onSuccess(String message) {
              Log.e("overlayVideoTag", "success：" + message);

              VideoTagContent videoTagContent = (VideoTagContent) recordActionLocationTask.getActionContent();
              String destinationFrameVideoFilePath =
                  processVideoPath.substring(
                      0, processVideoPath.lastIndexOf(".")) + "_overlayVideoTag_frame"
                      + ".mp4";
              String prefix = videoTagContent.getResPrefix();
              if (!TextUtils.isEmpty(prefix)
                  && (prefix.equals("classic_left") || prefix.equals("classic_right")
                  || (prefix.equals("upper") || prefix.equals("lower")
                  || prefix.equals("white_left") || prefix.equals("white_right")
                  || prefix.equals("cool_left") || prefix.equals("cool_right")))) {
                FFmpegUtils.overlayFrameVideoTag(destinationVideoFilePath, recordActionLocationTask,
                    destinationFrameVideoFilePath, rotation, new FFmpegExecuteResponseHandler() {
                      @Override
                      public void onSuccess(String message) {
                        Log.e("overlayVideoTag", "success：" + message);
                        videoProcessCallback.success(videoContentTask.getId(), destinationFrameVideoFilePath);
                      }

                      @Override
                      public void onProgress(String message) {
                        Log.e("overlayVideoTag", "progress：" + message);
                      }

                      @Override
                      public void onFailure(String message) {
                        Log.e("overlayVideoTag", "failure：" + message);
                        videoProcessCallback.failure(message);
                      }

                      @Override
                      public void onStart() {
                        Log.e("overlayVideoTag", "start");
                      }

                      @Override
                      public void onFinish() {
                        Log.e("overlayVideoTag", "finish");
                      }
                    });
              }else {
                videoProcessCallback.success(videoContentTask.getId(), destinationVideoFilePath);
              }
            }

            @Override
            public void onProgress(String message) {
              Log.e("overlayVideoTag", "progress：" + message);
            }

            @Override
            public void onFailure(String message) {
              Log.e("overlayVideoTag", "failure：" + message);
              videoProcessCallback.failure(message);
            }

            @Override
            public void onStart() {
              Log.e("overlayVideoTag", "start");
            }

            @Override
            public void onFinish() {
              Log.e("overlayVideoTag", "finish");
            }
          });

    });
  }
}
