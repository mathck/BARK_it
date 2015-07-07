package com.barkitapp.android.parse;

import android.app.Application;

import com.barkitapp.android.core.utility.Constants;
import com.parse.Parse;
import com.parse.ParseInstallation;

public class Connection {

    public static void Connect(Application application) {

        Parse.enableLocalDatastore(application);
        Parse.initialize(application, Constants.PARSE_APPLICATION_ID, Constants.PARSE_CLIENT_KEY);
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}
