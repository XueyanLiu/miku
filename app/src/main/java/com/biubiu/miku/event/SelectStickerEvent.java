package com.biubiu.miku.event;

import com.biubiu.miku.util.video.action.sticker.StickerImageData;
import com.biubiu.miku.util.video.action.sticker.StickerImageGroup;

public class SelectStickerEvent {

  public final StickerImageData stickerImageData;
  private final StickerImageGroup group;

  public SelectStickerEvent(StickerImageData stickerImageData, StickerImageGroup group) {
    this.stickerImageData = stickerImageData;
    this.group = group;
  }

  public StickerImageData getStickerImageData() {
    return stickerImageData;
  }

  public StickerImageGroup getStickerImageGroup(){
    return group;
  }
}
