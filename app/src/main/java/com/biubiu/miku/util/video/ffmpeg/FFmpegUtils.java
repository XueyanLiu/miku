package com.biubiu.miku.util.video.ffmpeg;

import android.text.TextUtils;
import android.util.Log;

import com.biubiu.miku.util.DateUtils;
import com.biubiu.miku.util.video.VideoMetaData;
import com.biubiu.miku.util.video.VideoUtils;
import com.biubiu.miku.util.video.action.RecordActionLocationTask;
import com.biubiu.miku.util.video.action.RecordLocation;
import com.biubiu.miku.util.video.action.TimeLine;
import com.biubiu.miku.util.video.action.chatBox.ChatBox;
import com.biubiu.miku.util.video.action.chatBox.ChatBoxContent;
import com.biubiu.miku.util.video.action.filter.FilterCurves;
import com.biubiu.miku.util.video.action.runMan.RunMan;
import com.biubiu.miku.util.video.action.runMan.RunManContent;
import com.biubiu.miku.util.video.action.sticker.Sticker;
import com.biubiu.miku.util.video.action.sticker.StickerContent;
import com.biubiu.miku.util.video.action.sticker.StickerImageData;
import com.biubiu.miku.util.video.action.subtitle.SubtitleContent;
import com.biubiu.miku.util.video.action.subtitle.SubtitleTimeBox;
import com.biubiu.miku.util.video.action.subtitle.util.SubtitleUtils;
import com.biubiu.miku.util.video.action.videoTag.VideoTag;
import com.biubiu.miku.util.video.action.videoTag.VideoTagContent;
import com.biubiu.miku.util.video.action.videoTag.VideoTagImageData;
import com.github.hiteshsondhi88.libffmpeg.CustomFFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * FFmpeg utility functions.
 */
public class FFmpegUtils {
  public static final int BEST_THREAD_COUNT = Runtime.getRuntime().availableProcessors() * 3 + 2;

  private FFmpegUtils() {
  }

  public static void crop(String originVideoFilePath, String destinationVideoFilePath,
                          VideoUtils.CropParams cropParams,
                          FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler) {
    String cropParam = "crop=" + cropParams.width + ":" + cropParams.height + ":" + cropParams.x
            + ":" + cropParams.y;
    String[] cmd =
            new String[]{"-y", "-i", originVideoFilePath, "-c:a", "copy", "-c:v",
                    "libx264", "-pix_fmt", "yuv420p", "-preset", "ultrafast",
                    "-filter:v", cropParam, destinationVideoFilePath};
    StringBuilder stringBuilder = new StringBuilder();
    for (String cmdStr : cmd) {
      stringBuilder.append(cmdStr);
    }
    Log.e("crop", "crop：" + stringBuilder.toString());
    try {
      CustomFFmpeg.getInstance().execute(originVideoFilePath, cmd, ffmpegExecuteResponseHandler);
    } catch (FFmpegCommandAlreadyRunningException e) {
      e.printStackTrace();
    }
  }

  public static void cropLocalVideo(String originVideoFilePath, String destinationVideoFilePath,
                                    VideoUtils.CropParams cropParams, int startPositionMs, int endPositionMs,
                                    int originVideoRotation, FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler) {
    String cropParam = "crop=" + cropParams.width + ":" + cropParams.height + ":" + cropParams.x
            + ":" + cropParams.y;
    List<String> cmdList = new ArrayList<>();
    cmdList.add("-y");
    cmdList.add("-i");
    cmdList.add(originVideoFilePath);
    cmdList.add("-ss");
    cmdList.add(DateUtils.getFfmpegDurationString(startPositionMs));
    cmdList.add("-t");
    cmdList.add(DateUtils.getFfmpegDurationString(endPositionMs));
    cmdList.add("-c:a");
    cmdList.add("copy");
    cmdList.add("-c:v");
    cmdList.add("libx264");
    // cmdList.add("-pix_fmt");
    // cmdList.add("yuv420p");
    cmdList.add("-preset");
    cmdList.add("ultrafast");
    cmdList.add("-vf");
    cmdList.add(cropParam);
    switch (originVideoRotation) {
      case 0:
        cmdList.add("-vf");
        cmdList.add("format=yuv420p," + cropParam);
        break;
      case 90:
        cmdList.add("-vf");
        cmdList
                .add("transpose=1,format=yuv420p," + cropParam);
        break;
      case 180:
        cmdList.add("-vf");
        cmdList.add("transpose=1,transpose=1,format=yuv420p," + cropParam);
        break;
      case 270:
        cmdList.add("-vf");
        cmdList
                .add("transpose=2,format=yuv420p," + cropParam);
        break;
    }
    cmdList.add("-metadata:s:v:0");
    cmdList.add("rotate=0");
    cmdList.add(destinationVideoFilePath);
    StringBuilder stringBuilder = new StringBuilder();
    for (String cmdStr : cmdList) {
      stringBuilder.append(cmdStr);
    }
    String[] cmdArray = cmdList.toArray(new String[cmdList.size()]);
    Log.e("crop", "crop：" + stringBuilder.toString());
    try {
      CustomFFmpeg.getInstance().execute(originVideoFilePath, cmdArray,
              ffmpegExecuteResponseHandler);
    } catch (FFmpegCommandAlreadyRunningException e) {
      e.printStackTrace();
    }
  }

  public static void convertToGif(String originVideoFilePath, String destinationGifFilePath,
                                  int width, int height, FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler) {
    String[] cmd = new String[]{"-y", "-i", originVideoFilePath, "-s", width + "x" + height,
            "-pix_fmt", "rgb24", "-preset", "ultrafast",
            destinationGifFilePath};
    try {
      CustomFFmpeg.getInstance().execute(originVideoFilePath, cmd, ffmpegExecuteResponseHandler);
    } catch (FFmpegCommandAlreadyRunningException e) {
      e.printStackTrace();
    }
  }

  public static void mixAudio(String originAudioFilePath, String secondOriginAudioFilePath,
                              String destinationVideoFilePath,
                              FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler) {
    // -filter_complex amix=inputs=2:duration=first:dropout_transition=2 -f mp3 a.mp3
    String[] cmdList =
            new String[]{"-y", "-i", originAudioFilePath, "-i", secondOriginAudioFilePath,
                    "-filter_complex", "amix=inputs=2:duration=first:dropout_transition=2", "-map", "0",
                    "-map", "1", "-f", "mp3", "-preset", "ultrafast", destinationVideoFilePath};
    StringBuilder stringBuilder = new StringBuilder();
    for (String cmd : cmdList) {
      stringBuilder.append(cmd);
    }
    Log.e("mixAudio", "mixAudio：" + stringBuilder.toString());
    try {
      CustomFFmpeg.getInstance().execute(originAudioFilePath, cmdList,
              ffmpegExecuteResponseHandler);
    } catch (FFmpegCommandAlreadyRunningException e) {
      e.printStackTrace();
    }
  }

  public static void addRunManAudio(List<RunMan> soundRunMans, String originAudioFilePath,
                                    String destinationVideoFilePath,
                                    FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler) {
    // -filter_complex amix=inputs=2:duration=first:dropout_transition=2 -f mp3 a.mp3
    List<String> cmdList = new ArrayList<>();
    cmdList.add("-y");
    cmdList.add("-i");
    cmdList.add(originAudioFilePath);
    for (int i = 0; i < soundRunMans.size(); i++) {
      cmdList.add("-i");
      cmdList.add(soundRunMans.get(i).getRunManContent().getRunManAttribute()
              .getRunManSoundAttribute().getSoundPath());
    }
    cmdList.add("-filter_complex");
    cmdList
            .add("amix=inputs=" + (soundRunMans.size() + 1) + ":duration=first:dropout_transition=2");
    cmdList.add("-map");
    cmdList.add(String.valueOf(0));
    for (int i = 0; i < soundRunMans.size(); i++) {
      for (RecordActionLocationTask recordActionLocationTask : soundRunMans.get(i)
              .getRecordActionLocationTasks()) {
        cmdList.add("-map");
        cmdList.add(String.valueOf(i + 1));
      }
    }
    cmdList.add("-f");
    cmdList.add("mp3");
    cmdList.add(destinationVideoFilePath);
    String[] cmdArray = cmdList.toArray(new String[cmdList.size()]);
    StringBuilder stringBuilder = new StringBuilder();
    for (String cmd : cmdList) {
      stringBuilder.append(cmd);
    }
    Log.e("saveAudioToSDCard", "addRunManAudio：" + stringBuilder.toString());
    try {
      CustomFFmpeg.getInstance().execute(originAudioFilePath, cmdArray,
              ffmpegExecuteResponseHandler);
    } catch (FFmpegCommandAlreadyRunningException e) {
      e.printStackTrace();
    }
  }

  public static void changeAudioVolume(String originVideoFilePath, float volume,
                                       String destinationVideoFilePath,
                                       FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler) {
    // -filter_complex =inputs=2:duration=first:dropout_transition=2 -f mp3 a.mp3
    String[] cmd =
            new String[]{"-y", "-i", originVideoFilePath, "-af", "volume=" + volume, "-vcodec",
                    "copy", "-acodec",
                    "libmp3lame", "-preset", "ultrafast",
                    destinationVideoFilePath};
    try {
      CustomFFmpeg.getInstance().execute(originVideoFilePath, cmd, ffmpegExecuteResponseHandler);
    } catch (FFmpegCommandAlreadyRunningException e) {
      e.printStackTrace();
    }
  }

  public static void aMergeAV(String originVideoFilePath, String originSecondAudioFilePath,
                              String destinationVideoFilePath,
                              FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler) {
    String[] cmd =
            new String[]{"-y", "-i", originVideoFilePath, "-i", originSecondAudioFilePath,
                    destinationVideoFilePath};
    try {
      CustomFFmpeg.getInstance().execute(originVideoFilePath, cmd, ffmpegExecuteResponseHandler);
    } catch (FFmpegCommandAlreadyRunningException e) {
      e.printStackTrace();
    }
  }

