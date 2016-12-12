package com.biubiu.miku.util.video.action.subtitle;

import com.biubiu.miku.util.video.action.Action;
import com.biubiu.miku.util.video.action.ActionContent;
import com.biubiu.miku.util.video.action.ActionImageData;
import com.biubiu.miku.util.video.action.RecordActionLocationTask;
import com.biubiu.miku.util.video.action.RecordLocation;
import com.biubiu.miku.util.video.action.videoTag.VideoTagLocation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Subtitle extends Action implements Serializable {
  private static final long serialVersionUID = 4068702452083379631L;
  private SubtitleContent subtitleContent;


  public Subtitle(SubtitleContent subtitleContent) {
    this.subtitleContent = subtitleContent;
  }

  public SubtitleContent getSubtitleContent() {
    return subtitleContent;
  }

  public List<VideoTagLocation> matchRecordLocation(int recordTimePositionMs) {
    List<VideoTagLocation> matchRecordLocationList = new ArrayList<>();
    for (RecordActionLocationTask recordLocationTask : recordActionLocationTasks) {
      Map<Integer, RecordLocation> recordLocationTaskMap =
          recordLocationTask.getGenerateLocationTaskMap();
      if (!recordLocationTaskMap.isEmpty()) {
        RecordLocation recordLocation = recordLocationTaskMap.get(recordTimePositionMs);
        if (recordLocation != null) {
          // matchRecordLocationList.add(new VideoTagLocation(recordLocation, videoTagContent));
        }
      }
    }
    return matchRecordLocationList;
  }

  public void setActionImageData(ActionImageData actionImageData) {
    subtitleContent.setActionImageData(actionImageData);
  }

  public ActionImageData getActionImageData() {
    return subtitleContent.getActionImageData();
  }

  @Override
  public ActionContent getActionContent() {
    return subtitleContent;
  }
}
