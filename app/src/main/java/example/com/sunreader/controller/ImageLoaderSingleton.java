package example.com.sunreader.controller;


import android.content.Context;

import example.com.sunreader.libs.ImageLoader;

public class ImageLoaderSingleton {
    private static ImageLoader mImageLoader;

    public static synchronized ImageLoader getInstance(Context context) {
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(context);
        }

        return mImageLoader;
    }
}
