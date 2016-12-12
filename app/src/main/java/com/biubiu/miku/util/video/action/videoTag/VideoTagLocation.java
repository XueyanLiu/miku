package com.biubiu.miku.util.video.action.videoTag;

import com.biubiu.miku.util.video.action.RecordLocation;

public class VideoTagLocation {
  private final RecordLocation recordLocation;
  private final VideoTagContent videoTagContent;

  public VideoTagLocation(RecordLocation recordLocation, VideoTagContent videoTagContent) {
    this.recordLocation = recordLocation;
    this.videoTagContent = videoTagContent;
  }

  public RecordLocation getRecordLocation() {
    return recordLocation;
  }

  public VideoTagContent getVideoTagContent() {
    return videoTagContent;
  }
}
