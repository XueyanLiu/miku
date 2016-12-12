package com.biubiu.miku.util.video.action;

import java.io.Serializable;

public class ActionImageData implements Serializable {
  private static final long serialVersionUID = -3110000289223443027L;
  private final String imagePath;
  private final float offsetProportion;

  public ActionImageData(float offsetProportion, String imagePath) {
    this.offsetProportion = offsetProportion;
    this.imagePath = imagePath;
  }

  public String getImagePath() {
    return imagePath;
  }

  public float getOffsetProportion() {
    return offsetProportion == 0 ? 1 : offsetProportion;
  }
}
