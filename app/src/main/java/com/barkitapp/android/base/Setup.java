package com.barkitapp.android.base;

import com.barkitapp.android.parse.Connection;
import com.orm.SugarApp;

public class Setup extends SugarApp {
    @Override
    public void onCreate() {
        super.onCreate();

        // establish parse connection
        Connection.Connect(this);
    }
}
