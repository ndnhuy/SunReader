package example.com.sunreader;


import android.content.ContentValues;
import android.test.AndroidTestCase;

import java.io.IOException;

import example.com.sunreader.data.RSSFeedContract;

public class TestStorage extends AndroidTestCase {
    public void test() throws IOException {
        ContentValues itemValues = new ContentValues();
        itemValues.put(
                RSSFeedContract.ItemEntry.COLUMN_TITLE,
                "1"
        );
        itemValues.put(
                RSSFeedContract.ItemEntry.COLUMN_LINK,
                "2"
        );
        itemValues.put(
                RSSFeedContract.ItemEntry.COLUMN_AUTHOR,
                "3"
        );
        itemValues.put(
                RSSFeedContract.ItemEntry.COLUMN_READ,
                0
        );
        itemValues.put(
                RSSFeedContract.ItemEntry.COLUMN_PUBLISHED_DATETEXT,
                "2015-06-24 18:47:24"
        );
        itemValues.put(
                RSSFeedContract.ItemEntry.COLUMN_CONTENT_SNIPPET,
                "4"
        );
        itemValues.put(
                RSSFeedContract.ItemEntry.COLUMN_CONTENT,
                "5"
        );
        itemValues.put(
                RSSFeedContract.ItemEntry.COLUMN_SAVED,
                0
        );
        itemValues.put(
                RSSFeedContract.ItemEntry.COLUMN_FEED_ID,
                9
        );
        mContext.getContentResolver().insert(
                RSSFeedContract.ItemEntry.CONTENT_URI,
                itemValues
        );

        itemValues = new ContentValues();
        itemValues.put(
                RSSFeedContract.ItemEntry.COLUMN_TITLE,
                "11"
        );
        itemValues.put(
                RSSFeedContract.ItemEntry.COLUMN_LINK,
                "22"
        );
        itemValues.put(
                RSSFeedContract.ItemEntry.COLUMN_AUTHOR,
                "33"
        );
        itemValues.put(
                RSSFeedContract.ItemEntry.COLUMN_READ,
                0
        );
        itemValues.put(
                RSSFeedContract.ItemEntry.COLUMN_PUBLISHED_DATETEXT,
                "2015-06-27 18:47:24"
        );
        itemValues.put(
                RSSFeedContract.ItemEntry.COLUMN_CONTENT_SNIPPET,
                "44"
        );
        itemValues.put(
                RSSFeedContract.ItemEntry.COLUMN_CONTENT,
                "55"
        );
        itemValues.put(
                RSSFeedContract.ItemEntry.COLUMN_SAVED,
                0
        );
        itemValues.put(
                RSSFeedContract.ItemEntry.COLUMN_FEED_ID,
                9
        );
        mContext.getContentResolver().insert(
                RSSFeedContract.ItemEntry.CONTENT_URI,
                itemValues
        );

    }

}
