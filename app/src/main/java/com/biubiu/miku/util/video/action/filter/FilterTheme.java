package com.biubiu.miku.util.video.action.filter;

public class FilterTheme {
  private final FilterData filterData;
  private final String thumbFilePath;

  public FilterTheme(FilterData filterData, String thumbFilePath) {
    this.filterData = filterData;
    this.thumbFilePath = thumbFilePath;
  }

  public FilterData getFilterData() {
    return filterData;
  }

  public String getThumbFilePath() {
    return thumbFilePath;
  }
}
