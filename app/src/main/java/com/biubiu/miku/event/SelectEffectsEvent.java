package com.biubiu.miku.event;

import com.biubiu.miku.util.video.action.montage.MontageType;

public class SelectEffectsEvent {

  public final MontageType type;

  public SelectEffectsEvent(MontageType montageType) {
    this.type = montageType;
  }

  public MontageType getMontageType() {
    return type;
  }
}
