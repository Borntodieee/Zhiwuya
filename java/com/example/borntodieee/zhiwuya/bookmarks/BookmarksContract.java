package com.example.borntodieee.zhiwuya.bookmarks;

import com.example.borntodieee.zhiwuya.bean.News;
import com.example.borntodieee.zhiwuya.interfaze.BasePresenter;
import com.example.borntodieee.zhiwuya.interfaze.BaseView;

import java.util.ArrayList;

public interface BookmarksContract {

    interface View extends BaseView<Presenter> {

        void showResults(ArrayList<News.Question> zhihuList);

        void notifyDataChanged();

        void showLoading();

        void stopLoading();

    }

    interface Presenter extends BasePresenter {

        void loadResults(boolean refresh);

        void startReading(int position);

        void checkForFreshData();

        void feelLucky();

    }

}
