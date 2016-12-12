package com.biubiu.miku.widget.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.biubiu.miku.R;

public class VerticalTextView extends LinearLayout {
  private float textSize = getResources().getDimension(R.dimen.jp_subtitle_content_size);
  private float textHeight = getResources().getDimension(R.dimen.jp_subtitle_content_height);
  private int textColor = getResources().getColor(R.color.white);
  private String text;

  public VerticalTextView(Context context) {
    super(context);
    initView(null);
  }

  public VerticalTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initView(attrs);
  }

  public VerticalTextView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initView(attrs);
  }

  {
    setOrientation(LinearLayout.VERTICAL);
    setGravity(Gravity.CENTER_HORIZONTAL);
  }

  private void initView(AttributeSet attrs) {
    if (attrs != null) {
      TypedArray typedArray =
          getContext().obtainStyledAttributes(attrs, R.styleable.VerticalTextView);
      textColor = typedArray.getColor(R.styleable.VerticalTextView_text_color,
          getResources().getColor(R.color.white));
      textSize = typedArray.getDimension(R.styleable.VerticalTextView_text_size,
          getResources().getDimension(R.dimen.jp_subtitle_content_size));
      textHeight = typedArray.getDimension(R.styleable.VerticalTextView_text_height,
          getResources().getDimension(R.dimen.jp_subtitle_content_height));
      typedArray.recycle();
    }
  }

  public void setText(String text) {
    this.text = text;
    addText();
  }

  private void addText() {
    removeAllViews();
    if (text != null) {
      char[] chara = text.toCharArray();
      for (int i = 0; i < chara.length; i++) {
        LayoutParams layoutParams =
            new LayoutParams(LayoutParams.WRAP_CONTENT, (int) textHeight);
        TextView xiaoMaiTextView = new TextView(getContext());
        xiaoMaiTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        xiaoMaiTextView.setTextColor(textColor);
        xiaoMaiTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        xiaoMaiTextView.setText(text.substring(i, i + 1));
        xiaoMaiTextView.setLayoutParams(layoutParams);
        addView(xiaoMaiTextView);
      }
    }
  }

  public void setTextColor(int textColor) {
    this.textColor = textColor;
  }

  public int getTextColor() {
    return textColor;
  }

  public void setTextSize(float textSize) {
    this.textSize = textSize;
  }

  public float getTextSize() {
    return textSize;
  }

  public float getTextHeight() {
    return textHeight;
  }

  public void setTextHeight(float textHeight) {
    this.textHeight = textHeight;
  }

  public float getContentHeight() {
    return textHeight * getChildCount();
  }
}
