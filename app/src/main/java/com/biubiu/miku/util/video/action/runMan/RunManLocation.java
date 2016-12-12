package com.biubiu.miku.util.video.action.runMan;

import com.biubiu.miku.util.video.action.RecordLocation;

public class RunManLocation {
  private final RecordLocation recordLocation;
  private final RunManContent runManContent;
  private final long createTimeMs;

  public RunManLocation(RecordLocation recordLocation, RunManContent runManContent,
      long createTimeMs) {
    this.recordLocation = recordLocation;
    this.runManContent = runManContent;
    this.createTimeMs = createTimeMs;
  }

  public RecordLocation getRecordLocation() {
    return recordLocation;
  }

  public RunManContent getRunManContent() {
    return runManContent;
  }

  public long getCreateTimeMs() {
    return createTimeMs;
  }
}
