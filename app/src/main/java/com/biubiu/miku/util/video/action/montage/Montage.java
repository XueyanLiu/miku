package com.biubiu.miku.util.video.action.montage;

import android.util.Log;

import com.biubiu.miku.util.video.action.Action;
import com.biubiu.miku.util.video.action.ActionContent;
import com.biubiu.miku.util.video.action.RecordActionLocationTask;


public class Montage extends Action {
  private MontageContent montageContent;

  public Montage(MontageType montageActionType) {
    montageContent = new MontageContent(montageActionType);
  }

  public MontageTimeLine matchMontageTimeLine(int recordTimePositionMs) {
    for (RecordActionLocationTask recordLocationTask : recordActionLocationTasks) {
      if (recordLocationTask.getStartPosition() - recordTimePositionMs < 200
          && recordTimePositionMs - recordLocationTask.getStartPosition() < 200) {
        Log.e("matchMontage", "currentPosition ：" + recordTimePositionMs
            + " recordLocationTask.getStartPosition():" + recordLocationTask.getStartPosition());
      }
      if (recordLocationTask.getStartPosition() - recordTimePositionMs < 10
          && recordTimePositionMs - recordLocationTask.getStartPosition() < 10) {
        // if (recordLocationTask.getStartPosition() == recordTimePositionMs) {
        return new MontageTimeLine(recordLocationTask.getTimeLine(), getMontageType());
      }
    }
    return null;
  }

  public int getLocationSize() {
    int size = 0;
    for (RecordActionLocationTask recordLocationTask : recordActionLocationTasks) {
      size += recordLocationTask.getGenerateLocationTaskMap().size();
    }
    return size;
  }

  public MontageType getMontageType() {
    return montageContent.getMontageType();
  }

  @Override
  public ActionContent getActionContent() {
    return montageContent;
  }
}
