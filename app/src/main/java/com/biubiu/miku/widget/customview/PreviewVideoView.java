package com.biubiu.miku.widget.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.biubiu.miku.R;
import com.biubiu.miku.util.Config;
import com.biubiu.miku.util.SystemUtils;
import com.biubiu.miku.util.sound.AudioSamplePlayer;
import com.biubiu.miku.util.video.SeekData;
import com.biubiu.miku.util.video.VideoMetaData;
import com.biubiu.miku.util.video.VideoUtils;
import com.biubiu.miku.util.video.action.Action;
import com.biubiu.miku.util.video.action.ActionType;
import com.biubiu.miku.util.video.action.OnRecordLocationChangeListener;
import com.biubiu.miku.util.video.action.OnRecordStatusChangeListener;
import com.biubiu.miku.util.video.action.RecordActionLocationTask;
import com.biubiu.miku.util.video.action.RecordLocation;
import com.biubiu.miku.util.video.action.RecordStatus;
import com.biubiu.miku.util.video.action.VideoActionParams;
import com.biubiu.miku.util.video.action.chatBox.ChatBox;
import com.biubiu.miku.util.video.action.chatBox.ChatBoxChild;
import com.biubiu.miku.util.video.action.chatBox.ChatBoxContent;
import com.biubiu.miku.util.video.action.chatBox.ChatBoxLocation;
import com.biubiu.miku.util.video.action.montage.Montage;
import com.biubiu.miku.util.video.action.montage.MontageTimeLine;
import com.biubiu.miku.util.video.action.montage.MontageType;
import com.biubiu.miku.util.video.action.music.Music;
import com.biubiu.miku.util.video.action.runMan.RunMan;
import com.biubiu.miku.util.video.action.runMan.RunManContent;
import com.biubiu.miku.util.video.action.runMan.RunManLocation;
import com.biubiu.miku.util.video.action.sticker.Sticker;
import com.biubiu.miku.util.video.action.sticker.StickerImageData;
import com.biubiu.miku.util.video.action.sticker.StickerLocation;
import com.biubiu.miku.util.video.action.subtitle.Subtitle;
import com.biubiu.miku.util.video.action.subtitle.SubtitleContent;
import com.biubiu.miku.util.video.action.videoTag.VideoTag;
import com.biubiu.miku.util.video.action.videoTag.VideoTagContent;
import com.biubiu.miku.util.video.action.videoTag.VideoTagLocation;
import com.biubiu.miku.util.video.player.MediaPlayer;
import com.biubiu.miku.util.video.task.VideoContentTaskManager;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PreviewVideoView extends RelativeLayout
    implements
        OnRecordStatusChangeListener,
        OnRecordLocationChangeListener,
    MediaPlayer.OnCompletionListener,
    MediaPlayer.OnVideoSizeChangedListener {
  @BindView(R.id.edit_action_content)
  RelativeLayout editActionContent;
  @BindView(R.id.old_edit_action_view)
  RelativeLayout oldEditActionView;
  @BindView(R.id.preview_layout)
  RelativeLayout previewLayout;
  @BindView(R.id.video_surface_view)
  TextureView videoSurfaceView;
  @BindView(R.id.preview_default_image)
  ImageView previewDefaultImage;
  @BindView(R.id.subtitle_container)
  RelativeLayout subtitleContainer;
  @BindView(R.id.video_tag_content_view)
  VideoTagContentView currentVideoTagContentView;
  @BindView(R.id.chat_box_action_view)
  ChatBoxContentView currentChatBoxContentView;
  @BindView(R.id.sticker_action_view)
  StickerContentView currentStickerActionView;
  @BindView(R.id.run_man_content_view)
  RunManContentView currentRunManContentView;
  @BindView(R.id.montage_content_view)
  MontageContentView currentMontageContentView;
  @BindView(R.id.montage_layout)
  View montageLayout;
  @BindView(R.id.montage_icon)
  ImageView montageIcon;
  SubtitleContentView subtitleContentView;
  private MediaPlayer mediaPlayer;
  private AudioSamplePlayer audioSamplePlayer;
  private String videoPath;
  private boolean isLoop = false;
  private Music music;
  private boolean isPlay;
  private TimerTask timerTask;
  private Timer timer;
  private Map<SubtitleContent, NormalSubtitleContentView> oldNormalSubtitleContentViewMap =
      new HashMap<>();
  private Map<SubtitleContent, JPSubtitleContentView> oldJpSubtitleContentViewMap =
      new HashMap<>();
  private Map<SubtitleContent, BackgroundSubtitleContentView> oldBackgroundSubtitleContentViewHashMap =
      new HashMap<>();
  private Map<VideoTagContent,VideoTagContentView> videoTagContentViewMap = new HashMap<>();
  //private List<VideoTagContentView> oldVideoTagContentViewList = new ArrayList<>();
  private List<ChatBoxContentView> oldChatBoxContentViews = new ArrayList<>();
  private Map<RunManContent, RunManContentView> oldRunManContentViews = new HashMap<>();
  private Map<Long, StickerContentView> oldStickerContentViews = new HashMap<>();
  private Subtitle currentSubtitle;
  private VideoTag currentVideoTag;
  private ChatBox currentChatBox;
  private Sticker currentSticker;
  private RunMan currentRunMan;
  private Montage currentMontage;
  private Bitmap defaultThemeImage;
  private Bitmap frameThemeImage;
  private int seekPosition = 1;
  private OnRecordSucceedListener onRecordSucceedListener;
  private boolean isUpdateSeek = false;
  private int sampleSize = 0;
  private int videoDurationMs = 0;
  private boolean isPrepare = false;
  private ActionType recordActionType;
  private boolean isPlayCompletion = false;
  private float videoRatioWH;
  private boolean mIsVideoSizeKnown = false;
  private VideoMetaData videoMetaData;
  private Surface surface;
  private MontageTimeLine currentShowMontageTimeLine;
  private int currentRepeatMontageShowNum = 1;
  private Handler mainHandler = new Handler(Looper.getMainLooper());
  private int montageAdditionalTimeMs = 0;
  private boolean isShowSpeedMontage = false;
  long showMontageTime = 0;
  long showMontageOffsetTime = 0;
  int offsetPosition = 0;
  int oldShowMontageShowTimeMs = 0;
  private VideoActionParams videoActionParams;
  private List<WeakReference<OnVideoPlayProgressListener>> onVideoPlayProgressListeners
      = new ArrayList<>();

  public PreviewVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initViewData(attrs, defStyleAttr);
  }

  public PreviewVideoView(Context context) {
    super(context);
    initViewData(null, 0);
  }

  public PreviewVideoView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initViewData(attrs, 0);
  }

  private void initViewData(AttributeSet attrs, int defStyleAttr) {
    LayoutInflater inflater =
        (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.video_edit_preview_layout, this, true);
    ButterKnife.bind(this);
    getAttributes(attrs, defStyleAttr);
    setVideoData();
    setUiParams();
  }

  private void getAttributes(AttributeSet attributeSet, int defStyleAttr) {
    TypedArray a = getContext().obtainStyledAttributes(attributeSet, R.styleable.PreviewVideoView,
        defStyleAttr, 0);
    int defaultThemeResId =
        a.getResourceId(R.styleable.PreviewVideoView_default_theme_drawable, -1);
    if (defaultThemeResId != -1) {
      defaultThemeImage =
          ((BitmapDrawable) getResources().getDrawable(defaultThemeResId)).getBitmap();
    }
    a.recycle();
  }

  private void setUiParams() {
    currentVideoTagContentView.setVisibility(GONE);
    currentChatBoxContentView.setVisibility(GONE);
    currentStickerActionView.setVisibility(GONE);
    currentRunManContentView.setVisibility(GONE);
    currentMontageContentView.setVisibility(GONE);
    montageIcon.setVisibility(GONE);
    videoRatioWH = VideoContentTaskManager.getInstance().getCurrentContentTask().getVideoRatioWH();
    LayoutParams layoutParams =
        (LayoutParams) previewLayout.getLayoutParams();
    layoutParams.width = SystemUtils.getScreenWidthPx();
    layoutParams.height = SystemUtils.getScreenWidthPx();
    previewLayout.setLayoutParams(layoutParams);
    LayoutParams videoParams = (LayoutParams) videoSurfaceView.getLayoutParams();
    int[] videoSize = VideoUtils.getVideoSize(videoPath);
    if (videoSize[0] > videoSize[1]) {
      videoParams.height = SystemUtils.getScreenWidthPx();
      videoParams.width = SystemUtils.getScreenWidthPx() * videoSize[0] / videoSize[1];
    } else {
      videoParams.width = SystemUtils.getScreenWidthPx();
      videoParams.height = SystemUtils.getScreenWidthPx() * videoSize[1] / videoSize[0];
    }
    videoSurfaceView.setLayoutParams(videoParams);
    setVideoRatioUI(previewDefaultImage);
    setVideoRatioUI(editActionContent);
    videoSurfaceView.setVisibility(GONE);
    if (defaultThemeImage == null) {
      showFrameThemeImage();
    } else {
      LayoutParams imageLayoutParams =
          (LayoutParams) previewDefaultImage.getLayoutParams();
      imageLayoutParams.height = (int) (SystemUtils.getScreenWidthPx() / 4 * 3f);
      previewDefaultImage.setLayoutParams(imageLayoutParams);
      previewDefaultImage
          .setBackgroundDrawable(new BitmapDrawable(getResources(), defaultThemeImage));
    }
  }

  private void setVideoRatioUI(View view) {
    LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
    layoutParams.width = SystemUtils.getScreenWidthPx();
    layoutParams.height = (int) (SystemUtils.getScreenWidthPx() / videoRatioWH);
    view.setLayoutParams(layoutParams);
  }

  private void setVideoData() {
    videoPath = getVideoPath();
    music = VideoContentTaskManager.getInstance().getCurrentContentTask().getMusic();
    sampleSize = VideoUtils.getVideoSamplesSize(videoPath);
    videoDurationMs = VideoUtils.getMediaDuration(videoPath);
    videoMetaData = VideoUtils.getVideoMetaData(videoPath);
    montageAdditionalTimeMs = VideoContentTaskManager.getInstance()
        .getCurrentContentTask().getMontageAdditionalTimeMs(currentMontage);

    List<Action> actions = VideoContentTaskManager.getInstance().getCurrentContentTask().getActions();
    for (Action action : actions) {
      if (action instanceof RunMan) {
        initRunMan((RunMan) action);
      } else if (action instanceof VideoTag) {
        initVideoTag(((VideoTag)action).getVideoTagContent());
      } else if (action instanceof Sticker) {
        initSticker((Sticker) action);
      } else if (action instanceof ChatBox) {
        initChatBox();
      } else if (action instanceof Subtitle) {
        initSubtitle((Subtitle) action);
      }
    }
    Log.e("sampleSize", "sampleSize:" + sampleSize + "   duration:" + videoDurationMs);
  }

  private String getVideoPath() {
    String filterVideoFilePath =
        VideoContentTaskManager.getInstance().getCurrentContentTask().getFilterVideoFilePath();
    String cropVideoFilePath =
        VideoContentTaskManager.getInstance().getCurrentContentTask().getProcessVideoFilePath();
    return TextUtils.isEmpty(filterVideoFilePath) ? (TextUtils.isEmpty(cropVideoFilePath)
        ? VideoContentTaskManager.getInstance().getCurrentContentTask().getVideoPath()
        : cropVideoFilePath) : filterVideoFilePath;
  }

  public int getVideoDurationMs() {
    return videoDurationMs;
  }

  public int getSampleSize() {
    return sampleSize;
  }

  public void play(boolean isLoop) {
    play(isLoop, null);
  }

  public boolean isTextureAvaiable = false;

  public void play(boolean isLoop, String selectVideoPath) {
    this.isLoop = isLoop;
    Log.e("replay", "isPrepare：" + isPrepare);
    if (!isPrepare) {
      videoSurfaceView.setVisibility(VISIBLE);
      if (isTextureAvaiable) {
        play(selectVideoPath);
      } else {
        videoSurfaceView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
          @Override
          public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            Log.e("replay", "onSurfaceTextureAvailable");
            play(selectVideoPath);
            isTextureAvaiable = true;
          }

          @Override
          public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

          }

          @Override
          public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            isTextureAvaiable = false;
            return false;
          }

          @Override
          public void onSurfaceTextureUpdated(SurfaceTexture surface) {

          }
        });
      }
    } else {
      isUpdateSeek = true;
      isPlay = true;
      play(selectVideoPath);
    }
  }

  private void play(String selectVideoPath) {
    Log.e("replay", "init play");
    if (mediaPlayer == null) {
      mediaPlayer = new MediaPlayer(getContext());
    }
    if (!isPlay) {
      isPlayCompletion = false;
      isUpdateSeek = true;
      isPlay = true;
      try {
        videoPath = TextUtils.isEmpty(selectVideoPath) ? getVideoPath() : selectVideoPath;
        videoSurfaceView.setVisibility(VISIBLE);
        previewDefaultImage.setVisibility(View.INVISIBLE);
        mediaPlayer.setDataSource(videoPath);
//        if (music != null) {
//          mediaPlayer.setVolume(music.getSoundVolume());
//        }
        surface = new Surface(videoSurfaceView.getSurfaceTexture());
        mediaPlayer.setSurface(surface);
        mediaPlayer.setOnPreparedListener(mp -> {
          Log.e("PreparedListener", "mIsVideoSizeKnown:" + mIsVideoSizeKnown);
          if (mIsVideoSizeKnown) {
            mediaPlayer.setPlayWhenReady(true);
            playAudio();
            startMatch();
          }
        });
        mediaPlayer.prepare();
        mediaPlayer.setPlayWhenReady(true);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnVideoSizeChangedListener(this);
      } catch (Exception e) {
        mediaPlayer.release();
        isPlay = false;
        e.printStackTrace();
      }
    }
  }

  public void replay() {
    replay(null);
  }

  public void replay(String videoPath) {
    if (mediaPlayer != null) {
      if (mediaPlayer.isPlaying()) {
        releaseVideo();
        play(videoPath);
      } else {
        Log.e("replay", "resume");
        seekPosition = 0;
        resume();
      }
    } else {
      Log.e("replay", "play");
      play(isLoop, videoPath);
    }
  }

  private void playAudio() {
    if (music != null) {
      if (audioSamplePlayer == null) {
        audioSamplePlayer = new AudioSamplePlayer(music.getSoundFile());
      }
      if (music != null) {
        mediaPlayer.setVolume(music.getSoundVolume());
      }
      audioSamplePlayer.seekTo(music.getStartPositionMs());
      if (!audioSamplePlayer.isPlaying()) {
        audioSamplePlayer.setVolume(music.getMusicVolume());
        audioSamplePlayer.start();
      }
    }
  }

  private void pauseAudio() {
    if (audioSamplePlayer != null && audioSamplePlayer.isPlaying()) {
      audioSamplePlayer.pause();
    }
  }

  long oldTime = 0;
  int oldPosition = 0;
  int duration = 0;

  private void startMatch() {
    oldTime = System.currentTimeMillis();
    duration = getDuration();
    if (timer == null) {
      timer = new Timer();
      timerTask = new TimerTask() {
        @Override
        public void run() {
          new Handler(Looper.getMainLooper()).post(() -> {
            if (mediaPlayer != null) {
              // int currentPosition = (int) ((currentTimeMillis - oldTime) % duration);
              int currentPosition = (int) mediaPlayer.getCurrentPosition();
              if (currentPosition != oldPosition) {
                // Log.e("startMatch", "currentPosition:" + currentPosition + " frame position:"
                // + (int) (sampleSize * (currentPosition / (float) duration)));
                oldPosition = currentPosition;
                if (currentPosition == 0) {
                  oldShowMontageShowTimeMs = offsetPosition = 0;
                }
                if (!isShowSpeedMontage) {
                  if (isUpdateSeek) {
                    iteratorVideoProgressListeners(duration + montageAdditionalTimeMs,
                        currentPosition + offsetPosition);
                  }
                  matchShowAction(currentPosition + offsetPosition);
                }
              }
            }
          });
        }
      };
      timer.scheduleAtFixedRate(timerTask, 0, 1);
    }
  }

  private void matchShowAction(int currentPosition) {
    if (!isSeekToMontage) {
      matchMontage(currentPosition);
    }
    matchSubtitle(currentPosition);
    matchVideoTag(currentPosition);
    matchChatBox(currentPosition);
    matchSticker(currentPosition);
    matchRunMan(currentPosition);
  }

  private void matchSubtitle(int currentPosition) {
    List<SubtitleContent> subtitleContents = VideoContentTaskManager.getInstance()
        .getCurrentContentTask().matchSubtitle(currentPosition, currentSubtitle);
    List<SubtitleContent> jpSubtitleContents = new ArrayList<>();
    List<SubtitleContent> normalSubtitleContents = new ArrayList<>();
    List<SubtitleContent> backgroundSubtitleContents = new ArrayList<>();
    for (SubtitleContent subtitleContent : subtitleContents) {
      switch (subtitleContent.getSubtitleType()) {
        case JP_STYLE_BLUE:
        case JP_STYLE_BLACK:
        case JP_STYLE_ORIGIN:
        case JP_STYLE_ORANGE:
        case JP_STYLE_YELLOW:
          jpSubtitleContents.add(subtitleContent);
          break;
        case LOVE_NOTE_YELLOW:
        case LOVE_NOTE_WHITE:
        case LOVE_NOTE_LIGHT_YELLOW:
        case OFFICE_STORY_BLUE:
        case OFFICE_STORY_RED:
        case OFFICE_STORY_YELLOW:
          backgroundSubtitleContents.add(subtitleContent);
        default:
          normalSubtitleContents.add(subtitleContent);
          break;
      }
    }
    matchBackgroundSubtitle(backgroundSubtitleContents);
    matchNormalSubtitle(normalSubtitleContents);
    matchJpSubtitle(jpSubtitleContents);
  }

  private void matchBackgroundSubtitle(List<SubtitleContent> subtitleContents) {
    if (subtitleContents != null && !subtitleContents.isEmpty()) {
      for (int i = 0; i < subtitleContents.size(); i++) {
        SubtitleContent subtitleContent = subtitleContents.get(i);
        BackgroundSubtitleContentView backgroundSubtitleContentView =
            oldBackgroundSubtitleContentViewHashMap.get(subtitleContent);
        if (backgroundSubtitleContentView == null) {
          backgroundSubtitleContentView = new BackgroundSubtitleContentView(getContext());
          backgroundSubtitleContentView.setVisibility(GONE);
          oldBackgroundSubtitleContentViewHashMap.put(subtitleContent,
              backgroundSubtitleContentView);
          oldEditActionView.addView(backgroundSubtitleContentView);
        }
        backgroundSubtitleContentView.setVisibility(View.VISIBLE);
        backgroundSubtitleContentView.setSubtitleContent(subtitleContent, false);
      }
      Iterator<Map.Entry<SubtitleContent, NormalSubtitleContentView>> iterator =
          oldNormalSubtitleContentViewMap.entrySet().iterator();
      w:
      while (iterator.hasNext()) {
        Map.Entry<SubtitleContent, NormalSubtitleContentView> entry = iterator.next();
        for (SubtitleContent subtitleContent : subtitleContents) {
          if (subtitleContent.equals(entry.getKey())) {
            continue w;
          }
        }
        entry.getValue().setVisibility(INVISIBLE);
      }
    } else {
      Iterator<Map.Entry<SubtitleContent, BackgroundSubtitleContentView>> iterator =
          oldBackgroundSubtitleContentViewHashMap.entrySet().iterator();
      while (iterator.hasNext()) {
        Map.Entry<SubtitleContent, BackgroundSubtitleContentView> entry = iterator.next();
        entry.getValue().setVisibility(INVISIBLE);
      }
    }
  }

  private void matchNormalSubtitle(List<SubtitleContent> subtitleContents) {
    if (subtitleContents != null && !subtitleContents.isEmpty()) {
      for (int i = 0; i < subtitleContents.size(); i++) {
        SubtitleContent subtitleContent = subtitleContents.get(i);
        NormalSubtitleContentView normalSubtitleContentView =
            oldNormalSubtitleContentViewMap.get(subtitleContent);
        if (normalSubtitleContentView == null) {
          normalSubtitleContentView = new NormalSubtitleContentView(getContext());
          normalSubtitleContentView.setVisibility(GONE);
          oldNormalSubtitleContentViewMap.put(subtitleContent, normalSubtitleContentView);
          oldEditActionView.addView(normalSubtitleContentView);
        }
        normalSubtitleContentView.setVisibility(View.VISIBLE);
        normalSubtitleContentView.setSubtitleContent(subtitleContent, false);
      }
      Iterator<Map.Entry<SubtitleContent, NormalSubtitleContentView>> iterator =
          oldNormalSubtitleContentViewMap.entrySet().iterator();
      w:
      while (iterator.hasNext()) {
        Map.Entry<SubtitleContent, NormalSubtitleContentView> entry = iterator.next();
        for (SubtitleContent subtitleContent : subtitleContents) {
          if (subtitleContent.equals(entry.getKey())) {
            continue w;
          }
        }
        entry.getValue().setVisibility(INVISIBLE);
      }
    } else {
      Iterator<Map.Entry<SubtitleContent, NormalSubtitleContentView>> iterator =
          oldNormalSubtitleContentViewMap.entrySet().iterator();
      while (iterator.hasNext()) {
        Map.Entry<SubtitleContent, NormalSubtitleContentView> entry = iterator.next();
        entry.getValue().setVisibility(INVISIBLE);
      }
    }
  }

  private void matchJpSubtitle(List<SubtitleContent> subtitleContents) {
    if (subtitleContents != null && !subtitleContents.isEmpty()) {
      for (int i = 0; i < subtitleContents.size(); i++) {
        SubtitleContent subtitleContent = subtitleContents.get(i);
        JPSubtitleContentView jpSubtitleContentView =
            oldJpSubtitleContentViewMap.get(subtitleContent);
        if (jpSubtitleContentView == null) {
          jpSubtitleContentView = new JPSubtitleContentView(getContext());
          jpSubtitleContentView.setVisibility(GONE);
          oldJpSubtitleContentViewMap.put(subtitleContent, jpSubtitleContentView);
          oldEditActionView.addView(jpSubtitleContentView);
        }
        jpSubtitleContentView.setVisibility(View.VISIBLE);
        jpSubtitleContentView.setSubtitleContent(subtitleContent, false);
      }
      Iterator<Map.Entry<SubtitleContent, JPSubtitleContentView>> iterator =
          oldJpSubtitleContentViewMap.entrySet().iterator();
      w:
      while (iterator.hasNext()) {
        Map.Entry<SubtitleContent, JPSubtitleContentView> entry = iterator.next();
        for (SubtitleContent subtitleContent : subtitleContents) {
          if (subtitleContent.equals(entry.getKey())) {
            continue w;
          }
        }
        entry.getValue().setVisibility(INVISIBLE);
      }
    } else {
      Iterator<Map.Entry<SubtitleContent, JPSubtitleContentView>> iterator =
          oldJpSubtitleContentViewMap.entrySet().iterator();
      while (iterator.hasNext()) {
        Map.Entry<SubtitleContent, JPSubtitleContentView> entry = iterator.next();
        entry.getValue().setVisibility(INVISIBLE);
      }
    }
  }

  private void initSubtitle(Subtitle subtitle) {
    SubtitleContent subtitleContent = subtitle.getSubtitleContent();
    switch (subtitle.getSubtitleContent().getSubtitleType()) {
      case JP_STYLE_BLUE:
      case JP_STYLE_BLACK:
      case JP_STYLE_ORIGIN:
      case JP_STYLE_ORANGE:
      case JP_STYLE_YELLOW:
        JPSubtitleContentView jpSubtitleContentView = oldJpSubtitleContentViewMap.get(subtitleContent);
        if (jpSubtitleContentView == null) {
          jpSubtitleContentView = new JPSubtitleContentView(getContext());
          jpSubtitleContentView.setVisibility(INVISIBLE);
          oldJpSubtitleContentViewMap.put(subtitleContent, jpSubtitleContentView);
          oldEditActionView.addView(jpSubtitleContentView);
        }
        jpSubtitleContentView.setVisibility(View.INVISIBLE);
        jpSubtitleContentView.setSubtitleContent(subtitleContent, false);
        break;
      case LOVE_NOTE_YELLOW:
      case LOVE_NOTE_WHITE:
      case LOVE_NOTE_LIGHT_YELLOW:
      case OFFICE_STORY_BLUE:
      case OFFICE_STORY_RED:
      case OFFICE_STORY_YELLOW:
        BackgroundSubtitleContentView backgroundSubtitleContentView =
            oldBackgroundSubtitleContentViewHashMap.get(subtitleContent);
        if (backgroundSubtitleContentView == null) {
          backgroundSubtitleContentView = new BackgroundSubtitleContentView(getContext());
          backgroundSubtitleContentView.setVisibility(GONE);
          oldBackgroundSubtitleContentViewHashMap.put(subtitleContent,
              backgroundSubtitleContentView);
          oldEditActionView.addView(backgroundSubtitleContentView);
        }
        backgroundSubtitleContentView.setVisibility(View.INVISIBLE);
        backgroundSubtitleContentView.setSubtitleContent(subtitleContent, false);
        break;
      default:
        NormalSubtitleContentView normalSubtitleContentView =
            oldNormalSubtitleContentViewMap.get(subtitleContent);
        if (normalSubtitleContentView == null) {
          normalSubtitleContentView = new NormalSubtitleContentView(getContext());
          normalSubtitleContentView.setVisibility(GONE);
          oldNormalSubtitleContentViewMap.put(subtitleContent, normalSubtitleContentView);
          oldEditActionView.addView(normalSubtitleContentView);
        }
        normalSubtitleContentView.setVisibility(View.INVISIBLE);
        normalSubtitleContentView.setSubtitleContent(subtitleContent, false);
        break;
    }

  }

  private void initRunMan(RunMan runMan) {
    RunManContent runManContent = runMan.getRunManContent();
    RunManContentView runManContentView = oldRunManContentViews.get(runManContent);
    if (runManContentView == null) {
      runManContentView = new RunManContentView(getContext());
      runManContentView.setVisibility(INVISIBLE);
      oldRunManContentViews.put(runManContent, runManContentView);
      oldEditActionView.addView(runManContentView);
    }
    if (runmanRecordLocation != null) {
      runManContentView.setRunManContent(runManContent, runmanRecordLocation);
    } else {
      runManContentView.setRunManContent(runManContent, new RecordLocation(0, 0, null));
    }
    runManContentView.setVisibility(View.GONE);
  }

  private void initVideoTag(VideoTagContent videoTagContent) {
    if(videoTagContent != null) {
      VideoTagContentView videoTagContentView = videoTagContentViewMap.get(videoTagContent);
      if(videoTagContentView == null){
        videoTagContentView = new VideoTagContentView(getContext());
        videoTagContentView.setVideoTagContent(videoTagContent,null,true,false);
        oldEditActionView.addView(videoTagContentView);
        videoTagContentView.setVisibility(INVISIBLE);
        videoTagContentViewMap.put(videoTagContent, videoTagContentView);
      }
    }
  }

  public void setVideoActionParams(VideoActionParams videoActionParams) {
    this.videoActionParams = videoActionParams;
  }

  private void matchVideoTag(int currentPosition) {
    List<VideoTagLocation> videoTagLocationList = VideoContentTaskManager.getInstance()
        .getCurrentContentTask().matchVideoTag(currentPosition, currentVideoTag);
      Stream.of(videoTagLocationList).forEach(videoTagLocation ->{
        VideoTagContentView videoTagContentView = videoTagContentViewMap.get(videoTagLocation.getVideoTagContent());
        if(videoTagContentView != null) {
          videoTagContentView.setVisibility(VISIBLE);
          videoTagContentView.setVideoTagContent(videoTagLocation.getVideoTagContent(),
              videoTagLocation.getRecordLocation(), false, false);
        }
      });

      List<VideoTagContent> visiableVideoTagContents = Stream.of(videoTagLocationList)
          .map(videoTagLocation -> videoTagLocation.getVideoTagContent()).distinct()
          .collect(Collectors.toList());
      Stream.of(VideoContentTaskManager.getInstance().getCurrentContentTask().getVideoTags())
          .forEach(videoTag -> {
            VideoTagContentView videoTagContentView = videoTagContentViewMap.get(videoTag.getVideoTagContent());
            if (!visiableVideoTagContents.contains(videoTag.getActionContent())) {
              videoTagContentView.setVisibility(INVISIBLE);
            }
          });
  }

  private void initChatBox() {
    ChatBoxContentView chatBoxContentView = new ChatBoxContentView(getContext());
    oldEditActionView.addView(chatBoxContentView);
    oldChatBoxContentViews.add(chatBoxContentView);
    chatBoxContentView.setVisibility(INVISIBLE);
  }

  private void matchChatBox(int currentPosition) {
    List<ChatBoxLocation> chatBoxLocations =
        VideoContentTaskManager.getInstance().getCurrentContentTask()
            .matchChatBox(currentPosition, currentChatBox);
    if (oldChatBoxContentViews.size() < chatBoxLocations.size()) {
      int x = chatBoxLocations.size() - oldChatBoxContentViews.size();
      for (int i = 0; i < x; i++) {
        ChatBoxContentView chatBoxContentView = new ChatBoxContentView(getContext());
        oldEditActionView.addView(chatBoxContentView);
        oldChatBoxContentViews.add(chatBoxContentView);
      }
    } else if (oldChatBoxContentViews.size() > chatBoxLocations.size()) {
      int x = oldChatBoxContentViews.size() - chatBoxLocations.size();
      for (int i = 0; i < x; i++) {
        oldChatBoxContentViews.get(i).setVisibility(View.INVISIBLE);
      }
    }
    if (chatBoxLocations != null && !chatBoxLocations.isEmpty()) {
      for (int i = 0; i < chatBoxLocations.size(); i++) {
        ChatBoxLocation chatBoxLocation = chatBoxLocations.get(i);
        ChatBoxContentView chatBoxContentView =
            oldChatBoxContentViews.get(i);
        chatBoxContentView.setVisibility(View.VISIBLE);
        chatBoxContentView.setChatBoxContent(chatBoxLocation.getChatBoxContent(),
            chatBoxLocation.getRecordLocation());
      }
    }
  }

  private void initSticker(Sticker sticker) {
    List<RecordActionLocationTask> recordActionLocationTasks = sticker.getRecordActionLocationTasks();
    for (RecordActionLocationTask recordActionLocationTask : recordActionLocationTasks) {
      StickerContentView stickerContentView = new StickerContentView(getContext());
      oldStickerContentViews.put(recordActionLocationTask.getCreateTimeMs(), stickerContentView);
      oldEditActionView.addView(stickerContentView);
      Map<Integer, RecordLocation> recordLocationTaskMap = recordActionLocationTask.getShowLocationTaskMap();
      if (!recordLocationTaskMap.isEmpty()) {
        RecordLocation recordLocation = recordLocationTaskMap.get(recordActionLocationTask.getCreateTimeMs());
        if (recordLocation != null) {
          stickerContentView.setStickerImageData(sticker.getStickerImageData(), recordLocation);
        }
      }
      stickerContentView.setVisibility(INVISIBLE);
    }
  }

  private void matchSticker(int currentPosition) {
    List<StickerLocation> stickerLocations = VideoContentTaskManager.getInstance().getCurrentContentTask()
        .matchSticker(currentPosition, currentSticker);
    if (stickerLocations != null && !stickerLocations.isEmpty()) {
      for (int i = 0; i < stickerLocations.size(); i++) {
        StickerLocation stickerLocation = stickerLocations.get(i);
        StickerContentView stickerContentView =
            oldStickerContentViews.get(stickerLocation.getCreateTimeMs());
        if (stickerContentView == null) {
          stickerContentView = new StickerContentView(getContext());
          stickerContentView.setVisibility(GONE);
          oldStickerContentViews.put(stickerLocation.getCreateTimeMs(), stickerContentView);
          oldEditActionView.addView(stickerContentView);
        }
        stickerContentView.setStickerImageData(stickerLocation.getStickerImageData(),
            stickerLocation.getRecordLocation());
        stickerContentView.setVisibility(View.VISIBLE);
      }
      Iterator<Map.Entry<Long, StickerContentView>> iterator =
          oldStickerContentViews.entrySet().iterator();
      w:
      while (iterator.hasNext()) {
        Map.Entry<Long, StickerContentView> entry = iterator.next();
        for (StickerLocation stickerLocation : stickerLocations) {
          if (stickerLocation.getCreateTimeMs() == entry.getKey()) {
            continue w;
          }
        }
        entry.getValue().setVisibility(INVISIBLE);
      }
    } else {
      Iterator<Map.Entry<Long, StickerContentView>> iterator =
          oldStickerContentViews.entrySet().iterator();
      while (iterator.hasNext()) {
        Map.Entry<Long, StickerContentView> entry = iterator.next();
        entry.getValue().setVisibility(INVISIBLE);
      }
    }
  }

  private void matchMontage(int currentPosition) {
    if (this.currentShowMontageTimeLine == null) {
      if (currentMontage != null) {
        MontageTimeLine montageTimeLine =
            VideoContentTaskManager.getInstance().getCurrentContentTask()
                .matchMontage(currentPosition, currentMontage);
        if (montageTimeLine != null) {
          showMontage(montageTimeLine);
        }
      }
    }
  }

  private void showMontage(MontageTimeLine montageTimeLine) {
    this.currentShowMontageTimeLine = montageTimeLine;
    switch (montageTimeLine.getMontageType()) {
      case FREEZE:
        showFreezeMontage();
        break;
      case REPEAT:
        showRepeatMontage();
        break;
      case FORWARD:
        showForwardMontage();
        break;
      case SLOW_MOTION:
        showSlowMontage();
        break;
    }
  }

  private void showRepeatMontage() {
    showMontageOffsetTime = showMontageTime = System.currentTimeMillis();
    loopRepeatMontageFrame();
  }

  private void loopRepeatMontageFrame() {
    mainHandler.postDelayed(() -> {
      long currentTimeMs = System.currentTimeMillis();
      long offsetTime = currentTimeMs - showMontageOffsetTime;
      showMontageOffsetTime = currentTimeMs;
      if (currentRepeatMontageShowNum > 1) {
        offsetPosition += offsetTime;
      }
      if (currentTimeMs - showMontageTime >= (currentShowMontageTimeLine.getTimeLine().getDuration()) * 3) {
        currentRepeatMontageShowNum = 1;
        currentShowMontageTimeLine = null;
        Log.e("loopRepeatMontageFrame", "currentShowMontageTimeLine = null");
      } else {
        if ((currentRepeatMontageShowNum < 2 && currentTimeMs - showMontageTime >= (currentShowMontageTimeLine.getTimeLine().getDuration())) || (currentRepeatMontageShowNum < 3 && currentTimeMs - showMontageTime >= (currentShowMontageTimeLine.getTimeLine().getDuration()) * 2)) {
          currentRepeatMontageShowNum++;
          if (mediaPlayer != null) {
            mediaPlayer.seekTo(currentShowMontageTimeLine.getTimeLine().getStartTimeMs() + 1);
          }
        }
        if (mediaPlayer != null) {
          int currentPosition = (int) mediaPlayer.getCurrentPosition();
          if (isUpdateSeek) {
            iteratorVideoProgressListeners((int) mediaPlayer.getDuration() + montageAdditionalTimeMs, currentPosition + offsetPosition);
          }
          matchShowAction(currentPosition + offsetPosition);
          loopRepeatMontageFrame();
        }
      }
    }, 1);
  }

  private void showFreezeMontage() {
    mediaPlayer.setPlayWhenReady(false);
    showMontageOffsetTime = showMontageTime = System.currentTimeMillis();
    loopFreezeMontageFrame();
  }

  private void loopFreezeMontageFrame() {
    mainHandler.postDelayed(() -> {
      long currentTimeMs = System.currentTimeMillis();
      long offsetTime = currentTimeMs - showMontageOffsetTime;
      showMontageOffsetTime = currentTimeMs;
      offsetPosition += offsetTime;
      if (currentTimeMs - showMontageTime >= (currentShowMontageTimeLine.getTimeLine().getDuration())) {
        if (mediaPlayer != null) {
          mediaPlayer.setPlayWhenReady(true);
        }
        currentShowMontageTimeLine = null;
      } else {
        if (mediaPlayer != null) {
          int currentPosition = (int) mediaPlayer.getCurrentPosition();
          if (isUpdateSeek) {
            iteratorVideoProgressListeners((int) mediaPlayer.getDuration() + montageAdditionalTimeMs, currentPosition + offsetPosition);
          }
          matchShowAction(currentPosition);
          loopFreezeMontageFrame();
        }
      }
    }, 1);
  }

  private void showForwardMontage() {
    isShowSpeedMontage = true;
    mediaPlayer.setPlaybackSpeed(MontageType.FORWARD.getSpeedSize());
    showMontageOffsetTime = showMontageTime = System.currentTimeMillis();
    loopSpeedMontageFrame(MontageType.FORWARD);
  }

  private void showSlowMontage() {
    isShowSpeedMontage = true;
    mediaPlayer.setPlaybackSpeed(MontageType.SLOW_MOTION.getSpeedSize());
    showMontageOffsetTime = showMontageTime = System.currentTimeMillis();
    loopSpeedMontageFrame(MontageType.SLOW_MOTION);
  }

  private void loopSpeedMontageFrame(MontageType montageType) {
    mainHandler.postDelayed(() -> {
      long currentTimeMs = System.currentTimeMillis();
      long offsetTime = currentTimeMs - showMontageOffsetTime;
      showMontageOffsetTime = currentTimeMs;
      switch (montageType) {
        case FORWARD:
          break;
        case SLOW_MOTION:
          offsetPosition += offsetTime * MontageType.SLOW_MOTION.getSpeedSize();
          break;
      }
      if (currentTimeMs - showMontageTime >= (currentShowMontageTimeLine.getTimeLine().getDuration() * montageType.getDurationProportion())) {
        isShowSpeedMontage = false;
        if (mediaPlayer != null) {
          mediaPlayer.setPlaybackSpeed(1f);
        }
        currentShowMontageTimeLine = null;
      } else {
        if (mediaPlayer != null) {
          int currentPosition = (int) mediaPlayer.getCurrentPosition();
          if (isUpdateSeek) {
            iteratorVideoProgressListeners((int) mediaPlayer.getDuration() + montageAdditionalTimeMs, currentPosition + offsetPosition);
          }
          matchShowAction(currentPosition);
          loopSpeedMontageFrame(montageType);
        }
      }
    }, 1);
  }

  private void iteratorVideoProgressListeners(int videoDuration, int progress) {
    Iterator<WeakReference<OnVideoPlayProgressListener>> it = onVideoPlayProgressListeners.iterator();
    while (it.hasNext()) {
      OnVideoPlayProgressListener listener = it.next().get();
      if (null != listener) {
        listener.onVideoPlayProgress(videoDuration, progress);
      } else {
        it.remove();
      }
    }
  }

  private void matchRunMan(int currentPosition) {
    List<RunManLocation> runManLocations = VideoContentTaskManager.getInstance().getCurrentContentTask()
        .matchRunMan(currentPosition, currentRunMan);
    if (runManLocations != null && !runManLocations.isEmpty()) {
      for (int i = 0; i < runManLocations.size(); i++) {
        RunManLocation runManLocation = runManLocations.get(i);
        RunManContentView runManContentView = oldRunManContentViews.get(runManLocation.getRunManContent());
        if (runManContentView == null) {
          runManContentView = new RunManContentView(getContext());
          runManContentView.setVisibility(GONE);
          oldRunManContentViews.put(runManLocation.getRunManContent(), runManContentView);
          oldEditActionView.addView(runManContentView);
        } else {
        }
        boolean isPlaySound = runManContentView.getVisibility() != View.VISIBLE && isPlay();
        runManContentView.setVisibility(View.VISIBLE);
        runManContentView.setRunManContent(runManLocation.getRunManContent(),
            runManLocation.getRecordLocation(), false, isPlaySound);
      }
      Iterator<Map.Entry<RunManContent, RunManContentView>> iterator =
          oldRunManContentViews.entrySet().iterator();
      w:
      while (iterator.hasNext()) {
        Map.Entry<RunManContent, RunManContentView> entry = iterator.next();
        for (RunManLocation runManLocation : runManLocations) {
          if (runManLocation.getRunManContent() == entry.getKey()) {
            continue w;
          }
        }
        entry.getValue().setVisibility(INVISIBLE);
      }
    } else {
      Iterator<Map.Entry<RunManContent, RunManContentView>> iterator =
          oldRunManContentViews.entrySet().iterator();
      while (iterator.hasNext()) {
        Map.Entry<RunManContent, RunManContentView> entry = iterator.next();
        entry.getValue().setVisibility(INVISIBLE);
      }
    }
  }

  private void stopMatch() {
    if (timer != null) {
      timer.cancel();
      timer = null;
      timerTask.cancel();
      timerTask = null;
    }
  }

  public void releaseAndCleanVideo() {
    releaseVideo();
    cleanCurrentActionRecordLocationTask();
  }

  public void releaseVideo() {
    Log.e("releaseVideo", "releaseVideo");
    if (mediaPlayer != null) {
      if (mediaPlayer.isPlaying()) {
        mediaPlayer.stop();
      }
      mediaPlayer.release();
      mediaPlayer = null;
    }
    if (audioSamplePlayer != null) {
      if (audioSamplePlayer.isPlaying() || audioSamplePlayer.isPaused()) {
        audioSamplePlayer.stop();
      }
      audioSamplePlayer.release();
      audioSamplePlayer = null;
    }
    isPlay = false;
    stopMatch();
  }

  public void updateMusic(Music music) {
    this.music = music;
    replay();
  }

  public void changeMusicFile(Music music) {
    this.music = music;
    if (audioSamplePlayer != null) {
      audioSamplePlayer.pause();
      audioSamplePlayer.release();
      audioSamplePlayer = null;
    }
    setLooping(true);
    replay();
  }

  public void setVolume(Music music) {
    this.music = music;
    if (audioSamplePlayer != null && audioSamplePlayer.isPlaying()) {
      audioSamplePlayer.setVolume(music.getMusicVolume());
    }
    if (mediaPlayer != null) {
      mediaPlayer.setVolume(music.getSoundVolume());
    }
  }

  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    releaseVideo();
  }

  public int getCurrentPosition() {
    if (mediaPlayer != null) {
      return (int) mediaPlayer.getCurrentPosition();
    }
    return 0;
  }

  private boolean isSeekToMontage = false;

  public void seekTo(int position) {
    SeekData seekData =
        VideoContentTaskManager.getInstance().getCurrentContentTask().getSeekPosition(position);
    if (seekData.getActionContent() != null) {
      isSeekToMontage = true;
    } else {
      isSeekToMontage = false;
    }
    offsetPosition = seekData.getOffsetPosition();
    this.seekPosition = seekData.getNewPosition();
    if (mediaPlayer != null) {
      if (position == mediaPlayer.getDuration()) {
        isPlayCompletion = true;
      } else {
        isPlayCompletion = false;
      }
      mediaPlayer.seekTo(seekPosition);
      seekAudio(seekPosition);
    } else {
      prepareVideo(false);
    }
  }

  public void resume() {
    isSeekToMontage = false;
    if (mediaPlayer == null) {
      play(isLoop);
    } else {
      resumeMediaPlayer();
    }
  }

  public void setMontageIconGone() {
    if (montageIcon.getVisibility() == View.VISIBLE) {
      montageIcon.setVisibility(GONE);
    }
  }

  private void resumeMediaPlayer() {
    if (surface != null && surface.isValid() && mediaPlayer != null) {
      isPlayCompletion = false;
      isUpdateSeek = true;
      mediaPlayer.start();
      if (audioSamplePlayer != null) {
        audioSamplePlayer.start();
        audioSamplePlayer.seekTo(seekPosition);
      }
    }
  }

  public void pause() {
    pauseMediaPlayer();
  }

  private void pauseMediaPlayer() {
    isUpdateSeek = false;
    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
      mediaPlayer.pause();
      seekPosition = (int) mediaPlayer.getCurrentPosition();
    }
    if (audioSamplePlayer != null && audioSamplePlayer.isPlaying()) {
      audioSamplePlayer.pause();
    }
  }

  private void seekAudio(int seekPosition) {
    this.seekPosition = seekPosition;
    if (audioSamplePlayer != null && audioSamplePlayer.isPlaying()) {
      audioSamplePlayer.seekTo(seekPosition);
    }
  }

  public void showFrameThemeImage() {
    if (frameThemeImage == null) {
      frameThemeImage = getFrameThemeImage();
    }
    videoSurfaceView.setVisibility(GONE);
    previewDefaultImage.setVisibility(VISIBLE);
    LayoutParams imageLayoutParams =
        (LayoutParams) previewDefaultImage.getLayoutParams();
    imageLayoutParams.height = LayoutParams.MATCH_PARENT;
    previewDefaultImage.setLayoutParams(imageLayoutParams);
    previewDefaultImage.setBackgroundDrawable(new BitmapDrawable(getResources(), frameThemeImage));
  }

  private void prepareVideo() {
    if (mediaPlayer == null) {
      mediaPlayer = new MediaPlayer(getContext());
    }
    if (!isPlay) {
      isUpdateSeek = false;
      isPlayCompletion = false;
      try {
        videoSurfaceView.setVisibility(VISIBLE);
        previewDefaultImage.setVisibility(View.INVISIBLE);
        mediaPlayer.setDataSource(videoPath);
        if (music != null) {
          mediaPlayer.setVolume(music.getSoundVolume());
        }
        surface = new Surface(videoSurfaceView.getSurfaceTexture());
        mediaPlayer.setSurface(surface);
        mediaPlayer.setOnPreparedListener(mp -> {
          Log.e("PreparedListener", "Prepared");
          mediaPlayer.seekTo(VideoContentTaskManager.getInstance().getCurrentContentTask()
              .getSeekPosition(seekPosition).getNewPosition());
          startMatch();
          isPrepare = true;
        });
        mediaPlayer.prepare();
        mediaPlayer.setOnCompletionListener(this);
      } catch (Exception e) {
        mediaPlayer.release();
        e.printStackTrace();
      }
    }
  }

  public void prepareVideo(boolean isLoop) {
    this.isLoop = isLoop;
    videoSurfaceView.setVisibility(VISIBLE);
    videoSurfaceView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
      @Override
      public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        prepareVideo();
      }

      @Override
      public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

      }

      @Override
      public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
      }

      @Override
      public void onSurfaceTextureUpdated(SurfaceTexture surface) {

      }
    });
  }


  private Bitmap getFrameThemeImage() {
    MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
    mediaMetadataRetriever.setDataSource(videoPath);
    Bitmap frameThemeImage = mediaMetadataRetriever.getFrameAtTime();
    mediaMetadataRetriever.release();
    return frameThemeImage;
  }


  public VideoTag getCurrentVideoTag() {
    return currentVideoTag;
  }

  public ChatBox getCurrentChatBox() {
    return currentChatBox;
  }

  public Sticker getCurrentSticker() {
    return currentSticker;
  }

  public Montage getCurrentMontage() {
    return currentMontage;
  }

  public void setCurrentSubtitle(SubtitleContent subtitleContent) {
    isLoop = false;
    subtitleContentView.setOnRecordLocationChangeListener(this);
    subtitleContentView.setOnRecordStatusChangeListener(this);
  }

  public void showCurrentSubtitle(SubtitleContent subtitleContent) {
    recordActionType = ActionType.SUBTITLE;
    currentSubtitle = new Subtitle(subtitleContent);
    if (subtitleContentView != null) {
      subtitleContentView.setVisibility(GONE);
      subtitleContainer.removeView(subtitleContentView);
    }
    switch (subtitleContent.getSubtitleType()) {
      case JP_STYLE_ORANGE:
      case JP_STYLE_ORIGIN:
      case JP_STYLE_YELLOW:
      case JP_STYLE_BLACK:
      case JP_STYLE_BLUE:
        subtitleContentView = new JPSubtitleContentView(getContext());
        setJpSubtitleContent(subtitleContent);
        break;
      case OFFICE_STORY_RED:
      case OFFICE_STORY_YELLOW:
      case OFFICE_STORY_BLUE:
      case LOVE_NOTE_LIGHT_YELLOW:
      case LOVE_NOTE_YELLOW:
        subtitleContentView = new BackgroundSubtitleContentView(getContext());
        setBackgroundSubtitleContent(subtitleContent);
        break;
      case LOVE_NOTE_WHITE:
        subtitleContentView = new BackgroundSubtitleContentView(getContext());
        setBackgroundSubtitleContent(subtitleContent);
        break;
      default:
        subtitleContentView = new NormalSubtitleContentView(getContext());
        setNormalSubtitleContent(subtitleContent);
        break;
    }

    subtitleContainer.addView(subtitleContentView);
    subtitleContentView.setOnRecordLocationChangeListener(null);
    subtitleContentView.setOnRecordStatusChangeListener(null);
  }

  private void setNormalSubtitleContent(SubtitleContent subtitleContent) {
    String imageFileName = System.currentTimeMillis() + ".jpg";
    String imageFilePath = new File(videoPath).getParent() + "/" + imageFileName;
    NormalSubtitleContentView normalSubtitleContentView =
        (NormalSubtitleContentView) subtitleContentView;
    normalSubtitleContentView.setSubtitleContent(subtitleContent, true);
    normalSubtitleContentView.setOnRecordStatusChangeListener(this);
    normalSubtitleContentView.setVisibility(View.VISIBLE);
    switch (subtitleContent.getSubtitleType()) {
      case CARTOON_EN:
      case CLASSIC_EN:
//        DataClient.translate(new TranslateRequest(LanguageType.EN, subtitleContent.getContent()),
//            new Callback<String>() {
//              @Override
//              public void success(String s, Response response) {
//                subtitleContent.setTranslateContent(s);
//                currentSubtitle.getSubtitleContent().setTranslateContent(s);
//                normalSubtitleContentView.setSubtitleContent(subtitleContent, true, true);
//                subtitleContentView.saveImage(imageFilePath, videoPath,
//                    videoTagImageData -> currentSubtitle.setActionImageData(videoTagImageData));
//              }
//
//              @Override
//              public void failure(RetrofitError error) {
//
//              }
//            });
        break;
      case CARTOON_JP:
      case CLASSIC_JP:
//      case TWO_LANGUAGE_JP:
//        DataClient.translate(new TranslateRequest(LanguageType.JP, subtitleContent.getContent()),
//            new Callback<String>() {
//              @Override
//              public void success(String s, Response response) {
//                subtitleContent.setTranslateContent(s);
//                currentSubtitle.getSubtitleContent().setTranslateContent(s);
//                normalSubtitleContentView.setSubtitleContent(subtitleContent, true, true);
//                subtitleContentView.saveImage(imageFilePath, videoPath,
//                    videoTagImageData -> currentSubtitle.setActionImageData(videoTagImageData));
//              }
//
//              @Override
//              public void failure(RetrofitError error) {
//
//              }
//            });
        break;
    }
    normalSubtitleContentView.setSubtitleContent(subtitleContent, true);
    subtitleContentView.saveImage(imageFilePath, videoPath,
        videoTagImageData -> currentSubtitle.setActionImageData(videoTagImageData));
  }

  private void setBackgroundSubtitleContent(SubtitleContent subtitleContent) {
    String imageFileName = System.currentTimeMillis() + ".jpg";
    String imageFilePath = new File(videoPath).getParent() + "/" + imageFileName;
    BackgroundSubtitleContentView backgroundSubtitleContentView =
        (BackgroundSubtitleContentView) subtitleContentView;
    backgroundSubtitleContentView.setSubtitleContent(subtitleContent, true);
    backgroundSubtitleContentView.setOnRecordStatusChangeListener(this);
    backgroundSubtitleContentView.setVisibility(View.VISIBLE);
//    DataClient.translate(new TranslateRequest(LanguageType.EN, subtitleContent.getContent()), new Callback<String>() {
//      @Override
//      public void success(String s, Response response) {
//        subtitleContent.setTranslateContent(s);
//        currentSubtitle.getSubtitleContent().setTranslateContent(s);
//        backgroundSubtitleContentView.setSubtitleContent(subtitleContent, true, true);
//        subtitleContentView.saveImage(imageFilePath, videoPath, videoTagImageData -> currentSubtitle.setActionImageData(videoTagImageData));
//      }
//
//      @Override
//      public void failure(RetrofitError error) {
//
//      }
//    });
    subtitleContentView.saveImage(imageFilePath, videoPath,
        videoTagImageData -> currentSubtitle.setActionImageData(videoTagImageData));
  }

  private void setJpSubtitleContent(SubtitleContent subtitleContent) {
    String imageFileName = System.currentTimeMillis() + ".jpg";
    String imageFilePath = new File(videoPath).getParent() + "/" + imageFileName;
    subtitleContentView.setSubtitleContent(subtitleContent, true, false);
    subtitleContentView.setOnRecordStatusChangeListener(this);
    subtitleContentView.setVisibility(View.VISIBLE);
//    DataClient.translate(new TranslateRequest(LanguageType.JP, subtitleContent.getContent()), new Callback<String>() {
//      @Override
//      public void success(String s, Response response) {
//        subtitleContent.setTranslateContent(s);
//        currentSubtitle.getSubtitleContent().setTranslateContent(s);
//        subtitleContentView.setSubtitleContent(subtitleContent, true, true);
//        subtitleContentView.saveImage(imageFilePath, videoPath, videoTagImageData -> currentSubtitle.setActionImageData(videoTagImageData));
//      }
//
//      @Override
//      public void failure(RetrofitError error) {
//
//      }
//    });
    subtitleContentView.setSubtitleContent(subtitleContent, true, false);
    subtitleContentView.saveImage(imageFilePath, videoPath, videoTagImageData -> currentSubtitle.setActionImageData(videoTagImageData));
  }

  public void setCurrentVideoTag(VideoTagContent currentVideoTagContent) {
    isLoop = false;
    recordActionType = ActionType.VIDEO_TAG;
    currentVideoTag = new VideoTag(currentVideoTagContent);
    currentVideoTagContentView.setVisibility(VISIBLE);
    if (videoTagRecordLocation == null) {
      currentVideoTagContentView.setVideoTagContent(currentVideoTagContent, new RecordLocation(0, 0, null), true, false);
    } else {
      currentVideoTagContentView.setVideoTagContent(currentVideoTagContent, videoTagRecordLocation, true, false);
    }
    String videoTagImageFileName = System.currentTimeMillis() + ".jpg";
    String videoTagImageFilePath = new File(videoPath).getParent() + "/" + videoTagImageFileName;
    currentVideoTagContentView.saveVideoTagImage(videoTagImageFilePath, videoPath,
        videoTagImageData -> currentVideoTag.setActionImageData(videoTagImageData));
    currentVideoTagContentView.setOnRecordLocationChangeListener(this);
    currentVideoTagContentView.setOnRecordStatusChangeListener(this);
  }

  public void setCurrentMontageActionType(MontageType montageActionType) {
    isLoop = false;
    recordActionType = ActionType.MONTAGE;
    currentMontage = new Montage(montageActionType);
    montageIcon.setVisibility(VISIBLE);
    currentMontageContentView.setVisibility(VISIBLE);
    switch (currentMontage.getMontageType()) {
      case FREEZE:
        montageIcon.setImageResource(R.drawable.freeze_icon);
        break;
      case SLOW_MOTION:
        montageIcon.setImageResource(R.drawable.slow_icon);
        break;
      case FORWARD:
        montageIcon.setImageResource(R.drawable.fast_icon);
        break;
      case REPEAT:
        montageIcon.setImageResource(R.drawable.repeat_icon);
        break;
    }
    currentMontageContentView.setMontageType(montageActionType, new RecordLocation(0, 0, null));
    currentMontageContentView.setOnRecordStatusChangeListener(this);
  }

  public void setChatBoxContent(ChatBoxContent chatBoxContent) {
    isLoop = false;
    recordActionType = ActionType.CHAT_BOX;
    currentChatBox = new ChatBox(chatBoxContent);
    currentChatBoxContentView.setVisibility(VISIBLE);
    if (chatBoxRecordLocation == null) {
      currentChatBoxContentView.setChatBoxContent(chatBoxContent, new RecordLocation(0, 0, null));
    } else {
      currentChatBoxContentView.setChatBoxContent(chatBoxContent, chatBoxRecordLocation);
    }
    String videoTagImageFileName = System.currentTimeMillis() + ".jpg";
    String videoTagImageFilePath = new File(videoPath).getParent() + "/" + videoTagImageFileName;
    currentChatBoxContentView.saveImage(videoTagImageFilePath, videoPath,
        videoTagImageData -> currentChatBox.setActionImageData(videoTagImageData));
    currentChatBoxContentView.setOnRecordLocationChangeListener(this);
    currentChatBoxContentView.setOnRecordStatusChangeListener(this);
  }

  private RecordLocation runmanRecordLocation = null;
  private RecordLocation stickerRecordLocation = null;
  private RecordLocation chatBoxRecordLocation = null;
  private RecordLocation videoTagRecordLocation = null;

  public void setRunManContent(RunManContent runManContent) {
    isLoop = false;
    recordActionType = ActionType.RUN_MAN;
    currentRunMan = new RunMan(runManContent);
    currentRunManContentView.setVisibility(VISIBLE);
    if (runmanRecordLocation == null) {
      currentRunManContentView.setRunManContent(runManContent, new RecordLocation(0, 0, null), true, false);
    } else {
      currentRunManContentView.setRunManContent(runManContent, runmanRecordLocation, true, false);
    }
    String runManImageFileName = System.currentTimeMillis() + ".jpg";
    String runManImageFilePath = new File(videoPath).getParent() + "/" + runManImageFileName;
    currentRunManContentView.saveImage(runManImageFilePath, videoPath,
        videoTagImageData -> currentRunMan.setActionImageData(videoTagImageData));
    currentRunManContentView.setOnRecordLocationChangeListener(this);
    currentRunManContentView.setOnRecordStatusChangeListener(this);
  }

  public void changeRunManContent(RunManContent runManContent) {
    currentRunMan = new RunMan(runManContent);
    if (runmanRecordLocation == null) {
      runmanRecordLocation = new RecordLocation(0, 0, null);
    }
    currentRunManContentView.setRunManContent(runManContent, runmanRecordLocation);
    currentRunManContentView.setVisibility(VISIBLE);
  }

  public void setRecordRunManOver() {
    isLoop = true;
    currentRunManContentView.setVisibility(GONE);
    currentRunManContentView.setOnRecordLocationChangeListener(null);
    currentRunManContentView.setOnRecordStatusChangeListener(null);
    List<Action> actions = VideoContentTaskManager.getInstance().getCurrentContentTask().getActions();
    for (Action action : actions) {
      if (action instanceof RunMan) {
        initRunMan((RunMan) action);
      }
    }
  }

  public void setRecordChatBoxOver() {
    isLoop = true;
    currentChatBoxContentView.setVisibility(GONE);
    currentChatBoxContentView.setOnRecordLocationChangeListener(null);
    currentChatBoxContentView.setOnRecordStatusChangeListener(null);
    List<Action> actions = VideoContentTaskManager.getInstance().getCurrentContentTask().getActions();
    Stream.of(actions).forEach(action -> {
      if (action instanceof ChatBox) {
        initChatBox();
      }
    });
  }

  public void setRecordVideoTagOver() {
    isLoop = true;
    currentVideoTagContentView.setVisibility(GONE);
    currentVideoTagContentView.setOnRecordLocationChangeListener(null);
    currentVideoTagContentView.setOnRecordStatusChangeListener(null);
    initVideoTag(getCurrentVideoTag().getVideoTagContent());
  }

  public void setRecordSubtitleOver() {
    isLoop = true;
    subtitleContentView.setVisibility(GONE);
    subtitleContentView.setOnRecordLocationChangeListener(null);
    subtitleContentView.setOnRecordStatusChangeListener(null);
    List<Action> actions = VideoContentTaskManager.getInstance().getCurrentContentTask().getActions();
    Stream.of(actions).forEach(action -> {
      if (action instanceof Subtitle) {
        initSubtitle((Subtitle) action);
      }
    });
  }

  public void setRecordMontageOver() {
    isLoop = true;
    setMontageIconGone();
    currentMontageContentView.setVisibility(GONE);
    currentMontageContentView.setOnRecordStatusChangeListener(null);
  }

  public void setRecordStickerOver() {
    isLoop = true;
    goneCurrentActionView(ActionType.STICKER);
    currentStickerActionView.setOnRecordLocationChangeListener(null);
    currentStickerActionView.setOnRecordStatusChangeListener(null);
  }

  public void setStickerImageData(StickerImageData stickerImageData) {
    isLoop = false;
    recordActionType = ActionType.STICKER;
    currentSticker = new Sticker(stickerImageData);
    currentStickerActionView.setVisibility(VISIBLE);
    if (stickerRecordLocation == null) {
      currentStickerActionView.setStickerImageData(stickerImageData, new RecordLocation(0, 0, null));
    } else {
      currentStickerActionView.setStickerImageData(stickerImageData, stickerRecordLocation);
    }
    currentStickerActionView.setOnRecordLocationChangeListener(this);
    currentStickerActionView.setOnRecordStatusChangeListener(this);
  }

  public void setOnVideoPlayProgressListener(OnVideoPlayProgressListener onVideoPlayProgressListener) {
    Stream.of(onVideoPlayProgressListeners).forEach(listenerRef -> {
      OnVideoPlayProgressListener listener = listenerRef.get();
      if (listener != null && listener.equals(onVideoPlayProgressListener)) {
        return;
      }
    });
    onVideoPlayProgressListeners.add(new WeakReference<>(onVideoPlayProgressListener));
  }

  @Override
  public void onVideoSizeChanged(MediaPlayer mp, int width, int height,
                                 float pixelWidthHeightRatio) {
    float scaleX = 1f, scaleY = 1f;
    LayoutParams params =
        (LayoutParams) videoSurfaceView.getLayoutParams();
    switch (videoMetaData.getRotation()) {
      case 0:
      case 180:
        params.height = (int) (SystemUtils.getScreenWidthPx() / (float) width * height);
        params.width = SystemUtils.getScreenWidthPx();
        break;
      case 90:
      case 270:
        params.height = (int) (SystemUtils.getScreenWidthPx() / (float) height * width);
        params.width = SystemUtils.getScreenWidthPx();
        scaleX = params.height / (float) params.width;
        scaleY = params.width / (float) params.height;
        break;
    }
    videoSurfaceView.setRotation(videoMetaData.getRotation());
    videoSurfaceView.setScaleX(scaleX);
    videoSurfaceView.setScaleY(scaleY);
    videoSurfaceView.setLayoutParams(params);
    mIsVideoSizeKnown = true;
    Log.e("onVideoSizeChanged", "onVideoSizeChanged");
  }

  public interface OnVideoPlayProgressListener {
    void onVideoPlayProgress(int videoDuration, int progress);
  }

  public interface OnRecordSucceedListener {
    void onRecordSucceed(RecordActionLocationTask recordActionLocationTask);
  }

  public void setOnRecordSucceedListener(OnRecordSucceedListener onRecordSucceedListener) {
    this.onRecordSucceedListener = onRecordSucceedListener;
  }

  public boolean isActionRecording() {
    if (currentVideoTagContentView != null && currentVideoTagContentView.isRecording()) {
      return true;
    }
    if (subtitleContentView != null
        && subtitleContentView.isRecording()) {
      return true;
    }
    if (currentChatBoxContentView != null && currentChatBoxContentView.isRecording()) {
      return true;
    }
    if (currentStickerActionView != null && currentStickerActionView.isRecording()) {
      return true;
    }
    if (currentRunManContentView != null && currentRunManContentView.isRecording()) {
      return true;
    }
    if (currentMontageContentView != null && currentMontageContentView.isRecording()) {
      return true;
    }
    return false;
  }

  public void removeRecordLocationTask(RecordActionLocationTask recordActionLocationTask,
                                       ActionType type) {
    if (type == ActionType.MONTAGE) {
      VideoContentTaskManager.getInstance().getCurrentContentTask()
          .removeRecordActionLocationTask(recordActionLocationTask, true);
      if (currentMontage != null && recordActionLocationTask != null) {
        currentMontage.removeVideoTagLocationTask(recordActionLocationTask);
      }
    } else {
      VideoContentTaskManager.getInstance().getCurrentContentTask()
          .removeRecordActionLocationTask(recordActionLocationTask, false);
      switch (type) {
        case VIDEO_TAG:
          if (currentVideoTag != null && recordActionLocationTask != null) {
            currentVideoTag.removeVideoTagLocationTask(recordActionLocationTask);
          }
          break;
        case CHAT_BOX:
          if (currentChatBox != null && recordActionLocationTask != null) {
            currentChatBox.removeVideoTagLocationTask(recordActionLocationTask);
          }
          break;
        case STICKER:
          if (currentSticker != null && recordActionLocationTask != null) {
            currentSticker.removeVideoTagLocationTask(recordActionLocationTask);
          }
          break;
        case SUBTITLE:
          if (currentSubtitle != null && recordActionLocationTask != null) {
            currentSubtitle.removeVideoTagLocationTask(recordActionLocationTask);
          }
          break;
        case RUN_MAN:
          if (currentRunMan != null && recordActionLocationTask != null) {
            currentRunMan.removeVideoTagLocationTask(recordActionLocationTask);
          }
          break;
      }
    }
  }

  public void cleanCurrentActionRecordLocationTask() {
    if (currentMontage != null) {
      VideoContentTaskManager.getInstance().getCurrentContentTask()
          .clearActionLocationTask(currentMontage, true);
    }
    if (currentVideoTag != null) {
      VideoContentTaskManager.getInstance().getCurrentContentTask()
          .clearActionLocationTask(currentVideoTag, false);
    }
    if (currentChatBox != null) {
      VideoContentTaskManager.getInstance().getCurrentContentTask()
          .clearActionLocationTask(currentChatBox, false);
    }
    if (currentSticker != null) {
      VideoContentTaskManager.getInstance().getCurrentContentTask()
          .clearActionLocationTask(currentSticker, false);
    }
    if (currentSubtitle != null) {
      VideoContentTaskManager.getInstance().getCurrentContentTask()
          .clearActionLocationTask(currentSubtitle, false);
    }
    if (currentRunMan != null) {
      VideoContentTaskManager.getInstance().getCurrentContentTask()
          .clearActionLocationTask(currentRunMan, false);
    }
  }

  public void seekToRecordLocationTask(RecordActionLocationTask recordActionLocationTask) {
    if (recordActionLocationTask != null) {
      seekTo(recordActionLocationTask.getTimeLine().getStartTimeMs());
    }
  }

  public void showChatBoxActionView(ChatBoxChild chatBoxChild) {
    currentChatBoxContentView.setVisibility(VISIBLE);
    currentChatBoxContentView.setChatBoxContent(new ChatBoxContent(chatBoxChild, ""));
  }

  public void showVideoTagView(VideoTagContent videoTagContent) {
    videoTagRecordLocation = new RecordLocation(0, 0, null);
    currentVideoTagContentView.setVisibility(VISIBLE);
    currentVideoTagContentView.setVideoTagContent(videoTagContent, videoTagRecordLocation, true, true);
    currentVideoTagContentView.setOnRecordLocationChangeListener(null);
    currentVideoTagContentView.setOnRecordStatusChangeListener(null);
  }

  public void showChatBoxActionView(ChatBoxContent chatBoxContent) {
    chatBoxRecordLocation = new RecordLocation(0, 0, null);
    currentChatBoxContentView.setVisibility(VISIBLE);
    currentChatBoxContentView.showChatBoxContent(chatBoxContent, chatBoxRecordLocation);
    currentChatBoxContentView.setOnRecordLocationChangeListener(null);
    currentChatBoxContentView.setOnRecordStatusChangeListener(null);
  }

  public void showStickerActionView(StickerImageData stickerImageData) {
    stickerRecordLocation = new RecordLocation(0, 0, null);
    currentStickerActionView.setVisibility(VISIBLE);
    currentStickerActionView.setStickerImageData(stickerImageData, stickerRecordLocation);
    currentStickerActionView.setOnRecordLocationChangeListener(null);
    currentStickerActionView.setOnRecordStatusChangeListener(null);
    currentRunManContentView.setOnRecordLocationChangeListener(null);
    currentRunManContentView.setOnRecordStatusChangeListener(null);
  }

  public void showRunManContentView(RunManContent runManContent) {
    runmanRecordLocation = new RecordLocation(0, 0, null);
    currentRunManContentView.setRunManContent(runManContent, runmanRecordLocation);
    currentRunManContentView.setVisibility(VISIBLE);
    currentRunManContentView.setOnRecordLocationChangeListener(null);
    currentRunManContentView.setOnRecordStatusChangeListener(null);
  }

  public void hideRunManContentView() {
    currentRunManContentView.setVisibility(GONE);
  }

  public void hideChatBoxContentView() {
    currentChatBoxContentView.setVisibility(GONE);
  }

  public void hideVideoTagContentView() {
    currentVideoTagContentView.setVisibility(GONE);
  }

  public void removeSubtitleView() {
    subtitleContainer.removeAllViews();
  }

  @Override
  public void onRecordLocationChange(int offsetX, int offsetY) {
    if (mediaPlayer != null && !isPlayCompletion) {
      switch (recordActionType) {
        case VIDEO_TAG:
          currentVideoTag.addRecordLocation((int) mediaPlayer.getCurrentPosition() + offsetPosition,
              sampleSize, videoDurationMs,
              new RecordLocation(offsetX, offsetY, videoActionParams));
          break;
        case SUBTITLE:
          currentSubtitle.addRecordLocation((int) mediaPlayer.getCurrentPosition() + offsetPosition,
              sampleSize, videoDurationMs,
              new RecordLocation(offsetX, offsetY, videoActionParams));
          break;
        case CHAT_BOX:
          currentChatBox.addRecordLocation((int) mediaPlayer.getCurrentPosition() + offsetPosition,
              sampleSize, videoDurationMs,
              new RecordLocation(offsetX, offsetY, videoActionParams));
          break;
        case STICKER:
          currentSticker.addRecordLocation((int) mediaPlayer.getCurrentPosition() + offsetPosition,
              sampleSize, videoDurationMs,
              new RecordLocation(offsetX, offsetY, videoActionParams));
          break;
        case RUN_MAN:
          currentRunMan.addRecordLocation((int) mediaPlayer.getCurrentPosition() + offsetPosition,
              sampleSize, videoDurationMs,
              new RecordLocation(offsetX, offsetY, videoActionParams));
          break;
      }
    }
  }

  public void saveDefaultTask() {
    switch (recordActionType) {
      case SUBTITLE:
        currentSubtitle.newRecordLocationTask(seekPosition + offsetPosition);
        currentSubtitle
            .addRecordLocation(seekPosition + offsetPosition, sampleSize, videoDurationMs, new RecordLocation(0, 0, videoActionParams));
        currentSubtitle
            .addRecordLocation(seekPosition + Config.SUBTITLE_TIME + offsetPosition, sampleSize,
                videoDurationMs, new RecordLocation(0, 0, videoActionParams));
        currentSubtitle
            .saveRecordLocationTask(seekPosition + Config.SUBTITLE_TIME + offsetPosition);
        VideoContentTaskManager.getInstance().getCurrentContentTask()
            .addNormalActionLocationTask(currentSubtitle.getCurrentRecordLocationTask());
        VideoContentTaskManager.getInstance().getCurrentContentTask()
            .addSubtitle(currentSubtitle);
        setRecordSubtitleOver();
        break;
      case STICKER:
        currentSticker.newRecordLocationTask(seekPosition + offsetPosition);
        currentSticker.addRecordLocation(seekPosition + offsetPosition, sampleSize, videoDurationMs, new RecordLocation(-currentStickerActionView.getScrollX(), -currentStickerActionView.getScrollY(), null));
        currentSticker.addRecordLocation(seekPosition + Config.STICKER_TIME + offsetPosition, sampleSize, videoDurationMs, new RecordLocation(-currentStickerActionView.getScrollX(), -currentStickerActionView.getScrollY(), null));
        currentSticker
            .saveRecordLocationTask(seekPosition + Config.STICKER_TIME + offsetPosition);
        VideoContentTaskManager.getInstance().getCurrentContentTask()
            .addNormalActionLocationTask(currentSticker.getCurrentRecordLocationTask());
        VideoContentTaskManager.getInstance().getCurrentContentTask()
            .addSticker(currentSticker);
        goneCurrentActionView(ActionType.STICKER);
        break;
      case RUN_MAN:
        currentRunMan.newRecordLocationTask(seekPosition + offsetPosition);
        currentRunMan.addRecordLocation(seekPosition + offsetPosition, sampleSize, videoDurationMs, new RecordLocation(-currentRunManContentView.getScrollX(), -currentRunManContentView.getScrollY(), null));
        currentRunMan.addRecordLocation(seekPosition + Config.RUN_MAN_TIME + offsetPosition,
            sampleSize, videoDurationMs, new RecordLocation(-currentRunManContentView.getScrollX(),
                -currentRunManContentView.getScrollY(), null));
        currentRunMan
            .saveRecordLocationTask(seekPosition + Config.RUN_MAN_TIME + offsetPosition);
        VideoContentTaskManager.getInstance().getCurrentContentTask()
            .addNormalActionLocationTask(currentRunMan.getCurrentRecordLocationTask());
        VideoContentTaskManager.getInstance().getCurrentContentTask()
            .addRunMan(currentRunMan);
        hideRunManContentView();
        setRecordRunManOver();
        break;
      case VIDEO_TAG:
        currentVideoTag.newRecordLocationTask(seekPosition + offsetPosition);
        currentVideoTag.addRecordLocation(seekPosition + offsetPosition, sampleSize,
            videoDurationMs, new RecordLocation(-currentVideoTagContentView.getScrollX(),
                -currentVideoTagContentView.getScrollY(), null));
        currentVideoTag.addRecordLocation(seekPosition + Config.VIDEO_TAG_TIME + offsetPosition,
            sampleSize, videoDurationMs,
            new RecordLocation(-currentVideoTagContentView.getScrollX(),
                -currentVideoTagContentView.getScrollY(), null));
        currentVideoTag
            .saveRecordLocationTask(seekPosition + Config.VIDEO_TAG_TIME + offsetPosition);
        VideoContentTaskManager.getInstance().getCurrentContentTask()
            .addNormalActionLocationTask(currentVideoTag.getCurrentRecordLocationTask());
        VideoContentTaskManager.getInstance().getCurrentContentTask()
            .addVideoTag(currentVideoTag);
        break;
      case CHAT_BOX:
        currentChatBox.newRecordLocationTask(seekPosition + offsetPosition);
        currentChatBox.addRecordLocation(seekPosition + offsetPosition, sampleSize, videoDurationMs, new RecordLocation(-currentChatBoxContentView.getScrollX(), -currentChatBoxContentView.getScrollY(), null));
        currentChatBox.addRecordLocation(seekPosition + Config.CHAT_BOX_TIME + offsetPosition, sampleSize, videoDurationMs, new RecordLocation(-currentChatBoxContentView.getScrollX(), -currentChatBoxContentView.getScrollY(), null));
        currentChatBox.saveRecordLocationTask(seekPosition + Config.CHAT_BOX_TIME + offsetPosition);
        VideoContentTaskManager.getInstance().getCurrentContentTask()
            .addNormalActionLocationTask(currentChatBox.getCurrentRecordLocationTask());
        VideoContentTaskManager.getInstance().getCurrentContentTask()
            .addChatBox(currentChatBox);
        hideChatBoxContentView();
        setRecordChatBoxOver();
        break;
      case MONTAGE:
        currentMontage.newRecordLocationTask(
            seekPosition + offsetPosition);
        currentMontage.addRecordLocation(seekPosition + offsetPosition, sampleSize, videoDurationMs,
            new RecordLocation(0, 0, null));
        switch (currentMontage.getMontageType()) {
          case FREEZE:
            currentMontage.addRecordLocation(
                seekPosition + Config.MONTAGE_PAUSE_TIME + offsetPosition, sampleSize,
                videoDurationMs,
                new RecordLocation(0, 0, null));
            currentMontage
                .saveRecordLocationTask(seekPosition + Config.MONTAGE_PAUSE_TIME + offsetPosition);
            break;
          case REPEAT:
            currentMontage.addRecordLocation(
                seekPosition + Config.MONTAGE_REPEAT_TIME + offsetPosition, sampleSize,
                videoDurationMs,
                new RecordLocation(0, 0, null));
            currentMontage
                .saveRecordLocationTask(seekPosition + Config.MONTAGE_REPEAT_TIME + offsetPosition);
            break;
          case FORWARD:
            currentMontage.addRecordLocation(
                seekPosition + Config.MONTAGE_SPEED_TIME + offsetPosition, sampleSize,
                videoDurationMs,
                new RecordLocation(0, 0, null));
            currentMontage
                .saveRecordLocationTask(seekPosition + Config.MONTAGE_SPEED_TIME + offsetPosition);
            break;
          case SLOW_MOTION:
            currentMontage.addRecordLocation(
                seekPosition + Config.MONTAGE_SLOW_TIME + offsetPosition, sampleSize,
                videoDurationMs,
                new RecordLocation(0, 0, null));
            currentMontage
                .saveRecordLocationTask(seekPosition + Config.MONTAGE_SLOW_TIME + offsetPosition);
            break;
        }
        VideoContentTaskManager.getInstance().getCurrentContentTask()
            .addMontageActionLocationTask(currentMontage.getCurrentRecordLocationTask());
        VideoContentTaskManager.getInstance().getCurrentContentTask()
            .addMontage(currentMontage);
        setRecordMontageOver();
        break;
    }
  }

  private boolean isRecording = false;

  @Override
  public void onRecordStatusChange(RecordStatus recordStatus, int offsetX, int offsetY) {
    if (recordStatus == RecordStatus.RECORDING) {
      if (isPlayCompletion) {
        isRecording = false;
        return;
      } else {
        isRecording = true;
      }
    }
    switch (recordActionType) {
      case SUBTITLE:
        switch (recordStatus) {
          case RECORDING:
            currentSubtitle.newRecordLocationTask(mediaPlayer == null ? 0 : (int) mediaPlayer
                .getCurrentPosition() + offsetPosition);
            resume();
            break;
          case PREPARE:
            if (mediaPlayer != null && isRecording) {
              currentSubtitle
                  .saveRecordLocationTask((int) mediaPlayer.getCurrentPosition() + offsetPosition);
              pause();
              VideoContentTaskManager.getInstance().getCurrentContentTask()
                  .addNormalActionLocationTask(currentSubtitle.getCurrentRecordLocationTask());
              if (onRecordSucceedListener != null) {
                onRecordSucceedListener
                    .onRecordSucceed(currentSubtitle.getCurrentRecordLocationTask());
              }
            }
            break;
        }
        break;
      case VIDEO_TAG:
        switch (recordStatus) {
          case RECORDING:
            currentVideoTag.newRecordLocationTask(mediaPlayer == null ? 0 : (int) mediaPlayer
                .getCurrentPosition() + offsetPosition);
            resume();
            break;
          case PREPARE:
            if (mediaPlayer != null && isRecording) {
              currentVideoTag
                  .saveRecordLocationTask((int) mediaPlayer.getCurrentPosition() + offsetPosition);
              pause();
              VideoContentTaskManager.getInstance().getCurrentContentTask()
                  .addNormalActionLocationTask(currentVideoTag.getCurrentRecordLocationTask());
              if (onRecordSucceedListener != null) {
                onRecordSucceedListener
                    .onRecordSucceed(currentVideoTag.getCurrentRecordLocationTask());
              }
            }
            break;
        }
        break;
      case CHAT_BOX:
        switch (recordStatus) {
          case RECORDING:
            currentChatBox.newRecordLocationTask(mediaPlayer == null ? 0 : (int) mediaPlayer
                .getCurrentPosition() + offsetPosition);
            resume();
            break;
          case PREPARE:
            if (mediaPlayer != null && isRecording) {
              currentChatBox
                  .saveRecordLocationTask((int) mediaPlayer.getCurrentPosition() + offsetPosition);
              pause();
              VideoContentTaskManager.getInstance().getCurrentContentTask()
                  .addNormalActionLocationTask(currentChatBox.getCurrentRecordLocationTask());
              if (onRecordSucceedListener != null) {
                onRecordSucceedListener
                    .onRecordSucceed(currentChatBox.getCurrentRecordLocationTask());
              }
            }
            break;
        }
        break;
      case STICKER:
        switch (recordStatus) {
          case RECORDING:
            currentSticker.newRecordLocationTask(mediaPlayer == null ? 0 : (int) mediaPlayer
                .getCurrentPosition() + offsetPosition);
            currentStickerActionView.playSound();
            resume();
            break;
          case PREPARE:
            if (mediaPlayer != null && isRecording) {
              currentSticker
                  .saveRecordLocationTask((int) mediaPlayer.getCurrentPosition() + offsetPosition);
              currentStickerActionView.stopSound();
              pause();
              VideoContentTaskManager.getInstance().getCurrentContentTask()
                  .addNormalActionLocationTask(currentSticker.getCurrentRecordLocationTask());
              if (onRecordSucceedListener != null) {
                onRecordSucceedListener
                    .onRecordSucceed(currentSticker.getCurrentRecordLocationTask());
              }
            }
            break;
        }
        break;
      case RUN_MAN:
        switch (recordStatus) {
          case RECORDING:
            currentRunMan.newRecordLocationTask(mediaPlayer == null ? 0 : (int) mediaPlayer
                .getCurrentPosition() + offsetPosition);
            currentRunManContentView.playSound();
            resume();
            break;
          case PREPARE:
            if (mediaPlayer != null && isRecording) {
              currentRunMan
                  .saveRecordLocationTask((int) mediaPlayer.getCurrentPosition() + offsetPosition);
              currentRunManContentView.stopSound();
              pause();
              VideoContentTaskManager.getInstance().getCurrentContentTask()
                  .addNormalActionLocationTask(currentRunMan.getCurrentRecordLocationTask());
              if (onRecordSucceedListener != null) {
                onRecordSucceedListener
                    .onRecordSucceed(currentRunMan.getCurrentRecordLocationTask());
              }
            }
            break;
        }
        break;
      case MONTAGE:
        switch (recordStatus) {
          case RECORDING:
            currentMontage.newRecordLocationTask(mediaPlayer == null ? 0 : (int) mediaPlayer
                .getCurrentPosition() + offsetPosition);
            resume();
            break;
          case PREPARE:
            if (mediaPlayer != null && isRecording) {
              currentMontage
                  .saveRecordLocationTask((int) mediaPlayer.getCurrentPosition() + offsetPosition);
              pause();
              VideoContentTaskManager.getInstance().getCurrentContentTask()
                  .addMontageActionLocationTask(currentMontage.getCurrentRecordLocationTask());
              if (onRecordSucceedListener != null) {
                onRecordSucceedListener
                    .onRecordSucceed(currentMontage.getCurrentRecordLocationTask());
              }
            }
            break;
        }
        break;
    }
    if (recordStatus == RecordStatus.RECORDING) {
      onRecordLocationChange(offsetX, offsetY);
    }
  }

  public RunMan getCurrentRunMan() {
    return currentRunMan;
  }

  public Subtitle getCurrentSubtitle() {
    return currentSubtitle;
  }

  enum RecordActionType {
    SUBTITLE, VIDEO_TAG, CHAT_BOX, STICKER, RUN_MAN, MONTAGE
  }

  public boolean isPlay() {
    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
      return true;
    }
    return false;
  }

  @Override
  public void onCompletion(MediaPlayer mp) {
    if (isLoop) {
      oldShowMontageShowTimeMs = offsetPosition = 0;
      mediaPlayer.seekTo(0);
      mediaPlayer.setPlayWhenReady(true);
      playAudio();
    } else {
      isPlayCompletion = true;
      pauseAudio();
    }
  }

  public int getDuration() {
    if (mediaPlayer != null) {
      return (int) mediaPlayer.getDuration();
    }
    return 0;
  }

  public void goneCurrentActionView(ActionType actionType) {
    switch (actionType) {
      case VIDEO_TAG:
        if (currentVideoTagContentView != null) {
          currentVideoTagContentView.setVisibility(GONE);
        }
        break;
      case SUBTITLE:
        if (subtitleContentView != null) {
          subtitleContentView.setVisibility(GONE);
        }
        break;
      case CHAT_BOX:
        if (currentChatBoxContentView != null) {
          currentChatBoxContentView.setVisibility(GONE);
        }
        break;
      case STICKER:
        if (currentStickerActionView != null) {
          currentStickerActionView.setVisibility(GONE);
        }
        break;
      case RUN_MAN:
        if (currentRunManContentView != null) {
          currentRunManContentView.setVisibility(GONE);
        }
        break;
    }
  }

  public void showCurrentActionView(ActionType actionType) {
    if (isPlay()) {
      return;
    }
    switch (actionType) {
      case VIDEO_TAG:
        if (currentVideoTagContentView != null) {
          currentVideoTagContentView.setVisibility(VISIBLE);
        }
        break;
      case SUBTITLE:
        if (subtitleContentView != null) {
          subtitleContentView.setVisibility(VISIBLE);
        }
        break;
      case CHAT_BOX:
        if (currentChatBoxContentView != null) {
          currentChatBoxContentView.setVisibility(VISIBLE);
        }
        break;
      case STICKER:
        if (currentStickerActionView != null) {
          currentStickerActionView.setVisibility(VISIBLE);
        }
        break;
      case RUN_MAN:
        if (currentRunManContentView != null) {
          currentRunManContentView.setVisibility(VISIBLE);
        }
        break;
    }
  }

  public void setLooping(boolean isLoop) {
    this.isLoop = isLoop;
  }

  public Object removeLatestAction() {
    Object object = null;
    Action action = VideoContentTaskManager.getInstance().getCurrentContentTask().removeLatestAction();
    if (action instanceof RunMan) {
      RunMan runMan = (RunMan) action;
      RunManContent runManContent = runMan.getRunManContent();
      oldEditActionView.removeView(oldRunManContentViews.remove(runManContent));
      currentRunMan = null;
      object = runManContent;
    } else if (action instanceof Subtitle) {
      Subtitle subtitle = (Subtitle) action;
      SubtitleContent subtitleContent = subtitle.getSubtitleContent();
      oldEditActionView.removeView(oldJpSubtitleContentViewMap.remove(subtitleContent));
      oldEditActionView.removeView(oldBackgroundSubtitleContentViewHashMap.remove(subtitleContent));
      oldEditActionView.removeView(oldNormalSubtitleContentViewMap.remove(subtitleContent));
      currentSubtitle = null;
      object = subtitleContent;
    } else if (action instanceof ChatBox) {
      ChatBox chatBox = (ChatBox) action;
      ChatBoxContent chatBoxContent = chatBox.getChatBoxContent();
      if (oldChatBoxContentViews.size() > 0) {
        ChatBoxContentView chatBoxContentView = oldChatBoxContentViews.get(oldChatBoxContentViews.size() - 1);
        oldEditActionView.removeView(chatBoxContentView);
        currentChatBox = null;
        object = chatBoxContent;
      }
    } else if (action instanceof VideoTag) {
      VideoTag videoTag = (VideoTag) action;
      VideoTagContent videoTagContent = videoTag.getVideoTagContent();
      VideoTagContentView videoTagContentView = videoTagContentViewMap.remove(videoTagContent);
      oldEditActionView.removeView(videoTagContentView);
      currentVideoTag = null;
      object = videoTagContent;
    } else if (action instanceof Sticker) {
      Sticker sticker = (Sticker) action;
      if (oldStickerContentViews.size() > 0) {
        RecordActionLocationTask recordActionLocationTask = sticker.getCurrentRecordLocationTask();
        oldEditActionView.removeView(oldStickerContentViews.remove(recordActionLocationTask.getCreateTimeMs()));
        currentSticker = null;
        object = sticker.getStickerContent();
      }
    } else if (action instanceof Montage) {
      Montage montage = (Montage) action;
      object = montage.getMontageType();
      mediaPlayer.setPlaybackSpeed(1f);
      mainHandler.removeCallbacksAndMessages(null);
      currentMontage = null;
      isShowSpeedMontage = false;
      currentShowMontageTimeLine = null;
      currentRepeatMontageShowNum = 1;
    }
    return object;
  }
}
