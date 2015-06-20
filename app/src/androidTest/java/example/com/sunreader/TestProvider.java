package example.com.sunreader;


import android.test.AndroidTestCase;

public class TestProvider extends AndroidTestCase {
//    @Override
//    protected void setUp() throws Exception {
//        deleteAllRecords();
//    }
//
//    public void testInsertFeedDb() {
//        RSSFeedDBHelper dbHelper = new RSSFeedDBHelper(mContext);
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//
//        ContentValues testFeedValues = TestDB.createFakeFeedValues();
//        Uri insertedFeedUri = insertFakeFeedData(testFeedValues);
//        assertTrue(insertedFeedUri != null);
//
//        long feedRowId = ContentUris.parseId(insertedFeedUri);
//        assertTrue(feedRowId != -1);
//
//        Cursor cursor = getCursorFromUri(RSSFeedContract.FeedEntry.CONTENT_URI);
//        TestDB.validateCursor(cursor, testFeedValues);
//
//        cursor.close();
//    }
//
//    public void testInsertItemDb() {
//        RSSFeedDBHelper dbHelper = new RSSFeedDBHelper(mContext);
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//
//        // Insert feed
//        Uri insertedFeedUri = insertFakeFeedData(TestDB.createFakeFeedValues());
//        long insertedFeedRow = ContentUris.parseId(insertedFeedUri);
//
//        // Insert item
//        ContentValues testItemValues = TestDB.createFakeItemValues(insertedFeedRow);
//        Uri insertedItemUri = mContext.getContentResolver().insert(
//                RSSFeedContract.ItemEntry.CONTENT_URI,
//                testItemValues
//        );
//        assertTrue(insertedItemUri != null);
//
//        long insertedItemRow = ContentUris.parseId(insertedItemUri);
//        assertTrue(insertedItemRow != -1);
//
//        // Verify
//        Cursor cursor = getCursorFromUri(RSSFeedContract.ItemEntry.CONTENT_URI);
//        TestDB.validateCursor(cursor, testItemValues);
//    }
//
//    public void testDeleteDb() {
//        Cursor cursor = getCursorFromUri(RSSFeedContract.FeedEntry.CONTENT_URI);
//        if (!cursor.moveToFirst()) {
//            mContext.getContentResolver().insert(
//                    RSSFeedContract.FeedEntry.CONTENT_URI,
//                    TestDB.createFakeFeedValues()
//            );
//        }
//
//        cursor = getCursorFromUri(RSSFeedContract.FeedEntry.CONTENT_URI);
//        assertTrue(cursor.moveToFirst());
//
//        mContext.getContentResolver().delete(
//                RSSFeedContract.FeedEntry.CONTENT_URI,
//                null,
//                null
//        );
//
//        // Verify
//        cursor = getCursorFromUri(RSSFeedContract.FeedEntry.CONTENT_URI);
//        assertFalse(cursor.moveToFirst());
//        cursor.close();
//    }
//
//    public void testQueryFeedWithId() {
//        ContentValues testFeedValues = TestDB.createFakeFeedValues();
//        Uri insertedFeedUri = insertFakeFeedData(testFeedValues);
//        long feedRowId = ContentUris.parseId(insertedFeedUri);
//        assertTrue(feedRowId != -1);
//        Cursor cursor = mContext.getContentResolver().query(
//                RSSFeedContract.FeedEntry.buildFeedUri(feedRowId),
//                null,
//                null,
//                null,
//                null
//        );
//
//        TestDB.validateCursor(cursor, testFeedValues);
//    }
//
//    public void testQueryItemWithId() {
//        // Insert feed
//        ContentValues testFeedValues = TestDB.createFakeFeedValues();
//        Uri insertedFeedUri = insertFakeFeedData(testFeedValues);
//        long feedRowId = ContentUris.parseId(insertedFeedUri);
//        assertTrue(feedRowId != -1);
//
//        // Insert item based on feed
//        ContentValues testItemValues = TestDB.createFakeItemValues(feedRowId);
//        Uri insertedItemUri = mContext.getContentResolver().insert(
//                RSSFeedContract.ItemEntry.CONTENT_URI,
//                testItemValues
//        );
//        long itemRowId = ContentUris.parseId(insertedItemUri);
//        assertTrue(itemRowId != -1);
//
//        // Query
//        Cursor cursor = mContext.getContentResolver().query(
//                RSSFeedContract.ItemEntry.buildItemUri(itemRowId),
//                null,
//                null,
//                null,
//                null
//        );
//
//        TestDB.validateCursor(cursor, testItemValues);
//
//    }
//
//    public void testQueryItemOfFeed() {
//        // Insert feed
//        ContentValues testFeedValues = TestDB.createFakeFeedValues();
//        Uri insertedFeedUri = insertFakeFeedData(testFeedValues);
//        long feedRowId = ContentUris.parseId(insertedFeedUri);
//        assertTrue(feedRowId != -1);
//
//        // Insert item based on feed
//        ContentValues testItemValues = TestDB.createFakeItemValues(feedRowId);
//        Uri insertedItemUri = mContext.getContentResolver().insert(
//                RSSFeedContract.ItemEntry.CONTENT_URI,
//                testItemValues
//        );
//        long itemRowId = ContentUris.parseId(insertedItemUri);
//        assertTrue(itemRowId != -1);
//
//        // Query
//        Cursor cursor = mContext.getContentResolver().query(
//                RSSFeedContract.ItemEntry.buildItemWithFeedId(feedRowId),
//                null,
//                null,
//                null,
//                null
//        );
//
//        TestDB.validateCursor(cursor, testItemValues);
//    }
//
//
//
//
//    private void deleteAllRecords() {
//        mContext.getContentResolver().delete(
//                RSSFeedContract.FeedEntry.CONTENT_URI,
//                null,
//                null
//        );
//        mContext.getContentResolver().delete(
//                RSSFeedContract.ItemEntry.CONTENT_URI,
//                null,
//                null
//        );
//    }
//    private Cursor getCursorFromUri(Uri contentUri) {
//        Cursor cursor = mContext.getContentResolver().query(
//                contentUri,
//                null,
//                null,
//                null,
//                null
//        );
//        return cursor;
//    }
//
//    private Uri insertFakeFeedData(ContentValues testFeedValues) {
//        Uri insertedFeedUri = mContext.getContentResolver().insert(
//                RSSFeedContract.FeedEntry.CONTENT_URI,
//                testFeedValues
//        );
//
//        return insertedFeedUri;
//    }

}
