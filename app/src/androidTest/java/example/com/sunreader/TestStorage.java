package example.com.sunreader;


import android.content.Context;
import android.test.AndroidTestCase;

import java.io.File;
import java.io.IOException;

import example.com.sunreader.data.InternalStorageHandler;
import example.com.sunreader.data.RSSFeedContract;

public class TestStorage extends AndroidTestCase {
    public void test() throws IOException {
        File dir = mContext.getDir(InternalStorageHandler.ITEM_IMAGE_DIRECTORY_NAME, Context.MODE_PRIVATE);
        File createdFile = new File(dir, "99.jpg");

        if (!createdFile.exists())
            createdFile.createNewFile();

        assertTrue(createdFile.exists());

        // Delete file
        mContext.getContentResolver().delete(
                RSSFeedContract.ItemEntry.CONTENT_URI,
                RSSFeedContract.ItemEntry._ID + " = ?",
                new String[] {"99"}
        );
        assertTrue(!createdFile.exists());
    }

}
