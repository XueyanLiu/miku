package com.biubiu.miku.util.video.action.videoTag;

import com.biubiu.miku.util.video.action.Action;
import com.biubiu.miku.util.video.action.ActionContent;
import com.biubiu.miku.util.video.action.ActionImageData;
import com.biubiu.miku.util.video.action.RecordActionLocationTask;
import com.biubiu.miku.util.video.action.RecordLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VideoTag extends Action {
  private VideoTagContent videoTagContent;

  public VideoTag(VideoTagContent videoTagContent) {
    this.videoTagContent = videoTagContent;
  }

  public List<VideoTagLocation> matchRecordLocation(int recordTimePositionMs) {
    List<VideoTagLocation> matchRecordLocationList = new ArrayList<>();
    for (RecordActionLocationTask recordLocationTask : recordActionLocationTasks) {
      Map<Integer, RecordLocation> recordLocationTaskMap =
          recordLocationTask.getShowLocationTaskMap();
      if (!recordLocationTaskMap.isEmpty()) {
        RecordLocation recordLocation = recordLocationTaskMap.get(recordTimePositionMs);
        if (recordLocation != null) {
          matchRecordLocationList
              .add(new VideoTagLocation(recordLocation, videoTagContent));
        }
      }
    }
    return matchRecordLocationList;
  }

  public void setActionImageData(ActionImageData actionImageData) {
    videoTagContent.setActionImageData(actionImageData);
  }

  public ActionImageData getActionImageData() {
    return videoTagContent.getActionImageData();
  }

  @Override
  public ActionContent getActionContent() {
    return videoTagContent;
  }

  public VideoTagContent getVideoTagContent() {
    return videoTagContent;
  }
}
