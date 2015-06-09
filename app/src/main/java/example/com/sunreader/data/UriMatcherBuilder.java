package example.com.sunreader.data;

import android.content.UriMatcher;

public class UriMatcherBuilder {
    public static final int FEED = 100;
    public static final int FEED_ID = 101;
    public static final int ITEM = 102;
    public static final int ITEM_ID = 103;

    public static UriMatcher build() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = RSSFeedContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, RSSFeedContract.PATH_FEED, FEED);
        matcher.addURI(authority, RSSFeedContract.PATH_FEED + "/#", FEED_ID);
        matcher.addURI(authority, RSSFeedContract.PATH_ITEM, ITEM);
        matcher.addURI(authority, RSSFeedContract.PATH_ITEM + "/#", ITEM_ID);
        return matcher;
    }
}