  // Merge two mono files into a stereo stream:
  // amovie=left.wav [l] ; amovie=right.mp3 [r] ; [l] [r] amerge
  // Multiple merges assuming 1 video stream and 6 audio streams in input.mkv:
  // ffmpeg -i input.mkv -filter_complex "[0:1][0:2][0:3][0:4][0:5][0:6] amerge=inputs=6" -c:a
  // pcm_s16le output.mkv
  public static void aMergeVideo(String originVideoFilePath, String originSecondAudioFilePath,
                                 String destinationVideoFilePath,
                                 FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler) {
    // -filter_complex amix=inputs=2:duration=first:dropout_transition=2 -f mp3 a.mp3
    String[] cmd =
            new String[]{"-y", "-i", originVideoFilePath, "-i", originSecondAudioFilePath,
                    "-filter_complex", "amerge", "-c:a", "libmp3lame",
                    destinationVideoFilePath};
    try {
      CustomFFmpeg.getInstance().execute(originVideoFilePath, cmd, ffmpegExecuteResponseHandler);
    } catch (FFmpegCommandAlreadyRunningException e) {
      e.printStackTrace();
    }
  }

  public static void generateVideo(String originVideoFilePath, String originAudioFilePath,
                                   String destinationVideoFilePath,
                                   FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler) {
    String[] cmd =
            new String[]{"-y", "-i", originVideoFilePath, "-i", originAudioFilePath,
                    "-vcodec", "libx264", "-acodec", "libmp3lame", "-preset", "ultrafast",
                    destinationVideoFilePath};
    try {
      CustomFFmpeg.getInstance().execute(originVideoFilePath, cmd, ffmpegExecuteResponseHandler);
    } catch (FFmpegCommandAlreadyRunningException e) {
      e.printStackTrace();
    }
  }

  public static void getAudio(String originVideoFilePath,
                              String destinationAudioFilePath,
                              FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler) {
    // ffmpeg -i source_video.avi -vn -ar 44100 -ac 2 -ab 192 -f mp3 sound.mp3
    String[] cmd =
            new String[]{"-y", "-i", originVideoFilePath, "-vn", "-ar", "44100", "-c:a",
                    "libmp3lame", "-f", "mp3", destinationAudioFilePath};
    try {
      CustomFFmpeg.getInstance().execute(originVideoFilePath, cmd, ffmpegExecuteResponseHandler);
    } catch (FFmpegCommandAlreadyRunningException e) {
      e.printStackTrace();
    }
  }

  public static void getVideo(String originVideoFilePath,
                              String destinationVideoFilePath,
                              FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler) {
    // ffmpeg -i test.mp4 -vcodec copy -an 视频流.avi
    String[] cmd =
            new String[]{"-y", "-i", originVideoFilePath, "-vcodec", "copy", "-an",
                    destinationVideoFilePath};
    try {
      CustomFFmpeg.getInstance().execute(originVideoFilePath, cmd, ffmpegExecuteResponseHandler);
    } catch (FFmpegCommandAlreadyRunningException e) {
      e.printStackTrace();
    }
  }

  public static void drawText(String originVideoFilePath, String text, String fontFilePath,
                              String destinationAudioFilePath,
                              FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler) {
    // ffplay -f lavfi -i color=c=white -vf
    // drawtext="fontfile=arial.ttf:text='Goodday':x=(w-tw)/2:y=(h-th)/2"
    // drawtext="fontfile=FreeSerif.ttf:fontcolor=white:x=100:y=x/dar:enable=lt(mod(t\,3)\,1):text='blink'"
    String[] cmd =
            new String[]{
                    "-y",
                    "-i",
                    originVideoFilePath,
                    "-filter_complex",
                    "drawtext",
                    "fontfile:‘file:///android_asset/fonts/huakang_W.ttf‘:fontsize=20:fontcolor=white:text='公租房的关心关心关心对方给媳妇的’",
                    "-preset", "veryfast",
                    destinationAudioFilePath};
    try {
      CustomFFmpeg.getInstance().execute(originVideoFilePath, cmd, ffmpegExecuteResponseHandler);
    } catch (FFmpegCommandAlreadyRunningException e) {
      e.printStackTrace();
    }
  }

  public static void overlayImage(String originVideoFilePath, String originOverlayImageFilePath,
                                  String secondOriginOverlayImageFilePath,
                                  String destinationAudioFilePath,
                                  FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler) {
    String[] cmd =
            new String[]{
                    "-y",
                    "-i",
                    originVideoFilePath,
                    "-i",
                    originOverlayImageFilePath,
                    "-i",
                    secondOriginOverlayImageFilePath,
                    "-filter_complex",
                    // "[0:v][1:v] overlay=10:10:enable='between(t,1,2)' [tmp];[tmp][2:v]
                    // overlay=100:80:enable='between(t,3,4)'",
                    "[0:v][1:v] overlay=10:10:enable='between(t,0,2)'",
                    "-c:a", "copy", "-c:v", "libx264", "-preset", "ultrafast",
                    destinationAudioFilePath};
    for (int i = 0; i < cmd.length; i++) {
      Log.e("getOverlayCmdContent", "stringBuilder.toString()：" + cmd[i]);
    }
    try {
      CustomFFmpeg.getInstance().execute(originVideoFilePath, cmd, ffmpegExecuteResponseHandler);
    } catch (FFmpegCommandAlreadyRunningException e) {
      e.printStackTrace();
    }
  }

  public static void overlaySubtitles(String originVideoFilePath,
                                      List<SubtitleTimeBox> timeBoxList,
                                      String destinationVideoFilePath, int videoWidth, int videoHeight,
                                      int videoRotation, FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler) {
    List<String> cmdList = new ArrayList<>();
    cmdList.add("-y");
    cmdList.add("-i");
    cmdList.add(originVideoFilePath);
    for (int i = 0; i < timeBoxList.size(); i++) {
      cmdList.add("-i");
      cmdList.add(timeBoxList.get(i).getSubtitleImagePath());
    }
    cmdList.add("-filter_complex");
    cmdList.add(getOverlayCmdContent(timeBoxList, videoWidth,
            videoHeight, videoRotation));
    cmdList.add("-c:a");
    cmdList.add("copy");
    cmdList.add("-c:v");
    cmdList.add("libx264");
    cmdList.add("-metadata:s:v:0");
    cmdList.add("rotate=" + videoRotation);
    // cmdList.add("-preset");
    // cmdList.add("-veryfast");
    cmdList.add(destinationVideoFilePath);
    String[] cmdArray = cmdList.toArray(new String[cmdList.size()]);
    try {
      CustomFFmpeg.getInstance().execute(originVideoFilePath, cmdArray,
              ffmpegExecuteResponseHandler);
    } catch (FFmpegCommandAlreadyRunningException e) {
      e.printStackTrace();
    }
  }

