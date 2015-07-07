package com.barkitapp.android.parse.functions;

import android.util.Log;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;

import org.json.JSONObject;

import java.util.HashMap;

public class ResetUserCache {

    public static void Run(String user_id) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("user_id", user_id);

        ParseCloud.callFunctionInBackground("ResetUserCache", params, new FunctionCallback<JSONObject>() {
            public void done(JSONObject result, ParseException e) {
                if (e != null) {
                    Log.d("ERROR", Log.getStackTraceString(e));
                }
            }
        });
    }
}

