package com.biubiu.miku.util.video.action.subtitle;

import com.biubiu.miku.R;

import java.util.ArrayList;
import java.util.List;

public class SubtitleManager {

  private static SubtitleManager subtitleManager;
  private List<SubtitleType> classicSubtitleTypeList;
  private List<SubtitleType> jpSubtitleTypeList;
  private List<SubtitleType> loveSubtitleTypeList;
  private List<SubtitleType> officeSubtitleTypeList;

  public synchronized static SubtitleManager getInstance() {
    if (subtitleManager == null) {
      subtitleManager = new SubtitleManager();
    }
    return subtitleManager;
  }

  public SubtitleManager() {
    classicSubtitleTypeList = new ArrayList<>();
    jpSubtitleTypeList = new ArrayList<>();
    loveSubtitleTypeList = new ArrayList<>();
    officeSubtitleTypeList = new ArrayList<>();

    classicSubtitleTypeList.add(SubtitleType.CARTOON_WHITE);
    classicSubtitleTypeList.add(SubtitleType.CARTOON_EN);
    classicSubtitleTypeList.add(SubtitleType.CARTOON_JP);
    classicSubtitleTypeList.add(SubtitleType.CLASSIC_WHITE);
    classicSubtitleTypeList.add(SubtitleType.CLASSIC_EN);
    classicSubtitleTypeList.add(SubtitleType.CLASSIC_JP);
    classicSubtitleTypeList.add(SubtitleType.CARTOON_BLACK);
    classicSubtitleTypeList.add(SubtitleType.CLASSIC_BLACK);

    jpSubtitleTypeList.add(SubtitleType.JP_STYLE_ORIGIN);
    jpSubtitleTypeList.add(SubtitleType.JP_STYLE_BLACK);
    jpSubtitleTypeList.add(SubtitleType.JP_STYLE_BLUE);
    jpSubtitleTypeList.add(SubtitleType.JP_STYLE_ORANGE);
    jpSubtitleTypeList.add(SubtitleType.JP_STYLE_YELLOW);

    loveSubtitleTypeList.add(SubtitleType.LOVE_NOTE_YELLOW);
    loveSubtitleTypeList.add(SubtitleType.LOVE_NOTE_WHITE);
    loveSubtitleTypeList.add(SubtitleType.LOVE_NOTE_LIGHT_YELLOW);

    officeSubtitleTypeList.add(SubtitleType.OFFICE_STORY_YELLOW);
    officeSubtitleTypeList.add(SubtitleType.OFFICE_STORY_RED);
    officeSubtitleTypeList.add(SubtitleType.OFFICE_STORY_BLUE);
  }

  public List<SubtitleType> getClassicSubtitle() {
    return classicSubtitleTypeList;
  }

  public List<SubtitleType> getJpSubtitleTypeList() {
    return jpSubtitleTypeList;
  }

  public List<SubtitleType> getLoveSubtitleTypeList() {
    return loveSubtitleTypeList;
  }

  public List<SubtitleType> getOfficeSubtitleTypeList() {
    return officeSubtitleTypeList;
  }

  public static int getImageResId(SubtitleType subtitleType) {
    switch (subtitleType) {
      case CARTOON_WHITE:
        return R.drawable.cartoonwhite;
      case CARTOON_EN:
        return R.drawable.cartoonen;
      case CARTOON_BLACK:
        return R.drawable.cartoonblack;
      case CARTOON_JP:
        return R.drawable.cartoonjp;
      case CLASSIC_WHITE:
        return R.drawable.classicwhite;
      case CLASSIC_EN:
        return R.drawable.classicen;
      case CLASSIC_BLACK:
        return R.drawable.classicblack;
      case CLASSIC_JP:
        return R.drawable.classicjp;
      case LOVE_NOTE_YELLOW:
        return R.drawable.love_note_yellow;
      case LOVE_NOTE_LIGHT_YELLOW:
        return R.drawable.love_note_light_yellow;
      case LOVE_NOTE_WHITE:
        return R.drawable.love_note_white;
      case OFFICE_STORY_YELLOW:
        return R.drawable.office_story_yellow;
      case OFFICE_STORY_RED:
        return R.drawable.office_story_red;
      case OFFICE_STORY_BLUE:
        return R.drawable.office_story_blue;
      case JP_STYLE_ORIGIN:
        return R.drawable.jp_style_origin;
      case JP_STYLE_BLACK:
        return R.drawable.jp_style_black;
      case JP_STYLE_BLUE:
        return R.drawable.jp_style_blue;
      case JP_STYLE_ORANGE:
        return R.drawable.jp_style_orange;
      case JP_STYLE_YELLOW:
        return R.drawable.jp_style_yellow;
      default:
        return 0;
    }
  }
}
