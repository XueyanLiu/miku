package com.biubiu.miku.widget.customview;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.biubiu.miku.R;
import com.biubiu.miku.util.SystemUtils;
import com.biubiu.miku.util.video.action.subtitle.SubtitleContent;
import com.biubiu.miku.util.video.task.VideoContentTaskManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class JPSubtitleContentView extends SubtitleContentView {

  private final static int MAX_JP_SUBTITLE_TEXT_LENGTH = 15;
  private final static int MAX_JP_SUBTITLE_FIRST_CONTENT_TEXT_LENGTH = 10;
  @BindView(R.id.first_subtitle)
  VerticalTextView firstSubtitle;
  @BindView(R.id.second_subtitle)
  VerticalTextView secondSubtitle;
  @BindView(R.id.bottom_clips_icon)
  ImageView bottomClipsIcon;
  @BindView(R.id.top_clips_icon)
  ImageView topClipsIcon;
  @BindView(R.id.first_translate_subtitle)
  VerticalTextView firstTranslateSubtitle;
  @BindView(R.id.content_layout)
  RelativeLayout contentLayout;
  @BindView(R.id.second_subtitle_layout)
  View secondSubtitleLayout;
  @BindView(R.id.translate_subtitle_layout)
  LinearLayout translateSubtitleLayout;
  private SubtitleContent subtitleContent;

  public JPSubtitleContentView(Context context) {
    super(context);
    initViewData();
  }

  public JPSubtitleContentView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initViewData();
  }

  public JPSubtitleContentView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initViewData();
  }

  @Override
  protected void initViewData() {
    LayoutInflater inflater =
        (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.jp_subtitle_content_view, this, true);
    ButterKnife.bind(this);
    LayoutParams contentLayoutParams =
        (LayoutParams) contentLayout.getLayoutParams();
    contentLayoutParams.height =
        (int) (SystemUtils.getScreenWidthPx()
            / VideoContentTaskManager.getInstance().getCurrentContentTask().getVideoRatioWH());
    contentLayout.setLayoutParams(contentLayoutParams);
    super.initViewData();
  }

  public void setSubtitleContent(SubtitleContent subtitleContent,
      boolean isRecord) {
    setSubtitleContent(subtitleContent, isRecord, false);
  }

  @Override
  public void setSubtitleContent(SubtitleContent subtitleContent, boolean isRecord,
      boolean isUpdate) {
    if (isUpdate || this.subtitleContent == null || !subtitleContent.equals(this.subtitleContent)) {
      this.isRecord = isRecord;
      this.subtitleContent = subtitleContent;
      switch (subtitleContent.getSubtitleType()) {
        case JP_STYLE_BLUE:
          setTextColor(R.color.blue_subtitle);
          topClipsIcon.setBackgroundResource(R.drawable.subtitle_blue_top_clips);
          bottomClipsIcon.setBackgroundResource(R.drawable.subtitle_blue_bottom_clips);
          break;
        case JP_STYLE_BLACK:
          setTextColor(R.color.black);
          topClipsIcon.setBackgroundResource(R.drawable.subtitle_black_top_clips);
          bottomClipsIcon.setBackgroundResource(R.drawable.subtitle_black_bottom_clips);
          break;
        case JP_STYLE_YELLOW:
          setTextColor(R.color.yellow_subtitle);
          topClipsIcon.setBackgroundResource(R.drawable.subtitle_yellow_top_clips);
          bottomClipsIcon.setBackgroundResource(R.drawable.subtitle_yellow_bottom_clips);
          break;
        case JP_STYLE_ORIGIN:
          setTextColor(R.color.white);
          topClipsIcon.setBackgroundResource(R.drawable.subtitle_origin_top_clips);
          bottomClipsIcon.setBackgroundResource(R.drawable.subtitle_origin_bottom_clips);
          break;
        case JP_STYLE_ORANGE:
          setTextColor(R.color.orange_subtitle);
          topClipsIcon.setBackgroundResource(R.drawable.subtitle_orange_top_clips);
          bottomClipsIcon.setBackgroundResource(R.drawable.subtitle_orange_bottom_clips);
          break;
      }
      setContentText(subtitleContent);
      setTranslateContent(subtitleContent);
    }
  }

  private void setTextColor(int colorRes) {
    firstSubtitle.setTextColor(getResources().getColor(colorRes));
    firstTranslateSubtitle.setTextColor(getResources().getColor(colorRes));
    secondSubtitle.setTextColor(getResources().getColor(colorRes));
  }

  @Override
  protected void setTranslateContent(final SubtitleContent subtitleContent) {
    if (!TextUtils.isEmpty(subtitleContent.getTranslateContent())) {
      int translateHeight;
      LayoutParams translateSubtitleLayoutParams =
          (LayoutParams) translateSubtitleLayout.getLayoutParams();
      if (secondSubtitle.getVisibility() != View.VISIBLE) {
        translateHeight = (int) firstSubtitle.getContentHeight();
        translateSubtitleLayoutParams.addRule(RelativeLayout.ALIGN_TOP, R.id.top_clips_icon);
        translateSubtitleLayoutParams.addRule(RelativeLayout.LEFT_OF, R.id.top_clips_icon);
      } else {
        translateHeight = (int) secondSubtitle.getContentHeight();
        translateSubtitleLayoutParams.addRule(RelativeLayout.ALIGN_TOP,
            R.id.second_subtitle_layout);
        translateSubtitleLayoutParams.addRule(RelativeLayout.LEFT_OF, R.id.second_subtitle_layout);
      }
      translateSubtitleLayout.setLayoutParams(translateSubtitleLayoutParams);
      int translateSubtitleTextCount = translateHeight
          / getResources().getDimensionPixelSize(R.dimen.jp_translate_subtitle_content_height);
      String translateContent = subtitleContent.getTranslateContent();
      if (translateContent.length() <= translateSubtitleTextCount) {
        firstTranslateSubtitle.setVisibility(VISIBLE);
        firstTranslateSubtitle.setText(translateContent);
      } else {
        int newTranslateSubtitleCount =
            (int) Math.ceil(translateContent.length() / (float) translateSubtitleTextCount);
        List<VerticalTextView> verticalTextViews = new ArrayList<>();
        int jpTranslateSubtitleMr =
            getResources().getDimensionPixelSize(R.dimen.jp_translate_subtitle_mr);
        firstTranslateSubtitle.setVisibility(GONE);
        for (int i = 0; i < newTranslateSubtitleCount; i++) {
          VerticalTextView verticalTextView = new VerticalTextView(getContext());
          verticalTextView.setGravity(Gravity.CENTER_HORIZONTAL);
          verticalTextView.setTextColor(firstTranslateSubtitle.getTextColor());
          verticalTextView.setTextSize(firstTranslateSubtitle.getTextSize());
          verticalTextView.setTextHeight(firstTranslateSubtitle.getTextHeight());
          LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
              ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
          layoutParams.rightMargin = jpTranslateSubtitleMr;
          verticalTextView.setLayoutParams(layoutParams);
          int start = (i) * translateSubtitleTextCount;
          int end = (i) * translateSubtitleTextCount + translateSubtitleTextCount > translateContent
              .length()
                  ? translateContent.length()
                  : (i) * translateSubtitleTextCount + translateSubtitleTextCount;
          verticalTextView.setText(translateContent.substring(start, end));
          verticalTextViews.add(0, verticalTextView);
        }
        for (VerticalTextView verticalTextView : verticalTextViews) {
          translateSubtitleLayout.addView(verticalTextView);
        }
      }
    } else {
      firstTranslateSubtitle.setText("");
    }
  }

  private void setContentText(SubtitleContent subtitleContent) {
    String subtitleContentText = subtitleContent.getContent();
    if (subtitleContentText.length() > MAX_JP_SUBTITLE_TEXT_LENGTH) {
      subtitleContentText =
          subtitleContentText.substring(0, MAX_JP_SUBTITLE_TEXT_LENGTH);
    }
    LayoutParams layoutParams =
        (LayoutParams) bottomClipsIcon.getLayoutParams();
    layoutParams.addRule(RelativeLayout.BELOW, 0);
    if (subtitleContentText.length() > MAX_JP_SUBTITLE_FIRST_CONTENT_TEXT_LENGTH) {
      secondSubtitle.setVisibility(VISIBLE);
      LayoutParams secondLayoutParams =
          (LayoutParams) secondSubtitleLayout.getLayoutParams();
      int maxJpSubtitleFirstContentWrapTextLength =
          (int) Math.ceil(subtitleContentText.length() / 2f);
      String firstContentText =
          subtitleContentText.substring(0, maxJpSubtitleFirstContentWrapTextLength);
      firstSubtitle.setText(firstContentText);
      String secondContentText = subtitleContentText
          .substring(maxJpSubtitleFirstContentWrapTextLength, subtitleContentText.length());
      secondSubtitle.setText(secondContentText);
      layoutParams.addRule(RelativeLayout.BELOW, R.id.second_subtitle_layout);
      layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
      layoutParams.rightMargin = getResources().getDimensionPixelSize(R.dimen.clips_icon_mr);
      secondLayoutParams.addRule(ALIGN_RIGHT, R.id.bottom_clips_icon);
      secondLayoutParams.addRule(ALIGN_LEFT, R.id.bottom_clips_icon);
      secondSubtitleLayout.setLayoutParams(secondLayoutParams);
    } else {
      secondSubtitle.setVisibility(GONE);
      firstSubtitle.setText(subtitleContent.getContent());
      layoutParams.addRule(RelativeLayout.BELOW, R.id.first_subtitle_layout);
      layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
      layoutParams.rightMargin = 0;

    }
    bottomClipsIcon.setLayoutParams(layoutParams);
  }
}
