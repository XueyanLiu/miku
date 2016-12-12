package com.biubiu.miku.util.video.action.subtitle.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.DynamicLayout;
import android.text.Layout;
import android.text.TextPaint;
import android.text.TextUtils;

import com.biubiu.miku.MikuApplication;
import com.biubiu.miku.R;
import com.biubiu.miku.util.FontsUtils;
import com.biubiu.miku.util.ImageUtils;
import com.biubiu.miku.util.video.action.RecordActionLocationTask;
import com.biubiu.miku.util.video.action.subtitle.Subtitle;
import com.biubiu.miku.util.video.action.subtitle.SubtitleBitmap;
import com.biubiu.miku.util.video.action.subtitle.SubtitleContent;
import com.biubiu.miku.util.video.action.subtitle.SubtitleGroupAttribute;

import java.util.ArrayList;
import java.util.List;

public class SubtitleUtils {
  public static final int SUBTITLE_PADDING = 20;

  public static SubtitleBitmap getSubtitleBitmap(SubtitleContent subtitleContent,
                                                 SubtitleGroupAttribute subtitleGroupAttribute, int videoRotation, int videoWidth,
                                                 int videoHeight) {
    switch (subtitleContent.getSubtitleType()) {
      case CARTOON_WHITE:
      case CARTOON_EN:
      case CARTOON_BLACK:
        return getCenterSubtitle(subtitleContent, subtitleGroupAttribute, videoRotation, videoWidth,
            videoHeight, true);
      case CARTOON_JP:
        return getCenterSubtitle(subtitleContent, subtitleGroupAttribute, videoRotation, videoWidth,
            videoHeight, false);
    }
    return null;
  }


