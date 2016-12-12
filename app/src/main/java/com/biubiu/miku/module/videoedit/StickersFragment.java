package com.biubiu.miku.module.videoedit;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.biubiu.miku.R;
import com.biubiu.miku.util.video.action.sticker.DefaultStickerManager;
import com.biubiu.miku.util.video.action.sticker.StickerImageGroup;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StickersFragment extends Fragment {

  @BindView(R.id.sticker_recyclerview)
  RecyclerView stickerRecyclerview;

  private View rootView;
  private StickerGroupAdapter madapter;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    if (rootView == null) {
      rootView = inflater.inflate(R.layout.fragment_stickers, container, false);
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
    DefaultStickerManager manager = DefaultStickerManager.getInstance();
    manager.setLoadListener(new DefaultStickerManager.OnLoadSuccessListener() {
      @Override
      public void onLoadPre() {
      }

      @Override
      public void onSuccess(List<StickerImageGroup> stickerImageGroupList) {
        madapter =
                new StickerGroupAdapter(getActivity(), stickerImageGroupList, null);
        stickerRecyclerview.setAdapter(madapter);
      }
    });

    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    stickerRecyclerview.setLayoutManager(linearLayoutManager);
  }


  @Override
  public void onDestroyView() {
    super.onDestroyView();
  }

  public void notifyAdapter() {
    if (madapter != null) {
      madapter.notifyDataSetChanged();
    }
  }

}
