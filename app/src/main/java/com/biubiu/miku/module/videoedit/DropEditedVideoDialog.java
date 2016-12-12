package com.biubiu.miku.module.videoedit;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.biubiu.miku.Navigator;
import com.biubiu.miku.R;
import com.biubiu.miku.base.BaseActivity;
import com.biubiu.miku.util.OnBackPressedListener;
import com.biubiu.miku.util.sound.SoundPoolPlayer;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DropEditedVideoDialog extends Fragment {

  @BindView(R.id.sure)
  View sure;
  @BindView(R.id.cancle)
  View cancle;

  private final OnBackPressedListener onBackPressedListener = () -> {
    getFragmentManager().popBackStack();
    return true;
  };

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.drop_edited_video_fragment, container, false);
    ButterKnife.bind(this, rootView);
    rootView.setOnClickListener(v -> getFragmentManager().popBackStack());
    addListener();
    return rootView;
  }

  private void addListener() {
    sure.setOnClickListener(v -> {
      SoundPoolPlayer.getInstance().playSound(R.raw.click);
      Navigator.INSTANCE.navigateToVideoRecord(getActivity());
      getActivity().finish();
    });
    cancle.setOnClickListener(v -> getFragmentManager().popBackStack());
    ((BaseActivity) getActivity()).addOnBackPressedListener(onBackPressedListener);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    ((BaseActivity) getActivity()).removeOnBackPressedListener(onBackPressedListener);
  }

}
