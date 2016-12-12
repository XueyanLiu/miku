package com.biubiu.miku.util.video.action.sticker;

import java.util.List;

public class StickerImageGroup {

  private String groupName;
  private List<StickerImageData> stickerImageDataList;

  public String getGroupName() {
    return groupName;
  }

  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }

  public List<StickerImageData> getStickerImageDataList() {
    return stickerImageDataList;
  }

  public void setStickerImageDataList(List<StickerImageData> stickerImageDataList) {
    this.stickerImageDataList = stickerImageDataList;
  }
}
