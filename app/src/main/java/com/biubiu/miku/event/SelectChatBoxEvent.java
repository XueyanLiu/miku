package com.biubiu.miku.event;

import com.biubiu.miku.util.video.action.chatBox.ChatBoxChild;

public class SelectChatBoxEvent {

  public final ChatBoxChild chatBoxChild;

  public SelectChatBoxEvent(ChatBoxChild chatBoxChild) {
    this.chatBoxChild = chatBoxChild;
  }

  public ChatBoxChild getChatBoxChild() {
    return chatBoxChild;
  }
}
