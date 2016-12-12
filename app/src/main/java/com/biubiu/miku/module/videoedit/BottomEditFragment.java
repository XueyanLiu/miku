package com.biubiu.miku.module.videoedit;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.biubiu.miku.R;
import com.biubiu.miku.event.ChangeElementEvent;
import com.viewpagerindicator.UnderlinePageIndicator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BottomEditFragment extends Fragment {
  @BindView(R.id.variety_view)
  TextView varietyView;
  @BindView(R.id.subtitle_view)
  TextView subtitleView;
  @BindView(R.id.bubble_view)
  TextView bubbleView;
  @BindView(R.id.tag_view)
  TextView tagView;
  @BindView(R.id.indicator)
  UnderlinePageIndicator underlinePageIndicator;
  @BindView(R.id.elements_viewpager)
  ViewPager elementsViewPager;

  private static final int VARIEYT = 0;
  private static final int SUBTITLE = 1;
  private static final int BUBBLE = 2;
  private static final int TAG = 3;
  private FragmentPagerAdapter fragmentPagerAdapter;
  private VarietyFragment varietyFragment;
  private SubtitleFragment subtitleFragment;
  private ChatBoxFragment bubbleFragment;
  private TagFragment tagFragment;
  private int mCurrentPage = VARIEYT;

  private final List<Fragment> fragmentList = new ArrayList<>();
  private View rootView;


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    if (rootView == null) {
      rootView = inflater.inflate(R.layout.bottom_edit_fragment, container, false);
      ButterKnife.bind(this, rootView);
      initData();
      addListener();
      resetTitle();
    } else {
      ViewGroup parent = (ViewGroup) rootView.getParent();
      if (parent != null) {
        parent.removeView(rootView);
      }
    }
    return rootView;
  }

  public static BottomEditFragment newFragment() {
    BottomEditFragment bottomEditFragment = new BottomEditFragment();
    return bottomEditFragment;
  }

  private void initData() {
    varietyFragment = new VarietyFragment();
    subtitleFragment = new SubtitleFragment();
    bubbleFragment = new ChatBoxFragment();
    tagFragment = new TagFragment();
    fragmentList.add(varietyFragment);
    fragmentList.add(subtitleFragment);
    fragmentList.add(bubbleFragment);
    fragmentList.add(tagFragment);
    fragmentPagerAdapter = new FragmentPagerAdapter(getFragmentManager(), fragmentList);
    elementsViewPager.setAdapter(fragmentPagerAdapter);
    elementsViewPager.setOffscreenPageLimit(0);
    elementsViewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
    underlinePageIndicator.setViewPager(elementsViewPager);
  }

  private void addListener() {
    underlinePageIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

      }

      @Override
      public void onPageSelected(int position) {
        mCurrentPage = position;
        resetTitle();
      }

      @Override
      public void onPageScrollStateChanged(int state) {}
    });
    varietyView.setOnClickListener(v -> {
      mCurrentPage = VARIEYT;
      underlinePageIndicator.setCurrentItem(mCurrentPage);
      resetTitle();
    });
    subtitleView.setOnClickListener(v -> {
      mCurrentPage = SUBTITLE;
      underlinePageIndicator.setCurrentItem(mCurrentPage);
      resetTitle();
    });
    bubbleView.setOnClickListener(v -> {
      mCurrentPage = BUBBLE;
      underlinePageIndicator.setCurrentItem(mCurrentPage);
      resetTitle();
    });
    tagView.setOnClickListener(v -> {
      mCurrentPage = TAG;
      underlinePageIndicator.setCurrentItem(mCurrentPage);
      resetTitle();
    });
  }

  private void resetTitle() {
    switch (mCurrentPage) {
      case VARIEYT:
        varietyView.setTextColor(getResources().getColor(R.color.yellow));
        subtitleView.setTextColor(getResources().getColor(R.color.white));
        bubbleView.setTextColor(getResources().getColor(R.color.white));
        tagView.setTextColor(getResources().getColor(R.color.white));
        break;
      case SUBTITLE:
        varietyView.setTextColor(getResources().getColor(R.color.white));
        subtitleView.setTextColor(getResources().getColor(R.color.yellow));
        bubbleView.setTextColor(getResources().getColor(R.color.white));
        tagView.setTextColor(getResources().getColor(R.color.white));
        break;
      case BUBBLE:
        varietyView.setTextColor(getResources().getColor(R.color.white));
        subtitleView.setTextColor(getResources().getColor(R.color.white));
        bubbleView.setTextColor(getResources().getColor(R.color.yellow));
        tagView.setTextColor(getResources().getColor(R.color.white));
        break;
      case TAG:
        varietyView.setTextColor(getResources().getColor(R.color.white));
        subtitleView.setTextColor(getResources().getColor(R.color.white));
        bubbleView.setTextColor(getResources().getColor(R.color.white));
        tagView.setTextColor(getResources().getColor(R.color.yellow));
        break;
      default:
        break;
    }
  }

  public void changeElement(ChangeElementEvent changeElementEvent) {
    if (changeElementEvent != null) {
          varietyFragment.notifyAdapter();
          subtitleFragment.notifyAdapter();
          bubbleFragment.notifyAdapter();
          tagFragment.notifyAdapter();
    }
  }

}
