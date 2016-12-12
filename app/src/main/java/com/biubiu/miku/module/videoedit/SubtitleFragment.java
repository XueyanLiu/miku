package com.biubiu.miku.module.videoedit;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.biubiu.miku.R;
import com.biubiu.miku.util.video.action.subtitle.SubtitleManager;
import com.biubiu.miku.util.video.action.subtitle.SubtitleType;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SubtitleFragment extends Fragment {

  @BindView(R.id.subtitle_recyclerview)
  RecyclerView subtitleRecyclerView;

  private List<SubtitleType> classicSubtitleList;
  private List<SubtitleType> jpSubtitleList;
  private List<SubtitleType> loveSubtitleList;
  private List<SubtitleType> officeSubtitleList;
  private List<String> SubTitleList;
  private List<List<SubtitleType>> subtitleTypes = new ArrayList<>();
  private SubtitleManager subtitleManager;
  private SubtitleAdapter subtitleAdapter;

  private final List<String> subtitleList = new ArrayList<>();
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_subtitle, container, false);
    ButterKnife.bind(this, rootView);
    initViewData();
    return rootView;
  }

  private void initViewData() {
    subtitleManager = SubtitleManager.getInstance();
    classicSubtitleList = subtitleManager.getClassicSubtitle();
    jpSubtitleList = subtitleManager.getJpSubtitleTypeList();
    loveSubtitleList = subtitleManager.getLoveSubtitleTypeList();
    officeSubtitleList = subtitleManager.getOfficeSubtitleTypeList();
    subtitleTypes.add(classicSubtitleList);
    subtitleTypes.add(jpSubtitleList);
    subtitleTypes.add(loveSubtitleList);
    subtitleTypes.add(officeSubtitleList);
    subtitleList.add(getResources().getString(R.string.classic_subtitle));
    subtitleList.add(getResources().getString(R.string.jp_subtitle));
    subtitleList.add(getResources().getString(R.string.love_subtitle));
    subtitleList.add(getResources().getString(R.string.office_subtitle));
    subtitleAdapter = new SubtitleAdapter(getActivity(), subtitleList, subtitleTypes);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    subtitleRecyclerView.setLayoutManager(linearLayoutManager);
    subtitleRecyclerView.setAdapter(subtitleAdapter);
  }

  public void notifyAdapter() {
    if (subtitleAdapter != null) {
      subtitleAdapter.notifyDataSetChanged();
    }
  }

}
