package com.biubiu.miku.module.videoedit;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.biubiu.miku.R;
import com.biubiu.miku.util.SystemUtils;
import com.biubiu.miku.util.video.ChangeElementState;
import com.biubiu.miku.util.video.action.ActionType;
import com.biubiu.miku.util.video.action.sticker.StickerImageData;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * Created by linmu on 15/11/5.
 */
public class StickerGvAdapter extends MyBaseAdapter<StickerImageData> {
  public StickerGvAdapter(Context ctx, List<StickerImageData> list) {
    super(ctx, list);
  }

  @Override
  public View getView(int pos, View convertView, ViewGroup group) {
    ViewHolder holder;
    if (convertView == null) {
      convertView = View.inflate(context, R.layout.view_sticker_item, null);
      holder = new ViewHolder();
      holder.stickerImage = (SimpleDraweeView) convertView.findViewById(R.id.imageview_sticker);
      holder.selectImage = (ImageView) convertView.findViewById(R.id.imageview_select_icon);
      holder.layout = convertView.findViewById(R.id.layout);
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }

    StickerImageData item = list.get(pos);
    Log.d("asset", "file://" + item.getPreviewImagePath());
    if (ChangeElementState.getActionType() != null &&
        ChangeElementState.getActionType() == ActionType.STICKER &&
        ((StickerImageData) (ChangeElementState.getObject())).getPreviewImagePath()
            .equals(item.getPreviewImagePath())) {
      holder.selectImage.setVisibility(View.VISIBLE);
    } else {
      holder.selectImage.setVisibility(View.GONE);
    }
      // DraweeController draweeController =
      // Fresco.newDraweeControllerBuilder().setAutoPlayAnimations(true).setUri(Uri.parse("file:/" +
      // item.getPreviewImagePath())).build();
      // DraweeController draweeController =
      // Fresco.newDraweeControllerBuilder().setAutoPlayAnimations(true).setUri(Uri.parse("file:/heart001.png")).build();
      // holder.stickerImage.setController(draweeController);

    // holder.stickerImage.setImageURI(Uri.parse("file://" + item.getPreviewImagePath()));
      holder.stickerImage.setImageURI(Uri.parse("file://" + item.getPreviewImagePath()));

    // holder.stickerImage.setImageURI(Uri.parse("asset:///" + item.getPreviewImagePath()));
    // try {
    // holder.stickerImage.setImageDrawable(new
    // BitmapDrawable(context.getAssets().open(item.getPreviewImagePath())));
    // } catch (IOException e) {
    // e.printStackTrace();
    // }

//    holder.selectImage.setVisibility(View.GONE);
    holder.stickerImage.setVisibility(View.VISIBLE);

    adjustVarietyItemView(holder.layout);

    convertView.setBackgroundColor(Color.BLACK);

    return convertView;
  }

  private void adjustVarietyItemView(View view) {
    int margin = SystemUtils.dpToPx(15);
    int marginParent = SystemUtils.dpToPx(15);
    int height = SystemUtils.dpToPx(109);
    int width = (SystemUtils.getScreenWidthPx() - 2 * margin - 2 * marginParent) / 3;

    resetImageViewParams(view, margin, height, width);
  }

  private void resetImageViewParams(View view, int margin, int height, int width) {
    LinearLayout.LayoutParams layoutParams1 = (LinearLayout.LayoutParams) view.getLayoutParams();
    layoutParams1.height = height;
    layoutParams1.width = width;
    // layoutParams1.leftMargin = margin;
    view.setLayoutParams(layoutParams1);
  }

  static class ViewHolder {
    SimpleDraweeView stickerImage;
    ImageView selectImage;
    View layout;
  }
}
