package com.biubiu.miku.module.videoedit;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.biubiu.miku.R;
import com.biubiu.miku.event.SelectStickerEvent;
import com.biubiu.miku.util.video.ChangeElementState;
import com.biubiu.miku.util.video.action.ActionType;
import com.biubiu.miku.util.video.action.sticker.StickerImageData;
import com.biubiu.miku.util.video.action.sticker.StickerImageGroup;
import com.biubiu.miku.widget.customview.MeasureGridView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class StickerGroupAdapter extends RecyclerView.Adapter<StickerGroupAdapter.ViewHolder> {

  private Context mContext;
  private List<StickerImageGroup> stickerImageGroups;
  private OnEditItemClickListener onEditItemClickListener;

  private String selectTitle;

  public StickerGroupAdapter(Context context, List<StickerImageGroup> list,
                             OnEditItemClickListener onItemClickListener) {
    this.mContext = context;
    this.stickerImageGroups = list;
    this.onEditItemClickListener = onItemClickListener;
  }

  @Override
  public int getItemCount() {
    if (stickerImageGroups == null || stickerImageGroups.isEmpty()) {
      return 0;
    }
    return stickerImageGroups.size();
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {

    // Logger.d1(tag, "onBindViewHolder:" + position);

    StickerImageGroup group = stickerImageGroups.get(position);
    holder.textView.setText(group.getGroupName());
    holder.stickerGv.setAdapter(new StickerGvAdapter(mContext, group.getStickerImageDataList()));
    // holder
    holder.stickerGv.setOnItemClickListener((parent, view, position1, id) -> {
      StickerImageData sticker = (StickerImageData) parent.getItemAtPosition(position1);
      ChangeElementState.setObject(sticker);
      ChangeElementState.setActionType(ActionType.STICKER);
      EventBus.getDefault().post(new SelectStickerEvent(sticker, group));
      notifyDataSetChanged();
    });
  }

  @Override
  public void onViewRecycled(ViewHolder holder) {
    super.onViewRecycled(holder);
  }

  public StickerImageGroup getItem(int position) {
    if (stickerImageGroups == null || stickerImageGroups.isEmpty()) {
      return null;
    }
    return stickerImageGroups.get(position);
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int position) {

    // Logger.d1(tag, "onCreateViewHolder:" + position);

    final View view =
        View.inflate(viewGroup.getContext(), R.layout.layout_sticker_group_item, null);
    ViewHolder holder = new ViewHolder(view, onEditItemClickListener);
    return holder;
  }

  //
  class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

    TextView textView;
    MeasureGridView stickerGv;
    private OnEditItemClickListener onEditItemClickListener;

    // private Object videoEffectsEntity;

    public ViewHolder(View itemView, OnEditItemClickListener onEditItemClickListener) {
      super(itemView);
      // imageView = (ImageView) itemView.findViewById(R.id.imageview);
      textView = (TextView) itemView.findViewById(R.id.textview_group_title);
      stickerGv = (MeasureGridView) itemView.findViewById(R.id.gv_sticker);
      // selectImage = (ImageView) itemView.findViewById(R.id.imageview_select);
      // progressBar = (ProgressBar) itemView.findViewById(R.id.pb_progressbar);
      this.onEditItemClickListener = onEditItemClickListener;
      // this.videoEffectsEntity = videoEffectsEntity;
      itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
      if (onEditItemClickListener != null) {
        onEditItemClickListener.onItemClick(v, getPosition(), getItem(getPosition()));
        notifyDataSetChanged();
      }
    }


  }

  public interface OnEditItemClickListener {
    void onItemClick(View view, int Position, Object object);
  }
}
