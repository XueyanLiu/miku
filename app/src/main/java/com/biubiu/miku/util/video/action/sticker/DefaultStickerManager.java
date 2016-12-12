package com.biubiu.miku.util.video.action.sticker;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.biubiu.miku.MikuApplication;
import com.biubiu.miku.R;
import com.biubiu.miku.util.FileUtils;
import com.biubiu.miku.util.ThreadPool;
import com.biubiu.miku.util.video.action.SourceType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DefaultStickerManager {
  private static DefaultStickerManager defaultStickerManager;
  private static OnLoadSuccessListener onLoadSuccessListener;
  private List<StickerImageGroup> stickerImageGroupList;

  private static final String[] paths =
  {"xinqingxiaowu", "wanshengjie", "gaoguaibiaoqing", "qifen"};
  private static final String[] titles = {"心情小物", "万圣节", "搞怪表情", "气氛"};

  private static String stickerPath = "";

  private static final int LOAD_BEFORE = 1;
  private static final int LOAD_FINISH = 2;
  private Handler mHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);
      switch (msg.what) {
        case LOAD_BEFORE:
          if (onLoadSuccessListener != null) {
            onLoadSuccessListener.onLoadPre();
          }
          break;
        case LOAD_FINISH:
          if (onLoadSuccessListener != null) {
            onLoadSuccessListener.onSuccess(getStickerImageGroupList());
          }
          break;
      }
    }
  };

  public synchronized static DefaultStickerManager getInstance() {
    if (defaultStickerManager == null) {
      defaultStickerManager = new DefaultStickerManager();
    }
    return defaultStickerManager;
  }

  static {
    stickerPath = FileUtils.FILE_DIR+"sticker/";
    if (!FileUtils.isDirExist(stickerPath)) {
      File file = new File(stickerPath);
      file.mkdirs();
    }
  }

  public void setLoadListener(OnLoadSuccessListener listener) {
    if (listener != null) {
      onLoadSuccessListener = listener;
    }
    List<StickerImageGroup> stickerImageGroupList = getStickerImageGroupList();
    Log.d("asset", "stickerImageGroupList.isEmpty():" + stickerImageGroupList.isEmpty());
    if (!stickerImageGroupList.isEmpty()) {
      if (onLoadSuccessListener != null) {
        onLoadSuccessListener.onSuccess(stickerImageGroupList);
      }
      return;
    }

    ThreadPool.getInstance().execute(() -> {
      Log.d("asset", "LOAD_BEFORE");
      mHandler.sendEmptyMessage(LOAD_BEFORE);
      initStickers();
      mHandler.sendEmptyMessage(LOAD_FINISH);
    });
  }

  private DefaultStickerManager() {
    this.stickerImageGroupList = new ArrayList<>();
  }

  private void initStickers() {
    try {
      if (TextUtils.isEmpty(stickerPath)) {
        return;
      }
      if (FileUtils.isDirEmpty(stickerPath)) {
        FileUtils.copyFolderFromAssets(MikuApplication.context, "stickers", stickerPath);
      }

      Log.d("asset", "stickerPath:"+stickerPath);

      Log.d("asset", "copyFolderFromAssets-finish");

      for (int n = 0; n < paths.length; n++) {
        StickerImageGroup group = new StickerImageGroup();
        ArrayList<StickerImageData> stickerImageDataList = new ArrayList<>();

        String path = paths[n];

        File file = new File(stickerPath + path);
        String[] stickers = file.list();
        for (int i = 0; i < stickers.length; i++) {
          List<String> stickerChildPathList = new ArrayList<>();
          String stickerName = stickers[i];

          if (".nomedia".equals(stickerName)) {
            continue;
          }

          File stickerFile = new File(stickerPath + path + File.separator + stickerName);
          String[] stickerChildNames = stickerFile.list();
          String previewGifPath = "";
          String previewImagePath = "";
          for (int j = 0; j < stickerChildNames.length; j++) {
            String stickerChildName = stickerChildNames[j];
            if (".nomedia".equals(stickerChildName)) {
              continue;
            }
            String stickerChildPath =
                stickerPath + path + File.separator + stickerName + File.separator
                    + stickerChildName;
            if (stickerChildName.equals("gif")) {
              previewGifPath = stickerChildPath + "/" + "preview.gif";
            } else if (stickerChildName.contains("preview")) {
              previewImagePath = stickerPath + path + File.separator + stickerName + File.separator
                      + stickerChildName;
            } else {
              stickerChildPathList.add(stickerChildPath);
            }
          }
          int soundId = -1;
          if (stickerName.equals("blink")) {
            soundId = R.raw.sticker_blink;
          } else if (stickerName.equals("clap")) {
            soundId = R.raw.sticker_clap;
          } else if (stickerName.equals("clap")) {
            soundId = R.raw.sticker_clap;
          } else if (stickerName.equals("dizzy")) {
            soundId = R.raw.sticker_yun;
          }

          stickerImageDataList.add(new StickerImageData.Builder()
              .setSourceType(SourceType.ASSETS).setPlayIntervalMs(30)
              .setStickerImagePathList(stickerChildPathList).setPreviewGifPath(previewGifPath)
              .setPreviewImagePath(previewImagePath)
              .setSoundId(soundId)
              .build());
        }

        group.setGroupName(titles[n]);
        group.setStickerImageDataList(stickerImageDataList);
        stickerImageGroupList.add(group);

      }

      Log.d("asset", "load sticker finish");

    } catch (Exception e) {
      Log.d("asset", Log.getStackTraceString(e));
    }
  }

  public List<StickerImageGroup> getStickerImageGroupList() {
    return stickerImageGroupList;
  }

  public List<StickerImageData> getStickerImageDataList() {
    return null;
  }

  public interface OnLoadSuccessListener {
    void onLoadPre();

    void onSuccess(List<StickerImageGroup> stickerImageGroupList);
  }
}
