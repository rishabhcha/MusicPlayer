package com.stare.out.olamusicplayer;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Created by rishabh on 16/12/17.
 */

public class OlaMusicPlayer extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
