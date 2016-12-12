package com.biubiu.miku.util.sound;

import android.app.Fragment;
import android.content.Intent;

public class SoundUtils {

  public static final int CHOOSE_MUSIC_TAG = 10003;

  public static void getMusic(Fragment fragment) {
    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
    intent.setType("audio/*");
    intent.putExtra("return-data", true);
    fragment.startActivityForResult(intent, CHOOSE_MUSIC_TAG);
  }
}
