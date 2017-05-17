package com.example.borntodieee.zhiwuya.bean;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.borntodieee.zhiwuya.app.VolleySingleton;
import com.example.borntodieee.zhiwuya.interfaze.OnStringListener;


public class StringModelImpl {

    private Context context;

    public StringModelImpl(Context context) {
        this.context = context;
    }

    public void load(String url,OnStringListener listener) {
        load(null,url,listener);
    }

    public void load(String tag, String url, final OnStringListener listener) {
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                listener.onSuccess(s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                listener.onError(volleyError);
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        request.setTag(tag);

        VolleySingleton.getVolleySingleton(context).addToRequestQueue(request);
    }

    public void cancel(String tag) {
        VolleySingleton.getVolleySingleton(context).cancelFromRequestQueue(tag);
    }

}
