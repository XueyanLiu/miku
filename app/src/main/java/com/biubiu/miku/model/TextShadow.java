package com.biubiu.miku.model;

import android.graphics.Color;

import java.io.Serializable;

public class TextShadow implements Serializable {
  private static final long serialVersionUID = 3627525830138803542L;
  private String color;
  private float alpha = 1.f;
  private int x;
  private int y;
  private int radius;


  public int getColor() {
    int alpha0x =  255;
    if (alpha > 0 && alpha < 1 ) {
      alpha0x = (int)(alpha * 255);
    }
    return getAlphaComponent(Color.parseColor(color),alpha0x);
  }

  public int getRadius() {
    return radius;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public TextShadow(int radius, int x, int y, String color, float alpha){
    this.radius = radius;
    this.x = x;
    this.y = y;
    this.color = color;
    this.alpha = alpha;
  }

  private static int getAlphaComponent(int color,int factor) {
    return (factor << 24) | (color & 0x00ffffff);
  }
}
