package com.biubiu.miku.util.video.action.sticker;

import com.biubiu.miku.util.video.action.SourceType;

import java.io.Serializable;
import java.util.List;

public class StickerImageData implements Serializable {
  private static final long serialVersionUID = -20562412609276111L;
  private SourceType sourceType;
  private List<String> stickerImagePathList;
  private int frameSize;
  private int playIntervalMs;
  private String previewGifPath;
  private String previewImagePath;
  private int soundId;
  private String soundPath;

  private StickerImageData(SourceType sourceType, List<String> stickerImagePathList,
                           int playIntervalMs, String previewGifPath, String previewImagePath, int soundId) {
    this.sourceType = sourceType;
    this.stickerImagePathList = stickerImagePathList;
    this.playIntervalMs = playIntervalMs;
    this.previewGifPath = previewGifPath;
    this.previewImagePath = previewImagePath;
    this.soundId = soundId;
    switch (sourceType) {
      case ASSETS:
      case FILE:
        frameSize = stickerImagePathList.size();
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

  public List<String> getStickerImagePathList() {
    return stickerImagePathList;
  }

  public String getPreviewGifPath() {
    return previewGifPath;
  }

  public String getPreviewImagePath() {
    return previewImagePath;
  }

  public int getSoundId() {
    return soundId;
  }

  public String getSoundPath() {
    return soundPath;
  }

  public void setSoundPath(String soundPath) {
    this.soundPath = soundPath;
  }

  public static class Builder {
    private SourceType sourceType;
    private List<String> stickerImagePathList;
    private int playIntervalMs = 33;
    private String previewGifPath;
    private String previewImagePath;
    private int soundId;

    public Builder setSourceType(SourceType sourceType) {
      this.sourceType = sourceType;
      return this;
    }

    public Builder setStickerImagePathList(List<String> stickerImagePathList) {
      this.stickerImagePathList = stickerImagePathList;
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

    public Builder setSoundId(int soundId) {
      this.soundId = soundId;
      return this;
    }

    public StickerImageData build() {
      return new StickerImageData(sourceType, stickerImagePathList,
          playIntervalMs, previewGifPath, previewImagePath,soundId);
    }
  }
}
