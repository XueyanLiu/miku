package com.biubiu.miku.widget.customview;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.biubiu.miku.R;
import com.biubiu.miku.util.ThreadPool;
import com.biubiu.miku.util.video.action.sticker.StickerImageData;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class StickerImageView extends ImageView {
  private AnimationDrawable anim;
  private StickerImageData stickerImageData;
  private OnFramePlaySeekChangeListener onFramePlaySeekChangeListener;
  private long startPlayTimeMs = 0;
  private Timer timer;
  private TimerTask timerTask;
  private int playSeek = -1;

  public StickerImageView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public StickerImageView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public StickerImageView(Context context) {
    super(context);
  }

  public void setStickerImageData(StickerImageData stickerImageData) {
    setStickerImageData(stickerImageData, true);
  }

  public void setStickerImageData(StickerImageData stickerImageData, boolean isScale) {
    // TODO:YANBINGWU 使用fresco获取bitmap达到复用与控制内存
    this.stickerImageData = stickerImageData;
    int playIntervalMs = stickerImageData.getPlayIntervalMs();
    stopFrame();
    anim = new AnimationDrawable();
    switch (stickerImageData.getSourceType()) {
      case ASSETS:
        AssetManager assetManager = getContext().getAssets();
        int childWidth = getResources().getDimensionPixelSize(R.dimen.action_child_width);
        int childHeight = getResources().getDimensionPixelSize(R.dimen.action_child_height);
        ThreadPool.getInstance().execute(() -> {
          List<String> stickerAssetList = stickerImageData.getStickerImagePathList();
          for (int i = 0; i < stickerAssetList.size(); i++) {
            String stickerChildPath = stickerAssetList.get(i);
            try {
              BitmapFactory.Options options = new BitmapFactory.Options();
              if (isScale) {
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(assetManager.open(stickerChildPath), null, options);
                if (options.outWidth / childWidth > options.outHeight / childHeight) {
                  options.inSampleSize = (int) Math.rint(options.outWidth / childWidth) * 2;
                } else {
                  options.inSampleSize = (int) Math.rint(options.outHeight / childHeight) * 2;
                }
                options.inJustDecodeBounds = false;
              }
//              Bitmap bitmap =
//                  BitmapFactory.decodeStream(assetManager.open(stickerChildPath), null, options);
              Bitmap bitmap =
                  BitmapFactory.decodeStream(new FileInputStream(stickerChildPath), null, options);
              anim.addFrame(new BitmapDrawable(getResources(), bitmap), playIntervalMs);
            } catch (IOException e) {
              Log.e("YTAG", Log.getStackTraceString(e));
            }
          }
          new Handler(Looper.getMainLooper()).post(() -> {
            anim.setOneShot(false);
            startPlayTimeMs = System.currentTimeMillis();
            setImageDrawable(anim);
            anim.start();
            startTimer();
          });
        });
        break;
      case FILE:
        break;
    }
  }

  public void stopFrame() {
    if (anim != null && anim.isRunning()) {
      anim.stop();
    }
    stopTimer();
  }

  private void startTimer() {
    stopTimer();
    Log.e("frame play seek",
        "onFramePlaySeekChangeListener:" + (onFramePlaySeekChangeListener == null));
    timer = new Timer();
    timerTask = new TimerTask() {
      @Override
      public void run() {
        playSeek++;
        if (StickerImageView.this.onFramePlaySeekChangeListener != null) {
          StickerImageView.this.onFramePlaySeekChangeListener.onFramePlaySeekChange(playSeek
              % stickerImageData.getFrameSize());
        }
      }
    };
    timer.scheduleAtFixedRate(timerTask, 0, stickerImageData.getPlayIntervalMs());
  }

  private void stopTimer() {
    if (timer != null) {
      timer.cancel();
      timer = null;
      timerTask.cancel();
      timerTask = null;
    }

  }

  public int getCurrentPlayPosition() {
    return (int) (System.currentTimeMillis() - startPlayTimeMs)
        / stickerImageData.getPlayIntervalMs() % stickerImageData.getFrameSize();
  }

  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    stopFrame();
  }

  public void setOnFramePlaySeekChangeListener(
      OnFramePlaySeekChangeListener onFramePlaySeekChangeListener) {
    this.onFramePlaySeekChangeListener = onFramePlaySeekChangeListener;
  }

  interface OnFramePlaySeekChangeListener {
    void onFramePlaySeekChange(int seek);
  }
}
