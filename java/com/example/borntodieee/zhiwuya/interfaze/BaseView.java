package com.example.borntodieee.zhiwuya.interfaze;

import android.view.View;


public interface BaseView<T> {

    /**
     * set the presenter of mvp
     * @param presenter
     */
    void setPresenter(T presenter);

    /**
     * init the views of fragment
     * @param view
     */
    void initViews(View view);

}
