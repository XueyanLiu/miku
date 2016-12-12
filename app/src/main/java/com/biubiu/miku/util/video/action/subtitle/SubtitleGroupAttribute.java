package com.biubiu.miku.util.video.action.subtitle;

public class SubtitleGroupAttribute {
  private final long id;
  private final int width;
  private final int height;

  public SubtitleGroupAttribute(long id, int width, int height) {
    this.id = id;
    this.width = width;
    this.height = height;
  }

  public long getId() {
    return id;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

}
