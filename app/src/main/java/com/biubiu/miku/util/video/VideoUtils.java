package com.biubiu.miku.util.video;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.biubiu.miku.MikuApplication;
import com.biubiu.miku.R;
import com.biubiu.miku.util.FileUtils;
import com.biubiu.miku.util.ImageUtils;
import com.biubiu.miku.util.SystemUtils;
import com.biubiu.miku.util.ThreadPool;
import com.biubiu.miku.util.camera.FilterTools;
import com.biubiu.miku.util.video.action.ActionImageData;
import com.biubiu.miku.util.video.action.SourceType;
import com.biubiu.miku.util.video.action.filter.FilterData;
import com.biubiu.miku.util.video.ffmpeg.FFmpegUtils;
import com.biubiu.miku.util.video.generator.VideoProcessResponseHandler;
import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.MovieHeaderBox;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.googlecode.mp4parser.util.Matrix;
import com.googlecode.mp4parser.util.Path;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jp.co.cyberagent.android.gpuimage.GPUImage;

/**
 * Video related utility functions.
 */
public class VideoUtils {
  private static final String VIDEO_SUFFIX = "_proc";
  public static final String COVER_NAME = "cover.jpg";
  public static final String TEMP_COVER_NAME = "temp.jpg";
  public static final String FILTER_COVER_NAME = "filter_cover.jpg";
  private static String tempFilePath;
  private static int concatFileNum = 0;
  private static int rotateFileNum = 0;

  private VideoUtils() {
  }

