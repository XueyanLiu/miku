package com.biubiu.miku.util.video.task;

public class VideoContentTaskManager {
  private static VideoContentTaskManager videoContentTaskManager;

  private VideoContentTaskManager() {}

  public synchronized static VideoContentTaskManager getInstance() {
    if (videoContentTaskManager == null) {
      videoContentTaskManager = new VideoContentTaskManager();
    }
    return videoContentTaskManager;
  }

  private VideoContentTask currentContentTask;

  public VideoContentTask createVideoContentTask() {
    return currentContentTask = new VideoContentTask();
  }

  public VideoContentTask getCurrentContentTask() {
    return currentContentTask;
  }
}
