package com.biubiu.miku.event;

import com.biubiu.miku.util.video.action.subtitle.SubtitleContent;

public class NewSubtitleEvent {
  private final SubtitleContent subtitleContent;

  public NewSubtitleEvent(SubtitleContent subtitleContent) {
    this.subtitleContent = subtitleContent;
  }

  public SubtitleContent getSubtitleContent() {
    return subtitleContent;
  }
}
