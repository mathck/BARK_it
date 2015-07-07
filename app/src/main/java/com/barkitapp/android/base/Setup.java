package com.barkitapp.android.base;

import android.app.Application;

import com.barkitapp.android.parse.Connection;

public class Setup extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // establish parse connection
        Connection.Connect(this);
    }
}
