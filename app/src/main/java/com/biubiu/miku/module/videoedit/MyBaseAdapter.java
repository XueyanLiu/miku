package com.biubiu.miku.module.videoedit;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public class MyBaseAdapter<T> extends BaseAdapter {
  protected List<T> list = new ArrayList<T>();
  protected Context context;
  protected boolean isLoadIcon = true;

  public MyBaseAdapter(Context ctx, List<T> list) {
    this.context = ctx;
    setData(list);
  }

  public void setData(List<T> lst) {
    list.clear();
    if (lst != null) {
      list.addAll(lst);
    }
    notifyDataSetChanged();
  }

  @Override
  public int getCount() {
    return list == null ? 0 : list.size();
  }

  @Override
  public Object getItem(int arg0) {
    return list.get(arg0);
  }

  @Override
  public long getItemId(int arg0) {
    return arg0;
  }

  @Override
  public View getView(int pos, View view, ViewGroup group) {

    return view;
  }

}
