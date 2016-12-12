package com.biubiu.miku.util.video.action.filter;

import android.graphics.PointF;
import android.text.TextUtils;

public class FilterCurves {
  private PointF[] rgbCompositeControlPoints;
  private PointF[] redControlPoints;
  private PointF[] greenControlPoints;
  private PointF[] blueControlPoints;

  public FilterCurves(PointF[] rgbCompositeControlPoints, PointF[] redControlPoints,
                      PointF[] greenControlPoints, PointF[] blueControlPoints) {
    this.rgbCompositeControlPoints = rgbCompositeControlPoints;
    this.redControlPoints = redControlPoints;
    this.greenControlPoints = greenControlPoints;
    this.blueControlPoints = blueControlPoints;
  }

  public PointF[] getBlueControlPoints() {
    return blueControlPoints;
  }

  public PointF[] getRgbCompositeControlPoints() {
    return rgbCompositeControlPoints;
  }

  public PointF[] getRedControlPoints() {
    return redControlPoints;
  }

  public PointF[] getGreenControlPoints() {
    return greenControlPoints;
  }

  public String toFFmpegExecuteString() {
    // curves=r='0/0.11 .42/.51 1/0.95':g='0.50/0.48':b='0/0.22 .49/.44 1/0.8'
    StringBuilder stringBuilder = new StringBuilder();
    if (rgbCompositeControlPoints != null && rgbCompositeControlPoints.length > 0) {
      stringBuilder.append("m='");
      for (int i = 0; i < rgbCompositeControlPoints.length; i++) {
        PointF pointF = rgbCompositeControlPoints[i];
        if (i < rgbCompositeControlPoints.length - 1) {
          stringBuilder.append(pointF.x + "/" + pointF.y + ",");
        } else {
          stringBuilder.append(pointF.x + "/" + pointF.y + "'");
        }
      }
    }
    if (redControlPoints != null && redControlPoints.length > 0) {
      if (TextUtils.isEmpty(stringBuilder)) {
        stringBuilder.append("r='");
      } else {
        stringBuilder.append(":r='");
      }
      for (int i = 0; i < redControlPoints.length; i++) {
        PointF pointF = redControlPoints[i];
        if (i < redControlPoints.length - 1) {
          stringBuilder.append(pointF.x + "/" + pointF.y + ",");
        } else {
          stringBuilder.append(pointF.x + "/" + pointF.y + "'");
        }
      }
    }
    if (greenControlPoints != null && greenControlPoints.length > 0) {
      if (TextUtils.isEmpty(stringBuilder)) {
        stringBuilder.append("g='");
      } else {
        stringBuilder.append(":g='");
      }
      for (int i = 0; i < greenControlPoints.length; i++) {
        PointF pointF = greenControlPoints[i];
        if (i < greenControlPoints.length - 1) {
          stringBuilder.append(pointF.x + "/" + pointF.y + ",");
        } else {
          stringBuilder.append(pointF.x + "/" + pointF.y + "'");
        }
      }
    }
    if (blueControlPoints != null && blueControlPoints.length > 0) {
      if (TextUtils.isEmpty(stringBuilder)) {
        stringBuilder.append("b='");
      } else {
        stringBuilder.append(":b='");
      }
      for (int i = 0; i < blueControlPoints.length; i++) {
        PointF pointF = blueControlPoints[i];
        if (i < blueControlPoints.length - 1) {
          stringBuilder.append(pointF.x + "/" + pointF.y + ",");
        } else {
          stringBuilder.append(pointF.x + "/" + pointF.y + "'");
        }
      }
    }
    return stringBuilder.toString();
  }
}
