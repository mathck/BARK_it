package com.barkitapp.android.parse_backend.functions;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.barkitapp.android.parse_backend.enums.ContentType;
import com.barkitapp.android.parse_backend.enums.VoteType;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;

import org.json.JSONObject;

import java.util.HashMap;

public class PostVote {

    public static void run(final Context context, String user_id, String content_id, ContentType content_type, ParseGeoPoint current_location, VoteType vote_type) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("content_id", content_id);
        params.put("content_type", content_type.ordinal());
        params.put("current_location", current_location);
        params.put("vote_type", vote_type.ordinal());

        ParseCloud.callFunctionInBackground("PostVote", params, new FunctionCallback<JSONObject>() {
            public void done(JSONObject result, ParseException e) {
                if (e != null) {
                    Log.d("ERROR", Log.getStackTraceString(e));
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}

