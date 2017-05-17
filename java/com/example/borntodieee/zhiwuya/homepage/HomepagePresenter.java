package com.example.borntodieee.zhiwuya.homepage;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.LocalBroadcastManager;

import com.android.volley.VolleyError;
import com.example.borntodieee.zhiwuya.bean.News;
import com.example.borntodieee.zhiwuya.bean.StringModelImpl;
import com.example.borntodieee.zhiwuya.db.DatabaseHelper;
import com.example.borntodieee.zhiwuya.detail.DetailActivity;
import com.example.borntodieee.zhiwuya.interfaze.OnStringListener;
import com.example.borntodieee.zhiwuya.util.Api;
import com.example.borntodieee.zhiwuya.util.DateFormatter;
import com.example.borntodieee.zhiwuya.util.NetworkState;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * Created by lcx on 2017/5/11.
 */

public class HomepagePresenter implements HomepageContract.Presenter {

    private HomepageContract.View view;
    private StringModelImpl model;
    private Context context;

    private DateFormatter formatter = new DateFormatter();
    private Gson gson = new Gson();

    private ArrayList<News.Question> list = new ArrayList<News.Question>();

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public HomepagePresenter(Context context, HomepageContract.View view) {
        this.view = view;
        this.view.setPresenter(this);
        this.context = context;
        model = new StringModelImpl(context);
        dbHelper = new DatabaseHelper(context, "History.db", null, 1);
        db = dbHelper.getWritableDatabase();
    }

    @Override
    public void loadPosts(long date, final boolean clearing) {

        if (clearing) {
            view.showLoading();
        }

        if (NetworkState.networkConnected(context)) {

            model.load( Api.ZHIHU_HISTORY + formatter.ZhihuDailyDateFormat(date), new OnStringListener() {
                @Override
                public void onSuccess(String result) {

                    try {
                        News post = gson.fromJson(result, News.class);
                        ContentValues values = new ContentValues();

                        if (clearing) {
                            list.clear();
                        }

                        for (News.Question item : post.getStories()) {
                            list.add(item);
                            if (!queryIfIDExists(item.getId())) {
                                db.beginTransaction();
                                try {
                                    DateFormat format = new SimpleDateFormat("yyyyMMdd");
                                    Date date = format.parse(post.getDate());
                                    values.put("zhihu_id", item.getId());
                                    values.put("zhihu_news", gson.toJson(item));
                                    values.put("zhihu_content", "");
                                    values.put("zhihu_time", date.getTime() / 1000);
                                    db.insert("Zhihu", null, values);
                                    values.clear();
                                    db.setTransactionSuccessful();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    db.endTransaction();
                                }

                            }
                            Intent intent = new Intent("com.example.borntodieee.zhiwuya.LOCAL_BROADCAST");
                            intent.putExtra("id", item.getId());
                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                        }
                        view.showResults(list);
                    } catch (JsonSyntaxException e) {
                        view.showError();
                    }

                    view.stopLoading();
                }

                @Override
                public void onError(VolleyError error) {
                    view.stopLoading();
                    view.showError();
                }
            });
        } else {

            if (clearing) {

                list.clear();

                Cursor cursor = db.query("Zhihu", null, null, null, null, null, null);
                if (cursor.moveToFirst()) {
                    do {
                        News.Question question = gson.fromJson(cursor.getString(cursor.getColumnIndex("zhihu_news")), News.Question.class);
                        list.add(question);
                    } while (cursor.moveToNext());
                }
                cursor.close();
                view.stopLoading();
                view.showResults(list);

            } else {
                view.showError();
            }

        }
    }

    @Override
    public void refresh() {
        loadPosts(Calendar.getInstance().getTimeInMillis(), true);
    }

    @Override
    public void loadMore(long date) {
        loadPosts(date, false);
    }

    @Override
    public void startReading(int position) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra("id", list.get(position).getId());
        intent.putExtra("title", list.get(position).getTitle());
        intent.putExtra("coverUrl", list.get(position).getImages().get(0));
        context.startActivity(intent);
    }

    @Override
    public void feelLucky() {
        if (list.isEmpty()) {
            view.showError();
            return;
        }
        startReading(new Random().nextInt(list.size()));
    }

    @Override
    public void start() {
        loadPosts(Calendar.getInstance().getTimeInMillis(), true);
    }

    // 查询数据库表中是否已经存在了此id
    private boolean queryIfIDExists(int id) {
        Cursor cursor = db.query("Zhihu", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                if (id == cursor.getInt(cursor.getColumnIndex("zhihu_id")))
                    return true;
            } while (cursor.moveToNext());
        }
        cursor.close();
        return false;
    }
}
