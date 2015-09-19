package com.barkitapp.android.parse_backend.functions;

import android.content.Context;
import android.widget.Toast;

import com.barkitapp.android._core.utility.LastRefresh;
import com.barkitapp.android.parse_backend.enums.Order;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.HashMap;

public class UpdatePosts {

    public interface OnUpdatePostsCompleted {
        void onUpdatePostsCompleted(HashMap<String, ArrayList<ParseObject>> result);
        void onUpdatePostsFailed(String error);
    }

    private static OnUpdatePostsCompleted listener;

    @Deprecated
    public static void run(final Context context, OnUpdatePostsCompleted listener, String user_id, ParseGeoPoint current_location, ParseGeoPoint chosen_location, int radius, int max_count, Order order, boolean resetUserCache) {

        LastRefresh.now(context);

        UpdatePosts.listener = listener;

        HashMap<String, Object> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("current_location", current_location);
        params.put("chosen_location", chosen_location);
        params.put("radius", radius);
        params.put("max_count", max_count);
        params.put("order", order.ordinal());
        params.put("resetUserCache", resetUserCache);

        ParseCloud.callFunctionInBackground("UpdatePosts", params, new FunctionCallback<HashMap<String, ArrayList<ParseObject>>>() {
            public void done(HashMap<String, ArrayList<ParseObject>> result, ParseException e) {
                if (e == null) {
                    UpdatePosts.listener.onUpdatePostsCompleted(result);
                } else {
                    UpdatePosts.listener.onUpdatePostsFailed(e.getMessage());
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
