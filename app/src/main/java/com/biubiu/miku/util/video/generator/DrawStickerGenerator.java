package com.biubiu.miku.util.video.generator;

import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.annimon.stream.Stream;
import com.biubiu.miku.model.Post;
import com.biubiu.miku.model.PostVideo;
import com.biubiu.miku.util.FileUtils;
import com.biubiu.miku.util.ThreadPool;
import com.biubiu.miku.util.video.VideoUtils;
import com.biubiu.miku.util.video.action.ActionImageData;
import com.biubiu.miku.util.video.action.RecordActionLocationTask;
import com.biubiu.miku.util.video.action.sticker.DefaultStickerManager;
import com.biubiu.miku.util.video.action.sticker.StickerContent;
import com.biubiu.miku.util.video.ffmpeg.FFmpegUtils;
import com.biubiu.miku.util.video.task.VideoContentTask;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

// TODO:YANBINGWU 与videoTag相关逻辑合并
public class DrawStickerGenerator extends VideoActionGenerator {
  private static String mixAudioFilePath = "";
  private static String mixVideoFilePath = "";
  private static boolean isSaveAudio = false;
  private static boolean isGetVideo = false;
  private boolean fullScreen = false;

  public DrawStickerGenerator(VideoProcessCallback videoProcessCallback, PostVideo postVideo) {
    super(videoProcessCallback, postVideo);
  }

  @Override
  public void generate(VideoContentTask videoContentTask, Post post, int actionLocationTaskPosition) {
    RecordActionLocationTask recordActionLocationTask
        = videoContentTask.getRecordActionLocationTasks().get(actionLocationTaskPosition);
    if (recordActionLocationTask != null) {
      generate(videoContentTask, post, recordActionLocationTask,
          videoContentTask.getProcessVideoFilePath(),
          actionLocationTaskPosition);
    } else {
      nextGenerate(videoContentTask, post, actionLocationTaskPosition + 1);
    }
  }

