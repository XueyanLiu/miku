package com.biubiu.miku.util.video.action.runMan;

import android.content.res.Resources;

import com.biubiu.miku.constant.FontType;
import com.biubiu.miku.MikuApplication;
import com.biubiu.miku.R;
import com.biubiu.miku.model.TextShadow;
import com.biubiu.miku.model.VerticalTypefaceAttribute;
import com.biubiu.miku.util.FontsUtils;
import com.biubiu.miku.util.video.action.SourceType;

import java.util.ArrayList;
import java.util.List;

public class DefaultRunManManager {
  private static DefaultRunManManager defaultRunManManager;
  private List<RunManAttribute> runManAttributes;
  private List<RunManAttribute> kangxiAttributes;

  public synchronized static DefaultRunManManager getInstance() {
    if (defaultRunManManager == null) {
      defaultRunManManager = new DefaultRunManManager();
    }
    return defaultRunManManager;
  }

  private DefaultRunManManager() {
    runManAttributes = new ArrayList<>();

    Resources resources = MikuApplication.context.getResources();
    runManAttributes.add(getRunManWhyAttribute(resources));
    runManAttributes.add(getRunManIKnowAttribute(resources));
    runManAttributes.add(getRunManTitleAttribute(resources));
    runManAttributes.add(getRunManSOSAttribute(resources));
    runManAttributes.add(getRunManDontCryAttribute(resources));
    runManAttributes.add(getRunManEatingWatermelonAttribute(resources));
    runManAttributes.add(getRunManArrivalsAttribute(resources));
    runManAttributes.add(getRunManFireAttribute(resources));

    kangxiAttributes = new ArrayList<>();
    kangxiAttributes.add(getRunManNotAfraidAttribute(resources));
    kangxiAttributes.add(getRunManAngryAttribute(resources));
    kangxiAttributes.add(getRunManFreakAttribute(resources));
    kangxiAttributes.add(getRunManTroublesomeAttribute(resources));
    kangxiAttributes.add(getRunManNothingToSayAttribute(resources));
    kangxiAttributes.add(getRunManIndifferentAttribute(resources));
    kangxiAttributes.add(getRunManDisdainAttribute(resources));
  }

  private RunManAttribute getRunManWhyAttribute(Resources resources) {
    List<RunManIconAttribute> runManWhyIconAttributes = new ArrayList<>();
    runManWhyIconAttributes.add(new RunManIconAttribute.Builder()
        .setSourceType(SourceType.RES).setImageResId(R.drawable.smile)
        .setRunManIconAlignCountType(RunManIconAlignCountType.FIRST)
        .setRunManIconAlignHorizontalType(RunManIconAlignHorizontalType.LEFT)
        .setRunManIconAlignVerticalType(RunManIconAlignVerticalType.BOTTOM)
        .setWidth(resources.getDimensionPixelSize(R.dimen.runman_smile_icon_width))
        .setHeight(resources.getDimensionPixelSize(R.dimen.runman_smile_icon_height))
        .build());
    runManWhyIconAttributes.add(new RunManIconAttribute.Builder()
        .setSourceType(SourceType.RES).setImageResId(R.drawable.water)
        .setRunManIconAlignCountType(RunManIconAlignCountType.FIRST)
        .setRunManIconAlignHorizontalType(RunManIconAlignHorizontalType.RIGHT)
        .setRunManIconAlignVerticalType(RunManIconAlignVerticalType.BOTTOM)
        .setWidth(resources.getDimensionPixelSize(R.dimen.runman_water_icon_width))
        .setHeight(resources.getDimensionPixelSize(R.dimen.runman_water_icon_height))
        .setMarginVertical(resources.getDimensionPixelSize(R.dimen.runman_water_icon_mb))
        .setMarginHorizontal(resources.getDimensionPixelSize(R.dimen.runman_water_icon_ml))
        .build());
    return new RunManAttribute.Builder().setRunManSoundAttribute(new RunManSoundAttribute.Builder()
        .setImageResId(R.raw.run_man_033).setSourceType(SourceType.RES)
        .setSoundName(
            MikuApplication.context.getString(R.string.run_man_sound_033_spring))
        .build()).setRunManFirstCountTextAttribute(new RunManTextAttribute.Builder()
        .setTextSize(
            resources.getDimensionPixelSize(R.dimen.runman_smile_content_text_size))
        .setTextColor(resources.getColor(R.color.runman_smile_text))
        .setBordersColor(resources.getColor(R.color.black))
        .setBordersSize(
            resources.getDimensionPixelSize(R.dimen.runman_smile_content_text_borders))
        .setShadowColor(resources.getColor(R.color.black))
        .setShadowX(3).setShadowY(3).setMaxTextSize(6)
        .setMarginX(resources.getDimensionPixelSize(R.dimen.runman_smile_icon_width))
        .setMarginY(resources.getDimensionPixelSize(R.dimen.runman_smile_first_count_mt))
        .build())
        .setRunManSecondCountTextAttribute(new RunManTextAttribute.Builder()
            .setTextSize(
                resources.getDimensionPixelSize(R.dimen.runman_smile_content_text_size))
            .setTextColor(resources.getColor(R.color.runman_smile_text))
            .setBordersColor(resources.getColor(R.color.black))
            .setBordersSize(
                resources.getDimensionPixelSize(R.dimen.runman_smile_content_text_borders))
            .setShadowColor(resources.getColor(R.color.black))
            .setShadowX(3).setShadowY(3).setMaxTextSize(9)
            .setMarginY(resources.getDimensionPixelSize(R.dimen.runman_second_content_mt))
            .build())
        .setRunManPreviewAttribute(
            new RunManPreviewAttribute.Builder().setSourceType(SourceType.RES)
                .setImageResId(R.drawable.why_preview).build())
        .setContent(resources.getString(R.string.runman_why_default_content))
        .setRunManIconAttributes(runManWhyIconAttributes).build();
  }

