package com.biubiu.miku.util.video.action;

import java.io.Serializable;

public class RecordLocation implements Serializable {
  private static final long serialVersionUID = -8553203838603401357L;
  private int offsetX;
  private int offsetY;
  private VideoActionParams videoActionParams;

  public RecordLocation(int offsetX, int offsetY, VideoActionParams videoActionParams) {
    this.offsetX = offsetX;
    this.offsetY = offsetY;
    this.videoActionParams = videoActionParams;
  }

  public int getOffsetX() {
    return offsetX;
  }

  public int getOffsetY() {
    return offsetY;
  }

  public void setOffsetX(int offsetX) {
    this.offsetX = offsetX;
  }

  public void setOffsetY(int offsetY) {
    this.offsetY = offsetY;
  }

  public void setVideoActionParams(VideoActionParams videoActionParams) {
    this.videoActionParams = videoActionParams;
  }

  public VideoActionParams getVideoActionParams() {
    return videoActionParams != null ? videoActionParams : new VideoActionParams(1, 1);
  }
}
