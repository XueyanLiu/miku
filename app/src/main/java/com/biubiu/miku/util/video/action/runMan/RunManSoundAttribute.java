package com.biubiu.miku.util.video.action.runMan;

import com.biubiu.miku.util.video.action.SourceType;

import java.io.Serializable;

public class RunManSoundAttribute implements Serializable {
  private static final long serialVersionUID = -5808004915149397246L;
  private final SourceType sourceType;
  private final String soundName;
  private final int soundResId;
  private String soundPath;

  private RunManSoundAttribute(String soundName,
                               SourceType sourceType, int soundResId, String soundPath) {
    this.soundName = soundName;
    this.sourceType = sourceType;
    this.soundResId = soundResId;
    this.soundPath = soundPath;
  }


  public SourceType getSourceType() {
    return sourceType;
  }

  public int getSoundResId() {
    return soundResId;
  }

  public String getSoundPath() {
    return soundPath;
  }

  public String getSoundName() {
    return soundName;
  }

  public void setSoundPath(String soundPath) {
    this.soundPath = soundPath;
  }

  public static class Builder {
    private String soundName;
    private SourceType sourceType;
    private int imageResId;
    private String imagePath;

    public Builder setSoundName(String soundName) {
      this.soundName = soundName;
      return this;
    }

    public Builder setSourceType(SourceType sourceType) {
      this.sourceType = sourceType;
      return this;
    }

    public Builder setImageResId(int imageResId) {
      this.imageResId = imageResId;
      return this;
    }

    public Builder setImagePath(String imagePath) {
      this.imagePath = imagePath;
      return this;
    }

    public RunManSoundAttribute build() {
      return new RunManSoundAttribute(soundName, sourceType, imageResId,
          imagePath);
    }
  }
}
