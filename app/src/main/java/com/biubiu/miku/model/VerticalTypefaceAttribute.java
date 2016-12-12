package com.biubiu.miku.model;

import java.io.Serializable;

public class VerticalTypefaceAttribute extends TypefaceConfig implements Serializable {

  private static final long serialVersionUID = -5842782399350206548L;
  private float angle;
  private int textSize;
  private String textFont;
  private String textColor;
  private String textForegroundImagePath;
  private int textForegroundImageId;
  private TextStroke textStroke;
  private TextShadow textShadow;
  private int maxSize;
  private int paddingTop;
  private int paddingBottom;

  public float getAngle() {
    return angle;
  }

  public void setAngle(float angle) {
    this.angle = angle;
  }

  public int getMaxSize() {
    return maxSize;
  }

  public void setMaxSize(int maxSize) {
    this.maxSize = maxSize;
  }

  public int getPaddingBottom() {
    return paddingBottom;
  }

  public void setPaddingBottom(int paddingBottom) {
    this.paddingBottom = paddingBottom;
  }

  public int getPaddingTop() {
    return paddingTop;
  }

  public void setPaddingTop(int paddingTop) {
    this.paddingTop = paddingTop;
  }

  public String getTextColor() {
    return textColor;
  }

  public void setTextColor(String textColor) {
    this.textColor = textColor;
  }

  public String getTextFont() {
    return textFont;
  }

  public void setTextFont(String textFont) {
    this.textFont = textFont;
  }

  public int getTextForegroundImageId() {
    return textForegroundImageId;
  }

  public void setTextForegroundImageId(int textForegroundImageId) {
    this.textForegroundImageId = textForegroundImageId;
  }

  public String getTextForegroundImagePath() {
    return textForegroundImagePath;
  }

  public void setTextForegroundImagePath(String textForegroundImagePath) {
    this.textForegroundImagePath = textForegroundImagePath;
  }

  public TextShadow getTextShadow() {
    return textShadow;
  }

  public void setTextShadow(TextShadow textShadow) {
    this.textShadow = textShadow;
  }

  public int getTextSize() {
    return textSize;
  }

  public void setTextSize(int textSize) {
    this.textSize = textSize;
  }

  public TextStroke getTextStroke() {
    return textStroke;
  }

  public void setTextStroke(TextStroke textStroke) {
    this.textStroke = textStroke;
  }
}
