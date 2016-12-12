package com.biubiu.miku.widget.customview;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by hanxiao on 15-10-23.
 */
public class NewVerticalTextView extends TextView {
  public NewVerticalTextView(Context context) {
    super(context);
  }

  public NewVerticalTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public NewVerticalTextView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public void setVerticalText(String text) {
    if (!TextUtils.isEmpty(text)) {
      String tempText = "";
      for (int i = 0; i < text.length(); i++) {
        tempText += text.substring(i, i + 1);
        if (i < text.length() - 1) {
          tempText += "\n";
        }
      }
      setText(tempText);
    }
  }
}
