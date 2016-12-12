package com.biubiu.miku.util.video.action.runMan;

import com.biubiu.miku.util.video.action.SourceType;

import java.io.Serializable;

public class RunManIconAttribute implements Serializable {
  private static final long serialVersionUID = -7606763046256014829L;
  private final SourceType sourceType;
  private final int imageResId;
  private final String imagePath;
  private final int width;
  private final int height;
  private final RunManIconAlignCountType runManIconAlignCountType;
  private final RunManIconAlignHorizontalType runManIconAlignHorizontalType;
  private final RunManIconAlignVerticalType runManIconAlignVerticalType;
  private final int marginHorizontal;
  private final int marginVertical;
  private final RunManIconInputAttribute runManIconInputAttribute;

  public RunManIconAttribute(SourceType sourceType, int imageResId, String imagePath, int width,
                             int height, RunManIconAlignCountType runManIconAlignCountType,
                             RunManIconAlignHorizontalType runManIconAlignHorizontalType,
                             RunManIconAlignVerticalType runManIconAlignVerticalType, int marginHorizontal,
                             int marginVertical, RunManIconInputAttribute runManIconInputAttribute) {
    this.sourceType = sourceType;
    this.imageResId = imageResId;
    this.imagePath = imagePath;
    this.width = width;
    this.height = height;
    this.runManIconAlignCountType = runManIconAlignCountType;
    this.runManIconAlignHorizontalType = runManIconAlignHorizontalType;
    this.runManIconAlignVerticalType = runManIconAlignVerticalType;
    this.marginHorizontal = marginHorizontal;
    this.marginVertical = marginVertical;
    this.runManIconInputAttribute = runManIconInputAttribute;
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

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public RunManIconAlignCountType getRunManIconAlignCountType() {
    return runManIconAlignCountType;
  }

  public RunManIconAlignHorizontalType getRunManIconAlignHorizontalType() {
    return runManIconAlignHorizontalType;
  }

  public RunManIconAlignVerticalType getRunManIconAlignVerticalType() {
    return runManIconAlignVerticalType;
  }

  public int getMarginHorizontal() {
    return marginHorizontal;
  }

  public int getMarginVertical() {
    return marginVertical;
  }

  public RunManIconInputAttribute getRunManIconInputAttribute() {
    return runManIconInputAttribute;
  }

  public static class Builder {
    private SourceType sourceType;
    private int imageResId;
    private String imagePath;
    private int width;
    private int height;
    private RunManIconAlignCountType runManIconAlignCountType;
    private RunManIconAlignHorizontalType runManIconAlignHorizontalType;
    private RunManIconAlignVerticalType runManIconAlignVerticalType;
    private int marginHorizontal;
    private int marginVertical;
    private RunManIconInputAttribute runManIconInputAttribute;


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

    public Builder setWidth(int width) {
      this.width = width;
      return this;
    }

    public Builder setHeight(int height) {
      this.height = height;
      return this;
    }

    public Builder setRunManIconAlignCountType(RunManIconAlignCountType runManIconAlignCountType) {
      this.runManIconAlignCountType = runManIconAlignCountType;
      return this;
    }

    public Builder setRunManIconAlignHorizontalType(
        RunManIconAlignHorizontalType runManIconAlignHorizontalType) {
      this.runManIconAlignHorizontalType = runManIconAlignHorizontalType;
      return this;
    }

    public Builder setRunManIconAlignVerticalType(
        RunManIconAlignVerticalType runManIconAlignVerticalType) {
      this.runManIconAlignVerticalType = runManIconAlignVerticalType;
      return this;
    }

    public Builder setMarginHorizontal(int marginHorizontal) {
      this.marginHorizontal = marginHorizontal;
      return this;
    }

    public Builder setMarginVertical(int marginVertical) {
      this.marginVertical = marginVertical;
      return this;
    }

    public Builder setRunManIconInputAttribute(RunManIconInputAttribute runManIconInputAttribute) {
      this.runManIconInputAttribute = runManIconInputAttribute;
      return this;
    }

    public RunManIconAttribute build() {
      return new RunManIconAttribute(sourceType, imageResId,
          imagePath, width, height, runManIconAlignCountType, runManIconAlignHorizontalType,
          runManIconAlignVerticalType, marginHorizontal, marginVertical, runManIconInputAttribute);
    }
  }
}
