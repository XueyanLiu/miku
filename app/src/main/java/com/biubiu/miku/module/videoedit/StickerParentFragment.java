package com.biubiu.miku.module.videoedit;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.biubiu.miku.R;
import com.viewpagerindicator.UnderlinePageIndicator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StickerParentFragment extends Fragment {
  @BindView(R.id.indicator)
  UnderlinePageIndicator underlinePageIndicator;
  @BindView(R.id.elements_viewpager)
  ViewPager elementsViewPager;
  @BindView(R.id.sticker_view)
  TextView stickerView;

  private FragmentPagerAdapter fragmentPagerAdapter;
  private StickersFragment stickerFragment;

  private final List<Fragment> fragmentList = new ArrayList<>();

  private int mCurrentPage = 1;
  private View rootView;

  // private final OnBackPressedListener onBackPressedListener = () -> {
  // if (isAdded() && isVisible()) {
  // getFragmentManager().beginTransaction().remove(StickerParentFragment.this).commit();
  // return true;
  // }
  // return false;
  // };

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    if (rootView == null) {
      rootView = inflater.inflate(R.layout.fragment_sticker_parent, container, false);
      // ((BaseActivity) getActivity()).addOnBackPressedListener(onBackPressedListener);
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

  @Override
  public void onDestroyView() {
    // ((BaseActivity) getActivity()).removeOnBackPressedListener(onBackPressedListener);
    super.onDestroyView();
  }



  public static StickerParentFragment newFragment() {
    StickerParentFragment bottomEditFragment = new StickerParentFragment();
    return bottomEditFragment;
  }

  private void initData() {
     stickerFragment = new StickersFragment();
     fragmentList.add(stickerFragment);
     fragmentPagerAdapter = new FragmentPagerAdapter(getFragmentManager(), fragmentList);
     elementsViewPager.setAdapter(fragmentPagerAdapter);
     elementsViewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
//     underlinePageIndicator.setViewPager(elementsViewPager);
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
    stickerView.setOnClickListener(v -> {
      mCurrentPage = 1;
      underlinePageIndicator.setCurrentItem(mCurrentPage);
      resetTitle();
    });
  }

  private void resetTitle() {
    switch (mCurrentPage) {
      case 1:
        stickerView.setTextColor(getResources().getColor(R.color.yellow));
        break;
      default:
        break;
    }
  }

  public void notifyAdapter() {
    if (stickerFragment != null) {
      stickerFragment.notifyAdapter();
    }
  }

}
