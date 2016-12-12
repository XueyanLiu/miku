package com.biubiu.miku.util.video.action.videoTag;

import com.biubiu.miku.util.video.action.SourceType;

import java.io.Serializable;
import java.util.List;

public class VideoTagImageData implements Serializable {
  private static final long serialVersionUID = -20562412609276111L;
  private SourceType sourceType;
  private List<String> videoTagImagePathList;
  private int frameSize;
  private int playIntervalMs;
  private String previewGifPath;
  private String previewImagePath;

  private VideoTagImageData(SourceType sourceType, List<String> videoTagImagePathList,
                            int playIntervalMs, String previewGifPath, String previewImagePath) {
    this.sourceType = sourceType;
    this.videoTagImagePathList = videoTagImagePathList;
    this.playIntervalMs = playIntervalMs;
    this.previewGifPath = previewGifPath;
    this.previewImagePath = previewImagePath;
    switch (sourceType) {
      case ASSETS:
      case FILE:
        frameSize = videoTagImagePathList.size();
        break;
    }
  }

  public SourceType getSourceType() {
    return sourceType;
  }

  public int getPlayIntervalMs() {
    return playIntervalMs;
  }

  public int getFrameSize() {
    return frameSize;
  }

  public List<String> getVideoTagImagePathList() {
    return videoTagImagePathList;
  }

  public String getPreviewGifPath() {
    return previewGifPath;
  }

  public String getPreviewImagePath() {
    return previewImagePath;
  }

  public static class Builder {
    private SourceType sourceType;
    private List<String> viewTagImagePathList;
    private int playIntervalMs = 33;
    private String previewGifPath;
    private String previewImagePath;

    public Builder setSourceType(SourceType sourceType) {
      this.sourceType = sourceType;
      return this;
    }

    public Builder setViewTagImagePathList(List<String> viewTagImagePathList) {
      this.viewTagImagePathList = viewTagImagePathList;
      return this;
    }

    public Builder setPlayIntervalMs(int playIntervalMs) {
      this.playIntervalMs = playIntervalMs;
      return this;
    }

    public Builder setPreviewGifPath(String previewGifPath) {
      this.previewGifPath = previewGifPath;
      return this;
    }

    public Builder setPreviewImagePath(String previewImagePath) {
      this.previewImagePath = previewImagePath;
      return this;
    }

    public VideoTagImageData build() {
      return new VideoTagImageData(sourceType, viewTagImagePathList,
          playIntervalMs, previewGifPath, previewImagePath);
    }
  }
}
