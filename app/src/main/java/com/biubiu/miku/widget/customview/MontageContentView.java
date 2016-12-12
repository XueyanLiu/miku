package com.biubiu.miku.widget.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.biubiu.miku.util.video.action.OnRecordLocationChangeListener;
import com.biubiu.miku.util.video.action.OnRecordStatusChangeListener;
import com.biubiu.miku.util.video.action.RecordLocation;
import com.biubiu.miku.util.video.action.RecordStatus;
import com.biubiu.miku.util.video.action.montage.MontageType;

public class MontageContentView extends RelativeLayout
    implements
      GestureDetector.OnGestureListener {
  private RecordLocation recordLocation;
  private GestureDetector gestureDetector;
  private RecordStatus recordStatus = RecordStatus.PREPARE;
  private OnRecordStatusChangeListener onRecordStatusChangeListener;
  private OnRecordLocationChangeListener onRecordLocationChangeListener;
  private MontageType montageActionType;

  public MontageContentView(Context context) {
    super(context);
    initViewData();
  }

  public MontageContentView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initViewData();
  }

  public MontageContentView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initViewData();
  }

  private void initViewData() {
    gestureDetector = new GestureDetector(getContext(), this);
  }

  public void setMontageType(MontageType montageActionType) {
    setMontageType(montageActionType, null);
  }

  public void setMontageType(MontageType montageActionType,
      RecordLocation recordLocation) {
    this.montageActionType = montageActionType;
    this.recordLocation = recordLocation;
  }

  public void setOnRecordStatusChangeListener(
      OnRecordStatusChangeListener onRecordStatusChangeListener) {
    this.onRecordStatusChangeListener = onRecordStatusChangeListener;
  }

  public void setOnRecordLocationChangeListener(
      OnRecordLocationChangeListener onRecordLocationChangeListener) {
    this.onRecordLocationChangeListener = onRecordLocationChangeListener;
  }

  float oldX;
  float oldY;

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (recordLocation != null) {
      switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
          oldX = event.getRawX();
          oldY = event.getRawY();
          break;
        case MotionEvent.ACTION_MOVE:
          float currentX = event.getRawX();
          float currentY = event.getRawY();
          scrollBy((int) (oldX - currentX), (int) (oldY - currentY));
          if (recordStatus == RecordStatus.RECORDING && onRecordLocationChangeListener != null) {
            onRecordLocationChangeListener.onRecordLocationChange(-getScrollX(), -getScrollY());
          }
          oldX = currentX;
          oldY = currentY;
          break;
        case MotionEvent.ACTION_CANCEL:
        case MotionEvent.ACTION_UP:
          if (onRecordStatusChangeListener != null && recordStatus != RecordStatus.PREPARE) {
            onRecordStatusChangeListener.onRecordStatusChange(RecordStatus.PREPARE, -getScrollX(),
                -getScrollY());
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
      onRecordStatusChangeListener.onRecordStatusChange(recordStatus, -getScrollX(), -getScrollY());
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
}
