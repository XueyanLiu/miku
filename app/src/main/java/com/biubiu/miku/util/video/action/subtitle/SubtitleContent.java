package com.biubiu.miku.util.video.action.subtitle;

import com.biubiu.miku.util.video.action.ActionContent;
import com.biubiu.miku.util.video.action.ActionImageData;
import com.biubiu.miku.util.video.action.ActionType;

import java.io.Serializable;

public class SubtitleContent extends ActionContent implements Serializable {

  private static final long serialVersionUID = -7027223486444766857L;
  private SubtitleType subtitleType;
  private String content;
  private String translateContent;
  private ActionImageData actionImageData;

  public SubtitleContent(SubtitleType subtitleType, String content, String translateContent) {
    super(ActionType.SUBTITLE);
    this.subtitleType = subtitleType;
    this.content = content;
    this.translateContent = translateContent;
  }

  public SubtitleType getSubtitleType() {
    return subtitleType;
  }

  public void setSubtitleType(SubtitleType subtitleType) {
    this.subtitleType = subtitleType;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getTranslateContent() {
    return translateContent;
  }

  public void setTranslateContent(String translateContent) {
    this.translateContent = translateContent;
  }

  public void setActionImageData(ActionImageData actionImageData) {
    this.actionImageData = actionImageData;
  }

  public ActionImageData getActionImageData() {
    return actionImageData;
  }

  public static class Builder {
    private SubtitleType subtitleType;
    private String content;
    private String translateContent;

    public Builder setSubtitleType(SubtitleType subtitleType) {
      this.subtitleType = subtitleType;
      return this;
    }


    public Builder setContent(String content) {
      this.content = content;
      return this;
    }

    public Builder setTranslateContent(String translateContent) {
      this.translateContent = translateContent;
      return this;
    }

    public SubtitleContent build() {
      return new SubtitleContent(subtitleType, content, translateContent);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || !(o instanceof SubtitleContent)) {
      return false;
    }
    SubtitleContent otherSubtitleContent = (SubtitleContent) o;
    return otherSubtitleContent.getSubtitleType() == getSubtitleType()
        && otherSubtitleContent.getTranslateContent() == getTranslateContent()
        && otherSubtitleContent.getContent() == getContent();
  }

  @Override
  public String toString() {
    return "SubtitleContent{" +
        "subtitleType=" + subtitleType +
        ", content='" + content + '\'' +
        ", translateContent='" + translateContent + '\'' +
        ", actionImageData=" + actionImageData +
        '}';
  }
}
