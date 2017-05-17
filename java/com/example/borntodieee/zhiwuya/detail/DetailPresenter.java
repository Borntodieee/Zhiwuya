package com.example.borntodieee.zhiwuya.detail;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.Html;
import android.webkit.WebView;

import com.android.volley.VolleyError;
import com.example.borntodieee.zhiwuya.R;
import com.example.borntodieee.zhiwuya.bean.Story;
import com.example.borntodieee.zhiwuya.bean.StringModelImpl;
import com.example.borntodieee.zhiwuya.db.DatabaseHelper;
import com.example.borntodieee.zhiwuya.interfaze.OnStringListener;
import com.example.borntodieee.zhiwuya.util.Api;
import com.example.borntodieee.zhiwuya.util.NetworkState;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import static android.content.Context.CLIPBOARD_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class DetailPresenter implements DetailContract.Presenter {

    private DetailContract.View view;
    private StringModelImpl model;
    private Context context;

    private Story story;

    private SharedPreferences sp;
    private DatabaseHelper dbHelper;

    private Gson gson;

    private int id;
    private String title;
    private String coverUrl;

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public DetailPresenter(@NonNull Context context, @NonNull DetailContract.View view) {
        this.context = context;
        this.view = view;
        this.view.setPresenter(this);
        model = new StringModelImpl(context);
        sp = context.getSharedPreferences("user_settings", MODE_PRIVATE);
        dbHelper = new DatabaseHelper(context, "History.db", null, 1);
        gson = new Gson();
    }


    @Override
    public void openInBrowser() {
        if (story == null) {
            view.showLoadingError();
            return;
        }

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(story.getShare_url()));
            context.startActivity(intent);
        } catch (android.content.ActivityNotFoundException ex){
            view.showBrowserNotFoundError();
        }
    }

    @Override
    public void shareAsText() {
        if (story == null) {
            view.showSharingError();
            return;
        }

        try {
            Intent shareIntent = new Intent().setAction(Intent.ACTION_SEND).setType("text/plain");
            String shareText = "" + title + " " + story.getShare_url() + "\t\t\t" + context.getString(R.string.share_extra);
            shareIntent.putExtra(Intent.EXTRA_TEXT,shareText);
            context.startActivity(Intent.createChooser(shareIntent,context.getString(R.string.share_to)));
        } catch (android.content.ActivityNotFoundException ex){
            view.showSharingError();
        }

    }

    @Override
    public void openUrl(WebView webView, String url) {
            try{
                context.startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)));
            } catch (android.content.ActivityNotFoundException ex){
                view.showBrowserNotFoundError();
            }
    }

    @Override
    public void copyText() {
        if (story == null) {
            view.showCopyTextError();
            return;
        }

        ClipboardManager manager = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text", Html.fromHtml(title + "\n" + story.getBody()).toString());
        manager.setPrimaryClip(clipData);
        view.showTextCopied();

    }

    @Override
    public void copyLink() {
        if (story == null) {
            view.showCopyTextError();
            return;
        }

        ClipboardManager manager = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text", Html.fromHtml(story.getShare_url()).toString());
        manager.setPrimaryClip(clipData);
        view.showTextCopied();

    }

    @Override
    public void addToOrDeleteFromBookmarks() {
        if (queryIfIsBookmarked()) {
            // delete
            // update Zhihu set bookmark = 0 where zhihu_id = id
            ContentValues values = new ContentValues();
            values.put("bookmark", 0);
            dbHelper.getWritableDatabase().update("Zhihu", values, "zhihu_id = ?", new String[]{String.valueOf(id)});
            values.clear();

            view.showDeletedFromBookmarks();
        } else {
            // add
            // update Zhihu set bookmark = 1 where zhihu_id = id

            ContentValues values = new ContentValues();
            values.put("bookmark", 1);
            dbHelper.getWritableDatabase().update("Zhihu", values, "zhihu_id = ?", new String[]{String.valueOf(id)});
            values.clear();

            view.showAddedToBookmarks();
        }
    }

    @Override
    public boolean queryIfIsBookmarked() {
        if (id == 0) {
            view.showLoadingError();
            return false;
        }

        String sql = "select * from Zhihu where zhihu_id" + " = ?";
        Cursor cursor = dbHelper.getReadableDatabase()
                .rawQuery(sql, new String[]{String.valueOf(id)});

        if (cursor.moveToFirst()) {
            do {
                int isBookmarked = cursor.getInt(cursor.getColumnIndex("bookmark"));
                if (isBookmarked == 1) {
                    return true;
                }
            } while (cursor.moveToNext());
        }

        cursor.close();

        return false;
    }

    @Override
    public void requestData() {
        if (id == 0) {
            view.showLoadingError();
            return;
        }

        view.showLoading();
        view.setTitle(title);
        view.showCover(coverUrl);

        // set the web view whether to show the image
        view.setImageMode(sp.getBoolean("no_picture_mode",false));

                if (NetworkState.networkConnected(context)) {
                    model.load(Api.ZHIHU_NEWS + id, new OnStringListener() {
                        @Override
                        public void onSuccess(String result) {
                            {
                                Gson gson = new Gson();
                                try {
                                    story = gson.fromJson(result, Story.class);
                                    if (story.getBody() == null) {
                                        view.showResultWithoutBody(story.getShare_url());
                                    } else {
                                        view.showResult(convertZhihuContent(story.getBody()));
                                    }
                                } catch (JsonSyntaxException e) {
                                    view.showLoadingError();
                                }
                                view.stopLoading();
                            }
                        }

                        @Override
                        public void onError(VolleyError error) {
                            view.stopLoading();
                            view.showLoadingError();
                        }
                    });
                } else {
                    Cursor cursor = dbHelper.getReadableDatabase()
                            .query("Zhihu", null, null, null, null, null, null);
                    if (cursor.moveToFirst()) {
                        do {
                            if (cursor.getInt(cursor.getColumnIndex("zhihu_id")) == id) {
                                String content = cursor.getString(cursor.getColumnIndex("zhihu_content"));
                                try {
                                    story = gson.fromJson(content, Story.class);
                                } catch (JsonSyntaxException e) {
                                    view.showResult(content);
                                }
                                view.showResult(convertZhihuContent(story.getBody()));
                            }
                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                }

        view.stopLoading();

    }

    @Override
    public void start() {

    }

    private String convertZhihuContent(String preResult) {

        preResult = preResult.replace("<div class=\"img-place-holder\">", "");
        preResult = preResult.replace("<div class=\"headline\">", "");

        // 在api中，css的地址是以一个数组的形式给出，这里需要设置
        // in fact,in api,css addresses are given as an array
        // api中还有js的部分，这里不再解析js
        // javascript is included,but here I don't use it
        // 不再选择加载网络css，而是加载本地assets文件夹中的css
        // use the css file from local assets folder,not from network
        String css = "<link rel=\"stylesheet\" href=\"file:///android_asset/zhiwuya.css\" type=\"text/css\">";


        // 根据主题的不同确定不同的加载内容
        // load content judging by different theme
        String theme = "<body className=\"\" onload=\"onLoaded()\">";
        if ((context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                == Configuration.UI_MODE_NIGHT_YES){
            theme = "<body className=\"\" onload=\"onLoaded()\" class=\"night\">";
        }

        return new StringBuilder()
                .append("<!DOCTYPE html>\n")
                .append("<html lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\">\n")
                .append("<head>\n")
                .append("\t<meta charset=\"utf-8\" />")
                .append(css)
                .append("\n</head>\n")
                .append(theme)
                .append(preResult)
                .append("</body></html>").toString();
    }

}
