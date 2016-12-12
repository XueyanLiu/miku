package com.biubiu.miku.util.video.generator;

import android.text.TextUtils;
import android.util.Log;

import com.biubiu.miku.model.Post;
import com.biubiu.miku.model.PostVideo;
import com.biubiu.miku.util.FileUtils;
import com.biubiu.miku.util.ThreadPool;
import com.biubiu.miku.util.video.action.RecordActionLocationTask;
import com.biubiu.miku.util.video.action.music.Music;
import com.biubiu.miku.util.video.action.music.MusicContent;
import com.biubiu.miku.util.video.ffmpeg.FFmpegUtils;
import com.biubiu.miku.util.video.task.VideoContentTask;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;

import java.io.File;

// TODO YANBINGWU
// 是不是感觉整个逻辑太复杂了，套的太多了，想好了，想好了，想好了方式，ffmpeg处理也好，层级套用也好，然后再想好了，改改吧！存在问题：1：层级套用复杂性过高
// 2：对调用源反馈不足，可能导致调用源在等待
public class MixBackgroundMusicGenerator extends VideoActionGenerator {
  private static String soundFilePath = "";
  private static String musicFilePath = "";
  private static String audioFilePath = "";
  private static String videoFilePath = "";

  public MixBackgroundMusicGenerator(VideoProcessCallback videoProcessCallback, PostVideo postVideo) {
    super(videoProcessCallback, postVideo);
  }

  @Override
  public void generate(VideoContentTask videoContentTask, Post post,
                       int actionLocationTaskPosition) {
    RecordActionLocationTask recordActionLocationTask =
        videoContentTask.getRecordActionLocationTasks().get(actionLocationTaskPosition);
    if (recordActionLocationTask != null) {
      MusicContent musicContent = (MusicContent) recordActionLocationTask.getActionContent();
      Music music = musicContent.getMusic();
      videoFilePath = videoContentTask.getProcessVideoFilePath();
      videoSoundProcess(videoFilePath, music.getSoundVolume(), new VideoProcessResponseHandler() {
        @Override
        public void onSuccess(String message, String filePath) {
          soundFilePath = filePath;
          if (!TextUtils.isEmpty(musicFilePath)) {
            generateAudioAndGetVideo(videoFilePath, soundFilePath, musicFilePath,
                new VideoProcessResponseHandler() {
                  @Override
                  public void onSuccess(String message, String filePath) {
                    videoContentTask.setProcessVideoFilePath(filePath);
                    nextGenerate(videoContentTask, post, actionLocationTaskPosition + 1);
                  }
                });
          }
        }

        @Override
        public void onProgress(String message) {
          super.onProgress(message);
          Log.e("videoSoundProcess", "onProgress:" + message);
        }

        @Override
        public void onFailure(String message) {
          super.onFailure(message);
          Log.e("videoSoundProcess", "onFailure:" + message);
        }

        @Override
        public void onStart() {
          super.onStart();
        }
      });
      backgroundMusicProcess(music, videoFilePath.substring(
          0, videoFilePath.lastIndexOf(".")), new VideoProcessResponseHandler() {
        @Override
        public void onSuccess(String message, String filePath) {
          musicFilePath = filePath;
          if (!TextUtils.isEmpty(soundFilePath)) {
            generateAudioAndGetVideo(videoFilePath, soundFilePath, musicFilePath,
                new VideoProcessResponseHandler() {
                  @Override
                  public void onSuccess(String message, String filePath) {
                    videoContentTask.setProcessVideoFilePath(filePath);
                    nextGenerate(videoContentTask, post, actionLocationTaskPosition + 1);
                  }
                });
          }
        }

        @Override
        public void onProgress(String message) {
          super.onProgress(message);
          Log.e("backgroundMusicProcess", "onProgress:" + message);
        }

        @Override
        public void onFailure(String message) {
          super.onFailure(message);
          Log.e("backgroundMusicProcess", "onFailure:" + message);
        }

        @Override
        public void onStart() {
          super.onStart();
        }
      });
    } else {
      nextGenerate(videoContentTask, post, actionLocationTaskPosition + 1);
    }
  }

