package com.biubiu.miku.util.video.action.videoTag;

import com.biubiu.miku.util.video.action.ActionContent;
import com.biubiu.miku.util.video.action.ActionImageData;
import com.biubiu.miku.util.video.action.ActionType;
import com.biubiu.miku.util.video.action.SourceType;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class VideoTagContent extends ActionContent implements Serializable {
  private static final long serialVersionUID = 2423951306283310050L;
  private SourceType sourceType;
  private int resId;
  private String filePath;
  private boolean selected;
  private String content;
  private ActionImageData actionImageData;
  private VideoTagImageData videoTagImageData;
  private Map<String, ActionImageData> videoTagFrameImagePathMap = new HashMap<>();
  private String resPrefix;
  private int blackPointMoveToLeft;
  private int blackPointMoveToTop;

  public VideoTagContent(SourceType sourceType, int resId) {
    super(ActionType.VIDEO_TAG);
    this.sourceType = sourceType;
    this.resId = resId;
  }

  public String getResPrefix() {
    return resPrefix;
  }

  public void setResPrefix(String resPrefix) {
    this.resPrefix = resPrefix;
  }

  public String getFilePath() {
    return filePath;
  }

  public SourceType getSourceType() {
    return sourceType;
  }

  public boolean isSelected() {
    return selected;
  }

  public int getResId() {
    return resId;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public int getBlackPointMoveToTop() {
    return blackPointMoveToTop;
  }

  public void setBlackPointMoveToTop(int blackPointMoveToRight) {
    this.blackPointMoveToTop = blackPointMoveToRight;
  }

  public int getBlackPointMoveToLeft() {
    return blackPointMoveToLeft;
  }

  public void setBlackPointMoveToLeft(int blackPointMoveToLeft) {
    this.blackPointMoveToLeft = blackPointMoveToLeft;
  }

  public void setActionImageData(ActionImageData actionImageData) {
    this.actionImageData = actionImageData;
  }

  public ActionImageData getActionImageData() {
    return actionImageData;
  }

  public Map<String, ActionImageData> getVideoTagFrameImagePathMap() {
    return videoTagFrameImagePathMap;
  }

  public void setVideoTagFrameImagePathMap(Map<String, ActionImageData> videoTagFrameImagePathMap) {
    this.videoTagFrameImagePathMap = videoTagFrameImagePathMap;
  }

  public VideoTagImageData getVideoTagImageData() {
    return videoTagImageData;
  }

  public void setVideoTagImageData(VideoTagImageData videoTagImageData) {
    this.videoTagImageData = videoTagImageData;
  }
}
