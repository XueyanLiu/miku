package com.biubiu.miku.module.videoedit;

import android.animation.Animator;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.annimon.stream.Stream;
import com.biubiu.miku.MikuApplication;
import com.biubiu.miku.R;
import com.biubiu.miku.base.BaseActivity;
import com.biubiu.miku.base.BaseFragment;
import com.biubiu.miku.constant.EditState;
import com.biubiu.miku.event.ChangeElementEvent;
import com.biubiu.miku.event.ExitBottomEditEvent;
import com.biubiu.miku.event.NewSubtitleEvent;
import com.biubiu.miku.event.PostStartCallbackEvent;
import com.biubiu.miku.event.SelectChatBoxEvent;
import com.biubiu.miku.event.SelectEffectsEvent;
import com.biubiu.miku.event.SelectRunManEvent;
import com.biubiu.miku.event.SelectStickerEvent;
import com.biubiu.miku.event.SelectVideoTagEvent;
import com.biubiu.miku.util.AddTaskListener;
import com.biubiu.miku.util.AnimUtils;
import com.biubiu.miku.util.ImageUtils;
import com.biubiu.miku.util.NetworkUtils;
import com.biubiu.miku.util.OnBackPressedListener;
import com.biubiu.miku.util.SystemUtils;
import com.biubiu.miku.util.sound.SoundPoolPlayer;
import com.biubiu.miku.util.sound.SoundUtils;
import com.biubiu.miku.util.video.ChangeElementState;
import com.biubiu.miku.util.video.action.ActionType;
import com.biubiu.miku.util.video.action.chatBox.ChatBoxChild;
import com.biubiu.miku.util.video.action.chatBox.ChatBoxContent;
import com.biubiu.miku.util.video.action.chatBox.DefaultChatBoxManager;
import com.biubiu.miku.util.video.action.montage.MontageType;
import com.biubiu.miku.util.video.action.runMan.DefaultRunManManager;
import com.biubiu.miku.util.video.action.runMan.RunManAttribute;
import com.biubiu.miku.util.video.action.runMan.RunManContent;
import com.biubiu.miku.util.video.action.sticker.DefaultStickerManager;
import com.biubiu.miku.util.video.action.sticker.StickerContent;
import com.biubiu.miku.util.video.action.sticker.StickerImageData;
import com.biubiu.miku.util.video.action.sticker.StickerImageGroup;
import com.biubiu.miku.util.video.action.subtitle.SubtitleContent;
import com.biubiu.miku.util.video.action.subtitle.SubtitleManager;
import com.biubiu.miku.util.video.action.subtitle.SubtitleType;
import com.biubiu.miku.util.video.action.videoTag.VideoTagContent;
import com.biubiu.miku.util.video.action.videoTag.VideoTagManager;
import com.biubiu.miku.util.video.task.VideoContentTaskManager;
import com.biubiu.miku.widget.customview.ChooseFilterView;
import com.biubiu.miku.widget.customview.ChooseMusicView;
import com.biubiu.miku.widget.customview.PreviewVideoView;
import com.biubiu.miku.widget.customview.ThumbnailsSeekView;
import com.biubiu.miku.widget.customview.WaveformView;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoEditFragment extends BaseFragment {
  public static final String VIDEO_FILE_PATH = "video_file_path";
  public static final String VIDEO_RATIO = "video_ratio";
  private static final long TOP_BAR_ANIM_DURATION = 300L;
  public final static int MAX_JP_SUBTITLE_TEXT_LENGTH = 15;
  public final static int MAX_LOVE_AND_NOTE_TEXT_LENGTH = 10;

  private static final int EDIT = 0;
  private static final int FILTER = 1;
  private static final int MUSIC = 2;
  private static final int NO_LIMIT = -1;

  @BindView(R.id.edit_parent_layout)
  RelativeLayout editParentLayout;
  @BindView(R.id.video_view)
  PreviewVideoView previewVideoView;
  @BindView(R.id.add_text_imageView)
  View addTextImageView;
  @BindView(R.id.input_text_top_bar)
  RelativeLayout topBarLayout;
  @BindView(R.id.input_close)
  ImageView inputCloseImageView;
  @BindView(R.id.input_confirm)
  ImageView inputConfirmImageView;
  @BindView(R.id.title)
  TextView titleTextView;
  @BindView(R.id.subtitle)
  TextView subtitleTextView;
  @BindView(R.id.input_edittext)
  EditText inputEditText;
  @BindView(R.id.post)
  TextView postTextView;
  @BindView(R.id.edit_seek_bar)
  ThumbnailsSeekView editSeekBar;
  @BindView(R.id.variety)
  ImageView varietyImageView;
  @BindView(R.id.music)
  ImageView musicImageView;
  @BindView(R.id.filter)
  ImageView filterImageView;
  @BindView(R.id.bottom_layout)
  RelativeLayout bottomLayout;
  @BindView(R.id.effect_imageView)
  View effectImageView;
  @BindView(R.id.sticker_imageView)
  View stickerImageView;
  @BindView(R.id.edit_layout)
  RelativeLayout editLayout;
  @BindView(R.id.music_layout)
  LinearLayout musicLayout;
  @BindView(R.id.filter_layout)
  LinearLayout filterLayout;
  @BindView(R.id.pause_play)
  ImageView pauseImageView;
  @BindView(R.id.redo_play)
  ImageView redoImageView;
  @BindView(R.id.filter_layout_content)
  HorizontalScrollView filterContent;
  @BindView(R.id.fragment_container)
  FrameLayout fragmentContainer;
  @BindView(R.id.change_volume_layout)
  View changeVolumeLayout;
  @BindView(R.id.visualizer_view)
  WaveformView waveformView;
  @BindView(R.id.change_volume_seek_bar)
  SeekBar changeVolumeSeekBar;
  @BindView(R.id.sound)
  TextView sound;
  @BindView(R.id.touch_to_record)
  TextView touchRecordView;
  @BindView(R.id.input_parent_layout)
  RelativeLayout inputLayout;
  @BindView(R.id.loading_layout)
  RelativeLayout loadingLayout;
  @BindView(R.id.loading_video)
  SimpleDraweeView loadingVideo;
  @BindView(R.id.loading_content)
  RelativeLayout loadingContent;

  public static final int MAX_VOLUME_SEEK = 100;
  private int currentVolumeSeek = 50;

  @BindView(R.id.elements_group_view)
  RecyclerView elementsGroupRecyclerView;
  @BindView(R.id.more_icon)
  ImageView moreIcon;

  private String videoFilePath;
  private boolean isPost = false;
  private BottomEditFragment bottomEditFragment;
  private SubtitleContent subtitleContent;
  private RunManContent runManContent;
  private ChatBoxContent chatBoxContent;
  private VideoTagContent videoTagContent;
  private ChatBoxChild chatBoxChild;
  private StickerImageData stickerImageData;
  private MontageType currentMontageType;
  private StickerImageGroup stickerImageGroup;
  private final Handler handler = new Handler();
  private ElementsGroupAdapter elementsGroupAdapter;
  private ActionType currentActionType = null;
  private int maxLength;
  private int currtentFunction = EDIT;
  private DraweeController draweeController;
  private ChooseFilterView chooseFilterView;
  private ChooseMusicView chooseMusicView;
  private AddTaskListener addFilterListener;
  private AddTaskListener addMusicListener;

  private StickerParentFragment stickersFragment;
  private EffectsFragment effectsFragment;
  private Fragment currentEditFragment;
  private String selectFilterVideoPath;
  private float videoRatioWH;
  private ChangeElementEvent changeElementEvent;
  private int currentVolume;
  private String text;
  private EditState editState = EditState.IDLE;
  private BottomAction bottomAction = BottomAction.VARIETY;
  private boolean isRelease = false;
  private boolean isPlaying = true;
  private boolean canRedo = false;

  private final OnBackPressedListener onBackPressedListener = new OnBackPressedListener() {
    @Override
    public boolean onBack() {
      if (currentEditFragment != null && currentEditFragment.isAdded()) {
        getFragmentManager().beginTransaction().remove(currentEditFragment).commit();
        previewVideoView.setLooping(true);
        if (editState == EditState.RECORD_MONTAGE) {
          previewVideoView.setRecordMontageOver();
        }
        if (editState != EditState.EDIT_TEXT && editState != EditState.EDIT_STICKER) {
          reset();
          if (previewVideoView != null) {
            previewVideoView.seekTo(0);
            previewVideoView.resume();
          }
          isPlaying = true;
          setPauseViewState();
          setRedoViewState();
        }
        return true;
      }
      return backEditStaus();
    }
  };

  private final PreviewVideoView.OnVideoPlayProgressListener onVideoPlayProgressListener =
      (videoDuration, progress) -> {
        editSeekBar.setMax(videoDuration);
        editSeekBar.setProgress(progress);
      };

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    ((BaseActivity) getActivity()).addOnBackPressedListener(onBackPressedListener);
    View rootView = inflater.inflate(R.layout.video_edit_fragment, container, false);
    ButterKnife.bind(this, rootView);
    EventBus.getDefault().register(this);
    initViewData();
    ((BaseActivity) getActivity()).addOnBackPressedListener(onBackPressedListener);
    setFunctionState();
    setLoadingLayoutUI();
    addListener();
    return rootView;
  }

  @Override
  public void onResume() {
    super.onResume();
    if (isRelease) {
      previewVideoView.replay();
    } else {
      previewVideoView.resume();
    }
    isPlaying = true;
    isRelease = false;
    setPauseViewState();
  }

  @Override
  public void onPause() {
    super.onPause();
    previewVideoView.pause();
    isPlaying = false;
  }

  @Override
  public void onStop() {
    super.onStop();
    previewVideoView.releaseVideo();
    isRelease = true;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    EventBus.getDefault().unregister(this);
    ((BaseActivity) getActivity()).removeOnBackPressedListener(onBackPressedListener);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    chooseFilterView.removeTaskListener(addFilterListener);
    chooseMusicView.removeTaskListener(addMusicListener);
  }

  private void addListener() {
    addTextImageView.setOnClickListener(v -> {
      previewVideoView.pause();
      if (bottomEditFragment == null) {
        bottomEditFragment = BottomEditFragment.newFragment();
      }
      getFragmentManager().beginTransaction().replace(R.id.fragment_container, bottomEditFragment)
          .commit();
      currentEditFragment = bottomEditFragment;
      previewVideoView.setOnVideoPlayProgressListener(onVideoPlayProgressListener);
    });

    musicImageView.setOnClickListener(v -> {
      if (bottomAction != BottomAction.MUSIC) {
        bottomAction = BottomAction.MUSIC;
        changeVolumeLayout.setVisibility(View.VISIBLE);
        editSeekBar.setVisibility(View.INVISIBLE);
        TranslateAnimation translateAnimation =
            new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f,
                Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.0f);
        translateAnimation.setDuration(300);
        currtentFunction = MUSIC;
        editLayout.setVisibility(View.GONE);
        musicLayout.setVisibility(View.VISIBLE);
        musicLayout.startAnimation(translateAnimation);
        chooseMusicView.checkMusicState();
        chooseMusicView.setSeekBar(changeVolumeSeekBar);
        filterContent.setVisibility(View.GONE);
        setFunctionState();
        pauseImageView.setVisibility(View.GONE);
        redoImageView.setVisibility(View.GONE);
        if (!isPlaying) {
          previewVideoView.resume();
          isPlaying = true;
        }
      }
    });

    filterImageView.setOnClickListener(v -> {
      if (bottomAction != BottomAction.FILLTER) {
        bottomAction = BottomAction.FILLTER;
        changeVolumeLayout.setVisibility(View.GONE);
        editSeekBar.setVisibility(View.VISIBLE);
        waveformView.setVisibility(View.GONE);
        TranslateAnimation translateAnimation =
            new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.0f);
        translateAnimation.setDuration(300);
        currtentFunction = FILTER;
        editLayout.setVisibility(View.GONE);
        musicLayout.setVisibility(View.GONE);
        filterContent.setVisibility(View.VISIBLE);
        filterContent.startAnimation(translateAnimation);
        setFunctionState();
        pauseImageView.setVisibility(View.GONE);
        redoImageView.setVisibility(View.GONE);
        if (!isPlaying) {
          previewVideoView.resume();
          isPlaying = true;
        }
      }
    });

    varietyImageView.setOnClickListener(v -> {
      if (bottomAction != BottomAction.VARIETY) {
        bottomAction = BottomAction.VARIETY;
        editSeekBar.setVisibility(View.VISIBLE);
        waveformView.setVisibility(View.GONE);
        changeVolumeLayout.setVisibility(View.GONE);
        TranslateAnimation translateAnimation =
            new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.0f);
        translateAnimation.setDuration(300);
        currtentFunction = EDIT;
        editLayout.setVisibility(View.VISIBLE);
        editLayout.startAnimation(translateAnimation);
        musicLayout.setVisibility(View.GONE);
        filterContent.setVisibility(View.GONE);
        setFunctionState();
        setPauseViewState();
        setRedoViewState();
      }
    });

    stickerImageView.setOnClickListener(v -> {
      previewVideoView.pause();
      if (stickersFragment == null) {
        stickersFragment = StickerParentFragment.newFragment();
      }
      currentEditFragment = stickersFragment;
      addEditFragment();
    });

    effectImageView.setOnClickListener(v -> {
      pauseImageView.setVisibility(View.GONE);
      previewVideoView.pause();
      if (effectsFragment == null) {
        effectsFragment = EffectsFragment.newFragment();
      }
      currentEditFragment = effectsFragment;
      addEditFragment();
    });

    pauseImageView.setOnClickListener(v -> {
      if (isPlaying) {
        previewVideoView.pause();
      } else {
        previewVideoView.resume();
      }
      isPlaying = !isPlaying;
      setPauseViewState();
    });

    redoImageView.setOnClickListener(view -> {
      previewVideoView.pause();
      redoImageView.setVisibility(View.GONE);
      Object object = previewVideoView.removeLatestAction();
      AnimUtils.ViewTranslationY(topBarLayout, -topBarLayout.getHeight(), 0, TOP_BAR_ANIM_DURATION,
          null);
      AnimUtils.ViewTranslationY(editParentLayout, 0, topBarLayout.getHeight(),
          TOP_BAR_ANIM_DURATION,
          new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
              if (object instanceof RunManContent) {
                editState = EditState.ADDTEXT_INPUT;
                currentActionType = ActionType.RUN_MAN;
                runManContent = (RunManContent) object;
                text = runManContent.getTextContent();
                editElement();
                selectElementGroupShow();
              } else if (object instanceof ChatBoxContent) {
                editState = EditState.ADDTEXT_INPUT;
                currentActionType = ActionType.CHAT_BOX;
                chatBoxContent = (ChatBoxContent) object;
                chatBoxChild = chatBoxContent.getChatBoxChild();
                text = chatBoxContent.getContent();
                editElement();
                selectElementGroupShow();
              } else if (object instanceof SubtitleContent) {
                editState = EditState.ADDTEXT_INPUT;
                currentActionType = ActionType.SUBTITLE;
                subtitleContent = (SubtitleContent) object;
                text = subtitleContent.getContent();
                editElement();
                selectElementGroupShow();
              } else if (object instanceof VideoTagContent) {
                editState = EditState.ADDTEXT_INPUT;
                currentActionType = ActionType.VIDEO_TAG;
                videoTagContent = (VideoTagContent) object;
                text = videoTagContent.getContent();
                editElement();
                selectElementGroupShow();
              } else if (object instanceof StickerContent) {
                currentActionType = ActionType.STICKER;
                StickerContent stickerContent = (StickerContent) object;
                stickerImageData = stickerContent.getStickerImageData();
                previewVideoView.showStickerActionView(stickerImageData);
                editState = EditState.EDIT_STICKER;
                selectElementGroupShow();
              } else if (object instanceof MontageType) {
                currentActionType = ActionType.MONTAGE;
                currentMontageType = (MontageType) object;
                editState = EditState.RECORD_MONTAGE;
                previewVideoView.setCurrentMontageActionType(currentMontageType);
                touchRecordView.setVisibility(View.VISIBLE);
                editLayout.setVisibility(View.GONE);
                bottomLayout.setVisibility(View.GONE);
                postTextView.setVisibility(View.GONE);
                inputCloseImageView.setVisibility(View.VISIBLE);
                titleTextView.setText("");
              }
              canRedo = false;
              setPauseViewState();
              setRedoViewState();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
          });
    });

    inputCloseImageView.setOnClickListener(v -> backEditStaus());

    inputConfirmImageView.setOnClickListener(v -> editElement());

    postTextView.setOnClickListener(v -> {
      SoundPoolPlayer.getInstance().playSound(R.raw.click);
      if (NetworkUtils.isMobileNetworkConnected(getActivity())) {
        getFragmentManager().beginTransaction().add(R.id.container, new PostSelectFragment())
            .commitAllowingStateLoss();
      } else {
        postVideo();
      }
    });

    previewVideoView.setOnVideoPlayProgressListener(onVideoPlayProgressListener);

    previewVideoView.setOnRecordSucceedListener(locationTask -> {
      finishRecord();
    });

    editSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
          previewVideoView.seekTo(progress);
        }
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {
      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {
      }
    });

    inputEditText.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        showMaxInput(s.toString());
      }

      @Override
      public void afterTextChanged(Editable s) {

      }
    });

    addFilterListener = new AddTaskListener() {
      @Override
      public void onProgress() {
        showLoadingView();
      }

      @Override
      public void onSuccess() {
        goneLoadingView();
      }

      @Override
      public void onFail() {
        goneLoadingView();
      }
    };
    addMusicListener = new AddTaskListener() {
      @Override
      public void onProgress() {
        showLoadingView();
      }

      @Override
      public void onSuccess() {
        goneLoadingView();
      }

      @Override
      public void onFail() {
        goneLoadingView();
      }
    };
    chooseFilterView.addTaskListener(addFilterListener);
    chooseMusicView.addTaskListener(addMusicListener);

    waveformView
        .setOnWaveStartPositionChangeListener(startPositionMs -> {
          previewVideoView.updateMusic(chooseMusicView.getMusic());
        });
    changeVolumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        currentVolume = progress;
        chooseMusicView.updateVolume(currentVolume);
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {

      }
    });
    moreIcon.setOnClickListener(v -> {
      if (currentActionType != null && editState != EditState.ADDTEXT_INPUT) {
        switch (currentActionType) {
          case RUN_MAN:
          case CHAT_BOX:
          case SUBTITLE:
          case VIDEO_TAG:
            previewVideoView.pause();
            if (bottomEditFragment == null) {
              bottomEditFragment = BottomEditFragment.newFragment();
            }
            getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, bottomEditFragment)
                .commit();
            bottomEditFragment.changeElement(changeElementEvent);
            currentEditFragment = bottomEditFragment;
            previewVideoView.setOnVideoPlayProgressListener(onVideoPlayProgressListener);
            break;
          case STICKER:
            previewVideoView.pause();
            addStickerFragment();
            break;
          default:
            break;
        }
      }
    });
  }

  private void addEffectsFragment() {
    if (effectsFragment == null) {
      effectsFragment = EffectsFragment.newFragment();
    }
    getFragmentManager().beginTransaction().replace(R.id.fragment_container, effectsFragment)
        .commit();
    currentEditFragment = effectsFragment;
  }

  private void addStickerFragment() {
    if (stickersFragment == null) {
      stickersFragment = StickerParentFragment.newFragment();
    }
    getFragmentManager().beginTransaction().replace(R.id.fragment_container, stickersFragment)
        .commit();
    stickersFragment.notifyAdapter();
    currentEditFragment = stickersFragment;
  }

  private void finishRecord() {
    switch (editState) {
      case RECORD_TEXT:
        switch (currentActionType) {
          case RUN_MAN:
            VideoContentTaskManager.getInstance().getCurrentContentTask()
                .addRunMan(previewVideoView.getCurrentRunMan());
            previewVideoView.setRecordRunManOver();
            break;
          case CHAT_BOX:
            VideoContentTaskManager.getInstance().getCurrentContentTask()
                .addChatBox(previewVideoView.getCurrentChatBox());
            previewVideoView.setRecordChatBoxOver();
            break;
          case SUBTITLE:
            VideoContentTaskManager.getInstance().getCurrentContentTask()
                .addSubtitle(previewVideoView.getCurrentSubtitle());
            previewVideoView.setRecordSubtitleOver();
            break;
          case VIDEO_TAG:
            VideoContentTaskManager.getInstance().getCurrentContentTask()
                .addVideoTag(previewVideoView.getCurrentVideoTag());
            previewVideoView.setRecordVideoTagOver();
          default:
            break;
        }
        break;
      case RECORD_STICKER:
        VideoContentTaskManager.getInstance().getCurrentContentTask()
            .addSticker(previewVideoView.getCurrentSticker());
        previewVideoView.setRecordStickerOver();
        break;
      case RECORD_MONTAGE:
        VideoContentTaskManager.getInstance().getCurrentContentTask()
            .addMontage(previewVideoView.getCurrentMontage());
        previewVideoView.setRecordMontageOver();
        break;
      default:
        break;
    }
    ChangeElementState.setActionType(null);
    if (bottomEditFragment != null) {
      bottomEditFragment.changeElement(changeElementEvent);
    }
    elementsGroupRecyclerView.setVisibility(View.GONE);
    moreIcon.setVisibility(View.GONE);
    editLayout.setVisibility(View.VISIBLE);
    bottomLayout.setVisibility(View.VISIBLE);
    postTextView.setVisibility(View.VISIBLE);
    touchRecordView.setVisibility(View.GONE);
    hideTopBar(new Animator.AnimatorListener() {
      @Override
      public void onAnimationStart(Animator animator) {

      }

      @Override
      public void onAnimationEnd(Animator animator) {
        reset();
        canRedo = true;
        setRedoViewState();
        isPlaying = true;
        setPauseViewState();
        previewVideoView.seekTo(0);
        handler.postDelayed(() -> previewVideoView.resume(), 200L);
      }

      @Override
      public void onAnimationCancel(Animator animator) {

      }

      @Override
      public void onAnimationRepeat(Animator animator) {

      }
    });
  }


  private void addEditFragment() {
    if (currentEditFragment != null) {
      getFragmentManager().beginTransaction().replace(R.id.fragment_container, currentEditFragment)
          .commit();
    }
  }

  private void removeEditFragment() {
    if (currentEditFragment != null && currentEditFragment.isAdded()) {
      getFragmentManager().beginTransaction().remove(currentEditFragment).commit();
    }
  }

  public static VideoEditFragment newInstance(String videoFilePath, float videoRatio) {
    VideoEditFragment editCenterFragment = new VideoEditFragment();
    Bundle bundle = new Bundle();
    bundle.putString(VIDEO_FILE_PATH, videoFilePath);
    bundle.putFloat(VIDEO_RATIO, videoRatio);
    editCenterFragment.setArguments(bundle);
    return editCenterFragment;
  }

  private void initViewData() {
    chooseFilterView = new ChooseFilterView(getActivity());
    chooseMusicView = new ChooseMusicView(getActivity());
    chooseMusicView.setFragment(this);
    setThemeLayoutData();
    adjustUI();
    videoFilePath = getArguments().getString(VIDEO_FILE_PATH);
    previewVideoView.play(true);
    editSeekBar.setVideoPath(videoFilePath);
    changeVolumeSeekBar.setMax(MAX_VOLUME_SEEK);
    changeVolumeSeekBar.setProgress(currentVolumeSeek);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
    linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
    elementsGroupRecyclerView.setLayoutManager(linearLayoutManager);
    elementsGroupAdapter = new ElementsGroupAdapter(getActivity());
    elementsGroupRecyclerView.setAdapter(elementsGroupAdapter);
    elementsGroupRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
  }

  private void adjustUI() {
    float videoRatioWH = VideoContentTaskManager.getInstance().getCurrentContentTask()
        .getVideoRatioWH();

    int videoWidth = SystemUtils.getScreenWidthPx()
        - 2 * getResources().getDimensionPixelSize(R.dimen.edit_center_margin);

    int videoViewHeight = (int) (videoWidth / videoRatioWH);
    ViewGroup.LayoutParams layoutParams = previewVideoView.getLayoutParams();
    layoutParams.height = videoViewHeight;
    previewVideoView.setLayoutParams(layoutParams);

    RelativeLayout.LayoutParams editTextParams =
        (RelativeLayout.LayoutParams) inputEditText.getLayoutParams();
    editTextParams.topMargin = videoViewHeight / 2;
    inputEditText.setLayoutParams(editTextParams);
    inputLayout.setLayoutParams(layoutParams);
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onEventMainThread(SelectRunManEvent runManSelectEvent) {

    if (currentActionType != null) {
      editState = EditState.EDIT_TEXT;
      gonePreActionView(currentActionType);
      currentActionType = ActionType.RUN_MAN;
      runManContent = new RunManContent(runManSelectEvent.getRunManAttribute());
      removeEditFragment();
      EventBus.getDefault()
          .post(new ChangeElementEvent(currentActionType,
              runManSelectEvent.getRunManAttribute()));
      handler.post(() -> SystemUtils.showInputMethod(inputEditText));
      calMaxInput();
      selectElementGroupShow();
      setRedoViewState();
      setPauseViewState();
    } else {
      editState = EditState.ADDTEXT_INPUT;
      ChangeElementState.setEditState(EditState.ADDTEXT_INPUT);
      currentActionType = ActionType.RUN_MAN;
      runManContent = new RunManContent(runManSelectEvent.getRunManAttribute());
      inputLayout.setVisibility(View.VISIBLE);
      inputEditText.requestFocus();
      removeEditFragment();
      AnimUtils.ViewTranslationY(topBarLayout, -topBarLayout.getHeight(), 0, TOP_BAR_ANIM_DURATION,
          null);
      AnimUtils.ViewTranslationY(editParentLayout, 0, topBarLayout.getHeight(),
          TOP_BAR_ANIM_DURATION,
          new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
              handler.post(() -> SystemUtils.showInputMethod(inputEditText));
              calMaxInput();
              selectElementGroupShow();
              setRedoViewState();
              setPauseViewState();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
          });
    }
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onEventMainThread(SelectChatBoxEvent selectChatBoxEvent) {
    if (currentActionType != null) {
      editState = EditState.EDIT_TEXT;
      gonePreActionView(currentActionType);
      currentActionType = ActionType.CHAT_BOX;
      chatBoxChild = selectChatBoxEvent.getChatBoxChild();
      removeEditFragment();
      EventBus.getDefault()
          .post(new ChangeElementEvent(currentActionType,
              selectChatBoxEvent.getChatBoxChild()));
      handler.post(() -> SystemUtils.showInputMethod(inputEditText));
      calMaxInput();
      selectElementGroupShow();
      setRedoViewState();
      setPauseViewState();
    } else {
      editState = EditState.ADDTEXT_INPUT;
      ChangeElementState.setEditState(EditState.ADDTEXT_INPUT);
      currentActionType = ActionType.CHAT_BOX;
      chatBoxChild = selectChatBoxEvent.getChatBoxChild();
      inputLayout.setVisibility(View.VISIBLE);
      inputEditText.requestFocus();
      removeEditFragment();
      AnimUtils.ViewTranslationY(topBarLayout, -topBarLayout.getHeight(), 0, TOP_BAR_ANIM_DURATION,
          null);
      AnimUtils.ViewTranslationY(editParentLayout, 0, topBarLayout.getHeight(),
          TOP_BAR_ANIM_DURATION,
          new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
              handler.post(() -> SystemUtils.showInputMethod(inputEditText));
              calMaxInput();
              selectElementGroupShow();
              setRedoViewState();
              setPauseViewState();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
          });
    }
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onEventMainThread(SelectStickerEvent selectStickerEvent) {
      stickerImageData = selectStickerEvent.getStickerImageData();
      editState = EditState.EDIT_STICKER;
      currentActionType = ActionType.STICKER;
      removeEditFragment();
      SoundPoolPlayer.getInstance().playSound(R.raw.click);
      pauseVideo();
      stickerImageGroup = selectStickerEvent.getStickerImageGroup();
      previewVideoView.showStickerActionView(stickerImageData);

      changeEditView(true);
      selectElementGroupShow();
      moreIcon.setVisibility(View.VISIBLE);

      setRedoViewState();
      setPauseViewState();
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onEventMainThread(SelectEffectsEvent selectEffectsEvent) {
    SoundPoolPlayer.getInstance().playSound(R.raw.click);
    pauseVideo();
    removeEditFragment();
    currentMontageType = selectEffectsEvent.getMontageType();
    previewVideoView.setCurrentMontageActionType(currentMontageType);
    touchRecordView.setVisibility(View.VISIBLE);
    changeEditView(true);

    editState = EditState.RECORD_MONTAGE;
    setRedoViewState();
    setPauseViewState();
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onEventMainThread(SelectVideoTagEvent selectVideoTagEvent) {
    if (editState != EditState.ADDTEXT_INPUT) {
      if (currentActionType != null) {
        editState = EditState.EDIT_TEXT;
        gonePreActionView(currentActionType);
        currentActionType = ActionType.VIDEO_TAG;
        videoTagContent = selectVideoTagEvent.getVideoTagContent();
        removeEditFragment();
        EventBus.getDefault().post(new ChangeElementEvent(currentActionType, selectVideoTagEvent.getVideoTagContent()));
        handler.post(() -> SystemUtils.showInputMethod(inputEditText));
        calMaxInput();
        selectElementGroupShow();
        setRedoViewState();
        setPauseViewState();
      } else {
        inputLayout.setVisibility(View.VISIBLE);
        inputEditText.requestFocus();
        currentActionType = ActionType.VIDEO_TAG;
        editState = EditState.ADDTEXT_INPUT;
        ChangeElementState.setEditState(EditState.ADDTEXT_INPUT);
        videoTagContent = selectVideoTagEvent.getVideoTagContent();
        removeEditFragment();
        AnimUtils.ViewTranslationY(topBarLayout, -topBarLayout.getHeight(), 0,
            TOP_BAR_ANIM_DURATION, null);
        AnimUtils.ViewTranslationY(editParentLayout, 0, topBarLayout.getHeight(),
            TOP_BAR_ANIM_DURATION, new Animator.AnimatorListener() {
              @Override
              public void onAnimationStart(Animator animator) {

              }

              @Override
              public void onAnimationEnd(Animator animator) {
                handler.post(() -> SystemUtils.showInputMethod(inputEditText));
                calMaxInput();
                selectElementGroupShow();
                setRedoViewState();
                setPauseViewState();
              }


              @Override
              public void onAnimationCancel(Animator animator) {

              }

              @Override
              public void onAnimationRepeat(Animator animator) {

              }
            });
      }
    }
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onEventMainThread(NewSubtitleEvent newSubtitleEvent) {
    if (editState != EditState.ADDTEXT_INPUT) {
      if (currentActionType != null) {
        editState = EditState.EDIT_TEXT;
        gonePreActionView(currentActionType);
        currentActionType = ActionType.SUBTITLE;
        subtitleContent = newSubtitleEvent.getSubtitleContent();
        calMaxInput();
        removeEditFragment();
        EventBus.getDefault().post(new ChangeElementEvent(currentActionType,
            newSubtitleEvent.getSubtitleContent().getSubtitleType()));
        handler.post(() -> SystemUtils.showInputMethod(inputEditText));
        selectElementGroupShow();
        setRedoViewState();
        setPauseViewState();
      } else {
        currentActionType = ActionType.SUBTITLE;
        subtitleContent = newSubtitleEvent.getSubtitleContent();
        editState = EditState.ADDTEXT_INPUT;
        ChangeElementState.setEditState(EditState.ADDTEXT_INPUT);
        inputLayout.setVisibility(View.VISIBLE);
        inputEditText.requestFocus();
        removeEditFragment();
        AnimUtils.ViewTranslationY(topBarLayout, -topBarLayout.getHeight(), 0,
            TOP_BAR_ANIM_DURATION, null);
        AnimUtils.ViewTranslationY(editParentLayout, 0, topBarLayout.getHeight(),
            TOP_BAR_ANIM_DURATION, new Animator.AnimatorListener() {
              @Override
              public void onAnimationStart(Animator animator) {

              }

              @Override
              public void onAnimationEnd(Animator animator) {
                handler.post(() -> SystemUtils.showInputMethod(inputEditText));
                switch (newSubtitleEvent.getSubtitleContent().getSubtitleType()) {
                  case OFFICE_STORY_BLUE:
                  case OFFICE_STORY_YELLOW:
                  case OFFICE_STORY_RED:
                  case LOVE_NOTE_YELLOW:
                  case LOVE_NOTE_LIGHT_YELLOW:
                  case LOVE_NOTE_WHITE:
                    maxLength = MAX_LOVE_AND_NOTE_TEXT_LENGTH;
                    break;
                  case JP_STYLE_BLUE:
                  case JP_STYLE_BLACK:
                  case JP_STYLE_ORANGE:
                  case JP_STYLE_ORIGIN:
                  case JP_STYLE_YELLOW:
                    maxLength = MAX_JP_SUBTITLE_TEXT_LENGTH;
                    break;
                  case CARTOON_WHITE:
                  case CARTOON_BLACK:
                  case CARTOON_EN:
                  case CARTOON_JP:
                  case CLASSIC_BLACK:
                  case CLASSIC_EN:
                  case CLASSIC_JP:
                  case CLASSIC_WHITE:
                    maxLength = NO_LIMIT;
                    break;
                  default:
                    break;
                }
                calMaxInput();
                selectElementGroupShow();
                setRedoViewState();
                setPauseViewState();
              }

              @Override
              public void onAnimationCancel(Animator animator) {

              }

              @Override
              public void onAnimationRepeat(Animator animator) {

              }
            });
      }
    }
  }

  private void gonePreActionView(ActionType actionType) {
    previewVideoView.goneCurrentActionView(actionType);
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onEventMainThread(ExitBottomEditEvent exitBottomEditEvent) {
    if (previewVideoView != null) {
      previewVideoView.resume();
    }
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onEventMainThread(ChangeElementEvent changeElementEvent) {
    this.changeElementEvent = changeElementEvent;
    switch (changeElementEvent.getActionType()) {
      case RUN_MAN:
        RunManAttribute runManAttribute = (RunManAttribute) changeElementEvent.getObject();
        RunManContent runMan = new RunManContent(runManAttribute);
        if (!TextUtils.isEmpty(text)) {
          runMan.setTextContent(text);
        }
        if (editState == EditState.EDIT_TEXT && runMan != runManContent) {
          previewVideoView.changeRunManContent(runMan);
          runManContent = runMan;
        }
        break;
      case CHAT_BOX:
        ChatBoxChild chatBoxChild = (ChatBoxChild) changeElementEvent.getObject();
        if (editState == EditState.EDIT_TEXT) {
          chatBoxContent = new ChatBoxContent(chatBoxChild, text);
          this.chatBoxChild = chatBoxChild;
          previewVideoView.showChatBoxActionView(chatBoxContent);
        }
        break;
      case STICKER:
        stickerImageData = (StickerImageData) changeElementEvent.getObject();
        previewVideoView.showStickerActionView(stickerImageData);
        break;
      case SUBTITLE:
        String tempText = text;
        if (maxLength > 0 && tempText.length() > maxLength) {
          tempText = text.substring(0, maxLength);
        }
        subtitleContent.setContent(tempText);
        SubtitleType subtitleType = (SubtitleType) changeElementEvent.getObject();
        subtitleContent = new SubtitleContent.Builder()
            .setSubtitleType(subtitleType).setContent(text).build();
        previewVideoView.showCurrentSubtitle(subtitleContent);
        break;
      case VIDEO_TAG:
        VideoTagContent videoTagContent = (VideoTagContent) changeElementEvent.getObject();
        if (editState == EditState.EDIT_TEXT) {
          this.videoTagContent = videoTagContent;
          videoTagContent.setContent(text);
          previewVideoView.showVideoTagView(videoTagContent);
        }
        break;
      default:
        break;
    }
  }

  private void hideTopBar(Animator.AnimatorListener listener) {
    titleTextView.setText(getResources().getString(R.string.input_text));
    AnimUtils.ViewTranslationY(topBarLayout, 0, -topBarLayout.getHeight(),
        TOP_BAR_ANIM_DURATION, null);
    AnimUtils.ViewTranslationY(editParentLayout, topBarLayout.getHeight(), 0,
        TOP_BAR_ANIM_DURATION, listener);
  }

  private void postVideo() {
    if (!isPost) {
      isPost = true;
      previewVideoView.releaseVideo();
      EventBus.getDefault()
          .post(new PostStartCallbackEvent(
              VideoContentTaskManager.getInstance().getCurrentContentTask()));
      getActivity().finish();
    }
  }

  private void showMaxInput(String str) {
    int length = str.trim().length();
    if (maxLength != NO_LIMIT) {
      if (length == 0) {
        subtitleTextView.setVisibility(View.GONE);
      } else if (length <= maxLength) {
        subtitleTextView.setText(getResources().getString(R.string.can_input_count,
            (maxLength - length)));
        subtitleTextView.setVisibility(View.VISIBLE);
        subtitleTextView.setTextColor(getResources().getColor(R.color.white));
      } else {
        subtitleTextView.setText(getResources().getString(R.string.exceed_count,
            (length - maxLength)));
        subtitleTextView.setVisibility(View.VISIBLE);
        subtitleTextView.setTextColor(getResources().getColor(R.color.main_red));
      }
    }
  }

  private void calMaxInput() {
    if (currentActionType != null) {
      switch (currentActionType) {
        case RUN_MAN:
          if (runManContent.getRunManAttribute().getRunManSecondCountTextAttribute() != null) {
            maxLength = runManContent.getRunManAttribute().getRunManFirstCountTextAttribute()
                .getMaxTextSize()
                + runManContent.getRunManAttribute().getRunManSecondCountTextAttribute()
                .getMaxTextSize();
          } else if (runManContent.getRunManAttribute().getRunManFirstCountTextAttribute() != null) {
            maxLength = runManContent.getRunManAttribute().getRunManFirstCountTextAttribute()
                .getMaxTextSize();
          } else if (runManContent.getRunManAttribute().getVertaicalTypefaceAttribute() != null) {
            maxLength =
                runManContent.getRunManAttribute().getVertaicalTypefaceAttribute().getMaxSize();
          }
          break;
        case CHAT_BOX:
        case VIDEO_TAG:
          maxLength = NO_LIMIT;
          break;
        case SUBTITLE:
          if (subtitleContent != null) {
            switch (subtitleContent.getSubtitleType()) {
              case OFFICE_STORY_BLUE:
              case OFFICE_STORY_YELLOW:
              case OFFICE_STORY_RED:
              case LOVE_NOTE_YELLOW:
              case LOVE_NOTE_LIGHT_YELLOW:
              case LOVE_NOTE_WHITE:
                maxLength = MAX_LOVE_AND_NOTE_TEXT_LENGTH;
                break;
              case JP_STYLE_BLUE:
              case JP_STYLE_BLACK:
              case JP_STYLE_ORANGE:
              case JP_STYLE_ORIGIN:
              case JP_STYLE_YELLOW:
                maxLength = MAX_JP_SUBTITLE_TEXT_LENGTH;
                break;
              case CARTOON_WHITE:
              case CARTOON_BLACK:
              case CARTOON_EN:
              case CARTOON_JP:
              case CLASSIC_BLACK:
              case CLASSIC_EN:
              case CLASSIC_JP:
              case CLASSIC_WHITE:
                maxLength = NO_LIMIT;
                break;
              default:
                break;
            }
          }
          break;
        default:
          maxLength = NO_LIMIT;
          break;
      }
    }
  }

  private void reset() {
    touchRecordView.setVisibility(View.GONE);
    inputEditText.setText("");
    inputLayout.setVisibility(View.GONE);
    subtitleTextView.setVisibility(View.GONE);
    runManContent = null;
    chatBoxChild = null;
    chatBoxContent = null;
    videoTagContent = null;
    currentActionType = null;
    editState = EditState.IDLE;
    setPauseViewState();
    subtitleContent = null;
    text = "";
    ChangeElementState.setObject(null);
    ChangeElementState.setActionType(null);
    if (bottomEditFragment != null) {
      bottomEditFragment.changeElement(changeElementEvent);
    }
  }

  private void setFunctionState() {
    switch (currtentFunction) {
      case EDIT:
        varietyImageView.setImageResource(R.drawable.addtext_pressed);
        filterImageView.setImageResource(R.drawable.filternew);
        musicImageView.setImageResource(R.drawable.musicnew);
        break;
      case FILTER:
        varietyImageView.setImageResource(R.drawable.addtext);
        filterImageView.setImageResource(R.drawable.filternew_pressed);
        musicImageView.setImageResource(R.drawable.musicnew);
        break;
      case MUSIC:
        varietyImageView.setImageResource(R.drawable.addtext);
        filterImageView.setImageResource(R.drawable.filternew);
        musicImageView.setImageResource(R.drawable.musicnew_pressed);
        break;
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == Activity.RESULT_OK) {
      switch (requestCode) {
        case SoundUtils.CHOOSE_MUSIC_TAG:
          if (data != null) {
            chooseMusicView.setLocalMusic(data.getData());
          }
          break;
        default:
          break;
      }
    }
  }

  private void selectElementGroupShow() {
    editLayout.setVisibility(View.GONE);
    bottomLayout.setVisibility(View.GONE);
    postTextView.setVisibility(View.GONE);
    elementsGroupRecyclerView.setVisibility(View.VISIBLE);
    moreIcon.setVisibility(View.VISIBLE);
    if (currentActionType != null) {
      switch (currentActionType) {
        case RUN_MAN:
          List<Object> runManAttributeList = new ArrayList<>();
          if (Stream.of(DefaultRunManManager.getInstance().getRunManAttributes())
              .anyMatch(
                  runManAttribute -> runManAttribute.equals(runManContent.getRunManAttribute()))) {
            Stream.of(DefaultRunManManager.getInstance().getRunManAttributes())
                .forEach(runManAttriBute -> runManAttributeList.add(runManAttriBute));
          } else if (Stream.of(DefaultRunManManager.getInstance().getKangxiAttributes())
              .anyMatch(
                  runManAttribute -> runManAttribute.equals(runManContent.getRunManAttribute()))) {
            Stream.of(DefaultRunManManager.getInstance().getKangxiAttributes())
                .forEach(runManAttriBute -> runManAttributeList.add(runManAttriBute));
          }
          elementsGroupAdapter.setElementList(ActionType.RUN_MAN, runManAttributeList);
          elementsGroupRecyclerView.setVisibility(View.VISIBLE);
          moreIcon.setVisibility(View.VISIBLE);
          break;
        case CHAT_BOX:
          List<Object> chatBoxList = new ArrayList<>();
          Stream.of(DefaultChatBoxManager.getInstance().getBoxGroups()).forEach(chatBoxGroup -> {
            if (Stream.of(chatBoxGroup.getChatBoxChildList())
                .anyMatch(chatBoxChild -> chatBoxChild.equals(this.chatBoxChild))) {
              Stream.of(chatBoxGroup.getChatBoxChildList())
                  .forEach(chatBoxChild -> chatBoxList.add(chatBoxChild));
            }
          });
          elementsGroupAdapter.setElementList(ActionType.CHAT_BOX, chatBoxList);
          elementsGroupRecyclerView.setVisibility(View.VISIBLE);
          moreIcon.setVisibility(View.VISIBLE);
          break;
        case SUBTITLE:
          List<Object> subtitleList = new ArrayList<>();
          if (Stream.of(SubtitleManager.getInstance().getJpSubtitleTypeList())
              .anyMatch(subtitleType -> subtitleType.equals(subtitleContent.getSubtitleType()))) {
            Stream.of(SubtitleManager.getInstance().getJpSubtitleTypeList())
                .forEach(subtitleType -> subtitleList.add(subtitleType));
          } else if (Stream.of(SubtitleManager.getInstance().getLoveSubtitleTypeList())
              .anyMatch(subtitleType -> subtitleType.equals(subtitleContent.getSubtitleType()))) {
            Stream.of(SubtitleManager.getInstance().getLoveSubtitleTypeList())
                .forEach(subtitleType -> subtitleList.add(subtitleType));
          } else if (Stream.of(SubtitleManager.getInstance().getOfficeSubtitleTypeList())
              .anyMatch(subtitleType -> subtitleType.equals(subtitleContent.getSubtitleType()))) {
            Stream.of(SubtitleManager.getInstance().getOfficeSubtitleTypeList())
                .forEach(subtitleType -> subtitleList.add(subtitleType));
          } else if (Stream.of(SubtitleManager.getInstance().getClassicSubtitle())
              .anyMatch(subtitleType -> subtitleType.equals(subtitleContent.getSubtitleType()))) {
            Stream.of(SubtitleManager.getInstance().getClassicSubtitle())
                .forEach(subtitleType -> subtitleList.add(subtitleType));
          }
          elementsGroupAdapter.setElementList(ActionType.SUBTITLE, subtitleList);
          elementsGroupRecyclerView.setVisibility(View.VISIBLE);
          moreIcon.setVisibility(View.VISIBLE);
          break;
        case VIDEO_TAG:
          List<Object> videoTagList = new ArrayList<>();
          if (Stream.of(VideoTagManager.getInstance().getBrifeVideoTagContentList()).anyMatch(
              videoTagContent -> videoTagContent.equals(this.videoTagContent))) {
            Stream.of(VideoTagManager.getInstance().getBrifeVideoTagContentList()).forEach(
                videoTagContent -> videoTagList.add(videoTagContent));
          } else if (Stream.of(VideoTagManager.getInstance().getPersonalityVideoTagContentList())
              .anyMatch(videoTagContent -> videoTagContent.equals(this.videoTagContent))) {
            Stream.of(VideoTagManager.getInstance().getPersonalityVideoTagContentList()).forEach(
                videoTagContent -> videoTagList.add(videoTagContent));
          }
          elementsGroupAdapter.setElementList(ActionType.VIDEO_TAG, videoTagList);
          elementsGroupRecyclerView.setVisibility(View.VISIBLE);
          break;
        case STICKER:
          List<Object> stickerList = new ArrayList<>();
          if (stickerImageGroup == null || stickerImageGroup.getStickerImageDataList().isEmpty()) {
            if (!DefaultStickerManager.getInstance().getStickerImageGroupList().isEmpty()) {
              stickerImageGroup =
                  Stream.of(DefaultStickerManager.getInstance().getStickerImageGroupList()).filter(
                      temp -> Stream.of(temp).anyMatch(
                          stickerImageData -> stickerImageData.equals(this.stickerImageData)))
                      .findFirst().get();
            }
          }
          if (stickerImageGroup != null) {
            Stream.of(stickerImageGroup.getStickerImageDataList())
                .forEach(sticker -> stickerList.add(sticker));
          }
          elementsGroupAdapter.setElementList(ActionType.STICKER, stickerList);
          elementsGroupRecyclerView.setVisibility(View.VISIBLE);
          break;
        default:
          break;
      }
    }
  }

  private void setThemeLayoutData() {
    chooseFilterView.setPreVideoView(previewVideoView);
    filterLayout.addView(chooseFilterView);
    chooseMusicView.setVideoView(previewVideoView);
    chooseMusicView.setWaveformView(waveformView);
    chooseMusicView.setVideoFilePath(videoFilePath);
    musicLayout.addView(chooseMusicView);
  }

  public void showLoadingView() {
    if (loadingLayout != null) {
      loadingLayout.setVisibility(View.VISIBLE);
      loadingLayout.setOnTouchListener((v, event) -> true);
      if (draweeController == null) {
        draweeController = ImageUtils.showResGif(R.drawable.loading_video, loadingVideo);
      } else {
        if (draweeController.getAnimatable() != null) {
          if (!draweeController.getAnimatable().isRunning()) {
            draweeController.getAnimatable().start();
          }
        } else {
          draweeController = ImageUtils.showResGif(R.drawable.loading_video, loadingVideo);
        }
      }
    }
  }

  private void setLoadingLayoutUI() {
    float videoRatioWH =
        VideoContentTaskManager.getInstance().getCurrentContentTask().getVideoRatioWH();
    RelativeLayout.LayoutParams layoutParams =
        (RelativeLayout.LayoutParams) loadingContent.getLayoutParams();
    layoutParams.width = SystemUtils.getScreenWidthPx();
    layoutParams.height = (int) (SystemUtils.getScreenWidthPx() / videoRatioWH);
    loadingContent.setLayoutParams(layoutParams);
  }

  public void goneLoadingView() {
    if (loadingLayout != null) {
      loadingLayout.setVisibility(View.GONE);
      if (draweeController.getAnimatable() != null) {
        if (draweeController.getAnimatable().isRunning()) {
          draweeController.getAnimatable().stop();
        }
      }
    }
  }

  private void pauseVideo() {
    previewVideoView.pause();
    previewVideoView.seekTo(0);
    editSeekBar.setProgress(0);
  }

  private boolean backEditStaus() {
    switch (editState) {
      case IDLE:
        back();
        return true;
      case ADDTEXT_INPUT:
      case EDIT_TEXT:
        if (currentEditFragment != null && currentEditFragment.isAdded()) {
          getFragmentManager().beginTransaction().remove(currentEditFragment).commit();
        } else {
          titleTextView.setText(getResources().getString(R.string.input_text));
          editLayout.setVisibility(View.VISIBLE);
          bottomLayout.setVisibility(View.VISIBLE);
          postTextView.setVisibility(View.VISIBLE);
          elementsGroupRecyclerView.setVisibility(View.GONE);
          moreIcon.setVisibility(View.GONE);
          previewVideoView.hideRunManContentView();
          previewVideoView.hideChatBoxContentView();
          previewVideoView.hideVideoTagContentView();
          previewVideoView.removeSubtitleView();
          hideTopBar(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
              SystemUtils.hideInputMethod(inputEditText);
              addEditFragment();
              reset();
              setRedoViewState();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
          });
        }
        return true;
      case RECORD_TEXT:
        switch (currentActionType) {
          case RUN_MAN:
            previewVideoView.showRunManContentView(runManContent);
            break;
          case CHAT_BOX:
            previewVideoView.showChatBoxActionView(chatBoxContent);
            break;
          case SUBTITLE:
            previewVideoView.showCurrentSubtitle(subtitleContent);
            break;
          case VIDEO_TAG:
            previewVideoView.showVideoTagView(videoTagContent);
            break;
          default:
            break;
        }
        titleTextView.setText(getResources().getString(R.string.edit_element));
        elementsGroupRecyclerView.setVisibility(View.VISIBLE);
        moreIcon.setVisibility(View.VISIBLE);
        touchRecordView.setVisibility(View.GONE);
        editState = EditState.EDIT_TEXT;
        return true;
      case EDIT_STICKER:
        titleTextView.setText(getResources().getString(R.string.input_text));
        editLayout.setVisibility(View.VISIBLE);
        bottomLayout.setVisibility(View.VISIBLE);
        postTextView.setVisibility(View.VISIBLE);
        elementsGroupRecyclerView.setVisibility(View.GONE);
        moreIcon.setVisibility(View.GONE);
        previewVideoView.goneCurrentActionView(ActionType.STICKER);
        hideTopBar(new Animator.AnimatorListener() {
          @Override
          public void onAnimationStart(Animator animator) {

          }

          @Override
          public void onAnimationEnd(Animator animator) {
            addStickerFragment();
            reset();
            setRedoViewState();
          }

          @Override
          public void onAnimationCancel(Animator animator) {

          }

          @Override
          public void onAnimationRepeat(Animator animator) {

          }
        });
        return true;
      case RECORD_STICKER:
        titleTextView.setText(getResources().getString(R.string.edit_element));
        previewVideoView.showStickerActionView(stickerImageData);
        elementsGroupRecyclerView.setVisibility(View.VISIBLE);
        moreIcon.setVisibility(View.VISIBLE);
        touchRecordView.setVisibility(View.GONE);
        editState = EditState.EDIT_STICKER;
        return true;
      case RECORD_MONTAGE:
        titleTextView.setText(getResources().getString(R.string.edit_element));
        editLayout.setVisibility(View.VISIBLE);
        bottomLayout.setVisibility(View.VISIBLE);
        postTextView.setVisibility(View.VISIBLE);
        touchRecordView.setVisibility(View.GONE);
        previewVideoView.setMontageIconGone();
        hideTopBar(new Animator.AnimatorListener() {
          @Override
          public void onAnimationStart(Animator animator) {

          }

          @Override
          public void onAnimationEnd(Animator animator) {
            addEffectsFragment();
            reset();
            setRedoViewState();
          }

          @Override
          public void onAnimationCancel(Animator animator) {

          }

          @Override
          public void onAnimationRepeat(Animator animator) {

          }
        });
        return true;
    }
    return false;
  }

  private void back() {
    getFragmentManager().beginTransaction().add(R.id.container, new DropEditedVideoDialog())
        .addToBackStack(null).commit();
    setRedoViewState();
    setPauseViewState();
  }

  private void changeEditView(boolean isEdit) {
    if (isEdit) {
      editLayout.setVisibility(View.GONE);
      bottomLayout.setVisibility(View.GONE);
      postTextView.setVisibility(View.GONE);
      inputCloseImageView.setVisibility(View.VISIBLE);
      AnimUtils.ViewTranslationY(topBarLayout, -topBarLayout.getHeight(), 0, TOP_BAR_ANIM_DURATION,
          null);
      AnimUtils.ViewTranslationY(editParentLayout, 0, topBarLayout.getHeight(),
          TOP_BAR_ANIM_DURATION, null);
      titleTextView.setText("");
    } else {
      editLayout.setVisibility(View.GONE);
      bottomLayout.setVisibility(View.GONE);
      postTextView.setVisibility(View.GONE);
      hideTopBar(null);
    }
  }

  private void editElement() {
    switch (editState) {
      case ADDTEXT_INPUT: {
        titleTextView.setText(getResources().getString(R.string.edit_element));
        if (TextUtils.isEmpty(text)) {
          text = inputEditText.getText().toString().trim();
        }
        inputEditText.setText("");
        subtitleTextView.setVisibility(View.GONE);
        calMaxInput();
        if (!TextUtils.isEmpty(text)) {
          editState = EditState.EDIT_TEXT;
          switch (currentActionType) {
            case RUN_MAN:
              if (runManContent != null) {
                runManContent.setTextContent(text);
                previewVideoView.showRunManContentView(runManContent);
              }
              break;
            case CHAT_BOX:
              if (chatBoxChild != null) {
                chatBoxContent = new ChatBoxContent(chatBoxChild, text);
                previewVideoView.showChatBoxActionView(chatBoxContent);
              }
              break;
            case SUBTITLE:
              String subtitleText = text;
              if (maxLength > 0 && subtitleText.length() > maxLength) {
                subtitleText = text.substring(0, maxLength);
              }
              subtitleContent.setContent(subtitleText);
              previewVideoView.showCurrentSubtitle(subtitleContent);
              break;
            case VIDEO_TAG:
              String tagText = text;
              if (maxLength > 0 && tagText.length() > maxLength) {
                tagText = text.substring(0, maxLength);
              }
              videoTagContent.setContent(tagText);
              previewVideoView.showVideoTagView(videoTagContent);
              break;
            default:
              break;
          }
          inputEditText.clearFocus();
          SystemUtils.hideInputMethod(inputEditText);
          new Handler().post(() -> inputLayout.setVisibility(View.GONE));
        } else {
          Toast.makeText(MikuApplication.context, R.string.input_text_empty,
              Toast.LENGTH_SHORT).show();
        }
        break;
      }
      case EDIT_TEXT:
        if (currentEditFragment != null && currentEditFragment.isAdded()) {
          getFragmentManager().beginTransaction().remove(currentEditFragment).commit();
        }
        titleTextView.setText(getResources().getString(R.string.add_element_video));
        elementsGroupRecyclerView.setVisibility(View.GONE);
        moreIcon.setVisibility(View.GONE);
        touchRecordView.setVisibility(View.VISIBLE);
        switch (currentActionType) {
          case RUN_MAN:
            previewVideoView.setRunManContent(runManContent);
            break;
          case CHAT_BOX:
            previewVideoView.setChatBoxContent(chatBoxContent);
            break;
          case SUBTITLE:
            previewVideoView.setCurrentSubtitle(subtitleContent);
            break;
          case VIDEO_TAG:
            previewVideoView.setCurrentVideoTag(videoTagContent);
            break;
          default:
            break;
        }
        editState = EditState.RECORD_TEXT;
        break;
      case RECORD_TEXT:
        if (currentEditFragment != null && currentEditFragment.isAdded()) {
          getFragmentManager().beginTransaction().remove(currentEditFragment).commit();
        }
        titleTextView.setText(getResources().getString(R.string.add_element_video));
        previewVideoView.saveDefaultTask();
        ChangeElementState.setActionType(null);
        bottomEditFragment.changeElement(changeElementEvent);
        elementsGroupRecyclerView.setVisibility(View.GONE);
        moreIcon.setVisibility(View.GONE);
        editLayout.setVisibility(View.VISIBLE);
        bottomLayout.setVisibility(View.VISIBLE);
        postTextView.setVisibility(View.VISIBLE);
        editState = EditState.IDLE;
        canRedo = true;
        setRedoViewState();
        hideTopBar(new Animator.AnimatorListener() {
          @Override
          public void onAnimationStart(Animator animator) {

          }

          @Override
          public void onAnimationEnd(Animator animator) {
            reset();
            previewVideoView.setLooping(true);
            handler.postDelayed(() -> previewVideoView.resume(), 200L);
          }

          @Override
          public void onAnimationCancel(Animator animator) {

          }

          @Override
          public void onAnimationRepeat(Animator animator) {

          }
        });
        break;
      case EDIT_STICKER:
        if (currentEditFragment != null && currentEditFragment.isAdded()) {
          getFragmentManager().beginTransaction().remove(currentEditFragment).commit();
        }
        titleTextView.setText(getResources().getString(R.string.edit_element));
        SoundPoolPlayer.getInstance().playSound(R.raw.click);
        previewVideoView.setStickerImageData(stickerImageData);
        editState = EditState.RECORD_STICKER;
        elementsGroupRecyclerView.setVisibility(View.GONE);
        moreIcon.setVisibility(View.GONE);
        touchRecordView.setVisibility(View.VISIBLE);
        break;
      case RECORD_STICKER:
        titleTextView.setText(getResources().getString(R.string.add_element_video));
        SoundPoolPlayer.getInstance().playSound(R.raw.click);
        previewVideoView.saveDefaultTask();
        ChangeElementState.setActionType(null);
        if (bottomEditFragment != null) {
          bottomEditFragment.changeElement(changeElementEvent);
        }
        elementsGroupRecyclerView.setVisibility(View.GONE);
        moreIcon.setVisibility(View.GONE);
        editLayout.setVisibility(View.VISIBLE);
        bottomLayout.setVisibility(View.VISIBLE);
        postTextView.setVisibility(View.VISIBLE);
        editState = EditState.IDLE;
        canRedo = true;
        setRedoViewState();
        hideTopBar(new Animator.AnimatorListener() {
          @Override
          public void onAnimationStart(Animator animator) {

          }

          @Override
          public void onAnimationEnd(Animator animator) {
            reset();
            previewVideoView.setLooping(true);
            handler.postDelayed(() -> previewVideoView.resume(), 200L);
          }

          @Override
          public void onAnimationCancel(Animator animator) {

          }

          @Override
          public void onAnimationRepeat(Animator animator) {

          }
        });
        finishRecord();
        break;
      case RECORD_MONTAGE:
        titleTextView.setText(getResources().getString(R.string.add_element_video));
        SoundPoolPlayer.getInstance().playSound(R.raw.click);
        elementsGroupRecyclerView.setVisibility(View.GONE);
        moreIcon.setVisibility(View.GONE);
        editLayout.setVisibility(View.VISIBLE);
        bottomLayout.setVisibility(View.VISIBLE);
        postTextView.setVisibility(View.VISIBLE);
        editState = EditState.IDLE;
        canRedo = true;
        setRedoViewState();
        previewVideoView.saveDefaultTask();
        hideTopBar(new Animator.AnimatorListener() {
          @Override
          public void onAnimationStart(Animator animator) {

          }

          @Override
          public void onAnimationEnd(Animator animator) {
            reset();
            previewVideoView.setLooping(true);
            handler.postDelayed(() -> previewVideoView.resume(), 200L);
          }

          @Override
          public void onAnimationCancel(Animator animator) {

          }

          @Override
          public void onAnimationRepeat(Animator animator) {

          }
        });
        finishRecord();
        break;
      default:
        break;
    }
    ChangeElementState.setEditState(editState);
    setPauseViewState();
  }

  private void setPauseViewState() {
    if (editState == EditState.IDLE) {
      if (isPlaying) {
        pauseImageView.setImageResource(R.drawable.pause_play);
      } else {
        pauseImageView.setImageResource(R.drawable.continue_play);
      }
      pauseImageView.setVisibility(View.VISIBLE);
    } else {
      pauseImageView.setVisibility(View.GONE);
    }
  }

  private void setRedoViewState() {
    if (editState == EditState.IDLE) {
      if (canRedo) {
        redoImageView.setVisibility(View.VISIBLE);
      } else {
        redoImageView.setVisibility(View.GONE);
      }
    } else {
      redoImageView.setVisibility(View.GONE);
    }
  }

  private enum BottomAction {
    VARIETY, FILLTER, MUSIC
  }
}
