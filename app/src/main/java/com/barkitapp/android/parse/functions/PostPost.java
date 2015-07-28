package com.barkitapp.android.parse.functions;

import android.content.Context;
import android.widget.Toast;

import com.barkitapp.android.Messages.RequestUpdatePostsEvent;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;

import org.json.JSONObject;

import java.util.HashMap;

import de.greenrobot.event.EventBus;

public class PostPost {

    public static void run(final Context context, String user_id, ParseGeoPoint chosen_location, String text, int badge) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("user_id", user_id);
        params.put("chosen_location", chosen_location);
        params.put("text", text);
        params.put("badge", badge);

        ParseCloud.callFunctionInBackground("PostPost", params, new FunctionCallback<JSONObject>() {
            public void done(JSONObject result, ParseException e) {
                if (e == null) {
                    EventBus.getDefault().post(new RequestUpdatePostsEvent());
                }
                else {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}

