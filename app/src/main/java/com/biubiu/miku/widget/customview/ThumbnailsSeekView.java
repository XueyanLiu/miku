package com.biubiu.miku.widget.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.biubiu.miku.R;
import com.biubiu.miku.util.FileUtils;
import com.biubiu.miku.util.SystemUtils;
import com.biubiu.miku.util.ThreadPool;
import com.biubiu.miku.util.video.VideoMetaData;
import com.biubiu.miku.util.video.VideoUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ThumbnailsSeekView extends RelativeLayout {
  @BindView(R.id.thumbnails_content)
  LinearLayout thumbnailsContent;
  @BindView(R.id.edit_thumbnails_seek_bar)
  SeekBar editSeekBar;
  private List<SimpleDraweeView> simpleDraweeViews = new ArrayList<>();
  private Bitmap upBitmap;

  public ThumbnailsSeekView(Context context) {
    super(context);
    initViewData();
  }

  public ThumbnailsSeekView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initViewData();
  }

  public ThumbnailsSeekView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initViewData();
  }

  private void initViewData() {
    LayoutInflater inflater =
        (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.thumbnails_seek_bar, this, true);
    ButterKnife.bind(this);
  }

  public void setVideoPath(String videoPath) {
    VideoMetaData videoMetaData = VideoUtils.getVideoMetaData(videoPath);
    int thumbnailsContentHeight =
        getResources().getDimensionPixelSize(R.dimen.thumbnails_content_height);
    int thumbnailWidth = (int) (videoMetaData.getWidth()
        / (videoMetaData.getHeight() / (float) thumbnailsContentHeight));
    int thumbnailCount = (int) Math.ceil(SystemUtils.getScreenWidthPx() / (float) thumbnailWidth);
    for (int i = 0; i < thumbnailCount; i++) {
      SimpleDraweeView simpleDraweeView = new SimpleDraweeView(getContext());
      LinearLayout.LayoutParams layoutParams =
          new LinearLayout.LayoutParams(thumbnailWidth, thumbnailsContentHeight);
      simpleDraweeView.setLayoutParams(layoutParams);
      thumbnailsContent.addView(simpleDraweeView);
      simpleDraweeViews.add(simpleDraweeView);
    }
    String parentPath = new File(videoPath).getParent();
    String thumbnailDirectory = parentPath.endsWith(File.separator)
        ? parentPath + "thumbnails"
        : parentPath + File.separator + "thumbnails";
    for (int i = 0; i < thumbnailCount; i++) {
      final int pos = i;
      ThreadPool.getInstance().execute(() -> {
        int thumbnailTimeName = pos * 1000;
        long thumbnailTime = videoMetaData.getDuration() / thumbnailCount * thumbnailTimeName;
        File thumbnailFile =
            new File(thumbnailDirectory + File.separator + thumbnailTimeName + ".jpg");
        if (!thumbnailFile.exists()) {
          Bitmap tempBitmap = VideoUtils.getVideoThumbnail(videoPath, thumbnailTime,
              MediaMetadataRetriever.OPTION_CLOSEST);
          tempBitmap = tempBitmap != null
              ? tempBitmap
              : VideoUtils.getVideoThumbnail(videoPath, thumbnailTime,
              MediaMetadataRetriever.OPTION_PREVIOUS_SYNC);
          Bitmap bitmap = tempBitmap != null ? tempBitmap : upBitmap;
          FileUtils.saveBitmapToFile(bitmap, thumbnailDirectory, thumbnailTimeName + ".jpg", 10);
          new Handler(Looper.getMainLooper()).post(() -> simpleDraweeViews
              .get(thumbnailTimeName / 1000).setImageURI(Uri.parse("file://" + thumbnailFile.getAbsolutePath())));
        } else {
          simpleDraweeViews.get(pos)
              .setImageURI(Uri.parse("file://" + thumbnailFile.getAbsolutePath()));
        }
      });
    }
  }

  public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener l) {
    if (editSeekBar != null) {
      editSeekBar.setOnSeekBarChangeListener(l);
    }
  }

  public void setMax(int max) {
    if (editSeekBar != null) {
      editSeekBar.setMax(max);
    }
  }

  public void setProgress(int progress) {
    if (editSeekBar != null) {
      editSeekBar.setProgress(progress);
    }
  }

  public int getMax() {
    if (editSeekBar != null) {
      editSeekBar.getMax();
    }
    return 0;
  }

  public int getProgress() {
    if (editSeekBar != null) {
      editSeekBar.getProgress();
    }
    return 0;
  }
}
