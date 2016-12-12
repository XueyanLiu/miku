package com.biubiu.miku.util.video.action.filter;

import com.biubiu.miku.util.video.action.ActionContent;
import com.biubiu.miku.util.video.action.ActionType;

import java.io.Serializable;

public class FilterContent extends ActionContent implements Serializable {
  private static final long serialVersionUID = -8295062362100551410L;
  private FilterThemeType filterThemeType;

  public FilterContent(FilterThemeType filterThemeType) {
    super(ActionType.FILTER);
    this.filterThemeType = filterThemeType;
  }

  public FilterThemeType getFilterThemeType() {
    return filterThemeType;
  }
}
