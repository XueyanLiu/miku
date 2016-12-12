package com.biubiu.miku.event;

import com.biubiu.miku.util.video.task.VideoContentTask;

public class PostStartCallbackEvent {
  private final VideoContentTask videoContentTask;

  public VideoContentTask getVideoContentTask() {
    return videoContentTask;
  }

  public PostStartCallbackEvent(VideoContentTask videoContentTask) {
    this.videoContentTask = videoContentTask;
  }
}