  private RunManAttribute getRunManTitleAttribute(Resources resources) {
    List<RunManIconAttribute> runManWhyIconAttributes = new ArrayList<>();
    runManWhyIconAttributes.add(new RunManIconAttribute.Builder()
        .setSourceType(SourceType.RES).setImageResId(R.drawable.shinning)
        .setRunManIconAlignCountType(RunManIconAlignCountType.FIRST)
        .setRunManIconAlignHorizontalType(RunManIconAlignHorizontalType.RIGHT)
        .setRunManIconAlignVerticalType(RunManIconAlignVerticalType.BOTTOM)
        .setWidth(resources.getDimensionPixelSize(R.dimen.runman_shinning_icon_width))
        .setHeight(resources.getDimensionPixelSize(R.dimen.runman_shinning_icon_height))
        .build());
    runManWhyIconAttributes.add(new RunManIconAttribute.Builder()
        .setSourceType(SourceType.RES).setImageResId(R.drawable.mess)
        .setRunManIconAlignCountType(RunManIconAlignCountType.FIRST)
        .setRunManIconAlignHorizontalType(RunManIconAlignHorizontalType.LEFT)
        .setRunManIconAlignVerticalType(RunManIconAlignVerticalType.TOP)
        .setWidth(resources.getDimensionPixelSize(R.dimen.runman_mess_icon_width))
        .setHeight(resources.getDimensionPixelSize(R.dimen.runman_mess_icon_height))
        .setRunManIconInputAttribute(
            new RunManIconInputAttribute.Builder().setMaxTextLength(2).setRotation(15)
                .setTextSize(resources.getDimensionPixelSize(R.dimen.runman_mess_icon_text_size))
                .setTextColor(resources.getColor(R.color.white))
                .setDefaultText(resources.getString(R.string.excitement))
                .build())
        .build());
    return new RunManAttribute.Builder().setRunManSoundAttribute(new RunManSoundAttribute.Builder()
        .setImageResId(R.raw.run_man_045).setSourceType(SourceType.RES)
        .setSoundName(
            MikuApplication.context.getString(R.string.run_man_sound_033_spring))
        .build()).setRunManFirstCountTextAttribute(new RunManTextAttribute.Builder()
        .setTextSize(
            resources.getDimensionPixelSize(R.dimen.runman_smile_content_text_size))
        .setTextColor(resources.getColor(R.color.runman_title_text))
        .setBordersColor(resources.getColor(R.color.black))
        .setBordersSize(
            resources.getDimensionPixelSize(R.dimen.runman_smile_content_text_borders))
        .setShadowColor(resources.getColor(R.color.black))
        .setShadowX(3).setShadowY(3).setMaxTextSize(6)
        .setMarginY(resources.getDimensionPixelSize(R.dimen.runman_mess_first_content_mt))
        .setMarginX(resources.getDimensionPixelSize(R.dimen.runman_mess_first_content_ml))
        .build())
        .setRunManSecondCountTextAttribute(new RunManTextAttribute.Builder()
            .setTextSize(
                resources.getDimensionPixelSize(R.dimen.runman_smile_content_text_size))
            .setTextColor(resources.getColor(R.color.white))
            .setBordersColor(resources.getColor(R.color.black))
            .setBordersSize(
                resources.getDimensionPixelSize(R.dimen.runman_smile_content_text_borders))
            .setShadowColor(resources.getColor(R.color.black))
            .setShadowX(3).setShadowY(3).setMaxTextSize(4)
            .setMarginY(resources.getDimensionPixelSize(R.dimen.runman_second_content_mt))
            .setMarginX(resources.getDimensionPixelSize(R.dimen.runman_mess_first_content_ml))
            .build())
        .setRunManPreviewAttribute(
            new RunManPreviewAttribute.Builder().setSourceType(SourceType.RES)
                .setImageResId(R.drawable.title_preview).build())
        .setContent(resources.getString(R.string.runman_title_default_content))
        .setRunManIconAttributes(runManWhyIconAttributes).build();
  }


