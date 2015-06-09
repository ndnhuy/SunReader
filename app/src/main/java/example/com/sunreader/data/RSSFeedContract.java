package example.com.sunreader.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class RSSFeedContract {
    public static final String CONTENT_AUTHORITY = "example.com.sunreader";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_FEED = "feed";
    public static final String PATH_ITEM = "item";

    public static final class FeedEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FEED).build();

        public static final String TABLE_NAME = "feed";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_LINK = "link";

        public static Uri buildFeedUri(long id) {
            Uri uri = ContentUris.withAppendedId(CONTENT_URI, id);
            return uri;
        }
    }

    public static final class ItemEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ITEM).build();
        public static final String TABLE_NAME = "item";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_LINK = "link";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_CONTENT_SNIPPET = "content_snippet";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_PUBLISHED_DATETEXT = "published_date";
        public static final String COLUMN_FEED_ID = "feed_id";

        public static Uri buildItemUri(long id) {
            Uri uri = ContentUris.withAppendedId(CONTENT_URI, id);
            return uri;
        }

        public static Uri buildItemWithFeedId(long id) {
            Uri uri = CONTENT_URI.buildUpon().appendQueryParameter(
                                                ItemEntry.COLUMN_FEED_ID,
                                                Long.toString(id)
                                                ).build();
            return uri;
        }

        public static String getFeedIdFromUri(Uri uri) {
            return uri.getQueryParameter(ItemEntry.COLUMN_FEED_ID);
        }
    }
}
