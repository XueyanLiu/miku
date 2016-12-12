package com.biubiu.miku.util.video.action.sticker;

import com.biubiu.miku.util.video.action.RecordLocation;

public class StickerLocation {
  private final RecordLocation recordLocation;
  private final StickerImageData stickerImageData;
  private long createTimeMs;

  public StickerLocation(RecordLocation recordLocation, StickerImageData stickerImageData,
      long createTimeMs) {
    this.recordLocation = recordLocation;
    this.stickerImageData = stickerImageData;
    this.createTimeMs = createTimeMs;
  }

  public RecordLocation getRecordLocation() {
    return recordLocation;
  }

  public StickerImageData getStickerImageData() {
    return stickerImageData;
  }

  public long getCreateTimeMs() {
    return createTimeMs;
  }
}
