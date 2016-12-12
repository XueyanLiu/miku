package com.biubiu.miku.module.videoedit;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.biubiu.miku.R;
import com.biubiu.miku.util.video.action.videoTag.VideoTagContent;
import com.biubiu.miku.util.video.action.videoTag.VideoTagManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TagFragment extends Fragment {

  @BindView(R.id.tag_recyclerview)
  RecyclerView tagRecyclerView;
  private VideoTagManager videoTagManager = VideoTagManager.getInstance();
  private List<VideoTagContent> videoBrifeTagContents =
      videoTagManager.getBrifeVideoTagContentList();
  private List<VideoTagContent> videoPersonalityTagContents =
      videoTagManager.getPersonalityVideoTagContentList();
  private List<List<VideoTagContent>> videoTagContentList = new ArrayList<>();
  private List<String> tagTitle = new ArrayList<>();
  private TagAdapter tagAdapter;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.tag_fragment, container, false);
    ButterKnife.bind(this, rootView);
    initViewData();
    return rootView;
  }

  private void initViewData() {
    videoTagContentList.add(videoBrifeTagContents);
    videoTagContentList.add(videoPersonalityTagContents);
    tagTitle.add(getResources().getString(R.string.brief_tag));
    tagTitle.add(getResources().getString(R.string.personality_tag));
    tagAdapter = new TagAdapter(getActivity(), tagTitle, videoTagContentList);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    tagRecyclerView.setLayoutManager(linearLayoutManager);
    tagRecyclerView.setAdapter(tagAdapter);
  }

  public void notifyAdapter() {
    if (tagAdapter != null) {
      tagAdapter.notifyDataSetChanged();
    }
  }

}
