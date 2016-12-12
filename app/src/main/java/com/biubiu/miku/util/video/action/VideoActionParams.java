package com.biubiu.miku.util.video.action;

import java.io.Serializable;

public class VideoActionParams implements Serializable {
  private static final long serialVersionUID = -2177159754814173181L;
  private float scale = 1.0F;
  private float rotation = 0.0F;

  public VideoActionParams(float scale, float rotation) {
    this.scale = scale;
    this.rotation = rotation;
  }

  public float getRotation() {
    return rotation;
  }

  public void setRotation(float rotation) {
    this.rotation = rotation;
  }

  public float getScale() {
    return scale;
  }

  public void setScale(float scale) {
    this.scale = scale;
  }
}
