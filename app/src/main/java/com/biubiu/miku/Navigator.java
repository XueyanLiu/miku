package com.biubiu.miku;

import android.content.Context;

import com.biubiu.miku.module.videoedit.VideoEditActivity;
import com.biubiu.miku.module.videorecord.VideoRecordActivity;

/**
 * 应用内activity跳转控制类
 * <p>
 * Created by luis on 2016/11/21.
 */

public enum Navigator {

    INSTANCE;

    public void navigateToVideoRecord(Context context) {
        if (context != null) {
            context.startActivity(VideoRecordActivity.getCallingIntent(context));
        }
    }

    public void navigateToVideoEdit(Context context, String videoFilePath, float videoRatio) {
        if (context != null) {
            context.startActivity(VideoEditActivity.getCallingIntent(context, videoFilePath, videoRatio));
        }
    }

}
