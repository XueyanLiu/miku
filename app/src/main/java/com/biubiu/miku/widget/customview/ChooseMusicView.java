package com.biubiu.miku.widget.customview;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.biubiu.miku.R;
import com.biubiu.miku.module.videoedit.VideoEditFragment;
import com.biubiu.miku.util.AddTaskListener;
import com.biubiu.miku.util.FileUtils;
import com.biubiu.miku.util.ThreadPool;
import com.biubiu.miku.util.sound.SoundFile;
import com.biubiu.miku.util.sound.SoundPoolPlayer;
import com.biubiu.miku.util.sound.SoundUtils;
import com.biubiu.miku.util.video.VideoUtils;
import com.biubiu.miku.util.video.action.music.Music;
import com.biubiu.miku.util.video.task.VideoContentTaskManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChooseMusicView extends RelativeLayout {

  @BindView(R.id.no_music)
  ImageView noMusic;
  @BindView(R.id.local_music)
  ImageView localMusic;
  @BindView(R.id.date)
  ImageView date;
  @BindView(R.id.picnic)
  ImageView picnic;
  @BindView(R.id.joy)
  ImageView joy;
  @BindView(R.id.silent)
  ImageView silent;
  @BindView(R.id.game)
  ImageView game;
  @BindView(R.id.sorrow)
  ImageView sorrow;
  @BindView(R.id.romance)
  ImageView romance;
  @BindView(R.id.musicbox)
  ImageView musicBox;
  @BindView(R.id.fresh)
  ImageView fresh;
  @BindView(R.id.funny1)
  ImageView funny1;
  @BindView(R.id.funny2)
  ImageView funny2;
  @BindView(R.id.lazy)
  ImageView lazy;
  @BindView(R.id.midnight)
  ImageView midNight;

  private ImageView currentMusic, preMusic;
  private MusicActionState currentMusicState;
  private List<AddTaskListener> addTaskListeners = new ArrayList<>();
  private SoundFile soundFile;
  private String videoFilePath;
  private String audioFilePath;
  private int currentVolumeSeek = 50;
  private float musicVolume = 1f;
  private float soundVolume = 1f;
  private WaveformView waveformView;
  private PreviewVideoView videoView;
  private Fragment fragment;
  private SeekBar seekBar;

  private final PreviewVideoView.OnVideoPlayProgressListener onVideoPlayProgressListener = (videoDuration, progress) -> {
    waveformView.setWaveformViewProgress(progress);
  };

  enum MusicActionState {
    NONE, PREVIEW, LOCAL_MUSIC, DATE, PICNIC, JOY, SILENT, GAME, SORROW, ROMANCE, MUSIC_BOX, FRESH,
    FUNNY1, FUNNY2, MIDNIGHT, LAZY
  }

  public ChooseMusicView(Context context) {
    super(context);
  }

  public ChooseMusicView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public ChooseMusicView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  {
    LayoutInflater inflater =
        (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View rootView = inflater.inflate(R.layout.choose_music_layout, this, true);
    ButterKnife.bind(rootView);
    currentMusic = noMusic;
    preMusic = noMusic;
    currentMusicState = MusicActionState.NONE;
    addListener();
  }

  private void addListener() {
    noMusic.setOnClickListener(v1 -> {
      SoundPoolPlayer.getInstance().playSound(R.raw.click);
      currentMusic = noMusic;
      soundFile = null;
      currentMusicState = MusicActionState.NONE;
      setCurrentMusicActionState();
    });
    localMusic.setOnClickListener(v -> {
      SoundPoolPlayer.getInstance().playSound(R.raw.click);
      currentMusic = localMusic;
      currentMusicState = MusicActionState.LOCAL_MUSIC;
      setCurrentMusicActionState();
    });
    date.setOnClickListener(v -> {
      SoundPoolPlayer.getInstance().playSound(R.raw.click);
      currentMusic = date;
      currentMusicState = MusicActionState.DATE;
      setCurrentMusicActionState();
    });
    picnic.setOnClickListener(v -> {
      SoundPoolPlayer.getInstance().playSound(R.raw.click);
      currentMusic = picnic;
      currentMusicState = MusicActionState.PICNIC;
      setCurrentMusicActionState();
    });
    joy.setOnClickListener(v -> {
      SoundPoolPlayer.getInstance().playSound(R.raw.click);
      currentMusic = joy;
      currentMusicState = MusicActionState.JOY;
      setCurrentMusicActionState();
    });
    silent.setOnClickListener(v -> {
      SoundPoolPlayer.getInstance().playSound(R.raw.click);
      currentMusic = silent;
      currentMusicState = MusicActionState.SILENT;
      setCurrentMusicActionState();
    });
    game.setOnClickListener(v -> {
      SoundPoolPlayer.getInstance().playSound(R.raw.click);
      currentMusic = game;
      currentMusicState = MusicActionState.GAME;
      setCurrentMusicActionState();
    });
    sorrow.setOnClickListener(v -> {
      SoundPoolPlayer.getInstance().playSound(R.raw.click);
      currentMusic = sorrow;
      currentMusicState = MusicActionState.SORROW;
      setCurrentMusicActionState();
    });
    romance.setOnClickListener(v -> {
      SoundPoolPlayer.getInstance().playSound(R.raw.click);
      currentMusic = romance;
      currentMusicState = MusicActionState.ROMANCE;
      setCurrentMusicActionState();
    });
    musicBox.setOnClickListener(v -> {
      SoundPoolPlayer.getInstance().playSound(R.raw.click);
      currentMusic = musicBox;
      currentMusicState = MusicActionState.MUSIC_BOX;
      setCurrentMusicActionState();
    });
    fresh.setOnClickListener(v -> {
      SoundPoolPlayer.getInstance().playSound(R.raw.click);
      currentMusic = fresh;
      currentMusicState = MusicActionState.FRESH;
      setCurrentMusicActionState();
    });
    funny1.setOnClickListener(v -> {
      SoundPoolPlayer.getInstance().playSound(R.raw.click);
      currentMusic = funny1;
      currentMusicState = MusicActionState.FUNNY1;
      setCurrentMusicActionState();
    });
    funny2.setOnClickListener(v -> {
      SoundPoolPlayer.getInstance().playSound(R.raw.click);
      currentMusic = funny2;
      currentMusicState = MusicActionState.FUNNY2;
      setCurrentMusicActionState();
    });
    lazy.setOnClickListener(v -> {
      SoundPoolPlayer.getInstance().playSound(R.raw.click);
      currentMusic = lazy;
      currentMusicState = MusicActionState.LAZY;
      setCurrentMusicActionState();
    });
    midNight.setOnClickListener(v -> {
      SoundPoolPlayer.getInstance().playSound(R.raw.click);
      currentMusic = midNight;
      currentMusicState = MusicActionState.MIDNIGHT;
      setCurrentMusicActionState();
    });
  }

  public void setVideoView(PreviewVideoView videoView) {
    this.videoView = videoView;
  }

  public void setWaveformView(WaveformView waveformView) {
    this.waveformView = waveformView;
  }

  public void setFragment(Fragment fragment) {
    this.fragment = fragment;
  }

  public void setVideoFilePath(String videoFilePath) {
    this.videoFilePath = videoFilePath;
  }

  public void checkMusicState() {
    if (currentMusicState == MusicActionState.NONE) {
      waveformView.setVisibility(GONE);
    } else {
      waveformView.setVisibility(VISIBLE);
    }
  }

  private void setCurrentMusicActionState() {
    preMusic = currentMusic;

    switch (currentMusicState) {
      case NONE:
        videoView.changeMusicFile(null);
        waveformView.setVisibility(GONE);
        break;
      // case PREVIEW:
      // break;
      case LOCAL_MUSIC:
        SoundUtils.getMusic(fragment);
        break;
      case DATE:
        createSoundFileFromAssets("music/Date.mp3");
        break;
      case PICNIC:
        createSoundFileFromAssets("music/Picnic.mp3");
        break;
      case JOY:
        createSoundFileFromAssets("music/Joy.mp3");
        break;
      case SILENT:
        createSoundFileFromAssets("music/Silent.mp3");
        break;
      case GAME:
        createSoundFileFromAssets("music/Game.mp3");
        break;
      case SORROW:
        createSoundFileFromAssets("music/Sorrow.mp3");
        break;
      case ROMANCE:
        createSoundFileFromAssets("music/romance.mp3");
        break;
      case MUSIC_BOX:
        createSoundFileFromAssets("music/musicbox.mp3");
        break;
      case FRESH:
        createSoundFileFromAssets("music/fresh.mp3");
        break;
      case FUNNY1:
        createSoundFileFromAssets("music/funny1.mp3");
        break;
      case FUNNY2:
        createSoundFileFromAssets("music/funny2.mp3");
        break;
      case MIDNIGHT:
        createSoundFileFromAssets("music/midnight.mp3");
        break;
      case LAZY:
        createSoundFileFromAssets("music/Lazy.mp3");
        break;
      default:
        break;
    }
  }

  public void setLocalMusic(Uri uri) {
    String audioPath = getRealAudioPath(uri);
    createSoundFile(audioPath);
  }

  public void setSeekBar(SeekBar seekBar) {
    this.seekBar = seekBar;
  }

  public void updateVolume(int progress) {
    currentVolumeSeek = progress;
    Log.e("progress", progress + "");
    musicVolume = getMusicVolume(currentVolumeSeek);
    soundVolume = getMusicVolume(VideoEditFragment.MAX_VOLUME_SEEK - currentVolumeSeek);
    Music music = getMusic();
    if (soundFile != null) {
      videoView.setVolume(music);
    }
  }

  private float getMusicVolume(float seek) {
    float musicVolume = seek / 50f;
    musicVolume = musicVolume > 1f ? 1f : musicVolume;
    return musicVolume;
  }

  private void createSoundFile(String musicFilePath) {
    if (!TextUtils.isEmpty(musicFilePath)) {
      if (addTaskListeners != null && !addTaskListeners.isEmpty()) {
        for (int i = addTaskListeners.size() - 1; i >= 0; i--) {
          addTaskListeners.get(i).onProgress();
        }
      }
      ThreadPool.getInstance().execute(() -> {
        try {
          soundFile = null;
          audioFilePath = musicFilePath;
          soundFile = SoundFile.create(musicFilePath, null);
          setMusicData();
        } catch (Exception e) {
          e.printStackTrace();
        }
      });
    }
  }

  private String getRealAudioPath(Uri audioContentUri) {
    String fileName = "";
    if (audioContentUri != null) {
      if (audioContentUri.getScheme().toString().compareTo("content") == 0) {
        Cursor cursor = null;
        try {
          cursor =
              getContext().getContentResolver().query(audioContentUri, null, null, null, null);
          if (cursor != null && cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            fileName = cursor.getString(column_index);
          }
        } finally {
          if (cursor != null && !cursor.isClosed()) {
            cursor.close();
          }
        }
      } else if (audioContentUri.getScheme().compareTo("file") == 0) {
        fileName = audioContentUri.toString().replace("file://", "");
      } else {
        fileName = audioContentUri.toString();
      }
    }
    return fileName;
  }

  private void createSoundFileFromAssets(String musicFileName) {
    if (!TextUtils.isEmpty(musicFileName)) {
      if (addTaskListeners != null && !addTaskListeners.isEmpty()) {
        for (int i = addTaskListeners.size() - 1; i >= 0; i--) {
          addTaskListeners.get(i).onProgress();
        }
      }
      ThreadPool.getInstance().execute(() -> {
        try {
          soundFile = null;
          String musicFilePath =
              FileUtils.copyAssetsFileToData(fragment.getActivity(), musicFileName);
          audioFilePath = musicFilePath;
          Log.e("musicName", musicFilePath);
          soundFile = SoundFile.create(musicFilePath, null);
          setMusicData();
        } catch (Exception e) {
          e.printStackTrace();
        }
      });
    }
  }


  private void setMusicData() {
    new Handler(Looper.getMainLooper()).post(() -> {
      if (addTaskListeners != null && !addTaskListeners.isEmpty()) {
        for (int i = addTaskListeners.size() - 1; i >= 0; i--) {
          addTaskListeners.get(i).onSuccess();
        }
      }
      waveformView.setVisibility(VISIBLE);
      if (videoFilePath == null || videoFilePath.isEmpty()) {
        videoFilePath =
            VideoContentTaskManager.getInstance().getCurrentContentTask().getVideoPath();
      }
      waveformView.setWaveformData(soundFile, VideoUtils.getMediaDuration(videoFilePath));
      Music music = getMusic();
      music.setStartPositionMs(0);
      videoView.changeMusicFile(music);
      videoView.setOnVideoPlayProgressListener(onVideoPlayProgressListener);
      seekBar.setProgress(50);
      if (!TextUtils.isEmpty(audioFilePath)) {
        VideoContentTaskManager.getInstance().getCurrentContentTask().setMusic(music);
      }
    });
  }

  public Music getMusic() {
    if (soundFile != null) {
      int videoDuration = VideoUtils.getMediaDuration(videoFilePath);
      int musicDuration =
          soundFile.getDurationMs() >= videoDuration ? videoDuration : soundFile
              .getDurationMs() - waveformView.getStartPosition();
      Music music =
          new Music(audioFilePath, waveformView.getStartPosition(), musicDuration, musicVolume,
              soundVolume, soundFile);
      Log.e("soundVolume", soundVolume + "");
      Log.e("musicVolume", musicVolume + "");
      return music;
    }
    return null;
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    videoView.resume();
  }

  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    videoView.releaseVideo();
  }

  public void addTaskListener(AddTaskListener listener) {
    if (addTaskListeners == null) {
      addTaskListeners = new ArrayList<>();
    }
    if (listener != null) {
      addTaskListeners.add(listener);
    }
  }

  public void removeTaskListener(AddTaskListener listener) {
    addTaskListeners.remove(listener);
  }

}
