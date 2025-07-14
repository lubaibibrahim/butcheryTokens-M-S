package com.nesto.butcharytokens;

import android.app.Application;

/**
 * Created by LUBAIB on 24-Mar-24.
 */
public class CustomFontApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        TypefaceUtils.setDefaultFont(this, "DEFAULT", "fonts/Roboto-Regular.ttf");
        TypefaceUtils.setDefaultFont(this, "MONOSPACE", "fonts/Roboto-Regular.ttf");
        TypefaceUtils.setDefaultFont(this, "SERIF", "fonts/Roboto-Regular.ttf");
        TypefaceUtils.setDefaultFont(this, "SANS_SERIF", "fonts/Roboto-Regular.ttf");
    }
}