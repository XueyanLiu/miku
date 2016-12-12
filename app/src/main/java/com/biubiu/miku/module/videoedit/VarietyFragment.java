package com.biubiu.miku.module.videoedit;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.biubiu.miku.R;
import com.biubiu.miku.util.video.action.runMan.DefaultRunManManager;
import com.biubiu.miku.util.video.action.runMan.RunManAttribute;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VarietyFragment extends Fragment {

  @BindView(R.id.variety_recyclerview)
  RecyclerView varietyRecyclerView;

  private View rootView;
  private final List<String> varietyTitleList = new ArrayList<>();
  private final List<RunManAttribute> runManAttributeList
      = DefaultRunManManager.getInstance().getRunManAttributes();
  private final List<RunManAttribute> kangxiAttributeList
      = DefaultRunManManager.getInstance().getKangxiAttributes();

  private VarietyAdapter varietyAdapter;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    rootView = inflater.inflate(R.layout.variety_fragment, container, false);
    ButterKnife.bind(this, rootView);
    initData();
    return rootView;
  }

  private void initData(){
    varietyTitleList.add(getResources().getString(R.string.runman_text));
    varietyTitleList.add(getResources().getString(R.string.kangxilaile));
    varietyAdapter = new VarietyAdapter(getActivity(),varietyTitleList,runManAttributeList,
        kangxiAttributeList);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    varietyRecyclerView.setLayoutManager(linearLayoutManager);
    varietyRecyclerView.setAdapter(varietyAdapter);
  }

  public void notifyAdapter() {
    if (varietyAdapter != null) {
      varietyAdapter.notifyDataSetChanged();
    }
  }

}
