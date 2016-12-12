package com.biubiu.miku.util.video.action.sticker;

import com.biubiu.miku.util.video.action.Action;
import com.biubiu.miku.util.video.action.ActionContent;
import com.biubiu.miku.util.video.action.ActionImageData;
import com.biubiu.miku.util.video.action.RecordActionLocationTask;
import com.biubiu.miku.util.video.action.RecordLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Sticker extends Action {
  private StickerContent stickerContent;

  public Sticker(StickerImageData stickerImageData) {
    stickerContent = new StickerContent(stickerImageData);
  }

  public List<StickerLocation> matchRecordLocation(int recordTimePositionMs) {
    List<StickerLocation> matchRecordLocationList = new ArrayList<>();
    for (RecordActionLocationTask recordLocationTask : recordActionLocationTasks) {
      Map<Integer, RecordLocation> recordLocationTaskMap =
          recordLocationTask.getShowLocationTaskMap();
      if (!recordLocationTaskMap.isEmpty()) {
        RecordLocation recordLocation = recordLocationTaskMap.get(recordTimePositionMs);
        if (recordLocation != null) {
          matchRecordLocationList
              .add(new StickerLocation(recordLocation, stickerContent.getStickerImageData(),
                  recordLocationTask.getCreateTimeMs()));
        }
      }
    }
    return matchRecordLocationList;
  }

  public void addFrameImageData(String stickerFrameName, ActionImageData actionImageData) {
    stickerContent.addFrameImageData(stickerFrameName, actionImageData);
  }

  public ActionImageData getStickerFrameImageData(String stickerFrameName) {
    return stickerContent.getStickerFrameImageData(stickerFrameName);
  }

  public StickerImageData getStickerImageData() {
    return stickerContent.getStickerImageData();
  }

  public StickerContent getStickerContent() {
    return stickerContent;
  }

  @Override
  public ActionContent getActionContent() {
    return stickerContent;
  }
}
