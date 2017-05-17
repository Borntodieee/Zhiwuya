package com.example.borntodieee.zhiwuya.bookmarks;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.borntodieee.zhiwuya.bean.News;
import com.example.borntodieee.zhiwuya.db.DatabaseHelper;
import com.example.borntodieee.zhiwuya.detail.DetailActivity;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Random;

public class BookmarksPresenter implements BookmarksContract.Presenter {

    private BookmarksContract.View view;
    private Context context;
    private Gson gson;

    private ArrayList<News.Question> zhihuList;


    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public BookmarksPresenter(Context context, BookmarksContract.View view) {
        this.context = context;
        this.view = view;
        this.view.setPresenter(this);
        gson = new Gson();
        dbHelper = new DatabaseHelper(context, "History.db", null, 1);
        db = dbHelper.getWritableDatabase();

        zhihuList = new ArrayList<>();

    }

    @Override
    public void start() {

    }

    @Override
    public void loadResults(boolean refresh) {

        if (!refresh) {
            view.showLoading();
        } else {
            zhihuList.clear();
        }

        checkForFreshData();

        view.showResults(zhihuList);

        view.stopLoading();

    }

    @Override
    public void startReading(int position) {
        Intent intent = new Intent(context, DetailActivity.class);
                News.Question q = zhihuList.get(position);
                intent.putExtra("id",q.getId());
                intent.putExtra("title", q.getTitle());
                intent.putExtra("coverUrl", q.getImages().get(0));
        context.startActivity(intent);
    }

    @Override
    public void checkForFreshData() {

        Cursor cursor = db.rawQuery("select * from Zhihu where bookmark = ?", new String[]{"1"});
        if (cursor.moveToFirst()) {
            do {
                News.Question question = gson.fromJson(cursor.getString(cursor.getColumnIndex("zhihu_news")), News.Question.class);
                zhihuList.add(question);
            } while (cursor.moveToNext());
        }
        cursor.close();

    }

    @Override
    public void feelLucky() {
        if (zhihuList.isEmpty()) {
            return;
        }
        startReading(new Random().nextInt(zhihuList.size()));
    }

}
