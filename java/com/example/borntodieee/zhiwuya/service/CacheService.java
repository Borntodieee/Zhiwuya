package com.example.borntodieee.zhiwuya.service;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.android.volley.VolleyError;
import com.example.borntodieee.zhiwuya.bean.Story;
import com.example.borntodieee.zhiwuya.bean.StringModelImpl;
import com.example.borntodieee.zhiwuya.db.DatabaseHelper;
import com.example.borntodieee.zhiwuya.interfaze.OnStringListener;
import com.example.borntodieee.zhiwuya.util.Api;
import com.google.gson.Gson;

import java.util.Calendar;

/**
 * Created by lcx on 2017/5/13.
 */

public class CacheService extends Service {

    private static final String TAG = CacheService.class.getSimpleName();

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private StringModelImpl stringModel;
    private LocalBroadcastManager localBroadcastManager;
    private LocalReceiver localReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        dbHelper = new DatabaseHelper(this, "History.db", null, 1);
        db = dbHelper.getWritableDatabase();
        stringModel = new StringModelImpl(this);
        localReceiver = new LocalReceiver();

        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.borntodieee.zhiwuya.LOCAL_BROADCAST");

        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(localReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(localReceiver);
        stringModel.cancel(TAG);
        deleteTimeoutPosts();
    }

    private void startZhihuCache(final int id) {
        Cursor cursor = db.query("Zhihu", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                if (id == cursor.getInt(cursor.getColumnIndex("zhihu_id"))
                        && cursor.getString(cursor.getColumnIndex("zhihu_content")).equals("")) {
                    stringModel.load(TAG, Api.ZHIHU_NEWS + id, new OnStringListener() {
                        @Override
                        public void onSuccess(String result) {
                            Gson gson = new Gson();
                            Story story = gson.fromJson(result, Story.class);
                            if (story.getType() == 1) {
                                stringModel.load(TAG,story.getShare_url(), new OnStringListener() {
                                    @Override
                                    public void onSuccess(String result) {
                                        ContentValues values = new ContentValues();
                                        values.put("zhihu_content", result);
                                        db.update("Zhihu", values, "zhihu_id = ?", new String[] {String.valueOf(id)});
                                        values.clear();
                                    }

                                    @Override
                                    public void onError(VolleyError error) {

                                    }
                                });
                            } else {
                                ContentValues values = new ContentValues();
                                values.put("zhihu_content", result);
                                db.update("Zhihu", values, "zhihu_id = ?", new String[] {String.valueOf(id)});
                                values.clear();
                            }
                        }

                        @Override
                        public void onError(VolleyError error) {

                        }
                    });
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
    }
    private void deleteTimeoutPosts(){

        SharedPreferences sp = getSharedPreferences("user_settings",MODE_PRIVATE);

        Calendar c = Calendar.getInstance();
        long timeStamp = (c.getTimeInMillis() / 1000) - Long.parseLong(sp.getString("time_of_saving_articles", "7"))*24*60*60;

        String[] whereArgs = new String[] {String.valueOf(timeStamp)};
        db.delete("Zhihu", "zhihu_time < ? and bookmark != 1", whereArgs);


    }

    class LocalReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int id = intent.getIntExtra("id", 0);
            startZhihuCache(id);
        }
    }
}
