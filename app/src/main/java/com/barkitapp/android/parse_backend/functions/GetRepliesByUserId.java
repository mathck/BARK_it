package com.barkitapp.android.parse_backend.functions;

import android.content.Context;

import com.barkitapp.android._core.utility.LastRefresh;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.HashMap;

public class GetRepliesByUserId {

    private static UpdatePosts.OnUpdatePostsCompleted listener;

    public static void run(final Context context, UpdatePosts.OnUpdatePostsCompleted listener, String user_id) {

        GetRepliesByUserId.listener = listener;

        HashMap<String, Object> params = new HashMap<>();
        params.put("user_id", user_id);

        ParseCloud.callFunctionInBackground("GetRepliesByUserId", params, new FunctionCallback<HashMap<String, ArrayList<ParseObject>>>() {
            public void done(HashMap<String, ArrayList<ParseObject>> result, ParseException e) {
                if (e == null) {
                    GetRepliesByUserId.listener.onUpdatePostsCompleted(result);
                } else {
                    GetRepliesByUserId.listener.onUpdatePostsFailed(e.getMessage());
                }
            }
        });
    }
}
