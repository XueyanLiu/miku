package com.biubiu.miku.util.video.action.runMan;

import com.biubiu.miku.util.video.action.SourceType;

import java.io.Serializable;

public class RunManPreviewAttribute implements Serializable {
  private static final long serialVersionUID = -7606763046256014829L;
  private final SourceType sourceType;
  private final int imageResId;
  private final String imagePath;

  private RunManPreviewAttribute(SourceType sourceType, int imageResId, String imagePath) {
    this.sourceType = sourceType;
    this.imageResId = imageResId;
    this.imagePath = imagePath;
  }

  public static long getSerialVersionUID() {
    return serialVersionUID;
  }

  public SourceType getSourceType() {
    return sourceType;
  }

  public int getImageResId() {
    return imageResId;
  }

  public String getImagePath() {
    return imagePath;
  }

  public static class Builder {

    private SourceType sourceType;
    private int imageResId;
    private String imagePath;

    public Builder setSourceType(SourceType sourceType) {
      this.sourceType = sourceType;
      return this;
    }

    public Builder setImageResId(int imageResId) {
      this.imageResId = imageResId;
      return this;
    }

    public Builder setImagePath(String imagePath) {
      this.imagePath = imagePath;
      return this;
    }

    public RunManPreviewAttribute build() {
      return new RunManPreviewAttribute(sourceType, imageResId, imagePath);
    }
  }
}
