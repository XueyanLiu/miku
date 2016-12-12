package com.biubiu.miku.util.video.action.montage;

public class Speed {
  private MontageType montageType;
  private int startPositionMs;
  private int durationMs;

  public Speed(MontageType montageType, int startPositionMs, int durationMs) {
    this.montageType = montageType;
    this.startPositionMs = startPositionMs;
    this.durationMs = durationMs > 3000 ? 3000 : durationMs;
  }

  public int getDurationMs() {
    return durationMs;
  }

  public int getStartPositionMs() {
    return startPositionMs;
  }

  public MontageType getMontageType() {
    return montageType;
  }
}
