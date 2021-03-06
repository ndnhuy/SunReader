package example.com.sunreader.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class RSSFeedDBHelper extends SQLiteOpenHelper {
    private static final String LOG_TAG = RSSFeedDBHelper.class.getSimpleName();
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "rssfeed.db";

    public RSSFeedDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.v(LOG_TAG, "CREATE TABLE FEED AND ITEM");
        final String SQL_CREATE_FEED_TABLE = "CREATE TABLE " + RSSFeedContract.FeedEntry.TABLE_NAME
                + " (" +
                RSSFeedContract.FeedEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                RSSFeedContract.FeedEntry.COLUMN_TITLE + " TEXT NOT NULL," +
                RSSFeedContract.FeedEntry.COLUMN_LINK + " TEXT UNIQUE NOT NULL," +
                RSSFeedContract.FeedEntry.COLUMN_FEED_URL + " TEXT UNIQUE NOT NULL, " +
                "UNIQUE (" + RSSFeedContract.FeedEntry.COLUMN_LINK + ") ON CONFLICT IGNORE" +
                " )";
        final String SQL_CREATE_ITEM_TABLE = "CREATE TABLE " + RSSFeedContract.ItemEntry.TABLE_NAME
                + " (" +
                RSSFeedContract.ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                RSSFeedContract.ItemEntry.COLUMN_TITLE + " TEXT NOT NULL," +
                RSSFeedContract.ItemEntry.COLUMN_LINK + " TEXT UNIQUE NOT NULL," +
                RSSFeedContract.ItemEntry.COLUMN_CONTENT + " TEXT NOT NULL," +
                RSSFeedContract.ItemEntry.COLUMN_CONTENT_SNIPPET + " TEXT NOT NULL," +
                RSSFeedContract.ItemEntry.COLUMN_AUTHOR + " TEXT NOT NULL," +
                RSSFeedContract.ItemEntry.COLUMN_PUBLISHED_DATETEXT + " TEXT NOT NULL," +
                RSSFeedContract.ItemEntry.COLUMN_FEED_ID + " INTEGER NOT NULL," +
                RSSFeedContract.ItemEntry.COLUMN_READ + " INTERGER NOT NULL," +
                RSSFeedContract.ItemEntry.COLUMN_SAVED + " INTEGER NOT NULL," +
                " FOREIGN KEY (" + RSSFeedContract.ItemEntry.COLUMN_FEED_ID + ") REFERENCES " +
                RSSFeedContract.FeedEntry.TABLE_NAME + " (" + RSSFeedContract.FeedEntry._ID + "), " +

                " UNIQUE (" + RSSFeedContract.ItemEntry.COLUMN_LINK + ") ON CONFLICT IGNORE" +
                ")";

        final String SQL_CREATE_TRIGGER = "CREATE TRIGGER AUTO_DELETE_ITEM AFTER INSERT "
                + "ON " + RSSFeedContract.ItemEntry.TABLE_NAME
                + " BEGIN DELETE FROM " + RSSFeedContract.ItemEntry.TABLE_NAME
                + " WHERE " + RSSFeedContract.ItemEntry.COLUMN_PUBLISHED_DATETEXT
                + " < " + "DATETIME(NEW." + RSSFeedContract.ItemEntry.COLUMN_PUBLISHED_DATETEXT
                + ", '-2 days'); END;";

        sqLiteDatabase.execSQL(SQL_CREATE_FEED_TABLE);
        Log.v(LOG_TAG, "Create Feed table " + SQL_CREATE_FEED_TABLE);

        sqLiteDatabase.execSQL(SQL_CREATE_ITEM_TABLE);
        Log.v(LOG_TAG, "Create Item table " + SQL_CREATE_ITEM_TABLE);

        sqLiteDatabase.execSQL(SQL_CREATE_TRIGGER);
        Log.v(LOG_TAG, "Create Trigger " + SQL_CREATE_TRIGGER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RSSFeedContract.FeedEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RSSFeedContract.ItemEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
