package com.story.sonder;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

class SingletonRequest {
    private static SingletonRequest singletonRequest;
    private RequestQueue requestQueue;
    private Context context;

    private SingletonRequest(Context context) {
        this.context = context;
        requestQueue = getRequestQueue();
    }

    static synchronized SingletonRequest getInstance(Context context) {
        if (singletonRequest == null) {
            singletonRequest = new SingletonRequest(context);
        }
        return singletonRequest;
    }

    private RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
