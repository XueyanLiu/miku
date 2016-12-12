package com.biubiu.miku.util.video.action.runMan;

import com.biubiu.miku.util.video.action.Action;
import com.biubiu.miku.util.video.action.ActionContent;
import com.biubiu.miku.util.video.action.ActionImageData;
import com.biubiu.miku.util.video.action.RecordActionLocationTask;
import com.biubiu.miku.util.video.action.RecordLocation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RunMan extends Action implements Serializable {
  private static final long serialVersionUID = -5719711578411255187L;
  private RunManContent runManContent;

  public RunMan(RunManContent runManContent) {
    this.runManContent = runManContent;
  }

  public void setActionImageData(ActionImageData actionImageData) {
    runManContent.setActionImageData(actionImageData);
  }

  public List<RunManLocation> matchRecordLocation(int recordTimePositionMs) {
    List<RunManLocation> matchRecordLocationList = new ArrayList<>();
    for (RecordActionLocationTask recordLocationTask : recordActionLocationTasks) {
      Map<Integer, RecordLocation> recordLocationTaskMap =
          recordLocationTask.getShowLocationTaskMap();
      if (!recordLocationTaskMap.isEmpty()) {
        RecordLocation recordLocation = recordLocationTaskMap.get(recordTimePositionMs);
        if (recordLocation != null) {
          matchRecordLocationList.add(new RunManLocation(recordLocation, runManContent,
              recordLocationTask.getCreateTimeMs()));
        }
      }
    }
    return matchRecordLocationList;
  }

  public ActionImageData getActionImageData() {
    return runManContent.getActionImageData();
  }

  public int getLocationSize() {
    int size = 0;
    for (RecordActionLocationTask recordLocationTask : recordActionLocationTasks) {
      size += recordLocationTask.getGenerateLocationTaskMap().size();
    }
    return size;
  }

  public RunManContent getRunManContent() {
    return runManContent;
  }

  @Override
  public ActionContent getActionContent() {
    return runManContent;
  }
}
