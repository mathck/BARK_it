package com.barkitapp.android.parse_backend.functions;

import android.content.Context;

import com.barkitapp.android._core.utility.LastRefresh;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ReferUser {

    public interface OnReferUserCompleted {
        void onReferUserCompleted(HashMap<String, ArrayList<ParseObject>> result);
        void onReferUserFailed(String error);
    }

    private static OnReferUserCompleted listener;

    public static void run(final Context context, OnReferUserCompleted listener, String user_id, String invite_code) {

        LastRefresh.now(context);

        ReferUser.listener = listener;

        HashMap<String, Object> params = new HashMap<>();
        params.put("referred_id", user_id);
        params.put("invite_code", invite_code);

        ParseCloud.callFunctionInBackground("ReferUser", params, new FunctionCallback<HashMap<String, ArrayList<ParseObject>>>() {
            public void done(HashMap<String, ArrayList<ParseObject>> result, ParseException e) {
                if (e == null) {
                    ReferUser.listener.onReferUserCompleted(result);
                } else {
                    ReferUser.listener.onReferUserFailed(e.getMessage());
                }
            }
        });
    }
}
