package example.com.sunreader;


import android.content.Context;
import android.test.AndroidTestCase;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import example.com.sunreader.data.ImageHandler;
import example.com.sunreader.data.InternalStorageHandler;

public class TestStorage extends AndroidTestCase {
    public void test() throws IOException {
        File directory = mContext.getDir(InternalStorageHandler.FEED_ICON_DIRECTORY_NAME, Context.MODE_PRIVATE);

        new ImageHandler(mContext).saveImage("http://www.google.com/s2/favicons?domain=http://www.androidcentral.com",
                InternalStorageHandler.FEED_ICON_DIRECTORY_NAME,
                "1.jpg");


        File f2 = new File(directory, "1.jpg");
        assertTrue(f2.exists());


//        File f = new File(directory, "2.jpg");
//
//        assertTrue(!f.exists());
//        f.createNewFile();
//        assertTrue(f.exists());
//        //FileOutputStream fos = new FileOutputStream(f);
//
//
//        File f2 = new File(InternalStorageHandler.FEED_ICON_DIRECTORY_NAME, "2.jpg");
//
        File[] files = directory.listFiles();
        for (int i = 0; i < files.length; i++) {

            Log.v("TEST", "List files: " + files[i].getName());
        }
//
//        Log.v("TEST", f2.getName());
//
//        // Create imageDir
////        File mypath=new File(directory,name);
//        //assertTrue(!f.exists());
    }
}
