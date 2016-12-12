package com.biubiu.miku.widget.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.biubiu.miku.R;
import com.biubiu.miku.util.FontsUtils;
import com.biubiu.miku.util.SystemUtils;
import com.biubiu.miku.util.video.VideoUtils;
import com.biubiu.miku.util.video.action.ActionImageData;
import com.biubiu.miku.util.video.action.OnContentImageSaveSuccessListener;
import com.biubiu.miku.util.video.action.OnRecordLocationChangeListener;
import com.biubiu.miku.util.video.action.OnRecordStatusChangeListener;
import com.biubiu.miku.util.video.action.RecordLocation;
import com.biubiu.miku.util.video.action.RecordStatus;
import com.biubiu.miku.util.video.action.chatBox.ChatBoxChild;
import com.biubiu.miku.util.video.action.chatBox.ChatBoxContent;
import com.biubiu.miku.util.video.task.VideoContentTaskManager;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatBoxContentView extends RelativeLayout
    implements
    GestureDetector.OnGestureListener {
  private ChatBoxContent chatBoxContent;
  @BindView(R.id.chat_child_bg)
  View chatChildBg;
  @BindView(R.id.chat_box_container)
  RelativeLayout chatBoxContainer;
  @BindView(R.id.chat_box_content)
  TextView chatBoxContentTextView;
  @BindView(R.id.content_layout)
  RelativeLayout contentLayout;
  private RecordLocation recordLocation;
  private GestureDetector gestureDetector;
  private RecordStatus recordStatus = RecordStatus.PREPARE;
  private OnRecordStatusChangeListener onRecordStatusChangeListener;
  private OnRecordLocationChangeListener onRecordLocationChangeListener;

  public ChatBoxContentView(Context context) {
    super(context);
    initViewData();
  }

  public ChatBoxContentView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initViewData();
  }

  public ChatBoxContentView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initViewData();
  }

  private void initViewData() {
    LayoutInflater inflater =
        (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.chat_box_action_layout, this, true);
    ButterKnife.bind(this);
    chatBoxContentTextView.setTypeface(FontsUtils.getHuaKangWTypeface());
    gestureDetector = new GestureDetector(getContext(), this);
    LayoutParams contentLayoutParams =
        (LayoutParams) contentLayout.getLayoutParams();
    contentLayoutParams.height =
        (int) (SystemUtils.getScreenWidthPx()
            / VideoContentTaskManager.getInstance().getCurrentContentTask().getVideoRatioWH());
    contentLayout.setLayoutParams(contentLayoutParams);
  }

  public void setChatBoxContent(ChatBoxContent chatBoxContent) {
    setChatBoxContent(chatBoxContent, null);
  }

  public void showChatBoxContent(ChatBoxContent chatBoxContent,RecordLocation recordLocation) {
    setChatBoxContent(chatBoxContent, recordLocation);
    chatBoxContainer.setBackgroundResource(R.drawable.edit_frame_shape);
  }

  public void setChatBoxContent(ChatBoxContent chatBoxContent, RecordLocation recordLocation) {
    if (chatBoxContent != this.chatBoxContent) {
      this.chatBoxContent = chatBoxContent;
      ChatBoxChild chatBoxChild = chatBoxContent.getChatBoxChild();
      LayoutParams chatChildBgLayoutParams =
          (LayoutParams) chatChildBg.getLayoutParams();
      chatChildBgLayoutParams.width = (int) chatBoxChild.getWidth();
      chatChildBgLayoutParams.height = (int) chatBoxChild.getHeight();
      chatChildBg.setLayoutParams(chatChildBgLayoutParams);

      LayoutParams editLayoutParams =
          (LayoutParams) chatBoxContentTextView.getLayoutParams();
      editLayoutParams.width = (int) chatBoxChild.getContentMaxWidth();
      editLayoutParams.height = (int) chatBoxChild.getContentMaxHeight();
      editLayoutParams.leftMargin = (int) chatBoxChild.getContentX();
      editLayoutParams.topMargin = (int) chatBoxChild.getContentY();
      chatBoxContentTextView.setLayoutParams(editLayoutParams);

      chatChildBg.setBackgroundResource(chatBoxContent.getChatBoxChild().getIconImageResId());
      chatBoxContentTextView.setTextColor(chatBoxChild.getContentColor());
      chatBoxContentTextView.setText(TextUtils.isEmpty(chatBoxContent.getContent())
          ? getResources().getString(R.string.mengmengda)
          : chatBoxContent.getContent());
    }
    chatBoxContainer.setBackgroundColor(Color.TRANSPARENT);
    if (recordLocation != null && recordLocation != this.recordLocation) {
      this.recordLocation = recordLocation;
      scrollTo(-recordLocation.getOffsetX(), -recordLocation.getOffsetY());
    }
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
  public void onLongPress(MotionEvent e) {
  }

  @Override
  public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
    return false;
  }


  public boolean isRecording() {
    return recordStatus == RecordStatus.RECORDING;
  }

  public ChatBoxContent getChatBoxContent() {
    return chatBoxContent;
  }

  public void saveImage(String saveImageFilePath, String videoPath,
                        OnContentImageSaveSuccessListener onContentImageSaveSuccessListener) {
    chatChildBg.setDrawingCacheEnabled(false);
    new Handler(Looper.getMainLooper()).post(() -> {
      chatChildBg.setDrawingCacheEnabled(true);
      chatChildBg.setDrawingCacheBackgroundColor(Color.TRANSPARENT);
      chatChildBg.setDrawingCacheQuality(DRAWING_CACHE_QUALITY_HIGH);
      chatChildBg.buildDrawingCache();
      Bitmap bitmap = chatChildBg.getDrawingCache();
      File file = new File(saveImageFilePath);
      Bitmap bgBmp = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
      Canvas canvas = new Canvas(bgBmp);
      Paint paint = new Paint();
      paint.setAntiAlias(true);
      paint.setDither(true);
      paint.setFilterBitmap(true);
      canvas.drawBitmap(bitmap, (getWidth() - bitmap.getWidth()) / 2, (getHeight() - bitmap.getHeight()) / 2, paint);
      ActionImageData actionImageData =
          VideoUtils.saveActionToFile(getContext(), bgBmp, file.getParent(), file.getName(), videoPath);
      if (onContentImageSaveSuccessListener != null) {
        onContentImageSaveSuccessListener.onContentImageSaveSuccess(actionImageData);
      }
    });
  }
}
