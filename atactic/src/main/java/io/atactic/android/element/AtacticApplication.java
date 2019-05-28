package io.atactic.android.element;

import android.app.Application;
import android.content.Context;

public class AtacticApplication extends Application {

    private static AtacticApplication instance;
    private static Context appContext;


    public static AtacticApplication getInstance() {
        return instance;
    }

    public static Context getContext() {
        return appContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        appContext = getApplicationContext();
    }

}
