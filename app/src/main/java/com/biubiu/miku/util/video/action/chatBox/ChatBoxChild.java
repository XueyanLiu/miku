package com.biubiu.miku.util.video.action.chatBox;

import android.graphics.Color;

import com.biubiu.miku.util.ActivityUtils;

import java.io.Serializable;

public class ChatBoxChild implements Serializable {
  private static final long serialVersionUID = -123374296976638825L;
  private int iconImageResId;
  private float width;
  private float height;
  private float contentX;
  private float contentY;
  private float contentMaxHeight;
  private float contentMaxWidth;
  private int contentColor;

  public ChatBoxChild(int iconImageResId, float width, float height, float contentX,
      float contentY, float contentMaxWidth, float contentMaxHeight, int contentColor) {
    this.iconImageResId = iconImageResId;
    this.width = width;
    this.height = height;
    this.contentX = contentX;
    this.contentY = contentY;
    this.contentMaxHeight = contentMaxHeight;
    this.contentMaxWidth = contentMaxWidth;
    this.contentColor = contentColor;
  }

  public ChatBoxChild(int iconImageResId, float width, float height, float contentX,
      float contentY, float contentMaxWidth, float contentMaxHeight) {
    this.iconImageResId = iconImageResId;
    this.width = width;
    this.height = height;
    this.contentX = contentX;
    this.contentY = contentY;
    this.contentMaxHeight = contentMaxHeight;
    this.contentMaxWidth = contentMaxWidth;
    this.contentColor = Color.BLACK;
  }

  public int getIconImageResId() {
    return iconImageResId;
  }

  public float getWidth() {
    return ActivityUtils.actionSizeToPx(width);
  }

  public float getHeight() {
    return ActivityUtils.actionSizeToPx(height);
  }

  public float getContentX() {
    return ActivityUtils.actionSizeToPx(contentX);
  }

  public float getContentY() {
    return ActivityUtils.actionSizeToPx(contentY);
  }

  public float getContentMaxHeight() {
    return ActivityUtils.actionSizeToPx(contentMaxHeight);
  }

  public float getContentMaxWidth() {
    return ActivityUtils.actionSizeToPx(contentMaxWidth);
  }

  public int getContentColor() {
    return contentColor;
  }
}
