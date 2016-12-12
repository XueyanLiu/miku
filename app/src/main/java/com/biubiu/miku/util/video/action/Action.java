package com.biubiu.miku.util.video.action;

import com.biubiu.miku.util.video.action.chatBox.ChatBox;
import com.biubiu.miku.util.video.action.montage.Montage;
import com.biubiu.miku.util.video.action.sticker.Sticker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

public abstract class Action {
  protected RecordActionLocationTask recordActionLocationTask;
  protected List<RecordActionLocationTask> recordActionLocationTasks = new ArrayList<>();

  public void addRecordLocation(int seekMs, int sampleSize, int durationMs,
      RecordLocation recordLocation) {
    if (recordActionLocationTask != null) {
      recordActionLocationTask
          .addRecordLocation(seekMs, sampleSize, durationMs, recordLocation);
    }
  }

  public void newRecordLocationTask(int playSeekPosition) {
    recordActionLocationTask = null;
    recordActionLocationTask = new RecordActionLocationTask(this.getActionContent());
    recordActionLocationTask.setStartPosition(playSeekPosition);
  }

  public void saveRecordLocationTask(int playSeekPosition) {
    if (recordActionLocationTask != null) {
      recordActionLocationTask.setEndPosition(playSeekPosition);
      recordActionLocationTasks.add(recordActionLocationTask);
    }
  }

  public void saveDefaultRecordLocationTask(int playSeekPosition) {
    if (recordActionLocationTask != null) {
      recordActionLocationTask.setEndPosition(playSeekPosition);
      recordActionLocationTasks.add(recordActionLocationTask);
    }
  }

  public RecordActionLocationTask getCurrentRecordLocationTask() {
    return recordActionLocationTask;
  }

  public int getLocationSize() {
    int size = 0;
    for (RecordActionLocationTask recordLocationTask : recordActionLocationTasks) {
      size += recordLocationTask.getGenerateLocationTaskMap().size();
    }
    return size;
  }

  public List<RecordActionLocationTask> getRecordActionLocationTasks() {
    return recordActionLocationTasks;
  }

  public void removeVideoTagLocationTask(RecordActionLocationTask recordActionLocationTask) {
    ListIterator<RecordActionLocationTask> listIterator = recordActionLocationTasks.listIterator();
    while (listIterator.hasNext()) {
      if (listIterator.next().getCreateTimeMs() == recordActionLocationTask
          .getCreateTimeMs()) {
        listIterator.remove();
        break;
      }
    }
  }

  public List<RecordActionLocationTask> sortRecordActionLocationTaskList() {
    Collections.sort(recordActionLocationTasks,
        (lhs, rhs) -> lhs.getStartPosition() - rhs.getStartPosition());
    return recordActionLocationTasks;
  }

  public ActionType getType() {
    if (getActionContent() != null) {
      return getActionContent().getActionType();
    } else {
      if (this instanceof Montage) {
        return ActionType.MONTAGE;
      } else if (this instanceof ChatBox) {
        return ActionType.CHAT_BOX;
      } else if (this instanceof Sticker) {
        return ActionType.STICKER;
      }
      return null;
    }
  }

  public abstract ActionContent getActionContent();
}
