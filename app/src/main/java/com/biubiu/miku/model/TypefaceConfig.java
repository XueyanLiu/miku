package com.biubiu.miku.model;

import java.io.Serializable;

public class TypefaceConfig implements Serializable {

  private static final long serialVersionUID = 5020870304683887526L;

  private String type;
  private String sound;
  private boolean traditionalChinese;
  private String backgroundImagePath;
  private int backgroundImageId;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public int getBackgroundImageId() {
    return backgroundImageId;
  }

  public void setBackgroundImageId(int backgroundImageId) {
    this.backgroundImageId = backgroundImageId;
  }

  public String getBackgroundImagePath() {
    return backgroundImagePath;
  }

  public void setBackgroundImagePath(String backgroundImagePath) {
    this.backgroundImagePath = backgroundImagePath;
  }

  public String getSound() {
    return sound;
  }

  public void setSound(String sound) {
    this.sound = sound;
  }

  public boolean isTraditionalChinese() {
    return traditionalChinese;
  }

  public void setTraditionalChinese(boolean traditionalChinese) {
    this.traditionalChinese = traditionalChinese;
  }
}