  private RunManAttribute getRunManSOSAttribute(Resources resources) {
    List<RunManIconAttribute> runManIconAttributes = new ArrayList<>();
    runManIconAttributes.add(new RunManIconAttribute.Builder()
        .setSourceType(SourceType.RES).setImageResId(R.drawable.blackbubble)
        .setRunManIconAlignCountType(RunManIconAlignCountType.FIRST)
        .setRunManIconAlignHorizontalType(RunManIconAlignHorizontalType.LEFT)
        .setRunManIconAlignVerticalType(RunManIconAlignVerticalType.TOP)
        .setWidth(resources.getDimensionPixelSize(R.dimen.runman_black_bubble_icon_width))
        .setHeight(resources.getDimensionPixelSize(R.dimen.runman_black_bubble_icon_height))
        .setMarginHorizontal(resources.getDimensionPixelSize(R.dimen.runman_black_bubble_icon_ml))
        .setRunManIconInputAttribute(
            new RunManIconInputAttribute.Builder().setMaxTextLength(3).setRotation(8)
                .setTextSize(
                    resources.getDimensionPixelSize(R.dimen.runman_black_bubble_icon_text_size))
                .setTextColor(resources.getColor(R.color.white))
                .setDefaultText(resources.getString(R.string.sos))
                .setMarginBottom(
                    resources.getDimensionPixelSize(R.dimen.runman_input_icon_content_mb))
                .setBordersColor(resources.getColor(R.color.black))
                .setBordersSize(
                    resources.getDimensionPixelSize(R.dimen.runman_smile_content_text_borders))
                .build())
        .build());
    return new RunManAttribute.Builder().setRunManSoundAttribute(new RunManSoundAttribute.Builder()
        .setImageResId(R.raw.run_man_014).setSourceType(SourceType.RES)
        .setSoundName(
            MikuApplication.context.getString(R.string.run_man_sound_033_spring))
        .build()).setRunManFirstCountTextAttribute(new RunManTextAttribute.Builder()
        .setTextSize(
            resources.getDimensionPixelSize(R.dimen.runman_smile_content_text_size))
        .setTextColorSourceType(SourceType.RES)
        .setTextColorImageResId(R.drawable.roy_texture)
        .setBordersColor(resources.getColor(R.color.black))
        .setBordersSize(
            resources.getDimensionPixelSize(R.dimen.runman_smile_content_text_borders))
        .setShadowColor(resources.getColor(R.color.black))
        .setShadowX(0).setShadowY(6).setMaxTextSize(10)
        .setMarginY(resources.getDimensionPixelSize(R.dimen.runman_black_bubble_icon_height))
        .build())
        .setRunManPreviewAttribute(
            new RunManPreviewAttribute.Builder().setSourceType(SourceType.RES)
                .setImageResId(R.drawable.sos_preview).build())
        .setContent(resources.getString(R.string.runman_sos_default_content))
        .setRunManIconAttributes(runManIconAttributes).build();
  }

  private RunManAttribute getRunManEatingWatermelonAttribute(Resources resources) {
    List<RunManIconAttribute> runManIconAttributes = new ArrayList<>();
    runManIconAttributes.add(new RunManIconAttribute.Builder()
        .setSourceType(SourceType.RES).setImageResId(R.drawable.blackbubble)
        .setRunManIconAlignCountType(RunManIconAlignCountType.FIRST)
        .setRunManIconAlignHorizontalType(RunManIconAlignHorizontalType.LEFT)
        .setRunManIconAlignVerticalType(RunManIconAlignVerticalType.TOP)
        .setWidth(resources.getDimensionPixelSize(R.dimen.runman_black_bubble_icon_width))
        .setHeight(resources.getDimensionPixelSize(R.dimen.runman_black_bubble_icon_height))
        .setMarginHorizontal(resources.getDimensionPixelSize(R.dimen.runman_black_bubble_icon_ml))
        .setRunManIconInputAttribute(
            new RunManIconInputAttribute.Builder().setMaxTextLength(7).setRotation(8)
                .setTextSize(
                    resources.getDimensionPixelSize(R.dimen.runman_black_bubble_icon_text_size))
                .setTextColor(resources.getColor(R.color.white))
                .setDefaultText(resources.getString(R.string.come_on))
                .setMarginBottom(
                    resources.getDimensionPixelSize(R.dimen.runman_input_icon_content_mb))
                .setBordersColor(resources.getColor(R.color.black))
                .setBordersSize(
                    resources.getDimensionPixelSize(R.dimen.runman_smile_content_text_borders))
                .build())
        .build());
    return new RunManAttribute.Builder().setRunManSoundAttribute(new RunManSoundAttribute.Builder()
        .setImageResId(R.raw.run_man_009).setSourceType(SourceType.RES)
        .setSoundName(
            MikuApplication.context.getString(R.string.run_man_sound_033_spring))
        .build()).setRunManFirstCountTextAttribute(new RunManTextAttribute.Builder()
        .setTextSize(
            resources.getDimensionPixelSize(R.dimen.runman_smile_content_text_size))
        .setTextColorSourceType(SourceType.RES)
        .setTextColorImageResId(R.drawable.bo_texture)
        .setBordersColor(resources.getColor(R.color.black))
        .setBordersSize(
            resources.getDimensionPixelSize(R.dimen.runman_smile_content_text_borders))
        .setShadowColor(resources.getColor(R.color.black))
        .setShadowX(3).setShadowY(3).setMaxTextSize(10)
        .setMarginY(resources.getDimensionPixelSize(R.dimen.runman_black_bubble_icon_height))
        .build())
        .setRunManPreviewAttribute(
            new RunManPreviewAttribute.Builder().setSourceType(SourceType.RES)
                .setImageResId(R.drawable.eating_watermelon_preview).build())
        .setContent(resources.getString(R.string.runman_eating_watermelon_default_content))
        .setRunManIconAttributes(runManIconAttributes).build();
  }

