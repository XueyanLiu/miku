package com.biubiu.miku.util;

import android.graphics.Typeface;

import com.biubiu.miku.MikuApplication;

public class FontsUtils {
  private static Typeface huaKangTypeface;
  private static Typeface huaKangWTypeface;
  private static Typeface huaKangPopWTypeface;
  private static Typeface xiaoMaiTypeface;
  private static Typeface hyxiaomaitij;
  private static Typeface hanyiheili;
  private static Typeface fanzhengchaocu;

  private FontsUtils() {}

  public static void init() {
    huaKangTypeface =
        Typeface
            .createFromAsset(MikuApplication.context.getAssets(),
                "fonts/huakang.ttc");
    huaKangWTypeface =
        Typeface
            .createFromAsset(MikuApplication.context.getAssets(),
                "fonts/huakang_w.ttf");
    huaKangPopWTypeface =
        Typeface
            .createFromAsset(MikuApplication.context.getAssets(),
                "fonts/huakang_pop_w3.ttc");
    xiaoMaiTypeface =
        Typeface
            .createFromAsset(MikuApplication.context.getAssets(),
                "fonts/xiaomai.ttf");

    hyxiaomaitij = Typeface.createFromAsset(MikuApplication.context.getAssets(),
        "fonts/hyxiaomaitij.ttf");

    hanyiheili = Typeface.createFromAsset(MikuApplication.context.getAssets(),
        "fonts/hanyiheili.ttf");

    fanzhengchaocu = Typeface.createFromAsset(MikuApplication.context.getAssets(),
        "fonts/fanzhengchaocu.ttf");
  }

  public static Typeface getHuaKangTypeface() {
    return huaKangTypeface;
  }


  public static Typeface getHuaKangWTypeface() {
    return huaKangWTypeface;
  }

  public static Typeface getXiaoMaiTypeface() {
    return xiaoMaiTypeface;
  }

  public static Typeface getHuaKangPopWTypeface() {
    return huaKangPopWTypeface;
  }

  public static Typeface getHanyiheili() {
    return hanyiheili;
  }

  public static Typeface getHyxiaomaitij() {
    return hyxiaomaitij;
  }

  public static Typeface getFanzhengchaocu() {
    return fanzhengchaocu;
  }
}
