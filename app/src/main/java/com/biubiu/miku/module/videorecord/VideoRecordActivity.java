package com.biubiu.miku.module.videorecord;

import android.content.Context;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.biubiu.miku.Navigator;
import com.biubiu.miku.R;
import com.biubiu.miku.base.BaseActivity;
import com.biubiu.miku.util.Config;
import com.biubiu.miku.util.FileUtils;
import com.biubiu.miku.util.ImageUtils;
import com.biubiu.miku.util.SystemUtils;
import com.biubiu.miku.util.ThreadPool;
import com.biubiu.miku.util.camera.CameraHelper;
import com.biubiu.miku.util.sound.SoundPoolPlayer;
import com.biubiu.miku.util.video.ConcatMediaCallback;
import com.biubiu.miku.util.video.VideoUtils;
import com.biubiu.miku.util.video.task.VideoContentTask;
import com.biubiu.miku.util.video.task.VideoContentTaskManager;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 视频录制界面
 */
public class VideoRecordActivity extends BaseActivity {

    private static final int BEST_VIDEO_WIDTH = 720;
    private static final int BEST_VIDEO_HEIGHT = 480;
    private static final float MIN_VIDEO_PROPORTION = 1f;
    private static final int GONE_VIEW = 1001;
    private static final int VIDEO_MAX_DU_MS = 18000;
    private static final int VIDEO_MIN_DU_MS = 1000;
    @BindView(R.id.record_size)
    ImageView recordSize;
    @BindView(R.id.flash)
    ImageView flash;
    @BindView(R.id.switch_camera)
    ImageView switchCamera;
    @BindView(R.id.header_layout)
    RelativeLayout headerLayout;
    @BindView(R.id.surface_view)
    SurfaceView surfaceView;
    @BindView(R.id.cover_record_head)
    TextView coverRecordHead;
    @BindView(R.id.video_progress)
    ProgressBar videoProgress;
    @BindView(R.id.progress_interval)
    RelativeLayout progressInterval;
    @BindView(R.id.progress_cover)
    RelativeLayout progressCover;
    @BindView(R.id.cover_record_footer)
    TextView coverRecordFooter;
    @BindView(R.id.record_container)
    RelativeLayout recordContainer;
    @BindView(R.id.record_stop)
    RelativeLayout recordStop;
    @BindView(R.id.record_start)
    SimpleDraweeView recordStart;
    @BindView(R.id.record_action_layout)
    RelativeLayout recordActionLayout;
    @BindView(R.id.record_duration_note)
    TextView recordDurationNote;
    @BindView(R.id.undo)
    TextView undo;
    @BindView(R.id.done)
    TextView done;
    @BindView(R.id.record_below_menu)
    RelativeLayout recordBelowMenu;
    @BindView(R.id.other_container)
    FrameLayout otherContainer;
    @BindView(R.id.record_layout)
    RelativeLayout recordLayout;
    @BindView(R.id.loading_video_layout)
    RelativeLayout loadingVideoLayout;
    @BindView(R.id.loading_video)
    SimpleDraweeView loadingVideoSimpleDraweeView;
    ImageView focusView;
    private UndoType undoType = UndoType.RECORD;
    private float recordRatioWH = 1.0f;
    private CameraHelper cameraHelper = new CameraHelper(this);
    private int currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    private Camera cameraInstance = cameraHelper.openCamera(currentCameraId);
    private boolean isFlashOpen;
    private MediaRecorder mediaRecorder;
    private boolean isRecord = false;
    private Timer timer;
    private TimerTask timerTask;
    private int focusViewSize;
    private AnimationSet focusAnim;
    private float downX, downY;
    private String currentRecordFilePath;
    private int currentRecordDuration;
    private List<String> recordFilePathList = new LinkedList<>();
    private List<Integer> recordTimeList = new LinkedList<>();
    private List<Camera.Size> sizes = new LinkedList<>();
    private boolean isOpenAudioRecord = true;
    private String recordVideoFileDirPath;
    private Camera.Size previewSize;
    private VideoContentTask videoContentTask;
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GONE_VIEW:
                    View view = (View) msg.obj;
                    view.setVisibility(View.GONE);
                    view.clearAnimation();
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_record_activity);
        ButterKnife.bind(this);
        initViewData();
        addListener();
        videoContentTask =
                VideoContentTaskManager.getInstance().createVideoContentTask();
        checkTextColorState();
    }

    private void initViewData() {
        recordStop.setVisibility(View.GONE);
        if (!cameraHelper.hasFrontCamera() || !cameraHelper.hasBackCamera()) {
            switchCamera.setVisibility(View.INVISIBLE);
        }
        focusViewSize = getResources().getDimensionPixelSize(R.dimen.focus_view_size);
        focusAnim = (AnimationSet) AnimationUtils.loadAnimation(this, R.anim.focus_anim);
        videoProgress.setMax(VIDEO_MAX_DU_MS);
        videoProgress.setProgress(0);
        setCameraUi();
        addFocusView();
//        if (getIntent().getExtras().getString(IS_EDIT_CENTER_ACTIVITY) != null
//                && getIntent().getExtras().getString(IS_EDIT_CENTER_ACTIVITY)
//                .equals(IS_EDIT_CENTER_ACTIVITY)) {
//            reShowRecordedVideos();
//        }
    }

    private void reShowRecordedVideos() {
        VideoContentTask videoContentTaskBack =
                VideoContentTaskManager.getInstance().getCurrentContentTask();
        if (videoContentTaskBack.getRecordVideoFileDirPath() == null) {
            recordVideoFileDirPath = FileUtils.getRecordTempDirPath();
        } else {
            recordVideoFileDirPath = videoContentTaskBack.getRecordVideoFileDirPath();
        }
        recordFilePathList = videoContentTaskBack.getRecordFilePathList();
        recordTimeList = videoContentTaskBack.getRecordTimeList();
        checkTextColorState();
        progressInterval.removeAllViews();
        int progress = 0;
        if (recordTimeList != null && !recordTimeList.isEmpty()) {
            for (int i = 0; i < recordTimeList.size(); i++) {
                int videoDuration = recordTimeList.get(i);
                progress += videoDuration;
                videoProgress.setProgress(progress);
                addProgressInterval();
            }
            done.setTextColor(getResources().getColor(R.color.main_red));
        } else {
            recordTimeList = new ArrayList<>();
        }
        currentRecordDuration = progress;
    }

    private void checkTextColorState() {
        if (recordFilePathList != null && !recordFilePathList.isEmpty()
                && recordFilePathList.size() > 0) {
            undo.setTextColor(getResources().getColor(R.color.main_red));
            undo.setClickable(true);
            done.setClickable(true);
            if (recordRatioWH == Config.VIDEO_RATIO_4_3) {
                recordSize.setImageResource(R.drawable.size_rectangle_gray);
            } else {
                recordSize.setImageResource(R.drawable.size_square_disable);
            }
        } else {
            recordFilePathList = new ArrayList<>();
            undo.setTextColor(getResources().getColor(R.color.dark));
            done.setTextColor(getResources().getColor(R.color.dark));
            undo.setClickable(false);
            done.setClickable(false);
            if (recordRatioWH == Config.VIDEO_RATIO_4_3) {
                recordSize.setImageResource(R.drawable.size_rectangle);
            } else {
                recordSize.setImageResource(R.drawable.size_square);
            }
        }
    }

    private void setCameraUi() {
        RelativeLayout.LayoutParams layoutParams =
                (RelativeLayout.LayoutParams) recordBelowMenu.getLayoutParams();
        layoutParams.height = SystemUtils.getRealScreenHeight() -
                getResources().getDimensionPixelSize(R.dimen.record_header_height)
                - SystemUtils.getScreenWidthPx() -
                getResources().getDimensionPixelSize(R.dimen.record_progress_height);
        recordBelowMenu.setLayoutParams(layoutParams);

        // RelativeLayout.LayoutParams recordParams =
        // (RelativeLayout.LayoutParams) surfaceView.getLayoutParams();
        // recordParams.height = SystemUtils.getScreenWidthPx();
        // surfaceView.setLayoutParams(recordParams);

        setCoverRecordUI(coverRecordFooter, false);
        setCoverRecordUI(coverRecordHead, false);
    }

    private void setCoverRecordUI(View view, boolean isVisiable) {
        RelativeLayout.LayoutParams layoutParams =
                (RelativeLayout.LayoutParams) view.getLayoutParams();
        if (isVisiable) {
            layoutParams.height = SystemUtils.getScreenWidthPx() / 8;
        } else {
            layoutParams.height = 0;
        }
        view.setLayoutParams(layoutParams);
    }

    private void releaseCamera() {
        if (cameraInstance != null) {
            cameraInstance.setPreviewCallback(null);
            cameraInstance.release();
            cameraInstance = null;
        }
    }

    private void addFocusView() {
        if (focusView != null) {
            recordLayout.removeView(focusView);
        }
        focusView = new ImageView(getApplicationContext());
        RelativeLayout.LayoutParams layoutParams =
                new RelativeLayout.LayoutParams(focusViewSize, focusViewSize);
        focusView.setImageResource(R.drawable.focus_shape);
        focusView.setLayoutParams(layoutParams);
        focusView.setVisibility(View.GONE);
        recordLayout.addView(focusView);
    }

    private void setupCamera() {
        try {
            cameraInstance = cameraHelper.openCamera(currentCameraId);
        } catch (Exception e) {
            // 例如：用户拒绝相机权限
            Toast.makeText(getApplicationContext(), R.string.open_camera_permission,
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return;
        }
        switchCamera.setEnabled(true);
        flash.setEnabled(true);
        final Camera.Parameters parameters = cameraInstance.getParameters();
        parameters.setPictureFormat(ImageFormat.JPEG);
        parameters.setFlashMode(
                isFlashOpen ? Camera.Parameters.FLASH_MODE_TORCH : Camera.Parameters.ANTIBANDING_OFF);
        cameraInstance.stopPreview();
        if (surfaceView.getHolder().getSurface() != null &&
                surfaceView.getHolder().getSurface().isValid()) {
            startVideoPreview(parameters);
        } else {
            surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    startVideoPreview(parameters);
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                }
            });
        }
    }

    private void startVideoPreview(Camera.Parameters parameters) {
        previewSize = getVideoSize(true);
        Log.e("preview", "preview size:   width:" + previewSize.width +
                "   height:" + previewSize.height);
        parameters.setPreviewSize(previewSize.width, previewSize.height);
        cameraInstance.setDisplayOrientation(90);
        cameraInstance.setParameters(parameters);
        try {
            cameraInstance.setPreviewDisplay(surfaceView.getHolder());
            cameraInstance.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addListener() {
        undo.setOnClickListener(v -> {
            SoundPoolPlayer.getInstance().playSound(R.raw.click);
            if (!isRecord) {
                if (undoType == UndoType.DELETE) {
                    if (!recordFilePathList.isEmpty() && recordFilePathList.size() > 0) {
                        if (new File(recordFilePathList.get(recordFilePathList.size() - 1)).exists()) {
                            FileUtils.deleteFile(recordFilePathList.get(recordFilePathList.size() - 1));
                            recordFilePathList.remove(recordFilePathList.size() - 1);
                        }
                    } else {
                        done.setTextColor(getResources().getColor(R.color.main_red));
                    }
                    if (!recordTimeList.isEmpty() && recordTimeList.size() > 0) {
                        if (recordTimeList.size() == 1) {
                            videoProgress.setProgress(0);
                            recordTimeList.clear();
                        } else {
                            videoProgress.setProgress(
                                    videoProgress.getProgress() - recordTimeList.get(recordTimeList.size() - 1));
                            recordTimeList.remove(recordTimeList.size() - 1);
                        }
                        if (progressInterval.getChildCount() > 0) {
                            progressInterval.removeViewAt(progressInterval.getChildCount() - 1);
                        }
                        progressCover.removeAllViews();
                    }
                    undoType = UndoType.RECORD;
                } else {
                    undoType = UndoType.DELETE;
                    if (recordFilePathList.size() > 0) {
                        progressUndoView();
                    }
                }
            }
            checkTextColorState();
        });
        recordSize.setOnClickListener(v -> {
            SoundPoolPlayer.getInstance().playSound(R.raw.click);
            if (!isRecord) {
                if (recordFilePathList.isEmpty() || recordFilePathList.size() == 0) {
                    if (recordRatioWH == Config.VIDEO_RATIO_4_3) {
                        recordSize.setImageResource(R.drawable.size_square);
                        recordRatioWH = Config.VIDEO_RATIO_1_1;
                        setCoverRecordUI(coverRecordFooter, false);
                        setCoverRecordUI(coverRecordHead, false);
                    } else if (recordRatioWH == Config.VIDEO_RATIO_1_1) {
                        recordSize.setImageResource(R.drawable.size_rectangle);
                        recordRatioWH = Config.VIDEO_RATIO_4_3;
                        setCoverRecordUI(coverRecordFooter, true);
                        setCoverRecordUI(coverRecordHead, true);
                        // setCameraUi();
                    }
                }
            }
        });
        surfaceView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downX = event.getRawX();
                    downY = event.getRawY();
                    break;
                case MotionEvent.ACTION_UP:
                    int x = (int) event.getRawX();
                    int y = (int) event.getRawY();
                    if (Math.abs(x - downX) <= 3 && Math.abs(y - downY) <= 3) {
                        setFocusViewParams(x, y);
                        autoFocus();
                    }
                    break;
            }
            return true;
        });
        switchCamera.setOnClickListener(v -> {
            SoundPoolPlayer.getInstance().playSound(R.raw.click);
            if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                switchCamera.setBackgroundResource(R.drawable.flip);
            } else {
                switchCamera.setBackgroundResource(R.drawable.flip);
            }
            switchCamera();
        });
        flash.setOnClickListener(v -> {
            SoundPoolPlayer.getInstance().playSound(R.raw.click);
            switchFlash();
        });
        recordActionLayout.setOnClickListener(v -> {
            if (cameraInstance != null) {
                if (isRecord) {
                    ScaleAnimation actionAnimation = new ScaleAnimation(1f, 0.8f, 1f, 0.8f,
                            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    actionAnimation.setRepeatMode(Animation.REVERSE);
                    actionAnimation.setDuration(100);
                    recordActionLayout.startAnimation(actionAnimation);
                    recordStop.clearAnimation();
                    recordStop.setVisibility(View.GONE);
                    recordDurationNote.setText(getResources().getString(R.string.record_to_start));
                    ScaleAnimation stopAnimation = new ScaleAnimation(0, 1f, 0, 1f,
                            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    stopAnimation.setDuration(100);
                    stopAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            recordStart.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    recordStart.startAnimation(stopAnimation);
                    stopRecord();
                } else {
                    ScaleAnimation recordAnimation = new ScaleAnimation(1f, 0.8f, 1f, 0.8f,
                            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    recordAnimation.setRepeatMode(Animation.REVERSE);
                    recordAnimation.setDuration(500);
                    recordAnimation.setRepeatCount(-1);
                    ScaleAnimation actionAnimation = new ScaleAnimation(1f, 0.8f, 1f, 0.8f,
                            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    actionAnimation.setRepeatMode(Animation.REVERSE);
                    actionAnimation.setDuration(100);
                    recordActionLayout.startAnimation(actionAnimation);
                    recordDurationNote.setText(getResources().getString(R.string.record_to_stop));
                    ScaleAnimation startAnimation = new ScaleAnimation(1f, 0, 1f, 0,
                            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    startAnimation.setDuration(100);
                    startAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            recordStart.setVisibility(View.GONE);
                            recordStop.setVisibility(View.VISIBLE);
                            recordStop.startAnimation(recordAnimation);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    recordStart.startAnimation(startAnimation);
                    startRecord();
                    if (recordFilePathList.size() == 0) {
                        done.setTextColor(getResources().getColor(R.color.main_red));
                    }
                }
            }
            checkTextColorState();
            done.setTextColor(getResources().getColor(R.color.main_red));
        });
        done.setOnClickListener(v -> {
            undo.setClickable(false);
            undo.setTextColor(getResources().getColor(R.color.dark));
            done.setClickable(false);
            undo.setTextColor(getResources().getColor(R.color.dark));
            SoundPoolPlayer.getInstance().playSound(R.raw.click);
            showEditCenter();
        });
    }

    public void switchCamera() {
        releaseCamera();
        currentCameraId = (currentCameraId + 1) % cameraHelper.getNumberOfCameras();
        setupCamera();
    }

    private void switchFlash() {
        if (cameraInstance == null) {
            return;
        }
        try {
            isFlashOpen = !isFlashOpen;
            if (isFlashOpen) {
                openFlash();
            } else {
                flash.setBackgroundResource(R.drawable.flash);
                Camera.Parameters parameters = cameraInstance.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                cameraInstance.setParameters(parameters);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openFlash() {
        flash.setBackgroundResource(R.drawable.flash);
        Camera.Parameters parameters = cameraInstance.getParameters();
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        cameraInstance.setParameters(parameters);
    }

    private void setFocusViewParams(int x, int y) {
        if (focusView != null) {
            RelativeLayout.LayoutParams params =
                    (RelativeLayout.LayoutParams) focusView.getLayoutParams();
            int leftMargin = x - focusViewSize / 2;
            if (leftMargin < 0) {
                leftMargin = 0;
            } else if (leftMargin > SystemUtils.getScreenWidthPx() - focusViewSize) {
                leftMargin = SystemUtils.getScreenWidthPx() - focusViewSize;
            }
            params.leftMargin = leftMargin;
            int topMargin = y - focusViewSize / 2;
            if (topMargin < 0) {
                topMargin = 0;
            } else if (topMargin > SystemUtils.getScreenHeightPx() - focusViewSize) {
                topMargin = SystemUtils.getScreenHeightPx() - focusViewSize;
            }
            params.topMargin = topMargin;
            focusView.setLayoutParams(params);
        }
    }

    private void autoFocus() {
        if (cameraInstance != null) {
            try {
                handler.removeMessages(GONE_VIEW);
                final long autoFocusTime = System.currentTimeMillis();
                if (focusView != null) {
                    focusView.setVisibility(View.VISIBLE);
                    focusView.startAnimation(focusAnim);
                }
                cameraInstance.autoFocus((success, camera) -> {
                    long currentTimeMillis = System.currentTimeMillis();
                    long animationTime = currentTimeMillis - autoFocusTime;
                    if (animationTime < 500) {
                        Message obtainMessage = handler.obtainMessage();
                        obtainMessage.obj = focusView;
                        obtainMessage.what = GONE_VIEW;
                        handler.sendMessageDelayed(obtainMessage, 500 - animationTime);
                    } else {
                        if (focusView != null) {
                            focusView.setVisibility(View.GONE);
                            focusView.clearAnimation();
                        }
                    }
                });
            } catch (Exception e) {
                if (focusView != null) {
                    focusView.setVisibility(View.GONE);
                }
            }
        } else {
            if (focusView != null) {
                focusView.setVisibility(View.GONE);
            }
        }
    }

    public Camera.Size getVideoSize(boolean isPreviewSize) {
        sizes = cameraInstance.getParameters().getSupportedVideoSizes();
        // List<Camera.Size> previewSizes = cameraInstance.getParameters().getSupportedPreviewSizes();
        // for (Camera.Size size : previewSizes) {
        // Log.e("videoSize", "previewSize wdith:" + size.width + " height:" + size.height);
        // }
        // for (Camera.Size size : sizes) {
        // Log.e("videoSize", "videoSize wdith:" + size.width + " height:" + size.height);
        // }
        if (sizes == null) {
            sizes = cameraInstance.getParameters().getSupportedPreviewSizes();
        }
        Camera.Size bestSize = getRecordSize(sizes);
        if (isPreviewSize && bestSize != null) {
            RelativeLayout.LayoutParams recordParams =
                    (RelativeLayout.LayoutParams) surfaceView.getLayoutParams();
            if (bestSize.height >= bestSize.width) {
                recordParams.height = SystemUtils.getScreenWidthPx();
                recordParams.width = SystemUtils.getScreenWidthPx() * bestSize.height / bestSize.width;
            } else {
                recordParams.height = SystemUtils.getScreenWidthPx() * bestSize.width / bestSize.height;
                recordParams.width = SystemUtils.getScreenWidthPx();
            }
            surfaceView.setLayoutParams(recordParams);
        }
        return bestSize;
    }

    public Camera.Size getRecordSize(List<Camera.Size> sizes) {
        Camera.Size bestSize = null;
        for (int i = 0; i < sizes.size(); ++i) {
            if (sizes.get(i).width <= BEST_VIDEO_WIDTH) {
                if (sizes.get(i).width / (float) sizes.get(i).height >= MIN_VIDEO_PROPORTION) {
                    if (bestSize != null) {
                        if (Math.abs(sizes.get(i).width - BEST_VIDEO_WIDTH) < Math
                                .abs(bestSize.width - BEST_VIDEO_WIDTH)) {
                            bestSize = sizes.get(i);
                        } else if (Math.abs(sizes.get(i).width - BEST_VIDEO_WIDTH) == Math
                                .abs(bestSize.width - BEST_VIDEO_WIDTH)) {
                            if (sizes.get(i).width < bestSize.width) {
                                bestSize = sizes.get(i);
                            } else if (sizes.get(i).width == bestSize.width && sizes.get(i).height < bestSize.height) {
                                bestSize = sizes.get(i);
                            }
                        }
                    } else {
                        bestSize = sizes.get(i);
                    }
                }
            }
        }
        return bestSize;
    }

    private Camera.Size getBestSize(List<Camera.Size> sizes) {
        float bestSizeValue = Integer.MAX_VALUE;
        int bestSizePosition = 0;
        for (int i = 0; i < sizes.size(); ++i) {
            if (sizes.get(i).width >= 720) {
                if (Math
                        .abs((float) sizes.get(i).width / (float) sizes.get(i).height - 1f) <= bestSizeValue) {
                    bestSizeValue = Math.abs((float) sizes.get(i).width / (float) sizes.get(i).height - 1f);
                    bestSizePosition = i;
                }
            }
        }
        return sizes.get(bestSizePosition);
    }

    public void startRecord() {
        undo.setClickable(false);
        if (undoType == UndoType.DELETE) {
            undoType = UndoType.RECORD;
            if (progressCover.getChildCount() > 0) {
                progressCover.removeViewAt(progressCover.getChildCount() - 1);
            }
        }
        if (!isOpenAudioRecord) {
            Toast.makeText(getApplicationContext(), "请前往设置开启鹿影的录音权限！！！", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            switchCamera.setEnabled(false);
            releaseMediaRecorder();
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setOrientationHint(90);
            Camera.Size size = getVideoSize(false);
            Log.e("preview", "record size:   width:" + size.width + "   height:" + size.height);
            CameraHelper.CameraInfo2 cameraInfo = new CameraHelper.CameraInfo2();
            cameraHelper.getCameraInfo(currentCameraId, cameraInfo);
            boolean flipHorizontal = cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT;
            if (!flipHorizontal) {
                cameraInstance.setDisplayOrientation(90);
                cameraInstance.unlock();
                mediaRecorder.setOrientationHint(90);
            } else {
                cameraInstance.setDisplayOrientation(90);
                cameraInstance.unlock();
                mediaRecorder.setOrientationHint(270);
            }
            mediaRecorder.setCamera(cameraInstance);
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mediaRecorder.setVideoSize(size.width, size.height);
            mediaRecorder.setVideoFrameRate(30);
            mediaRecorder.setVideoEncodingBitRate(2000000);
            mediaRecorder.setAudioSamplingRate(44100);
            mediaRecorder.setAudioEncodingBitRate(96000);
            mediaRecorder.setAudioChannels(2);
            mediaRecorder.setMaxDuration(VIDEO_MAX_DU_MS - videoProgress.getProgress());
            recordVideoFileDirPath = FileUtils.getRecordTempDirPath();
            currentRecordFilePath =
                    recordVideoFileDirPath + File.separator + recordFilePathList.size() + ".mp4";
            File file = new File(currentRecordFilePath);
            if (file.exists()) {
                file.delete();
            }
            mediaRecorder.setPreviewDisplay(surfaceView.getHolder().getSurface());
            mediaRecorder.setOutputFile(currentRecordFilePath);
            mediaRecorder.prepare();
            mediaRecorder.start();
            showRecorderVideoProgress();
            isRecord = true;
        } catch (Exception e) {
            Log.e("e", e.getMessage());
            e.printStackTrace();
            stopRecord();
        }
    }


    private void releaseMediaRecorder() {
        if (isRecord && mediaRecorder != null) {
            try {
                mediaRecorder.stop();
                mediaRecorder.reset();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                isRecord = false;
                mediaRecorder.release();
                mediaRecorder = null;
            }
        }
    }

    private void showRecorderVideoProgress() {
        timer = new Timer();
        if (getAllProgress() != 0) {
            addProgressInterval();
        }
        timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> {
                    if (isRecord) {
                        videoProgress.setProgress(videoProgress.getProgress() + 5);
                        if (videoProgress.getProgress() >= videoProgress.getMax()) {
                            ScaleAnimation actionAnimation = new ScaleAnimation(1f, 0.8f, 1f, 0.8f,
                                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                            actionAnimation.setRepeatMode(Animation.REVERSE);
                            actionAnimation.setDuration(100);
                            recordActionLayout.startAnimation(actionAnimation);
                            recordStop.clearAnimation();
                            recordStop.setVisibility(View.GONE);
                            recordDurationNote.setText(getResources().getString(R.string.record_to_start));
                            ScaleAnimation stopAnimation = new ScaleAnimation(0, 1f, 0, 1f,
                                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                            stopAnimation.setDuration(100);
                            stopAnimation.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {
                                    recordStart.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });
                            recordStart.startAnimation(stopAnimation);
                            stopRecord();
                            showEditCenter();
                        }
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask, 5, 5);
    }

    private void checkVideoDuration() {
        if (new File(currentRecordFilePath).exists()) {
            if (new File(currentRecordFilePath).length() > 0) {
                currentRecordDuration += VideoUtils.getMediaDuration(currentRecordFilePath);
            } else {
                isOpenAudioRecord = false;
                Toast.makeText(getApplicationContext(), R.string.open_record_audio_permission,
                        Toast.LENGTH_SHORT)
                        .show();
            }
            if (!isOpenAudioRecord || VideoUtils.getMediaDuration(currentRecordFilePath) <= 1000) {
                deleteErrorVideoFile(currentRecordFilePath);
                videoProgress.setProgress(getAllProgress());
            } else {
                if (!recordFilePathList.contains(currentRecordFilePath)) {
                    recordFilePathList.add(currentRecordFilePath);
                    recordTimeList.add(videoProgress.getProgress() - getAllProgress());
                }
            }
        }
    }

    public void stopRecord() {
        undo.setClickable(true);
        if (isRecord) {
            releaseMediaRecorder();
            checkVideoDuration();
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        switchCamera.setEnabled(true);
    }

    private void deleteErrorVideoFile(final String videoFilePath) {
        ThreadPool.getInstance().execute(() -> FileUtils.deleteFile(videoFilePath));
    }

    private void showEditCenter() {
        for (int time : recordTimeList) {
            if (currentRecordDuration <= 0) {
                currentRecordDuration += time;
            }
        }
        if (currentRecordDuration >= VIDEO_MIN_DU_MS) {
            ImageUtils.showResGif(R.drawable.loading_video, loadingVideoSimpleDraweeView);
            loadingVideoLayout.setVisibility(View.VISIBLE);
            VideoUtils.concat(recordFilePathList, recordVideoFileDirPath, recordRatioWH,
                    new ConcatMediaCallback() {
                        @Override
                        public void success(String outputFilePath) {
                            videoContentTask.setVideoRatioWH(recordRatioWH);
                            videoContentTask.setVideoPath(outputFilePath);
                            videoContentTask.setRecordFilePathList(recordFilePathList);
                            videoContentTask.setRecordTimeList(recordTimeList);
                            videoContentTask.setRecordVideoFileDirPath(recordVideoFileDirPath);
                            ThreadPool.getInstance().execute(
                                    () -> VideoUtils.getVideoCover(outputFilePath, VideoUtils.TEMP_COVER_NAME));

                            ThreadPool.getInstance().execute(() -> {
                                String videoFilterThemePath =
                                        VideoUtils.getVideoCover(outputFilePath, VideoUtils.FILTER_COVER_NAME);
                                VideoUtils.generateVideoFilterTheme(videoFilterThemePath);
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    loadingVideoLayout.setVisibility(View.GONE);
                                    videoContentTask.setVideoCropState(VideoContentTask.ProcessState.SUCCESS);
                                    Navigator.INSTANCE.navigateToVideoEdit(VideoRecordActivity.this, outputFilePath, recordRatioWH);
                                    finish();
                                    videoContentTask.cutSheets();
                                });
                            });
                        }

                        @Override
                        public void failure(Throwable throwable) {
                            done.setClickable(true);
                            undo.setTextColor(getResources().getColor(R.color.main_red));
                            undo.setClickable(true);
                            loadingVideoLayout.setVisibility(View.GONE);
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), R.string.record_time_warn, Toast.LENGTH_SHORT).show();
        }
    }

    private void addProgressInterval() {
        TextView textView = new TextView(this);
        textView.setBackgroundResource(R.drawable.progress_interval_bg);
        RelativeLayout.LayoutParams layoutParams =
                new RelativeLayout.LayoutParams(5,
                        getResources().getDimensionPixelSize(R.dimen.record_progress_height));
        layoutParams.leftMargin =
                SystemUtils.getScreenWidthPx() * videoProgress.getProgress() / VIDEO_MAX_DU_MS;
        textView.setLayoutParams(layoutParams);
        progressInterval.addView(textView);
    }

    private void progressUndoView() {
        TextView textView = new TextView(this);
        textView.setBackgroundResource(R.drawable.progress_undo_view_bg);
        int right = SystemUtils.getScreenWidthPx() * videoProgress.getProgress() / VIDEO_MAX_DU_MS;
        int left;
        if (recordTimeList.size() <= 1) {
            left = 0;
        } else {
            left = SystemUtils.getScreenWidthPx()
                    * (videoProgress.getProgress() - recordTimeList.get(recordTimeList.size() - 1))
                    / VIDEO_MAX_DU_MS;
        }
        RelativeLayout.LayoutParams layoutParams =
                new RelativeLayout.LayoutParams(right - left, videoProgress.getHeight());
        layoutParams.leftMargin = SystemUtils.getScreenWidthPx()
                * (videoProgress.getProgress() - recordTimeList.get(recordTimeList.size() - 1))
                / VIDEO_MAX_DU_MS + (recordTimeList.size() == 1 ? 0 : 5);
        textView.setLayoutParams(layoutParams);
        progressCover.addView(textView);
    }

    private int getAllProgress() {
        int allProgress = 0;
        for (int duration : recordTimeList) {
            allProgress += duration;
        }
        return allProgress;
    }

    private Camera.Size getSquareVideoSize() {
        int position = 0;
        List<Integer> minValue = new LinkedList<>();
        for (Camera.Size size : sizes) {
            minValue.add(Math.abs(size.width - size.height));
        }
        for (int i = 0; i < minValue.size(); i++) {
            if (minValue.get(i) - minValue.get(i + 1) <= 0) {
                position = i;
            } else {
                position = i + 1;
            }
        }
        return sizes.get(position);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ScaleAnimation actionAnimation = new ScaleAnimation(1f, 0.8f, 1f, 0.8f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        actionAnimation.setRepeatMode(Animation.REVERSE);
        actionAnimation.setDuration(100);
        recordActionLayout.startAnimation(actionAnimation);
        recordStop.clearAnimation();
        recordStop.setVisibility(View.GONE);
        recordDurationNote.setText(getResources().getString(R.string.record_to_start));
        ScaleAnimation stopAnimation = new ScaleAnimation(0, 1f, 0, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        stopAnimation.setDuration(100);
        stopAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                recordStart.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        recordStart.startAnimation(stopAnimation);
        stopRecord();
        releaseMediaRecorder();
        releaseCamera();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setupCamera();
    }

    private enum UndoType {
        RECORD, DELETE
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public static Intent getCallingIntent(Context context) {
        return new Intent(context, VideoRecordActivity.class);
    }

}
