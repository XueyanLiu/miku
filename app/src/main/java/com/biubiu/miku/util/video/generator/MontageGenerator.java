package com.biubiu.miku.util.video.generator;

import android.util.Log;

import com.biubiu.miku.model.Post;
import com.biubiu.miku.model.PostVideo;
import com.biubiu.miku.util.video.action.RecordActionLocationTask;
import com.biubiu.miku.util.video.action.montage.MontageContent;
import com.biubiu.miku.util.video.action.montage.MontageType;
import com.biubiu.miku.util.video.action.montage.Speed;
import com.biubiu.miku.util.video.task.VideoContentTask;

// TODO:YANBINGWU 与videoTag相关逻辑合并
public class MontageGenerator extends VideoActionGenerator {
  private VideoContentTask videoContentTask;

  public MontageGenerator(VideoProcessCallback videoProcessCallback, PostVideo postVideo) {
    super(videoProcessCallback, postVideo);
  }

  @Override
  public void generate(VideoContentTask videoContentTask, Post post, int actionLocationTaskPosition) {
    this.videoContentTask = videoContentTask;
    RecordActionLocationTask recordActionLocationTask =
        videoContentTask.getRecordActionLocationTasks().get(actionLocationTaskPosition);
    if (recordActionLocationTask != null) {
      addMontage(recordActionLocationTask, videoContentTask.getProcessVideoFilePath(),
          new VideoProcessResponseHandler() {
            @Override
            public void onSuccess(String message, String filePath) {
              videoContentTask.setProcessVideoFilePath(filePath);
              nextGenerate(videoContentTask, post, actionLocationTaskPosition + 1);
            }
          });
    } else {
      nextGenerate(videoContentTask, post, actionLocationTaskPosition + 1);
    }
  }

  private void addMontage(RecordActionLocationTask recordActionLocationTask,
                          String processVideoPath, VideoProcessResponseHandler videoProcessResponseHandler) {
    MontageContent montageContent = (MontageContent) recordActionLocationTask.getActionContent();
    switch (montageContent.getMontageType()) {
      case FREEZE:
        Speed freezeSpeed =
            new Speed(MontageType.FREEZE,
                recordActionLocationTask.getTimeLine().getStartTimeMs(),
                recordActionLocationTask.getTimeLine().getEndTimeMs()
                    - recordActionLocationTask.getTimeLine().getStartTimeMs());
        VideoMontageModifier.modify(processVideoPath, freezeSpeed,
            videoContentTask.getVideoMetaData().getRotation(),
            new VideoProcessResponseHandler() {
              @Override
              public void onSuccess(String message, String filePath) {
                Log.e("modify", "modify message:" + message + "   filePath:" + filePath);
                videoProcessResponseHandler.onSuccess("", filePath);
              }
            });
        break;
      case SLOW_MOTION:
        Speed slowSpeed =
            new Speed(MontageType.SLOW_MOTION,
                recordActionLocationTask.getTimeLine().getStartTimeMs(),
                recordActionLocationTask.getTimeLine().getEndTimeMs()
                    - recordActionLocationTask.getTimeLine().getStartTimeMs());
        VideoMontageModifier.modify(processVideoPath, slowSpeed,
            videoContentTask.getVideoMetaData().getRotation(),
            new VideoProcessResponseHandler() {
              @Override
              public void onSuccess(String message, String filePath) {
                Log.e("modify", "modify message:" + message + "   filePath:" + filePath);
                videoProcessResponseHandler.onSuccess("", filePath);
              }
            });
        break;
      case FORWARD:
        Speed forwardSpeed =
            new Speed(MontageType.FORWARD,
                recordActionLocationTask.getTimeLine().getStartTimeMs(),
                recordActionLocationTask.getTimeLine().getEndTimeMs()
                    - recordActionLocationTask.getTimeLine().getStartTimeMs());
        VideoMontageModifier.modify(processVideoPath, forwardSpeed,
            videoContentTask.getVideoMetaData().getRotation(),
            new VideoProcessResponseHandler() {
              @Override
              public void onSuccess(String message, String filePath) {
                Log.e("modify", "modify message:" + message + "   filePath:" + filePath);
                videoProcessResponseHandler.onSuccess("", filePath);
              }
            });
        break;
      case REPEAT:
        Speed repeatSpeed =
            new Speed(MontageType.REPEAT,
                recordActionLocationTask.getTimeLine().getStartTimeMs(),
                recordActionLocationTask.getTimeLine().getEndTimeMs()
                    - recordActionLocationTask.getTimeLine().getStartTimeMs());
        VideoMontageModifier.modify(processVideoPath, repeatSpeed,
            videoContentTask.getVideoMetaData().getRotation(),
            new VideoProcessResponseHandler() {
              @Override
              public void onSuccess(String message, String filePath) {
                Log.e("modify", "modify message:" + message + "   filePath:" + filePath);
                videoProcessResponseHandler.onSuccess("", filePath);
              }
            });
        break;
    }
  }

}
