package com.biubiu.miku.base;

/**
 * Created by luis on 2016/11/21.
 */

public abstract class BasePresenter<V extends BaseView> {

    private V mView;

    public void attachView(V mView) {
        this.mView = mView;
    }

    public void detachView() {
        mView = null;
    }

    public boolean isViewAttached() {
        return mView != null;
    }

    public V getMvpView() {
        return mView;
    }

}
