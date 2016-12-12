package com.biubiu.miku.util.video.action;

import com.biubiu.miku.util.video.VideoUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class RecordActionLocationTask implements Serializable {
  private static final long serialVersionUID = 121333832423833358L;
  private long createTimeMs;
  private int startPosition;
  private int endPosition;
  private Map<Integer, RecordLocation> generateLocationTaskMap;
  private Map<Integer, RecordLocation> showLocationTaskMap;
  private int oldRecordFramePosition = 0;
  private int oldShowSeekPosition = 0;
  private RecordLocation oldRecordLocation;
  private RecordLocation oldShowLocation;
  private ActionContent actionContent;

  public RecordActionLocationTask(ActionContent actionContent) {
    this.actionContent = actionContent;
    generateLocationTaskMap = new HashMap<>();
    showLocationTaskMap = new HashMap<>();
    createTimeMs = System.currentTimeMillis();
  }

  public void setEndPosition(int endPosition) {
    this.endPosition = endPosition;
    if (endPosition < startPosition) {
      endPosition = startPosition ^ endPosition;
      startPosition = startPosition ^ endPosition;
      endPosition = startPosition ^ endPosition;
    }
    this.endPosition = endPosition;
  }


  public void correctEndPosition(int offsetPosition) {
    setEndPosition(this.endPosition += offsetPosition);
  }


  public void setStartPosition(int startPosition) {
    this.startPosition = startPosition;
  }

  public void correctStartPosition(int offsetPosition) {
    this.startPosition += offsetPosition;
  }

  public void correctTimeLine(int offsetPosition) {
    correctStartPosition(offsetPosition);
    correctEndPosition(offsetPosition);
  }


  public TimeLine getTimeLine() {
    return new TimeLine(startPosition, endPosition);
  }

  public Map<Integer, RecordLocation> getGenerateLocationTaskMap() {
    return generateLocationTaskMap;
  }

  public void addRecordLocation(int seekMs, int sampleSize, int durationMs,
                                RecordLocation recordLocation) {
    if (generateLocationTaskMap != null) {
      int recordFramePosition =
          VideoUtils.videoSeekMsToSamplePosition(seekMs, sampleSize, durationMs);
      if (!generateLocationTaskMap.containsKey(recordFramePosition)) {
        generateLocationTaskMap.put(recordFramePosition, recordLocation);
        if (recordFramePosition - oldRecordFramePosition > 1
            && oldRecordLocation != null) {
          int loseFrameSize = recordFramePosition - oldRecordFramePosition;
          for (int i = 1; i < loseFrameSize; i++) {
            int x =
                oldRecordLocation.getOffsetX()
                    - (int) ((oldRecordLocation.getOffsetX() - recordLocation.getOffsetX())
                    / (float) (i + 1));
            int y =
                oldRecordLocation.getOffsetY()
                    - (int) ((oldRecordLocation.getOffsetY() - recordLocation.getOffsetY())
                        / (float) (i + 1));
            RecordLocation tempRecordLocation =
                new RecordLocation(x, y, recordLocation.getVideoActionParams());
            generateLocationTaskMap.put(recordFramePosition - i, tempRecordLocation);
          }
        }
      }
      oldRecordFramePosition = recordFramePosition;
      oldRecordLocation = recordLocation;
    }
    addShowLocation(seekMs, recordLocation);
  }

  private void addShowLocation(int playSeek, RecordLocation recordLocation) {
    if (showLocationTaskMap != null) {
      if (!showLocationTaskMap.containsKey(playSeek)) {
        showLocationTaskMap.put(playSeek, recordLocation);
        if (playSeek - oldShowSeekPosition > 1
            && oldShowLocation != null) {
          int loseFrameSize = playSeek - oldShowSeekPosition;
          for (int i = 1; i < loseFrameSize; i++) {
            int x =
                oldShowLocation.getOffsetX()
                    - (int) ((oldShowLocation.getOffsetX() - recordLocation.getOffsetX())
                    / (float) (i + 1));
            int y =
                oldShowLocation.getOffsetY()
                    - (int) ((oldShowLocation.getOffsetY() - recordLocation.getOffsetY())
                        / (float) (i + 1));
            RecordLocation tempRecordLocation =
                new RecordLocation(x, y, recordLocation.getVideoActionParams());
            showLocationTaskMap.put(playSeek - i, tempRecordLocation);
          }
        }
      }
      oldShowSeekPosition = playSeek;
      oldShowLocation = recordLocation;
    }
  }

  public Map<Integer, RecordLocation> getShowLocationTaskMap() {
    return showLocationTaskMap;
  }

  public long getCreateTimeMs() {
    return createTimeMs;
  }

  public int getStartPosition() {
    return startPosition;
  }

  public int getEndPosition() {
    return endPosition;
  }

  public ActionContent getActionContent() {
    return actionContent;
  }

  public void setActionContent(ActionContent actionContent) {
    this.actionContent = actionContent;
  }

  public void setShowLocationTaskMap(Map<Integer, RecordLocation> showLocationTaskMap) {
    this.showLocationTaskMap = showLocationTaskMap;
  }

  public void setGenerateLocationTaskMap(Map<Integer, RecordLocation> generateLocationTaskMap) {
    this.generateLocationTaskMap = generateLocationTaskMap;
  }

  public int getOldRecordFramePosition() {
    return oldRecordFramePosition;
  }
}
