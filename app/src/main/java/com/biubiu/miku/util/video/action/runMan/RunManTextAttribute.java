package com.biubiu.miku.util.video.action.runMan;

import android.graphics.Typeface;

import com.biubiu.miku.util.video.action.SourceType;

import java.io.Serializable;

public class RunManTextAttribute implements Serializable {
  private static final long serialVersionUID = -1970266747831732448L;
  private final SourceType textColorSourceType;
  private final int textColorImageResId;
  private final String textColorImagePath;
  private final int textSize;
  private final int textColor;
  private final int shadowColor;
  private final int shadowX;
  private final int shadowY;
  private final int bordersColor;
  private final int bordersSize;
  private final int maxTextSize;
  private final int marginX;
  private final int marginY;
  private Typeface typeface;
  private final boolean traditionalChinese;

  public RunManTextAttribute(SourceType textColorSourceType, int textColorImageResId,
                             String textColorImagePath, int textSize, int textColor, int shadowColor, int shadowX,
                             int shadowY, int bordersColor, int bordersSize, int maxTextSize, int marginX, int marginY,
                             Typeface typeface, boolean traditionalChinese) {
    this.textColorSourceType = textColorSourceType;
    this.textColorImageResId = textColorImageResId;
    this.textColorImagePath = textColorImagePath;
    this.textSize = textSize;
    this.textColor = textColor;
    this.shadowColor = shadowColor;
    this.shadowX = shadowX;
    this.shadowY = shadowY;
    this.bordersColor = bordersColor;
    this.bordersSize = bordersSize;
    this.maxTextSize = maxTextSize;
    this.marginX = marginX;
    this.marginY = marginY;
    this.typeface = typeface;
    this.traditionalChinese = traditionalChinese;
  }

  public SourceType getTextColorSourceType() {
    return textColorSourceType;
  }

  public int getTextColorImageResId() {
    return textColorImageResId;
  }

  public String getTextColorImagePath() {
    return textColorImagePath;
  }

  public int getTextSize() {
    return textSize;
  }

  public int getShadowColor() {
    return shadowColor;
  }

  public int getShadowX() {
    return shadowX;
  }

  public int getShadowY() {
    return shadowY;
  }

  public int getBordersColor() {
    return bordersColor;
  }

  public int getBordersSize() {
    return bordersSize;
  }

  public int getTextColor() {
    return textColor;
  }

  public int getMaxTextSize() {
    return maxTextSize;
  }

  public int getMarginX() {
    return marginX;
  }

  public int getMarginY() {
    return marginY;
  }

  public Typeface getTypeface() {
    return typeface;
  }

  public boolean isTraditionalChinese() {
    return traditionalChinese;
  }

  public static class Builder {
    private SourceType textColorSourceType;
    private int textColorImageResId;
    private String textColorImagePath;
    private int textSize;
    private int textColor;
    private int shadowColor;
    private int shadowX;
    private int shadowY;
    private int bordersColor;
    private int bordersSize;
    private int maxTextSize;
    private int marginX;
    private int marginY;
    private Typeface typeface;
    private boolean traditionalChinese = true;

    public Builder setTextColorSourceType(SourceType textColorSourceType) {
      this.textColorSourceType = textColorSourceType;
      return this;
    }

    public Builder setTextColorImageResId(int textColorImageResId) {
      this.textColorImageResId = textColorImageResId;
      return this;
    }

    public Builder setTextColorImagePath(String textColorImagePath) {
      this.textColorImagePath = textColorImagePath;
      return this;
    }

    public Builder setTextSize(int textSize) {
      this.textSize = textSize;
      return this;
    }

    public Builder setTextColor(int textColor) {
      this.textColor = textColor;
      return this;
    }

    public Builder setShadowColor(int shadowColor) {
      this.shadowColor = shadowColor;
      return this;
    }

    public Builder setShadowX(int shadowX) {
      this.shadowX = shadowX;
      return this;
    }

    public Builder setShadowY(int shadowY) {
      this.shadowY = shadowY;
      return this;
    }

    public Builder setBordersColor(int bordersColor) {
      this.bordersColor = bordersColor;
      return this;
    }

    public Builder setBordersSize(int bordersSize) {
      this.bordersSize = bordersSize;
      return this;
    }

    public Builder setMaxTextSize(int maxTextSize) {
      this.maxTextSize = maxTextSize;
      return this;
    }

    public Builder setMarginX(int marginX) {
      this.marginX = marginX;
      return this;
    }

    public Builder setMarginY(int marginY) {
      this.marginY = marginY;
      return this;
    }

    public Builder setTypeface(Typeface typeface) {
      this.typeface = typeface;
      return this;
    }

    public Builder setTraditionalChinese(boolean traditionalChinese) {
      this.traditionalChinese = traditionalChinese;
      return this;
    }

    public RunManTextAttribute build() {
      return new RunManTextAttribute(textColorSourceType, textColorImageResId,
          textColorImagePath, textSize, textColor, shadowColor, shadowX, shadowY, bordersColor,
          bordersSize, maxTextSize, marginX, marginY, typeface, traditionalChinese);
    }
  }
}