  private RunManAttribute getRunManArrivalsAttribute(Resources resources) {
    List<RunManIconAttribute> runManIconAttributes = new ArrayList<>();
    runManIconAttributes.add(new RunManIconAttribute.Builder()
        .setSourceType(SourceType.RES).setImageResId(R.drawable.blackbubble)
        .setRunManIconAlignCountType(RunManIconAlignCountType.FIRST)
        .setRunManIconAlignHorizontalType(RunManIconAlignHorizontalType.LEFT)
        .setRunManIconAlignVerticalType(RunManIconAlignVerticalType.TOP)
        .setWidth(resources.getDimensionPixelSize(R.dimen.runman_black_bubble_icon_width))
        .setHeight(resources.getDimensionPixelSize(R.dimen.runman_black_bubble_icon_height))
        .setMarginHorizontal(resources.getDimensionPixelSize(R.dimen.runman_black_bubble_icon_ml))
        .setRunManIconInputAttribute(
            new RunManIconInputAttribute.Builder().setMaxTextLength(7).setRotation(8)
                .setTextSize(
                    resources.getDimensionPixelSize(R.dimen.runman_black_bubble_icon_text_size))
                .setTextColor(resources.getColor(R.color.white))
                .setDefaultText(resources.getString(R.string.come_on))
                .setMarginBottom(
                    resources.getDimensionPixelSize(R.dimen.runman_input_icon_content_mb))
                .setBordersColor(resources.getColor(R.color.black))
                .setBordersSize(
                    resources.getDimensionPixelSize(R.dimen.runman_smile_content_text_borders))
                .build())
        .build());
    return new RunManAttribute.Builder().setRunManSoundAttribute(new RunManSoundAttribute.Builder()
        .setImageResId(R.raw.run_man_009).setSourceType(SourceType.RES)
        .setSoundName(
            MikuApplication.context.getString(R.string.run_man_sound_033_spring))
        .build()).setRunManFirstCountTextAttribute(new RunManTextAttribute.Builder()
        .setTextSize(
            resources.getDimensionPixelSize(R.dimen.runman_smile_content_text_size))
        .setTextColorSourceType(SourceType.RES)
        .setTextColorImageResId(R.drawable.bb_texture)
        .setBordersColor(resources.getColor(R.color.black))
        .setBordersSize(
            resources.getDimensionPixelSize(R.dimen.runman_smile_content_text_borders))
        .setShadowColor(resources.getColor(R.color.black))
        .setShadowX(0).setShadowY(3).setMaxTextSize(10)
        .setMarginY(resources.getDimensionPixelSize(R.dimen.runman_black_bubble_icon_height))
        .build())
        .setRunManPreviewAttribute(
            new RunManPreviewAttribute.Builder().setSourceType(SourceType.RES)
                .setImageResId(R.drawable.arrivals_preview).build())
        .setContent(resources.getString(R.string.runman_arrivals_default_content))
        .setRunManIconAttributes(runManIconAttributes).build();
  }

