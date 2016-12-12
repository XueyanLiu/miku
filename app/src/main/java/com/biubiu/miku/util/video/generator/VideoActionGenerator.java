package com.biubiu.miku.util.video.generator;

import com.biubiu.miku.model.Post;
import com.biubiu.miku.model.PostVideo;
import com.biubiu.miku.util.video.task.VideoContentTask;

public abstract class VideoActionGenerator {
  public VideoProcessCallback videoProcessCallback;
  public PostVideo postVideo;

  public VideoActionGenerator(VideoProcessCallback videoProcessCallback, PostVideo postVideo) {
    this.videoProcessCallback = videoProcessCallback;
    this.postVideo = postVideo;
  }

  public abstract void generate(VideoContentTask videoContentTask,
                                Post post, int actionLocationTaskPosition);

  protected void nextGenerate(VideoContentTask videoContentTask,
      Post post, int nextActionLocationTaskPosition) {
    if (postVideo != null) {
      postVideo.setCropVideoPath(videoContentTask.getProcessVideoFilePath());
      postVideo.setActionLocationTaskPosition(nextActionLocationTaskPosition);
      // TODO
//      DatabaseManager.getInstance().updatePostVideo(postVideo);
    }
//    double percent = UserFeedsFragment.CROP_PERCENT
//        + nextActionLocationTaskPosition * UserFeedsFragment.ACTION_PERCENT
//            / videoContentTask.getRecordActionLocationTasks().size();
//    EventBus.getDefault().post(new PostProgressCallbackEvent(post, percent));
    VideoProcessControler.processTask(videoContentTask, videoProcessCallback, postVideo, post,
        nextActionLocationTaskPosition);
  }
}
