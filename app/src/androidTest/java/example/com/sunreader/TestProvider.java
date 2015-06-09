package example.com.sunreader;


import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;

import example.com.sunreader.data.RSSFeedContract;
import example.com.sunreader.data.RSSFeedDBHelper;

public class TestProvider extends AndroidTestCase {
    @Override
    protected void setUp() throws Exception {
        deleteAllRecords();
    }

    public void testInsertDb() {
        RSSFeedDBHelper dbHelper = new RSSFeedDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testFeedValues = TestDB.createFakeFeedValues();
        Uri insertedFeedUri = insertFakeFeedData(testFeedValues);
        assertTrue(insertedFeedUri != null);

        long feedRowId = ContentUris.parseId(insertedFeedUri);
        assertTrue(feedRowId != -1);

        Cursor cursor = getCursorFromUri(RSSFeedContract.FeedEntry.CONTENT_URI);

        TestDB.validateCursor(cursor, testFeedValues);

        cursor.close();
    }

    public void testDeleteDb() {
        Cursor cursor = getCursorFromUri(RSSFeedContract.FeedEntry.CONTENT_URI);
        if (!cursor.moveToFirst()) {
            mContext.getContentResolver().insert(
                    RSSFeedContract.FeedEntry.CONTENT_URI,
                    TestDB.createFakeFeedValues()
            );
        }

        cursor = getCursorFromUri(RSSFeedContract.FeedEntry.CONTENT_URI);
        assertTrue(cursor.moveToFirst());

        mContext.getContentResolver().delete(
                RSSFeedContract.FeedEntry.CONTENT_URI,
                null,
                null
        );

        // Verify
        cursor = getCursorFromUri(RSSFeedContract.FeedEntry.CONTENT_URI);
        assertFalse(cursor.moveToFirst());
        cursor.close();
    }

    public void testQueryFeedWithId() {
        ContentValues testFeedValues = TestDB.createFakeFeedValues();
        Uri insertedFeedUri = insertFakeFeedData(testFeedValues);
        long feedRowId = ContentUris.parseId(insertedFeedUri);
        assertTrue(feedRowId != -1);

        Cursor cursor = mContext.getContentResolver().query(
                RSSFeedContract.FeedEntry.buildFeedUri(feedRowId),
                null,
                null,
                null,
                null
        );

        TestDB.validateCursor(cursor, testFeedValues);
    }

    private void deleteAllRecords() {
        mContext.getContentResolver().delete(
                RSSFeedContract.FeedEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                RSSFeedContract.ItemEntry.CONTENT_URI,
                null,
                null
        );
    }
    private Cursor getCursorFromUri(Uri contentUri) {
        Cursor cursor = mContext.getContentResolver().query(
                contentUri,
                null,
                null,
                null,
                null
        );
        return cursor;
    }

    private Uri insertFakeFeedData(ContentValues testFeedValues) {
        Uri insertedFeedUri = mContext.getContentResolver().insert(
                RSSFeedContract.FeedEntry.CONTENT_URI,
                testFeedValues
        );

        return insertedFeedUri;
    }


}
