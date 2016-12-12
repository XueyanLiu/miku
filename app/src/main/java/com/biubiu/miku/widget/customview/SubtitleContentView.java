package com.biubiu.miku.widget.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.biubiu.miku.util.video.VideoUtils;
import com.biubiu.miku.util.video.action.ActionImageData;
import com.biubiu.miku.util.video.action.OnContentImageSaveSuccessListener;
import com.biubiu.miku.util.video.action.OnRecordLocationChangeListener;
import com.biubiu.miku.util.video.action.OnRecordStatusChangeListener;
import com.biubiu.miku.util.video.action.RecordStatus;
import com.biubiu.miku.util.video.action.subtitle.SubtitleContent;

import java.io.File;

abstract class SubtitleContentView extends RelativeLayout
    implements
      GestureDetector.OnGestureListener {
  protected boolean isRecord;
  private GestureDetector gestureDetector;
  protected RecordStatus recordStatus = RecordStatus.PREPARE;
  protected OnRecordStatusChangeListener onRecordStatusChangeListener;
  protected OnRecordLocationChangeListener onRecordLocationChangeListener;

  public SubtitleContentView(Context context) {
    super(context);
  }

  public SubtitleContentView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public SubtitleContentView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  protected void initViewData() {
    gestureDetector = new GestureDetector(getContext(), this);
  }

  abstract void setSubtitleContent(SubtitleContent subtitleContent, boolean isRecord,
                                   boolean isUpdate);

  abstract void setTranslateContent(final SubtitleContent subtitleContent);



  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (isRecord) {
      switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
          break;
        case MotionEvent.ACTION_MOVE:
          if (recordStatus == RecordStatus.RECORDING && onRecordLocationChangeListener != null) {
            onRecordLocationChangeListener.onRecordLocationChange(0, 0);
          }
          break;
        case MotionEvent.ACTION_CANCEL:
        case MotionEvent.ACTION_UP:
          if (onRecordStatusChangeListener != null && recordStatus != RecordStatus.PREPARE) {
            onRecordStatusChangeListener.onRecordStatusChange(RecordStatus.PREPARE, 0, 0);
          }
          recordStatus = RecordStatus.PREPARE;
          break;
      }
      gestureDetector.onTouchEvent(event);
      return true;
    } else {
      return false;
    }
  }

  @Override
  public boolean onDown(MotionEvent e) {
    return false;
  }

  @Override
  public void onShowPress(MotionEvent e) {
    recordStatus = RecordStatus.RECORDING;
    if (onRecordStatusChangeListener != null) {
      onRecordStatusChangeListener.onRecordStatusChange(recordStatus, 0, 0);
    }
  }

  @Override
  public boolean onSingleTapUp(MotionEvent e) {
    return false;
  }

  @Override
  public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
    return false;
  }

  @Override
  public void onLongPress(MotionEvent e) {}

  @Override
  public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
    return false;
  }

  public boolean isRecording() {
    return recordStatus == RecordStatus.RECORDING;
  }

  public void saveImage(String saveImageFilePath, String videoPath,
      OnContentImageSaveSuccessListener onContentImageSaveSuccessListener) {
    new Handler(Looper.getMainLooper()).post(() -> {
      File file = new File(saveImageFilePath);
      setDrawingCacheEnabled(true);
//      setBackgroundColor(Color.TRANSPARENT);
      setDrawingCacheBackgroundColor(Color.TRANSPARENT);
      setDrawingCacheQuality(DRAWING_CACHE_QUALITY_HIGH);
      buildDrawingCache();
      Bitmap bitmap = getDrawingCache();
      ActionImageData actionImageData =
          VideoUtils.saveActionToFile(getContext(), bitmap, file.getParent(), file.getName(), videoPath);
      setDrawingCacheEnabled(false);
      if (onContentImageSaveSuccessListener != null) {
        onContentImageSaveSuccessListener.onContentImageSaveSuccess(actionImageData);
      }
    });
  }

  public void setOnRecordStatusChangeListener(
      OnRecordStatusChangeListener onRecordStatusChangeListener) {
    this.onRecordStatusChangeListener = onRecordStatusChangeListener;
  }

  public void setOnRecordLocationChangeListener(
      OnRecordLocationChangeListener onRecordLocationChangeListener) {
    this.onRecordLocationChangeListener = onRecordLocationChangeListener;
  }
}
