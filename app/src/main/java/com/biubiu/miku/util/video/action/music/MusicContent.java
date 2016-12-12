package com.biubiu.miku.util.video.action.music;

import com.biubiu.miku.util.video.action.ActionContent;
import com.biubiu.miku.util.video.action.ActionType;

import java.io.Serializable;

public class MusicContent extends ActionContent implements Serializable {
  private static final long serialVersionUID = 2716153426046470470L;
  private Music music;

  public MusicContent(Music music) {
    super(ActionType.MUSIC);
    this.music = music;
  }

  public Music getMusic() {
    return music;
  }
}
