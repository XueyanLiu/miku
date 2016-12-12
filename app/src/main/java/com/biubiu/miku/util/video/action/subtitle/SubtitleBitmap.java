package com.biubiu.miku.util.video.action.subtitle;

import android.graphics.Bitmap;

public class SubtitleBitmap {
  private final Bitmap bitmap;
  private final int width;
  private final int height;

  public SubtitleBitmap(Bitmap bitmap, int width, int height) {
    this.bitmap = bitmap;
    this.width = width;
    this.height = height;
  }

  public int getHeight() {
    return height;
  }

  public Bitmap getBitmap() {
    return bitmap;
  }

  public int getWidth() {
    return width;
  }
}