  private RunManAttribute getRunManFireAttribute(Resources resources) {
    List<RunManIconAttribute> runManIconAttributes = new ArrayList<>();
    runManIconAttributes.add(new RunManIconAttribute.Builder()
        .setSourceType(SourceType.RES).setImageResId(R.drawable.bluelight)
        .setRunManIconAlignCountType(RunManIconAlignCountType.FIRST)
        .setRunManIconAlignHorizontalType(RunManIconAlignHorizontalType.RIGHT)
        .setRunManIconAlignVerticalType(RunManIconAlignVerticalType.TOP)
        .setWidth(resources.getDimensionPixelSize(R.dimen.runman_bluelight_icon_width))
        .setHeight(resources.getDimensionPixelSize(R.dimen.runman_bluelight_icon_height))
        .setMarginVertical(resources.getDimensionPixelSize(R.dimen.runman_icon_bluelight_mb))
        .build());
    runManIconAttributes.add(new RunManIconAttribute.Builder()
        .setSourceType(SourceType.RES).setImageResId(R.drawable.bg_black)
        .setRunManIconAlignCountType(RunManIconAlignCountType.FIRST)
        .setRunManIconAlignHorizontalType(RunManIconAlignHorizontalType.LEFT)
        .setRunManIconAlignVerticalType(RunManIconAlignVerticalType.TOP)
        .setWidth(resources.getDimensionPixelSize(R.dimen.runman_black_icon_width))
        .setHeight(resources.getDimensionPixelSize(R.dimen.runman_black_icon_height))
        .setRunManIconInputAttribute(
            new RunManIconInputAttribute.Builder().setMaxTextLength(4).setRotation(0)
                .setTextSize(
                    resources.getDimensionPixelSize(R.dimen.runman_black_icon_size))
                .setTextColor(resources.getColor(R.color.runman_black_icon_text))
                .setDefaultText(resources.getString(R.string.fire))
                .build())
        .build());
    return new RunManAttribute.Builder().setRunManSoundAttribute(new RunManSoundAttribute.Builder()
        .setImageResId(R.raw.run_man_009).setSourceType(SourceType.RES)
        .setSoundName(
            MikuApplication.context.getString(R.string.run_man_sound_033_spring))
        .build()).setRunManFirstCountTextAttribute(new RunManTextAttribute.Builder()
        .setTextSize(
            resources.getDimensionPixelSize(R.dimen.runman_smile_content_text_size))
        .setTextColorSourceType(SourceType.RES)
        .setTextColorImageResId(R.drawable.pgy_texture)
        .setBordersColor(resources.getColor(R.color.black))
        .setBordersSize(
            resources.getDimensionPixelSize(R.dimen.runman_smile_content_text_borders))
        .setShadowColor(resources.getColor(R.color.black))
        .setShadowX(3).setShadowY(3).setMaxTextSize(10)
        .setMarginY(resources.getDimensionPixelSize(R.dimen.runman_black_icon_height))
        .build())
        .setContent(resources.getString(R.string.runman_fire_default_content))
        .setRunManPreviewAttribute(
            new RunManPreviewAttribute.Builder().setSourceType(SourceType.RES)
                .setImageResId(R.drawable.fire_preview).build())
        .setRunManIconAttributes(runManIconAttributes).build();
  }

  private RunManAttribute getRunManIKnowAttribute(Resources resources) {
    return new RunManAttribute.Builder().setRunManSoundAttribute(new RunManSoundAttribute.Builder()
        .setImageResId(R.raw.run_man_013).setSourceType(SourceType.RES)
        .setSoundName(
            MikuApplication.context.getString(R.string.run_man_sound_033_spring))
        .build()).setRunManFirstCountTextAttribute(new RunManTextAttribute.Builder()
        .setTextSize(
            resources.getDimensionPixelSize(R.dimen.runman_smile_content_text_size))
        .setTextColor(resources.getColor(R.color.white))
        .setBordersColor(resources.getColor(R.color.black))
        .setBordersSize(
            resources.getDimensionPixelSize(R.dimen.runman_smile_content_text_borders))
        .setShadowColor(resources.getColor(R.color.black))
        .setShadowX(3).setShadowY(3).setMaxTextSize(3)
        .setMarginX(
            resources.getDimensionPixelSize(R.dimen.runman_hiahia_first_count_content_ml))
        .setMarginY(
            resources.getDimensionPixelSize(R.dimen.runman_hiahia_first_count_content_mt))
        .build())
        .setRunManSecondCountTextAttribute(new RunManTextAttribute.Builder()
            .setTextSize(
                resources.getDimensionPixelSize(R.dimen.runman_smile_content_text_size))
            .setTextColor(resources.getColor(R.color.runman_water_text))
            .setBordersColor(resources.getColor(R.color.black))
            .setBordersSize(
                resources.getDimensionPixelSize(R.dimen.runman_smile_content_text_borders))
            .setShadowColor(resources.getColor(R.color.black))
            .setShadowX(3).setShadowY(3).setMaxTextSize(4)
            .setMarginX(
                resources.getDimensionPixelSize(R.dimen.runman_hiahia_second_count_content_ml))
            .setMarginY(resources.getDimensionPixelSize(R.dimen.runman_second_content_mt))
            .build())
        .setRunManBackgroundAttribute(new RunManBackgroundAttribute.Builder()
            .setSourceType(SourceType.RES).setBgResId(R.drawable.hiahia)
            .setWidth(resources.getDimensionPixelSize(R.dimen.runman_hiahia_bg_width))
            .setHeight(resources.getDimensionPixelSize(R.dimen.runman_hiahia_bg_height))
            .build())
        .setRunManPreviewAttribute(
            new RunManPreviewAttribute.Builder().setSourceType(SourceType.RES)
                .setImageResId(R.drawable.iknow_preview).build())
        .setContent(resources.getString(R.string.runman_i_know_default_content))
        .build();
  }


