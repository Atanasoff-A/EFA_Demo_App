package com.example.efa_demo_app.Helpers;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleyHelper {

    private static final String TAG = VolleyHelper.class
            .getSimpleName();

    private RequestQueue requestQueue;
    private static VolleyHelper instance;
    private static Context context;

    private VolleyHelper(Context context) {
        this.context = context;
        getRequestQueue();
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        requestQueue.add(req);
    }


}
