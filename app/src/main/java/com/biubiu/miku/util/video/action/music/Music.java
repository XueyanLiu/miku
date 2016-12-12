package com.biubiu.miku.util.video.action.music;

import com.biubiu.miku.util.sound.SoundFile;

import java.io.Serializable;

public class Music implements Serializable {

  private static final long serialVersionUID = -7519076626563323476L;
  private String filePath;
  private int startPositionMs;
  private int durationMs;
  private float musicVolume;
  private float soundVolume;
  private transient SoundFile soundFile;

  public Music(String filePath, int startPositionMs, int durationMs, float musicVolume,
               float soundVolume, SoundFile soundFile) {
    this.filePath = filePath;
    this.startPositionMs = startPositionMs;
    this.durationMs = durationMs;
    this.musicVolume = musicVolume;
    this.soundVolume = soundVolume;
    this.soundFile = soundFile;
  }

  public int getStartPositionMs() {
    return startPositionMs;
  }

  public String getFilePath() {
    return filePath;
  }

  public float getMusicVolume() {
    return musicVolume;
  }

  public float getSoundVolume() {
    return soundVolume;
  }

  public int getDurationMs() {
    return durationMs;
  }

  public SoundFile getSoundFile() {
    return soundFile;
  }

  public void setStartPositionMs(int startPositionMs) {
    this.startPositionMs = startPositionMs;
  }
}
