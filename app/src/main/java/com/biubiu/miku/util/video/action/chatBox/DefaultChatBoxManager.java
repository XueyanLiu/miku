package com.biubiu.miku.util.video.action.chatBox;

import android.graphics.Color;

import com.biubiu.miku.R;

import java.util.ArrayList;
import java.util.List;

public class DefaultChatBoxManager {
    private static DefaultChatBoxManager defaultChatBoxManager;
    private List<ChatBoxGroup> boxGroups;
    private List<ChatBoxChild> childBoxGroup1;
    private List<ChatBoxChild> childBoxGroup2;
    private List<ChatBoxChild> childBoxGroup3;
    private List<ChatBoxChild> childBoxGroup4;
    private List<ChatBoxChild> childBoxGroup5;

    public synchronized static DefaultChatBoxManager getInstance() {
        if (defaultChatBoxManager == null) {
            defaultChatBoxManager = new DefaultChatBoxManager();
        }
        return defaultChatBoxManager;
    }

    private DefaultChatBoxManager() {
        this.childBoxGroup1 = new ArrayList<>();
        this.childBoxGroup2 = new ArrayList<>();
        this.childBoxGroup3 = new ArrayList<>();
        this.childBoxGroup4 = new ArrayList<>();
        this.childBoxGroup5 = new ArrayList<>();
        boxGroups = new ArrayList<>();
        boxGroups.add(new ChatBoxGroup(R.drawable.chat_box_group_1, childBoxGroup1));
//        boxGroups.add(new ChatBoxGroup(R.drawable.chat_box_group_2, childBoxGroup2));
//        boxGroups.add(new ChatBoxGroup(R.drawable.chat_box_group_3, childBoxGroup3));
//        boxGroups.add(new ChatBoxGroup(R.drawable.chat_box_group_4, childBoxGroup4));
//        boxGroups.add(new ChatBoxGroup(R.drawable.chat_box_group_5, childBoxGroup5));
        initChatBoxGroupData();
    }

