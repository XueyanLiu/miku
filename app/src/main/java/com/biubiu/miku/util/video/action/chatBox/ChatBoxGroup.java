package com.biubiu.miku.util.video.action.chatBox;

import java.util.List;

public class ChatBoxGroup {
  private int iconImageResId;
  private List<ChatBoxChild> chatBoxChildList;

  public ChatBoxGroup(int iconImageResId, List<ChatBoxChild> chatBoxChildList) {
    this.iconImageResId = iconImageResId;
    this.chatBoxChildList = chatBoxChildList;
  }

  public int getIconImageResId() {
    return iconImageResId;
  }

  public List<ChatBoxChild> getChatBoxChildList() {
    return chatBoxChildList;
  }
}
