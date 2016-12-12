package com.biubiu.miku.util.video.generator;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.biubiu.miku.util.FileUtils;
import com.biubiu.miku.util.ThreadPool;
import com.biubiu.miku.util.video.ConcatMediaCallback;
import com.biubiu.miku.util.video.VideoUtils;
import com.biubiu.miku.util.video.action.montage.MontageType;
import com.biubiu.miku.util.video.action.montage.Speed;
import com.biubiu.miku.util.video.ffmpeg.FFmpegUtils;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VideoMontageModifier {
  private static int montageVideoPosition;
  private static int childVideoCount;
  private static List<String> childVideoPathList;

  public static void modify(String videoPath, Speed speed, int videoRotation,
                            VideoProcessResponseHandler videoProcessResponseHandler) {
    childVideoPathList = new ArrayList<>();
    if (speed.getMontageType() == MontageType.REPEAT) {
      cutRepeat(videoPath, speed, new VideoProcessResponseHandler() {
        @Override
        public void onSuccess(String message, String filePath) {
          for (int i = 0; i < 2; i++) {
            childVideoPathList.add(montageVideoPosition + i,
                childVideoPathList.get(montageVideoPosition));
          }
          VideoUtils.concatIdenticalVideoList(childVideoPathList, new File(filePath).getParent(),
              new ConcatMediaCallback() {
            @Override
            public void success(String outputFilePath) {
              Log.e("concat", "outputFilePath:" + outputFilePath);
              videoProcessResponseHandler.onSuccess(outputFilePath, outputFilePath);
            }

            @Override
            public void failure(Throwable throwable) {

            }
          });
        }
      });
    } else if (speed.getMontageType() == MontageType.FREEZE) {
      cutFreeze(videoPath, speed, new VideoProcessResponseHandler() {
        @Override
        public void onSuccess(String message, String filePath) {
          freezeFrame(videoPath, speed, new VideoProcessResponseHandler() {
            @Override
            public void onSuccess(String message, String filePath) {
              childVideoPathList.add(montageVideoPosition, filePath);
              for (String childPath : childVideoPathList) {
                Log.e("concat", "childPath:" + childPath);
              }
              String tmpDestinationFilepath = videoPath.substring(
                  0, videoPath.lastIndexOf(".")) + "_concat.mp4";
              FFmpegUtils.concatVideo(childVideoPathList,
                  tmpDestinationFilepath, VideoUtils.getVideoRotation(videoPath),
                  new FFmpegExecuteResponseHandler() {
                @Override
                public void onSuccess(String message) {
                  Log.e("concatVideo", "outputFilePath:" + tmpDestinationFilepath);
                  videoProcessResponseHandler.onSuccess(tmpDestinationFilepath,
                      tmpDestinationFilepath);
                }

                @Override
                public void onProgress(String message) {
                  Log.e("concatVideo", "onProgress:" + message);
                }

                @Override
                public void onFailure(String message) {
                  Log.e("concatVideo", "onFailure:" + message);
                }

                @Override
                public void onStart() {

                }

                @Override
                public void onFinish() {

                }
              });
            }
          });
        }
      });
    } else {
      cutSpeed(videoPath, speed, new VideoProcessResponseHandler() {
        @Override
        public void onSuccess(String message, String filePath) {
          if (childVideoCount == 1) {
            modifySpeed(filePath, speed, videoRotation, new VideoProcessResponseHandler() {
              @Override
              public void onSuccess(String message, String filePath) {
                Log.e("filePath", "modifySpeed:" + filePath);
                if (childVideoCount == 1) {
                  videoProcessResponseHandler.onSuccess(message, filePath);
                }
              }
            });
          } else {
            cutVideo(videoPath, speed.getStartPositionMs(), speed.getDurationMs(), 2,
                new VideoProcessResponseHandler() {
              @Override
              public void onSuccess(String message, String filePath) {
                modifySpeed(filePath, speed, videoRotation, new VideoProcessResponseHandler() {
                  @Override
                  public void onSuccess(String message, String filePath) {
                    Log.e("filePath", "modifySpeed:" + filePath);
                    childVideoPathList.add(montageVideoPosition, filePath);
                    String tmpDestinationFilepath = videoPath.substring(
                        0, videoPath.lastIndexOf(".")) + "_concat.mp4";
                    FFmpegUtils.concatVideo(childVideoPathList,
                        tmpDestinationFilepath, VideoUtils.getVideoRotation(videoPath),
                        new FFmpegExecuteResponseHandler() {
                      @Override
                      public void onSuccess(String message) {
                        Log.e("concatVideo", "outputFilePath:" + tmpDestinationFilepath);
                        videoProcessResponseHandler.onSuccess(tmpDestinationFilepath,
                            tmpDestinationFilepath);
                      }

                      @Override
                      public void onProgress(String message) {
                        Log.e("concatVideo", "onProgress:" + message);
                      }

                      @Override
                      public void onFailure(String message) {
                        Log.e("concatVideo", "onFailure:" + message);
                      }

                      @Override
                      public void onStart() {

                      }

                      @Override
                      public void onFinish() {

                      }
                    });
                  }
                });
              }
            });
          }
        }
      });
    }

  }

  private static void cutSpeed(String videoPath, Speed speed,
                               VideoProcessResponseHandler videoProcessResponseHandler) {
    int videoDuration = VideoUtils.getMediaDuration(videoPath);
    if (speed.getStartPositionMs() <= 1) {
      if (speed.getStartPositionMs() + speed.getDurationMs() == videoDuration) {
        childVideoCount = 1;
        montageVideoPosition = 0;
        childVideoPathList.add(videoPath);
        videoProcessResponseHandler.onSuccess(videoPath, videoPath);
      } else {
        childVideoCount = 2;
        montageVideoPosition = 0;
        cutVideo(videoPath, speed.getDurationMs(), videoDuration - speed.getDurationMs(), 0,
            new VideoProcessResponseHandler() {
              @Override
              public void onSuccess(String message, String filePath) {
                childVideoPathList.add(filePath);
                videoProcessResponseHandler.onSuccess(message, filePath);
              }
            });
      }
    } else if (speed.getStartPositionMs() + speed.getDurationMs() == videoDuration) {
      childVideoCount = 2;
      montageVideoPosition = 1;
      cutVideo(videoPath, 0, speed.getStartPositionMs(), 0,
          new VideoProcessResponseHandler() {
            @Override
            public void onSuccess(String message, String filePath) {
              childVideoPathList.add(filePath);
              videoProcessResponseHandler.onSuccess(message, filePath);
            }
          });
    } else {
      childVideoCount = 3;
      montageVideoPosition = 1;
      Log.e("cutVideo", "childVideoCount:" + childVideoCount);
      cutVideo(videoPath, 0, speed.getStartPositionMs(), 0,
          new VideoProcessResponseHandler() {
            @Override
            public void onSuccess(String message, String filePath) {
              childVideoPathList.add(filePath);
              cutVideo(videoPath, speed.getStartPositionMs() + speed.getDurationMs(),
                  videoDuration - speed.getStartPositionMs() - speed.getDurationMs(), 1,
                  new VideoProcessResponseHandler() {
                @Override
                public void onSuccess(String message, String filePath) {
                  childVideoPathList.add(filePath);
                  videoProcessResponseHandler.onSuccess(message, filePath);
                }
              });
            }
          });
    }

  }

  private static void cutFreeze(String videoPath, Speed speed,
                                VideoProcessResponseHandler videoProcessResponseHandler) {
    int videoDuration = VideoUtils.getMediaDuration(videoPath);
    if (speed.getStartPositionMs() <= 1) {
      if (speed.getStartPositionMs() == videoDuration) {
        childVideoCount = 1;
        montageVideoPosition = 0;
        childVideoPathList.add(videoPath);
        videoProcessResponseHandler.onSuccess(videoPath, videoPath);
      } else {
        childVideoCount = 2;
        montageVideoPosition = 0;
        childVideoPathList.add(videoPath);
        videoProcessResponseHandler.onSuccess(videoPath, videoPath);
      }
    } else if (speed.getStartPositionMs() == videoDuration) {
      childVideoCount = 2;
      montageVideoPosition = 1;
      childVideoPathList.add(videoPath);
      videoProcessResponseHandler.onSuccess(videoPath, videoPath);
    } else {
      childVideoCount = 3;
      montageVideoPosition = 1;
      Log.e("cutVideo", "childVideoCount:" + childVideoCount);
      cutVideo(videoPath, 0, speed.getStartPositionMs(), 0,
          new VideoProcessResponseHandler() {
            @Override
            public void onSuccess(String message, String filePath) {
              childVideoPathList.add(filePath);
              cutVideo(videoPath, speed.getStartPositionMs(),
                  videoDuration - speed.getStartPositionMs(), 2,
                  new VideoProcessResponseHandler() {
                @Override
                public void onSuccess(String message, String filePath) {
                  childVideoPathList.add(filePath);
                  videoProcessResponseHandler.onSuccess(message, filePath);
                }
              });
            }
          });
    }
  }

  private static void cutRepeat(String videoPath, Speed speed,
                                VideoProcessResponseHandler videoProcessResponseHandler) {
    int videoDuration = VideoUtils.getMediaDuration(videoPath);
    Log.e("cutVideo", "speed.getStartPositionMs():" + speed.getStartPositionMs()
        + "  speed.getDurationMs():" + speed.getDurationMs() + "   videoDuration:" + videoDuration);
    if (speed.getStartPositionMs() <= 1) {
      if (speed.getStartPositionMs() + speed.getDurationMs() == videoDuration) {
        childVideoCount = 1;
        montageVideoPosition = 0;
        childVideoPathList.add(videoPath);
        videoProcessResponseHandler.onSuccess(videoPath, videoPath);
      } else {
        childVideoCount = 2;
        montageVideoPosition = 0;
        cutVideo(videoPath, 0,
            speed.getDurationMs(), 0,
            new VideoProcessResponseHandler() {
              @Override
              public void onSuccess(String message, String filePath) {
                childVideoPathList.add(filePath);
                cutVideo(videoPath, speed.getDurationMs(),
                    videoDuration - speed.getDurationMs(), 1,
                    new VideoProcessResponseHandler() {
                  @Override
                  public void onSuccess(String message, String filePath) {
                    childVideoPathList.add(filePath);
                    videoProcessResponseHandler.onSuccess(message, filePath);
                  }
                });
              }
            });
      }
    } else if (speed.getStartPositionMs() + speed.getDurationMs() == videoDuration) {
      childVideoCount = 2;
      montageVideoPosition = 1;
      cutVideo(videoPath, 0, speed.getStartPositionMs(), 0,
          new VideoProcessResponseHandler() {

            @Override
            public void onSuccess(String message, String filePath) {
              childVideoPathList.add(filePath);
              cutVideo(videoPath, speed.getStartPositionMs(),
                  videoDuration - speed.getStartPositionMs(), 1,
                  new VideoProcessResponseHandler() {
                @Override
                public void onSuccess(String message, String filePath) {
                  childVideoPathList.add(filePath);
                  videoProcessResponseHandler.onSuccess(message, filePath);
                }
              });
            }
          });
    } else {
      childVideoCount = 3;
      montageVideoPosition = 1;
      Log.e("cutVideo", "childVideoCount:" + childVideoCount);
      cutVideo(videoPath, 0, speed.getStartPositionMs(), 0,
          new VideoProcessResponseHandler() {

            @Override
            public void onSuccess(String message, String filePath) {
              childVideoPathList.add(filePath);
              cutVideo(videoPath, speed.getStartPositionMs(),
                  speed.getDurationMs(), 1,
                  new VideoProcessResponseHandler() {
                @Override
                public void onSuccess(String message, String filePath) {
                  childVideoPathList.add(filePath);
                  cutVideo(videoPath, speed.getStartPositionMs() + speed.getDurationMs(),
                      videoDuration - speed.getStartPositionMs() - speed.getDurationMs(), 2,
                      new VideoProcessResponseHandler() {
                    @Override
                    public void onSuccess(String message, String filePath) {
                      childVideoPathList.add(filePath);
                      videoProcessResponseHandler.onSuccess(message, filePath);
                    }
                  });
                }
              });
            }

          });
    }
  }

  private static void cutVideo(String videoPath, int startPositionMs, int durationMs, int position,
                               VideoProcessResponseHandler videoProcessResponseHandler) {
    String destinationVideoPath = videoPath.substring(
        0, videoPath.lastIndexOf(".")) + "_cut_temp_" + position + System.currentTimeMillis()
        + ".mp4";
    FFmpegUtils.cutMedia(videoPath, destinationVideoPath, startPositionMs, durationMs,
        new FFmpegExecuteResponseHandler() {
          @Override
          public void onSuccess(String message) {
            Log.e("cutVideo", "onSuccess message:" + message);
          }

          @Override
          public void onProgress(String message) {
            Log.e("cutVideo", "onProgress message:" + message);
            videoProcessResponseHandler.onProgress("cutVideo:" + message);
          }

          @Override
          public void onFailure(String message) {
            Log.e("cutVideo", "onFailure message:" + message);
            videoProcessResponseHandler.onFailure("cutVideo:" + message);
          }

          @Override
          public void onStart() {
            videoProcessResponseHandler.onStart();
          }

          @Override
          public void onFinish() {
            Log.e("cutVideo", "onFinish :childVideoCount:" + childVideoCount);
            videoProcessResponseHandler.onSuccess("cutVideo", destinationVideoPath);
          }
        });
  }

  private static void modifySpeed(String videoPath, Speed speed, int videoRotation,
                                  VideoProcessResponseHandler videoProcessResponseHandler) {
    String destinationVideoPath = videoPath.substring(
        0, videoPath.lastIndexOf(".")) + "_speed_temp.mp4";
    FFmpegUtils.changeVideoSpeed(videoPath, speed.getMontageType().getSpeedSize(), 0,
        (int) (speed.getDurationMs() * speed.getMontageType().getDurationProportion()),
        videoRotation, destinationVideoPath,
        new FFmpegExecuteResponseHandler() {
          @Override
          public void onSuccess(String message) {
            // ThreadPool.execute(() -> {
            // File tempFile = new File(videoPath);
            // if (tempFile.isFile()) {
            // tempFile.delete();
            // }
            // });
            Log.e("modifySpeed", "onSuccess message:" + message);
          }

          @Override
          public void onProgress(String message) {
            Log.e("modifySpeed", "onProgress message:" + message);
            videoProcessResponseHandler.onProgress("modifySpeed:" + message);
          }

          @Override
          public void onFailure(String message) {
            Log.e("modifySpeed", "onFailure message:" + message);
            videoProcessResponseHandler.onFailure("modifySpeed:" + message);
          }

          @Override
          public void onStart() {
            videoProcessResponseHandler.onStart();
          }

          @Override
          public void onFinish() {
            videoProcessResponseHandler.onFinish();
            videoProcessResponseHandler.onSuccess("modifySpeed", destinationVideoPath);
          }
        });
  }

  private static void freezeFrame(String videoPath, Speed speed,
                                  VideoProcessResponseHandler videoProcessResponseHandler) {
    ThreadPool.getInstance().execute(() -> {
      String frameParentPath = new File(videoPath).getParent() + "/frame_temp/";
      Bitmap frameBitmap =
          VideoUtils.saveVideoFrameToFile(speed.getStartPositionMs(), videoPath);
      String frameTempFilePath =
          FileUtils.saveBitmapToFile(frameBitmap, frameParentPath, "frame.jpg", 100);
      for (int i = 0; i < 100; i++) {
        FileUtils.copyFile(frameTempFilePath,
            frameParentPath
                + (i < 10 ? "00" + i + ".jpg" : i < 100 ? "0" + i + ".jpg" : i + ".jpg"));
      }
      new Handler(Looper.getMainLooper()).post(() -> {
        String destinationVideoPath = videoPath.substring(
            0, videoPath.lastIndexOf(".")) + "_freeze_temp" + System.currentTimeMillis() + ".mp4";
        FFmpegUtils.imageToVideo(frameParentPath + "%03d.jpg", destinationVideoPath,
            speed.getDurationMs(),null, new FFmpegExecuteResponseHandler() {
          @Override
          public void onSuccess(String message) {
            Log.e("imageToVideo", "onSuccess:" + destinationVideoPath);
            String destinationAudioPath = videoPath.substring(
                0, videoPath.lastIndexOf(".")) + "_freeze_temp_audio" + System.currentTimeMillis()
                + ".mp3";
            FFmpegUtils.getAudio(videoPath, destinationAudioPath,
                new FFmpegExecuteResponseHandler() {
              @Override
              public void onSuccess(String message) {
                Log.e("getAudio", "getAudio:" + destinationAudioPath);
                String destinationCutAudioPath = videoPath.substring(
                    0, videoPath.lastIndexOf(".")) + "_freeze_temp_cut_audio"
                    + System.currentTimeMillis() + ".mp3";
                FFmpegUtils.cutMedia(destinationAudioPath, destinationCutAudioPath,
                    speed.getStartPositionMs(), 200, new FFmpegExecuteResponseHandler() {
                  @Override
                  public void onSuccess(String message) {
                    Log.e("cutMedia", "onSuccess:" + destinationCutAudioPath);
                    String destinationFreezeVideoPath = videoPath.substring(
                        0, videoPath.lastIndexOf(".")) + "_freeze" + System.currentTimeMillis()
                        + ".mp4";
                    FFmpegUtils.generateVideo(destinationVideoPath, destinationCutAudioPath,
                        destinationFreezeVideoPath, new FFmpegExecuteResponseHandler() {
                      @Override
                      public void onSuccess(String message) {
                        Log.e("generateVideo", "onSuccess:" + destinationFreezeVideoPath);
                        videoProcessResponseHandler.onSuccess(message, destinationFreezeVideoPath);
                      }

                      @Override
                      public void onProgress(String message) {
                        Log.e("generateVideo", "onProgress:" + message);
                      }

                      @Override
                      public void onFailure(String message) {
                        Log.e("generateVideo", "onFailure:" + message);
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
                    Log.e("cutMedia", "onProgress:" + message);
                  }

                  @Override
                  public void onFailure(String message) {
                    Log.e("cutMedia", "onFailure:" + message);
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
                Log.e("getAudio", "onProgress:" + message);
              }

              @Override
              public void onFailure(String message) {
                Log.e("getAudio", "onFailure:" + message);
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
            Log.e("imageToVideo", "onProgress:" + message);
          }

          @Override
          public void onFailure(String message) {
            Log.e("imageToVideo", "onFailure:" + message);
          }

          @Override
          public void onStart() {

          }

          @Override
          public void onFinish() {

          }
        });
      });
    });
  }

}