  private RunManAttribute getRunManDontCryAttribute(Resources resources) {
    List<RunManIconAttribute> runManWhyIconAttributes = new ArrayList<>();
    runManWhyIconAttributes.add(new RunManIconAttribute.Builder()
        .setSourceType(SourceType.RES).setImageResId(R.drawable.speechless)
        .setRunManIconAlignCountType(RunManIconAlignCountType.FIRST)
        .setRunManIconAlignHorizontalType(RunManIconAlignHorizontalType.LEFT)
        .setRunManIconAlignVerticalType(RunManIconAlignVerticalType.TOP)
        .setWidth(resources.getDimensionPixelSize(R.dimen.runman_speechless_icon_width))
        .setHeight(resources.getDimensionPixelSize(R.dimen.runman_speechless_icon_height))
        .build());
    return new RunManAttribute.Builder().setRunManSoundAttribute(new RunManSoundAttribute.Builder()
        .setImageResId(R.raw.run_man_058).setSourceType(SourceType.RES)
        .setSoundName(
            MikuApplication.context.getString(R.string.run_man_sound_033_spring))
        .build()).setRunManFirstCountTextAttribute(new RunManTextAttribute.Builder()
        .setTextSize(
            resources.getDimensionPixelSize(R.dimen.runman_smile_content_text_size))
        .setTextColor(resources.getColor(R.color.white))
        .setBordersColor(resources.getColor(R.color.black))
        .setBordersSize(
            resources.getDimensionPixelSize(R.dimen.runman_smile_content_text_borders))
        .setShadowColor(resources.getColor(R.color.white))
        .setShadowX(5).setShadowY(5).setMaxTextSize(10)
        .setMarginX(
            resources.getDimensionPixelSize(R.dimen.runman_speechless_text_ml))
        .setMarginY(
            resources.getDimensionPixelSize(R.dimen.runman_speechless_text_mt))
        .build())
        .setRunManPreviewAttribute(
            new RunManPreviewAttribute.Builder().setSourceType(SourceType.RES)
                .setImageResId(R.drawable.dont_cry_preview).build())
        .setContent(resources.getString(R.string.runman_dont_cry_default_content))
        .setRunManIconAttributes(runManWhyIconAttributes)
        .build();
  }

  private RunManAttribute getRunManNotAfraidAttribute(Resources resources) {
    return new RunManAttribute.Builder()
        .setRunManFirstCountTextAttribute(new RunManTextAttribute.Builder()
            .setTextSize(
                resources.getDimensionPixelSize(R.dimen.runman_smile_content_text_size))
            .setBordersColor(resources.getColor(R.color.black))
            .setTextColorSourceType(SourceType.RES)
            .setTextColorImageResId(R.drawable.green_grad)
            .setBordersSize(
                resources.getDimensionPixelSize(R.dimen.runman_smile_content_text_borders))
            .setShadowColor(resources.getColor(R.color.black))
            .setShadowX(3).setShadowY(3).setMaxTextSize(5)
            .setTypeface(FontsUtils.getFanzhengchaocu())
            .setTraditionalChinese(false)
            .build())
        .setRunManSecondCountTextAttribute(new RunManTextAttribute.Builder()
            .setTextSize(
                resources.getDimensionPixelSize(R.dimen.runman_smile_content_text_size))
            .setBordersColor(resources.getColor(R.color.black))
            .setTextColorSourceType(SourceType.RES)
            .setTextColorImageResId(R.drawable.red_grad)
            .setBordersSize(
                resources.getDimensionPixelSize(R.dimen.runman_smile_content_text_borders))
            .setShadowColor(resources.getColor(R.color.black))
            .setShadowX(3).setShadowY(3).setMaxTextSize(4)
            .setMarginX(resources.getDimensionPixelSize(R.dimen.runman_smile_content_text_size) * 2)
            .setTypeface(FontsUtils.getFanzhengchaocu())
            .setMarginY(resources.getDimensionPixelSize(R.dimen.runman_second_content_mt))
            .setTraditionalChinese(false)
            .build())
        .setRunManPreviewAttribute(
            new RunManPreviewAttribute.Builder().setSourceType(SourceType.RES)
                .setImageResId(R.drawable.not_afraid).build())
        .setContent(resources.getString(R.string.not_afraid)).build();
  }

