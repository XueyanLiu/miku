package com.biubiu.miku.event;

import com.biubiu.miku.util.video.action.videoTag.VideoTagContent;

public class SelectVideoTagEvent {
  private final VideoTagContent videoTagContent;

  public SelectVideoTagEvent(VideoTagContent videoTagContent){
    this.videoTagContent = videoTagContent;
  }

  public VideoTagContent getVideoTagContent() {
    return videoTagContent;
  }
}
