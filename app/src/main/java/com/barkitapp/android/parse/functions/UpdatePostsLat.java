package com.barkitapp.android.parse.functions;

import android.content.Context;
import android.widget.Toast;

import com.barkitapp.android.core.services.InternalAppData;
import com.barkitapp.android.core.utility.LastRefresh;
import com.barkitapp.android.core.utility.SharedPrefKeys;
import com.barkitapp.android.parse.enums.Order;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;

import java.util.HashMap;

public class UpdatePostsLat {

    private static UpdatePosts.OnUpdatePostsCompleted listener;
    private static double SOUTH_WEST_RANGE = -0.05d;
    private static double NORTH_EAST_RANGE = 0.05d;

    //private static final int times = 26;
    //private static long[] starttime = new long[times];

    public static void run(final Context context, UpdatePosts.OnUpdatePostsCompleted listener, String user_id, ParseGeoPoint current_location, ParseGeoPoint chosen_location, int max_count, Order order, boolean resetUserCache) {

        LastRefresh.now(context);

        UpdatePostsLat.listener = listener;

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("user_id", user_id);
        params.put("current_location", current_location);
        params.put("chosen_location", chosen_location);
        params.put("southwest", boundRect(current_location, SOUTH_WEST_RANGE));
        params.put("northeast", boundRect(current_location, NORTH_EAST_RANGE));
        params.put("max_count", max_count);
        params.put("order", order.ordinal());
        params.put("resetUserCache", resetUserCache);

        //starttime[i] = System.currentTimeMillis();

        //final int finalI = i;
        ParseCloud.callFunctionInBackground("UpdatePostsLat", params, new FunctionCallback<HashMap<String, Object>>() {
            public void done(HashMap<String, Object> result, ParseException e) {
                //Log.e("TIME", "Request " + finalI + ": " + (System.currentTimeMillis() - starttime[finalI]));
                if (e == null) {
                    UpdatePostsLat.listener.onUpdatePostsCompleted(result);
                } else {
                    UpdatePostsLat.listener.onUpdatePostsFailed(e.getMessage());
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private static ParseGeoPoint boundRect(ParseGeoPoint location, double range) {
        ParseGeoPoint result = new ParseGeoPoint();
        result.setLatitude(location.getLatitude() + range);
        result.setLongitude(location.getLongitude() + range);

        return result;
    }
}
