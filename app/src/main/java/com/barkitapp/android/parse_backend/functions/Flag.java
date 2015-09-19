package com.barkitapp.android.parse_backend.functions;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.barkitapp.android.parse_backend.enums.ContentType;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;

import java.util.HashMap;

public class Flag {

    public static void run(final Context context, String user_id, String content_id, ContentType content_type, ParseGeoPoint current_location) {

        HashMap<String, Object> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("content_id", content_id);
        params.put("content_type", content_type.ordinal());
        params.put("current_location", current_location);

        ParseCloud.callFunctionInBackground("Flag", params, new FunctionCallback<Boolean>() {
            public void done(Boolean result, ParseException e) {
                if (e != null) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.d("ERROR", Log.getStackTraceString(e));
                }
                else {
                    if(result)
                        Toast.makeText(context, "Content flagged", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(context, "Already flagged", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}

