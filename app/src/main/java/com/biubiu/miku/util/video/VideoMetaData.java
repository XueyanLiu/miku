package com.biubiu.miku.util.video;

public class VideoMetaData {
  private int width;
  private int height;
  private int rotation;
  private long duration;

  public VideoMetaData(int width, int height, int rotation, long duration) {
    this.width = width;
    this.height = height;
    this.rotation = rotation;
    this.duration = duration;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public int getRotation() {
    return rotation;
  }

  public long getDuration() {
    return duration;
  }
}
