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
import com.biubiu.miku.event.SelectRunManEvent;
import com.biubiu.miku.util.SystemUtils;
import com.biubiu.miku.util.video.ChangeElementState;
import com.biubiu.miku.util.video.action.ActionType;
import com.biubiu.miku.util.video.action.runMan.RunManAttribute;
import com.facebook.drawee.view.SimpleDraweeView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class VarietyAdapter extends RecyclerView.Adapter {
  private final static int COLUMN = 3;
  private final static int TITLE_TYPE = 0;
  private final static int VARIETY_TYPE = 1;
  private Context context;
  private final List<List<RunManAttribute>> varietyList;
  private final List<String> titleList;

  public VarietyAdapter(Context context, List<String> titleList,
                        List<RunManAttribute> runManAttributeList,
                        List<RunManAttribute> kangxiAttributeList) {
    this.context = context;
    this.titleList = titleList;
    varietyList = new ArrayList<>();
    varietyList.add(runManAttributeList);
    varietyList.add(kangxiAttributeList);
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    switch (viewType) {
      case TITLE_TYPE:
        View titleView = LayoutInflater.from(context).inflate(R.layout.addtext_title, null, false);
        return new RecyclerView.ViewHolder(titleView) {};
      case VARIETY_TYPE:
        View runManView = LayoutInflater.from(context).inflate(R.layout.variety_item, null, false);
        return new RecyclerView.ViewHolder(runManView) {};
    }
    return null;
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    switch (holder.getItemViewType()) {
      case TITLE_TYPE:
        initTitleView(holder.itemView, position);
        break;
      case VARIETY_TYPE:
        initVarietyView(holder.itemView, position);
        break;
      default:
        break;
    }
  }

  @Override
  public int getItemCount() {
    int varietyLines = 0;
    for (List<RunManAttribute> variety : varietyList) {
      varietyLines += (int) Math.ceil((double) variety.size() / COLUMN);
    }
    return titleList.size() + varietyLines;
  }

  @Override
  public int getItemViewType(int position) {
    int currentPos = 0;
    for (List<RunManAttribute> runManAttributes : varietyList) {
      int runManAttributesLines = (int) Math.ceil((double) runManAttributes.size() / COLUMN);
      if (position == currentPos) {
        return TITLE_TYPE;
      } else if (position > currentPos && position <= currentPos + runManAttributesLines) {
        return VARIETY_TYPE;
      }
      currentPos += (runManAttributesLines + 1);
    }
    return VARIETY_TYPE;
  }

  private void initTitleView(View view, int position) {
    TextView title = (TextView) view.findViewById(R.id.title);
    int titlePos = 0;
    int titleRealPos = 0;
    for (List<RunManAttribute> runManAttributes : varietyList) {
      if (position == titlePos) {
        title.setText(titleList.get(titleRealPos));
        return;
      }
      ++titleRealPos;
      titlePos += (1 + (int) Math.ceil((double) runManAttributes.size() / COLUMN));
    }
  }

  private void initVarietyView(View view, int position) {
    int currentPos = 0;
    for (List<RunManAttribute> runManAttributes : varietyList) {
      int runManAttributesLines = (int) Math.ceil((double) runManAttributes.size() / COLUMN);
      if (position > currentPos && position <= currentPos + runManAttributesLines) {
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
        setImageViewResource(runManAttributes, leftImageView, leftSelectedView,
            runManAttributesPos);
        setImageViewResource(runManAttributes, centerImageView, centerSelectedView,
            runManAttributesPos + 1);
        setImageViewResource(runManAttributes, rightImageView, rightSelectedView,
            runManAttributesPos + 2);
        addListener(leftView, runManAttributes, runManAttributesPos);
        addListener(centerView, runManAttributes, runManAttributesPos + 1);
        addListener(rightView, runManAttributes, runManAttributesPos + 2);
      }
      currentPos += (runManAttributesLines + 1);
    }
  }

  private void addListener(View view, List<RunManAttribute> runManAttributeList, int position) {
    view.setOnClickListener(v -> {
      if (position < runManAttributeList.size() && runManAttributeList.get(position) != null) {
        ChangeElementState.setActionType(ActionType.RUN_MAN);
        ChangeElementState.setObject(runManAttributeList.get(position));
        EventBus.getDefault().post(new SelectRunManEvent(runManAttributeList.get(position)));
        notifyDataSetChanged();
      }
    });
  }

  private void setImageViewResource(List<RunManAttribute> runManAttributes,
                                    SimpleDraweeView imageView, View selectView, int pos) {
    if (pos < runManAttributes.size() && runManAttributes.get(pos) != null) {
      if (ChangeElementState.getActionType() != null
          && ChangeElementState.getActionType() == ActionType.RUN_MAN &&
          ((RunManAttribute) (ChangeElementState.getObject())).getRunManPreviewAttribute()
              .getImageResId() == runManAttributes.get(pos).getRunManPreviewAttribute()
                  .getImageResId()) {
        selectView.setVisibility(View.VISIBLE);
      } else {
        selectView.setVisibility(View.GONE);
      }
      int resId = runManAttributes.get(pos).getRunManPreviewAttribute().getImageResId();
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
