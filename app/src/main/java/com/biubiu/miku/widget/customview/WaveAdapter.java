package com.biubiu.miku.widget.customview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.biubiu.miku.R;

import java.util.List;

import butterknife.ButterKnife;

public class WaveAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
  private static final int TYPE_ITEM = 0;
  private Context context;
  private List<Integer> waveHeightList;
  private int screenMaxWaveSize;
  private int visualizerCylinderWidth;
  private int visualizerBodyHeight;
  private int visualizerCylinderInterval;

  public WaveAdapter(Context context, List<Integer> waveHeightList, int screenMaxWaveSize) {
    this.context = context;
    this.waveHeightList = waveHeightList;
    this.screenMaxWaveSize = screenMaxWaveSize;
    visualizerCylinderWidth = context.getResources().getDimensionPixelSize(
        R.dimen.music_visualizer_cylinder_width);
    visualizerCylinderInterval = context.getResources().getDimensionPixelSize(
        R.dimen.music_visualizer_cylinder_interval);
    visualizerBodyHeight = context.getResources().getDimensionPixelSize(
        R.dimen.visualizer_body_height);
  }

  @Override
  public int getItemCount() {
    return waveHeightList.size();
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    switch (holder.getItemViewType()) {
      case TYPE_ITEM:
        LinearLayout.LayoutParams layoutParams =
            new LinearLayout.LayoutParams(visualizerCylinderWidth, visualizerBodyHeight);
        if (position == 0) {
          if (waveHeightList.size() > screenMaxWaveSize) {
            layoutParams.leftMargin = (int) (visualizerCylinderInterval * 2.5);
          } else {
            layoutParams.leftMargin = 0;
          }
        } else {
          layoutParams.leftMargin = visualizerCylinderInterval;
        }
        if (position == waveHeightList.size() - 1) {
          layoutParams.rightMargin = (int) (visualizerCylinderInterval * 2.5);
        }
        holder.itemView.setLayoutParams(layoutParams);
        holder.itemView.setSelected(false);
        RelativeLayout.LayoutParams waveViewLayoutParams =
            (RelativeLayout.LayoutParams) ButterKnife.findById(holder.itemView, R.id.wave_view)
                .getLayoutParams();
        waveViewLayoutParams.height = waveHeightList.get(position);
        ButterKnife.findById(holder.itemView, R.id.wave_view).setLayoutParams(waveViewLayoutParams);
        break;
    }
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    switch (viewType) {
      case TYPE_ITEM:
        View view = LayoutInflater.from(context).inflate(R.layout.wave_view_layout, null);
        ButterKnife.findById(view, R.id.wave_view).setBackgroundResource(
            R.drawable.wave_view_bg_selector);
        return new WaveViewHolder(view);
      default:
        return null;
    }
  }

  @Override
  public int getItemViewType(int position) {
    return TYPE_ITEM;
  }

  private static class WaveViewHolder extends RecyclerView.ViewHolder {
    public View view;

    public WaveViewHolder(View view) {
      super(view);
      this.view = view;
    }
  }

}
