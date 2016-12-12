package com.biubiu.miku.event;

import com.biubiu.miku.util.video.action.runMan.RunManAttribute;

public class SelectRunManEvent {
  private final RunManAttribute runManAttribute;

  public SelectRunManEvent(RunManAttribute runManAttribute) {
    this.runManAttribute = runManAttribute;
  }

  public RunManAttribute getRunManAttribute() {
    return runManAttribute;
  }
}
