package com.barkitapp.android.parse_backend.functions;

import android.content.Context;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GetUser {

    public interface OnCreateUserCompleted {
        void onCreateUserCompleted(ParseObject result);
        void onCreateUserFailed(String error);
    }

    private static List<OnCreateUserCompleted> listener;

    public static void run(final Context context, final OnCreateUserCompleted listener, final String userId) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("user_id", userId);

        if(GetUser.listener == null)
            GetUser.listener = new ArrayList<>();

        GetUser.listener.add(listener);

        ParseCloud.callFunctionInBackground("GetUser", params, new FunctionCallback<ParseObject>() {
            public void done(ParseObject result, ParseException e) {
                if (e == null) {
                    for(OnCreateUserCompleted sub : GetUser.listener) {
                        sub.onCreateUserCompleted(result);
                    }

                } else {
                    for(OnCreateUserCompleted sub : GetUser.listener) {
                        sub.onCreateUserFailed(e.getMessage());
                    }
                }
                GetUser.listener.clear();
            }
        });
    }
}

