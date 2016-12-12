package com.biubiu.miku.util;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;

/**
 * Created by hanxiao on 15-11-3.
 */
public class AnimUtils {

  public static void ViewTranslationY(View view, float startY, float endY, long duration, Animator.AnimatorListener listener) {
    ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", startY, endY);
    animator.setDuration(duration);
    animator.start();
    if(listener != null) {
      animator.addListener(listener);
    }
  }
}
