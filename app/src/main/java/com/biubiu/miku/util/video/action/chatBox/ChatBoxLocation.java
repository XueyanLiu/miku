package com.biubiu.miku.util.video.action.chatBox;

import com.biubiu.miku.util.video.action.RecordLocation;

public class ChatBoxLocation {
    private final RecordLocation recordLocation;
    private final ChatBoxContent chatBoxContent;

    public ChatBoxLocation(RecordLocation recordLocation, ChatBoxContent chatBoxContent) {
        this.recordLocation = recordLocation;
        this.chatBoxContent = chatBoxContent;
    }

    public RecordLocation getRecordLocation() {
        return recordLocation;
    }

    public ChatBoxContent getChatBoxContent() {
        return chatBoxContent;
    }
}
