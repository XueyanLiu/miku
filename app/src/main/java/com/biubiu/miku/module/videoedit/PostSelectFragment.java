package com.biubiu.miku.module.videoedit;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.biubiu.miku.R;
import com.biubiu.miku.base.BaseActivity;
import com.biubiu.miku.event.MobileNetPostEvent;
import com.biubiu.miku.util.OnBackPressedListener;
import com.biubiu.miku.util.sound.SoundPoolPlayer;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PostSelectFragment extends Fragment {
  private View rootView;
  @BindView(R.id.post)
  TextView postView;
  @BindView(R.id.save)
  TextView saveView;

  private final OnBackPressedListener onBackPressedListener = () -> {
    getFragmentManager().beginTransaction().remove(PostSelectFragment.this).commitAllowingStateLoss();
    return true;
  };

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    ((BaseActivity)getActivity()).addOnBackPressedListener(onBackPressedListener);
    rootView = inflater.inflate(R.layout.post_select_fragment, container, false);
    ButterKnife.bind(this, rootView);
    addListener();
    return rootView;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    ((BaseActivity)getActivity()).removeOnBackPressedListener(onBackPressedListener);
  }

  private void addListener() {
    postView.setOnClickListener(v -> {
      SoundPoolPlayer.getInstance().playSound(R.raw.click);
      EventBus.getDefault().post(new MobileNetPostEvent(true));
      getFragmentManager().beginTransaction().remove(PostSelectFragment.this).commitAllowingStateLoss();
    });

    saveView.setOnClickListener(v -> {
      SoundPoolPlayer.getInstance().playSound(R.raw.click);
      EventBus.getDefault().post(new MobileNetPostEvent(false));
      getFragmentManager().beginTransaction().remove(PostSelectFragment.this).commitAllowingStateLoss();
    });
  }
}
