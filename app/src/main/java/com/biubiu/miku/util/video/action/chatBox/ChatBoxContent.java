package com.biubiu.miku.util.video.action.chatBox;

import com.biubiu.miku.util.video.action.ActionContent;
import com.biubiu.miku.util.video.action.ActionImageData;
import com.biubiu.miku.util.video.action.ActionType;

import java.io.Serializable;

public class ChatBoxContent extends ActionContent implements Serializable {
  private static final long serialVersionUID = -7888461400085678393L;
  private ChatBoxChild chatBoxChild;
  private String content;
  private ActionImageData actionImageData;

  public ChatBoxContent(ChatBoxChild chatBoxChild, String content) {
    super(ActionType.CHAT_BOX);
    this.chatBoxChild = chatBoxChild;
    this.content = content;
  }

  public ChatBoxChild getChatBoxChild() {
    return chatBoxChild;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getContent() {
    return content;
  }

  public void setActionImageData(ActionImageData actionImageData) {
    this.actionImageData = actionImageData;
  }

  public ActionImageData getActionImageData() {
    return actionImageData;
  }
}