  private void generateAudioAndGetVideo(String videoPath, String soundFilePath,
                                        String musicFilePath,
                                        VideoProcessResponseHandler videoProcessResponseHandler) {
    generateAudio(soundFilePath, musicFilePath, videoPath.substring(
        0, videoPath.lastIndexOf(".")), new VideoProcessResponseHandler() {
      @Override
      public void onSuccess(String message, String filePath) {
        Log.e("generateAudio", "onSuccess:" + message + "   filePath:" + filePath);
        audioFilePath = filePath;
        if (videoFilePath != null && !TextUtils.isEmpty(videoFilePath)) {
          generateVideo(videoFilePath, audioFilePath, videoProcessResponseHandler);
        }
      }

      @Override
      public void onProgress(String message) {
        super.onProgress(message);
        Log.e("generateAudio", "onProgress:" + message);
        videoProcessResponseHandler.onProgress(message);
      }

      @Override
      public void onFailure(String message) {
        super.onFailure(message);
        Log.e("generateAudio", "onFailure:" + message);
        videoProcessResponseHandler.onFailure(message);
      }

      @Override
      public void onStart() {
        super.onStart();
      }
    });
    getVideo(videoPath, new VideoProcessResponseHandler() {
      @Override
      public void onSuccess(String message, String filePath) {
        Log.e("getVideo", "onSuccess:" + message + "   filePath:" + filePath);
        videoFilePath = filePath;
        if (audioFilePath != null && !TextUtils.isEmpty(audioFilePath)) {
          generateVideo(videoFilePath, audioFilePath, videoProcessResponseHandler);
        }
      }

      @Override
      public void onProgress(String message) {
        super.onProgress(message);
        Log.e("getVideo", "onProgress:" + message);
        videoProcessResponseHandler.onProgress(message);
      }

      @Override
      public void onFailure(String message) {
        super.onFailure(message);
        Log.e("getVideo", "onFailure:" + message);
        videoProcessResponseHandler.onFailure(message);
      }

      @Override
      public void onStart() {
        super.onStart();
      }
    });
  }

