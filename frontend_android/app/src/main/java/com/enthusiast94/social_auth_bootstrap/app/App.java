package com.enthusiast94.social_auth_bootstrap.app;

import android.app.Application;
import android.content.Context;

/**
 * Created by manas on 23-08-2015.
 */

public class App extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        context = this;
    }

    public static Context getAppContext() {
        return context;
    }
}