  private RunManAttribute getRunManAngryAttribute(Resources resources) {
    List<RunManIconAttribute> runManIconAttributes = new ArrayList<>();
    runManIconAttributes.add(new RunManIconAttribute.Builder()
        .setSourceType(SourceType.RES).setImageResId(R.drawable.angry)
        .setRunManIconAlignCountType(RunManIconAlignCountType.FIRST)
        .setRunManIconAlignHorizontalType(RunManIconAlignHorizontalType.LEFT)
        .setRunManIconAlignVerticalType(RunManIconAlignVerticalType.TOP)
        .setWidth(resources.getDimensionPixelSize(R.dimen.runman_angry_size))
        .setHeight(resources.getDimensionPixelSize(R.dimen.runman_angry_size))
        .setMarginVertical(resources.getDimensionPixelSize(R.dimen.runman_smile_content_text_size) * 4 / 5
            + resources.getDimensionPixelSize(R.dimen.runman_angry_size))
        .build());
    runManIconAttributes.add(new RunManIconAttribute.Builder()
        .setSourceType(SourceType.RES).setImageResId(R.drawable.angry)
        .setRunManIconAlignCountType(RunManIconAlignCountType.FIRST)
        .setRunManIconAlignHorizontalType(RunManIconAlignHorizontalType.RIGHT)
        .setRunManIconAlignVerticalType(RunManIconAlignVerticalType.BOTTOM)
        .setWidth(resources.getDimensionPixelSize(R.dimen.runman_angry_size))
        .setHeight(resources.getDimensionPixelSize(R.dimen.runman_angry_size))
        .setMarginVertical(resources.getDimensionPixelSize(R.dimen.runman_smile_content_text_size) * 4 / 5)
        .build());
    return new RunManAttribute.Builder()
        .setRunManFirstCountTextAttribute(new RunManTextAttribute.Builder()
            .setTextSize(
                resources.getDimensionPixelSize(R.dimen.runman_smile_content_text_size))
            .setTextColor(resources.getColor(R.color.runman_afraid_first_color))
            .setBordersColor(resources.getColor(R.color.black))
            .setBordersSize(
                resources.getDimensionPixelSize(R.dimen.runman_smile_content_text_borders))
            .setShadowColor(resources.getColor(R.color.black))
            .setShadowX(3).setShadowY(3).setMaxTextSize(12)
            .setTypeface(FontsUtils.getFanzhengchaocu())
            .setMarginX(resources.getDimensionPixelSize(R.dimen.runman_angry_size))
            .setMarginY(resources.getDimensionPixelSize(R.dimen.runman_angry_size))
            .setTextColorSourceType(SourceType.RES)
            .setTextColorImageResId(R.drawable.darkblue_grad)
            .setTraditionalChinese(false)
            .build())
        .setRunManPreviewAttribute(
            new RunManPreviewAttribute.Builder().setSourceType(SourceType.RES)
                .setImageResId(R.drawable.not_use).build())
        .setContent(resources.getString(R.string.angry))
        .setRunManIconAttributes(runManIconAttributes).build();
  }

  private RunManAttribute getRunManFreakAttribute(Resources resources) {
    return new RunManAttribute.Builder()
        .setRunManFirstCountTextAttribute(new RunManTextAttribute.Builder()
            .setTextSize(
                resources.getDimensionPixelSize(R.dimen.runman_smile_content_text_size))
            .setTextColor(resources.getColor(R.color.runman_afraid_first_color))
            .setBordersColor(resources.getColor(R.color.black))
            .setBordersSize(
                resources.getDimensionPixelSize(R.dimen.runman_smile_content_text_borders))
            .setShadowColor(resources.getColor(R.color.black))
            .setShadowX(3).setShadowY(3).setMaxTextSize(3)
            .setTypeface(FontsUtils.getFanzhengchaocu())
            .setTextColorSourceType(SourceType.RES)
            .setTextColorImageResId(R.drawable.textture_grad)
            .setTraditionalChinese(false)
            .build())
        .setRunManPreviewAttribute(
            new RunManPreviewAttribute.Builder().setSourceType(SourceType.RES)
                .setImageResId(R.drawable.freak).build())
        .setContent(resources.getString(R.string.freak)).build();
  }