    private void initChatBoxGroupData() {
        childBoxGroup1.add(new ChatBoxChild(R.drawable.chat_box_group1_1, 168, 108, 39, 17, 94, 68));
        childBoxGroup1.add(new ChatBoxChild(R.drawable.chat_box_group1_2, 168, 108, 33, 24, 100, 58));
//        childBoxGroup1.add(new ChatBoxChild(R.drawable.chat_box_group1_3, 168, 108, 41, 24, 86, 54));
//        childBoxGroup1.add(new ChatBoxChild(R.drawable.chat_box_group1_4, 168, 108, 41, 18, 82, 63));
//        childBoxGroup1.add(new ChatBoxChild(R.drawable.chat_box_group1_5, 168, 108, 41, 20, 86, 58));
//        childBoxGroup1.add(new ChatBoxChild(R.drawable.chat_box_group1_6, 168, 108, 38, 31, 90, 44));
//        childBoxGroup1.add(new ChatBoxChild(R.drawable.chat_box_group1_7, 168, 108, 30, 19, 109, 66));
        childBoxGroup1.add(new ChatBoxChild(R.drawable.chat_box_group1_8, 168, 108, 43, 34, 83, 45));
//        childBoxGroup1.add(new ChatBoxChild(R.drawable.chat_box_group1_9, 168, 108, 62, 23, 44, 58));
//        childBoxGroup1.add(new ChatBoxChild(R.drawable.chat_box_group1_10, 168, 108, 32, 15, 102, 62));
//        childBoxGroup1.add(new ChatBoxChild(R.drawable.chat_box_group1_11, 168, 108, 46, 46, 76, 39));
//        childBoxGroup1.add(new ChatBoxChild(R.drawable.chat_box_group1_12, 168, 108, 48, 25, 70, 58));
//        childBoxGroup1.add(new ChatBoxChild(R.drawable.chat_box_group1_13, 168, 108, 48, 17, 72, 64));
//        childBoxGroup1.add(new ChatBoxChild(R.drawable.chat_box_group1_14, 168, 108, 78, 18, 58, 61));
//        childBoxGroup1.add(new ChatBoxChild(R.drawable.chat_box_group1_15, 168, 108, 49, 39, 70, 43));
//        childBoxGroup1.add(new ChatBoxChild(R.drawable.chat_box_group1_16, 168, 108, 77, 13, 48, 58));
//        childBoxGroup1.add(new ChatBoxChild(R.drawable.chat_box_group1_17, 168, 108, 21, 27, 106, 49));
//        childBoxGroup1.add(new ChatBoxChild(R.drawable.chat_box_group1_18, 168, 108, 46, 41, 77, 39));
//        childBoxGroup1.add(new ChatBoxChild(R.drawable.chat_box_group1_19, 168, 108, 50, 25, 75, 42));
//        childBoxGroup1.add(new ChatBoxChild(R.drawable.chat_box_group1_20, 168, 108, 17, 9, 71, 63));
//        childBoxGroup1.add(new ChatBoxChild(R.drawable.chat_box_group1_21, 168, 108, 56, 19, 96, 57));
//        childBoxGroup2.add(new ChatBoxChild(R.drawable.chat_box_group2_1, 168, 108, 25, 11, 118, 64));
//        childBoxGroup2.add(new ChatBoxChild(R.drawable.chat_box_group2_2, 168, 108, 14, 9, 140, 42));
//        childBoxGroup2.add(new ChatBoxChild(R.drawable.chat_box_group2_3, 168, 108, 65, 32, 43, 42));
//        childBoxGroup2.add(new ChatBoxChild(R.drawable.chat_box_group2_4, 168, 108, 53, 20, 95, 65));
//        childBoxGroup2.add(new ChatBoxChild(R.drawable.chat_box_group2_5, 168, 108, 14, 24, 140, 42));
//        childBoxGroup2.add(new ChatBoxChild(R.drawable.chat_box_group2_6, 168, 108, 44, 10, 80, 60));
//        childBoxGroup2.add(new ChatBoxChild(R.drawable.chat_box_group2_7, 168, 108, 14, 24, 132, 46));
//        childBoxGroup2.add(new ChatBoxChild(R.drawable.chat_box_group2_8, 168, 108, 15, 24, 140, 21));
//        childBoxGroup2.add(new ChatBoxChild(R.drawable.chat_box_group2_9, 168, 108, 22, 12, 118, 68));
//        childBoxGroup2.add(new ChatBoxChild(R.drawable.chat_box_group2_10, 168, 108, 29, 42, 128, 46));
//        childBoxGroup2.add(new ChatBoxChild(R.drawable.chat_box_group2_11, 168, 108, 14, 39, 140, 21));
//        childBoxGroup2.add(new ChatBoxChild(R.drawable.chat_box_group2_12, 168, 108, 66, 15, 88, 36));
//        childBoxGroup2.add(new ChatBoxChild(R.drawable.chat_box_group2_13, 168, 108, 39, 28, 88, 55));
//        childBoxGroup2.add(new ChatBoxChild(R.drawable.chat_box_group2_14, 168, 108, 9, 15, 150, 47));
        childBoxGroup1.add(new ChatBoxChild(R.drawable.chat_box_group2_15, 168, 108, 31, 41, 109, 36));
        childBoxGroup1.add(new ChatBoxChild(R.drawable.chat_box_group2_16, 168, 108, 16, 21, 134, 42));
//        childBoxGroup2.add(new ChatBoxChild(R.drawable.chat_box_group2_17, 168, 108, 55, 9, 55, 66));
//        childBoxGroup2.add(new ChatBoxChild(R.drawable.chat_box_group2_18, 168, 108, 49, 16, 66, 40));
//        childBoxGroup2.add(new ChatBoxChild(R.drawable.chat_box_group2_19, 168, 108, 18, 13, 108, 62));
//        childBoxGroup2.add(new ChatBoxChild(R.drawable.chat_box_group2_20, 168, 108, 45, 12, 72, 66));
//        childBoxGroup2.add(new ChatBoxChild(R.drawable.chat_box_group2_21, 168, 108, 89, 9, 62, 64));
//        childBoxGroup2.add(new ChatBoxChild(R.drawable.chat_box_group2_22, 168, 108, 51, 10, 64, 62));
//        childBoxGroup2.add(new ChatBoxChild(R.drawable.chat_box_group2_23, 168, 108, 46, 16, 81, 42));
//        childBoxGroup2.add(new ChatBoxChild(R.drawable.chat_box_group2_24, 168, 108, 37, 25, 109, 48));
//        childBoxGroup2.add(new ChatBoxChild(R.drawable.chat_box_group2_25, 168, 108, 42, 18, 74, 73));
//        childBoxGroup2.add(new ChatBoxChild(R.drawable.chat_box_group2_26, 168, 108, 42, 16, 96, 64));
//        childBoxGroup2.add(new ChatBoxChild(R.drawable.chat_box_group2_27, 168, 108, 87, 10, 52, 42));
//        childBoxGroup2.add(new ChatBoxChild(R.drawable.chat_box_group2_28, 168, 108, 58, 33, 50, 44));
        childBoxGroup1.add(new ChatBoxChild(R.drawable.chat_box_group2_29, 168, 108, 14, 24, 126, 40));
//        childBoxGroup2.add(new ChatBoxChild(R.drawable.chat_box_group2_30, 168, 108, 52, 14, 62, 30));
//        childBoxGroup2.add(new ChatBoxChild(R.drawable.chat_box_group2_31, 168, 108, 48, 15, 72, 54));
//        childBoxGroup2.add(new ChatBoxChild(R.drawable.chat_box_group2_32, 168, 108, 40, 26, 62, 46));
//        childBoxGroup2.add(new ChatBoxChild(R.drawable.chat_box_group2_33, 168, 108, 60, 35, 45, 40));
//        childBoxGroup2.add(new ChatBoxChild(R.drawable.chat_box_group2_34, 168, 108, 12, 45, 143, 38));
//        childBoxGroup2.add(new ChatBoxChild(R.drawable.chat_box_group2_35, 168, 108, 38, 13, 86, 58));
//        childBoxGroup2.add(new ChatBoxChild(R.drawable.chat_box_group2_36, 168, 108, 12, 25, 143, 31));
//        childBoxGroup2.add(new ChatBoxChild(R.drawable.chat_box_group2_37, 168, 108, 37, 30, 90, 20));
//        childBoxGroup3.add(new ChatBoxChild(R.drawable.chat_box_group3_1, 168, 108, 44, 26, 76, 46));
//        childBoxGroup3.add(new ChatBoxChild(R.drawable.chat_box_group3_2, 168, 108, 36, 19, 99, 57));
//        childBoxGroup3.add(new ChatBoxChild(R.drawable.chat_box_group3_3, 168, 108, 40, 29, 70, 45));
//        childBoxGroup3.add(new ChatBoxChild(R.drawable.chat_box_group3_4, 168, 108, 54, 9, 61, 50));
//        childBoxGroup3.add(new ChatBoxChild(R.drawable.chat_box_group3_5, 168, 108, 58, 21, 57, 48));
//        childBoxGroup3.add(new ChatBoxChild(R.drawable.chat_box_group3_6, 168, 108, 33, 34, 46, 44));
//        childBoxGroup3.add(new ChatBoxChild(R.drawable.chat_box_group3_7, 168, 108, 68, 43, 30, 29));
//        childBoxGroup3.add(new ChatBoxChild(R.drawable.chat_box_group3_8, 168, 108, 52, 25, 72, 37));
//        childBoxGroup4.add(new ChatBoxChild(R.drawable.chat_box_group4_1, 168, 108, 40, 26, 90, 60));
//        childBoxGroup4.add(new ChatBoxChild(R.drawable.chat_box_group4_2, 168, 108, 44, 11, 81, 62));
//        childBoxGroup4.add(new ChatBoxChild(R.drawable.chat_box_group4_3, 168, 108, 44, 12, 69, 55));
//        childBoxGroup4.add(new ChatBoxChild(R.drawable.chat_box_group4_4, 168, 108, 49, 20, 72, 63));
//        childBoxGroup4.add(new ChatBoxChild(R.drawable.chat_box_group4_5, 168, 108, 45, 18, 72, 55));
//        childBoxGroup4.add(new ChatBoxChild(R.drawable.chat_box_group4_6, 168, 108, 48, 18, 71, 55));
//        childBoxGroup4.add(new ChatBoxChild(R.drawable.chat_box_group4_7, 168, 108, 34, 15, 100, 65));
//        childBoxGroup4.add(new ChatBoxChild(R.drawable.chat_box_group4_8, 168, 108, 43, 12, 81, 64));
//        childBoxGroup4.add(new ChatBoxChild(R.drawable.chat_box_group4_9, 168, 108, 31, 15, 99, 55));
//        childBoxGroup4.add(new ChatBoxChild(R.drawable.chat_box_group4_10, 168, 108, 34, 18, 100, 50));
//        childBoxGroup4.add(new ChatBoxChild(R.drawable.chat_box_group4_11, 168, 108, 20, 14, 127, 50));
//        childBoxGroup4.add(new ChatBoxChild(R.drawable.chat_box_group4_12, 168, 108, 45, 18, 75, 64));
//        childBoxGroup4.add(new ChatBoxChild(R.drawable.chat_box_group4_13, 168, 108, 20, 12, 127, 63));
//        childBoxGroup5
//                .add(new ChatBoxChild(R.drawable.chat_box_group5_1, 168, 108, 40, 26, 90, 60, Color.WHITE));
//        childBoxGroup5
//                .add(new ChatBoxChild(R.drawable.chat_box_group5_2, 168, 108, 44, 11, 81, 62, Color.WHITE));
        childBoxGroup1
                .add(new ChatBoxChild(R.drawable.chat_box_group5_3, 168, 108, 44, 12, 69, 55, Color.WHITE));
//        childBoxGroup5
//                .add(new ChatBoxChild(R.drawable.chat_box_group5_4, 168, 108, 49, 20, 72, 63, Color.WHITE));
        childBoxGroup1
                .add(new ChatBoxChild(R.drawable.chat_box_group5_5, 168, 108, 45, 18, 72, 55, Color.WHITE));
//        childBoxGroup5
//                .add(new ChatBoxChild(R.drawable.chat_box_group5_6, 168, 108, 48, 18, 71, 55, Color.WHITE));
        childBoxGroup1.add(
                new ChatBoxChild(R.drawable.chat_box_group5_7, 168, 108, 34, 15, 100, 65, Color.WHITE));
//        childBoxGroup5
//                .add(new ChatBoxChild(R.drawable.chat_box_group5_8, 168, 108, 43, 12, 81, 64, Color.WHITE));
//        childBoxGroup5
//                .add(new ChatBoxChild(R.drawable.chat_box_group5_9, 168, 108, 31, 15, 99, 55, Color.WHITE));
//        childBoxGroup5.add(
//                new ChatBoxChild(R.drawable.chat_box_group5_10, 168, 108, 34, 18, 100, 50, Color.WHITE));
//        childBoxGroup5.add(
//                new ChatBoxChild(R.drawable.chat_box_group5_11, 168, 108, 20, 14, 127, 50, Color.WHITE));
//        childBoxGroup5.add(
//                new ChatBoxChild(R.drawable.chat_box_group5_12, 168, 108, 45, 18, 75, 64, Color.WHITE));
//        childBoxGroup5.add(
//                new ChatBoxChild(R.drawable.chat_box_group5_13, 168, 108, 20, 12, 127, 63, Color.WHITE));
    }

    public List<ChatBoxGroup> getBoxGroups() {
        return boxGroups;
    }
}
