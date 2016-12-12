package com.biubiu.miku.util.video.action.videoTag;

import com.biubiu.miku.util.video.action.SourceType;

import java.io.Serializable;

/**
 * video tag 输入框在图标周围
 */
public class BorderVideoTagContent extends VideoTagContent implements Serializable {
  private static final long serialVersionUID = -2756307358882870929L;
  private transient VideoTagType videoTagType;

  public BorderVideoTagContent(SourceType sourceType, int resId,
                               VideoTagType videoTagType) {
    super(sourceType, resId);
    this.videoTagType = videoTagType;
  }

  public VideoTagType getVideoTagType() {
    return videoTagType;
  }

}
