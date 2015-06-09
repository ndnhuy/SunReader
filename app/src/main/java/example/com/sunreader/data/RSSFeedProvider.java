package example.com.sunreader.data;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;


public class RSSFeedProvider extends ContentProvider {
    private RSSFeedDBHelper mOpenHelper;
    private static final UriMatcher uriMatcher = UriMatcherBuilder.build();

    @Override
    public boolean onCreate() {
        mOpenHelper = new RSSFeedDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        Cursor returnCursor = null;
        switch (match) {
            case UriMatcherBuilder.FEED: {
                returnCursor = mOpenHelper.getReadableDatabase().query(
                        RSSFeedContract.FeedEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case UriMatcherBuilder.FEED_ID: {
                returnCursor = mOpenHelper.getReadableDatabase().query(
                        RSSFeedContract.FeedEntry.TABLE_NAME,
                        projection,
                        RSSFeedContract.FeedEntry._ID + " = " + "?",
                        new String[]{Long.toString(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return returnCursor;

    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        Uri returnUri = null;

        switch (match) {
            case UriMatcherBuilder.FEED: {
                long _id = db.insert(RSSFeedContract.FeedEntry.TABLE_NAME, null, contentValues);
                if (_id > 0) {
                    returnUri = RSSFeedContract.FeedEntry.buildFeedUri(_id);
                }
                else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int rowsDeleted = 0;
        switch (match) {
            case UriMatcherBuilder.FEED: {
                rowsDeleted = db.delete(
                        RSSFeedContract.FeedEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            }
        }

        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
