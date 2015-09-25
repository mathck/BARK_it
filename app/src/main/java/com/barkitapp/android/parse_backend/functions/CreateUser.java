package com.barkitapp.android.parse_backend.functions;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.barkitapp.android._core.services.UserId;
import com.barkitapp.android.events.UserIdRecievedEvent;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;

public class CreateUser {

    public interface OnCreateUserCompleted {
        void onCreateUserCompleted(ParseObject result);
        void onCreateUserFailed(String error);
    }

    private static List<OnCreateUserCompleted> listener;

    public static void run(final Context context, final OnCreateUserCompleted listener, final String device_id) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("device_id", device_id);

        if(CreateUser.listener == null)
            CreateUser.listener = new ArrayList<>();

        CreateUser.listener.add(listener);

        ParseCloud.callFunctionInBackground("CreateUser", params, new FunctionCallback<ParseObject>() {
            public void done(ParseObject result, ParseException e) {
                if (e == null) {
                    for(OnCreateUserCompleted sub : CreateUser.listener) {
                        sub.onCreateUserCompleted(result);
                    }

                } else {
                    for(OnCreateUserCompleted sub : CreateUser.listener) {
                        sub.onCreateUserFailed(e.getMessage());
                    }
                }
                CreateUser.listener.clear();
            }
        });
    }
}

