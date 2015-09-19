package com.barkitapp.android.parse_backend.connection;

import android.app.Application;

import com.barkitapp.android._core.services.UserId;
import com.parse.Parse;
import com.parse.ParseInstallation;

public class Connection {

    public static void Connect(Application application) {

        Parse.enableLocalDatastore(application);
        Parse.initialize(application, ParseKey.getAppId(), ParseKey.getClientKey());
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("user_id", UserId.get(application));
        installation.saveInBackground();
    }
}