  private void generateVideo(String videoPath, String audioPath,
                             VideoProcessResponseHandler videoProcessResponseHandler) {
    String destinationVideoPath = videoPath.substring(
        0, videoPath.lastIndexOf(".")) + "_video.mp4";
    FFmpegUtils.generateVideo(videoPath, audioPath, destinationVideoPath,
        new FFmpegExecuteResponseHandler() {
          @Override
          public void onSuccess(String message) {
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
            videoProcessResponseHandler.onSuccess("generateVideo:" + message, destinationVideoPath);
          }

          @Override
          public void onProgress(String message) {
            videoProcessResponseHandler.onProgress("generateVideo:" + message);
          }

          @Override
          public void onFailure(String message) {
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


  private void generateAudio(String soundAudioPath,
                             String musicAudioPath, String filePrefix,
                             VideoProcessResponseHandler videoProcessResponseHandler) {
    String audioPath = filePrefix + "_audio.mp3";
    FFmpegUtils.mixAudio(soundAudioPath, musicAudioPath, audioPath,
        new FFmpegExecuteResponseHandler() {
          @Override
          public void onSuccess(String message) {
            ThreadPool.getInstance().execute(() -> {
              File soundTmpFile = new File(soundAudioPath);
              if (soundTmpFile.isFile()) {
                soundTmpFile.delete();
              }
              File musicTmpFile = new File(musicAudioPath);
              if (musicTmpFile.isFile()) {
                musicTmpFile.delete();
              }
            });
            videoProcessResponseHandler.onSuccess("generateAudio:" + message, audioPath);
          }

          @Override
          public void onProgress(String message) {
            videoProcessResponseHandler.onProgress("generateAudio:" + message);
          }

          @Override
          public void onFailure(String message) {
            videoProcessResponseHandler.onFailure("generateAudio:" + message);
          }

          @Override
          public void onStart() {
          }

          @Override
          public void onFinish() {

          }
        });
  }

  private void getVideo(String srcVideoFilePath,
                        VideoProcessResponseHandler videoProcessResponseHandler) {
    String videoPath = srcVideoFilePath.substring(
        0, srcVideoFilePath.lastIndexOf(".")) + "_tmp_video.mp4";
    String copyPath = new File(srcVideoFilePath).getParent() + "/tmp1.mp4";
    FileUtils.copyFile(srcVideoFilePath, copyPath);
    FFmpegUtils.getVideo(copyPath, videoPath,
        new FFmpegExecuteResponseHandler() {
          @Override
          public void onSuccess(String message) {
            ThreadPool.getInstance().execute(() -> {
              File soundTmpFile = new File(srcVideoFilePath);
              if (soundTmpFile.isFile()) {
                soundTmpFile.delete();
              }
            });
            videoProcessResponseHandler.onSuccess("getVideo:" + message, videoPath);
          }

          @Override
          public void onProgress(String message) {
            videoProcessResponseHandler.onProgress("getVideo:" + message);
          }

          @Override
          public void onFailure(String message) {
            videoProcessResponseHandler.onFailure("getVideo:" + message);
          }

          @Override
          public void onStart() {

          }

          @Override
          public void onFinish() {

          }
        });
  }

  private static void videoSoundProcess(String videoFilePath, float volume,
                                        VideoProcessResponseHandler videoProcessResponseHandler) {
    String soundTmpPath = videoFilePath.substring(
        0, videoFilePath.lastIndexOf(".")) + "_sound_tmp.mp3";
    String soundVolumePath = videoFilePath.substring(
        0, videoFilePath.lastIndexOf(".")) + "_sound_volume.mp3";
    String copyPath = new File(videoFilePath).getParent() + "/tm2.mp4";
    FileUtils.copyFile(videoFilePath, copyPath);
    FFmpegUtils.getAudio(copyPath, soundTmpPath, new FFmpegExecuteResponseHandler() {
      @Override
      public void onSuccess(String message) {
        if (volume != 1f) {
          videoProcessResponseHandler.onSuccess(message);
          FFmpegUtils.changeAudioVolume(soundTmpPath, volume,
              soundVolumePath,
              new FFmpegExecuteResponseHandler() {
                @Override
                public void onSuccess(String message) {
                  videoProcessResponseHandler.onSuccess("videoSoundProcess changeAudioVolume: "
                          + message,
                      soundVolumePath);
                }

                @Override
                public void onProgress(String message) {
                  videoProcessResponseHandler.onProgress("videoSoundProcess changeAudioVolume: "
                      + message);
                }

                @Override
                public void onFailure(String message) {
                  videoProcessResponseHandler.onProgress("videoSoundProcess changeAudioVolume: "
                      + message);
                }

                @Override
                public void onStart() {

                }

                @Override
                public void onFinish() {
                  videoProcessResponseHandler.onFinish();
                  ThreadPool.getInstance().execute(() -> {
                    File tmpFile = new File(soundTmpPath);
                    if (tmpFile.isFile()) {
                      tmpFile.delete();
                    }
                  });
                }
              });
        } else {
          videoProcessResponseHandler.onSuccess("videoSoundProcess: " + message, soundTmpPath);
        }
      }

      @Override
      public void onProgress(String message) {
        videoProcessResponseHandler.onProgress("getAudio: " + message);
      }

      @Override
      public void onFailure(String message) {
        videoProcessResponseHandler.onFailure("getAudio: " + message);
      }

      @Override
      public void onStart() {
        videoProcessResponseHandler.onStart();
      }

      @Override
      public void onFinish() {
        if (volume == 1f) {
          videoProcessResponseHandler.onFinish();
        }
      }
    });
  }

  private void backgroundMusicProcess(Music music, String filePrefix,
                                      VideoProcessResponseHandler videoProcessResponseHandler) {
    String musicCutPath = filePrefix + "_music_cut.mp3";
    String musicVolumePath = filePrefix + "_music_volume.mp3";
    FFmpegUtils.cutMedia(music.getFilePath(), musicCutPath, music.getStartPositionMs(),
        music.getDurationMs(), new FFmpegExecuteResponseHandler() {
          @Override
          public void onSuccess(String message) {
            if (music.getMusicVolume() != 1f) {
              videoProcessResponseHandler.onSuccess(message);
              FFmpegUtils.changeAudioVolume(musicCutPath, music.getMusicVolume(),
                  musicVolumePath,
                  new FFmpegExecuteResponseHandler() {
                    @Override
                    public void onSuccess(String message) {
                      videoProcessResponseHandler.onSuccess("changeAudioVolume: " + message,
                          musicVolumePath);
                    }

                    @Override
                    public void onProgress(String message) {
                      videoProcessResponseHandler.onProgress("changeAudioVolume: " + message);
                    }

                    @Override
                    public void onFailure(String message) {
                      videoProcessResponseHandler.onProgress("changeAudioVolume: " + message);
                    }

                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onFinish() {
                      videoProcessResponseHandler.onFinish();
                      ThreadPool.getInstance().execute(() -> {
                        File tmpFile = new File(musicCutPath);
                        if (tmpFile.isFile()) {
                          tmpFile.delete();
                        }
                      });
                    }
                  });
            } else {
              videoProcessResponseHandler.onSuccess("backgroundMusicProcess: " + message,
                  musicCutPath);
            }
          }

          @Override
          public void onProgress(String message) {
            videoProcessResponseHandler.onProgress("cutMedia" + message);
          }

          @Override
          public void onFailure(String message) {
            videoProcessResponseHandler.onFailure("cutMedia: " + message);
          }

          @Override
          public void onStart() {
            videoProcessResponseHandler.onStart();
          }

          @Override
          public void onFinish() {
            if (music.getMusicVolume() == 1f) {
              videoProcessResponseHandler.onFinish();
            }
          }
        });
  }
}
