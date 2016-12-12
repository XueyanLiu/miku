package com.biubiu.miku.module.videoedit;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.biubiu.miku.R;
import com.biubiu.miku.event.NewSubtitleEvent;
import com.biubiu.miku.util.SystemUtils;
import com.biubiu.miku.util.video.ChangeElementState;
import com.biubiu.miku.util.video.action.ActionType;
import com.biubiu.miku.util.video.action.subtitle.SubtitleContent;
import com.biubiu.miku.util.video.action.subtitle.SubtitleManager;
import com.biubiu.miku.util.video.action.subtitle.SubtitleType;
import com.facebook.drawee.view.SimpleDraweeView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class SubtitleAdapter extends RecyclerView.Adapter {
  private final static int COLUMN = 3;
  private final static int TITLE_TYPE = 0;
  private final static int SUBTITLE_ITEM = 1;
  private Context context;
  private final List<List<SubtitleType>> subtitleTypeList;
  private final List<String> titleList;
  private SubtitleManager subtitleManager = SubtitleManager.getInstance();

  public SubtitleAdapter(Context context, List<String> titleList,
                         List<List<SubtitleType>> subtitleTypeList) {
    this.context = context;
    this.titleList = titleList;
    this.subtitleTypeList = subtitleTypeList;
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    switch (viewType) {
      case TITLE_TYPE:
        View titleView = LayoutInflater.from(context).inflate(R.layout.addtext_title, null, false);
        return new RecyclerView.ViewHolder(titleView) {};
      case SUBTITLE_ITEM:
        View subtitleView =
            LayoutInflater.from(context).inflate(R.layout.subtitle_action_item_layout, null, false);
        return new RecyclerView.ViewHolder(subtitleView) {};
    }
    return null;
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    switch (holder.getItemViewType()) {
      case TITLE_TYPE:
        initTitleView(holder.itemView, position);
        break;
      case SUBTITLE_ITEM:
        initSubtitleView(holder.itemView, position);
        break;
      default:
        break;
    }
  }

  @Override
  public int getItemCount() {
    int subtitleLines = 0;
    for (List<SubtitleType> subtitleTypes : subtitleTypeList) {
      subtitleLines += (int) Math.ceil((double) subtitleTypes.size() / COLUMN);
    }
    return titleList.size() + subtitleLines;
  }

  @Override
  public int getItemViewType(int position) {
    int currentPos = 0;
    for (List<SubtitleType> subtitleTypes : subtitleTypeList) {
      int subtitleLines = (int) Math.ceil((double) subtitleTypes.size() / COLUMN);
      if (position == currentPos) {
        return TITLE_TYPE;
      } else if (position > currentPos && position <= currentPos + subtitleLines) {
        return SUBTITLE_ITEM;
      }
      currentPos += (subtitleLines + 1);
    }
    return SUBTITLE_ITEM;
  }

  private void initTitleView(View view, int position) {
    TextView title = (TextView) view.findViewById(R.id.title);
    int titlePos = 0;
    int titleRealPos = 0;
    for (List<SubtitleType> subtitleTypes : subtitleTypeList) {
      if (position == titlePos) {
        title.setText(titleList.get(titleRealPos));
        return;
      }
      ++titleRealPos;
      titlePos += (1 + (int) Math.ceil((double) subtitleTypes.size() / COLUMN));
    }
  }

  private void initSubtitleView(View view, int position) {
    int currentPos = 0;
    for (List<SubtitleType> subtitleTypes : subtitleTypeList) {
      int subtitleLines = (int) Math.ceil((double) subtitleTypes.size() / COLUMN);
      if (position > currentPos && position <= currentPos + subtitleLines) {
        View leftView = view.findViewById(R.id.left_layout);
        View centerView = view.findViewById(R.id.center_layout);
        View rightView = view.findViewById(R.id.right_layout);
        adjustVarietyItemView(leftView, centerView, rightView);
        int runManAttributesPos = (position - currentPos - 1) * 3;
        SimpleDraweeView leftImageView = (SimpleDraweeView) view.findViewById(R.id.left_image);
        SimpleDraweeView centerImageView = (SimpleDraweeView) view.findViewById(R.id.center_image);
        SimpleDraweeView rightImageView = (SimpleDraweeView) view.findViewById(R.id.right_image);
        View leftSelectedView = view.findViewById(R.id.left_selected_icon);
        View centerSelectedView = view.findViewById(R.id.center_selected_icon);
        View rightSelectedView = view.findViewById(R.id.right_selected_icon);
        setImageViewResource(subtitleTypes, leftImageView, leftSelectedView, runManAttributesPos);
        setImageViewResource(subtitleTypes, centerImageView, centerSelectedView,
            runManAttributesPos + 1);
        setImageViewResource(subtitleTypes, rightImageView, rightSelectedView,
            runManAttributesPos + 2);
        addListener(leftImageView, subtitleTypes, runManAttributesPos);
        addListener(centerImageView, subtitleTypes, runManAttributesPos + 1);
        addListener(rightImageView, subtitleTypes, runManAttributesPos + 2);
      }
      currentPos += (subtitleLines + 1);
    }

  }

  private void addListener(View view, List<SubtitleType> subtitleTypeList, int position) {
    view.setOnClickListener(v -> {
      if (position < subtitleTypeList.size() && subtitleTypeList.get(position) != null) {
        ChangeElementState.setActionType(ActionType.SUBTITLE);
        ChangeElementState.setObject(subtitleTypeList.get(position));
        EventBus.getDefault().post(new NewSubtitleEvent(
            new SubtitleContent.Builder().setSubtitleType(subtitleTypeList.get(position)).build()));
        notifyDataSetChanged();
      }
    });
  }

  private void setImageViewResource(List<SubtitleType> subtitleTypes,
                                    SimpleDraweeView imageView, View selectView, int pos) {
    if (pos < subtitleTypes.size() && subtitleTypes.get(pos) != null) {
      if (ChangeElementState.getActionType() != null
          && ChangeElementState.getActionType() == ActionType.SUBTITLE &&
          ChangeElementState.getObject() == subtitleTypes.get(pos)) {
        selectView.setVisibility(View.VISIBLE);
      } else {
        selectView.setVisibility(View.GONE);
      }
      int resId = subtitleManager.getImageResId(subtitleTypes.get(pos));
      imageView.setImageURI(Uri.parse("res:/" + resId));
      imageView.setVisibility(View.VISIBLE);
    } else {
      imageView.setVisibility(View.GONE);
    }
  }

  private void adjustVarietyItemView(View leftView, View centerView, View rightView) {
    int margin = SystemUtils.dpToPx(15);
    int marginParent = SystemUtils.dpToPx(15);
    int height = SystemUtils.dpToPx(109);
    int width = (SystemUtils.getScreenWidthPx() - 2 * margin - 2 * marginParent) / 3;

    resetImageViewParams(leftView, margin, height, width);
    resetImageViewParams(centerView, margin, height, width);
    resetImageViewParams(rightView, margin, height, width);
  }

  private void resetImageViewParams(View view, int margin, int height, int width) {
    RelativeLayout.LayoutParams layoutParams1 =
        (RelativeLayout.LayoutParams) view.getLayoutParams();
    layoutParams1.height = height;
    layoutParams1.width = width;
    layoutParams1.leftMargin = margin;
    view.setLayoutParams(layoutParams1);
  }

}
