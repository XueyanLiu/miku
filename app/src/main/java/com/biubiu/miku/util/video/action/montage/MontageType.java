package com.biubiu.miku.util.video.action.montage;

public enum MontageType {
  FREEZE(0f, 0f), REPEAT(1f, 1f), SLOW_MOTION(0.5f, 2f), FORWARD(2f, 0.5f), NONE(0f, 0f);
  private final float speedSize;
  private final float durationProportion;

  MontageType(float speedSize, float durationProportion) {
    this.speedSize = speedSize;
    this.durationProportion = durationProportion;
  }

  public float getSpeedSize() {
    return speedSize;
  }

  public float getDurationProportion() {
    return durationProportion;
  }
}
