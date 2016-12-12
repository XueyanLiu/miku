package com.biubiu.miku.module.videoedit;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.biubiu.miku.R;
import com.biubiu.miku.util.video.action.chatBox.ChatBoxChild;
import com.biubiu.miku.util.video.action.chatBox.DefaultChatBoxManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatBoxFragment extends Fragment {

  @BindView(R.id.bubble_recyclerview)
  RecyclerView bubbleRecyclerView;

  private DefaultChatBoxManager chatBoxManager = DefaultChatBoxManager.getInstance();
  private List<List<ChatBoxChild>> chatBoxChilds = new ArrayList<>();
  private List<String> chatBoxTitle = new ArrayList<>();
  private ChatBoxAdapter bubbleAdapter;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.bubble_fragment, container, false);
    ButterKnife.bind(this, rootView);
    initViewData();
    return rootView;
  }

  private void initViewData() {
    chatBoxChilds.add(chatBoxManager.getBoxGroups().get(0).getChatBoxChildList());
    chatBoxTitle.add(getResources().getString(R.string.normal_bubble));
    bubbleAdapter = new ChatBoxAdapter(getActivity(), chatBoxTitle, chatBoxChilds);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    bubbleRecyclerView.setLayoutManager(linearLayoutManager);
    bubbleRecyclerView.setAdapter(bubbleAdapter);
  }

  public void notifyAdapter() {
    if (bubbleAdapter != null) {
      bubbleAdapter.notifyDataSetChanged();
    }
  }

}
