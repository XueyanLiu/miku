package com.biubiu.miku.util.video.action.montage;

import com.biubiu.miku.util.video.action.TimeLine;

public class MontageTimeLine {
  private final TimeLine timeLine;
  private final MontageType montageActionType;


  public MontageTimeLine(TimeLine timeLine, MontageType montageActionType) {
    this.timeLine = timeLine;
    this.montageActionType = montageActionType;
  }

  public TimeLine getTimeLine() {
    return timeLine;
  }

  public MontageType getMontageType() {
    return montageActionType;
  }
}
