package com.biubiu.miku.util.sound;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Looper;

import com.biubiu.miku.MikuApplication;

import java.util.HashMap;
import java.util.Map;

/**
 * SoundPool音频播放工具类
 */
public class SoundPoolPlayer {
  private static final int MAX_STREAMS = 5;
  private static final int SRC_QUALITY = 50;
  private static final long RELEASE_RESOURCES_INTERVAL_MS = 3 * 60 * 1000L;
  private static SoundPoolPlayer soundPoolPlayer;
  private SoundPool soundPool;
  private Map<Integer, Integer> soundResIdMap = new HashMap<>();

  public static synchronized SoundPoolPlayer getInstance() {
    if (soundPoolPlayer == null) {
      soundPoolPlayer = new SoundPoolPlayer();
    }
    return soundPoolPlayer;
  }

  private SoundPoolPlayer() {
    soundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, SRC_QUALITY);
    final Handler handler = new Handler(Looper.getMainLooper());
    handler.postDelayed(new Runnable() {
      @Override
      public void run() {
        if (soundPool != null) {
          for (Integer soundId : soundResIdMap.values()) {
            soundPool.unload(soundId);
          }
          soundResIdMap.clear();
        }
        handler.postDelayed(this, RELEASE_RESOURCES_INTERVAL_MS);
      }
    }, RELEASE_RESOURCES_INTERVAL_MS);
  }

  public void playSound(final int soundResId) {
    // AudioManager audioManager = (AudioManager) GaiaApplication.getAppContext()
    // .getSystemService(Context.AUDIO_SERVICE);
    // // 获取系统设置的铃声模式
    // switch (audioManager.getRingerMode()) {
    // // 静音模式，值为0，这时候不震动，不响铃
    // case AudioManager.RINGER_MODE_SILENT:
    // // 震动模式，值为1，这时候震动，不响铃
    // case AudioManager.RINGER_MODE_VIBRATE:
    // return;
    // }
    if (soundResIdMap.containsKey(soundResId)) {
      play(soundPool, soundResIdMap.get(soundResId));
    } else {
      soundPool.setOnLoadCompleteListener((soundPool1, sampleId, status) -> {
        soundResIdMap.put(soundResId, sampleId);
        play(soundPool1, sampleId);
      });
      soundPool.load(MikuApplication.context, soundResId, 1);
    }
  }

  private static void play(SoundPool soundPool, int soundId) {
    AudioManager audioManager = (AudioManager) MikuApplication.context
        .getSystemService(Context.AUDIO_SERVICE);
    float actualVolume = (float) audioManager
        .getStreamVolume(AudioManager.STREAM_MUSIC);
    float maxVolume = (float) audioManager
        .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    final float audioCurrentVolume = actualVolume / maxVolume;
    soundPool.play(soundId, audioCurrentVolume, audioCurrentVolume, 0, 0, 1);
  }

  public void stop(int soundResId) {
    if (soundResIdMap.containsKey(soundResId)) {
      soundPool.stop(soundResIdMap.get(soundResId));
    }
  }
}
