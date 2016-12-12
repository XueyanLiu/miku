package com.biubiu.miku.util.video.action.filter;

public class FilterData {
  private final String filterPath;
  private final FilterThemeType filterThemeType;

  public FilterData(String filterPath, FilterThemeType filterThemeType) {
    this.filterPath = filterPath;
    this.filterThemeType = filterThemeType;
  }

  public String getFilterPath() {
    return filterPath;
  }

  public FilterThemeType getFilterThemeType() {
    return filterThemeType;
  }
}
