package com.biubiu.miku.util.video.action.subtitle;

import com.biubiu.miku.util.video.action.TimeLine;

import java.util.List;

public class SubtitleTimeBox {
  private SubtitleType subtitleType;
  private final String subtitleImagePath;
  private final List<TimeLine> timeLineList;
  private final int bitmapHeight;
  private final int bitmapWidth;

  public SubtitleTimeBox(String subtitleImagePath, List<TimeLine> timeLineList,
                         int bitmapWidth, int bitmapHeight, SubtitleType subtitleType) {
    this.subtitleImagePath = subtitleImagePath;
    this.timeLineList = timeLineList;
    this.bitmapHeight = bitmapHeight;
    this.bitmapWidth = bitmapWidth;
    this.subtitleType = subtitleType;
  }

  public String getSubtitleImagePath() {
    return subtitleImagePath;
  }

  public List<TimeLine> getTimeLineList() {
    return timeLineList;
  }

  public int getBitmapHeight() {
    return bitmapHeight;
  }

  public int getBitmapWidth() {
    return bitmapWidth;
  }

  public SubtitleType getSubtitleType() {
    return subtitleType;
  }
}
