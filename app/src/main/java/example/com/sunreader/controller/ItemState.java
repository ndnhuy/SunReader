package example.com.sunreader.controller;


import android.content.ContentValues;
import android.content.Context;

import example.com.sunreader.data.RSSFeedContract;

public class ItemState {
    public static final int SAVED = 1;
    public static final int UNSAVED = 0;
    public static final int READ = 1;
    public static final int UNREAD = 0;

    public static void saveForLater(Context context, long itemId) {
        ContentValues values = new ContentValues();
        values.put(RSSFeedContract.ItemEntry.COLUMN_SAVED, 1);

        context.getContentResolver().update(
                RSSFeedContract.ItemEntry.CONTENT_URI,
                values,
                RSSFeedContract.ItemEntry._ID + " = ?",
                new String[]{Long.toString(itemId)}
        );
    }

    public static void unsaved(Context context, long itemId) {
        ContentValues values = new ContentValues();
        values.put(RSSFeedContract.ItemEntry.COLUMN_SAVED, 0);

        context.getContentResolver().update(
                RSSFeedContract.ItemEntry.CONTENT_URI,
                values,
                RSSFeedContract.ItemEntry._ID + " = ?",
                new String[]{Long.toString(itemId)}
        );
    }
    public static void markAsUnread(Context context, long itemId) {
        ContentValues values = new ContentValues();
        values.put(RSSFeedContract.ItemEntry.COLUMN_READ, 0);

        context.getContentResolver().update(
                RSSFeedContract.ItemEntry.CONTENT_URI,
                values,
                RSSFeedContract.ItemEntry._ID + " = ?",
                new String[]{Long.toString(itemId)}
        );
    }

}
