package com.biubiu.miku.widget.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.biubiu.miku.R;
import com.biubiu.miku.util.SystemUtils;
import com.biubiu.miku.util.video.VideoUtils;
import com.biubiu.miku.util.video.action.ActionImageData;
import com.biubiu.miku.util.video.action.OnContentImageSaveSuccessListener;
import com.biubiu.miku.util.video.action.OnRecordLocationChangeListener;
import com.biubiu.miku.util.video.action.OnRecordStatusChangeListener;
import com.biubiu.miku.util.video.action.RecordLocation;
import com.biubiu.miku.util.video.action.RecordStatus;
import com.biubiu.miku.util.video.action.VideoActionParams;
import com.biubiu.miku.util.video.action.videoTag.BorderVideoTagContent;
import com.biubiu.miku.util.video.action.videoTag.DefaultVideoTagManager;
import com.biubiu.miku.util.video.action.videoTag.InnerEditTextParams;
import com.biubiu.miku.util.video.action.videoTag.InnerVideoTagContent;
import com.biubiu.miku.util.video.action.videoTag.VideoTagContent;
import com.biubiu.miku.util.video.action.videoTag.VideoTagManager;
import com.biubiu.miku.util.video.task.VideoContentTaskManager;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoTagContentView extends RelativeLayout
    implements
      GestureDetector.OnGestureListener {
  @BindView(R.id.root_view)
  RelativeLayout rootView;
  @BindView(R.id.tag_content)
  TextView tagContent;
  @BindView(R.id.video_tag_icon)
  ImageView videoTagIcon;
  @BindView(R.id.tag_content_layout)
  RelativeLayout tagContentLayout;
  @BindView(R.id.inner_tag_content)
  TextView innerTextView;
  @BindView(R.id.left_image)
  ImageView leftImageView;
  @BindView(R.id.right_image)
  ImageView rightImageView;
  @BindView(R.id.down_image)
  ImageView downImageView;
  @BindView(R.id.up_image)
  ImageView upImageView;
  @BindView(R.id.black_point)
  ImageView blackPoint;

  private VideoTagContent videoTagContent;
  private RecordLocation recordLocation;
  private GestureDetector gestureDetector;
  private RecordStatus recordStatus = RecordStatus.PREPARE;
  private OnRecordStatusChangeListener onRecordStatusChangeListener;
  private OnRecordLocationChangeListener onRecordLocationChangeListener;
  private VideoActionParams videoActionParams;
  private LayoutParams oldLeftViewParams;
  private LayoutParams oldRightViewParams;
  private LayoutParams oldContentParams;
  private LayoutParams oldUpViewParams;

  public VideoTagContentView(Context context) {
    super(context);
    initViewData();
  }

  public VideoTagContentView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initViewData();
  }

  public VideoTagContentView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initViewData();
  }

  private void initViewData() {
    LayoutInflater inflater =
        (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.video_tag_content_view, this, true);
    ButterKnife.bind(this);
    gestureDetector = new GestureDetector(getContext(), this);
    float videoRatioWH =
        VideoContentTaskManager.getInstance().getCurrentContentTask().getVideoRatioWH();
    if (recordLocation != null) {
      this.videoActionParams = recordLocation.getVideoActionParams();
    }
    LayoutParams contentLayoutParams =
        (LayoutParams) rootView.getLayoutParams();
    contentLayoutParams.height =
        (int) (SystemUtils.getScreenWidthPx() / videoRatioWH);
    rootView.setLayoutParams(contentLayoutParams);
  }

  public void setVideoTagContent(VideoTagContent videoTagContent, RecordLocation recordLocation,
      boolean isUpdate, boolean isPreview) {
    if (videoTagContent instanceof BorderVideoTagContent) {
      if (videoTagContent != this.videoTagContent || isUpdate) {
        tagContent.setText(videoTagContent.getContent());
        this.videoTagContent = videoTagContent;
        BorderVideoTagContent borderVideoTagContent = (BorderVideoTagContent) videoTagContent;
        videoTagIcon.setVisibility(View.VISIBLE);
        tagContent.setVisibility(View.VISIBLE);
        rightImageView.setVisibility(GONE);
        leftImageView.setVisibility(GONE);
        innerTextView.setVisibility(GONE);
        upImageView.setVisibility(GONE);
        downImageView.setVisibility(GONE);
        blackPoint.setVisibility(GONE);
        LayoutParams videoTagIconParams =
            new LayoutParams(SystemUtils.dpToPx(95), SystemUtils.dpToPx(52));

        LayoutParams tagContentParams =
            new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        switch (borderVideoTagContent.getVideoTagType()) {
          case LEFT_BOTTOM: {
            videoTagIcon.setBackgroundResource(R.drawable.video_tag_left_bottom);
            tagContentParams.topMargin = SystemUtils.dpToPx(52);
            int heightSpec = MeasureSpec.makeMeasureSpec(SystemUtils.getScreenHeightPx(),
                MeasureSpec.AT_MOST);
            int widthSpec = MeasureSpec.makeMeasureSpec(SystemUtils.getScreenWidthPx(),
                MeasureSpec.AT_MOST);
            tagContent.measure(widthSpec, heightSpec);
            videoTagIconParams.addRule(ALIGN_LEFT, R.id.tag_content);
            videoTagIconParams.leftMargin =
                tagContent.getMeasuredWidth() / 2 - SystemUtils.dpToPx(15);
            break;
          }
          case CENTER_BOTTOM:
            videoTagIconParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            tagContentParams.addRule(CENTER_HORIZONTAL);
            videoTagIcon.setBackgroundResource(R.drawable.video_tag_center_bottom);
            tagContentParams.topMargin = SystemUtils.dpToPx(52);
            break;
          case CENTER_UP:
            videoTagIconParams.addRule(RelativeLayout.BELOW, R.id.tag_content);
            videoTagIconParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            tagContentParams.addRule(CENTER_HORIZONTAL);
            videoTagIconParams.topMargin =
                getResources().getDimensionPixelSize(R.dimen.video_tag_icon_tb_m);
            videoTagIcon.setBackgroundResource(R.drawable.video_tag_center_up);
            break;
          case RIGHT_UP:
            videoTagIcon.setBackgroundResource(R.drawable.video_tag_right_up);
            int heightSpec = MeasureSpec.makeMeasureSpec(SystemUtils.getScreenHeightPx(),
                MeasureSpec.AT_MOST);
            int widthSpec = MeasureSpec.makeMeasureSpec(SystemUtils.getScreenWidthPx(),
                MeasureSpec.AT_MOST);
            videoTagIcon.measure(widthSpec, heightSpec);
            tagContent.measure(widthSpec, heightSpec);
            videoTagIconParams.topMargin = tagContent.getMeasuredHeight();
            tagContentParams.addRule(ALIGN_LEFT, R.id.video_tag_icon);
            tagContentParams.leftMargin =
                SystemUtils.dpToPx(95) - tagContent.getMeasuredWidth() / 2 - SystemUtils.dpToPx(15);
            break;
          default:
            break;
        }
        tagContent.setLayoutParams(tagContentParams);
        videoTagIcon.setLayoutParams(videoTagIconParams);
      }
    } else if (videoTagContent instanceof InnerVideoTagContent) {
      if (videoTagContent != this.videoTagContent) {
        this.videoTagContent = videoTagContent;
        InnerVideoTagContent innerVideoTagContent = (InnerVideoTagContent) videoTagContent;
        String prefix = innerVideoTagContent.getResPrefix();
        tagContent.setVisibility(GONE);
        videoTagIcon.setVisibility(GONE);
        rightImageView.setVisibility(VISIBLE);
        leftImageView.setVisibility(VISIBLE);
        innerTextView.setVisibility(VISIBLE);
        int[] resIds = innerVideoTagContent.getComponentResIds();
        innerTextView.setText(videoTagContent.getContent());
        leftImageView.setImageResource(resIds[0]);
        innerTextView.setBackgroundResource(resIds[1]);
        rightImageView.setImageResource(resIds[2]);
        InnerEditTextParams innerEditTextParams = innerVideoTagContent.getInnerEditTextParams();
        setInnerParams(innerEditTextParams);
        if (resIds[3] > 0) {
          upImageView.setVisibility(VISIBLE);
          upImageView.setBackgroundResource(resIds[3]);
        } else {
          upImageView.setVisibility(GONE);
        }
        if (resIds[4] > 0) {
          downImageView.setVisibility(VISIBLE);
          downImageView.setBackgroundResource(resIds[4]);
        } else {
          downImageView.setVisibility(GONE);
        }
        if (oldContentParams == null) {
          oldContentParams = (LayoutParams) innerTextView.getLayoutParams();
        }
        if (oldLeftViewParams == null) {
          oldLeftViewParams = (LayoutParams) leftImageView.getLayoutParams();
        }
        if (oldRightViewParams == null) {
          oldRightViewParams = (LayoutParams) rightImageView.getLayoutParams();
        }
        if (oldUpViewParams == null) {
          oldUpViewParams = (LayoutParams) upImageView.getLayoutParams();
        }
        int blackPointSize = getResources().getDimensionPixelSize(R.dimen.black_point_size);
        if (prefix.equals("classic_left")) {
          blackPoint.setVisibility(VISIBLE);
          LayoutParams layoutParams =
              new LayoutParams(blackPointSize, blackPointSize);
          layoutParams.addRule(ALIGN_LEFT, R.id.left_image);
          layoutParams.addRule(CENTER_VERTICAL);
          layoutParams.leftMargin = 0 - SystemUtils.dpToPx(3);
          blackPoint.setLayoutParams(layoutParams);
          innerVideoTagContent
              .setVideoTagImageData(DefaultVideoTagManager.getInstance().getViewTagImageData());
        } else if (prefix.equals("classic_right")) {
          LayoutParams layoutParams =
              new LayoutParams(blackPointSize, blackPointSize);
          layoutParams.addRule(ALIGN_RIGHT, R.id.right_image);
          layoutParams.rightMargin = 0 - SystemUtils.dpToPx(3);
          layoutParams.addRule(CENTER_VERTICAL);
          blackPoint.setLayoutParams(layoutParams);
          blackPoint.setVisibility(VISIBLE);
          innerVideoTagContent
              .setVideoTagImageData(DefaultVideoTagManager.getInstance().getViewTagImageData());
        } else if (prefix.equals("upper")) {
          innerVideoTagContent
              .setVideoTagImageData(DefaultVideoTagManager.getInstance().getViewTagImageData());

          LayoutParams blackpointLayoutParams =
              new LayoutParams(blackPointSize, blackPointSize);
          blackpointLayoutParams.addRule(CENTER_HORIZONTAL);
          blackPoint.setLayoutParams(blackpointLayoutParams);
          blackPoint.setVisibility(VISIBLE);

          LayoutParams upViewParams =
              (LayoutParams) upImageView.getLayoutParams();
          upViewParams.topMargin = blackPointSize / 2;

          LayoutParams contentParams =
              (LayoutParams) innerTextView.getLayoutParams();
          contentParams.addRule(BELOW, R.id.up_image);
          innerTextView.setLayoutParams(contentParams);

          LayoutParams leftViewParams =
              (LayoutParams) leftImageView.getLayoutParams();
          leftViewParams.addRule(BELOW, R.id.up_image);
          leftImageView.setLayoutParams(leftViewParams);

          LayoutParams rightViewParams =
              (LayoutParams) rightImageView.getLayoutParams();
          rightViewParams.addRule(BELOW, R.id.up_image);
          rightImageView.setLayoutParams(rightViewParams);
        } else if (prefix.equals("lower")) {
          blackPoint.setVisibility(VISIBLE);
          LayoutParams layoutParams =
              new LayoutParams(blackPointSize, blackPointSize);
          layoutParams.addRule(BELOW, R.id.down_image);
          layoutParams.topMargin = 0 - SystemUtils.dpToPx(10);
          layoutParams.addRule(CENTER_HORIZONTAL);
          blackPoint.setLayoutParams(layoutParams);
          innerVideoTagContent
              .setVideoTagImageData(DefaultVideoTagManager.getInstance().getViewTagImageData());
        } else if (prefix.equals("cool_left")) {
          blackPoint.setVisibility(VISIBLE);
          LayoutParams layoutParams =
              new LayoutParams(blackPointSize, blackPointSize);
          layoutParams.addRule(ALIGN_LEFT, R.id.left_image);
          layoutParams.addRule(ALIGN_TOP, R.id.left_image);
          layoutParams.addRule(CENTER_VERTICAL);
          layoutParams.leftMargin = 0 - SystemUtils.dpToPx(3);
          layoutParams.topMargin = 0 - SystemUtils.dpToPx(1);
          blackPoint.setLayoutParams(layoutParams);
          innerVideoTagContent
              .setVideoTagImageData(DefaultVideoTagManager.getInstance().getViewTagImageData());
        } else if (prefix.equals("cool_right")) {
          LayoutParams layoutParams =
              new LayoutParams(blackPointSize, blackPointSize);
          layoutParams.addRule(ALIGN_RIGHT, R.id.right_image);
          layoutParams.addRule(ALIGN_TOP, R.id.right_image);
          layoutParams.rightMargin = 0 - SystemUtils.dpToPx(3);
          layoutParams.topMargin = 0 - SystemUtils.dpToPx(1);
          blackPoint.setLayoutParams(layoutParams);
          blackPoint.setVisibility(VISIBLE);
          innerVideoTagContent
              .setVideoTagImageData(DefaultVideoTagManager.getInstance().getViewTagImageData());
        } else if (prefix.equals("white_left")) {
          blackPoint.setVisibility(VISIBLE);
          LayoutParams layoutParams =
              new LayoutParams(blackPointSize, blackPointSize);
          layoutParams.addRule(ALIGN_LEFT, R.id.left_image);
          layoutParams.addRule(ALIGN_TOP, R.id.left_image);
          blackPoint.setLayoutParams(layoutParams);
          layoutParams.leftMargin = SystemUtils.dpToPx(3);
          layoutParams.topMargin = SystemUtils.dpToPx(5) + 1;
          innerVideoTagContent
              .setVideoTagImageData(DefaultVideoTagManager.getInstance().getViewTagImageData());
        } else if (prefix.equals("white_right")) {
          LayoutParams layoutParams =
              new LayoutParams(blackPointSize, blackPointSize);
          layoutParams.addRule(ALIGN_RIGHT, R.id.right_image);
          layoutParams.addRule(ALIGN_TOP, R.id.right_image);
          blackPoint.setLayoutParams(layoutParams);
          blackPoint.setVisibility(VISIBLE);
          layoutParams.rightMargin = SystemUtils.dpToPx(3);
          layoutParams.topMargin = SystemUtils.dpToPx(5) + 1;
          innerVideoTagContent
              .setVideoTagImageData(DefaultVideoTagManager.getInstance().getViewTagImageData());
        } else {
          blackPoint.setVisibility(GONE);
          innerVideoTagContent.setVideoTagImageData(null);
        }
      }
    }
    if (isPreview) {
      tagContentLayout.setBackgroundResource(R.drawable.edit_frame_shape);
    } else {
      tagContentLayout.setBackgroundColor(Color.TRANSPARENT);
    }
    if (recordLocation != null && recordLocation != this.recordLocation) {
      this.recordLocation = recordLocation;
      scrollTo(-recordLocation.getOffsetX(), -recordLocation.getOffsetY());
    }
  }

  public void setInnerParams(InnerEditTextParams innerParams) {
    if (innerParams != null) {
      innerTextView.setTextSize(innerParams.getTextSize());
      innerTextView.setTextColor(innerParams.getTextColor());
      if (innerParams.getTypeface() != null) {
        innerTextView.setTypeface(innerParams.getTypeface());
      }
      ViewGroup.LayoutParams layoutParams = innerTextView.getLayoutParams();
      layoutParams.height = innerParams.getHeight();
      innerTextView.setLayoutParams(layoutParams);
      innerTextView.setPadding(0, innerParams.getPaddingTop(), 0, 0);

      leftImageView.getLayoutParams().height = innerParams.getHeight();
      rightImageView.getLayoutParams().height = innerParams.getHeight();
    } else {
      innerTextView.setTextSize(SystemUtils.dpToPx(6));
      innerTextView.setTextColor(getResources().getColor(R.color.white));
      innerTextView.setTypeface(Typeface.DEFAULT);
      ViewGroup.LayoutParams layoutParams = innerTextView.getLayoutParams();
      layoutParams.height = SystemUtils.dpToPx(VideoTagManager.DEFAULT_HEGITH);
      innerTextView.setLayoutParams(layoutParams);

      leftImageView.getLayoutParams().height = SystemUtils.dpToPx(VideoTagManager.DEFAULT_HEGITH);
      rightImageView.getLayoutParams().height = SystemUtils.dpToPx(VideoTagManager.DEFAULT_HEGITH);
    }
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
          Log.e("scroll", "x:" + (-getScrollX()) + "  y:" + (-getScrollY()));
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

  public void setOnRecordStatusChangeListener(
      OnRecordStatusChangeListener onRecordStatusChangeListener) {
    this.onRecordStatusChangeListener = onRecordStatusChangeListener;
  }

  public void setOnRecordLocationChangeListener(
      OnRecordLocationChangeListener onRecordLocationChangeListener) {
    this.onRecordLocationChangeListener = onRecordLocationChangeListener;
  }

  public boolean isRecording() {
    return recordStatus == RecordStatus.RECORDING;
  }


  public RecordLocation getRecordLocation() {
    return new RecordLocation(getScrollX(), getScrollY(), videoActionParams);
  }

  public void saveVideoTagImage(String saveImageFilePath, String videoPath,
      OnContentImageSaveSuccessListener onContentImageSaveSuccessListener) {
    new Handler(Looper.getMainLooper()).post(() -> {
      tagContentLayout.setDrawingCacheEnabled(true);
      tagContentLayout.setBackgroundColor(Color.TRANSPARENT);
      tagContentLayout.setDrawingCacheBackgroundColor(Color.TRANSPARENT);
      tagContentLayout.setDrawingCacheQuality(DRAWING_CACHE_QUALITY_HIGH);
      tagContentLayout.buildDrawingCache();
      Bitmap bitmap = tagContentLayout.getDrawingCache();
      File file = new File(saveImageFilePath);
      Bitmap bgBmp = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
      Canvas canvas = new Canvas(bgBmp);
      Paint paint = new Paint();
      paint.setAntiAlias(true);
      paint.setDither(true);
      paint.setFilterBitmap(true);
      canvas.drawBitmap(bitmap, (getWidth() - bitmap.getWidth()) / 2,
          (getHeight() - bitmap.getHeight()) / 2, paint);
      tagContentLayout.setDrawingCacheEnabled(false);
      ActionImageData actionImageData =
          VideoUtils.saveVideoTagToFile(bgBmp, file.getParent(), file.getName(), videoPath);
      if (onContentImageSaveSuccessListener != null) {
        onContentImageSaveSuccessListener.onContentImageSaveSuccess(actionImageData);
      }
    });
  }
}
