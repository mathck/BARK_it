package com.barkitapp.android.parse_backend.functions;

import android.content.Context;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.HashMap;

public class UpdateReplies {

    public interface OnUpdateRepliesCompleted {
        void onUpdateRepliesCompleted(HashMap<String, ArrayList<ParseObject>> result);
        void onUpdateRepliesFailed(String error);
    }

    private static OnUpdateRepliesCompleted listener;

    public static void run(final Context context, OnUpdateRepliesCompleted listener, String user_id, String post_id, ParseGeoPoint current_location) {

        UpdateReplies.listener = listener;

        HashMap<String, Object> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("post_id", post_id);
        params.put("current_location", current_location);

        ParseCloud.callFunctionInBackground("UpdateReplies", params, new FunctionCallback<HashMap<String, ArrayList<ParseObject>>>() {
            public void done(HashMap<String, ArrayList<ParseObject>> result, ParseException e) {
                if (e == null) {
                    UpdateReplies.listener.onUpdateRepliesCompleted(result);
                } else {
                    UpdateReplies.listener.onUpdateRepliesFailed(e.getMessage());
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
