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
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.biubiu.miku.constant.FontType;
import com.biubiu.miku.R;
import com.biubiu.miku.model.TextShadow;
import com.biubiu.miku.model.VerticalTypefaceAttribute;
import com.biubiu.miku.util.FontsUtils;
import com.biubiu.miku.util.SystemUtils;
import com.biubiu.miku.util.sound.SoundPoolPlayer;
import com.biubiu.miku.util.video.VideoUtils;
import com.biubiu.miku.util.video.action.ActionImageData;
import com.biubiu.miku.util.video.action.OnContentImageSaveSuccessListener;
import com.biubiu.miku.util.video.action.OnRecordLocationChangeListener;
import com.biubiu.miku.util.video.action.OnRecordStatusChangeListener;
import com.biubiu.miku.util.video.action.RecordLocation;
import com.biubiu.miku.util.video.action.RecordStatus;
import com.biubiu.miku.util.video.action.runMan.RunManAttribute;
import com.biubiu.miku.util.video.action.runMan.RunManBackgroundAttribute;
import com.biubiu.miku.util.video.action.runMan.RunManContent;
import com.biubiu.miku.util.video.action.runMan.RunManIconAlignCountType;
import com.biubiu.miku.util.video.action.runMan.RunManIconAlignHorizontalType;
import com.biubiu.miku.util.video.action.runMan.RunManIconAlignVerticalType;
import com.biubiu.miku.util.video.action.runMan.RunManIconAttribute;
import com.biubiu.miku.util.video.action.runMan.RunManIconInputAttribute;
import com.biubiu.miku.util.video.action.runMan.RunManSoundAttribute;
import com.biubiu.miku.util.video.action.runMan.RunManTextAttribute;
import com.biubiu.miku.util.video.task.VideoContentTaskManager;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RunManContentView extends RelativeLayout
        implements
        GestureDetector.OnGestureListener {

    @BindView(R.id.content_layout)
    RelativeLayout contentLayout;
    @BindView(R.id.show_content_layout)
    RelativeLayout showContentLayout;
    @BindView(R.id.show_content_layout_frame)
    RelativeLayout contentFrameLayout;

    private RunManAttribute runManAttribute;
    private RecordLocation recordLocation;
    private GestureDetector gestureDetector;
    private RecordStatus recordStatus = RecordStatus.PREPARE;
    private OnRecordStatusChangeListener onRecordStatusChangeListener;
    private OnRecordLocationChangeListener onRecordLocationChangeListener;
    private RunManContent runManContent;
    private TextView firstrainbowTextView;
    private TextView editFristRainbowTextView;

    public RunManContentView(Context context) {
        super(context);
        initViewData();
    }

    public RunManContentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViewData();
    }

    public RunManContentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViewData();
    }

    private void initViewData() {
        LayoutInflater inflater =
                (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.run_man_content_layout, this, true);
        ButterKnife.bind(this);
        gestureDetector = new GestureDetector(getContext(), this);
        LayoutParams contentLayoutParams =
                (LayoutParams) contentLayout.getLayoutParams();
        contentLayoutParams.height =
                (int) (SystemUtils.getScreenWidthPx()
                        / VideoContentTaskManager.getInstance().getCurrentContentTask().getVideoRatioWH());
        contentLayout.setLayoutParams(contentLayoutParams);
    }

    public void setRunManContent(RunManContent runManContent, RecordLocation recordLocation) {
        setRunManContent(runManContent, recordLocation, false, false);
        contentFrameLayout.setBackgroundResource(R.drawable.edit_frame_shape);
    }

    public void setRunManContent(RunManContent runManContent, RecordLocation recordLocation,
                                 boolean isUpdate, boolean isPlaySound) {
        if (this.runManContent == null || isUpdate || !runManContent.equals(this.runManContent)) {
            this.runManContent = runManContent;
            showContentLayout.removeAllViews();
            showContentLayout.setBackground(null);
            if (runManContent.getRunManAttribute().getVertaicalTypefaceAttribute() == null) {
                firstrainbowTextView = new TextView(getContext());
                firstrainbowTextView.setId(R.id.runman_first_count_content);
                editFristRainbowTextView = new TextView(getContext());
                editFristRainbowTextView.setId(R.id.edit_runman_first_count_contet);
                runManAttribute = runManContent.getRunManAttribute();
                RunManBackgroundAttribute runManBackgroundAttribute =
                        runManAttribute.getRunManBackgroundAttribute();
                RunManTextAttribute firstRunManTextAttribute =
                        runManAttribute.getRunManFirstCountTextAttribute();
                RunManTextAttribute secondRunManTextAttribute =
                        runManAttribute.getRunManSecondCountTextAttribute();
                List<RunManIconAttribute> runManIconAttributes = runManAttribute.getRunManIconAttributes();
                setRunManBackground(runManBackgroundAttribute, showContentLayout);
                setRunManIcons(runManIconAttributes, showContentLayout, R.id.runman_first_count_content);
                setRunManText(firstRunManTextAttribute, secondRunManTextAttribute,
                        TextUtils.isEmpty(runManContent.getTextContent()) ? runManAttribute.getContent()
                                : runManContent.getTextContent(), firstrainbowTextView, showContentLayout, R.id.runman_first_count_content);
            } else {
                NewVerticalTextView verticalTextview = new NewVerticalTextView(getContext());
                verticalTextview.setGravity(Gravity.CENTER);
                showContentLayout.addView(verticalTextview);
                NewVerticalTextView editVerticalTextView = new NewVerticalTextView(getContext());
                editVerticalTextView.setGravity(Gravity.CENTER);
                RunManAttribute runManAttribute = runManContent.getRunManAttribute();
                VerticalTypefaceAttribute verticalTypefaceAttribute = runManAttribute.getVertaicalTypefaceAttribute();
                setVerticalTextviewConfig(verticalTextview, runManContent, verticalTypefaceAttribute);
                setVerticalTextviewConfig(editVerticalTextView, runManContent, verticalTypefaceAttribute);
                verticalTextview.setVisibility(VISIBLE);
            }
        }
        if (isPlaySound) {
            playSound();
        }
        contentFrameLayout.setBackgroundColor(Color.TRANSPARENT);
        if (recordLocation != null && recordLocation != this.recordLocation) {
            this.recordLocation = recordLocation;
            scrollTo(-recordLocation.getOffsetX(), -recordLocation.getOffsetY());
        }
    }

    private void setVerticalTextviewConfig(NewVerticalTextView verticalTextview,
                                           RunManContent runManContent,
                                           VerticalTypefaceAttribute verticalTypefaceAttribute) {
        verticalTextview.setMaxLines(verticalTypefaceAttribute.getMaxSize());
        verticalTextview.setRotation(0 - verticalTypefaceAttribute.getAngle());
        verticalTextview.setTextSize(TypedValue.COMPLEX_UNIT_PX, verticalTypefaceAttribute.getTextSize());
        verticalTextview.setTextColor(Color.parseColor(verticalTypefaceAttribute.getTextColor()));

        if (verticalTypefaceAttribute.getTextFont().equals(FontType.FANZHENGCHAOCU)) {
            verticalTextview.setTypeface(FontsUtils.getFanzhengchaocu());
        }

        if (verticalTypefaceAttribute.getBackgroundImageId() > 0) {
            verticalTextview.setBackgroundResource(verticalTypefaceAttribute.getBackgroundImageId());
        }

        String content = TextUtils.isEmpty(runManContent.getTextContent())
                ? runManContent.getRunManAttribute().getContent()
                : runManContent.getTextContent();
        int maxContentLength = verticalTypefaceAttribute.getMaxSize();
        if (!TextUtils.isEmpty(content)) {
            if (content.length() > maxContentLength) {
                content = content.substring(0, maxContentLength);
            }
            verticalTextview.setVerticalText(content);
        }

        if (verticalTypefaceAttribute.getTextShadow() != null) {
            TextShadow textShadow = verticalTypefaceAttribute.getTextShadow();
            verticalTextview.setShadowLayer(textShadow.getRadius(), textShadow.getX(),
                    textShadow.getY(), textShadow.getColor());
        }
    }

    private void setRunManBackground(RunManBackgroundAttribute runManBackgroundAttribute, RelativeLayout contentLayout) {
        LayoutParams layoutParams =
                (LayoutParams) contentLayout.getLayoutParams();
        if (runManBackgroundAttribute != null) {
            layoutParams.width = runManBackgroundAttribute.getWidth();
            layoutParams.height = runManBackgroundAttribute.getHeight();
            contentLayout.setLayoutParams(layoutParams);
            switch (runManBackgroundAttribute.getSourceType()) {
                case RES:
                    contentLayout.setBackgroundResource(runManBackgroundAttribute.getBgResId());
                    break;
                default:
                    break;
            }
        } else {
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            contentLayout.setLayoutParams(layoutParams);
            contentLayout.setBackgroundResource(R.color.transparent);
        }
    }

    private void setRunManIcons(List<RunManIconAttribute> runManIconAttributes, RelativeLayout contentLayout, int id) {
        if (runManIconAttributes != null) {
            for (RunManIconAttribute runManIconAttribute : runManIconAttributes) {
                RunManIconAlignCountType runManIconAlignCountType =
                        runManIconAttribute.getRunManIconAlignCountType();
                RunManIconAlignHorizontalType runManIconAlignHorizontalType =
                        runManIconAttribute.getRunManIconAlignHorizontalType();
                RunManIconAlignVerticalType runManIconAlignVerticalType =
                        runManIconAttribute.getRunManIconAlignVerticalType();
                if (runManIconAttribute.getRunManIconInputAttribute() == null) {
                    SimpleDraweeView simpleDraweeView = new SimpleDraweeView(getContext());
                    LayoutParams layoutParams = new LayoutParams(
                            runManIconAttribute.getWidth(), runManIconAttribute.getHeight());
                    switch (runManIconAlignCountType) {
                        case FIRST:
                            if (runManIconAlignHorizontalType == RunManIconAlignHorizontalType.RIGHT) {
                                if (runManIconAlignVerticalType == RunManIconAlignVerticalType.BOTTOM) {
                                    layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, id);
                                    layoutParams.addRule(RelativeLayout.RIGHT_OF, id);
                                    layoutParams.bottomMargin = runManIconAttribute.getMarginVertical();
                                    layoutParams.leftMargin = runManIconAttribute.getMarginHorizontal();
                                } else if (runManIconAlignVerticalType == RunManIconAlignVerticalType.TOP) {
                                    layoutParams.addRule(RelativeLayout.RIGHT_OF, id);
                                    layoutParams.topMargin = runManIconAttribute.getMarginVertical();
                                    layoutParams.leftMargin = runManIconAttribute.getMarginHorizontal();
                                }
                            } else {
                                if (runManIconAlignVerticalType == RunManIconAlignVerticalType.BOTTOM) {
                                    layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, id);
                                    layoutParams.bottomMargin = runManIconAttribute.getMarginVertical();
                                    layoutParams.leftMargin = runManIconAttribute.getMarginHorizontal();
                                } else if (runManIconAlignVerticalType == RunManIconAlignVerticalType.TOP) {
                                    layoutParams.topMargin = runManIconAttribute.getMarginVertical();
                                    layoutParams.leftMargin = runManIconAttribute.getMarginHorizontal();
                                }
                            }
                            break;
                    }
                    switch (runManIconAttribute.getSourceType()) {
                        case RES:
                            simpleDraweeView.setBackgroundResource(runManIconAttribute.getImageResId());
                            break;
                        default:
                            break;
                    }
                    simpleDraweeView.setLayoutParams(layoutParams);
                    contentLayout.addView(simpleDraweeView);
                } else {
                    RunManIconInputAttribute runManIconInputAttribute =
                            runManIconAttribute.getRunManIconInputAttribute();
                    RelativeLayout relativeLayout = new RelativeLayout(getContext());
                    relativeLayout.setBackgroundColor(Color.BLUE);
                    SimpleDraweeView simpleDraweeView = new SimpleDraweeView(getContext());
                    TextView xiaoMaiTextView = new TextView(getContext());
                    LayoutParams layoutParams = new LayoutParams(
                            runManIconAttribute.getWidth(), runManIconAttribute.getHeight());
                    switch (runManIconAlignCountType) {
                        case FIRST:
                            if (runManIconAlignHorizontalType == RunManIconAlignHorizontalType.RIGHT) {
                                if (runManIconAlignVerticalType == RunManIconAlignVerticalType.BOTTOM) {
                                    layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, id);
                                    layoutParams.addRule(RelativeLayout.RIGHT_OF, id);
                                    layoutParams.bottomMargin = runManIconAttribute.getMarginVertical();
                                    layoutParams.leftMargin = runManIconAttribute.getMarginHorizontal();
                                } else if (runManIconAlignVerticalType == RunManIconAlignVerticalType.TOP) {
                                    layoutParams.addRule(RelativeLayout.RIGHT_OF, id);
                                    layoutParams.bottomMargin = runManIconAttribute.getMarginVertical();
                                    layoutParams.leftMargin = runManIconAttribute.getMarginHorizontal();
                                }
                            } else {
                                if (runManIconAlignVerticalType == RunManIconAlignVerticalType.BOTTOM) {
                                    layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, id);
                                    layoutParams.addRule(RelativeLayout.RIGHT_OF, id);
                                    layoutParams.bottomMargin = runManIconAttribute.getMarginVertical();
                                    layoutParams.leftMargin = runManIconAttribute.getMarginHorizontal();
                                } else if (runManIconAlignVerticalType == RunManIconAlignVerticalType.TOP) {
                                    layoutParams.bottomMargin = runManIconAttribute.getMarginVertical();
                                    layoutParams.leftMargin = runManIconAttribute.getMarginHorizontal();
                                }
                            }
                            break;
                    }
                    switch (runManIconAttribute.getSourceType()) {
                        case RES:
                            simpleDraweeView.setBackgroundResource(runManIconAttribute.getImageResId());
                            break;
                        default:
                            break;
                    }
                    xiaoMaiTextView.setTypeface(FontsUtils.getXiaoMaiTypeface());
                    xiaoMaiTextView.setTextColor(runManIconInputAttribute.getTextColor());
                    xiaoMaiTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                            runManIconInputAttribute.getTextSize());
                    xiaoMaiTextView.setRotation(runManIconInputAttribute.getRotation());
                    xiaoMaiTextView.setText(runManIconInputAttribute.getDefaultText());
                    LayoutParams textLayoutParams = new LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    if (runManIconInputAttribute.getMarginBottom() > 0) {
                        textLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                    } else {
                        textLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                    }
                    xiaoMaiTextView.setLayoutParams(textLayoutParams);
                    LayoutParams iconLayoutParams = new LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    iconLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                    simpleDraweeView.setLayoutParams(iconLayoutParams);
                    relativeLayout.setLayoutParams(layoutParams);
                    relativeLayout.addView(simpleDraweeView);
                    relativeLayout.addView(xiaoMaiTextView);
                    contentLayout.addView(relativeLayout);
                }
            }
        }
    }

    private void setRunManText(RunManTextAttribute firstRunManTextAttribute,
                               RunManTextAttribute secondRunManTextAttribute,
                               String content,
                               TextView rainbowTextView,
                               RelativeLayout rainbowTextViewContainer,
                               int id) {
        int firstCount = firstRunManTextAttribute.getMaxTextSize();
        int secondCount = 0;
        int maxContentLength;
        if (secondRunManTextAttribute != null) {
            maxContentLength =
                    secondRunManTextAttribute.getMaxTextSize() + firstRunManTextAttribute.getMaxTextSize();

            if (content.length() > maxContentLength) {
                content = content.substring(0, maxContentLength);
            }
            if (content.length() > firstRunManTextAttribute.getMaxTextSize() && content.length() <= maxContentLength) {
                secondCount = content.length() - firstCount;
                int configDifference =
                        firstRunManTextAttribute.getMaxTextSize() - secondRunManTextAttribute.getMaxTextSize();
                int realDifference = firstCount - secondCount;
                if (realDifference != 0) {
                    if (realDifference > configDifference) {
                        if (configDifference < 0) {
                            firstCount = (int) Math.ceil((double) (content.length() - configDifference) / 2) + configDifference;
                        } else {
                            firstCount = (content.length() - configDifference) / 2 + configDifference;
                        }
                        secondCount = content.length() - firstCount;
                    } else if (realDifference < configDifference) {
                        secondCount = (content.length() - configDifference) / 2;
                        firstCount = content.length() - firstCount;
                    }
                }
            }
        } else {
            maxContentLength = firstRunManTextAttribute.getMaxTextSize();
            if (content.length() > maxContentLength) {
                content = content.substring(0, maxContentLength);
                firstCount = firstRunManTextAttribute.getMaxTextSize();
            } else {
                firstCount = content.length();
            }
        }
        if (secondCount > 0) {
            LayoutParams firstLayoutParams = new LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (firstRunManTextAttribute.getTypeface() != null) {
                rainbowTextView.setTypeface(firstRunManTextAttribute.getTypeface());
            } else {
                rainbowTextView.setTypeface(FontsUtils.getHuaKangPopWTypeface());
            }
            rainbowTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    firstRunManTextAttribute.getTextSize());
            rainbowTextView.setShadowLayer(1, firstRunManTextAttribute.getShadowX(),
                    firstRunManTextAttribute.getShadowY(), firstRunManTextAttribute.getShadowColor());
            if (firstRunManTextAttribute.getTextColorSourceType() == null) {
                rainbowTextView.setTextColor(firstRunManTextAttribute.getTextColor());
            } else {
                switch (firstRunManTextAttribute.getTextColorSourceType()) {
                    case RES:
                        rainbowTextView.setTextColor(Color.WHITE);
                        break;
                    default:
                        break;
                }
            }
            rainbowTextView.setText(content.substring(0, firstCount));
            firstLayoutParams.setMargins(firstRunManTextAttribute.getMarginX(),
                    firstRunManTextAttribute.getMarginY(), 0, 0);
            rainbowTextView.setLayoutParams(firstLayoutParams);
            rainbowTextViewContainer.addView(rainbowTextView);

            LayoutParams secondLayoutParams = new LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            TextView secondRainbowTextView = new TextView(getContext());
            if (secondRunManTextAttribute.getTypeface() != null) {
                secondRainbowTextView.setTypeface(secondRunManTextAttribute.getTypeface());
            } else {
                secondRainbowTextView.setTypeface(FontsUtils.getHuaKangPopWTypeface());
            }
            secondRainbowTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    secondRunManTextAttribute.getTextSize());
            secondRainbowTextView.setShadowLayer(1, secondRunManTextAttribute.getShadowX(),
                    secondRunManTextAttribute.getShadowY(), secondRunManTextAttribute.getShadowColor());
            if (secondRunManTextAttribute.getTextColorSourceType() == null) {
                secondRainbowTextView.setTextColor(secondRunManTextAttribute.getTextColor());
            } else {
                switch (secondRunManTextAttribute.getTextColorSourceType()) {
                    case RES:
                        secondRainbowTextView.setTextColor(Color.WHITE);
                        break;
                    default:
                        break;
                }
            }
            secondRainbowTextView.setText(content.substring(firstCount, firstCount + secondCount));
            secondLayoutParams.setMargins(secondRunManTextAttribute.getMarginX(),
                    secondRunManTextAttribute.getMarginY(), 0, 0);
            secondLayoutParams.addRule(RelativeLayout.BELOW, id);
            secondRainbowTextView.setLayoutParams(secondLayoutParams);
            rainbowTextViewContainer.addView(secondRainbowTextView);
        } else {
            LayoutParams layoutParams = new LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (firstRunManTextAttribute.getTypeface() != null) {
                rainbowTextView.setTypeface(firstRunManTextAttribute.getTypeface());
            } else {
                rainbowTextView.setTypeface(FontsUtils.getHuaKangPopWTypeface());
            }
            rainbowTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    firstRunManTextAttribute.getTextSize());
            rainbowTextView.setShadowLayer(1, firstRunManTextAttribute.getShadowX(),
                    firstRunManTextAttribute.getShadowY(), firstRunManTextAttribute.getShadowColor());
            if (firstRunManTextAttribute.getTextColorSourceType() == null) {
                rainbowTextView.setTextColor(firstRunManTextAttribute.getTextColor());
            } else {
                switch (firstRunManTextAttribute.getTextColorSourceType()) {
                    case RES:
                        rainbowTextView.setTextColor(Color.WHITE);
                        break;
                    default:
                        break;
                }
            }
            rainbowTextView.setText(content);
            layoutParams.setMargins(firstRunManTextAttribute.getMarginX(),
                    firstRunManTextAttribute.getMarginY(), 0, 0);
            rainbowTextView.setLayoutParams(layoutParams);
            rainbowTextViewContainer.addView(rainbowTextView);
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

    public void playSound() {
        if (runManAttribute != null && runManAttribute.getRunManSoundAttribute() != null) {
            RunManSoundAttribute runManSoundAttribute = runManAttribute.getRunManSoundAttribute();
            switch (runManSoundAttribute.getSourceType()) {
                case RES:
                    SoundPoolPlayer.getInstance().playSound(runManSoundAttribute.getSoundResId());
                    break;
            }
        }
    }

    public void stopSound() {
        if (runManAttribute != null && runManAttribute.getRunManSoundAttribute() != null) {
            RunManSoundAttribute runManSoundAttribute = runManAttribute.getRunManSoundAttribute();
            switch (runManSoundAttribute.getSourceType()) {
                case RES:
                    SoundPoolPlayer.getInstance().stop(runManSoundAttribute.getSoundResId());
                    break;
            }
        }
    }

    public void saveImage(String saveImageFilePath, String videoPath,
                          OnContentImageSaveSuccessListener onContentImageSaveSuccessListener) {
        new Handler(Looper.getMainLooper()).post(() -> {
            showContentLayout.setDrawingCacheEnabled(true);
            showContentLayout.setDrawingCacheBackgroundColor(Color.TRANSPARENT);
            showContentLayout.setDrawingCacheQuality(DRAWING_CACHE_QUALITY_HIGH);
            showContentLayout.buildDrawingCache();
            Bitmap bitmap = showContentLayout.getDrawingCache();
            File file = new File(saveImageFilePath);
            Bitmap bgBmp = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bgBmp);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setDither(true);
            paint.setFilterBitmap(true);
            canvas.drawBitmap(bitmap, (getWidth() - bitmap.getWidth()) / 2, (getHeight() - bitmap.getHeight()) / 2, paint);
            showContentLayout.setDrawingCacheEnabled(false);
            ActionImageData actionImageData =
                    VideoUtils.saveActionToFile(getContext(), bgBmp, file.getParent(), file.getName(), videoPath);
            if (onContentImageSaveSuccessListener != null) {
                onContentImageSaveSuccessListener.onContentImageSaveSuccess(actionImageData);
            }
        });
    }
}
