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

public class UpdatePostsLat {

    private static UpdatePosts.OnUpdatePostsCompleted listener;

    public static void run(final Context context, UpdatePosts.OnUpdatePostsCompleted listener, String user_id, ParseGeoPoint current_location, ParseGeoPoint chosen_location, int max_count, final Order order, boolean resetUserCache) {

        if(order.equals(Order.MY_BARKS)) {
            GetPostsByUserId.run(context, listener, user_id);
            return;
        }

        if(order.equals(Order.MY_REPLIES)) {
            GetRepliesByUserId.run(context, listener, user_id);
            return;
        }

        LastRefresh.now(context);

        UpdatePostsLat.listener = listener;

        HashMap<String, Object> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("current_location", current_location);
        params.put("chosen_location", chosen_location);
        params.put("southwest", DistanceHelper.GetOffsetLocation(current_location, -InternalAppData.getLong(context, SharedPrefKeys.RADIUS)));
        params.put("northeast", DistanceHelper.GetOffsetLocation(current_location, InternalAppData.getLong(context, SharedPrefKeys.RADIUS)));
        params.put("max_count", max_count);
        params.put("order", order.ordinal());
        params.put("resetUserCache", resetUserCache);

        ParseCloud.callFunctionInBackground("UpdatePostsLat", params, new FunctionCallback<HashMap<String, ArrayList<ParseObject>>>() {
            public void done(HashMap<String, ArrayList<ParseObject>> result, ParseException e) {
                if (e == null) {
                    UpdatePostsLat.listener.onUpdatePostsCompleted(result);
                } else {
                    UpdatePostsLat.listener.onUpdatePostsFailed(e.getMessage());
                }
            }
        });
    }
}
