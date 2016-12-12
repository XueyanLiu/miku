package com.biubiu.miku.widget.customview;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.biubiu.miku.R;
import com.biubiu.miku.util.FontsUtils;
import com.biubiu.miku.util.SystemUtils;
import com.biubiu.miku.util.video.action.subtitle.SubtitleContent;
import com.biubiu.miku.util.video.task.VideoContentTaskManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NormalSubtitleContentView extends SubtitleContentView {
  private SubtitleContent subtitleContent;
  @BindView(R.id.subtitle)
  TextView subtitleTextView;
  @BindView(R.id.translate_subtitle)
  TextView translateSubtitleTextView;
  @BindView(R.id.content_layout)
  LinearLayout contentLayout;

  public NormalSubtitleContentView(Context context) {
    super(context);
    initViewData();
  }

  public NormalSubtitleContentView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initViewData();
  }

  public NormalSubtitleContentView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initViewData();
  }

  protected void initViewData() {
    LayoutInflater inflater =
        (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.subtitle_content_view, this, true);
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
    if (isUpdate || subtitleContent != this.subtitleContent) {
      this.isRecord = isRecord;
      this.subtitleContent = subtitleContent;
      setBackgroundResource(R.color.transparent);
      contentLayout.setVerticalGravity(Gravity.BOTTOM);
      switch (subtitleContent.getSubtitleType()) {
        case CLASSIC_WHITE:
          subtitleTextView.setVisibility(VISIBLE);
          translateSubtitleTextView.setVisibility(GONE);
          subtitleTextView.setText(subtitleContent.getContent());
          break;
        case CLASSIC_BLACK:
          setBackgroundResource(R.color.black);
          subtitleTextView.setVisibility(VISIBLE);
          translateSubtitleTextView.setVisibility(GONE);
          subtitleTextView.setText(subtitleContent.getContent());
          contentLayout.setVerticalGravity(Gravity.CENTER);
          break;
        case CLASSIC_EN:
          subtitleTextView.setVisibility(VISIBLE);
          translateSubtitleTextView.setVisibility(GONE);
          subtitleTextView.setText(subtitleContent.getContent());
          setTranslateContent(subtitleContent);
          break;
        case CLASSIC_JP:
          subtitleTextView.setVisibility(VISIBLE);
          setTranslateContent(subtitleContent);
          subtitleTextView.setText(subtitleContent.getContent());
          contentLayout.setVerticalGravity(Gravity.CENTER);
          break;
        case CARTOON_WHITE:
          subtitleTextView.setTypeface(FontsUtils.getHuaKangWTypeface());
          translateSubtitleTextView.setTypeface(FontsUtils.getHuaKangWTypeface());
          subtitleTextView.setVisibility(VISIBLE);
          translateSubtitleTextView.setVisibility(GONE);
          subtitleTextView.setText(subtitleContent.getContent());
          break;
        case CARTOON_EN:
          subtitleTextView.setTypeface(FontsUtils.getHuaKangWTypeface());
          translateSubtitleTextView.setTypeface(FontsUtils.getHuaKangWTypeface());
          subtitleTextView.setVisibility(VISIBLE);
          translateSubtitleTextView.setVisibility(GONE);
          subtitleTextView.setText(subtitleContent.getContent());
          setTranslateContent(subtitleContent);
          break;
        case CARTOON_BLACK:
          subtitleTextView.setTypeface(FontsUtils.getHuaKangWTypeface());
          translateSubtitleTextView.setTypeface(FontsUtils.getHuaKangWTypeface());
          setBackgroundResource(R.color.black);
          subtitleTextView.setVisibility(VISIBLE);
          translateSubtitleTextView.setVisibility(GONE);
          subtitleTextView.setText(subtitleContent.getContent());
          contentLayout.setVerticalGravity(Gravity.CENTER);
          break;
        case CARTOON_JP:
          subtitleTextView.setTypeface(FontsUtils.getHuaKangWTypeface());
          translateSubtitleTextView.setTypeface(FontsUtils.getHuaKangWTypeface());
          subtitleTextView.setVisibility(VISIBLE);
          setTranslateContent(subtitleContent);
          subtitleTextView.setText(subtitleContent.getContent());
          contentLayout.setVerticalGravity(Gravity.CENTER);
          break;
      }
    }
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
