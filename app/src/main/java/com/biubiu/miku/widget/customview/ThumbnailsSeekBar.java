package com.biubiu.miku.widget.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.SeekBar;

import com.biubiu.miku.R;

public class ThumbnailsSeekBar extends SeekBar {
  public ThumbnailsSeekBar(Context context) {
    super(context);
    initViewData(null);
  }

  public ThumbnailsSeekBar(Context context, AttributeSet attrs) {
    super(context, attrs);
    initViewData(attrs);
  }

  public ThumbnailsSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initViewData(attrs);
  }

  private void initViewData(AttributeSet attrs) {
    TypedArray typedArray =
        getContext().obtainStyledAttributes(attrs, R.styleable.ThumbnailsSeekBar);
    int w = typedArray.getDimensionPixelSize(R.styleable.ThumbnailsSeekBar_thumb_width, -1);
    int h = typedArray.getDimensionPixelSize(R.styleable.ThumbnailsSeekBar_thumb_height, -1);
    int thumbResId = typedArray.getResourceId(R.styleable.ThumbnailsSeekBar_thumb_image, -1);
    typedArray.recycle();
    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), thumbResId);
    Bitmap thumb = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(thumb);
    canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()),
        new Rect(0, 0, thumb.getWidth(), thumb.getHeight()), null);
    Drawable drawable = new BitmapDrawable(getResources(), thumb);
    setThumb(drawable);
  }
}
