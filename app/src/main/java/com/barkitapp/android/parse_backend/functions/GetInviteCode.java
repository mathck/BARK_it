package com.barkitapp.android.parse_backend.functions;

import android.content.Context;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.HashMap;

public class GetInviteCode {

    public interface OnInviteCodeCompleted {
        void onInviteCodeCompleted(String result);
        void onInviteCodeFailed(String error);
    }

    private static OnInviteCodeCompleted listener;

    public static void run(final Context context, OnInviteCodeCompleted listener, final String user_id) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("user_id", user_id);

        GetInviteCode.listener = listener;

        ParseCloud.callFunctionInBackground("GetInviteCode", params, new FunctionCallback<String>() {
            public void done(String result, ParseException e) {
                if (e == null) {
                    GetInviteCode.listener.onInviteCodeCompleted(result);
                } else {
                    GetInviteCode.listener.onInviteCodeFailed(e.getMessage());
                }
            }
        });
    }
}

