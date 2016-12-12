package com.biubiu.miku.util.video.task;

import android.util.Log;

import com.annimon.stream.Stream;
import com.biubiu.miku.util.video.SeekData;
import com.biubiu.miku.util.video.VideoMetaData;
import com.biubiu.miku.util.video.VideoUtils;
import com.biubiu.miku.util.video.action.Action;
import com.biubiu.miku.util.video.action.ActionContent;
import com.biubiu.miku.util.video.action.ActionType;
import com.biubiu.miku.util.video.action.RecordActionLocationTask;
import com.biubiu.miku.util.video.action.RecordLocation;
import com.biubiu.miku.util.video.action.chatBox.ChatBox;
import com.biubiu.miku.util.video.action.chatBox.ChatBoxLocation;
import com.biubiu.miku.util.video.action.filter.FilterContent;
import com.biubiu.miku.util.video.action.filter.FilterThemeType;
import com.biubiu.miku.util.video.action.montage.Montage;
import com.biubiu.miku.util.video.action.montage.MontageContent;
import com.biubiu.miku.util.video.action.montage.MontageTimeLine;
import com.biubiu.miku.util.video.action.montage.MontageType;
import com.biubiu.miku.util.video.action.music.Music;
import com.biubiu.miku.util.video.action.music.MusicContent;
import com.biubiu.miku.util.video.action.runMan.RunMan;
import com.biubiu.miku.util.video.action.runMan.RunManLocation;
import com.biubiu.miku.util.video.action.sticker.Sticker;
import com.biubiu.miku.util.video.action.sticker.StickerLocation;
import com.biubiu.miku.util.video.action.subtitle.Subtitle;
import com.biubiu.miku.util.video.action.subtitle.SubtitleContent;
import com.biubiu.miku.util.video.action.subtitle.util.SubtitleUtils;
import com.biubiu.miku.util.video.action.videoTag.VideoTag;
import com.biubiu.miku.util.video.action.videoTag.VideoTagLocation;
import com.biubiu.miku.util.video.generator.VideoCutSheetsProcessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class VideoContentTask {
  private static long id;
  private String videoPath;
  private String recordVideoFileDirPath;
  private float videoRatioWH;
  private List<Subtitle> subtitles;
  private List<VideoTag> videoTags;
  private List<ChatBox> chatBoxs;
  private List<Sticker> stickers;
  private List<RunMan> runMans;

  private List<Action> actions;

  private List<Montage> montages;
  // private List<Action> actionLoops;
  private List<String> recordFilePathList;
  private List<Integer> recordTimeList;
  private List<RecordActionLocationTask> recordActionLocationTasks;
  private ProcessState videoCropProcessState = ProcessState.IDLE;
  private ProcessState videoCutSheetsProcessState = ProcessState.FAILURE;
  private String processVideoFilePath;
  private String filterVideoFilePath;
  private List<OnVideoCropStateChangeListener> onVideoCropStateChangeListenerList;
  private List<OnVideoCutSheetsStateChangeListener> onVideoCutSheetsStateChangeListenerList;
  private Music music;
  private RecordActionLocationTask musicRecordActionLocationTask;
  private RecordActionLocationTask filterRecordActionLocationTask;
  private FilterThemeType filterThemeType = FilterThemeType.ORIGIN;
  private VideoMetaData videoMetaData;
  private int sampleSize = 1;
  private int durationMs = 1;
  private List<String> videoSheetFilePathList;

  protected VideoContentTask() {
    //actionLoops = new ArrayList<>();
    subtitles = new ArrayList<>();
    videoTags = new ArrayList<>();
    chatBoxs = new ArrayList<>();
    stickers = new ArrayList<>();
    runMans = new ArrayList<>();
    montages = new ArrayList<>();
    actions = new ArrayList<>();
    recordActionLocationTasks = new ArrayList<>();
    onVideoCropStateChangeListenerList = new ArrayList<>();
    onVideoCutSheetsStateChangeListenerList = new ArrayList<>();
    id = System.currentTimeMillis();
  }

  public VideoContentTask(String videoPath,
                          List<RecordActionLocationTask> recordActionLocationTasks,
                          ProcessState videoCropProcessState, String processVideoFilePath,
                          float videoRatioWH, VideoMetaData videoMetaData, int sampleSize) {
    this.videoPath = videoPath;
    this.recordActionLocationTasks = recordActionLocationTasks;
    this.processVideoFilePath = processVideoFilePath;
    this.videoCropProcessState = videoCropProcessState;
    this.videoMetaData = videoMetaData;
    this.sampleSize = sampleSize;
    this.videoRatioWH = videoRatioWH;
  }

  public void clear() {
    subtitles.clear();
    onVideoCropStateChangeListenerList.clear();
    onVideoCropStateChangeListenerList = null;
    onVideoCutSheetsStateChangeListenerList.clear();
    onVideoCutSheetsStateChangeListenerList = null;
    videoCropProcessState = ProcessState.IDLE;
    music = null;
    processVideoFilePath = null;
    videoPath = null;
    videoRatioWH = 0;
    filterThemeType = null;
  }

  public void setVideoCropState(ProcessState processState, String cropVideoFilePath) {
    this.videoCropProcessState = processState;
    this.processVideoFilePath = cropVideoFilePath;
    for (OnVideoCropStateChangeListener onVideoCropStateChangeListener : onVideoCropStateChangeListenerList) {
      if (onVideoCropStateChangeListener != null) {
        onVideoCropStateChangeListener.onVideoCropStateChange(processState, cropVideoFilePath);
      }
    }
  }

  public void setVideoCropState(ProcessState processState) {
    this.videoCropProcessState = processState;
  }

  public void setOnVideoCutSheetsState(ProcessState processState,
                                       List<String> videoSheetFilePathList) {
    this.videoCutSheetsProcessState = processState;
    this.videoSheetFilePathList = videoSheetFilePathList;
    Stream.of(onVideoCutSheetsStateChangeListenerList)
            .forEach(value -> value.onVideoCutSheetsStateChange(videoCutSheetsProcessState,
                    videoSheetFilePathList));
  }

  public void setOnVideoCutSheetsState(ProcessState processState) {
    this.videoCutSheetsProcessState = processState;
  }

  public enum ProcessState {
    IDLE, SUCCESS, PROGRESS, FAILURE
  }

  public interface OnVideoCropStateChangeListener {
    void onVideoCropStateChange(ProcessState processState, String cropVideoFilePath);
  }

  public interface OnVideoCutSheetsStateChangeListener {
    void onVideoCutSheetsStateChange(ProcessState processState,
                                     List<String> videoSheetFilePathList);
  }

  public void addOnVideoCutSheetsStateChangeListener(
          OnVideoCutSheetsStateChangeListener onVideoCutSheetsStateChangeListener) {
    onVideoCutSheetsStateChangeListenerList.add(onVideoCutSheetsStateChangeListener);
  }

  public void removeOnVideoCutSheetsStateChangeListener(
          OnVideoCutSheetsStateChangeListener onVideoCutSheetsStateChangeListener) {
    onVideoCutSheetsStateChangeListenerList.remove(onVideoCutSheetsStateChangeListener);
  }

  public void clearOnVideoCutSheetsStateChangeListener() {
    onVideoCutSheetsStateChangeListenerList.clear();
  }

  public void addOnVideoCropStateChangeListener(
          OnVideoCropStateChangeListener onVideoCropStateChangeListener) {
    onVideoCropStateChangeListenerList.add(onVideoCropStateChangeListener);
  }

  public void removeOnVideoCropStateChangeListener(
          OnVideoCropStateChangeListener onVideoCropStateChangeListener) {
    onVideoCropStateChangeListenerList.remove(onVideoCropStateChangeListener);
  }

  public void clearOnVideoCropStateChangeListener() {
    onVideoCropStateChangeListenerList.clear();
  }

  public String getProcessVideoFilePath() {
    return processVideoFilePath;
  }

  public ProcessState getVideoCropProcessState() {
    return videoCropProcessState;
  }

  public Music getMusic() {
    return music;
  }

  public void setMusic(Music music) {
    this.music = music;
    if (musicRecordActionLocationTask != null) {
      recordActionLocationTasks.remove(musicRecordActionLocationTask);
    }
    musicRecordActionLocationTask = new RecordActionLocationTask(new MusicContent(music));
    if (recordActionLocationTasks.isEmpty()) {
      musicRecordActionLocationTask.setStartPosition(0);
    } else {
      musicRecordActionLocationTask.setStartPosition(
              recordActionLocationTasks.get(recordActionLocationTasks.size() - 1).getStartPosition());
    }
    recordActionLocationTasks.add(musicRecordActionLocationTask);
  }

  public int getSampleSize() {
    return sampleSize;
  }

  public String getVideoPath() {
    return videoPath;
  }

  public void setVideoPath(String videoPath) {
    Log.e("luis", videoPath);
    this.videoPath = videoPath;
    this.processVideoFilePath = videoPath;
    durationMs = VideoUtils.getMediaDuration(videoPath);
    sampleSize = VideoUtils.getVideoSamplesSize(videoPath);
  }

  public String getRecordVideoFileDirPath() {
    return recordVideoFileDirPath;
  }

  public void setRecordVideoFileDirPath(String recordVideoFileDirPath) {
    this.recordVideoFileDirPath = recordVideoFileDirPath;
  }

  public List<String> getRecordFilePathList() {
    return recordFilePathList;
  }

  public void setRecordFilePathList(List<String> recordFilePathList) {
    this.recordFilePathList = recordFilePathList;
  }

  public List<Integer> getRecordTimeList() {
    return recordTimeList;
  }

  public void setRecordTimeList(List<Integer> recordTimeList) {
    this.recordTimeList = recordTimeList;
  }

  public float getVideoRatioWH() {
    return videoRatioWH;
  }

  public void setVideoRatioWH(float videoRatioWH) {
    this.videoRatioWH = videoRatioWH;
  }

  public long getId() {
    return id;
  }


  public void setFilterThemeType(FilterThemeType filterThemeType) {
    this.filterThemeType = filterThemeType;
    if (filterRecordActionLocationTask != null) {
      recordActionLocationTasks.remove(filterRecordActionLocationTask);
    }
    filterRecordActionLocationTask =
            new RecordActionLocationTask(new FilterContent(filterThemeType));
    if (recordActionLocationTasks.isEmpty()) {
      filterRecordActionLocationTask.setStartPosition(0);
    } else {
      filterRecordActionLocationTask.setStartPosition(
              recordActionLocationTasks.get(recordActionLocationTasks.size() - 1).getStartPosition());
    }
    recordActionLocationTasks.add(filterRecordActionLocationTask);
  }

  public FilterThemeType getFilterThemeType() {
    return filterThemeType;
  }

  public void setProcessVideoFilePath(String processVideoFilePath) {
    this.processVideoFilePath = processVideoFilePath;
  }

  public void setFilterVideoFilePath(String filterVideoFilePath) {
    this.filterVideoFilePath = filterVideoFilePath;
  }

  public String getFilterVideoFilePath() {
    return filterVideoFilePath;
  }

  public List<SubtitleContent> matchSubtitle(int playSeek, Subtitle subtitle) {
    return SubtitleUtils.matchSubtitle(subtitles, subtitle, playSeek);
  }

  public List<VideoTagLocation> matchVideoTag(int playSeek, VideoTag videoTag) {
    List<VideoTagLocation> matchVideoTagLocationList = new ArrayList<>();
    if (videoTag != null) {
      matchVideoTagLocationList.addAll(videoTag.matchRecordLocation(playSeek));
    }
    for (VideoTag tempVideoTag : videoTags) {
      matchVideoTagLocationList.addAll(tempVideoTag.matchRecordLocation(playSeek));
    }
    return matchVideoTagLocationList;
  }

  public List<ChatBoxLocation> matchChatBox(int playSeek, ChatBox chatBox) {
    List<ChatBoxLocation> matchChatBoxLocationArrayList = new ArrayList<>();
    if (chatBox != null) {
      matchChatBoxLocationArrayList.addAll(chatBox.matchRecordLocation(playSeek));
    }
    for (ChatBox tempChatBox : chatBoxs) {
      matchChatBoxLocationArrayList.addAll(tempChatBox.matchRecordLocation(playSeek));
    }
    return matchChatBoxLocationArrayList;
  }

  public List<StickerLocation> matchSticker(int playSeek, Sticker sticker) {
    List<StickerLocation> matchStickerLocationList = new ArrayList<>();
    if (sticker != null) {
      matchStickerLocationList.addAll(sticker.matchRecordLocation(playSeek));
    }
    for (Sticker tempSticker : stickers) {
      matchStickerLocationList.addAll(tempSticker.matchRecordLocation(playSeek));
    }
    return matchStickerLocationList;
  }

  public MontageTimeLine matchMontage(int playSeek, Montage montage) {
    if (montage != null) {
      MontageTimeLine tempMatchMontageTimeLine = montage.matchMontageTimeLine(playSeek);
      if (tempMatchMontageTimeLine != null) {
        return tempMatchMontageTimeLine;
      }
    }
    for (Montage tmpMontage : montages) {
      MontageTimeLine tempMatchMontageTimeLine = tmpMontage.matchMontageTimeLine(playSeek);
      if (tempMatchMontageTimeLine != null) {
        return tempMatchMontageTimeLine;
      }
    }
    return null;
  }

  public List<RunManLocation> matchRunMan(int playSeek, RunMan runMan) {
    List<RunManLocation> matchRunManLocationList = new ArrayList<>();
    if (runMan != null) {
      matchRunManLocationList.addAll(runMan.matchRecordLocation(playSeek));
    }
    for (RunMan tempRunMan : runMans) {
      matchRunManLocationList.addAll(tempRunMan.matchRecordLocation(playSeek));
    }
    return matchRunManLocationList;
  }

  public List<VideoTagLocation> matchVideoTag(int playSeek) {
    return matchVideoTag(playSeek, null);
  }

  public List<Sticker> getStickers() {
    return stickers;
  }

  public List<ChatBox> getChatBoxs() {
    return chatBoxs;
  }

  public List<VideoTag> getVideoTags() {
    return videoTags;
  }

  public List<RunMan> getRunMans() {
    return runMans;
  }

  public List<Montage> getMontages() {
    return montages;
  }

  public List<Subtitle> getSubtitles() {
    return subtitles;
  }

  public void setVideoMetaData(VideoMetaData videoMetaData) {
    this.videoMetaData = videoMetaData;
  }

  public VideoMetaData getVideoMetaData() {
    return videoMetaData;
  }

  public void addVideoTag(VideoTag videoTag) {
    videoTags.add(videoTag);
    //actionLoops.add(videoTag);
    actions.add(videoTag);
  }

  public void addChatBox(ChatBox chatBox) {
    chatBoxs.add(chatBox);
    //actionLoops.add(chatBox);
    actions.add(chatBox);
  }

  public void addSticker(Sticker sticker) {
    stickers.add(sticker);
    //actionLoops.add(sticker);
    actions.add(sticker);
  }

  public void addRunMan(RunMan runMan) {
    runMans.add(runMan);
    // actionLoops.add(runMan);
    actions.add(runMan);
  }

  public void addSubtitle(Subtitle subtitle) {
    subtitles.add(subtitle);
    //actionLoops.add(subtitle);
    actions.add(subtitle);
  }

  public void addMontage(Montage montage) {
    montages.add(montage);
    actions.add(montage);
  }

  public List<Action> getActions() {
    return actions;
  }

  public void removeRecordActionLocationTask(RecordActionLocationTask recordActionLocationTask,
                                             boolean isMontage) {
    if (recordActionLocationTasks != null) {
      recordActionLocationTasks.remove(recordActionLocationTask);
    }
    if (isMontage) {
      Iterator<RecordActionLocationTask> recordActionLocationTaskIterator =
              recordActionLocationTasks.iterator();
      MontageContent montageContent =
              (MontageContent) recordActionLocationTask.getActionContent();
      while (recordActionLocationTaskIterator.hasNext()) {
        RecordActionLocationTask oldRecordActionLocationTask =
                recordActionLocationTaskIterator.next();
        if (oldRecordActionLocationTask != recordActionLocationTask) {
          if (oldRecordActionLocationTask.getActionContent()
                  .getActionType() == ActionType.MONTAGE) {
            MontageContent oldMontageContent =
                    (MontageContent) oldRecordActionLocationTask.getActionContent();
            if (oldRecordActionLocationTask.getStartPosition() > recordActionLocationTask
                    .getStartPosition()) {
              // 纠正后面特效时间
              boolean isIntersect =
                      oldRecordActionLocationTask.getStartPosition() < recordActionLocationTask
                              .getEndPosition();
              int montageTaskAdditionalMs =
                      -getMontageTaskAdditionalMs(recordActionLocationTask);
              correctAfterMontageAction(oldRecordActionLocationTask,
                      recordActionLocationTask,
                      montageTaskAdditionalMs, isIntersect,
                      oldMontageContent.getMontageType(),
                      oldMontageContent.getMontageType());
            } else if (oldRecordActionLocationTask.getEndPosition() > recordActionLocationTask
                    .getStartPosition()) {
              // 纠正时间线之前有重复的特效

            }
          } else {
            // 对普通动作进行时间纠正
            if (recordActionLocationTask.getStartPosition() < oldRecordActionLocationTask
                    .getStartPosition()) {
              boolean isIntersect =
                      oldRecordActionLocationTask.getStartPosition() < recordActionLocationTask
                              .getEndPosition();
              int montageTaskAdditionalMs =
                      -getMontageTaskAdditionalMs(recordActionLocationTask);
              correctAfterAction(oldRecordActionLocationTask, recordActionLocationTask,
                      montageTaskAdditionalMs, isIntersect, montageContent.getMontageType());
            }
          }
        }
      }
    }
  }

  public void clearActionLocationTask(Action action,
                                      boolean isMontage) {
    if (action != null) {
      List<RecordActionLocationTask> recordActionLocationTasks =
              action.getRecordActionLocationTasks();
      if (recordActionLocationTasks != null && !recordActionLocationTasks.isEmpty()) {
        for (RecordActionLocationTask recordActionLocationTask : recordActionLocationTasks) {
          removeRecordActionLocationTask(recordActionLocationTask, isMontage);
        }
      }
    }
  }

  public void addNormalActionLocationTask(
          RecordActionLocationTask normalRecordActionLocationTask) {
    // 普通动作添加，在所有show position与generate position都是正确的情况下不需要纠正，直接使用生成的seek值作为position
    addRecordActionLocationTask(normalRecordActionLocationTask, false);
    // Iterator<RecordActionLocationTask> recordActionLocationTaskIterator =
    // recordActionLocationTasks.iterator();
    // while (recordActionLocationTaskIterator.hasNext()) {
    // RecordActionLocationTask oldRecordActionLocationTask =
    // recordActionLocationTaskIterator.next();
    //
    // }
  }

  public void addMontageActionLocationTask(
          RecordActionLocationTask recordActionLocationTask) {
    // 先将时间线之后全部的动作时间线纠正,因为时间线是根据seek值设定，所以无需校正当前时间线
    addRecordActionLocationTask(recordActionLocationTask, true);
  }

  public void addRecordActionLocationTask(
          RecordActionLocationTask recordActionLocationTask, boolean isMontage) {
    // 先将时间线之后全部的动作时间线纠正,因为时间线是根据seek值设定，所以无需校正当前时间线
    recordActionLocationTasks.add(recordActionLocationTask);
    sortActionLocationTaskList();
    if (isMontage) {
      MontageContent montageContent =
              (MontageContent) recordActionLocationTask.getActionContent();
      Iterator<RecordActionLocationTask> recordActionLocationTaskIterator =
              recordActionLocationTasks.iterator();
      while (recordActionLocationTaskIterator.hasNext()) {
        RecordActionLocationTask oldRecordActionLocationTask =
                recordActionLocationTaskIterator.next();
        if (oldRecordActionLocationTask != recordActionLocationTask) {
          if (oldRecordActionLocationTask.getActionContent()
                  .getActionType() == ActionType.MONTAGE) {
            MontageContent oldMontageContent =
                    (MontageContent) oldRecordActionLocationTask.getActionContent();
            if (oldRecordActionLocationTask.getStartPosition() > recordActionLocationTask
                    .getStartPosition()) {
              // 纠正后面特效时间
              boolean isIntersect =
                      oldRecordActionLocationTask.getStartPosition() < recordActionLocationTask
                              .getEndPosition();
              int montageTaskAdditionalMs =
                      getMontageTaskAdditionalMs(recordActionLocationTask);
              correctAfterMontageAction(oldRecordActionLocationTask,
                      recordActionLocationTask,
                      montageTaskAdditionalMs, isIntersect, oldMontageContent.getMontageType(),
                      oldMontageContent.getMontageType());
            } else if (oldRecordActionLocationTask.getEndPosition() > recordActionLocationTask
                    .getStartPosition()) {
              // 纠正时间线之前有重复的特效

            }
          } else {
            // 对普通动作进行时间纠正
            if (recordActionLocationTask.getStartPosition() < oldRecordActionLocationTask
                    .getStartPosition()) {
              boolean isIntersect =
                      oldRecordActionLocationTask.getStartPosition() < recordActionLocationTask
                              .getEndPosition();
              int montageTaskAdditionalMs =
                      getMontageTaskAdditionalMs(recordActionLocationTask);
              correctAfterAction(oldRecordActionLocationTask, recordActionLocationTask,
                      montageTaskAdditionalMs, isIntersect, montageContent.getMontageType());
            }
          }
        }
      }
    }
  }

  private int getMontageTaskAdditionalMs(RecordActionLocationTask montageRecordActionLocationTask) {
    switch (((MontageContent) montageRecordActionLocationTask.getActionContent())
            .getMontageType()) {
      case FREEZE:
        return montageRecordActionLocationTask.getTimeLine().getDuration();
      case REPEAT:
        return montageRecordActionLocationTask.getTimeLine().getDuration() * 2;
      case FORWARD:
        return -(int) (montageRecordActionLocationTask.getTimeLine().getDuration()
                * MontageType.FORWARD.getDurationProportion());
      case SLOW_MOTION:
        return montageRecordActionLocationTask.getTimeLine().getDuration();
      default:
        return 0;
    }
  }

  private void correctAfterAction(RecordActionLocationTask recordActionLocationTask,
                                  RecordActionLocationTask montageRecordActionLocationTask, int offsetPosition,
                                  boolean isIntersect, MontageType currentMontageType) {
    if (isIntersect) {
      // 有交集位置需要修改生成坐标集合与显示坐标集合
      switch (currentMontageType) {
        case FORWARD:
          recordActionLocationTask.correctTimeLine(offsetPosition);
          break;
        case FREEZE:
          recordActionLocationTask.correctTimeLine(offsetPosition);
          break;
        case SLOW_MOTION:
          recordActionLocationTask.correctTimeLine(offsetPosition);
          // 被完全包含，则将两者顺序调整，先处理之前的,处理完加上特效
          recordActionLocationTasks.remove(recordActionLocationTask);
          recordActionLocationTasks.add(
                  recordActionLocationTasks.indexOf(montageRecordActionLocationTask),
                  recordActionLocationTask);
          if (recordActionLocationTask.getEndPosition() < montageRecordActionLocationTask
                  .getEndPosition()) {

          } else {

          }
          break;
        case REPEAT:
          break;
      }
    } else {
      // 在新加特效之后，并且完全无交集情况，直接根据特效类型，对动作时间线改变，需要修改生成坐标集合与显示坐标集合
      recordActionLocationTask.correctTimeLine(offsetPosition);
      replaceGenerateLocationTaskMap(recordActionLocationTask, offsetPosition);
      replaceAfterShowLocationTaskMap(recordActionLocationTask, offsetPosition);
    }
  }

  private void correctAfterMontageAction(RecordActionLocationTask recordActionLocationTask,
                                         RecordActionLocationTask montageRecordActionLocationTask, int offsetPosition,
                                         boolean isIntersect, MontageType correctMontageType, MontageType currentMontageType) {
    if (isIntersect) {
      // 如果特效过程时间内，特效出现重复，保留新加特效的时间
      recordActionLocationTasks.remove(recordActionLocationTask);
      recordActionLocationTasks.add(
              recordActionLocationTasks.indexOf(montageRecordActionLocationTask),
              recordActionLocationTask);
    } else {
      // 对时间线之后特效时间有修改的情况下需要再次对照被修改特效，对之后影响时间的动作进行修改
      recordActionLocationTask.correctTimeLine(offsetPosition);
      replaceGenerateLocationTaskMap(recordActionLocationTask, offsetPosition);
      replaceAfterShowLocationTaskMap(recordActionLocationTask, offsetPosition);
    }
  }


  private void replaceGenerateLocationTaskMap(RecordActionLocationTask recordActionLocationTask,
                                              int offsetPosition) {
    Map<Integer, RecordLocation> recordLocationTaskMap =
            recordActionLocationTask.getGenerateLocationTaskMap();
    Map<Integer, RecordLocation> tempRecordLocationTaskMap = new HashMap<>();
    Iterator<Map.Entry<Integer, RecordLocation>> recordLocationTaskMapIterator =
            recordLocationTaskMap.entrySet().iterator();
    while (recordLocationTaskMapIterator.hasNext()) {
      Map.Entry<Integer, RecordLocation> entry = recordLocationTaskMapIterator.next();
      int newPosition = entry.getKey()
              + VideoUtils.videoSeekMsToSamplePosition(offsetPosition, sampleSize, durationMs);
      tempRecordLocationTaskMap.put(newPosition, entry.getValue());
    }
    recordActionLocationTask.setGenerateLocationTaskMap(tempRecordLocationTaskMap);
  }

  private void replaceAfterShowLocationTaskMap(RecordActionLocationTask recordActionLocationTask,
                                               int offsetPosition) {
    Map<Integer, RecordLocation> recordLocationTaskMap =
            recordActionLocationTask.getShowLocationTaskMap();
    Map<Integer, RecordLocation> tempShowLocationTaskMap = new HashMap<>();
    Iterator<Map.Entry<Integer, RecordLocation>> recordLocationTaskMapIterator =
            recordLocationTaskMap.entrySet().iterator();
    while (recordLocationTaskMapIterator.hasNext()) {
      Map.Entry<Integer, RecordLocation> entry = recordLocationTaskMapIterator.next();
      Log.e("replace",
              "position:" + entry.getKey() + "   new Position:" + entry.getKey() + offsetPosition
                      + "  durationMs:" + durationMs + "   sampleSize:" + sampleSize + "   offsetPosition:"
                      + offsetPosition);
      tempShowLocationTaskMap.put(entry.getKey() + offsetPosition, entry.getValue());
    }
    recordActionLocationTask.setShowLocationTaskMap(tempShowLocationTaskMap);
  }

  private void replaceIntersectShowLocationTaskMap(
          RecordActionLocationTask recordActionLocationTask,
          RecordActionLocationTask montageRecordActionLocationTask, int offsetPosition) {
    // Map<Integer, RecordLocation> recordLocationTaskMap =
    // recordActionLocationTask.getShowLocationTaskMap();
    // Map<Integer, RecordLocation> tempShowLocationTaskMap = new HashMap<>();
    // Iterator<Map.Entry<Integer, RecordLocation>> recordLocationTaskMapIterator =
    // recordLocationTaskMap.entrySet().iterator();
    // while (recordLocationTaskMapIterator.hasNext()) {
    // Map.Entry<Integer, RecordLocation> entry = recordLocationTaskMapIterator.next();
    // tempShowLocationTaskMap.put(
    // entry.getKey()
    // + offsetPosition,
    // entry.getValue());
    // }
    // recordActionLocationTask.setGenerateLocationTaskMap(tempShowLocationTaskMap);
  }

  public int getMontageAdditionalTimeMs(Montage currentMontage) {
    int montageAdditionalTimeMs = 0;
    for (Action action : actions) {
      if (action instanceof Montage) {
        montageAdditionalTimeMs =
                additionalMontageTimeMs(montageAdditionalTimeMs, (Montage) action);
      }
    }
    if (currentMontage != null) {
      montageAdditionalTimeMs = additionalMontageTimeMs(montageAdditionalTimeMs, currentMontage);
    }
    return montageAdditionalTimeMs;
  }


  public SeekData getSeekPosition(int position) {
    int offsetPosition = 0;
    sortActionLocationTaskList();
    for (RecordActionLocationTask recordActionLocationTask : recordActionLocationTasks) {
      ActionContent actionContent = recordActionLocationTask.getActionContent();
      if (actionContent.getActionType() == ActionType.MONTAGE) {
        MontageContent montageContent = (MontageContent) actionContent;
        int duration;
        int startTime;
        switch (montageContent.getMontageType()) {
          case REPEAT:
            startTime = recordActionLocationTask.getStartPosition() + offsetPosition;
            duration = recordActionLocationTask.getTimeLine().getDuration() * 3;
            if (position < startTime || position > startTime + duration) {
              offsetPosition += recordActionLocationTask.getTimeLine().getDuration() * 2;
            } else {
              return new SeekData(position, startTime
                      + ((position - startTime) % recordActionLocationTask.getTimeLine().getDuration()),
                      offsetPosition, actionContent, recordActionLocationTask);
            }
            break;
          case FREEZE:
            startTime = recordActionLocationTask.getStartPosition() + offsetPosition;
            duration = recordActionLocationTask.getTimeLine().getDuration();
            if (position < startTime || position > startTime + duration) {
              offsetPosition += recordActionLocationTask.getTimeLine().getDuration();
            } else {
              return new SeekData(position, startTime,
                      offsetPosition, actionContent, recordActionLocationTask);
            }
            break;
          case FORWARD:
            startTime = recordActionLocationTask.getStartPosition() + offsetPosition;
            duration = (int) (recordActionLocationTask.getTimeLine().getDuration()
                    * MontageType.FORWARD.getDurationProportion());
            if (position < startTime || position > startTime + duration) {
              offsetPosition -= (int) (recordActionLocationTask.getTimeLine().getDuration()
                      * MontageType.FORWARD.getDurationProportion());
            } else {
              return new SeekData(position, startTime + ((position - startTime) * 2),
                      offsetPosition, actionContent,
                      recordActionLocationTask);
            }
            break;
          case SLOW_MOTION:
            startTime = recordActionLocationTask.getStartPosition() + offsetPosition;
            duration = (int) (recordActionLocationTask.getTimeLine().getDuration()
                    * MontageType.SLOW_MOTION.getDurationProportion());
            if (position < startTime || position > startTime + duration) {
              offsetPosition += recordActionLocationTask.getTimeLine().getDuration();
            } else {
              return new SeekData(position, startTime + ((position - startTime) % 2),
                      offsetPosition, actionContent,
                      recordActionLocationTask);
            }
            break;
        }
      }
    }
    // Log.e("seekTo", "offsetPosition:" + offsetPosition);
    return new SeekData(position, position - offsetPosition,
            offsetPosition, null, null);
  }


  private int additionalMontageTimeMs(int montageAdditionalTimeMs, Montage montage) {
    List<RecordActionLocationTask> recordActionLocationTaskList =
            montage.sortRecordActionLocationTaskList();
    for (RecordActionLocationTask recordActionLocationTask : recordActionLocationTaskList) {
      switch (montage.getMontageType()) {
        case REPEAT:
          montageAdditionalTimeMs += recordActionLocationTask.getTimeLine().getDuration() * 2;
          break;
        case FREEZE:
          montageAdditionalTimeMs += recordActionLocationTask.getTimeLine().getDuration();
          break;
        case FORWARD:
          montageAdditionalTimeMs -= (int) (recordActionLocationTask.getTimeLine().getDuration()
                  * MontageType.FORWARD.getDurationProportion());
          break;
        case SLOW_MOTION:
          montageAdditionalTimeMs += recordActionLocationTask.getTimeLine().getDuration();
          break;
      }
    }
    return montageAdditionalTimeMs;
  }


  public List<RecordActionLocationTask> sortActionLocationTaskList() {
    Collections.sort(recordActionLocationTasks,
            (lhs, rhs) -> lhs.getStartPosition() - rhs.getStartPosition());
    return recordActionLocationTasks;
  }

  public List<RecordActionLocationTask> getRecordActionLocationTasks() {
    return recordActionLocationTasks;
  }

  public void setVideoSheetFilePathList(List<String> videoSheetFilePathList) {
    this.videoSheetFilePathList = videoSheetFilePathList;
  }

  public List<String> getVideoSheetFilePathList() {
    return videoSheetFilePathList;
  }

  public void cutSheets() {
    setOnVideoCutSheetsState(ProcessState.PROGRESS, null);
    VideoCutSheetsProcessor.process(processVideoFilePath,
            new VideoCutSheetsProcessor.VideoCutSheetsProcessCallback() {
              @Override
              public void success(List<String> videoSheetFilePathList) {
                setOnVideoCutSheetsState(ProcessState.SUCCESS, videoSheetFilePathList);
              }

              @Override
              public void failure(String message) {
                setOnVideoCutSheetsState(ProcessState.SUCCESS, null);
              }
            });
  }

  public ProcessState getVideoCutSheetsProcessState() {
    return videoCutSheetsProcessState;
  }

  public Action removeLatestAction() {
    Action action = null;
    if (actions != null && actions.size() > 0) {
      action = actions.remove(actions.size() - 1);
      if (action instanceof RunMan) {
        runMans.remove(action);
      } else if (action instanceof Subtitle) {
        subtitles.remove(action);
      } else if (action instanceof ChatBox) {
        chatBoxs.remove(action);
      } else if (action instanceof VideoTag) {
        videoTags.remove(action);
      } else if (action instanceof Sticker) {
        stickers.remove(action);
      } else if (action instanceof Montage) {
        montages.remove(action);
      }
    }
    final Action temp = action;
    RecordActionLocationTask recordActionLocationTask
            = Stream.of(recordActionLocationTasks).filter(tempTask -> tempTask.getActionContent()
            .equals(temp.getActionContent())).findFirst().get();
    recordActionLocationTasks.remove(recordActionLocationTask);
    return action;
  }
}
