package com.biubiu.miku.module.videoedit;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.biubiu.miku.R;
import com.biubiu.miku.base.BaseActivity;

public class VideoEditActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.container_activity);
        if (savedInstanceState == null) {
            if (getIntent() != null && getIntent().getExtras() != null) {
                Bundle bundle = getIntent().getExtras();
                Fragment centerFragment = VideoEditFragment.newInstance(bundle.getString(VideoEditFragment.VIDEO_FILE_PATH), bundle.getFloat(VideoEditFragment.VIDEO_RATIO));
                getFragmentManager().beginTransaction().add(R.id.container, centerFragment).commit();
            }
        }
    }

    public static Intent getCallingIntent(Context context, String videoFilePath, float videoRatio) {
        Intent intent = new Intent(context, VideoEditActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(VideoEditFragment.VIDEO_FILE_PATH, videoFilePath);
        bundle.putFloat(VideoEditFragment.VIDEO_RATIO, videoRatio);
        intent.putExtras(bundle);
        return intent;
    }

}
