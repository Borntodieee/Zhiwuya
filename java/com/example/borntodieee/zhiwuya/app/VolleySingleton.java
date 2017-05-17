package com.example.borntodieee.zhiwuya.app;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by lcx on 2017/5/11.
 */

public class VolleySingleton {
    private static VolleySingleton volleySingleton;
    private RequestQueue requestQueue;

    private VolleySingleton(Context context){
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }
    public static synchronized VolleySingleton getVolleySingleton(Context context){
        if(volleySingleton == null)
            volleySingleton = new VolleySingleton(context);
        return volleySingleton;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req){
        getRequestQueue().add(req);
    }

    public void cancelFromRequestQueue(String tag) {
        getRequestQueue().cancelAll(tag);
    }
}
