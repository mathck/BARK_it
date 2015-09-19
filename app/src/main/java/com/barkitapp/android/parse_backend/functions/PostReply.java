package com.barkitapp.android.parse_backend.functions;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.barkitapp.android.events.RequestUpdateRepliesEvent;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;

import org.json.JSONObject;

import java.util.HashMap;

import de.greenrobot.event.EventBus;

public class PostReply {

    public static void run(final Context context, String user_id, String post_id, ParseGeoPoint current_location, ParseGeoPoint chosen_location, String text, int badge) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("post_id", post_id);
        params.put("current_location", current_location);
        params.put("chosen_location", chosen_location);
        params.put("text", text);
        params.put("badge", badge);

        ParseCloud.callFunctionInBackground("PostReply", params, new FunctionCallback<JSONObject>() {
            public void done(JSONObject result, ParseException e) {
                if (e != null) {
                    Log.d("ERROR", Log.getStackTraceString(e));
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                }
                else {
                    EventBus.getDefault().post(new RequestUpdateRepliesEvent());
                }
            }
        });
    }
}

