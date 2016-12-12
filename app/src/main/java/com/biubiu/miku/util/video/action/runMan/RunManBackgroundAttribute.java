package com.biubiu.miku.util.video.action.runMan;

import com.biubiu.miku.util.video.action.SourceType;

import java.io.Serializable;

public class RunManBackgroundAttribute implements Serializable {
  private static final long serialVersionUID = 2995334840519211284L;
  private final SourceType sourceType;
  private final int bgResId;
  private final String imagePath;
  private final int width;
  private final int height;

  public RunManBackgroundAttribute(SourceType sourceType, int bgResId, String imagePath, int width,
                                   int height) {
    this.sourceType = sourceType;
    this.bgResId = bgResId;
    this.imagePath = imagePath;
    this.width = width;
    this.height = height;
  }

  public SourceType getSourceType() {
    return sourceType;
  }

  public int getBgResId() {
    return bgResId;
  }

  public String getImagePath() {
    return imagePath;
  }

  public int getHeight() {
    return height;
  }

  public int getWidth() {
    return width;
  }

  public static class Builder {
    private SourceType sourceType;
    private int bgResId;
    private String imagePath;
    private int width;
    private int height;

    public Builder setBgResId(int bgResId) {
      this.bgResId = bgResId;
      return this;
    }

    public Builder setImagePath(String imagePath) {
      this.imagePath = imagePath;
      return this;
    }

    public Builder setSourceType(SourceType sourceType) {
      this.sourceType = sourceType;
      return this;
    }

    public Builder setHeight(int height) {
      this.height = height;
      return this;
    }

    public Builder setWidth(int width) {
      this.width = width;
      return this;
    }

    public RunManBackgroundAttribute build() {
      return new RunManBackgroundAttribute(sourceType, bgResId, imagePath, width, height);
    }
  }
}