  public static void overlaySubtitles(String originVideoFilePath,
                                      RecordActionLocationTask recordActionLocationTask, String destinationVideoFilePath,
                                      FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler) {
    SubtitleContent subtitleContent = (SubtitleContent) recordActionLocationTask.getActionContent();
    List<String> cmdList = new ArrayList<>();
    cmdList.add("-y");
    cmdList.add("-i");
    cmdList.add(originVideoFilePath);
    cmdList.add("-i");
    cmdList.add(subtitleContent.getActionImageData().getImagePath());
    cmdList.add("-filter_complex");
    cmdList.add(getOverlayCmdContent(recordActionLocationTask));
    cmdList.add("-c:a");
    cmdList.add("copy");
    cmdList.add("-c:v");
    cmdList.add("libx264");
    cmdList.add("-preset");
    cmdList.add("ultrafast");
    cmdList.add(destinationVideoFilePath);
    String[] cmdArray = cmdList.toArray(new String[cmdList.size()]);
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < cmdArray.length; i++) {
      stringBuilder.append(cmdArray[i]);
    }
    Log.e("overlaySubtitles", "overlaySubtitles：" + stringBuilder.toString());
    try {
      CustomFFmpeg.getInstance().execute(originVideoFilePath, cmdArray,
              ffmpegExecuteResponseHandler);
    } catch (FFmpegCommandAlreadyRunningException e) {
      e.printStackTrace();
    }
  }

  public static void overlayVideoTag(String originVideoFilePath,
                                     List<VideoTag> videoTags, String destinationVideoFilePath,
                                     int videoRotation, FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler) {
    List<String> cmdList = new ArrayList<>();
    cmdList.add("-y");
    cmdList.add("-i");
    cmdList.add(originVideoFilePath);
    for (int i = 0; i < videoTags.size(); i++) {
      cmdList.add("-i");
      cmdList.add(videoTags.get(i).getActionImageData().getImagePath());
    }
    cmdList.add("-filter_complex");
    cmdList.add(getOverlayVideoTagCmdContent(videoTags, videoRotation));
    cmdList.add("-c:a");
    cmdList.add("copy");
    cmdList.add("-c:v");
    cmdList.add("libx264");
    cmdList.add("-metadata:s:v:0");
    cmdList.add("rotate=" + videoRotation);
    // cmdList.add("-preset");
    // cmdList.add("-veryfast");
    cmdList.add(destinationVideoFilePath);
    String[] cmdArray = cmdList.toArray(new String[cmdList.size()]);
    try {
      CustomFFmpeg.getInstance().execute(originVideoFilePath, cmdArray,
              ffmpegExecuteResponseHandler);
    } catch (FFmpegCommandAlreadyRunningException e) {
      e.printStackTrace();
    }
  }

  private static String getOverlayVideoTagCmdContent(VideoTagContent videoTagContent,
                                                     RecordActionLocationTask recordActionLocationTask,
                                                     Map<String, Integer> frameInsertPositionMap, int videoRotation) {
    int allTimeLineSize = recordActionLocationTask.getGenerateLocationTaskMap().size();
    int currentTimePosition = 0;
    StringBuilder stringBuilder = new StringBuilder();
    VideoTagImageData videoTagImageData = videoTagContent.getVideoTagImageData();
    int frameSize = videoTagImageData.getFrameSize();
    Map<Integer, RecordLocation> recordLocationTaskMap =
            recordActionLocationTask.getGenerateLocationTaskMap();
    Iterator<Map.Entry<Integer, RecordLocation>> iterator =
            recordLocationTaskMap.entrySet().iterator();
    int locationPosition = 0;
    List<Integer> timePositionList = new ArrayList<>();
    while (iterator.hasNext()) {
      Map.Entry<Integer, RecordLocation> entry = iterator.next();
      timePositionList.add(entry.getKey());
    }
    Collections.sort(timePositionList, (lhs, rhs) -> lhs - rhs);
    for (Integer integer : timePositionList) {
      int framePosition = locationPosition % frameSize;
      String frameName = videoTagImageData.getVideoTagImagePathList().get(framePosition);
      float offsetProportion =
              videoTagContent.getVideoTagFrameImagePathMap().get(frameName).getOffsetProportion();
      int frameInsertPosition = frameInsertPositionMap.get(frameName);
      int timePositionMs = integer;
      Log.e("videotag", "framePosition:" + framePosition + "  locationPosition：" + locationPosition
              + "   frameSize:" + frameSize + "   timePositionMs:" + timePositionMs + "  frameName:"
              + frameName);
      RecordLocation recordLocation = recordLocationTaskMap.get(integer);
      stringBuilder.append(currentTimePosition == 0 ? "[0:v][1:v]" : ("[tmp]")
              + "[" + frameInsertPosition + ":v]");
      int offsetX;
      int offsetY;
      if (videoRotation == 90) {
        offsetX = (int) ((recordLocation.getOffsetY() + videoTagContent.getBlackPointMoveToTop()) * offsetProportion);
        offsetY = (int) ((-recordLocation.getOffsetX() + videoTagContent.getBlackPointMoveToLeft()) * offsetProportion);
      } else {
        offsetX = (int) ((recordLocation.getOffsetX() + videoTagContent.getBlackPointMoveToLeft()) * offsetProportion);
        offsetY = (int) ((recordLocation.getOffsetY() + videoTagContent.getBlackPointMoveToTop()) * offsetProportion);
      }
      Log.v("CCC", "------- " + recordLocation.getOffsetY() + " --- " + videoTagContent.getBlackPointMoveToLeft() + " " + offsetProportion);
      stringBuilder.append(" overlay=" + offsetX + ":" + offsetY + ":enable='between(n,"
              + timePositionMs + "," + (timePositionMs) + ")'"
              + (currentTimePosition != allTimeLineSize - 1 ? (" [tmp];") : ""));
      currentTimePosition++;
      if (currentTimePosition % 2 == 0) {
        locationPosition++;
      }
    }
    return stringBuilder.toString();
  }

  public static void overlayFrameVideoTag(String originVideoFilePath,
                                          RecordActionLocationTask recordActionLocationTask, String destinationVideoFilePath,
                                          int videoRotation, FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler) {
    VideoTagContent videoTagContent = (VideoTagContent) recordActionLocationTask.getActionContent();
    List<String> cmdList = new ArrayList<>();
    cmdList.add("-y");
    cmdList.add("-i");
    cmdList.add(originVideoFilePath);
    Map<String, Integer> frameInsertPositionMap = new HashMap<>();
    int insertFrame = 1;
    List<String> videoTagImagePathList =
            videoTagContent.getVideoTagImageData().getVideoTagImagePathList();
    for (int j = 0; j < videoTagImagePathList.size(); j++) {
      String frameName = videoTagImagePathList.get(j);
      cmdList.add("-i");
      cmdList.add(videoTagContent.getVideoTagFrameImagePathMap().get(frameName).getImagePath());
      frameInsertPositionMap.put(frameName, insertFrame);
      insertFrame++;
    }
    cmdList.add("-filter_complex");
    cmdList.add(
            getOverlayVideoTagCmdContent(videoTagContent, recordActionLocationTask, frameInsertPositionMap, videoRotation));
    cmdList.add("-c:a");
    cmdList.add("copy");
    cmdList.add("-c:v");
    cmdList.add("libx264");
    // cmdList.add("-metadata:s:v:0");
    // cmdList.add("rotate=" + videoRotation);
    cmdList.add("-preset");
    cmdList.add("ultrafast");
    cmdList.add(destinationVideoFilePath);
    String[] cmdArray = cmdList.toArray(new String[cmdList.size()]);
    try {
      CustomFFmpeg.getInstance().execute(originVideoFilePath, cmdArray,
              ffmpegExecuteResponseHandler);
    } catch (FFmpegCommandAlreadyRunningException e) {
      e.printStackTrace();
    }
  }

  public static void overlayNormalVideoTag(String originVideoFilePath,
                                           RecordActionLocationTask recordActionLocationTask, String destinationVideoFilePath,
                                           int videoRotation, FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler) {
    VideoTagContent videoTagContent = (VideoTagContent) recordActionLocationTask.getActionContent();
    List<String> cmdList = new ArrayList<>();
    cmdList.add("-y");
    cmdList.add("-i");
    cmdList.add(originVideoFilePath);
    cmdList.add("-i");
    cmdList.add(videoTagContent.getActionImageData().getImagePath());
    cmdList.add("-filter_complex");
    cmdList.add(
            getOverlayVideoTagCmdContent(videoTagContent, recordActionLocationTask, videoRotation));
    cmdList.add("-c:a");
    cmdList.add("copy");
    cmdList.add("-c:v");
    cmdList.add("libx264");
    // cmdList.add("-metadata:s:v:0");
    // cmdList.add("rotate=" + videoRotation);
    cmdList.add("-preset");
    cmdList.add("ultrafast");
    cmdList.add(destinationVideoFilePath);
    String[] cmdArray = cmdList.toArray(new String[cmdList.size()]);
    try {
      CustomFFmpeg.getInstance().execute(originVideoFilePath, cmdArray,
              ffmpegExecuteResponseHandler);
    } catch (FFmpegCommandAlreadyRunningException e) {
      e.printStackTrace();
    }
  }

  public static void overlayChatBox(String originVideoFilePath,
                                    List<ChatBox> chatBoxes, String destinationVideoFilePath,
                                    int videoRotation, FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler) {
    List<String> cmdList = new ArrayList<>();
    cmdList.add("-y");
    cmdList.add("-i");
    cmdList.add(originVideoFilePath);
    for (int i = 0; i < chatBoxes.size(); i++) {
      cmdList.add("-i");
      cmdList.add(chatBoxes.get(i).getActionImageData().getImagePath());
    }
    cmdList.add("-filter_complex");
    cmdList.add(getOverlayChatBoxCmdContent(chatBoxes, videoRotation));
    Log.e("preview", "content:" + getOverlayChatBoxCmdContent(chatBoxes, videoRotation));
    cmdList.add("-c:a");
    cmdList.add("copy");
    cmdList.add("-c:v");
    cmdList.add("libx264");
    cmdList.add("-metadata:s:v:0");
    cmdList.add("rotate=" + videoRotation);
    // cmdList.add("-preset");
    // cmdList.add("-veryfast");
    cmdList.add(destinationVideoFilePath);
    String[] cmdArray = cmdList.toArray(new String[cmdList.size()]);
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < cmdArray.length; i++) {
      stringBuilder.append(cmdArray[i]);
    }
    Log.e("overlayChatBox", "overlayChatBox：" + stringBuilder.toString());
    try {
      CustomFFmpeg.getInstance().execute(originVideoFilePath, cmdArray,
              ffmpegExecuteResponseHandler);
    } catch (FFmpegCommandAlreadyRunningException e) {
      e.printStackTrace();
    }
  }

  public static void overlayChatBox(String originVideoFilePath,
                                    RecordActionLocationTask recordActionLocationTask, String destinationVideoFilePath,
                                    int videoRotation, FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler) {
    ChatBoxContent chatBoxContent = (ChatBoxContent) recordActionLocationTask.getActionContent();
    List<String> cmdList = new ArrayList<>();
    cmdList.add("-y");
    cmdList.add("-i");
    cmdList.add(originVideoFilePath);
    cmdList.add("-i");
    cmdList
            .add(chatBoxContent.getActionImageData().getImagePath());
    cmdList.add("-filter_complex");
    cmdList
            .add(getOverlayChatBoxCmdContent(recordActionLocationTask, chatBoxContent, videoRotation));
    cmdList.add("-c:a");
    cmdList.add("copy");
    cmdList.add("-c:v");
    cmdList.add("libx264");
    cmdList.add("-preset");
    cmdList.add("ultrafast");
    // cmdList.add("-metadata:s:v:0");
    // cmdList.add("rotate=" + videoRotation);
    cmdList.add(destinationVideoFilePath);
    String[] cmdArray = cmdList.toArray(new String[cmdList.size()]);
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < cmdArray.length; i++) {
      stringBuilder.append(cmdArray[i]);
    }
    Log.e("overlayChatBox", "overlayChatBox：" + stringBuilder.toString());
    try {
      CustomFFmpeg.getInstance().execute(originVideoFilePath, cmdArray,
              ffmpegExecuteResponseHandler);
    } catch (FFmpegCommandAlreadyRunningException e) {
      e.printStackTrace();
    }
  }

  public static void overlayRunMan(String originVideoFilePath,
                                   List<RunMan> runMans, String destinationVideoFilePath,
                                   int videoRotation, FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler) {
    List<String> cmdList = new ArrayList<>();
    cmdList.add("-y");
    cmdList.add("-i");
    cmdList.add(originVideoFilePath);
    for (int i = 0; i < runMans.size(); i++) {
      cmdList.add("-i");
      cmdList.add(runMans.get(i).getActionImageData().getImagePath());
    }
    cmdList.add("-filter_complex");
    cmdList.add(getOverlayRunManCmdContent(runMans, videoRotation));
    Log.e("preview", "content:" + getOverlayRunManCmdContent(runMans, videoRotation));
    cmdList.add("-c:a");
    cmdList.add("copy");
    cmdList.add("-c:v");
    cmdList.add("libx264");
    cmdList.add("-metadata:s:v:0");
    cmdList.add("rotate=" + videoRotation);
    // cmdList.add("-preset");
    // cmdList.add("-veryfast");
    cmdList.add(destinationVideoFilePath);
    String[] cmdArray = cmdList.toArray(new String[cmdList.size()]);
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < cmdArray.length; i++) {
      stringBuilder.append(cmdArray[i]);
    }
    Log.e("overlayRunMan", "overlayRunMan：" + stringBuilder.toString());
    try {
      CustomFFmpeg.getInstance().execute(originVideoFilePath, cmdArray,
              ffmpegExecuteResponseHandler);
    } catch (FFmpegCommandAlreadyRunningException e) {
      e.printStackTrace();
    }
  }

  public static void overlayRunMan(String originVideoFilePath,
                                   RecordActionLocationTask recordActionLocationTask, String destinationVideoFilePath,
                                   int videoRotation, FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler) {
    List<String> cmdList = new ArrayList<>();
    RunManContent runManContent = (RunManContent) recordActionLocationTask.getActionContent();
    cmdList.add("-y");
    cmdList.add("-i");
    cmdList.add(originVideoFilePath);
    cmdList.add("-i");
    cmdList.add(runManContent.getActionImageData().getImagePath());
    cmdList.add("-filter_complex");
    cmdList.add(getOverlayRunManCmdContent(recordActionLocationTask, runManContent, videoRotation));
    cmdList.add("-c:a");
    cmdList.add("copy");
    cmdList.add("-c:v");
    cmdList.add("libx264");
    // cmdList.add("-metadata:s:v:0");
    // cmdList.add("rotate=" + videoRotation);
    cmdList.add("-preset");
    cmdList.add("ultrafast");
    cmdList.add(destinationVideoFilePath);
    String[] cmdArray = cmdList.toArray(new String[cmdList.size()]);
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < cmdArray.length; i++) {
      stringBuilder.append(cmdArray[i]);
    }
    Log.e("overlayRunMan", "overlayRunMan：" + stringBuilder.toString());
    try {
      CustomFFmpeg.getInstance().execute(originVideoFilePath, cmdArray,
              ffmpegExecuteResponseHandler);
    } catch (FFmpegCommandAlreadyRunningException e) {
      e.printStackTrace();
    }
  }

  public static void overlaySticker(String originVideoFilePath,
                                    List<Sticker> stickers, String destinationVideoFilePath,
                                    int videoRotation, FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler) {
    Map<String, Integer> frameInsertPositionMap = new HashMap<>();
    List<String> cmdList = new ArrayList<>();
    cmdList.add("-y");
    cmdList.add("-i");
    cmdList.add(originVideoFilePath);
    int insertFrame = 1;
    for (int i = 0; i < stickers.size(); i++) {
      Sticker sticker = stickers.get(i);
      List<String> stickerImagePathList =
              stickers.get(i).getStickerImageData().getStickerImagePathList();
      for (int j = 0; j < stickerImagePathList.size(); j++) {
        String frameName = stickerImagePathList.get(j);
        cmdList.add("-i");
        cmdList.add(sticker.getStickerFrameImageData(frameName).getImagePath());
        frameInsertPositionMap.put(frameName, insertFrame);
        insertFrame++;
      }
    }
    cmdList.add("-filter_complex");
    cmdList.add(getOverlayStickerCmdContent(stickers, frameInsertPositionMap, videoRotation));
    cmdList.add("-c:a");
    cmdList.add("copy");
    cmdList.add("-c:v");
    cmdList.add("libx264");
    cmdList.add("-metadata:s:v:0");
    cmdList.add("rotate=" + videoRotation);
    // cmdList.add("-preset");
    // cmdList.add("-veryfast");
    cmdList.add(destinationVideoFilePath);
    String[] cmdArray = cmdList.toArray(new String[cmdList.size()]);
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < cmdArray.length; i++) {
      stringBuilder.append(cmdArray[i]);
    }
    Log.e("overlaySticker", "overlaySticker：" + stringBuilder.toString());
    try {
      CustomFFmpeg.getInstance().execute(originVideoFilePath, cmdArray,
              ffmpegExecuteResponseHandler);
    } catch (FFmpegCommandAlreadyRunningException e) {
      e.printStackTrace();
    }
  }

  public static void overlaySticker(String originVideoFilePath,
                                    StickerContent stickerContent, RecordActionLocationTask recordActionLocationTask,
                                    String destinationVideoFilePath, int videoRotation,
                                    FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler) {
    Map<String, Integer> frameInsertPositionMap = new HashMap<>();
    List<String> cmdList = new ArrayList<>();
    cmdList.add("-y");
    cmdList.add("-i");
    cmdList.add(originVideoFilePath);
    int insertFrame = 1;
    List<String> stickerImagePathList =
            stickerContent.getStickerImageData().getStickerImagePathList();
    for (int j = 0; j < stickerImagePathList.size(); j++) {
      String frameName = stickerImagePathList.get(j);
      cmdList.add("-i");
      cmdList.add(stickerContent.getStickerFrameImageData(frameName).getImagePath());
      frameInsertPositionMap.put(frameName, insertFrame);
      insertFrame++;
    }
    cmdList.add("-filter_complex");
    cmdList.add(getOverlayStickerCmdContent(stickerContent, recordActionLocationTask,
            frameInsertPositionMap, videoRotation));
    cmdList.add("-c:a");
    cmdList.add("copy");
    cmdList.add("-c:v");
    cmdList.add("libx264");
    // cmdList.add("-metadata:s:v:0");
    // cmdList.add("rotate=" + videoRotation);
    cmdList.add("-preset");
    cmdList.add("ultrafast");
    cmdList.add(destinationVideoFilePath);
    String[] cmdArray = cmdList.toArray(new String[cmdList.size()]);
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < cmdArray.length; i++) {
      stringBuilder.append(cmdArray[i]);
    }
    Log.e("overlaySticker", "overlaySticker：" + stringBuilder.toString());
    try {
      CustomFFmpeg.getInstance().execute(originVideoFilePath, cmdArray,
              ffmpegExecuteResponseHandler);
    } catch (FFmpegCommandAlreadyRunningException e) {
      e.printStackTrace();
    }
  }


  public static void rotateRecordVideo(String originVideoFilePath, String destinationVideoFilePath,
                                       VideoMetaData videoMetaData, VideoMetaData originVideoMetaData, float videoRatioWH,
                                       int threadCount, FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler) {
    Log.e("duration", "duration:" + videoMetaData.getDuration());
    List<String> cmdList = new ArrayList<>();
    cmdList.add("-hwaccel");
    cmdList.add("auto");
    cmdList.add("-flags2");
    cmdList.add("fast");
    cmdList.add("-y");
    cmdList.add("-i");
    cmdList.add(originVideoFilePath);
    cmdList.add("-dct");
    cmdList.add("fastint");
    cmdList.add("-me_method");
    cmdList.add("zero");
    cmdList.add("-c:a");
    cmdList.add("copy");
    cmdList.add("-c:v");
    cmdList.add("libx264");
    cmdList.add("-coder");
    cmdList.add("vlc");
    cmdList.add("-fastfirstpass");
    cmdList.add("1");
    switch (videoMetaData.getRotation()) {
      case 0:
        cmdList.add("-vf");
        cmdList.add("format=yuv420p" + ",select='eq(pict_type,P)'");
        break;
      case 90:
        cmdList.add("-vf");
        cmdList
                .add("transpose=1,format=yuv420p," + getCropParamsString(videoMetaData, videoRatioWH)
                        + ",select='eq(pict_type,P)'");
        break;
      case 180:
        cmdList.add("-vf");
        cmdList.add("transpose=1,transpose=1,format=yuv420p" + ",select='eq(pict_type,P)'");
        break;
      case 270:
        cmdList.add("-vf");
        // cmdList
        // .add("transpose=2,geq=p(W-X\\,Y),format=yuv420p,"
        // + getCropParamsString(videoMetaData, videoRatioWH)
        // + ",select='eq(pict_type,P)'");
        cmdList
                .add("transpose=2,format=yuv420p,"
                        + getCropParamsString(videoMetaData, videoRatioWH)
                        + ",select='eq(pict_type,P)'");
        break;
      default:
        break;
    }
    cmdList.add("-r");
    cmdList.add("30");
    cmdList.add("-x264opts");
    cmdList.add("opencl");
    cmdList.add("-preset");
    cmdList.add("ultrafast");
    cmdList.add("-tune");
    cmdList.add("zerolatency");
    cmdList.add("-thread_type");
    cmdList.add("frame");
    cmdList.add("-threads");
    cmdList.add(String.valueOf(threadCount));
    cmdList.add("-metadata:s:v:0");
    cmdList.add("rotate=" + 0);
    cmdList.add(destinationVideoFilePath);
    String[] cmdArray = cmdList.toArray(new String[cmdList.size()]);
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < cmdArray.length; i++) {
      stringBuilder.append(cmdArray[i]);
    }
    Log.e("rotateVideo", "rotateVideo：" + stringBuilder.toString());
    try {
      CustomFFmpeg.getInstance().execute(originVideoFilePath, cmdArray,
              ffmpegExecuteResponseHandler);
    } catch (FFmpegCommandAlreadyRunningException e) {
      e.printStackTrace();
    }
  }

  public static void cutMedia(String originMediaFilePath, String destinationMediaFilePath,
                              int startPositionMs, int durationMs,
                              FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler) {
    String[] cmdList =
            new String[]{"-y", "-i", originMediaFilePath, "-ss",
                    DateUtils.getFfmpegDurationString(startPositionMs), "-t",
                    DateUtils.getFfmpegDurationString(durationMs), "-vcodec", "libx264", "-acodec",
                    "libmp3lame", "-pix_fmt", "yuv420p", "-preset",
                    "ultrafast", "-threads", String.valueOf(BEST_THREAD_COUNT), destinationMediaFilePath};
    StringBuilder stringBuilder = new StringBuilder();
    for (String cmd : cmdList) {
      stringBuilder.append(cmd);
    }
    Log.e("cutMedia", "cutMedia：" + stringBuilder.toString());
    try {
      CustomFFmpeg.getInstance().execute(originMediaFilePath, cmdList,
              ffmpegExecuteResponseHandler);
    } catch (FFmpegCommandAlreadyRunningException e) {
      e.printStackTrace();
    }
  }

  public static void cutMedia(String originMediaFilePath, String destinationMediaFilePath,
                              int startPositionMs, int durationMs, int threadCount,
                              FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler) {
    List<String> cmdList = new ArrayList<>();
    cmdList.add("-hwaccel");
    cmdList.add("auto");
    cmdList.add("-flags2");
    cmdList.add("fast");
    cmdList.add("-y");
    cmdList.add("-i");
    cmdList.add(originMediaFilePath);
    cmdList.add("-dct");
    cmdList.add("fastint");
    cmdList.add("-me_method");
    cmdList.add("zero");
    cmdList.add("-c:a");
    cmdList.add("copy");
    cmdList.add("-c:v");
    cmdList.add("libx264");
    cmdList.add("-coder");
    cmdList.add("vlc");
    cmdList.add("-fastfirstpass");
    cmdList.add("1");
    cmdList.add("-ss");
    cmdList.add(DateUtils.getFfmpegDurationString(startPositionMs));
    cmdList.add("-t");
    cmdList.add(DateUtils.getFfmpegDurationString(durationMs));
    cmdList.add("-pix_fmt");
    cmdList.add("yuv420p");
    cmdList.add("-preset");
    cmdList.add("ultrafast");
    cmdList.add("-thread_type");
    cmdList.add("frame");
    cmdList.add("-threads");
    cmdList.add(String.valueOf(threadCount));
    cmdList.add(destinationMediaFilePath);
    String[] cmdArray = cmdList.toArray(new String[cmdList.size()]);
    StringBuilder stringBuilder = new StringBuilder();
    for (String cmd : cmdList) {
      stringBuilder.append(cmd);
    }
    Log.e("cutMedia", "cutMedia：" + stringBuilder.toString());
    try {
      CustomFFmpeg.getInstance().execute(destinationMediaFilePath, cmdArray,
              ffmpegExecuteResponseHandler);
    } catch (FFmpegCommandAlreadyRunningException e) {
      e.printStackTrace();
    }
  }

  public static void cutMediaToEnd(String originMediaFilePath, String destinationMediaFilePath,
                                   int startPositionMs, FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler) {
    String[] cmdList =
            new String[]{"-y", "-i", originMediaFilePath, "-ss",
                    DateUtils.getFfmpegDurationString(startPositionMs), "-vcodec", "libx264", "-acodec",
                    "libmp3lame", "-pix_fmt", "yuv420p", "-preset",
                    "ultrafast", destinationMediaFilePath};
    StringBuilder stringBuilder = new StringBuilder();
    for (String cmd : cmdList) {
      stringBuilder.append(cmd);
    }
    Log.e("cutMedia", "cutMedia：" + stringBuilder.toString());
    try {
      CustomFFmpeg.getInstance().execute(originMediaFilePath, cmdList,
              ffmpegExecuteResponseHandler);
    } catch (FFmpegCommandAlreadyRunningException e) {
      e.printStackTrace();
    }
  }

  private static String getCropParamsString(VideoMetaData videoMetaData, float videoRatioWH) {
    int height, width, x = 0, y = 0;
    switch (videoMetaData.getRotation()) {
      case 0:
        break;
      case 90:
        width = videoMetaData.getHeight();
        height = (int) (width / videoRatioWH);
        return "crop=w=" + width + ":h=" + height + ":x=" + x
                + ":y=" + y;
      case 180:
        break;
      case 270:
        width = videoMetaData.getHeight();
        height = (int) (width / videoRatioWH);
        y = videoMetaData.getWidth() - width;
        return "crop=w=" + width + ":h=" + height + ":x=" + x
                + ":y=" + y;
    }
    return null;
  }

  private static String getDestinationVideoSizeString(VideoMetaData originVideoMetaData,
                                                      float videoRatioWH) {
    int height, width;
    switch (originVideoMetaData.getRotation()) {
      case 0:
        break;
      case 90:
        width = originVideoMetaData.getHeight();
        height = (int) (width / videoRatioWH);
        return width + "x" + height;
      case 180:
        break;
      case 270:
        height = originVideoMetaData.getHeight();
        width = (int) (height / videoRatioWH);
        return width + "x" + height;
    }
    return null;
  }

  public static void changeVideoRotationMetaData(String originVideoFilePath,
                                                 int rotation, String destinationVideoFilePath,
                                                 FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler) {
    List<String> cmdList = new ArrayList<>();
    cmdList.add("-y");
    cmdList.add("-i");
    cmdList.add(originVideoFilePath);
    cmdList.add("-c:a");
    cmdList.add("copy");
    cmdList.add("-c:v");
    cmdList.add("copy");
    cmdList.add("-metadata:s:v:0");
    cmdList.add("rotate=" + rotation);
    cmdList.add("-preset");
    cmdList.add("ultrafast");
    cmdList.add(destinationVideoFilePath);
    String[] cmdArray = cmdList.toArray(new String[cmdList.size()]);
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < cmdArray.length; i++) {
      stringBuilder.append(cmdArray[i]);
    }
    Log.e("changeMetaData", "rotateVideo：" + stringBuilder.toString());
    try {
      CustomFFmpeg.getInstance().execute(originVideoFilePath, cmdArray,
              ffmpegExecuteResponseHandler);
    } catch (FFmpegCommandAlreadyRunningException e) {
      e.printStackTrace();
    }
  }

  private static String getOverlayCmdContent(List<SubtitleTimeBox> timeBoxList,
                                             int videoWidth, int videoHeight, int videoRotation) {
    int allTimeLineSize = 0;
    int currentTimePosition = 0;
    for (SubtitleTimeBox subtitleTimeBox : timeBoxList) {
      allTimeLineSize += subtitleTimeBox.getTimeLineList().size();
    }
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < timeBoxList.size(); i++) {
      SubtitleTimeBox subtitleTimeBox = timeBoxList.get(i);
      int overlayX = 0;
      int overlayY = 0;
      switch (subtitleTimeBox.getSubtitleType()) {
        case CARTOON_WHITE:
        case CARTOON_EN:
          switch (videoRotation) {
            case 0:
              overlayY =
                      videoHeight - subtitleTimeBox.getBitmapHeight() - SubtitleUtils.SUBTITLE_PADDING;
              overlayX = SubtitleUtils.SUBTITLE_PADDING;
              break;
            case 90:
              overlayX =
                      videoWidth - subtitleTimeBox.getBitmapHeight() - SubtitleUtils.SUBTITLE_PADDING;
              overlayY = SubtitleUtils.SUBTITLE_PADDING;
              break;
          }
          break;
      }
      List<TimeLine> timeLines = subtitleTimeBox.getTimeLineList();
      for (TimeLine timeLine : timeLines) {
        stringBuilder.append(currentTimePosition == 0 ? "[0:v][1:v]" : ("[tmp]")
                + "[" + (i + 1) + ":v]");
        stringBuilder.append(" overlay=" + overlayX + ":" + overlayY + ":enable='between(t,"
                + String.valueOf(timeLine.getStartTimeMs() / 1000) + ","
                + String.valueOf(timeLine.getEndTimeMs() / 1000) + ")'"
                + (currentTimePosition != allTimeLineSize - 1 ? (" [tmp];") : ""));
        currentTimePosition++;
      }
    }
    return stringBuilder.toString();
  }

  private static String getOverlayCmdContent(RecordActionLocationTask recordActionLocationTask) {
    int allTimeLineSize = recordActionLocationTask.getGenerateLocationTaskMap().size();
    int currentTimePosition = 0;
    StringBuilder stringBuilder = new StringBuilder();
    Map<Integer, RecordLocation> recordLocationTaskMap =
            recordActionLocationTask.getGenerateLocationTaskMap();
    Iterator<Map.Entry<Integer, RecordLocation>> iterator =
            recordLocationTaskMap.entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry<Integer, RecordLocation> entry = iterator.next();
      int timePositionMs = entry.getKey();
      stringBuilder.append(currentTimePosition == 0 ? "[0:v][1:v]" : "[tmp][1:v]");
      stringBuilder.append(" overlay=" + 0 + ":" + 0 + ":enable='between(n,"
              + timePositionMs + "," + (timePositionMs) + ")'"
              + (currentTimePosition != allTimeLineSize - 1 ? (" [tmp];") : ""));
      currentTimePosition++;
    }
    return stringBuilder.toString();
  }

  private static String getOverlayVideoTagCmdContent(List<VideoTag> videoTags, int videoRotation) {
    int allTimeLineSize = 0;
    int currentTimePosition = 0;
    for (VideoTag videoTag : videoTags) {
      allTimeLineSize += videoTag.getLocationSize();
    }
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < videoTags.size(); i++) {
      VideoTag videoTag = videoTags.get(i);
      float videoTagOffsetProportion = videoTag.getActionImageData().getOffsetProportion();
      List<RecordActionLocationTask> recordLocationLineList =
              videoTag.getRecordActionLocationTasks();
      for (RecordActionLocationTask recordActionLocationTask : recordLocationLineList) {
        Map<Integer, RecordLocation> recordLocationTaskMap =
                recordActionLocationTask.getGenerateLocationTaskMap();
        Iterator<Map.Entry<Integer, RecordLocation>> iterator =
                recordLocationTaskMap.entrySet().iterator();
        while (iterator.hasNext()) {
          Map.Entry<Integer, RecordLocation> entry = iterator.next();
          int timePositionMs = entry.getKey();
          RecordLocation recordLocation = entry.getValue();
          stringBuilder.append(currentTimePosition == 0 ? "[0:v][1:v]" : ("[tmp]")
                  + "[" + (i + 1) + ":v]");
          int offsetX;
          int offsetY;
          if (videoRotation == 90) {
            offsetX = (int) (recordLocation.getOffsetY() * videoTagOffsetProportion);
            offsetY = (int) (-recordLocation.getOffsetX() * videoTagOffsetProportion);
          } else {
            offsetX = (int) (recordLocation.getOffsetX() * videoTagOffsetProportion);
            offsetY = (int) (recordLocation.getOffsetY() * videoTagOffsetProportion);
          }
          stringBuilder.append(" overlay=" + offsetX + ":" + offsetY + ":enable='between(n,"
                  + timePositionMs + "," + (timePositionMs) + ")'"
                  + (currentTimePosition != allTimeLineSize - 1 ? (" [tmp];") : ""));
          currentTimePosition++;
        }
      }
    }
    return stringBuilder.toString();
  }

  private static String getOverlayVideoTagCmdContent(VideoTagContent videoTagContent,
                                                     RecordActionLocationTask recordActionLocationTask, int videoRotation) {
    int allTimeLineSize = recordActionLocationTask.getGenerateLocationTaskMap().size();
    int currentTimePosition = 0;
    StringBuilder stringBuilder = new StringBuilder();
    float videoTagOffsetProportion = videoTagContent.getActionImageData().getOffsetProportion();
    Map<Integer, RecordLocation> recordLocationTaskMap =
            recordActionLocationTask.getGenerateLocationTaskMap();
    Iterator<Map.Entry<Integer, RecordLocation>> iterator =
            recordLocationTaskMap.entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry<Integer, RecordLocation> entry = iterator.next();
      int timePositionMs = entry.getKey();
      RecordLocation recordLocation = entry.getValue();
      stringBuilder.append(currentTimePosition == 0 ? "[0:v][1:v]" : "[tmp][1:v]");
      int offsetX;
      int offsetY;
      if (videoRotation == 90) {
        offsetX = (int) (recordLocation.getOffsetY() * videoTagOffsetProportion);
        offsetY = (int) (-recordLocation.getOffsetX() * videoTagOffsetProportion);
      } else {
        offsetX = (int) (recordLocation.getOffsetX() * videoTagOffsetProportion);
        offsetY = (int) (recordLocation.getOffsetY() * videoTagOffsetProportion);
      }
      stringBuilder.append(" overlay=" + offsetX + ":" + offsetY + ":enable='between(n,"
              + timePositionMs + "," + (timePositionMs) + ")'"
              + (currentTimePosition != allTimeLineSize - 1 ? (" [tmp];") : ""));
      currentTimePosition++;
    }
    return stringBuilder.toString();
  }

  private static String getOverlayStickerCmdContent(List<Sticker> stickers,
                                                    Map<String, Integer> frameInsertPositionMap, int videoRotation) {
    int allTimeLineSize = 0;
    int currentTimePosition = 0;
    for (Sticker sticker : stickers) {
      allTimeLineSize += sticker.getLocationSize();
    }
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < stickers.size(); i++) {
      Sticker sticker = stickers.get(i);
      StickerImageData stickerImageData = sticker.getStickerImageData();
      List<RecordActionLocationTask> recordLocationLineList =
              sticker.getRecordActionLocationTasks();
      int frameSize = stickerImageData.getFrameSize();
      for (RecordActionLocationTask recordActionLocationTask : recordLocationLineList) {
        Map<Integer, RecordLocation> recordLocationTaskMap =
                recordActionLocationTask.getGenerateLocationTaskMap();
        Iterator<Map.Entry<Integer, RecordLocation>> iterator =
                recordLocationTaskMap.entrySet().iterator();
        int locationPosition = 0;
        while (iterator.hasNext()) {
          Map.Entry<Integer, RecordLocation> entry = iterator.next();
          int framePosition = locationPosition % frameSize;
          Log.e("sticker",
                  "framePosition:" + framePosition + "  locationPosition：" + locationPosition
                          + "   frameSize:" + frameSize);
          String frameName = stickerImageData.getStickerImagePathList().get(framePosition);
          float offsetProportion =
                  sticker.getStickerFrameImageData(frameName).getOffsetProportion();
          int frameInsertPosition = frameInsertPositionMap.get(frameName);
          int timePositionMs = entry.getKey();
          RecordLocation recordLocation = entry.getValue();
          stringBuilder.append(currentTimePosition == 0 ? "[0:v][1:v]" : ("[tmp]")
                  + "[" + frameInsertPosition + ":v]");
          int offsetX;
          int offsetY;
          if (videoRotation == 90) {
            offsetX = (int) (recordLocation.getOffsetY() * offsetProportion);
            offsetY = (int) (-recordLocation.getOffsetX() * offsetProportion);
          } else {
            offsetX = (int) (recordLocation.getOffsetX() * offsetProportion);
            offsetY = (int) (recordLocation.getOffsetY() * offsetProportion);
          }
          stringBuilder.append(" overlay=" + offsetX + ":" + offsetY + ":enable='between(n,"
                  + timePositionMs + "," + (timePositionMs) + ")'"
                  + (currentTimePosition != allTimeLineSize - 1 ? (" [tmp];") : ""));
          currentTimePosition++;
          if (currentTimePosition % 2 == 0) {
            locationPosition++;
          }
        }
      }
    }
    return stringBuilder.toString();
  }

  private static String getOverlayStickerCmdContent(StickerContent stickerContent,
                                                    RecordActionLocationTask recordActionLocationTask,
                                                    Map<String, Integer> frameInsertPositionMap, int videoRotation) {
    int allTimeLineSize = recordActionLocationTask.getGenerateLocationTaskMap().size();
    int currentTimePosition = 0;
    StringBuilder stringBuilder = new StringBuilder();
    StickerImageData stickerImageData = stickerContent.getStickerImageData();
    int frameSize = stickerImageData.getFrameSize();
    Map<Integer, RecordLocation> recordLocationTaskMap =
            recordActionLocationTask.getGenerateLocationTaskMap();
    Iterator<Map.Entry<Integer, RecordLocation>> iterator =
            recordLocationTaskMap.entrySet().iterator();
    int locationPosition = 0;
    List<Integer> timePositionList = new ArrayList<>();
    while (iterator.hasNext()) {
      Map.Entry<Integer, RecordLocation> entry = iterator.next();
      timePositionList.add(entry.getKey());
    }
    Collections.sort(timePositionList, (lhs, rhs) -> lhs - rhs);
    for (Integer integer : timePositionList) {
      int framePosition = locationPosition % frameSize;
      String frameName = stickerImageData.getStickerImagePathList().get(framePosition);
      float offsetProportion =
              stickerContent.getStickerFrameImageData(frameName).getOffsetProportion();
      int frameInsertPosition = frameInsertPositionMap.get(frameName);
      int timePositionMs = integer;
      Log.e("sticker", "framePosition:" + framePosition + "  locationPosition：" + locationPosition
              + "   frameSize:" + frameSize + "   timePositionMs:" + timePositionMs + "  frameName:"
              + frameName);
      RecordLocation recordLocation = recordLocationTaskMap.get(integer);
      stringBuilder.append(currentTimePosition == 0 ? "[0:v][1:v]" : ("[tmp]")
              + "[" + frameInsertPosition + ":v]");
      int offsetX;
      int offsetY;
      if (videoRotation == 90) {
        offsetX = (int) (recordLocation.getOffsetY() * offsetProportion);
        offsetY = (int) (-recordLocation.getOffsetX() * offsetProportion);
      } else {
        offsetX = (int) (recordLocation.getOffsetX() * offsetProportion);
        offsetY = (int) (recordLocation.getOffsetY() * offsetProportion);
      }
      stringBuilder.append(" overlay=" + offsetX + ":" + offsetY + ":enable='between(n,"
              + timePositionMs + "," + (timePositionMs) + ")'"
              + (currentTimePosition != allTimeLineSize - 1 ? (" [tmp];") : ""));
      currentTimePosition++;
      if (currentTimePosition % 2 == 0) {
        locationPosition++;
      }
    }
    return stringBuilder.toString();
  }

  // TODO:videoTag与ChatBox相关逻辑性合并
  private static String getOverlayChatBoxCmdContent(List<ChatBox> chatBoxList, int videoRotation) {
    int allTimeLineSize = 0;
    int currentTimePosition = 0;
    for (ChatBox chatBox : chatBoxList) {
      allTimeLineSize += chatBox.getLocationSize();
    }
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < chatBoxList.size(); i++) {
      ChatBox chatBox = chatBoxList.get(i);
      float videoTagOffsetProportion = chatBox.getActionImageData().getOffsetProportion();
      List<RecordActionLocationTask> recordLocationLineList =
              chatBox.getRecordActionLocationTasks();
      for (RecordActionLocationTask recordActionLocationTask : recordLocationLineList) {
        Map<Integer, RecordLocation> recordLocationTaskMap =
                recordActionLocationTask.getGenerateLocationTaskMap();
        Iterator<Map.Entry<Integer, RecordLocation>> iterator =
                recordLocationTaskMap.entrySet().iterator();
        while (iterator.hasNext()) {
          Map.Entry<Integer, RecordLocation> entry = iterator.next();
          int timePositionMs = entry.getKey();
          RecordLocation recordLocation = entry.getValue();
          stringBuilder.append(currentTimePosition == 0 ? "[0:v][1:v]" : ("[tmp]")
                  + "[" + (i + 1) + ":v]");
          int offsetX;
          int offsetY;
          if (videoRotation == 90) {
            offsetX = (int) (recordLocation.getOffsetY() * videoTagOffsetProportion);
            offsetY = (int) (-recordLocation.getOffsetX() * videoTagOffsetProportion);
          } else {
            offsetX = (int) (recordLocation.getOffsetX() * videoTagOffsetProportion);
            offsetY = (int) (recordLocation.getOffsetY() * videoTagOffsetProportion);
          }
          stringBuilder.append(" overlay=" + offsetX + ":" + offsetY + ":enable='between(n,"
                  + timePositionMs + "," + (timePositionMs) + ")'"
                  + (currentTimePosition != allTimeLineSize - 1 ? (" [tmp];") : ""));
          currentTimePosition++;
        }
      }
    }
    return stringBuilder.toString();
  }

  private static String getOverlayChatBoxCmdContent(
          RecordActionLocationTask recordActionLocationTask, ChatBoxContent chatBoxContent,
          int videoRotation) {
    int allTimeLineSize = 0;
    int currentTimePosition = 0;
    allTimeLineSize += recordActionLocationTask.getGenerateLocationTaskMap().size();
    StringBuilder stringBuilder = new StringBuilder();
    float videoTagOffsetProportion = chatBoxContent.getActionImageData().getOffsetProportion();
    Map<Integer, RecordLocation> recordLocationTaskMap =
            recordActionLocationTask.getGenerateLocationTaskMap();
    Iterator<Map.Entry<Integer, RecordLocation>> iterator =
            recordLocationTaskMap.entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry<Integer, RecordLocation> entry = iterator.next();
      int timePositionMs = entry.getKey();
      RecordLocation recordLocation = entry.getValue();
      stringBuilder.append(currentTimePosition == 0 ? "[0:v][1:v]" : "[tmp][1:v]");
      int offsetX;
      int offsetY;
      if (videoRotation == 90) {
        offsetX = (int) (recordLocation.getOffsetY() * videoTagOffsetProportion);
        offsetY = (int) (-recordLocation.getOffsetX() * videoTagOffsetProportion);
      } else {
        offsetX = (int) (recordLocation.getOffsetX() * videoTagOffsetProportion);
        offsetY = (int) (recordLocation.getOffsetY() * videoTagOffsetProportion);
      }
      stringBuilder.append(" overlay=" + offsetX + ":" + offsetY + ":enable='between(n,"
              + timePositionMs + "," + (timePositionMs) + ")'"
              + (currentTimePosition != allTimeLineSize - 1 ? (" [tmp];") : ""));
      currentTimePosition++;
    }
    return stringBuilder.toString();
  }

  private static String getOverlayRunManCmdContent(List<RunMan> runManList, int videoRotation) {
    int allTimeLineSize = 0;
    int currentTimePosition = 0;
    for (RunMan runMan : runManList) {
      allTimeLineSize += runMan.getLocationSize();
    }
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < runManList.size(); i++) {
      RunMan runMan = runManList.get(i);
      float videoTagOffsetProportion = runMan.getActionImageData().getOffsetProportion();
      List<RecordActionLocationTask> recordLocationLineList =
              runMan.getRecordActionLocationTasks();
      for (RecordActionLocationTask recordActionLocationTask : recordLocationLineList) {
        Map<Integer, RecordLocation> recordLocationTaskMap =
                recordActionLocationTask.getGenerateLocationTaskMap();
        Iterator<Map.Entry<Integer, RecordLocation>> iterator =
                recordLocationTaskMap.entrySet().iterator();
        while (iterator.hasNext()) {
          Map.Entry<Integer, RecordLocation> entry = iterator.next();
          int timePositionMs = entry.getKey();
          RecordLocation recordLocation = entry.getValue();
          stringBuilder.append(currentTimePosition == 0 ? "[0:v][1:v]" : ("[tmp]")
                  + "[" + (i + 1) + ":v]");
          int offsetX;
          int offsetY;
          if (videoRotation == 90) {
            offsetX = (int) (recordLocation.getOffsetY() * videoTagOffsetProportion);
            offsetY = (int) (-recordLocation.getOffsetX() * videoTagOffsetProportion);
          } else {
            offsetX = (int) (recordLocation.getOffsetX() * videoTagOffsetProportion);
            offsetY = (int) (recordLocation.getOffsetY() * videoTagOffsetProportion);
          }
          stringBuilder.append(" overlay=" + offsetX + ":" + offsetY + ":enable='between(n,"
                  + timePositionMs + "," + (timePositionMs) + ")'"
                  + (currentTimePosition != allTimeLineSize - 1 ? (" [tmp];") : ""));
          currentTimePosition++;
        }
      }
    }
    return stringBuilder.toString();
  }

  private static String getOverlayRunManCmdContent(
          RecordActionLocationTask recordActionLocationTask, RunManContent runManContent,
          int videoRotation) {
    int allTimeLineSize = recordActionLocationTask.getGenerateLocationTaskMap().size();
    int currentTimePosition = 0;
    StringBuilder stringBuilder = new StringBuilder();
    float videoTagOffsetProportion = runManContent.getActionImageData().getOffsetProportion();
    Map<Integer, RecordLocation> recordLocationTaskMap =
            recordActionLocationTask.getGenerateLocationTaskMap();
    Iterator<Map.Entry<Integer, RecordLocation>> iterator =
            recordLocationTaskMap.entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry<Integer, RecordLocation> entry = iterator.next();
      int timePositionMs = entry.getKey();
      RecordLocation recordLocation = entry.getValue();
      stringBuilder.append(currentTimePosition == 0 ? "[0:v][1:v]" : "[tmp][1:v]");
      int offsetX;
      int offsetY;
      if (videoRotation == 90) {
        offsetX = (int) (recordLocation.getOffsetY() * videoTagOffsetProportion);
        offsetY = (int) (-recordLocation.getOffsetX() * videoTagOffsetProportion);
      } else {
        offsetX = (int) (recordLocation.getOffsetX() * videoTagOffsetProportion);
        offsetY = (int) (recordLocation.getOffsetY() * videoTagOffsetProportion);
      }
      stringBuilder.append(" overlay=" + offsetX + ":" + offsetY + ":enable='between(n,"
              + timePositionMs + "," + (timePositionMs) + ")'"
              + (currentTimePosition != allTimeLineSize - 1 ? (" [tmp];") : ""));
      currentTimePosition++;
    }
    return stringBuilder.toString();
  }

  public static void loopFrame(String originVideoFilePath,
                               String destinationAudioFilePath,
                               FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler) {
    String[] cmd =
            new String[]{
                    "-i",
                    originVideoFilePath,
                    "-loop",
                    "-c:a", "copy", "-c:v", "libx264", "-preset", "veryfast",
                    destinationAudioFilePath};
    try {
      CustomFFmpeg.getInstance().execute(originVideoFilePath, cmd, ffmpegExecuteResponseHandler);
    } catch (FFmpegCommandAlreadyRunningException e) {
      e.printStackTrace();
    }
  }

  public static void concatVideo(List<String> concatVideoPathList,
                                 String destinationVideoFilePath, int videoRotation,
                                 FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler) {
    List<String> cmdList = new ArrayList<>();
    for (int i = 0; i < concatVideoPathList.size(); i++) {
      cmdList.add("-i");
      cmdList.add(concatVideoPathList.get(i));
    }
    cmdList.add("-filter_complex");
    StringBuilder filterBuilder = new StringBuilder();
    for (int i = 0; i < concatVideoPathList.size(); i++) {
      // if (i == 1) {
      // filterBuilder.append("[" + i + ":0] ");
      // } else {
      filterBuilder.append("[" + i + ":0] " + "[" + i + ":1] ");
      // }
    }
    filterBuilder.append("concat=n=" + concatVideoPathList.size() + ":v=1:a=1 [v] [a]");
    cmdList.add(filterBuilder.toString());
    cmdList.add("-map");
    cmdList.add("[v]");
    cmdList.add("-map");
    cmdList.add("[a]");
    cmdList.add("-c:a");
    cmdList.add("libmp3lame");
    cmdList.add("-c:v");
    cmdList.add("libx264");
    cmdList.add("-pix_fmt");
    cmdList.add("yuv420p");
    cmdList.add("-preset");
    cmdList.add("ultrafast");
    cmdList.add("-tune");
    cmdList.add("zerolatency");
    cmdList.add(destinationVideoFilePath);
    String[] cmdArray = cmdList.toArray(new String[cmdList.size()]);
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < cmdArray.length; i++) {
      stringBuilder.append(cmdArray[i]);
    }
    Log.e("concatAudio", "concatAudio：" + stringBuilder.toString());
    try {
      CustomFFmpeg.getInstance().execute(concatVideoPathList.get(0), cmdArray,
              ffmpegExecuteResponseHandler);
    } catch (FFmpegCommandAlreadyRunningException e) {
      e.printStackTrace();
    }
  }

  public static void concatAudio(String originAudioFilePath, String secondOriginAudioFilePath,
                                 String destinationAudioFilePath,
                                 FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler) {
    List<String> cmdList = new ArrayList<>();
    cmdList.add("-i");
    // cmdList.add(originAudioFilePath);
    // cmdList.add("-i");
    // cmdList.add(secondOriginAudioFilePath);
    // cmdList.add("-filter_complex");
    cmdList
            .add("concat:" + originAudioFilePath + "|" + secondOriginAudioFilePath);
    cmdList.add("-c:a");
    cmdList.add("libmp3lame");
    cmdList.add("-preset");
    cmdList.add("ultrafast");
    cmdList.add(destinationAudioFilePath);
    String[] cmdArray = cmdList.toArray(new String[cmdList.size()]);
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < cmdArray.length; i++) {
      stringBuilder.append(cmdArray[i]);
    }
    Log.e("concatAudio", "concatAudio：" + stringBuilder.toString());
    try {
      CustomFFmpeg.getInstance().execute(originAudioFilePath, cmdArray,
              ffmpegExecuteResponseHandler);
    } catch (FFmpegCommandAlreadyRunningException e) {
      e.printStackTrace();
    }
  }

  @Deprecated
  public static void concatVideoList(List<String> originVideoFilePathList,
                                     String destinationAudioFilePath,
                                     FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler) {
    if (originVideoFilePathList != null && !originVideoFilePathList.isEmpty()) {
      List<String> cmdList = new ArrayList<>();
      for (String videoFilePath : originVideoFilePathList) {
        cmdList.add("-y");
        cmdList.add("-i");
        cmdList.add(videoFilePath);
        Log.e("concatVideoList", "concatVideoList:" + videoFilePath);
      }
      cmdList.add("-filter_complex");
      cmdList
              .add(
                      "[0:0] [0:1] [1:0] [1:1] [2:0] [2:1] concat=n=3:v=1:a=2 [v] [a1] [a2]");
      cmdList.add("-map");
      cmdList.add("[v]");
      cmdList.add("-map");
      cmdList.add("[a1]");
      cmdList.add("-map");
      cmdList.add("[a2]");
      cmdList.add("-c:a");
      cmdList.add("libmp3lame");
      cmdList.add("-c:v");
      cmdList.add("libx264");
      cmdList.add("-preset");
      cmdList.add("veryfast");
      cmdList.add("-pix_fmt");
      cmdList.add("yuv420p");
      cmdList.add(destinationAudioFilePath);
      String[] cmdArray = cmdList.toArray(new String[cmdList.size()]);
      try {
        CustomFFmpeg.getInstance().execute(originVideoFilePathList.get(0), cmdArray,
                ffmpegExecuteResponseHandler);
      } catch (FFmpegCommandAlreadyRunningException e) {
        e.printStackTrace();
      }
    }
  }


  public static void changeVideoSpeed(String originVideoFilePath, float speed, int startPositionMs,
                                      int durationMs, int videoRotation, String destinationVideoFilePath,
                                      FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler) {
    // ffmpeg -i input.mkv -filter_complex "[0:v]setpts=0.5*PTS[v];[0:a]atempo=2.0[a]" -map "[v]"
    // -map "[a]" output.mkv
    String[] cmd =
            new String[]{"-y", "-i", originVideoFilePath, "-filter_complex",
                    "[0:v]setpts=" + 1 / speed + "*PTS[v];[0:a]atempo=" + speed + "[a]", "-map",
                    "[v]", "-map", "[a]", "-ss",
                    DateUtils.getFfmpegDurationString(startPositionMs), "-t",
                    DateUtils.getFfmpegDurationString(durationMs), "-acodec", "libmp3lame", "-vcodec",
                    "libx264", "-preset", "ultrafast", "-pix_fmt", "yuv420p", "-metadata:s:v:0",
                    "rotate=" + videoRotation,
                    destinationVideoFilePath};
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < cmd.length; i++) {
      stringBuilder.append(cmd[i]);
    }
    Log.e("changeVideoSpeed", "changeVideoSpeed：" + stringBuilder.toString());
    try {
      CustomFFmpeg.getInstance().execute(originVideoFilePath, cmd, ffmpegExecuteResponseHandler);
    } catch (FFmpegCommandAlreadyRunningException e) {
      e.printStackTrace();
    }
  }

  public static void freezeFrame(String originVideoFilePath, int framePosition,
                                 int freezeFrameDuration,
                                 String destinationVideoFilePath,
                                 FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler) {
    // ffmpeg -i input.mkv -filter_complex "[0:v]setpts=0.5*PTS[v];[0:a]atempo=2.0[a]" -map "[v]"
    // -map "[a]" output.mkv
    String[] cmd =
            new String[]{"-y", "-i", originVideoFilePath, "-filter_complex", "select='eq(n\\,10)'",
                    "-t",
                    DateUtils.getFfmpegDurationString(20000), "-c:a", "libmp3lame", "-c:v",
                    "libx264",
                    "-preset", "veryfast", "-pix_fmt", "yuv420p",
                    destinationVideoFilePath};
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < cmd.length; i++) {
      stringBuilder.append(cmd[i]);
    }
    Log.e("saveAudioToSDCard", "addRunManAudio：" + stringBuilder.toString());
    try {
      CustomFFmpeg.getInstance().execute(originVideoFilePath, cmd, ffmpegExecuteResponseHandler);
    } catch (FFmpegCommandAlreadyRunningException e) {
      e.printStackTrace();
    }
  }

  public static void imageToVideo(String originImageFilePath, String destinationVideoFilePath,
                                  int videoDuration, String defulatMusicPath, FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler) {
    List<String> cmdList = new ArrayList<>();
    cmdList.add("-y");
    cmdList.add("-r");
    cmdList.add("1/5");
    cmdList.add("-start_number");
    cmdList.add("0");
    cmdList.add("-f");
    cmdList.add("image2");
    cmdList.add("-i");
    cmdList.add(originImageFilePath);
    if (!TextUtils.isEmpty(defulatMusicPath)) {
      cmdList.add("-i");
      cmdList.add(defulatMusicPath);
    }
    cmdList.add("-t");
    cmdList.add(DateUtils.getFfmpegDurationString(videoDuration));
    cmdList.add("-c:a");
    cmdList.add("libmp3lame");
    cmdList.add("-c:v");
    cmdList.add("libx264");
    cmdList.add("-preset");
    cmdList.add("ultrafast");
    cmdList.add("-vf");
    cmdList.add("fps=25,format=yuv420p");
    cmdList.add(destinationVideoFilePath);
    String[] cmdArray = cmdList.toArray(new String[cmdList.size()]);
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < cmdArray.length; i++) {
      stringBuilder.append(cmdArray[i]);
    }
    Log.e("imageToVideo", "imageToVideo：" + stringBuilder.toString());
    try {
      CustomFFmpeg.getInstance().execute(originImageFilePath, cmdArray,
              ffmpegExecuteResponseHandler);
    } catch (FFmpegCommandAlreadyRunningException e) {
      e.printStackTrace();
    }
  }

  public static void imageToVideo(String originImageFilePath, String destinationVideoFilePath,
                                  String defaultMusicPath,int videoDuration, float imageCount,
                                  FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler) {
    List<String> cmdList = new ArrayList<>();
    cmdList.add("-y");
    cmdList.add("-r");
    cmdList.add(imageCount + "");
    cmdList.add("-start_number");
    cmdList.add("0");
    cmdList.add("-f");
    cmdList.add("image2");
    cmdList.add("-i");
    cmdList.add(originImageFilePath);
    if (!TextUtils.isEmpty(defaultMusicPath)) {
      cmdList.add("-i");
      cmdList.add(defaultMusicPath);
    }
    cmdList.add("-t");
    cmdList.add(DateUtils.getFfmpegDurationString(videoDuration));
    cmdList.add("-c:a");
    cmdList.add("libmp3lame");
    cmdList.add("-c:v");
    cmdList.add("libx264");
    cmdList.add("-preset");
    cmdList.add("ultrafast");
    cmdList.add("-vf");
    cmdList.add("format=yuv420p");
    cmdList.add(destinationVideoFilePath);
    String[] cmdArray = cmdList.toArray(new String[cmdList.size()]);
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < cmdArray.length; i++) {
      stringBuilder.append(cmdArray[i]);
    }
    Log.e("imageToVideo", "imageToVideo：" + stringBuilder.toString());
    try {
      CustomFFmpeg.getInstance().execute(originImageFilePath, cmdArray,
              ffmpegExecuteResponseHandler);
    } catch (FFmpegCommandAlreadyRunningException e) {
      e.printStackTrace();
    }
  }

  public static void addCurvesFilter(String originVideoFilePath, FilterCurves filterCurves,
                                     String destinationVideoFilePath,
                                     FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler) {
    // curves=r='0/0.11 .42/.51 1/0.95':g='0.50/0.48':b='0/0.22 .49/.44 1/0.8'
    List<String> cmdList = new ArrayList<>();
    cmdList.add("-hwaccel");
    cmdList.add("auto");
    cmdList.add("-flags2");
    cmdList.add("fast");
    cmdList.add("-y");
    cmdList.add("-i");
    cmdList.add(originVideoFilePath);
    cmdList.add("-dct");
    cmdList.add("fastint");
    cmdList.add("-me_method");
    cmdList.add("zero");
    cmdList.add("-c:a");
    cmdList.add("copy");
    cmdList.add("-c:v");
    cmdList.add("libx264");
    cmdList.add("-coder");
    cmdList.add("vlc");
    cmdList.add("-fastfirstpass");
    cmdList.add("1");
    cmdList.add("-vf");
    cmdList.add("curves=" + filterCurves.toFFmpegExecuteString() + ",fps=30,format=yuv420p");
    cmdList.add("-x264opts");
    cmdList.add("opencl");
    cmdList.add("-preset");
    cmdList.add("ultrafast");
    cmdList.add("-tune");
    cmdList.add("zerolatency");
    cmdList.add("-thread_type");
    cmdList.add("frame");
    cmdList.add(destinationVideoFilePath);
    String[] cmdArray = cmdList.toArray(new String[cmdList.size()]);
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < cmdArray.length; i++) {
      stringBuilder.append(cmdArray[i]);
    }
    Log.e("addCurvesFilter", "addCurvesFilter：" + stringBuilder.toString());
    try {
      CustomFFmpeg.getInstance().execute(destinationVideoFilePath, cmdArray,
              ffmpegExecuteResponseHandler);
    } catch (FFmpegCommandAlreadyRunningException e) {
      e.printStackTrace();
    }
  }

  public static void addCurvesFilter(String originVideoFilePath, FilterCurves filterCurves,
                                     String destinationVideoFilePath, int startPosition, int endPosition,
                                     FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler) {
    // curves=r='0/0.11 .42/.51 1/0.95':g='0.50/0.48':b='0/0.22 .49/.44 1/0.8'
    String[] cmd =
            new String[]{"-y", "-i", originVideoFilePath, "-ss",
                    DateUtils.getFfmpegDurationString(startPosition), "-t",
                    DateUtils.getFfmpegDurationString(endPosition), "-vf", "curves=" +
                    filterCurves.toFFmpegExecuteString() + ",fps=30,format=yuv420p",
                    "-c:a", "copy", "-c:v", "libx264", "-threads", "15", "-x264opts", "opencl", "-preset",
                    "ultrafast", "-tune", "zerolatency", destinationVideoFilePath};
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < cmd.length; i++) {
      stringBuilder.append(cmd[i]);
    }
    Log.e("addCurvesFilter", "addCurvesFilter：" + stringBuilder.toString());
    try {
      CustomFFmpeg.getInstance().execute(destinationVideoFilePath, cmd,
              ffmpegExecuteResponseHandler);
    } catch (FFmpegCommandAlreadyRunningException e) {
      e.printStackTrace();
    }
  }

  public static void addBlackWhiteFilter(String originVideoFilePath,
                                         String destinationVideoFilePath, FFmpegExecuteResponseHandler ffmpegExecuteResponseHandler) {
    List<String> cmdList = new ArrayList<>();
    cmdList.add("-hwaccel");
    cmdList.add("auto");
    cmdList.add("-flags2");
    cmdList.add("fast");
    cmdList.add("-y");
    cmdList.add("-i");
    cmdList.add(originVideoFilePath);
    cmdList.add("-dct");
    cmdList.add("fastint");
    cmdList.add("-me_method");
    cmdList.add("zero");
    cmdList.add("-c:a");
    cmdList.add("copy");
    cmdList.add("-c:v");
    cmdList.add("libx264");
    cmdList.add("-coder");
    cmdList.add("vlc");
    cmdList.add("-fastfirstpass");
    cmdList.add("1");
    cmdList.add("-vf");
    cmdList.add("hue=s=0" + ",fps=30,format=yuv420p");
    cmdList.add("-x264opts");
    cmdList.add("opencl");
    cmdList.add("-preset");
    cmdList.add("ultrafast");
    cmdList.add("-tune");
    cmdList.add("zerolatency");
    cmdList.add("-thread_type");
    cmdList.add("slice");
    cmdList.add("-threads");
    cmdList.add(String.valueOf(BEST_THREAD_COUNT));
    cmdList.add(destinationVideoFilePath);
    String[] cmdArray = cmdList.toArray(new String[cmdList.size()]);
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < cmdArray.length; i++) {
      stringBuilder.append(cmdArray[i]);
    }
    Log.e("addCurvesFilter", "addCurvesFilter：" + stringBuilder.toString());
    try {
      CustomFFmpeg.getInstance().execute(originVideoFilePath, cmdArray,
              ffmpegExecuteResponseHandler);
    } catch (FFmpegCommandAlreadyRunningException e) {
      e.printStackTrace();
    }
  }
}