package com.biubiu.miku.base;

import android.support.v7.app.AppCompatActivity;

import com.biubiu.miku.util.OnBackPressedListener;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by luis on 2016/11/21.
 */

public class BaseActivity extends AppCompatActivity {
    private List<OnBackPressedListener> onBackPressedListeners = new LinkedList<>();

    @Override
    public void onBackPressed() {
        for (OnBackPressedListener onBackPressedListener : onBackPressedListeners) {
            if (onBackPressedListener.onBack()) {
                return;
            }
        }
        super.onBackPressed();
    }

    public void addOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        onBackPressedListeners.add(0, onBackPressedListener);
    }

    public void removeOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        onBackPressedListeners.remove(onBackPressedListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
