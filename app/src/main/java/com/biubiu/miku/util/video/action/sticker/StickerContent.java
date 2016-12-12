package com.biubiu.miku.util.video.action.sticker;

import com.biubiu.miku.util.video.action.ActionContent;
import com.biubiu.miku.util.video.action.ActionImageData;
import com.biubiu.miku.util.video.action.ActionType;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class StickerContent extends ActionContent implements Serializable {
  private static final long serialVersionUID = -2645692687856054505L;
  private StickerImageData stickerImageData;
  private Map<String, ActionImageData> stickerFrameImagePathMap = new HashMap<>();

  public StickerContent(StickerImageData stickerImageData) {
    super(ActionType.STICKER);
    this.stickerImageData = stickerImageData;
  }

  public void addFrameImageData(String stickerFrameName, ActionImageData actionImageData) {
    stickerFrameImagePathMap.put(stickerFrameName, actionImageData);
  }

  public ActionImageData getStickerFrameImageData(String stickerFrameName) {
    return stickerFrameImagePathMap.get(stickerFrameName);
  }

  public StickerImageData getStickerImageData() {
    return stickerImageData;
  }

}
