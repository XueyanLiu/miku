package com.biubiu.miku.event;

import com.biubiu.miku.model.Post;

public class PostProgressCallbackEvent {
  private final Post sendPost;
  private final double percent;

  public PostProgressCallbackEvent(Post sendPost, double percent) {
    this.sendPost = sendPost;
    this.percent = percent;
  }

  public double getPercent() {
    return percent;
  }

  public Post getSendPost() {
    return sendPost;
  }
}
