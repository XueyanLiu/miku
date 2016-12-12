package com.biubiu.miku.module.videoedit;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.biubiu.miku.R;
import com.biubiu.miku.constant.EditState;
import com.biubiu.miku.event.ChangeElementEvent;
import com.biubiu.miku.util.video.ChangeElementState;
import com.biubiu.miku.util.video.action.ActionType;
import com.biubiu.miku.util.video.action.chatBox.ChatBoxChild;
import com.biubiu.miku.util.video.action.runMan.RunManAttribute;
import com.biubiu.miku.util.video.action.sticker.StickerImageData;
import com.biubiu.miku.util.video.action.subtitle.SubtitleManager;
import com.biubiu.miku.util.video.action.subtitle.SubtitleType;
import com.biubiu.miku.util.video.action.videoTag.VideoTagContent;
import com.facebook.drawee.view.SimpleDraweeView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class ElementsGroupAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
  private Context context;
  private ActionType actionType;
  private List<Object> elementList;

  public ElementsGroupAdapter(Context context) {
    this.context = context;
  }

  public void setElementList(ActionType actionType, List<Object> elementList) {
    this.actionType = actionType;
    this.elementList = elementList;
    notifyDataSetChanged();
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(context).inflate(R.layout.element_group_item, null, false);
    return new RecyclerView.ViewHolder(view) {
    };
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    View view = holder.itemView;
    SimpleDraweeView simpleDraweeView = (SimpleDraweeView) view.findViewById(R.id.item_view);
    View selectedView = view.findViewById(R.id.selected_icon);
    Object object = elementList.get(position);
    switch (actionType) {
      case RUN_MAN: {
        RunManAttribute runManAttribute = (RunManAttribute) object;
        int resId = runManAttribute.getRunManPreviewAttribute().getImageResId();
        simpleDraweeView.setImageURI(Uri.parse("res:/" + resId));
        if (ChangeElementState.getActionType() != null
            && ChangeElementState.getActionType() == ActionType.RUN_MAN &&
            ((RunManAttribute) (ChangeElementState.getObject())).getRunManPreviewAttribute()
                .getImageResId() == runManAttribute.getRunManPreviewAttribute().getImageResId()) {
          selectedView.setVisibility(View.VISIBLE);
        } else {
          selectedView.setVisibility(View.GONE);
        }
        break;
      }
      case CHAT_BOX: {
        ChatBoxChild chatBoxChild = (ChatBoxChild) object;
        int resId = chatBoxChild.getIconImageResId();
        simpleDraweeView.setImageURI(Uri.parse("res:/" + resId));
        if (ChangeElementState.getActionType() != null
            && ChangeElementState.getActionType() == ActionType.CHAT_BOX &&
            ((ChatBoxChild) ChangeElementState.getObject())
                .getIconImageResId() == chatBoxChild.getIconImageResId()) {
          selectedView.setVisibility(View.VISIBLE);
        } else {
          selectedView.setVisibility(View.GONE);
        }
        break;
      }
      case STICKER: {
        StickerImageData sticker = (StickerImageData) object;
        if (ChangeElementState.getActionType() != null
            && ChangeElementState.getActionType() == ActionType.STICKER &&
            ((StickerImageData) (ChangeElementState.getObject())).getPreviewImagePath()
                .equals(sticker.getPreviewImagePath())) {
          selectedView.setVisibility(View.VISIBLE);
        } else {
          selectedView.setVisibility(View.GONE);
        }
        simpleDraweeView.setImageURI(Uri.parse("file://" + sticker.getPreviewImagePath()));
        break;
      }
      case SUBTITLE: {
        SubtitleType subtitleType = (SubtitleType) object;
        int resId = SubtitleManager.getImageResId(subtitleType);
        simpleDraweeView.setImageURI(Uri.parse("res:/" + resId));
        if (ChangeElementState.getActionType() != null
            && ChangeElementState.getActionType() == ActionType.SUBTITLE &&
            ChangeElementState.getObject() == subtitleType) {
          selectedView.setVisibility(View.VISIBLE);
        } else {
          selectedView.setVisibility(View.GONE);
        }
        break;
      }
      case VIDEO_TAG: {
        VideoTagContent videoTagContent = (VideoTagContent) object;
        int resId = videoTagContent.getResId();
        simpleDraweeView.setImageURI(Uri.parse("res:/" + resId));
        if (ChangeElementState.getActionType() != null
            && ChangeElementState.getActionType() == ActionType.VIDEO_TAG &&
            ((VideoTagContent) (ChangeElementState.getObject())).getResId() == videoTagContent
                .getResId()) {
          selectedView.setVisibility(View.VISIBLE);
        } else {
          selectedView.setVisibility(View.GONE);
        }
        break;
      }
      default:
        break;
    }
    simpleDraweeView.setOnClickListener(v -> {
      if (ChangeElementState.getEditState() != null
          && ChangeElementState.getEditState() != EditState.ADDTEXT_INPUT) {
        ChangeElementState.setActionType(actionType);
        ChangeElementState.setObject(object);
        notifyDataSetChanged();
      }
      EventBus.getDefault().post(new ChangeElementEvent(actionType, object));
    });
  }

  @Override
  public int getItemCount() {
    return elementList == null ? 0 : elementList.size();
  }

}
