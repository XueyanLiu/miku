package com.biubiu.miku.util.video;

import com.biubiu.miku.util.video.action.ActionContent;
import com.biubiu.miku.util.video.action.RecordActionLocationTask;

public class SeekData {
  private int oldPosition;
  private int newPosition;
  private int offsetPosition;
  private ActionContent actionContent;
  private RecordActionLocationTask recordActionLocationTask;

  public SeekData(int oldPosition, int newPosition, int offsetPosition, ActionContent actionContent,
      RecordActionLocationTask recordActionLocationTask) {
    this.oldPosition = oldPosition;
    this.newPosition = newPosition;
    this.offsetPosition = offsetPosition;
    this.actionContent = actionContent;
    this.recordActionLocationTask = recordActionLocationTask;
  }

  public int getOldPosition() {
    return oldPosition;
  }

  public int getNewPosition() {
    return newPosition;
  }

  public int getOffsetPosition() {
    return offsetPosition;
  }

  public ActionContent getActionContent() {
    return actionContent;
  }

  public RecordActionLocationTask getRecordActionLocationTask() {
    return recordActionLocationTask;
  }
}