  private static SubtitleBitmap getNormalSubtitle(SubtitleContent subtitleContent,
      SubtitleGroupAttribute subtitleGroupAttribute, int videoRotation, int videoWidth,
      int videoHeight) {
    float proportion = 1;
    switch (videoRotation) {
      case 0:
        proportion =
            (videoWidth - SUBTITLE_PADDING * 2) / (float) subtitleGroupAttribute.getWidth();
        break;
      case 90:
        proportion =
            (videoHeight - SUBTITLE_PADDING * 2) / (float) subtitleGroupAttribute.getWidth();
        break;
    }
    float textSize = MikuApplication.context.getResources()
        .getDimension(R.dimen.draw_subtitle_text) * proportion;
    float translateTextSize = MikuApplication.context.getResources()
        .getDimension(R.dimen.draw_translate_subtitle_text) * proportion;
    float translateTextMt = MikuApplication.context.getResources()
        .getDimension(R.dimen.translate_subtitle_text_mt) * proportion;
    Bitmap subtitleBitmap =
        Bitmap
            .createBitmap((int) (subtitleGroupAttribute.getWidth() * proportion),
                (int) (subtitleGroupAttribute.getHeight() * proportion + translateTextMt),
                Bitmap.Config.ARGB_8888); // 建立一个空的BItMap
    Canvas canvas = new Canvas(subtitleBitmap);// 初始化画布绘制的图像到icon上
    TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG
        | Paint.DEV_KERN_TEXT_FLAG);// 设置画笔
    textPaint.setDither(true); // 获取跟清晰的图像采样
    textPaint.setFilterBitmap(true);// 过滤一些
    textPaint.setAntiAlias(true);
    if (!TextUtils.isEmpty(subtitleContent.getTranslateContent())) {
      textPaint.setTextSize(textSize);// 字体大小
      textPaint.setTypeface(FontsUtils.getHuaKangWTypeface());// 采用默认的宽度
      textPaint.setColor(MikuApplication.context.getResources()
          .getColor(R.color.subtitle_text));// 采用的颜色
      textPaint.setShadowLayer(0f, 3f, 3f,
              MikuApplication.context.getResources().getColor(R.color.subtitle_shadow_text));
      DynamicLayout subtitleDynamicLayout =
          new DynamicLayout(subtitleContent.getContent(), textPaint,
              (int) (subtitleGroupAttribute.getWidth()
                  * proportion),
              Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
      canvas.save();
      canvas.translate(0, 0);
      subtitleDynamicLayout.draw(canvas);
      textPaint.setTextSize(translateTextSize);// 字体大小
      textPaint.setTypeface(FontsUtils.getHuaKangWTypeface());// 采用默认的宽度
      textPaint.setColor(MikuApplication.context.getResources()
          .getColor(R.color.translate_subtitle_text));// 采用的颜色
      textPaint.setShadowLayer(0f, 3f, 3f,
              MikuApplication.context.getResources().getColor(R.color.subtitle_shadow_text));
      DynamicLayout dynamicLayout =
          new DynamicLayout(subtitleContent.getTranslateContent(), textPaint,
              (int) (subtitleGroupAttribute.getWidth()
                  * proportion),
              Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
      canvas.save();
      canvas.translate(0, dynamicLayout.getHeight() + translateTextMt);
      dynamicLayout.draw(canvas);
    } else {
      textPaint.setTextSize(textSize);// 字体大小
      textPaint.setTypeface(FontsUtils.getHuaKangWTypeface());// 采用默认的宽度
      textPaint.setColor(MikuApplication.context.getResources()
          .getColor(R.color.subtitle_text));// 采用的颜色
      textPaint.setShadowLayer(0f, 3f, 3f,
              MikuApplication.context.getResources().getColor(R.color.subtitle_shadow_text));
      DynamicLayout dynamicLayout =
          new DynamicLayout(subtitleContent.getContent(), textPaint,
              (int) (subtitleGroupAttribute.getWidth()
                  * proportion),
              Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
      canvas.save();
      canvas.translate(0, 0);
      dynamicLayout.draw(canvas);
    }
    return new SubtitleBitmap(videoRotation == 0 ? subtitleBitmap : ImageUtils.adjustPhotoRotation(
        subtitleBitmap,
        360 - videoRotation), subtitleBitmap.getWidth(), subtitleBitmap.getHeight());
  }

  private static SubtitleBitmap getBlackSubtitle(SubtitleContent subtitleContent,
                                                 SubtitleGroupAttribute subtitleGroupAttribute, int videoRotation, int videoWidth,
                                                 int videoHeight) {
    int subtitleWidth = 0;
    int subtitleHeight = 0;
    float proportion = 1;
    switch (videoRotation) {
      case 0:
        proportion =
            videoWidth / (float) subtitleGroupAttribute.getWidth();
        subtitleWidth = videoWidth;
        subtitleHeight = videoHeight;
        break;
      case 90:
        proportion =
            videoHeight / (float) subtitleGroupAttribute.getWidth();
        subtitleWidth = videoHeight;
        subtitleHeight = videoWidth;
        break;
    }
    float textSize = MikuApplication.context.getResources()
        .getDimension(R.dimen.draw_subtitle_text) * proportion;
    float translateTextSize = MikuApplication.context.getResources()
        .getDimension(R.dimen.draw_translate_subtitle_text) * proportion;
    float translateTextMt = MikuApplication.context.getResources()
        .getDimension(R.dimen.translate_subtitle_text_mt) * proportion;
    Bitmap subtitleBitmap =
        Bitmap.createBitmap(subtitleWidth, subtitleHeight, Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(subtitleBitmap);// 初始化画布绘制的图像到icon上
    canvas.drawColor(Color.BLACK);
    TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG
        | Paint.DEV_KERN_TEXT_FLAG);// 设置画笔
    textPaint.setDither(true); // 获取跟清晰的图像采样
    textPaint.setFilterBitmap(true);// 过滤一些
    textPaint.setAntiAlias(true);
    if (!TextUtils.isEmpty(subtitleContent.getTranslateContent())) {
      textPaint.setTextSize(textSize);// 字体大小
      textPaint.setTypeface(FontsUtils.getHuaKangWTypeface());// 采用默认的宽度
      textPaint.setColor(MikuApplication.context.getResources()
          .getColor(R.color.subtitle_text));// 采用的颜色
      textPaint.setShadowLayer(0f, 3f, 3f,
              MikuApplication.context.getResources().getColor(R.color.subtitle_shadow_text));
      DynamicLayout subtitleDynamicLayout =
          new DynamicLayout(subtitleContent.getContent(), textPaint,
              (int) (subtitleGroupAttribute.getWidth()
                  * proportion),
              Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
      canvas.save();
      canvas.translate(SUBTITLE_PADDING,
          subtitleHeight - SUBTITLE_PADDING - subtitleGroupAttribute.getHeight());
      subtitleDynamicLayout.draw(canvas);
      textPaint.setTextSize(translateTextSize);// 字体大小
      textPaint.setTypeface(FontsUtils.getHuaKangWTypeface());// 采用默认的宽度
      textPaint.setColor(MikuApplication.context.getResources()
          .getColor(R.color.translate_subtitle_text));// 采用的颜色
      textPaint.setShadowLayer(0f, 3f, 3f,
              MikuApplication.context.getResources().getColor(R.color.subtitle_shadow_text));
      DynamicLayout dynamicLayout =
          new DynamicLayout(subtitleContent.getTranslateContent(), textPaint,
              (int) (subtitleGroupAttribute.getWidth()
                  * proportion),
              Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
      canvas.save();
      canvas.translate(0, dynamicLayout.getHeight() + translateTextMt);
      dynamicLayout.draw(canvas);
    } else {
      textPaint.setTextSize(textSize);// 字体大小
      textPaint.setTypeface(FontsUtils.getHuaKangWTypeface());// 采用默认的宽度
      textPaint.setColor(MikuApplication.context.getResources()
          .getColor(R.color.subtitle_text));// 采用的颜色
      textPaint.setShadowLayer(0f, 3f, 3f,
              MikuApplication.context.getResources().getColor(R.color.subtitle_shadow_text));
      DynamicLayout dynamicLayout =
          new DynamicLayout(subtitleContent.getContent(), textPaint,
              subtitleWidth - SUBTITLE_PADDING * 2,
              Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
      canvas.save();
      canvas.translate(SUBTITLE_PADDING,
          subtitleHeight - SUBTITLE_PADDING - subtitleGroupAttribute.getHeight());
      dynamicLayout.draw(canvas);
    }
    return new SubtitleBitmap(videoRotation == 0 ? subtitleBitmap : ImageUtils.adjustPhotoRotation(
        subtitleBitmap, 360 - videoRotation), subtitleBitmap.getWidth(),
        subtitleBitmap.getHeight());
  }

  private static SubtitleBitmap getCenterSubtitle(SubtitleContent subtitleContent,
      SubtitleGroupAttribute subtitleGroupAttribute, int videoRotation, int videoWidth,
      int videoHeight, boolean isBlack) {
    int subtitleWidth = 0;
    int subtitleHeight = 0;
    float proportion = 1;
    switch (videoRotation) {
      case 0:
        proportion =
            videoWidth / (float) subtitleGroupAttribute.getWidth();
        subtitleWidth = videoWidth;
        subtitleHeight = videoHeight;
        break;
      case 90:
        proportion =
            videoHeight / (float) subtitleGroupAttribute.getWidth();
        subtitleWidth = videoHeight;
        subtitleHeight = videoWidth;
        break;
    }
    float textSize = MikuApplication.context.getResources()
        .getDimension(R.dimen.draw_subtitle_text) * proportion;
    float translateTextSize = MikuApplication.context.getResources()
        .getDimension(R.dimen.draw_translate_subtitle_text) * proportion;
    float translateTextMt = MikuApplication.context.getResources()
        .getDimension(R.dimen.translate_subtitle_text_mt) * proportion;
    Bitmap subtitleBitmap =
        Bitmap.createBitmap(subtitleWidth, subtitleHeight, Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(subtitleBitmap);// 初始化画布绘制的图像到icon上
    if (isBlack) {
      canvas.drawColor(Color.BLACK);
    }
    TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG
        | Paint.DEV_KERN_TEXT_FLAG);// 设置画笔
    textPaint.setDither(true); // 获取跟清晰的图像采样
    textPaint.setFilterBitmap(true);// 过滤一些
    textPaint.setAntiAlias(true);
    if (!TextUtils.isEmpty(subtitleContent.getTranslateContent())) {
      textPaint.setTextSize(textSize);// 字体大小
      textPaint.setTypeface(FontsUtils.getHuaKangWTypeface());// 采用默认的宽度
      textPaint.setColor(MikuApplication.context.getResources()
          .getColor(R.color.subtitle_text));// 采用的颜色
      textPaint.setShadowLayer(0f, 3f, 3f,
              MikuApplication.context.getResources().getColor(R.color.subtitle_shadow_text));
      DynamicLayout subtitleDynamicLayout =
          new DynamicLayout(subtitleContent.getContent(), textPaint,
              (int) (subtitleGroupAttribute.getWidth()
                  * proportion),
              Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
      canvas.save();
      canvas.translate(SUBTITLE_PADDING,
          (subtitleHeight - subtitleGroupAttribute.getHeight()) / 2);
      subtitleDynamicLayout.draw(canvas);
      textPaint.setTextSize(translateTextSize);// 字体大小
      textPaint.setTypeface(FontsUtils.getHuaKangWTypeface());// 采用默认的宽度
      textPaint.setColor(MikuApplication.context.getResources()
          .getColor(R.color.translate_subtitle_text));// 采用的颜色
      textPaint.setShadowLayer(0f, 3f, 3f,
              MikuApplication.context.getResources().getColor(R.color.subtitle_shadow_text));
      DynamicLayout dynamicLayout =
          new DynamicLayout(subtitleContent.getTranslateContent(), textPaint,
              (int) (subtitleGroupAttribute.getWidth()
                  * proportion),
              Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
      canvas.save();
      canvas.translate(0, dynamicLayout.getHeight() + translateTextMt);
      dynamicLayout.draw(canvas);
    } else {
      textPaint.setTextSize(textSize);// 字体大小
      textPaint.setTypeface(FontsUtils.getHuaKangWTypeface());// 采用默认的宽度
      textPaint.setColor(MikuApplication.context.getResources()
          .getColor(R.color.subtitle_text));// 采用的颜色
      textPaint.setShadowLayer(0f, 3f, 3f,
              MikuApplication.context.getResources().getColor(R.color.subtitle_shadow_text));
      DynamicLayout dynamicLayout =
          new DynamicLayout(subtitleContent.getContent(), textPaint,
              subtitleWidth - SUBTITLE_PADDING * 2,
              Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
      canvas.save();
      canvas.translate(SUBTITLE_PADDING,
          (subtitleHeight - subtitleGroupAttribute.getHeight()) / 2);
      dynamicLayout.draw(canvas);
    }
    return new SubtitleBitmap(videoRotation == 0 ? subtitleBitmap : ImageUtils.adjustPhotoRotation(
        subtitleBitmap, 360 - videoRotation), subtitleBitmap.getWidth(),
        subtitleBitmap.getHeight());
  }


  public static List<SubtitleContent> matchSubtitle(List<Subtitle> subtitles,
                                                    Subtitle currentSubtitle,
                                                    int playSeek) {
    List<SubtitleContent> matchSubtitleContents = new ArrayList<>();
    s: for (Subtitle subtitle : subtitles) {
      for (RecordActionLocationTask recordActionLocationTask : subtitle
          .getRecordActionLocationTasks()) {
        if (isMatchSubtitle(playSeek, recordActionLocationTask)) {
          matchSubtitleContents.add(subtitle.getSubtitleContent());
          continue s;
        }
      }
    }
    if (currentSubtitle != null) {
      for (RecordActionLocationTask recordActionLocationTask : currentSubtitle
          .getRecordActionLocationTasks()) {
        if (isMatchSubtitle(playSeek, recordActionLocationTask)) {
          matchSubtitleContents.add(currentSubtitle.getSubtitleContent());
          break;
        }
      }
    }
    return matchSubtitleContents;
  }

  public static boolean isMatchSubtitle(int playSeek,
      RecordActionLocationTask recordActionLocationTask) {
    if (recordActionLocationTask.getStartPosition() < playSeek
        && recordActionLocationTask.getEndPosition() > playSeek) {
      return true;
    }
    return false;
  }

}
