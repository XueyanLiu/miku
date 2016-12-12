package com.biubiu.miku.util.video.generator;

import android.media.MediaMetadataRetriever;
import android.text.TextUtils;
import android.util.Log;

import com.biubiu.miku.model.Post;
import com.biubiu.miku.model.PostVideo;
import com.biubiu.miku.util.FileUtils;
import com.biubiu.miku.util.ThreadPool;
import com.biubiu.miku.util.video.action.RecordActionLocationTask;
import com.biubiu.miku.util.video.action.SourceType;
import com.biubiu.miku.util.video.action.runMan.RunManContent;
import com.biubiu.miku.util.video.ffmpeg.FFmpegUtils;
import com.biubiu.miku.util.video.task.VideoContentTask;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DrawRunManGenerator extends VideoActionGenerator {
  private static String mixAudioFilePath = "";
  private static String mixVideoFilePath = "";
  private static boolean isSaveAudio = false;
  private static boolean isGetVideo = false;

  public DrawRunManGenerator(VideoProcessCallback videoProcessCallback, PostVideo postVideo) {
    super(videoProcessCallback, postVideo);
  }

  @Override
  public void generate(VideoContentTask videoContentTask, Post post,
                       int actionLocationTaskPosition) {
    RecordActionLocationTask recordActionLocationTask =
        videoContentTask.getRecordActionLocationTasks().get(actionLocationTaskPosition);
    if (recordActionLocationTask != null) {
      generate(videoContentTask, post, recordActionLocationTask,
          videoContentTask.getProcessVideoFilePath(),
          actionLocationTaskPosition, new VideoProcessResponseHandler() {
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

  private void generate(VideoContentTask videoContentTask, Post post,
      RecordActionLocationTask recordActionLocationTask, String videoFilePath,
      int actionLocationTaskPosition, VideoProcessResponseHandler videoProcessResponseHandler) {
    RunManContent runManContent = (RunManContent) recordActionLocationTask.getActionContent();
    if (runManContent.getRunManAttribute().getRunManSoundAttribute() == null) {
      drawRunMan(recordActionLocationTask, videoFilePath, new VideoProcessResponseHandler() {
        @Override
        public void onSuccess(String message, String filePath) {
          videoProcessResponseHandler.onSuccess("", filePath);
        }
      });
    } else {
      saveAudioToSDCard(runManContent, videoFilePath, new VideoProcessResponseHandler() {

        @Override
        public void onSuccess(String message, String filePath) {
          isSaveAudio = true;
          if (isGetVideo) {
            mixAudio(runManContent, recordActionLocationTask, videoFilePath,
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
          0, videoFilePath.lastIndexOf(".")) + "runman" + "_video.mp4";
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
          drawRunMan(recordActionLocationTask, destinationVideoPath,
              new VideoProcessResponseHandler() {
            @Override
            public void onSuccess(String message, String filePath) {
              mixVideoFilePath = filePath;
              if (!TextUtils.isEmpty(mixAudioFilePath)) {
                mixVideo(videoContentTask, post, actionLocationTaskPosition);
              }
            }
          });
          if (isSaveAudio) {
            Log.e("saveAudioToSDCard",
                "isSaveAudio：" + isSaveAudio + "   isGetVideo:" + isGetVideo);
            mixAudio(runManContent, recordActionLocationTask, videoFilePath,
                new VideoProcessResponseHandler() {
              @Override
              public void onSuccess(String message, String filePath) {
                mixAudioFilePath = filePath;
                if (!TextUtils.isEmpty(mixVideoFilePath)) {
                  mixVideo(videoContentTask, post, actionLocationTaskPosition);
                }
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

  private void drawRunMan(RecordActionLocationTask recordActionLocationTask,
                          String processVideoPath, VideoProcessResponseHandler videoProcessResponseHandler) {
    MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
    mediaMetadataRetriever.setDataSource(processVideoPath);
    int rotation =
        Integer.parseInt(mediaMetadataRetriever
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));
    mediaMetadataRetriever.release();
    String destinationVideoFilePath =
        processVideoPath.substring(
            0, processVideoPath.lastIndexOf(".")) + "_drawRunMan"
            + ".mp4";
    FFmpegUtils.overlayRunMan(processVideoPath, recordActionLocationTask,
        destinationVideoFilePath, rotation, new FFmpegExecuteResponseHandler() {
          @Override
          public void onSuccess(String message) {
            Log.e("overlayRunMan", "overlayRunMan success：" + message);
            videoProcessResponseHandler.onSuccess("", destinationVideoFilePath);
          }

          @Override
          public void onProgress(String message) {
            Log.e("overlayRunMan", "progress：" + message);
          }

          @Override
          public void onFailure(String message) {
            Log.e("overlayRunMan", "failure：" + message);
            videoProcessResponseHandler.onFailure(message);
          }

          @Override
          public void onStart() {
            Log.e("overlayRunMan", "start");
          }

          @Override
          public void onFinish() {
            Log.e("overlayRunMan", "finish");
          }
        });
  }

  private void mixAudio(RunManContent runManContent,
                        RecordActionLocationTask recordActionLocationTask,
                        String processVideoPath, VideoProcessResponseHandler videoProcessResponseHandler) {
    String destinationAudioPath = processVideoPath.substring(
        0, processVideoPath.lastIndexOf(".")) + "runman" + "_audio.mp3";
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
            mixAudio(new MixAudioData(recordActionLocationTask, runManContent),
                destinationAudioPath,
                videoProcessResponseHandler);
          }
        });
  }

  private void mixAudio(MixAudioData mixAudioData, String processAudioPath,
      VideoProcessResponseHandler videoProcessResponseHandler) {
    RecordActionLocationTask recordActionLocationTask = mixAudioData.getRecordActionLocationTask();
    RunManContent runManContent = mixAudioData.getSoundRunMan();
    Log.e("cutMediaToEnd", "cutMediaToEnd：" + recordActionLocationTask.getStartPosition());
    String destinationAudioFilePath =
        processAudioPath.substring(
            0, processAudioPath.lastIndexOf(".")) + "_drawRunManAudio"
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
                      0, processAudioPath.lastIndexOf(".")) + "_drawRunManAudio"
                      + System.currentTimeMillis()
                      + ".mp3";
              FFmpegUtils.mixAudio(destinationAudioFilePath, runManContent
                  .getRunManAttribute().getRunManSoundAttribute().getSoundPath(),
                  mixDestinationAudioFilePath, new FFmpegExecuteResponseHandler() {
                @Override
                public void onSuccess(String message) {
                  Log.e("mixAudio", "mixAudio onSuccess：" + mixDestinationAudioFilePath);
                  String cutDestinationAudioFilePath =
                      processAudioPath.substring(
                          0, processAudioPath.lastIndexOf(".")) + "_drawRunManAudio"
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
                              0, processAudioPath.lastIndexOf(".")) + "_drawRunManAudio"
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
              0, processAudioPath.lastIndexOf(".")) + "_drawRunManAudio"
              + System.currentTimeMillis()
              + ".mp3";
      FFmpegUtils.mixAudio(processAudioPath, runManContent
          .getRunManAttribute().getRunManSoundAttribute().getSoundPath(),
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

  private void saveAudioToSDCard(RunManContent runManContent, String videoFilePath,
      VideoProcessResponseHandler videoProcessResponseHandler) {
    ThreadPool.getInstance().execute(() -> {
      String fileParentPath = new File(videoFilePath).getParent();
      String runManFileDirectory = fileParentPath.endsWith(File.separator) ? fileParentPath
          + "runManSound" : fileParentPath + File.separator + "runManSound";
      if (runManContent.getRunManAttribute().getRunManSoundAttribute()
          .getSourceType() == SourceType.RES) {
        int resId = runManContent.getRunManAttribute().getRunManSoundAttribute().getSoundResId();
        Log.e("saveAudioToSDCard", "sucess：" + resId);
        String saveFileName =
            FileUtils.saveRawMp3ToFile(runManContent.getRunManAttribute().getRunManSoundAttribute()
                .getSoundResId(), runManFileDirectory, resId + ".mp3");
        runManContent.getRunManAttribute().getRunManSoundAttribute().setSoundPath(saveFileName);
      }
      videoProcessResponseHandler.onSuccess("", "");
    });

  }

  class MixAudioData {
    RecordActionLocationTask recordActionLocationTask;
    RunManContent runManContent;

    public MixAudioData(RecordActionLocationTask recordActionLocationTask,
        RunManContent soundRunManContent) {
      this.recordActionLocationTask = recordActionLocationTask;
      this.runManContent = soundRunManContent;
    }

    public RecordActionLocationTask getRecordActionLocationTask() {
      return recordActionLocationTask;
    }

    public RunManContent getSoundRunMan() {
      return runManContent;
    }
  }
}
