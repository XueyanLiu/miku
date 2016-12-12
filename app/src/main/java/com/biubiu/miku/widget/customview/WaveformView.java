package com.biubiu.miku.widget.customview;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

import com.biubiu.miku.R;
import com.biubiu.miku.util.SystemUtils;
import com.biubiu.miku.util.sound.SoundFile;

import java.util.ArrayList;
import java.util.List;

public class WaveformView extends LinearLayout {
  private RecyclerView recyclerView;
  private SoundFile soundFile;
  private double[][] mValuesByZoomLevel;
  private double[] mZoomFactorByZoomLevel;
  private double[] mHeightsAtThisZoomLevel;
  private int mZoomLevel = 3;
  private int[] mLenByZoomLevel;
  private int screenMaxWaveSize;
  private int allWaveSize;
  private int visualizerCylinderMaxHeight;
  private int visualizerCylinderMinHeight;
  private int visualizerCylinderWidth;
  private int visualizerCylinderInterval;
  private List<Integer> waveHeightList;
  private WaveAdapter waveAdapter;
  private OnWaveStartPositionChangeListener onWaveStartPositionChangeListener;
  private int startPositionMs;
  private int oldFirstVisiblePosition;
  private boolean isWaveProgressing = true;
  private int currentPosition = -1;
  private boolean isScrolling = false;
  private Handler handler = new Handler();

  public WaveformView(Context context) {
    super(context);
    initViewData();
  }

