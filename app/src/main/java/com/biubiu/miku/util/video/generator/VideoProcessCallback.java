package com.biubiu.miku.util.video.generator;

public interface VideoProcessCallback {
  void success(long id, String videoPath);

  void failure(String message);
}
