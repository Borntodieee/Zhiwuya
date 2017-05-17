package com.example.borntodieee.zhiwuya.homepage;


import com.example.borntodieee.zhiwuya.bean.News;
import com.example.borntodieee.zhiwuya.interfaze.BasePresenter;
import com.example.borntodieee.zhiwuya.interfaze.BaseView;

import java.util.ArrayList;

public interface HomepageContract {

    interface View extends BaseView<Presenter> {

        void showError();

        void showLoading();

        void stopLoading();

        void showResults(ArrayList<News.Question> list);

    }

    interface Presenter extends BasePresenter {

        void loadPosts(long date, boolean clearing);

        void refresh();

        void loadMore(long date);

        void startReading(int position);

        void feelLucky();

    }

}