  public WaveformView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initViewData();
  }

  public WaveformView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initViewData();
  }

  private void initViewData() {
    visualizerCylinderMaxHeight = getResources().getDimensionPixelSize(
        R.dimen.music_visualizer_cylinder_max_height);
    visualizerCylinderMinHeight = getResources().getDimensionPixelSize(
        R.dimen.music_visualizer_cylinder_min_height);
    visualizerCylinderWidth = getResources().getDimensionPixelSize(
        R.dimen.music_visualizer_cylinder_width);
    visualizerCylinderInterval = getResources().getDimensionPixelSize(
        R.dimen.music_visualizer_cylinder_interval);
    int screenWidth = SystemUtils.getScreenWidthPx();
    screenMaxWaveSize = screenWidth / (visualizerCylinderWidth + visualizerCylinderInterval);
    recyclerView = new RecyclerView(getContext());
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
    linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
    recyclerView.setItemAnimator(new DefaultItemAnimator());
    recyclerView.setLayoutManager(linearLayoutManager);
    waveHeightList = new ArrayList<>();
    waveAdapter = new WaveAdapter(getContext(), waveHeightList, screenMaxWaveSize);
    recyclerView.setAdapter(waveAdapter);
    addView(recyclerView);
    addListener();
  }

  private void addListener() {
    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
          isWaveProgressing = true;
          LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
          int currentFirstVisiblePosition =
              recyclerView.getChildLayoutPosition(layoutManager.getChildAt(0));
          if (currentFirstVisiblePosition != oldFirstVisiblePosition) {
            oldFirstVisiblePosition = currentFirstVisiblePosition;
            recyclerView.scrollToPosition(currentFirstVisiblePosition);
            startPositionMs = (int) (currentFirstVisiblePosition / (float) waveHeightList.size()
                * soundFile.getDurationMs());
            Log.e("currentPositionMs", "currentPositionMs:" + startPositionMs);
            if (onWaveStartPositionChangeListener != null) {
              onWaveStartPositionChangeListener.onWaveStartPositionChange(startPositionMs);
            }
          }
        } else {
          if (isWaveProgressing) {
            waveAdapter.notifyDataSetChanged();
            isWaveProgressing = false;
          }
          isScrolling = true;
        }
      }
    });
  }

  public void setWaveformData(SoundFile soundFile, int videoDurationMs) {
    waveHeightList.clear();
    this.soundFile = soundFile;
    computeDoublesForAllZoomLevels();
    computeIntsForThisZoomLevel();
    allWaveSize =
        (int) (soundFile.getDurationMs() / (float) videoDurationMs * screenMaxWaveSize);
    allWaveSize =
        allWaveSize > mHeightsAtThisZoomLevel.length ? mHeightsAtThisZoomLevel.length : allWaveSize;
    for (int i = 0; i < allWaveSize; i++) {
      int heightsPosition =
          mHeightsAtThisZoomLevel.length / allWaveSize * i > mHeightsAtThisZoomLevel.length - 1
              ? mHeightsAtThisZoomLevel.length - 1
              : mHeightsAtThisZoomLevel.length / allWaveSize * i;
      int height = (int) (mHeightsAtThisZoomLevel[heightsPosition] * visualizerCylinderMaxHeight);
      height =
          height == 0
              ? visualizerCylinderMinHeight
              : height > visualizerCylinderMaxHeight
                  ? visualizerCylinderMaxHeight + visualizerCylinderMinHeight
                  : height + visualizerCylinderMinHeight;
      waveHeightList.add(height);
    }
    recyclerView.smoothScrollToPosition(0);
    waveAdapter.notifyDataSetChanged();
  }

  public void setWaveformViewProgress(int progress) {
    int soundDuration = soundFile.getDurationMs();
    int waveItemViewCount = allWaveSize;
    int waveItemViewDuration = (int) ((float) (soundDuration) / waveItemViewCount);
    int currentItemViewPosition = progress / waveItemViewDuration;
    if (currentItemViewPosition != currentPosition) {
      currentPosition = currentItemViewPosition;
      if (isWaveProgressing) {
        if (currentItemViewPosition == 0) {
          waveAdapter.notifyDataSetChanged();
        }
        if (recyclerView != null && recyclerView.getChildAt(currentItemViewPosition) != null) {
          if (isScrolling) {
            handler.post(() -> {
              for (int i = 0; i <= currentItemViewPosition; i++) {
                recyclerView.getChildAt(i).setSelected(true);
              }
            });
            isScrolling = false;
          } else {
            handler.post(() -> recyclerView.getChildAt(currentItemViewPosition).setSelected(true));
          }
        }
      }
    }
  }

  /**
   * Called once when a new sound file is added
   */
  private void computeDoublesForAllZoomLevels() {
    int numFrames = soundFile.getNumFrames();
    int[] frameGains = soundFile.getFrameGains();
    double[] smoothedGains = new double[numFrames];
    if (numFrames == 1) {
      smoothedGains[0] = frameGains[0];
    } else if (numFrames == 2) {
      smoothedGains[0] = frameGains[0];
      smoothedGains[1] = frameGains[1];
    } else if (numFrames > 2) {
      smoothedGains[0] = (double) ((frameGains[0] / 2.0) +
          (frameGains[1] / 2.0));
      for (int i = 1; i < numFrames - 1; i++) {
        smoothedGains[i] = (double) ((frameGains[i - 1] / 3.0) +
            (frameGains[i] / 3.0) +
            (frameGains[i + 1] / 3.0));
      }
      smoothedGains[numFrames - 1] = (double) ((frameGains[numFrames - 2] / 2.0) +
          (frameGains[numFrames - 1] / 2.0));
    }

    // Make sure the range is no more than 0 - 255
    double maxGain = 1.0;
    for (int i = 0; i < numFrames; i++) {
      if (smoothedGains[i] > maxGain) {
        maxGain = smoothedGains[i];
      }
    }
    double scaleFactor = 1.0;
    if (maxGain > 255.0) {
      scaleFactor = 255 / maxGain;
    }

    // Build histogram of 256 bins and figure out the new scaled max
    maxGain = 0;
    int gainHist[] = new int[256];
    for (int i = 0; i < numFrames; i++) {
      int smoothedGain = (int) (smoothedGains[i] * scaleFactor);
      if (smoothedGain < 0)
        smoothedGain = 0;
      if (smoothedGain > 255)
        smoothedGain = 255;

      if (smoothedGain > maxGain)
        maxGain = smoothedGain;

      gainHist[smoothedGain]++;
    }

    // Re-calibrate the min to be 5%
    double minGain = 0;
    int sum = 0;
    while (minGain < 255 && sum < numFrames / 20) {
      sum += gainHist[(int) minGain];
      minGain++;
    }

    // Re-calibrate the max to be 99%
    sum = 0;
    while (maxGain > 2 && sum < numFrames / 100) {
      sum += gainHist[(int) maxGain];
      maxGain--;
    }

    // Compute the heights
    double[] heights = new double[numFrames];
    double range = maxGain - minGain;
    for (int i = 0; i < numFrames; i++) {
      double value = (smoothedGains[i] * scaleFactor - minGain) / range;
      if (value < 0.0)
        value = 0.0;
      if (value > 1.0)
        value = 1.0;
      heights[i] = value * value;
    }

    mLenByZoomLevel = new int[5];
    mZoomFactorByZoomLevel = new double[5];
    mValuesByZoomLevel = new double[5][];

    // Level 0 is doubled, with interpolated values
    mLenByZoomLevel[0] = numFrames * 2;
    mZoomFactorByZoomLevel[0] = 2.0;
    mValuesByZoomLevel[0] = new double[mLenByZoomLevel[0]];
    if (numFrames > 0) {
      mValuesByZoomLevel[0][0] = 0.5 * heights[0];
      mValuesByZoomLevel[0][1] = heights[0];
    }
    for (int i = 1; i < numFrames; i++) {
      mValuesByZoomLevel[0][2 * i] = 0.5 * (heights[i - 1] + heights[i]);
      mValuesByZoomLevel[0][2 * i + 1] = heights[i];
    }

    // Level 1 is normal
    mLenByZoomLevel[1] = numFrames;
    mValuesByZoomLevel[1] = new double[mLenByZoomLevel[1]];
    mZoomFactorByZoomLevel[1] = 1.0;
    for (int i = 0; i < mLenByZoomLevel[1]; i++) {
      mValuesByZoomLevel[1][i] = heights[i];
    }

    // 3 more levels are each halved
    for (int j = 2; j < 5; j++) {
      mLenByZoomLevel[j] = mLenByZoomLevel[j - 1] / 2;
      mValuesByZoomLevel[j] = new double[mLenByZoomLevel[j]];
      mZoomFactorByZoomLevel[j] = mZoomFactorByZoomLevel[j - 1] / 2.0;
      for (int i = 0; i < mLenByZoomLevel[j]; i++) {
        mValuesByZoomLevel[j][i] =
            0.5 * (mValuesByZoomLevel[j - 1][2 * i] +
                mValuesByZoomLevel[j - 1][2 * i + 1]);
      }
    }

    if (numFrames > 5000) {
      mZoomLevel = 3;
    } else if (numFrames > 1000) {
      mZoomLevel = 2;
    } else if (numFrames > 300) {
      mZoomLevel = 1;
    } else {
      mZoomLevel = 0;
    }
  }

  /**
   * Called the first time we need to draw when the zoom level has changed
   * or the screen is resized
   */
  private void computeIntsForThisZoomLevel() {
    mHeightsAtThisZoomLevel = new double[mLenByZoomLevel[mZoomLevel]];
    for (int i = 0; i < mLenByZoomLevel[mZoomLevel]; i++) {
      mHeightsAtThisZoomLevel[i] = mValuesByZoomLevel[mZoomLevel][i];
      Log.e("tag", "i:" + mHeightsAtThisZoomLevel[i]);
    }
    Log.e("tag", "height size:" + mHeightsAtThisZoomLevel.length
        + "  frameNums:" + soundFile.getNumFrames());
  }

  public int getStartPosition() {
    return startPositionMs;
  }

  public void setOnWaveStartPositionChangeListener(
      OnWaveStartPositionChangeListener onWaveStartPositionChangeListener) {
    this.onWaveStartPositionChangeListener = onWaveStartPositionChangeListener;
  }

  public interface OnWaveStartPositionChangeListener {
    void onWaveStartPositionChange(int startPositionMs);
  }
}
