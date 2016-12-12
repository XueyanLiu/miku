package com.biubiu.miku.util.video.action.videoTag;

import android.content.res.AssetManager;

import com.biubiu.miku.MikuApplication;
import com.biubiu.miku.util.video.action.SourceType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DefaultVideoTagManager {
  public static int BLACK_POINT_NUM = 0;
  private static DefaultVideoTagManager defaultStickerManager;
  private VideoTagImageData videoTagImageData;

  public synchronized static DefaultVideoTagManager getInstance() {
    if (defaultStickerManager == null) {
      defaultStickerManager = new DefaultVideoTagManager();
    }
    return defaultStickerManager;
  }

  private DefaultVideoTagManager() {
    try {
      AssetManager assetManager = MikuApplication.context.getAssets();
      String[] viewtags = assetManager.list("videotag");
      List<String> viewTagPathList = new ArrayList<>();
      String previewGifPath = "";
      String previewImagePath = "";
      BLACK_POINT_NUM = viewtags.length;
      for (int i = 0; i < viewtags.length; i++) {
        String viewTagName = viewtags[i];
        if (viewTagName.equals("gif")) {
          previewGifPath = "videotag/preview.gif";
        } else if (viewTagName.equals("preview")) {
          previewImagePath = "videotag/preview.png";
        } else {
          viewTagPathList.add("videotag/" + viewtags[i]);
        }
      }
      videoTagImageData = new VideoTagImageData.Builder()
          .setSourceType(SourceType.ASSETS).setPlayIntervalMs(30)
          .setViewTagImagePathList(viewTagPathList).setPreviewGifPath(previewGifPath)
          .setPreviewImagePath(previewImagePath)
          .build();
    } catch (IOException e) {

    }
  }

  public VideoTagImageData getViewTagImageData() {
    return videoTagImageData;
  }
}
