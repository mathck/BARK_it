package com.barkitapp.android.parse.functions;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.barkitapp.android.parse.enums.ContentType;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;

import org.json.JSONObject;

import java.util.HashMap;

public class Flag {

    public static void run(final Context context, String user_id, String content_id, ContentType content_type, ParseGeoPoint current_location) {

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("user_id", user_id);
        params.put("content_id", content_id);
        params.put("content_type", content_type.ordinal());
        params.put("current_location", current_location);

        ParseCloud.callFunctionInBackground("Flag", params, new FunctionCallback<JSONObject>() {
            public void done(JSONObject result, ParseException e) {
                if (e != null) {
                    Toast.makeText(context, "Already flagged", Toast.LENGTH_LONG).show();
                    Log.d("ERROR", Log.getStackTraceString(e));
                }
                else {
                    Toast.makeText(context, "Content flagged", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}

