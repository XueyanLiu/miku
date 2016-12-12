package com.biubiu.miku.widget.customview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.annimon.stream.Stream;
import com.biubiu.miku.R;
import com.biubiu.miku.util.SystemUtils;
import com.biubiu.miku.util.sound.SoundPoolPlayer;
import com.biubiu.miku.util.video.action.OnRecordLocationChangeListener;
import com.biubiu.miku.util.video.action.OnRecordStatusChangeListener;
import com.biubiu.miku.util.video.action.RecordLocation;
import com.biubiu.miku.util.video.action.RecordStatus;
import com.biubiu.miku.util.video.action.sticker.DefaultStickerManager;
import com.biubiu.miku.util.video.action.sticker.StickerImageData;
import com.biubiu.miku.util.video.task.VideoContentTaskManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StickerContentView extends RelativeLayout
    implements
    GestureDetector.OnGestureListener {
  @BindView(R.id.sticker_parent_layout)
  RelativeLayout stickerParentLayout;
  @BindView(R.id.sticker_image_view)
  StickerImageView stickerImageView;
  @BindView(R.id.fullscreen_sticker_image_view)
  StickerImageView fullScreenStickerImageView;
  @BindView(R.id.content_layout)
  RelativeLayout contentLayout;
  private RecordLocation recordLocation;
  private GestureDetector gestureDetector;
  private RecordStatus recordStatus = RecordStatus.PREPARE;
  private OnRecordStatusChangeListener onRecordStatusChangeListener;
  private OnRecordLocationChangeListener onRecordLocationChangeListener;
  private StickerImageData stickerImageData;
  private boolean isRuning = false;
  private boolean fullScreen = false;

  public StickerContentView(Context context) {
    super(context);
    initViewData();
  }

  public StickerContentView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initViewData();
  }

  public StickerContentView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initViewData();
  }

  private void initViewData() {
    LayoutInflater inflater =
        (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.sticker_action_layout, this, true);
    ButterKnife.bind(this);
    gestureDetector = new GestureDetector(getContext(), this);
    LayoutParams contentLayoutParams =
        (LayoutParams) contentLayout.getLayoutParams();
    contentLayoutParams.height =
        (int) (SystemUtils.getScreenWidthPx()
            / VideoContentTaskManager.getInstance().getCurrentContentTask().getVideoRatioWH());
    contentLayout.setLayoutParams(contentLayoutParams);
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
          if (!fullScreen) {
            scrollBy((int) (oldX - currentX), (int) (oldY - currentY));
          }
          if (recordStatus == RecordStatus.RECORDING && onRecordLocationChangeListener != null) {
            if (fullScreen) {
              onRecordLocationChangeListener.onRecordLocationChange(0, 0);
            } else {
              onRecordLocationChangeListener.onRecordLocationChange(-getScrollX(), -getScrollY());
            }
          }
          oldX = currentX;
          oldY = currentY;
          break;
        case MotionEvent.ACTION_CANCEL:
        case MotionEvent.ACTION_UP:
          if (onRecordStatusChangeListener != null && recordStatus != RecordStatus.PREPARE) {
            if (fullScreen) {
              onRecordStatusChangeListener.onRecordStatusChange(RecordStatus.PREPARE, 0, 0);
            } else {
              onRecordStatusChangeListener.onRecordStatusChange(RecordStatus.PREPARE, -getScrollX(),
                  -getScrollY());
            }
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
      if (fullScreen) {
        onRecordStatusChangeListener.onRecordStatusChange(recordStatus, 0, 0);
      } else {
        onRecordStatusChangeListener.onRecordStatusChange(recordStatus, -getScrollX(), -getScrollY());
      }
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
  public void onLongPress(MotionEvent e) {
  }

  @Override
  public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
    return false;
  }


  public void setStickerImageData(StickerImageData stickerImageData) {
    setStickerImageData(stickerImageData, null);
  }

  public void setStickerImageData(StickerImageData stickerImageData,
                                  RecordLocation recordLocation) {
    fullScreen = false;
    Stream.of(DefaultStickerManager.getInstance().getStickerImageGroupList()).forEach(stickerImageGroup -> {
      if (Stream.of(stickerImageGroup.getStickerImageDataList()).anyMatch(imageData -> imageData.equals(stickerImageData))) {
        if (stickerImageGroup.getGroupName().equals("气氛")) {
          fullScreen = true;
        }
      }
    });
    if (stickerImageData != this.stickerImageData) {
      this.stickerImageData = stickerImageData;
      if (fullScreen) {
        stickerParentLayout.setVisibility(GONE);
        fullScreenStickerImageView.setVisibility(VISIBLE);
        if (stickerImageData != null) {
          fullScreenStickerImageView.setOnFramePlaySeekChangeListener(seek -> {
          });
          fullScreenStickerImageView.setStickerImageData(stickerImageData, false);
        }
      } else {
        stickerParentLayout.setVisibility(VISIBLE);
        fullScreenStickerImageView.setVisibility(GONE);
        if (stickerImageData != null) {
          stickerImageView.setOnFramePlaySeekChangeListener(seek -> {
          });
          stickerImageView.setStickerImageData(stickerImageData, false);
        }
      }
    }
    if (getVisibility() != VISIBLE) {
      playSound();
    }
    if (recordLocation != null && recordLocation != this.recordLocation) {
      this.recordLocation = recordLocation;
      scrollTo(-recordLocation.getOffsetX(), -recordLocation.getOffsetY());
    }
  }

  public void playSound() {
    int soundId = stickerImageData.getSoundId();
    if (soundId > 0) {
      SoundPoolPlayer.getInstance().playSound(soundId);
    }
  }

  public void stopSound() {
    int soundId = stickerImageData.getSoundId();
    if (soundId > 0) {
      SoundPoolPlayer.getInstance().stop(soundId);
    }
  }

  public boolean isRecording() {
    return recordStatus == RecordStatus.RECORDING;
  }

  public void setOnRecordStatusChangeListener(
      OnRecordStatusChangeListener onRecordStatusChangeListener) {
    this.onRecordStatusChangeListener = onRecordStatusChangeListener;
  }

  public void setOnRecordLocationChangeListener(
      OnRecordLocationChangeListener onRecordLocationChangeListener) {
    this.onRecordLocationChangeListener = onRecordLocationChangeListener;
    if (this.onRecordLocationChangeListener == null) {
      stickerParentLayout.setBackgroundResource(R.drawable.edit_frame_shape);
    } else {
      stickerParentLayout.setBackgroundColor(Color.TRANSPARENT);
    }
  }

  public void setIsRuning(boolean isRuning) {
    this.isRuning = isRuning;
  }

  public boolean isRuning() {
    return isRuning;
  }
}
