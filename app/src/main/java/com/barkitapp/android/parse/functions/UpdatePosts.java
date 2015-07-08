package com.barkitapp.android.parse.functions;

import com.barkitapp.android.parse.enums.Order;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;

import java.util.HashMap;

public class UpdatePosts {

    public interface OnUpdatePostsCompleted {
        void onUpdatePostsCompleted(HashMap<String, Object> result);
        void onUpdatePostsFailed(String error);
    }

    private static OnUpdatePostsCompleted listener;

    public static void Run(OnUpdatePostsCompleted listener, String user_id, ParseGeoPoint current_location, ParseGeoPoint chosen_location, int radius, int max_count, Order order, boolean resetUserCache) {

        UpdatePosts.listener = listener;

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("user_id", user_id);
        params.put("current_location", current_location);
        params.put("chosen_location", chosen_location);
        params.put("radius", radius);
        params.put("max_count", max_count);
        params.put("order", order.ordinal());
        params.put("resetUserCache", resetUserCache);

        ParseCloud.callFunctionInBackground("UpdatePosts", params, new FunctionCallback<HashMap<String, Object>>() {
            public void done(HashMap<String, Object> result, ParseException e) {
                if (e == null) {
                    UpdatePosts.listener.onUpdatePostsCompleted(result);
                } else {
                    UpdatePosts.listener.onUpdatePostsFailed(e.getMessage());
                }
            }
        });
    }

}
