package com.biubiu.miku.util.video.action.runMan;

import java.io.Serializable;

public class RunManIconInputAttribute implements Serializable {
  private static final long serialVersionUID = 4789914488857358571L;
  private final int textSize;
  private final int maxTextLength;
  private final int rotation;
  private final int textColor;
  private final String defaultText;
  private final int marginBottom;
  private final int borderSize;
  private final int borderColor;

  public RunManIconInputAttribute(int textSize, int maxTextLength, int rotation, int textColor,
                                  String defaultText, int marginBottom, int borderSize, int borderColor) {
    this.textSize = textSize;
    this.maxTextLength = maxTextLength;
    this.rotation = rotation;
    this.textColor = textColor;
    this.defaultText = defaultText;
    this.borderSize = borderSize;
    this.borderColor = borderColor;
    this.marginBottom = marginBottom;
  }

  public int getTextSize() {
    return textSize;
  }

  public int getMaxTextLength() {
    return maxTextLength;
  }

  public int getRotation() {
    return rotation;
  }

  public int getTextColor() {
    return textColor;
  }

  public String getDefaultText() {
    return defaultText;
  }

  public int getBorderColor() {
    return borderColor;
  }

  public int getBorderSize() {
    return borderSize;
  }

  public int getMarginBottom() {
    return marginBottom;
  }

  public static class Builder {
    private int textSize;
    private int maxTextLength;
    private int rotation;
    private int textColor;
    private String defaultText;
    private int marginBottom;
    private int borderSize;
    private int borderColor;

    public Builder setTextSize(int textSize) {
      this.textSize = textSize;
      return this;
    }

    public Builder setMaxTextLength(int maxTextLength) {
      this.maxTextLength = maxTextLength;
      return this;
    }

    public Builder setRotation(int rotation) {
      this.rotation = -rotation;
      return this;
    }

    public Builder setTextColor(int textColor) {
      this.textColor = textColor;
      return this;
    }

    public Builder setDefaultText(String defaultText) {
      this.defaultText = defaultText;
      return this;
    }

    public Builder setBordersColor(int borderColor) {
      this.borderColor = borderColor;
      return this;
    }

    public Builder setBordersSize(int borderSize) {
      this.borderSize = borderSize;
      return this;
    }

    public Builder setMarginBottom(int marginBottom) {
      this.marginBottom = marginBottom;
      return this;
    }

    public RunManIconInputAttribute build() {
      return new RunManIconInputAttribute(textSize, maxTextLength, rotation, textColor,
          defaultText, marginBottom, borderSize, borderColor);
    }
  }
}
