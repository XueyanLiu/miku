package com.biubiu.miku.util.video.action.videoTag;

import android.graphics.Typeface;

import com.biubiu.miku.MikuApplication;
import com.biubiu.miku.R;
import com.biubiu.miku.util.SystemUtils;

import java.io.Serializable;

public class InnerEditTextParams implements Serializable {

  private static final long serialVersionUID = 6320768466978876776L;
  private int height;
  private int textColor;
  private int paddingTop;
  private int textSize;
  private Typeface typeface;

  public InnerEditTextParams(int height, int textColor, int paddingTop, int textSize,
                             Typeface typeface) {
    this.height = height;
    this.textColor = textColor;
    this.paddingTop = paddingTop;
    this.textSize = textSize;
    this.typeface = typeface;
  }

  public int getHeight() {
    return height;
  }

  public int getPaddingTop() {
    return paddingTop;
  }

  public int getTextColor() {
    return textColor;
  }

  public int getTextSize() {
    return textSize;
  }

  public Typeface getTypeface() {
    return typeface;
  }

  public static class Builder {
    private int height = SystemUtils.dpToPx(VideoTagManager.DEFAULT_HEGITH);
    private int textColor = MikuApplication.context.getResources().getColor(R.color.white);
    private int paddingTop = SystemUtils.dpToPx(0);
    private int textSize = 12;
    private Typeface typeface = Typeface.DEFAULT;

    public Builder setHeight(int height) {
      this.height = SystemUtils.dpToPx(height);
      return this;
    }

    public Builder setPaddingTop(int paddingTop) {
      this.paddingTop = SystemUtils.dpToPx(paddingTop);
      return this;
    }

    public Builder setTextColor(int textColor) {
      this.textColor = textColor;
      return this;
    }

    public Builder setTextSize(int textSize) {
      this.textSize = textSize;
      return this;
    }

    public Builder setTypeface(Typeface typeface) {
      this.typeface = typeface;
      return this;
    }

    public InnerEditTextParams build() {
      return new InnerEditTextParams(height, textColor, paddingTop, textSize, typeface);
    }
  }
}
