package com.biubiu.miku.constant;

public enum VideoQuality {
  HIGH(1),
  MEDIUM(2),
  LOW(3);
  private final int quality;

  VideoQuality(int quality) {
    this.quality = quality;
  }

  public int getQuality() {
    return quality;
  }


}
