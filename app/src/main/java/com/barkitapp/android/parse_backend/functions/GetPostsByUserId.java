package com.barkitapp.android.parse_backend.functions;

import android.content.Context;

import com.barkitapp.android._core.services.InternalAppData;
import com.barkitapp.android._core.utility.DistanceHelper;
import com.barkitapp.android._core.utility.LastRefresh;
import com.barkitapp.android._core.utility.SharedPrefKeys;
import com.barkitapp.android.parse_backend.enums.Order;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.HashMap;

public class GetPostsByUserId {

    private static UpdatePosts.OnUpdatePostsCompleted listener;

    public static void run(final Context context, UpdatePosts.OnUpdatePostsCompleted listener, String user_id) {

        LastRefresh.now(context);

        GetPostsByUserId.listener = listener;

        HashMap<String, Object> params = new HashMap<>();
        params.put("user_id", user_id);

        ParseCloud.callFunctionInBackground("GetPostsByUserId", params, new FunctionCallback<HashMap<String, ArrayList<ParseObject>>>() {
            public void done(HashMap<String, ArrayList<ParseObject>> result, ParseException e) {
                if (e == null) {
                    GetPostsByUserId.listener.onUpdatePostsCompleted(result);
                } else {
                    GetPostsByUserId.listener.onUpdatePostsFailed(e.getMessage());
                }
            }
        });
    }
}
