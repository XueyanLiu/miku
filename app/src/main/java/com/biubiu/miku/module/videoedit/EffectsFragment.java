package com.biubiu.miku.module.videoedit;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.biubiu.miku.R;
import com.biubiu.miku.event.SelectEffectsEvent;
import com.biubiu.miku.util.video.action.montage.MontageType;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EffectsFragment extends Fragment implements View.OnClickListener {

  @BindView(R.id.layout_freeze)
  LinearLayout layoutFreeze;
  @BindView(R.id.layout_slow_motion)
  LinearLayout layoutSlowMotion;
  @BindView(R.id.layout_repeat)
  LinearLayout layoutRepeat;
  @BindView(R.id.layout_forward)
  LinearLayout layoutForward;
  private View rootView;

  public static EffectsFragment newFragment() {
    EffectsFragment bottomEditFragment = new EffectsFragment();
    return bottomEditFragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    if (rootView == null) {
      rootView = inflater.inflate(R.layout.fragment_effects, container, false);
      ButterKnife.bind(this, rootView);
      setUpView();
    } else {
      ViewGroup parent = (ViewGroup) rootView.getParent();
      if (parent != null) {
        parent.removeView(rootView);
      }
    }

    return rootView;
  }

  private void setUpView() {
    layoutFreeze.setOnClickListener(this);
    layoutSlowMotion.setOnClickListener(this);
    layoutRepeat.setOnClickListener(this);
    layoutForward.setOnClickListener(this);
  }


  @Override
  public void onDestroyView() {
    super.onDestroyView();
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()){
      case R.id.layout_freeze:
        EventBus.getDefault().post(new SelectEffectsEvent(MontageType.FREEZE));
        break;
      case R.id.layout_slow_motion:
        EventBus.getDefault().post(new SelectEffectsEvent(MontageType.SLOW_MOTION));
        break;
      case R.id.layout_repeat:
        EventBus.getDefault().post(new SelectEffectsEvent(MontageType.REPEAT));
        break;
      case R.id.layout_forward:
        EventBus.getDefault().post(new SelectEffectsEvent(MontageType.FORWARD));
        break;
    }
  }
}
