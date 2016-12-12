package com.biubiu.miku.module.videoedit;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class FragmentPagerAdapter extends PagerAdapter {
  private List<? extends Fragment> fragments;
  private FragmentManager fragmentManager;
  private FragmentTransaction fragmentTransaction;

  public FragmentPagerAdapter(FragmentManager fm, List<? extends Fragment> fragments) {
    this.fragmentManager = fm;
    this.fragments = fragments;
  }

  @Override
  public int getCount() {
    return fragments.size();
  }

  @Override
  public boolean isViewFromObject(View view, Object obj) {
    return ((Fragment) obj).getView() == view;
  }

  @Override
  public Fragment instantiateItem(ViewGroup container, int position) {
    String name = makeFragmentName(container.getId(), position);
    Fragment fragment = fragments.get(position);
    if (fragmentTransaction == null) {
      fragmentTransaction = fragmentManager.beginTransaction();
    }
    if (fragmentManager.findFragmentByTag(name) != null
        && fragmentManager.findFragmentByTag(name) == fragment) {
      fragmentTransaction.show(fragment);
    } else {
      fragmentTransaction.add(container.getId(), fragment, name);
    }
    return fragment;
  }

  @Override
  public void destroyItem(ViewGroup container, int position, Object object) {
    Fragment fragment = (Fragment) object;
    if (fragmentTransaction == null) {
      fragmentTransaction = fragmentManager.beginTransaction();
    }
    fragmentTransaction.hide(fragment);
  }

  @Override
  public void finishUpdate(ViewGroup container) {
    if (fragmentTransaction != null) {
      fragmentTransaction.commitAllowingStateLoss();
      fragmentTransaction = null;
      fragmentManager.executePendingTransactions();
    }
  }

  private static String makeFragmentName(int viewId, long id) {
    return "android:switcher:" + viewId + ":" + id;
  }

  @Override
  public int getItemPosition(Object object) {
    return PagerAdapter.POSITION_NONE;
  }
}