  public static boolean needToCrop(String originMediaPath) {
    MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
    mediaMetadataRetriever.setDataSource(originMediaPath);
    int videoWidth =
        Integer.parseInt(mediaMetadataRetriever
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
    int videoHeight =
        Integer.parseInt(mediaMetadataRetriever
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
    int rotation = getRotation(mediaMetadataRetriever);
    mediaMetadataRetriever.release();
    if (rotation == 0 || rotation == 180) {
      return videoHeight != videoWidth * 3 / 4;
    } else if (rotation == 90 || rotation == 270) {
      return videoWidth != videoHeight * 3 / 4;
    }
    return true;
  }

  public static CropParams getCropParams(String originMediaPath, float videoRatioWH) {
    MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
    mediaMetadataRetriever.setDataSource(originMediaPath);
    int videoWidth =
        Integer.parseInt(mediaMetadataRetriever
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
    int videoHeight =
        Integer.parseInt(mediaMetadataRetriever
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
    int rotation = getRotation(mediaMetadataRetriever);
    mediaMetadataRetriever.release();
    Log.e("getCropParams", "height:" + videoHeight + "   width:" + videoWidth + "   rotation:"
        + rotation);
    // switch (videoRatioType) {
    // case RECTANGLE:
    switch (rotation) {
      case 0:
        return new CropParams(videoWidth, (int) (videoWidth * videoRatioWH), 0, 0);
      case 90:
        int height = videoHeight;
        int width = (int) (height * videoRatioWH);
        Log.e("getCropParams", "height:" + height + "   width:" + width);
        return new CropParams(width, height, 0, 0);
      case 180:
        break;
      case 270:
        height = videoHeight;
        width = (int) (height * videoRatioWH);
        int x = videoWidth - width;
        int y = 0;
        return new CropParams(width, height, x, y);
      default:
        break;
    }
    // break;
    // case SQUARE:
    // switch (rotation) {
    // case 0:
    // return new CropParams(videoWidth, videoWidth, 0, 0);
    // default:
    // break;
    // }
    // break;
    // }
    return null;
  }

  public static final class CropParams {
    public final int width;
    public final int height;
    public final int x;
    public final int y;

    public CropParams(int width, int height, int x, int y) {
      this.width = width;
      this.height = height;
      this.x = x;
      this.y = y;
    }

    public CropParams(int rotation, int width, int height, int x, int y) {
      switch (rotation) {
        case 0:
          this.width = width;
          this.height = height;
          break;
        case 90:
          this.width = width;
          this.height = height;

          break;
        case 180:
          this.width = width;
          this.height = height;

          break;
        case 270:
          this.width = width;
          this.height = height;
          break;
        default:
          this.width = width;
          this.height = height;
          break;
      }
      this.x = x;
      this.y = y;
    }
  }

  public static void concat(List<String> pathList, String srcDirPath, float videoRatioWH,
                            ConcatMediaCallback concatMediaCallback) {
    concatFileNum = 0;
    tempFilePath = "";
    if (pathList.size() == 1) {
      VideoMetaData videoMetaData = getVideoMetaData(pathList.get(0));
      if (videoMetaData.getRotation() != 0) {
        rotateVideo(new RotationVideoData(videoMetaData, pathList.get(0)), videoRatioWH,
            new VideoProcessResponseHandler() {
              @Override
              public void onSuccess(String message, String filePath) {
                new Handler(Looper.getMainLooper()).post(() -> concatMediaCallback
                    .success(pathList.get(0)));
              }
            });
      } else {
        new Handler(Looper.getMainLooper()).post(() -> concatMediaCallback
            .success(pathList.get(0)));
      }
    } else {
      List<RotationVideoData> rotationVideoDataList = new ArrayList<>();
      for (int i = 0; i < pathList.size(); i++) {
        String videoPath = pathList.get(i);
        VideoMetaData videoMetaData = getVideoMetaData(videoPath);
        rotationVideoDataList
            .add(new RotationVideoData(videoMetaData, videoPath));
      }
      FileUtils.createFolderIfNotExist(srcDirPath);
      if (!rotationVideoDataList.isEmpty()) {
        rotateVideo(rotationVideoDataList, videoRatioWH, new VideoProcessResponseHandler() {
          @Override
          public void onSuccess(String message, String filePath) {
            concatIdenticalVideoList(pathList, srcDirPath, concatMediaCallback);
          }
        });
      } else {
        concatIdenticalVideoList(pathList, srcDirPath, concatMediaCallback);
      }
    }
  }

  private static void concatVideoListFromFFmpeg(List<String> pathList,
                                                String destinationVideoFilePath, int videoRotation,
                                                ConcatMediaCallback concatMediaCallback) {
    FFmpegUtils.concatVideo(pathList, destinationVideoFilePath, videoRotation,
        new FFmpegExecuteResponseHandler() {
          @Override
          public void onSuccess(String message) {
            Log.e("concatVideo", "onSuccess:" + destinationVideoFilePath);
            concatMediaCallback.success(destinationVideoFilePath);
            ThreadPool.getInstance().execute(() -> {
              List<String> tempFilePathList = new ArrayList<>();
              tempFilePathList.addAll(pathList);
              for (String videoFilePath : tempFilePathList) {
                File tempFile = new File(videoFilePath);
                if (tempFile.isFile()) {
                  tempFile.delete();
                }
              }
            });
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

  private static void concatVideoListFromMediaCodec(List<String> pathList,
                                                    String destinationVideoFilePath, int videoRotation,
                                                    ConcatMediaCallback concatMediaCallback) throws IOException {
    MediaExtractor extractor = new MediaExtractor();
    MediaFormat format = null;
    extractor.setDataSource(pathList.get(0));
    int numTracks = extractor.getTrackCount();
    // find and select the first audio track present in the file.
    for (int i = 0; i < numTracks; i++) {
      format = extractor.getTrackFormat(i);
      if (format.getString(MediaFormat.KEY_MIME).startsWith("audio/")) {
        extractor.selectTrack(i);
        break;
      }
    }
    MediaCodec mediaCodec = MediaCodec.createDecoderByType(format.getString(MediaFormat.KEY_MIME));
    mediaCodec.configure(format, null, null, 0);
    mediaCodec.start();
  }

  public static void concatIdenticalVideoList(List<String> pathList, String srcDirPath,
                                              ConcatMediaCallback concatMediaCallback) {
    concatIdenticalVideoList(pathList, srcDirPath, null, concatMediaCallback);
  }

  public static void concatIdenticalVideoList(List<String> pathList, String srcDirPath,
                                              String destinationVideoFilePath,
                                              ConcatMediaCallback concatMediaCallback) {
    ThreadPool
        .getInstance().execute(() -> {
          FileOutputStream fileOutputStream = null;
          try {
            Log.e("concatVideoList", "concatVideoList Start");
            Movie[] inMovies = new Movie[pathList.size()];
            for (int i = 0; i < pathList.size(); i++) {
              String path = pathList.get(i);
              if (new File(path).isFile()) {
                Log.e("concatVideoList",
                    "concatVideoList:" + path + "   concatFileNum:" + concatFileNum
                        + "   destinationVideoFilePath:" + destinationVideoFilePath);
                Movie movie = MovieCreator.build(path);
                inMovies[i] = movie;
                concatFileNum++;
                if (TextUtils.isEmpty(tempFilePath)) {
                  tempFilePath = path;
                }
              }
            }
            if (concatFileNum == 0) {
              new Handler(Looper.getMainLooper()).post(() -> concatMediaCallback
                  .failure(new NullPointerException("concat video path list is empty")));
            } else if (concatFileNum == 1) {
              new File(tempFilePath).renameTo(new File(destinationVideoFilePath));
              new Handler(Looper.getMainLooper())
                  .post(() -> concatMediaCallback.success(destinationVideoFilePath));
            } else if (concatFileNum > 1) {
              String fileName = System.currentTimeMillis() + ".mp4";
              FileUtils.createFolderIfNotExist(srcDirPath);
              final String videoPath =
                  TextUtils.isEmpty(destinationVideoFilePath) ? srcDirPath.endsWith(File.separator)
                      ? srcDirPath + fileName
                      : srcDirPath + File.separator + fileName : destinationVideoFilePath;
              Log.e("concatVideoList",
                  "concatVideoList:" + videoPath + "   concatFileNum:" + concatFileNum);
              List<Track> videoTracks = new LinkedList();
              List<Track> audioTracks = new LinkedList();
              for (Movie m : inMovies) {
                for (Track t : m.getTracks()) {
                  if (t.getHandler().equals("soun")) {
                    audioTracks.add(t);
                  }
                  if (t.getHandler().equals("vide")) {
                    videoTracks.add(t);
                  }
                }
              }
              Movie result = new Movie();
              if (audioTracks.size() > 0) {
                result
                    .addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
              }
              if (videoTracks.size() > 0) {
                result
                    .addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
              }
              Container out = new DefaultMp4Builder().build(result);
              fileOutputStream = new FileOutputStream(new File(videoPath));
              FileChannel fc = fileOutputStream.getChannel();
              out.writeContainer(fc);
              fc.close();
              new Handler(Looper.getMainLooper())
                  .post(() -> concatMediaCallback.success(videoPath));
              Log.e("concatVideoList", "concatVideoList End");
              // List<String> tempFilePathList = new ArrayList<>();
              // tempFilePathList.addAll(pathList);
              // for (String videoFilePath : tempFilePathList) {
              // File tempFile = new File(videoFilePath);
              // if (tempFile.isFile()) {
              // tempFile.delete();
              // }
              // }
            }
          } catch (Exception e) {
            e.printStackTrace();
            new Handler(Looper.getMainLooper()).post(() -> concatMediaCallback.failure(e));
          } finally {
            try {
              if (fileOutputStream != null) {
                fileOutputStream.close();
                fileOutputStream = null;
              }
            } catch (IOException e) {
              e.printStackTrace();
              new Handler(Looper.getMainLooper()).post(() -> concatMediaCallback.failure(e));
            }
          }
        });
  }

  private static void rotateVideo(List<RotationVideoData> rotationVideoDataList,
                                  float videoRatioWH,
                                  VideoProcessResponseHandler videoProcessResponseHandler) {
    rotateFileNum = 0;
    Log.e("rotateVideo", "rotationVideoDataList size:" + rotationVideoDataList.size());
    Map<RotationVideoData, Integer> threadCounts = new HashMap<>();
    for (int i = 0; i < rotationVideoDataList.size(); i++) {
      threadCounts.put(rotationVideoDataList.get(i),
          FFmpegUtils.BEST_THREAD_COUNT / rotationVideoDataList.size());
    }
    if (FFmpegUtils.BEST_THREAD_COUNT % rotationVideoDataList.size() != 0) {
      int surplusThreadCountSize = FFmpegUtils.BEST_THREAD_COUNT % rotationVideoDataList.size();
      Collections.sort(rotationVideoDataList,
          (lhs, rhs) -> (int) (lhs.getVideoMetaData().getDuration()
              - rhs.getVideoMetaData().getDuration()));
      for (int i = 0; i < surplusThreadCountSize; i++) {
        threadCounts.put(rotationVideoDataList.get(i),
            threadCounts.get(rotationVideoDataList.get(i)) + 1);
      }
    }
    for (int i = 0; i < rotationVideoDataList.size(); i++) {
      RotationVideoData rotationVideoData = rotationVideoDataList.get(i);
      String tmpDestinationFilepath =
          rotationVideoData.getVideoPath().substring(
              0, rotationVideoData.getVideoPath().lastIndexOf(".")) + "_rotate_video" + i
              + System.currentTimeMillis()
              + ".mp4";
      FFmpegUtils.rotateRecordVideo(rotationVideoData.getVideoPath(), tmpDestinationFilepath,
          getVideoMetaData(rotationVideoData.getVideoPath()),
          getVideoMetaData(rotationVideoDataList.get(0).getVideoPath()), videoRatioWH,
          threadCounts.get(rotationVideoData), new FFmpegExecuteResponseHandler() {
            @Override
            public void onSuccess(String message) {
              rotateFileNum++;
              File rotateFile = new File(rotationVideoData.getVideoPath());
              rotateFile.delete();
              new File(tmpDestinationFilepath).renameTo(new File(rotationVideoData.getVideoPath()));
              Log.e("rotateVideo", "rotateVideo path:" + tmpDestinationFilepath
                  + " rotateFileNum:" + rotateFileNum);
              if (rotateFileNum == rotationVideoDataList.size()) {
                videoProcessResponseHandler.onSuccess("", "");
              }
            }

            @Override
            public void onProgress(String message) {
              Log.e("rotateVideo",
                  "onProgress:" + " path：" + rotationVideoData.getVideoPath() + "   " + message);
            }

            @Override
            public void onFailure(String message) {
              Log.e("rotateVideo",
                  "onFailure:" + " path：" + rotationVideoData.getVideoPath() + "   " + message);
            }

            @Override
            public void onStart() {
              Log.e("rotateVideo", "onStart" + " path：" + rotationVideoData.getVideoPath() + "   ");
            }

            @Override
            public void onFinish() {
              Log.e("rotateVideo",
                  "onFinish" + " path：" + rotationVideoData.getVideoPath() + "   ");
            }
          });
    }
  }

  private static void rotateVideo(RotationVideoData rotationVideoData,
                                  float videoRatioWH,
                                  VideoProcessResponseHandler videoProcessResponseHandler) {
    String tmpDestinationFilepath =
        rotationVideoData.getVideoPath().substring(
            0, rotationVideoData.getVideoPath().lastIndexOf(".")) + "_rotate_video"
            + System.currentTimeMillis() + ".mp4";
    FFmpegUtils.rotateRecordVideo(rotationVideoData.getVideoPath(), tmpDestinationFilepath,
        getVideoMetaData(rotationVideoData.getVideoPath()),
        getVideoMetaData(rotationVideoData.getVideoPath()), videoRatioWH,
        FFmpegUtils.BEST_THREAD_COUNT,
        new FFmpegExecuteResponseHandler() {
          @Override
          public void onSuccess(String message) {
            Log.e("rotateVideo", "rotateVideo:" + tmpDestinationFilepath);
            File rotateFile = new File(rotationVideoData.getVideoPath());
            rotateFile.delete();
            new File(tmpDestinationFilepath).renameTo(new File(rotationVideoData.getVideoPath()));
            videoProcessResponseHandler.onSuccess("", "");
          }

          @Override
          public void onProgress(String message) {
            Log.e("rotateVideo", "onProgress:" + message);
          }

          @Override
          public void onFailure(String message) {
            Log.e("rotateVideo", "onFailure:" + message);
          }

          @Override
          public void onStart() {
            Log.e("rotateVideo", "onStart");
          }

          @Override
          public void onFinish() {
            Log.e("rotateVideo", "onFinish");
          }
        });
  }

  public static void concatAudio(List<String> audioPathList, String destinationAudioFilePath,
                                 ConcatMediaCallback concatMediaCallback) {
    if (audioPathList.size() == 1) {
      new Handler(Looper.getMainLooper()).post(() -> concatMediaCallback
          .success(audioPathList.get(0)));
      return;
    }
    ThreadPool
        .getInstance().execute(() -> {
          FileOutputStream fileOutputStream = null;
          try {
            Movie[] inMovies = new Movie[audioPathList.size()];
            for (int i = 0; i < audioPathList.size(); i++) {
              String path = audioPathList.get(i);
              if (new File(path).isFile()) {
                // rotateVideo(path);
                Movie movie = MovieCreator.build(path);
                inMovies[i] = movie;
                concatFileNum++;
                if (tempFilePath == null) {
                  tempFilePath = path;
                }
              }
            }
            if (concatFileNum == 0) {
              new Handler(Looper.getMainLooper()).post(() -> concatMediaCallback
                  .failure(new NullPointerException("concat video path list is empty")));
            } else if (concatFileNum == 1) {
              new Handler(Looper.getMainLooper()).post(() -> concatMediaCallback
                  .success(tempFilePath));
            } else if (concatFileNum > 1) {
              List<Track> audioTracks = new LinkedList();
              for (Movie m : inMovies) {
                for (Track t : m.getTracks()) {
                  if (t.getHandler().equals("soun")) {
                    audioTracks.add(t);
                  }
                }
              }
              Movie result = new Movie();
              if (audioTracks.size() > 0) {
                result
                    .addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
              }
              Container out = new DefaultMp4Builder().build(result);
              fileOutputStream = new FileOutputStream(new File(destinationAudioFilePath));
              FileChannel fc = fileOutputStream.getChannel();
              out.writeContainer(fc);
              fc.close();
              new Handler(Looper.getMainLooper()).post(() -> concatMediaCallback
                  .success(destinationAudioFilePath));
              List<String> tempFilePathList = new ArrayList<>();
              tempFilePathList.addAll(audioPathList);
              // for (String videoFilePath : tempFilePathList) {
              // File tempFile = new File(videoFilePath);
              // if (tempFile.isFile()) {
              // tempFile.delete();
              // }
              // }
            }
          } catch (Exception e) {
            e.printStackTrace();
            new Handler(Looper.getMainLooper()).post(() -> concatMediaCallback.failure(e));
          } finally {
            try {
              if (fileOutputStream != null) {
                fileOutputStream.close();
                fileOutputStream = null;
              }
            } catch (IOException e) {
              e.printStackTrace();
              new Handler(Looper.getMainLooper()).post(() -> concatMediaCallback.failure(e));
            }
          }
        });
  }


  public static int getVideoSamplesSize(String videoPath) {
    int videoSamplesSize = 0;
    try {
      Movie inMovie = MovieCreator.build(videoPath);
      for (Track t : inMovie.getTracks()) {
        if (t.getHandler().equals("vide")) {
          videoSamplesSize += t.getSamples().size();
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return videoSamplesSize;
  }

  public static int videoSeekMsToSamplePosition(int seekMs, int sampleSize, int durationMs) {
    return (int) (sampleSize / (float) durationMs * seekMs);
  }

  public static void rotateVideo(String videoPath,
                                 VideoProcessResponseHandler videoProcessResponseHandler) {
    ThreadPool.getInstance().execute(() -> {
      FileOutputStream fileOutputStream = null;
      try {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(videoPath);
        int rotation = getRotation(mediaMetadataRetriever);
        switch (rotation) {
          case 90:
            // Movie rotate90Movie = MovieCreator.build(videoPath);
            // rotate90Movie.setMatrix(Matrix.ROTATE_270);
            // String rotate90TmpDestinationFilepath = videoPath.substring(
            // 0, videoPath.lastIndexOf(".")) + "_tmp.mp4";
            // fileOutputStream = new FileOutputStream(rotate90TmpDestinationFilepath);
            // new DefaultMp4Builder().build(rotate90Movie)
            // .writeContainer(fileOutputStream.getChannel());
            // File rotate90File = new File(videoPath);
            // rotate90File.delete();
            // new File(rotate90TmpDestinationFilepath).renameTo(new File(videoPath));
            videoProcessResponseHandler.onSuccess(videoPath, videoPath);
            break;
          case 270:
            // Movie rotate270Movie = MovieCreator.build(videoPath);
            IsoFile isoFile = new IsoFile(videoPath);
            // Container out = new DefaultMp4Builder().build(rotate270Movie);
            MovieHeaderBox mvhd = Path.getPath(isoFile, "moov/mvhd");
            mvhd.setMatrix(Matrix.ROTATE_180);
            String rotate270TmpDestinationFilepath = videoPath.substring(
                0, videoPath.lastIndexOf(".")) + "_tmp.mp4";
            fileOutputStream = new FileOutputStream(rotate270TmpDestinationFilepath);
            isoFile.writeContainer(fileOutputStream.getChannel());
            // File file = new File(videoPath);
            // file.delete();
            // new File(rotate270TmpDestinationFilepath).renameTo(new File(videoPath));
            videoProcessResponseHandler.onSuccess(rotate270TmpDestinationFilepath,
                rotate270TmpDestinationFilepath);
            break;
        }
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        try {
          if (fileOutputStream != null) {
            fileOutputStream.close();
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    });
  }

  public static void setVideoRotationParam(String videoPath, int rotation) {
    FileOutputStream fileOutputStream = null;
    Matrix matrix = null;
    switch (rotation) {
      case 90:
        matrix = Matrix.ROTATE_90;
        break;
      case 180:
        matrix = Matrix.ROTATE_180;
        break;
      case 270:
        matrix = Matrix.ROTATE_270;
        break;
    }
    if (matrix != null) {
      try {
        Movie rotateMovie = MovieCreator.build(videoPath);
        rotateMovie.setMatrix(matrix);
        String rotate90TmpDestinationFilepath = videoPath.substring(
            0, videoPath.lastIndexOf(".")) + "_tmp.mp4";
        fileOutputStream = new FileOutputStream(rotate90TmpDestinationFilepath);
        new DefaultMp4Builder().build(rotateMovie)
            .writeContainer(fileOutputStream.getChannel());
        File rotateFile = new File(videoPath);
        rotateFile.delete();
        new File(rotate90TmpDestinationFilepath).renameTo(new File(videoPath));
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        try {
          if (fileOutputStream != null) {
            fileOutputStream.close();
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public static int getMediaDuration(String mediaPath) {
    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
    int videoDurationMs = -1;
    try {
      retriever.setDataSource(mediaPath);
      videoDurationMs =
          Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
      retriever.release();
    } catch (Exception e) {
      return -1;
    }
    return videoDurationMs;
  }

  public static int getVideoRotation(String videoPath) {
    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
    retriever.setDataSource(videoPath);
    int rotation = getRotation(retriever);
    retriever.release();
    return rotation;
  }

  public static int getVideoHeight(String videoPath) {
    MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
    mediaMetadataRetriever.setDataSource(videoPath);
    int videoWidth;
    int videoHeight;
    int rotation = getRotation(mediaMetadataRetriever);
    switch (rotation) {
      case 90:
      case 270:
        videoHeight =
            Integer.parseInt(mediaMetadataRetriever
                .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        videoWidth =
            Integer.parseInt(mediaMetadataRetriever
                .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        break;
      default:
        videoWidth =
            Integer.parseInt(mediaMetadataRetriever
                .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        videoHeight =
            Integer.parseInt(mediaMetadataRetriever
                .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        break;
    }
    mediaMetadataRetriever.release();
    float previewSizeProportion = videoWidth / (float) videoHeight;
    return (int) (SystemUtils.getScreenWidthPx() / previewSizeProportion);
  }

  public static int[] getVideoSize(String videoPath) {
    int[] videoSize = new int[2];
    MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
    mediaMetadataRetriever.setDataSource(videoPath);
    int videoWidth;
    int videoHeight;
    int rotation = getRotation(mediaMetadataRetriever);
    switch (rotation) {
      case 90:
      case 270:
        videoHeight =
            Integer.parseInt(mediaMetadataRetriever
                .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        videoWidth =
            Integer.parseInt(mediaMetadataRetriever
                .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        break;
      default:
        videoWidth =
            Integer.parseInt(mediaMetadataRetriever
                .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        videoHeight =
            Integer.parseInt(mediaMetadataRetriever
                .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        break;
    }
    videoSize[0] = videoWidth;
    videoSize[1] = videoHeight;
    return videoSize;
  }

  public static VideoMetaData getVideoMetaData(String videoPath) {
    MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
    mediaMetadataRetriever.setDataSource(videoPath);
    int videoWidth = Integer.parseInt(mediaMetadataRetriever
        .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
    int videoHeight = Integer.parseInt(mediaMetadataRetriever
        .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
    long duration = Long.parseLong(mediaMetadataRetriever
        .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
    int rotation = getRotation(mediaMetadataRetriever);
    mediaMetadataRetriever.release();
    return new VideoMetaData(videoWidth, videoHeight, rotation, duration);
  }

  public static ActionImageData saveVideoTagToFile(Bitmap bitmap, String saveDirectory,
                                                   String filename, String videoPath) {
    MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
    mediaMetadataRetriever.setDataSource(videoPath);
    int videoWidth;
    int rotation = getRotation(mediaMetadataRetriever);
    switch (rotation) {
      case 90:
      case 270:
        videoWidth =
            Integer.parseInt(mediaMetadataRetriever
                .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        break;
      default:
        videoWidth =
            Integer.parseInt(mediaMetadataRetriever
                .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        break;
    }
    mediaMetadataRetriever.release();
    android.graphics.Matrix matrix = new android.graphics.Matrix();
    matrix
        .postScale(videoWidth / (float) bitmap.getWidth(), videoWidth / (float) bitmap.getWidth());
    Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
        bitmap.getHeight(), matrix, false);
    return new ActionImageData(videoWidth / (float) bitmap.getWidth(),
        FileUtils.saveVideoTagToFile(resizedBitmap, saveDirectory,
            filename, rotation));
  }

  public static Bitmap saveVideoFrameToFile(int framePosition, String videoPath) {
    Bitmap frameBitmap;
    MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
    mediaMetadataRetriever.setDataSource(videoPath);
    int rotation = getRotation(mediaMetadataRetriever);
    frameBitmap =
        mediaMetadataRetriever.getFrameAtTime(framePosition,
            MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
    mediaMetadataRetriever.release();
    if (rotation != 0) {
      frameBitmap = ImageUtils.adjustPhotoRotation(frameBitmap, 360 - rotation);
    }
    return frameBitmap;
  }

  public static ActionImageData saveActionToFile(Context context, Bitmap bitmap,
                                                 String saveDirectory,
                                                 String filename, String videoPath) {
    MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
    mediaMetadataRetriever.setDataSource(videoPath);
    int videoWidth;
    int rotation = getRotation(mediaMetadataRetriever);
    switch (rotation) {
      case 90:
      case 270:
        videoWidth =
            Integer.parseInt(mediaMetadataRetriever
                .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        break;
      default:
        videoWidth =
            Integer.parseInt(mediaMetadataRetriever
                .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        break;
    }
    mediaMetadataRetriever.release();
    android.graphics.Matrix matrix = new android.graphics.Matrix();
    matrix
        .postScale(
            videoWidth / ((float) SystemUtils.getScreenWidthPx()
                - context.getResources().getDimensionPixelSize(R.dimen.edit_center_margin) * 2),
            videoWidth
                / ((float) SystemUtils.getScreenWidthPx()
                - context.getResources().getDimensionPixelSize(R.dimen.edit_center_margin) * 2));
    Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
        bitmap.getHeight(), matrix, false);
    return new ActionImageData(videoWidth / (float) bitmap.getWidth(),
        FileUtils.saveVideoTagToFile(resizedBitmap, saveDirectory,
            filename, rotation));
  }

  private static int getRotation(MediaMetadataRetriever mediaMetadataRetriever) {
    String rotationStr = mediaMetadataRetriever
        .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
    int rotation = 0;
    if (!TextUtils.isEmpty(rotationStr) && !rotationStr.equals("null")) {
      rotation = Integer.parseInt(mediaMetadataRetriever
          .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));
    }
    return rotation;
  }

  public static float getVideoProportion(SourceType sourceType, String videoPath) {
    float proportion = 1;
    MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
    mediaMetadataRetriever.setDataSource(videoPath);
    int videoWidth;
    int rotation = getRotation(mediaMetadataRetriever);
    switch (rotation) {
      case 90:
      case 270:
        videoWidth =
            Integer.parseInt(mediaMetadataRetriever
                .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        break;
      default:
        videoWidth =
            Integer.parseInt(mediaMetadataRetriever
                .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        break;
    }
    mediaMetadataRetriever.release();
    switch (sourceType) {
      case ASSETS:
        proportion = videoWidth / (float) SystemUtils.getScreenWidthPx();
        break;
      case FILE:
        break;
    }
    return proportion;
  }

  public static ActionImageData saveVideoTagFrameToFile(SourceType sourceType,
                                                        String stickerFrameImagePath,
                                                        String saveDirectory,
                                                        String videoPath, String saveFileName) {
    try {
      float proportion = 1;
      MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
      mediaMetadataRetriever.setDataSource(videoPath);
      int videoWidth, videoHeight;
      int rotation = getRotation(mediaMetadataRetriever);
      switch (rotation) {
        case 90:
        case 270:
          videoWidth =
              Integer.parseInt(mediaMetadataRetriever
                  .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
          videoHeight =
              Integer.parseInt(mediaMetadataRetriever
                  .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
          break;
        default:
          videoWidth =
              Integer.parseInt(mediaMetadataRetriever
                  .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
          videoHeight =
              Integer.parseInt(mediaMetadataRetriever
                  .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
          break;
      }
      mediaMetadataRetriever.release();
      Bitmap stickerBitmap = null;
      switch (sourceType) {
        case ASSETS:
          AssetManager assetManager = MikuApplication.context.getAssets();
          BitmapFactory.Options options = new BitmapFactory.Options();
          options.inJustDecodeBounds = true;
          BitmapFactory.decodeStream(assetManager.open(stickerFrameImagePath), null,
              options);
          proportion = videoWidth / (float) SystemUtils.getScreenWidthPx();
          options.inJustDecodeBounds = false;
          stickerBitmap =
              BitmapFactory.decodeStream(assetManager.open(stickerFrameImagePath), null,
                  options);
          int blackPointSize = MikuApplication.context.getResources()
              .getDimensionPixelSize(R.dimen.black_point_size);
          stickerBitmap =
              Bitmap.createScaledBitmap(stickerBitmap, (int) (blackPointSize * proportion),
                  (int) (blackPointSize * proportion), false);
          break;
        case FILE:
          break;
      }
      Bitmap bgBitmap =
          Bitmap.createBitmap(videoWidth, videoHeight, Bitmap.Config.ARGB_8888);
      Canvas canvas = new Canvas(bgBitmap);
      Paint paint = new Paint();
      paint.setDither(true);
      paint.setAntiAlias(true);
      canvas.drawBitmap(stickerBitmap, (videoWidth - stickerBitmap.getWidth()) / 2,
          (videoHeight - stickerBitmap.getHeight()) / 2, paint);
      canvas.save();
      return new ActionImageData(proportion, FileUtils.saveVideoTagToFile(bgBitmap, saveDirectory,
          saveFileName, rotation));
    } catch (IOException e) {

    }
    return null;
  }

  public static ActionImageData saveStickerFrameToFile(SourceType sourceType,
                                                       String stickerFrameImagePath, String saveDirectory, String videoPath, String saveFileName, boolean fullScreen) {
    try {
      float proportion = 1;
      MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
      mediaMetadataRetriever.setDataSource(videoPath);
      int videoWidth, videoHeight;
      int rotation = getRotation(mediaMetadataRetriever);
      switch (rotation) {
        case 90:
        case 270:
          videoWidth =
              Integer.parseInt(mediaMetadataRetriever
                  .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
          videoHeight =
              Integer.parseInt(mediaMetadataRetriever
                  .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
          break;
        default:
          videoWidth =
              Integer.parseInt(mediaMetadataRetriever
                  .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
          videoHeight =
              Integer.parseInt(mediaMetadataRetriever
                  .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
          break;
      }
      mediaMetadataRetriever.release();
      Bitmap stickerBitmap = null;
      // switch (sourceType) {
      // case ASSETS:
      // AssetManager assetManager = GaiaApplication.getAppContext().getAssets();
      // BitmapFactory.Options options = new BitmapFactory.Options();
      // options.inJustDecodeBounds = true;
      // BitmapFactory.decodeStream(assetManager.open(stickerFrameImagePath), null,
      // options);
      // proportion = videoWidth / (float) SystemUtils.getScreenWidthPx();
      // options.inJustDecodeBounds = false;
      // stickerBitmap =
      // BitmapFactory.decodeStream(assetManager.open(stickerFrameImagePath), null,
      // options);
      // stickerBitmap =
      // Bitmap.createScaledBitmap(stickerBitmap, (int) (options.outWidth * proportion),
      // (int) (options.outHeight * proportion), false);
      // break;
      // case FILE:
      // break;
      // }

      BitmapFactory.Options options = new BitmapFactory.Options();
      options.inJustDecodeBounds = true;
      BitmapFactory.decodeFile(stickerFrameImagePath, options);
      proportion = videoWidth / (float) SystemUtils.getScreenWidthPx();
      options.inJustDecodeBounds = false;
      stickerBitmap = BitmapFactory.decodeFile(stickerFrameImagePath, options);
      if (fullScreen) {
        stickerBitmap =
            Bitmap.createScaledBitmap(stickerBitmap, videoWidth, videoHeight, false);
      } else {
        stickerBitmap =
            Bitmap.createScaledBitmap(stickerBitmap, (int) (options.outWidth * proportion),
                (int) (options.outHeight * proportion), false);
      }
      Bitmap bgBitmap =
          Bitmap.createBitmap(videoWidth, videoHeight, Bitmap.Config.ARGB_8888);
      Canvas canvas = new Canvas(bgBitmap);
      Paint paint = new Paint();
      paint.setDither(true);
      paint.setAntiAlias(true);
      canvas.drawBitmap(stickerBitmap, (videoWidth - stickerBitmap.getWidth()) / 2,
          (videoHeight - stickerBitmap.getHeight()) / 2, paint);
      canvas.save();
      return new ActionImageData(proportion, FileUtils.saveVideoTagToFile(bgBitmap, saveDirectory,
          saveFileName, rotation));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public static String getVideoCover(String videoPath, String coverName) {
    coverName = TextUtils.isEmpty(coverName) ? COVER_NAME : coverName;
    String parentPath = new File(videoPath).getParent();
    String coverPath = parentPath.endsWith(File.separator) ? parentPath
        + coverName : parentPath + File.separator + coverName;
    if (new File(coverPath).isFile()) {
      return coverPath;
    }
    MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
    mediaMetadataRetriever.setDataSource(videoPath);
    Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime(1);
    mediaMetadataRetriever.release();
    if (bitmap != null) {
      return FileUtils.saveBitmapToFile(bitmap, new File(videoPath).getParent(), coverName,
          FILTER_COVER_NAME.equals(coverName) ? 10 : 100);
    }
    return "";
  }

  public static void generateVideoFilterTheme(String filterImagePath) {
    List<FilterData> filterDataList = FilterTools.getFilterDataList();
    String parentPath = new File(filterImagePath).getParent();
    for (int i = 0; i < filterDataList.size(); i++) {
      FilterData filterData = filterDataList.get(i);
      String filterName =
          FilterTools.getFilterName(MikuApplication.context,
              filterData.getFilterThemeType());
      ThreadPool.getInstance().execute(() -> {
        GPUImage gpuImage = new GPUImage(MikuApplication.context);
        gpuImage.setFilter(FilterTools.getFilter(filterData.getFilterThemeType()));
        Bitmap bitmap =
            gpuImage
                .getBitmapWithFilterApplied(BitmapFactory.decodeFile(filterImagePath));
        FileUtils.saveBitmapToFile(bitmap, parentPath, filterName, 10);
      });
    }
  }

  static class RotationVideoData {
    private VideoMetaData videoMetaData;
    private String videoPath;

    public RotationVideoData(VideoMetaData videoMetaData, String videoPath) {
      this.videoMetaData = videoMetaData;
      this.videoPath = videoPath;
    }

    public VideoMetaData getVideoMetaData() {
      return videoMetaData;
    }

    public String getVideoPath() {
      return videoPath;
    }
  }

  /**
   * 获取视频的缩略图
   * 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
   * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
   *
   * @param videoPath 视频的路径
   * @param width     指定输出视频缩略图的宽度
   * @param height    指定输出视频缩略图的高度度
   * @param kind      参照MediaStore.Images.Thumbnails类中的常量MINI_KIND和MICRO_KIND。
   *                  其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
   * @return 指定大小的视频缩略图
   */
  public final static Bitmap getVideoThumbnail(String videoPath, int width, int height,
                                               int kind) {
    Bitmap bitmap;
    // 获取视频的缩略图
    bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
    if (bitmap == null) {
      return null;
    }
    bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
        ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
    return bitmap;
  }

  public final static Bitmap getVideoThumbnail(String videoPath, long time, int option) {
    MediaMetadataRetriever mmr = new MediaMetadataRetriever();
    mmr.setDataSource(videoPath);
    Bitmap bitmap = mmr.getFrameAtTime(time, option);
    mmr.release();
    return bitmap;
  }
}
