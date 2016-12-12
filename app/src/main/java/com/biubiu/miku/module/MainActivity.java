package com.biubiu.miku.module;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.biubiu.miku.Navigator;
import com.biubiu.miku.R;
import com.biubiu.miku.base.BaseActivity;

/**
 * Created by luis on 2016/11/23.
 */

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.video);
        addContentView(imageView, new ViewGroup.LayoutParams(200, 200));
        imageView.setOnClickListener(v -> Navigator.INSTANCE.navigateToVideoRecord(MainActivity.this));
    }
}
