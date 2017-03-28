package com.jamarfal.instagramndk;

import android.app.Application;
import android.graphics.Bitmap;

/**
 * Created by jamarfal on 28/3/17.
 */

public class BaseApplication extends Application {

    private static BaseApplication baseApplication;

    private Bitmap bitmapToShare;

    public static BaseApplication get() {
        return baseApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        baseApplication = this;

    }

    public Bitmap getBitmapToShare() {
        return bitmapToShare;
    }

    public void setBitmapToShare(Bitmap bitmapToShare) {
        this.bitmapToShare = bitmapToShare;
    }
}
