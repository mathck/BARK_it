package com.barkitapp.android.parse.functions;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;

import java.util.HashMap;

public class UpdateReplies {

    public interface OnUpdateRepliesCompleted {
        void onUpdateRepliesCompleted(HashMap<String, Object> result);
        void onUpdateRepliesFailed(String error);
    }

    private static OnUpdateRepliesCompleted listener;

    public static void run(OnUpdateRepliesCompleted listener, String user_id, String post_id, ParseGeoPoint current_location) {

        UpdateReplies.listener = listener;

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("user_id", user_id);
        params.put("post_id", post_id);
        params.put("current_location", current_location);

        ParseCloud.callFunctionInBackground("UpdateReplies", params, new FunctionCallback<HashMap<String, Object>>() {
            public void done(HashMap<String, Object> result, ParseException e) {
                if (e == null) {
                    UpdateReplies.listener.onUpdateRepliesCompleted(result);
                } else {
                    UpdateReplies.listener.onUpdateRepliesFailed(e.getMessage());
                }
            }
        });
    }
}
