package com.biubiu.miku.util.video.action.chatBox;

import com.biubiu.miku.util.video.action.Action;
import com.biubiu.miku.util.video.action.ActionContent;
import com.biubiu.miku.util.video.action.ActionImageData;
import com.biubiu.miku.util.video.action.RecordActionLocationTask;
import com.biubiu.miku.util.video.action.RecordLocation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatBox extends Action implements Serializable {
  private static final long serialVersionUID = 6461126905260639931L;
  private ChatBoxContent chatBoxContent;

  public ChatBox(ChatBoxContent chatBoxContent) {
    this.chatBoxContent = chatBoxContent;
  }

  public void setActionImageData(ActionImageData actionImageData) {
    this.chatBoxContent.setActionImageData(actionImageData);
  }

  public List<ChatBoxLocation> matchRecordLocation(int recordTimePositionMs) {
    List<ChatBoxLocation> matchRecordLocationList = new ArrayList<>();
    for (RecordActionLocationTask recordLocationTask : recordActionLocationTasks) {
      Map<Integer, RecordLocation> recordLocationTaskMap =
          recordLocationTask.getShowLocationTaskMap();
      if (!recordLocationTaskMap.isEmpty()) {
        RecordLocation recordLocation = recordLocationTaskMap.get(recordTimePositionMs);
        if (recordLocation != null) {
          matchRecordLocationList.add(new ChatBoxLocation(recordLocation, chatBoxContent));
        }
      }
    }
    return matchRecordLocationList;
  }

  public ChatBoxContent getChatBoxContent() {
    return chatBoxContent;
  }

  public ActionImageData getActionImageData() {
    return chatBoxContent.getActionImageData();
  }

  @Override
  public ActionContent getActionContent() {
    return chatBoxContent;
  }
}
