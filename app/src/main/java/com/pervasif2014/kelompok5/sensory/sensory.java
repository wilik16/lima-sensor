package com.pervasif2014.kelompok5.sensory;

import android.app.Application;
import android.content.Context;

/**
 * Created by Wilik on 10/12/2014.
 */
public class sensory extends Application {

    private static Context context;

    public void onCreate(){
        super.onCreate();
        sensory.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return sensory.context;
    }
}