  private void generate(VideoContentTask videoContentTask, Post post,
                        RecordActionLocationTask recordActionLocationTask, String videoFilePath,
                        int actionLocationTaskPosition) {
    StickerContent stickerContent = (StickerContent) recordActionLocationTask.getActionContent();
    Stream.of(DefaultStickerManager.getInstance().getStickerImageGroupList()).forEach(stickerImageGroup -> {
      if (Stream.of(stickerImageGroup.getStickerImageDataList()).anyMatch(imageData -> imageData.equals(stickerContent.getStickerImageData()))) {
        if (stickerImageGroup.getGroupName().equals("气氛")) {
          fullScreen = true;
        }
      }
    });
    if (stickerContent.getStickerImageData().getSoundId() <= 0) {
      ThreadPool.getInstance().execute(() -> {
        File file = new File(videoContentTask.getProcessVideoFilePath());
        List<String> stickerImagePathList =
            stickerContent.getStickerImageData().getStickerImagePathList();
        for (String stickerFrameName : stickerImagePathList) {
          File stickerFrameImageFile = new File(file.getParent() + "/" + stickerFrameName);
          ActionImageData actionImageData = VideoUtils.saveStickerFrameToFile(
              stickerContent.getStickerImageData().getSourceType(), stickerFrameName,
              stickerFrameImageFile.getParent(), videoContentTask.getProcessVideoFilePath(),
              stickerFrameImageFile.getName(), fullScreen);
          stickerContent.addFrameImageData(stickerFrameName, actionImageData);
        }
        new Handler(Looper.getMainLooper())
            .post(() -> drawSticker(videoContentTask, stickerContent, recordActionLocationTask,
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
      saveAudioToSDCard(stickerContent, videoFilePath, new VideoProcessResponseHandler() {

        @Override
        public void onSuccess(String message, String filePath) {
          isSaveAudio = true;
          if (isGetVideo) {
            mixAudio(stickerContent, recordActionLocationTask, videoFilePath,
                new VideoProcessResponseHandler() {
                  @Override
                  public void onSuccess(String message, String filePath) {
                    mixAudioFilePath = filePath;
                    if (!TextUtils.isEmpty(mixAudioFilePath)) {
                      mixVideo(videoContentTask, post, actionLocationTaskPosition);
                    }
                  }
                });
          }
        }
      });
      String destinationVideoPath = videoFilePath.substring(
          0, videoFilePath.lastIndexOf(".")) + "sticker" + "_video.mp4";
      FFmpegUtils.getVideo(videoFilePath, destinationVideoPath, new FFmpegExecuteResponseHandler() {

        @Override
        public void onSuccess(String message) {
          Log.e("getVideo", "getVideo success：" + destinationVideoPath);
        }

        @Override
        public void onProgress(String message) {
          Log.e("getVideo", "onProgress：" + message);
        }

        @Override
        public void onFailure(String message) {
          Log.e("getVideo", "onFailure：" + message);
        }

        @Override
        public void onStart() {

        }

        @Override
        public void onFinish() {
          Log.e("getVideo", "onFinish：" + destinationVideoPath);
          isGetVideo = true;
          if (isSaveAudio) {
            Log.e("saveAudioToSDCard",
                "isSaveAudio：" + isSaveAudio + "   isGetVideo:" + isGetVideo);
            mixAudio(stickerContent, recordActionLocationTask, videoFilePath,
                new VideoProcessResponseHandler() {
                  @Override
                  public void onSuccess(String message, String filePath) {
                    mixAudioFilePath = filePath;
                    ThreadPool.getInstance().execute(() -> {
                      File file = new File(videoContentTask.getProcessVideoFilePath());
                      List<String> stickerImagePathList =
                          stickerContent.getStickerImageData().getStickerImagePathList();
                      for (String stickerFrameName : stickerImagePathList) {
                        File stickerFrameImageFile = new File(file.getParent() + "/" + stickerFrameName);
                        ActionImageData actionImageData = VideoUtils.saveStickerFrameToFile(
                            stickerContent.getStickerImageData().getSourceType(), stickerFrameName,
                            stickerFrameImageFile.getParent(), videoContentTask.getProcessVideoFilePath(),
                            stickerFrameImageFile.getName(), fullScreen);
                        stickerContent.addFrameImageData(stickerFrameName, actionImageData);
                      }
                      new Handler(Looper.getMainLooper())
                          .post(() -> drawSticker(videoContentTask, stickerContent, recordActionLocationTask,
                              destinationVideoPath,
                              new VideoProcessCallback() {
                                @Override
                                public void success(long id, String videoPath) {
                                  mixVideoFilePath = videoPath;
                                  if (!TextUtils.isEmpty(mixVideoFilePath)) {
                                    mixVideo(videoContentTask, post, actionLocationTaskPosition);
                                  }
                                }

                                @Override
                                public void failure(String message) {
                                  if (!TextUtils.isEmpty(mixVideoFilePath)) {
                                    mixVideo(videoContentTask, post, actionLocationTaskPosition);
                                  }
                                }
                              }));
                    });
                  }
                });
          }
        }

      });
    }
  }

  private void mixVideo(VideoContentTask videoContentTask, Post post,
                        int actionLocationTaskPosition) {
    generateVideo(mixVideoFilePath, mixAudioFilePath, new VideoProcessResponseHandler() {
      @Override
      public void onSuccess(String message, String filePath) {
        videoContentTask.setProcessVideoFilePath(filePath);
        nextGenerate(videoContentTask, post, actionLocationTaskPosition + 1);
      }
    });
  }

  private void generateVideo(String videoPath, String audioPath,
                             VideoProcessResponseHandler videoProcessResponseHandler) {
    Log.e("videoPath", "videoPath:" + videoPath);
    String destinationVideoPath = videoPath.substring(
        0, videoPath.lastIndexOf(".")) + "_video.mp4";
    FFmpegUtils.generateVideo(videoPath, audioPath, destinationVideoPath,
        new FFmpegExecuteResponseHandler() {
          @Override
          public void onSuccess(String message) {
            Log.e("generateVideo", "generateVideo onSuccess：" + destinationVideoPath);
            ThreadPool.getInstance().execute(() -> {
              File videoTmpFile = new File(videoPath);
              if (videoTmpFile.isFile()) {
                videoTmpFile.delete();
              }
              File audioTmpFile = new File(audioPath);
              if (audioTmpFile.isFile()) {
                audioTmpFile.delete();
              }
            });
            videoProcessResponseHandler.onSuccess("generateVideo:" + message,
                destinationVideoPath);
          }

          @Override
          public void onProgress(String message) {
            Log.e("generateVideo", "generateVideo onProgress：" + message);
            videoProcessResponseHandler.onProgress("generateVideo:" + message);
          }

          @Override
          public void onFailure(String message) {
            Log.e("generateVideo", "generateVideo onFailure：" + message);
            videoProcessResponseHandler.onFailure("generateVideo:" + message);
          }

          @Override
          public void onStart() {
            videoProcessResponseHandler.onStart();
          }

          @Override
          public void onFinish() {
            videoProcessResponseHandler.onFinish();
          }
        });
  }

  private void mixAudio(StickerContent stickerContent,
                        RecordActionLocationTask recordActionLocationTask,
                        String processVideoPath, VideoProcessResponseHandler videoProcessResponseHandler) {
    String destinationAudioPath = processVideoPath.substring(
        0, processVideoPath.lastIndexOf(".")) + "sticker" + "_audio.mp3";
    FFmpegUtils.getAudio(processVideoPath, destinationAudioPath,
        new FFmpegExecuteResponseHandler() {
          @Override
          public void onSuccess(String message) {
            Log.e("getAudio", "getAudio onSuccess：" + destinationAudioPath);
          }

          @Override
          public void onProgress(String message) {
            Log.e("getAudio", "onProgress：" + message);
          }

          @Override
          public void onFailure(String message) {
            Log.e("getAudio", "onFailure：" + message);
          }

          @Override
          public void onStart() {
            Log.e("getAudio", "onStart");
          }

          @Override
          public void onFinish() {
            Log.e("getAudio", "onFinish：" + destinationAudioPath);
            mixAudio(new MixAudioData(recordActionLocationTask, stickerContent),
                destinationAudioPath,
                videoProcessResponseHandler);
          }
        });
  }

  private void mixAudio(MixAudioData mixAudioData, String processAudioPath,
                        VideoProcessResponseHandler videoProcessResponseHandler) {
    RecordActionLocationTask recordActionLocationTask = mixAudioData.getRecordActionLocationTask();
    StickerContent stickerContent = mixAudioData.getStickerContent();
    Log.e("cutMediaToEnd", "cutMediaToEnd：" + recordActionLocationTask.getStartPosition());
    String destinationAudioFilePath =
        processAudioPath.substring(
            0, processAudioPath.lastIndexOf(".")) + "_drawStickerAudio"
            + ".mp3";
    if (recordActionLocationTask.getStartPosition() > 1) {
      FFmpegUtils.cutMediaToEnd(
          processAudioPath,
          destinationAudioFilePath,
          recordActionLocationTask.getStartPosition(), new FFmpegExecuteResponseHandler() {
            @Override
            public void onSuccess(String message) {
              Log.e("cutMediaToEnd", "cutMediaToEnd onSuccess：" + destinationAudioFilePath);
              String mixDestinationAudioFilePath =
                  processAudioPath.substring(
                      0, processAudioPath.lastIndexOf(".")) + "_drawStickerAudio"
                      + System.currentTimeMillis()
                      + ".mp3";
              FFmpegUtils.mixAudio(destinationAudioFilePath, stickerContent.getStickerImageData().getSoundPath(),
                  mixDestinationAudioFilePath, new FFmpegExecuteResponseHandler() {
                    @Override
                    public void onSuccess(String message) {
                      Log.e("mixAudio", "mixAudio onSuccess：" + mixDestinationAudioFilePath);
                      String cutDestinationAudioFilePath =
                          processAudioPath.substring(
                              0, processAudioPath.lastIndexOf(".")) + "_drawStickerAudio"
                              + System.currentTimeMillis()
                              + ".mp3";
                      FFmpegUtils.cutMedia(processAudioPath, cutDestinationAudioFilePath, 0,
                          recordActionLocationTask.getStartPosition(),
                          new FFmpegExecuteResponseHandler() {
                            @Override
                            public void onSuccess(String message) {
                              Log.e("cutMedia", "cutMedia onSuccess：" + cutDestinationAudioFilePath);
                              String concatDestinationVideoFilePath =
                                  processAudioPath.substring(
                                      0, processAudioPath.lastIndexOf(".")) + "_drawStickerAudio"
                                      + System.currentTimeMillis()
                                      + ".mp3";
                              List<String> concatAudioPathList = new ArrayList();
                              concatAudioPathList.add(cutDestinationAudioFilePath);
                              concatAudioPathList.add(mixDestinationAudioFilePath);
                              FFmpegUtils.concatAudio(cutDestinationAudioFilePath,
                                  mixDestinationAudioFilePath, concatDestinationVideoFilePath,
                                  new FFmpegExecuteResponseHandler() {
                                    @Override
                                    public void onSuccess(String message) {
                                      Log.e("concatAudio",
                                          "concatAudio onSuccess：" + concatDestinationVideoFilePath);
                                      videoProcessResponseHandler.onSuccess("", concatDestinationVideoFilePath);
                                    }

                                    @Override
                                    public void onProgress(String message) {
                                      Log.e("concatAudio", "concatAudio onProgress：" + message);
                                    }

                                    @Override
                                    public void onFailure(String message) {
                                      Log.e("concatAudio", "concatAudio onFailure：" + message);
                                    }

                                    @Override
                                    public void onStart() {

                                    }

                                    @Override
                                    public void onFinish() {

                                    }
                                  });
                            }

                            @Override
                            public void onProgress(String message) {
                              Log.e("cutMedia", "cutMedia onProgress：" + message);
                            }

                            @Override
                            public void onFailure(String message) {
                              Log.e("cutMedia", "cutMedia onFailure：" + message);
                            }

                            @Override
                            public void onStart() {

                            }

                            @Override
                            public void onFinish() {

                            }

                          });
                    }

                    @Override
                    public void onProgress(String message) {
                      Log.e("mixAudio", "mixAudio onProgress：" + message);
                    }

                    @Override
                    public void onFailure(String message) {
                      Log.e("mixAudio", "mixAudio onFailure：" + message);
                    }

                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onFinish() {

                    }

                  });
            }

            @Override
            public void onProgress(String message) {
              Log.e("cutMediaToEnd", "cutMediaToEnd onProgress：" + message);
            }

            @Override
            public void onFailure(String message) {
              Log.e("cutMediaToEnd", "cutMediaToEnd onFailure：" + message);
            }

            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

          });
    } else {
      String mixDestinationAudioFilePath =
          processAudioPath.substring(
              0, processAudioPath.lastIndexOf(".")) + "_drawStickerAudio"
              + System.currentTimeMillis()
              + ".mp3";
      FFmpegUtils.mixAudio(processAudioPath, stickerContent.getStickerImageData().getSoundPath(),
          mixDestinationAudioFilePath, new FFmpegExecuteResponseHandler() {
            @Override
            public void onSuccess(String message) {
              Log.e("mixAudio", "mixAudio onSuccess：" + mixDestinationAudioFilePath);
              videoProcessResponseHandler.onSuccess("",
                  mixDestinationAudioFilePath);
            }

            @Override
            public void onProgress(String message) {
              Log.e("mixAudio", "mixAudio onProgress：" + message);
            }

            @Override
            public void onFailure(String message) {
              Log.e("mixAudio", "mixAudio onFailure：" + message);
            }

            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }
          });
    }

  }

  private void drawSticker(VideoContentTask videoContentTask, StickerContent stickerContent,
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
            0, processVideoPath.lastIndexOf(".")) + "_drawSticker"
            + ".mp4";
    FFmpegUtils.overlaySticker(processVideoPath, stickerContent, recordActionLocationTask,
        destinationVideoFilePath, rotation, new FFmpegExecuteResponseHandler() {
          @Override
          public void onSuccess(String message) {
            Log.e("drawSticker", "success：" + message);
            videoProcessCallback.success(videoContentTask.getId(), destinationVideoFilePath);
          }

          @Override
          public void onProgress(String message) {
            Log.e("drawSticker", "progress：" + message);
          }

          @Override
          public void onFailure(String message) {
            Log.e("drawSticker", "failure：" + message);
            videoProcessCallback.failure(message);
          }

          @Override
          public void onStart() {
            Log.e("drawSticker", "start");
          }

          @Override
          public void onFinish() {
            Log.e("drawSticker", "finish");
          }
        });
  }

  private void saveAudioToSDCard(StickerContent stickerContent, String videoFilePath,
                                 VideoProcessResponseHandler videoProcessResponseHandler) {
    ThreadPool.getInstance().execute(() -> {
      String fileParentPath = new File(videoFilePath).getParent();
      String stickerFileDirectory = fileParentPath.endsWith(File.separator) ? fileParentPath
          + "stickerSound" : fileParentPath + File.separator + "stickerSound";
      int resId = stickerContent.getStickerImageData().getSoundId();
      Log.e("saveAudioToSDCard", "sucess：" + resId);
      String saveFileName =
          FileUtils.saveRawMp3ToFile(resId, stickerFileDirectory, resId + ".mp3");
      stickerContent.getStickerImageData().setSoundPath(saveFileName);
      videoProcessResponseHandler.onSuccess("", "");
    });

  }

  class MixAudioData {
    RecordActionLocationTask recordActionLocationTask;
    StickerContent stickerContent;

    public MixAudioData(RecordActionLocationTask recordActionLocationTask,
                        StickerContent stickerContent) {
      this.recordActionLocationTask = recordActionLocationTask;
      this.stickerContent = stickerContent;
    }

    public RecordActionLocationTask getRecordActionLocationTask() {
      return recordActionLocationTask;
    }

    public StickerContent getStickerContent() {
      return stickerContent;
    }
  }
}
