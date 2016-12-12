package com.biubiu.miku.util.video.generator;

import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;

public abstract class VideoProcessResponseHandler implements FFmpegExecuteResponseHandler {
  public abstract void onSuccess(String message, String filePath);

  @Override
  public void onSuccess(String message) {

  }

  @Override
  public void onProgress(String message) {

  }

  @Override
  public void onFailure(String message) {

  }

  @Override
  public void onStart() {

  }

  @Override
  public void onFinish() {

  }
}
