package com.biubiu.miku.widget.customview;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.biubiu.miku.R;
import com.biubiu.miku.util.FontsUtils;
import com.biubiu.miku.util.SystemUtils;
import com.biubiu.miku.util.video.action.subtitle.SubtitleContent;
import com.biubiu.miku.util.video.task.VideoContentTaskManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BackgroundSubtitleContentView extends SubtitleContentView {
  private SubtitleContent subtitleContent;
  @BindView(R.id.subtitle)
  TextView subtitleTextView;
  @BindView(R.id.translate_subtitle)
  TextView translateSubtitleTextView;
  @BindView(R.id.content_layout)
  LinearLayout contentLayout;
  @BindView(R.id.left)
  ImageView leftImageView;
  @BindView(R.id.right)
  ImageView rightImageView;
  @BindView(R.id.mid)
  LinearLayout midLayout;
  @BindView(R.id.parent_layout)
  RelativeLayout parentLayout;

  public BackgroundSubtitleContentView(Context context) {
    super(context);
    initViewData();
  }

  public BackgroundSubtitleContentView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initViewData();
  }

  public BackgroundSubtitleContentView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initViewData();
  }

  protected void initViewData() {
    LayoutInflater inflater =
        (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.background_subtitle_content_view, this, true);
    ButterKnife.bind(this);
    subtitleTextView.setTypeface(FontsUtils.getHuaKangWTypeface());
    translateSubtitleTextView.setTypeface(FontsUtils.getHuaKangWTypeface());
    LayoutParams contentLayoutParams =
        (LayoutParams) contentLayout.getLayoutParams();
    contentLayoutParams.height =
        (int) (SystemUtils.getScreenWidthPx()
            / VideoContentTaskManager.getInstance().getCurrentContentTask().getVideoRatioWH());
    contentLayout.setLayoutParams(contentLayoutParams);
    contentLayout.setBackgroundColor(Color.TRANSPARENT);
    super.initViewData();
  }

  public void setSubtitleContent(SubtitleContent subtitleContent,
      boolean isRecord) {
    setSubtitleContent(subtitleContent, isRecord, false);
  }

  @Override
  public void setSubtitleContent(SubtitleContent subtitleContent, boolean isRecord,
      boolean isUpdate) {
    if (isUpdate || subtitleContent != this.subtitleContent) {
      this.isRecord = isRecord;
      this.subtitleContent = subtitleContent;
      contentLayout.setVerticalGravity(Gravity.BOTTOM);
      subtitleTextView.setVisibility(VISIBLE);
      subtitleTextView.setText(subtitleContent.getContent());
      switch (subtitleContent.getSubtitleType()){
        case OFFICE_STORY_YELLOW:
        case OFFICE_STORY_BLUE:
        case OFFICE_STORY_RED:
          translateSubtitleTextView.setVisibility(VISIBLE);
          subtitleTextView.setTypeface(FontsUtils.getHanyiheili());
          translateSubtitleTextView.setTypeface(FontsUtils.getHanyiheili());
          setTranslateContent(subtitleContent);
          break;
        case LOVE_NOTE_LIGHT_YELLOW:
        case LOVE_NOTE_WHITE:
        case LOVE_NOTE_YELLOW:
          subtitleTextView.setTypeface(FontsUtils.getXiaoMaiTypeface());
          translateSubtitleTextView.setTypeface(FontsUtils.getXiaoMaiTypeface());
          break;
        default:
          break;
      }

      switch (subtitleContent.getSubtitleType()) {
        case OFFICE_STORY_BLUE:
          subtitleTextView.setTextColor(Color.WHITE);
          translateSubtitleTextView.setTextColor(Color.WHITE);
          setBackground(R.drawable.office_blue_left, R.drawable.office_blue_mid, R.drawable.office_blue_right);
          break;
        case OFFICE_STORY_YELLOW:
          setBackground(R.drawable.office_yellow_left, R.drawable.office_yellow_mid, R.drawable.office_yellow_right);
          break;
        case OFFICE_STORY_RED:
          subtitleTextView.setTextColor(Color.WHITE);
          translateSubtitleTextView.setTextColor(Color.WHITE);
          setBackground(R.drawable.office_red_left, R.drawable.office_red_mid, R.drawable.office_red_right);
          break;
        case LOVE_NOTE_LIGHT_YELLOW:
          subtitleTextView.setTextColor(getResources().getColor(R.color.love_note_yellow));
          translateSubtitleTextView.setTextColor(getResources().getColor(R.color.love_note_yellow));
          setTranslateContent(subtitleContent);
          setBackground(R.drawable.light_yellow_left, R.drawable.light_yellow_mid, R.drawable.light_yellow_right);
          parentLayout.setBackgroundResource(R.drawable.light_yellow_shadow);
          break;
        case LOVE_NOTE_YELLOW:
          subtitleTextView.setTextColor(getResources().getColor(R.color.love_note_yellow));
          translateSubtitleTextView.setTextColor(getResources().getColor(R.color.love_note_yellow));
          translateSubtitleTextView.setVisibility(VISIBLE);
          setTranslateContent(subtitleContent);
          setBackground(R.drawable.yellow_left, R.drawable.yellow_mid, R.drawable.yellow_right);
          parentLayout.setBackgroundResource(R.drawable.yellow_shadow);
          break;
        case LOVE_NOTE_WHITE:
          subtitleTextView.setTextColor(getResources().getColor(R.color.love_note_white));
          translateSubtitleTextView.setVisibility(GONE);
          setBackground(R.drawable.white_left, R.drawable.white_mid, R.drawable.white_right);
          parentLayout.setBackgroundResource(R.drawable.white_shadow);
          break;
        default:
          break;
      }
      parentLayout.setVisibility(VISIBLE);
    }
  }

  private void setBackground(int leftImageID,int midImageID,int rightImageId){
    leftImageView.setBackgroundResource(leftImageID);
    rightImageView.setBackgroundResource(rightImageId);
    midLayout.setBackgroundResource(midImageID);
  }

  @Override
  protected void setTranslateContent(final SubtitleContent subtitleContent) {
    if (TextUtils.isEmpty(subtitleContent.getTranslateContent())) {
      translateSubtitleTextView.setVisibility(GONE);
    } else {
      translateSubtitleTextView.setVisibility(VISIBLE);
      translateSubtitleTextView.setText(subtitleContent.getTranslateContent());
    }
  }
}
