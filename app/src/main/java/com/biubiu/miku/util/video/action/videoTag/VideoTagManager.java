package com.biubiu.miku.util.video.action.videoTag;

import android.content.res.Resources;

import com.biubiu.miku.MikuApplication;
import com.biubiu.miku.R;
import com.biubiu.miku.util.FontsUtils;
import com.biubiu.miku.util.video.action.SourceType;

import java.util.ArrayList;
import java.util.List;

public class VideoTagManager {
  public static final int DEFAULT_HEGITH = 32;
  public static final int KAWAII_BG_HEGITH2 = 50;
  private final List<VideoTagContent> videoTagContentList;
  private final List<VideoTagContent> videoBrifeTagContentList;
  private final List<VideoTagContent> videoPersonalityTagContentList;
  private final Resources resources;
  private static VideoTagManager videoTagManager;

  public synchronized static VideoTagManager getInstance() {
    if (videoTagManager == null) {
      videoTagManager = new VideoTagManager();
    }
    return videoTagManager;
  }

  private VideoTagManager() {
    videoTagContentList = new ArrayList<>();
    videoBrifeTagContentList = new ArrayList<>();
    videoPersonalityTagContentList = new ArrayList<>();
    resources = MikuApplication.context.getResources();



    videoTagContentList.add(new BorderVideoTagContent(SourceType.RES,
        R.drawable.video_tag_center_up, VideoTagType.CENTER_UP));
    videoBrifeTagContentList.add(new BorderVideoTagContent(SourceType.RES,
        R.drawable.video_tag_center_up,
        VideoTagType.CENTER_UP));

    videoTagContentList.add(new BorderVideoTagContent(SourceType.RES,
        R.drawable.video_tag_center_bottom, VideoTagType.CENTER_BOTTOM));
    videoBrifeTagContentList.add(new BorderVideoTagContent(SourceType.RES,
        R.drawable.video_tag_center_bottom, VideoTagType.CENTER_BOTTOM));



    videoTagContentList.add(new BorderVideoTagContent(SourceType.RES,
        R.drawable.video_tag_right_bottom, VideoTagType.RIGHT_BOTTOM));

    videoTagContentList.add(new BorderVideoTagContent(SourceType.RES, R.drawable.video_tag_right_up,
        VideoTagType.RIGHT_UP));
    videoBrifeTagContentList
        .add(new BorderVideoTagContent(SourceType.RES, R.drawable.video_tag_right_up,
            VideoTagType.RIGHT_UP));

    videoTagContentList.add(new BorderVideoTagContent(SourceType.RES,
        R.drawable.video_tag_left_bottom,
        VideoTagType.LEFT_BOTTOM));
    videoBrifeTagContentList.add(new BorderVideoTagContent(SourceType.RES,
        R.drawable.video_tag_left_bottom,
        VideoTagType.LEFT_BOTTOM));

    videoTagContentList.add(new BorderVideoTagContent(SourceType.RES, R.drawable.video_tag_left_up,
        VideoTagType.LEFT_UP));

    InnerVideoTagContent greenLeft = new InnerVideoTagContent(SourceType.RES,
        R.drawable.tag_green_left_preview, "tag_green_left");
    greenLeft.setInnerEditTextParams(new InnerEditTextParams.Builder().setTextSize(14)
        .setPaddingTop(-3).setTypeface(FontsUtils.getHuaKangTypeface()).build());
    videoTagContentList.add(greenLeft);
    videoPersonalityTagContentList.add(greenLeft);

    InnerVideoTagContent greenRight = new InnerVideoTagContent(SourceType.RES,
        R.drawable.green_right_preview, "green_right");
    greenRight.setInnerEditTextParams(new InnerEditTextParams.Builder().setTextSize(14)
        .setPaddingTop(-3).setTypeface(FontsUtils.getHuaKangTypeface()).build());
    videoTagContentList.add(greenRight);

    InnerVideoTagContent whiteLeft = new InnerVideoTagContent(SourceType.RES,
        R.drawable.white_left_preview, "white_left");
    whiteLeft.setInnerEditTextParams(new InnerEditTextParams.Builder()
        .setTextColor(resources.getColor(R.color.black))
        .setPaddingTop(-5).build());
    videoTagContentList.add(whiteLeft);
    videoBrifeTagContentList.add(whiteLeft);

    InnerVideoTagContent whiteRight = new InnerVideoTagContent(SourceType.RES,
        R.drawable.white_right_preview, "white_right");
    whiteRight.setInnerEditTextParams(new InnerEditTextParams.Builder()
        .setTextColor(resources.getColor(R.color.black)).setPaddingTop(-5).build());
    videoTagContentList.add(whiteRight);

    InnerVideoTagContent coolLeft = new InnerVideoTagContent(SourceType.RES,
        R.drawable.cool_left_preview, "cool_left");
    coolLeft.setInnerEditTextParams(new InnerEditTextParams.Builder()
        .setHeight(27)
        .setPaddingTop(-1)
        .build());
    videoTagContentList.add(coolLeft);
    videoPersonalityTagContentList.add(coolLeft);

    InnerVideoTagContent coolRight = new InnerVideoTagContent(SourceType.RES,
        R.drawable.cool_right_preview, "cool_right");
    coolRight.setInnerEditTextParams(
        new InnerEditTextParams.Builder().setHeight(27).setPaddingTop(-1).build());
    videoTagContentList.add(coolRight);

    InnerVideoTagContent pinkWhiteLeft = new InnerVideoTagContent(SourceType.RES,
        R.drawable.pinkwhite_left_preview, "pinkwhite_left");
    pinkWhiteLeft.setInnerEditTextParams(new InnerEditTextParams.Builder().setTextSize(14)
        .setHeight(KAWAII_BG_HEGITH2).setPaddingTop(20).setTypeface(FontsUtils.getHuaKangTypeface())
        .setTextColor(resources.getColor(R.color.text_pink)).build());
    videoTagContentList.add(pinkWhiteLeft);
    videoPersonalityTagContentList.add(pinkWhiteLeft);

    InnerVideoTagContent pinkWhiteRight = new InnerVideoTagContent(SourceType.RES,
        R.drawable.pinkwhite_right_preview, "pinkwhite_right");
    pinkWhiteRight.setInnerEditTextParams(new InnerEditTextParams.Builder().setTextSize(14)
        .setHeight(KAWAII_BG_HEGITH2).setPaddingTop(20).setTypeface(FontsUtils.getHuaKangTypeface())
        .setTextColor(resources.getColor(R.color.text_pink)).build());
    videoTagContentList.add(pinkWhiteRight);

    InnerVideoTagContent pinkDarkLeft = new InnerVideoTagContent(SourceType.RES,
        R.drawable.pinkdark_left_preivew, "pinkdark_left");
    pinkDarkLeft.setInnerEditTextParams(new InnerEditTextParams.Builder().setTextSize(14)
        .setHeight(KAWAII_BG_HEGITH2).setPaddingTop(20).setTypeface(FontsUtils.getHuaKangTypeface())
        .setTextColor(resources.getColor(R.color.text_pink)).build());
    videoTagContentList.add(pinkDarkLeft);

    InnerVideoTagContent pinkDarkRight = new InnerVideoTagContent(SourceType.RES,
        R.drawable.pinkdark_right_preview, "pinkdark_right");
    pinkDarkRight.setInnerEditTextParams(new InnerEditTextParams.Builder().setTextSize(14)
        .setHeight(KAWAII_BG_HEGITH2).setPaddingTop(20).setTypeface(FontsUtils.getHuaKangTypeface())
        .setTextColor(resources.getColor(R.color.text_pink)).build());
    videoTagContentList.add(pinkDarkRight);

    InnerVideoTagContent classicLeft = new InnerVideoTagContent(SourceType.RES,
        R.drawable.classic_left_preview, "classic_left");
    classicLeft.setInnerEditTextParams(
        new InnerEditTextParams.Builder().setHeight(27).setPaddingTop(-1).build());
    videoTagContentList.add(classicLeft);
    videoBrifeTagContentList.add(classicLeft);

    InnerVideoTagContent classicRight = new InnerVideoTagContent(SourceType.RES,
        R.drawable.classic_right_preview, "classic_right");
    classicRight.setInnerEditTextParams(
        new InnerEditTextParams.Builder().setHeight(27).setPaddingTop(-1).build());
    videoTagContentList.add(classicRight);

    InnerVideoTagContent blueLeft = new InnerVideoTagContent(SourceType.RES,
        R.drawable.blue_left_preview, "blue_left");
    blueLeft.setInnerEditTextParams(new InnerEditTextParams.Builder().setTextSize(14)
        .setPaddingTop(-1).setTypeface(FontsUtils.getHuaKangTypeface())
        .setTextColor(resources.getColor(R.color.text_blue)).build());
    videoTagContentList.add(blueLeft);
    videoPersonalityTagContentList.add(blueLeft);

    InnerVideoTagContent blueRight = new InnerVideoTagContent(SourceType.RES,
        R.drawable.blue_right_preview, "blue_right");
    blueRight.setInnerEditTextParams(new InnerEditTextParams.Builder().setTextSize(14)
        .setPaddingTop(-1).setTypeface(FontsUtils.getHuaKangTypeface())
        .setTextColor(resources.getColor(R.color.text_blue)).build());
    videoTagContentList.add(blueRight);

    InnerVideoTagContent kawaiiLeft = new InnerVideoTagContent(SourceType.RES,
        R.drawable.kawaii_left_preview, "kawaii_left");
    kawaiiLeft.setInnerEditTextParams(new InnerEditTextParams.Builder().setTextSize(14)
        .setPaddingTop(-5).setTypeface(FontsUtils.getHuaKangTypeface())
        .setTextColor(resources.getColor(R.color.black)).build());
    videoTagContentList.add(kawaiiLeft);
    videoPersonalityTagContentList.add(kawaiiLeft);

    InnerVideoTagContent kawaiiRight = new InnerVideoTagContent(SourceType.RES,
        R.drawable.kawaii_right_preview, "kawaii_right");
    kawaiiRight.setInnerEditTextParams(new InnerEditTextParams.Builder().setTextSize(14)
        .setTypeface(FontsUtils.getHuaKangTypeface()).setPaddingTop(-5)
        .setTextColor(resources.getColor(R.color.black)).build());
    videoTagContentList.add(kawaiiRight);

    InnerVideoTagContent upper = new InnerVideoTagContent(SourceType.RES,
        R.drawable.upper_preview, "upper");
    upper.setInnerEditTextParams(new InnerEditTextParams.Builder()
        .setHeight(27)
        .setPaddingTop(-1).build());
    videoTagContentList.add(upper);
    videoBrifeTagContentList.add(upper);

    InnerVideoTagContent lower = new InnerVideoTagContent(SourceType.RES,
        R.drawable.lower_preview, "lower");
    lower.setInnerEditTextParams(new InnerEditTextParams.Builder()
        .setHeight(27)
        .setPaddingTop(-1).build());
    videoTagContentList.add(lower);
    videoBrifeTagContentList.add(lower);
  }

  public List<VideoTagContent> getVideoTagContentList() {
    return videoTagContentList;
  }

  public List<VideoTagContent> getBrifeVideoTagContentList() {
    return videoBrifeTagContentList;
  }

  public List<VideoTagContent> getPersonalityVideoTagContentList() {
    return videoPersonalityTagContentList;
  }
}
