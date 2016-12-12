package com.biubiu.miku.util.video;

public interface ConcatMediaCallback {
  void success(String outputFilePath);

  void failure(Throwable throwable);
}
