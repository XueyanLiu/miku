package com.biubiu.miku.util.video.action;

public class TimeLine {
  private int startTimeMs;
  private int endTimeMs;

  public int getEndTimeMs() {
    return endTimeMs;
  }

  public int getStartTimeMs() {
    return startTimeMs;
  }

  public TimeLine(int startTimeMs, int endTimeMs) {
    this.startTimeMs = startTimeMs;
    this.endTimeMs = endTimeMs;
  }

  public void setEndTimeMs(int endTimeMs) {
    this.endTimeMs = endTimeMs;
  }

  public void setStartTimeMs(int startTimeMs) {
    this.startTimeMs = startTimeMs;
  }

  public boolean intersect(TimeLine timeLine) {
    if (timeLine.getEndTimeMs() >= startTimeMs && timeLine.getStartTimeMs() < endTimeMs) {
      return true;
    }
    return false;
  }

  public int getDuration() {
    return endTimeMs - startTimeMs;
  }

  public TimeLine merge(TimeLine timeLine) {
    return new TimeLine(timeLine.getStartTimeMs() < startTimeMs
        ? timeLine.getStartTimeMs()
        : startTimeMs, timeLine.getEndTimeMs() > endTimeMs ? timeLine.getEndTimeMs() : endTimeMs);
  }

  public TimeLine merge(TimeLine timeLine, int absoluteEndPosition) {
    return new TimeLine(timeLine.getStartTimeMs() < startTimeMs
        ? timeLine.getStartTimeMs()
        : startTimeMs, timeLine.getEndTimeMs() > endTimeMs
            ? (timeLine.getEndTimeMs() > absoluteEndPosition
                ? absoluteEndPosition
                : timeLine.getEndTimeMs())
            : (endTimeMs > absoluteEndPosition
                ? absoluteEndPosition
                : endTimeMs));
  }
}
