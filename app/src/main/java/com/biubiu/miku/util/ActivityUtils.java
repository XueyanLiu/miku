package com.biubiu.miku.util;

import android.app.Fragment;
import android.content.Intent;

import com.biubiu.miku.MikuApplication;

public class ActivityUtils {
  public static void startPhotoZoom(Intent data, Fragment fragment) {
    if (data != null && data.getData() != null) {
      ImageUtils.startPhotoZoomActivity(fragment, data);
    }
  }

  public static float dpToPx(float dpSize) {
    final float scale = MikuApplication.context.getResources().getDisplayMetrics().density;
    return dpSize * scale;
  }

  public static float actionSizeToPx(float actionSize) {
    return dpToPx(actionSize / 1.075f);
  }
}
