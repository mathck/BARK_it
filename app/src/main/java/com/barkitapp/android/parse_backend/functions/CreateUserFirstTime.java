package com.barkitapp.android.parse_backend.functions;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.barkitapp.android.events.UserIdRecievedEvent;
import com.barkitapp.android._core.services.UserId;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.HashMap;

import de.greenrobot.event.EventBus;

public class CreateUserFirstTime {

    public static void run(final Context context, final String device_id) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("device_id", device_id);

        ParseCloud.callFunctionInBackground("CreateUser", params, new FunctionCallback<ParseObject>() {
            public void done(ParseObject result, ParseException e) {
                if (e == null) {

                    String userId = result.getObjectId();

                    UserId.store(context, userId);
                    EventBus.getDefault().post(new UserIdRecievedEvent(userId));
                }
                else {
                    Log.d("ERROR", device_id);
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}

