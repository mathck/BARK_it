package com.barkitapp.android.base;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "bPaekY4XHBk0wDHQlhHwXPSZUTIwGI5m4s8vxcxt", "d7moOVAxLVbHfd6ybMW4UItPo5nvq7YmhgObNcyd");
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}
