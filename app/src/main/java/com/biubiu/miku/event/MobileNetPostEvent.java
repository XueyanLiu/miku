package com.biubiu.miku.event;

public class MobileNetPostEvent {
  private boolean post;

  public MobileNetPostEvent(boolean post) {
    this.post = post;
  }

  public boolean isPost() {
    return post;
  }
}
