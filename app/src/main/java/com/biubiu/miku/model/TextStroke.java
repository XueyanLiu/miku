package com.biubiu.miku.model;

import java.io.Serializable;

public class TextStroke implements Serializable {
  private static final long serialVersionUID = 516465395590813304L;

  private String color;
  private int width;

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public int getWidth() {
    return width;
  }

  public void setWidth(int width) {
    this.width = width;
  }
}