  private RunManAttribute getRunManTroublesomeAttribute(Resources resources) {
    return new RunManAttribute.Builder()
        .setRunManFirstCountTextAttribute(new RunManTextAttribute.Builder()
            .setTextSize(
                resources.getDimensionPixelSize(R.dimen.runman_smile_content_text_size))
            .setTextColorSourceType(SourceType.RES)
            .setTextColorImageResId(R.drawable.red_grad)
            .setBordersColor(resources.getColor(R.color.black))
            .setBordersSize(
                resources.getDimensionPixelSize(R.dimen.runman_smile_content_text_borders))
            .setShadowColor(resources.getColor(R.color.black))
            .setShadowX(3).setShadowY(3).setMaxTextSize(4)
            .setTypeface(FontsUtils.getFanzhengchaocu())
            .setTraditionalChinese(false)
            .build())
        .setRunManSecondCountTextAttribute(new RunManTextAttribute.Builder()
            .setTextSize(
                resources.getDimensionPixelSize(R.dimen.runman_smile_content_text_size))
            .setTextColorSourceType(SourceType.RES)
            .setTextColorImageResId(R.drawable.yellow_grad)
            .setBordersColor(resources.getColor(R.color.black))
            .setBordersSize(
                resources.getDimensionPixelSize(R.dimen.runman_smile_content_text_borders))
            .setShadowColor(resources.getColor(R.color.black))
            .setShadowX(3).setShadowY(3).setMaxTextSize(6)
            .setMarginX(resources.getDimensionPixelSize(R.dimen.runman_smile_content_text_size) * 2)
            .setTypeface(FontsUtils.getFanzhengchaocu())
            .setMarginY(resources.getDimensionPixelSize(R.dimen.runman_second_content_mt))
            .setTraditionalChinese(false)
            .build())
        .setRunManPreviewAttribute(
            new RunManPreviewAttribute.Builder().setSourceType(SourceType.RES)
                .setImageResId(R.drawable.troublesome).build())
        .setContent(resources.getString(R.string.troublesome)).build();
  }

  private RunManAttribute getRunManNothingToSayAttribute(Resources resources) {
    VerticalTypefaceAttribute verticalTypefaceAttribute = new VerticalTypefaceAttribute();
    verticalTypefaceAttribute.setTextFont(FontType.FANZHENGCHAOCU);
    verticalTypefaceAttribute.setTraditionalChinese(false);
    verticalTypefaceAttribute.setMaxSize(2);
    verticalTypefaceAttribute.setAngle(15);
    verticalTypefaceAttribute.setTextSize(resources.getDimensionPixelSize(R.dimen.runman_nothing));
    verticalTypefaceAttribute.setBackgroundImageId(R.drawable.nothing_to_say);
    verticalTypefaceAttribute.setTextColor("#ffffff");
    verticalTypefaceAttribute.setBackgroundImageId(R.drawable.pinkbubble);
    return new RunManAttribute.Builder().setVertaicalTypefaceAttribute(verticalTypefaceAttribute)
        .setRunManPreviewAttribute(new RunManPreviewAttribute.Builder().setSourceType(SourceType.RES)
            .setImageResId(R.drawable.nothing_to_say).build())
        .setContent(resources.getString(R.string.nothing)).build();
  }

  private RunManAttribute getRunManIndifferentAttribute(Resources resources) {
    VerticalTypefaceAttribute verticalTypefaceAttribute = new VerticalTypefaceAttribute();
    verticalTypefaceAttribute.setTextFont(FontType.FANZHENGCHAOCU);
    verticalTypefaceAttribute.setTraditionalChinese(false);
    verticalTypefaceAttribute.setMaxSize(4);
    verticalTypefaceAttribute.setTextSize(resources.getDimensionPixelSize(R.dimen.runman_nothing));
    verticalTypefaceAttribute.setBackgroundImageId(R.drawable.blueback);
    verticalTypefaceAttribute.setTextColor("#10355c");
    TextShadow textShadow = new TextShadow(3, 0, 2, "#000000", 0.4f);
    verticalTypefaceAttribute.setTextShadow(textShadow);
    return new RunManAttribute.Builder().setVertaicalTypefaceAttribute(verticalTypefaceAttribute)
        .setRunManPreviewAttribute(new RunManPreviewAttribute.Builder().setSourceType(SourceType.RES)
            .setImageResId(R.drawable.indifferent).build())
        .setContent(resources.getString(R.string.indifferent)).build();
  }

  private RunManAttribute getRunManDisdainAttribute(Resources resources) {
    VerticalTypefaceAttribute verticalTypefaceAttribute = new VerticalTypefaceAttribute();
    verticalTypefaceAttribute.setTraditionalChinese(false);
    verticalTypefaceAttribute.setTextFont(FontType.FANZHENGCHAOCU);
    verticalTypefaceAttribute.setMaxSize(4);
    verticalTypefaceAttribute.setTextSize(resources.getDimensionPixelSize(R.dimen.runman_smile_content_text_size));
    verticalTypefaceAttribute.setBackgroundImageId(R.drawable.redback);
    verticalTypefaceAttribute.setTextColor("#ff2626");
    return new RunManAttribute.Builder().setVertaicalTypefaceAttribute(verticalTypefaceAttribute)
        .setRunManPreviewAttribute(new RunManPreviewAttribute.Builder().setSourceType(SourceType.RES)
            .setImageResId(R.drawable.disdain).build())
        .setContent(resources.getString(R.string.disdain)).build();
  }

  public List<RunManAttribute> getRunManAttributes() {
    return runManAttributes;
  }

  public List<RunManAttribute> getKangxiAttributes() {
    return kangxiAttributes;
  }
}
