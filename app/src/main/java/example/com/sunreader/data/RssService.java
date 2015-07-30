package example.com.sunreader.data;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.Html;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import example.com.sunreader.value_object.RssFeed;

public class RssService {
    private static final String LOG_TAG = RssService.class.getSimpleName();
    public static final String SEARCH_BY_KEYWORDS = "load";
    public static final String SEARCH_BY_LINK = "url";
    public static final String JSON_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss Z";

    public static RssFeed[] getFeedFromJSONInSearchByKeyword(String JsonRssString) throws JSONException {
        JSONObject RssJSON = new JSONObject(JsonRssString);
        JSONObject responseDataJSON = RssJSON.getJSONObject("responseData");
        JSONArray feedsArrJSON = responseDataJSON.getJSONArray("entries");

        List<RssFeed> feeds = new ArrayList<RssFeed>();
        for (int i = 0; i < feedsArrJSON.length(); i++) {
            JSONObject feedJSON = feedsArrJSON.getJSONObject(i);

            String rawTitle = Html.fromHtml(feedJSON.getString("title")).toString();
            String title = rawTitle.split("\\|")[0];

            feeds.add(new RssFeed(
                    title,
                    feedJSON.getString("link"),
                    feedJSON.getString("url"),
                    ImageHandler.BASE_FAVICON_URL + feedJSON.getString("link")
            ));
        }

        RssFeed[] rssFeedArr = new RssFeed[feeds.size()];
        feeds.toArray(rssFeedArr);
        return rssFeedArr;
    }

    private static URL buildURLBasedOnKeywords(String keywords) throws MalformedURLException {
        final String RSS_BASE_URL = "https://ajax.googleapis.com/ajax/services/feed/find?v=1.0&";
        final String QUERY_PARAM = "q";

        Uri builtUri = Uri.parse(RSS_BASE_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAM, keywords)
                .build();
        URL url = new URL(builtUri.toString());

        return url;
    }

    private static URL buildURLBasedOnURL(String urlStr) throws MalformedURLException {
        final String RSS_BASE_URL = "https://ajax.googleapis.com/ajax/services/feed/load?v=1.0&";
        final String QUERY_PARAM = "q";
        final String NUM_PARAM = "num";

        Uri builtUri = Uri.parse(RSS_BASE_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAM, urlStr)
                .appendQueryParameter(NUM_PARAM, "-1")
                .build();
        URL url = new URL(builtUri.toString());
        return url;
    }

    public static StringBuilder downloadFeedJSON(String searchType, String urlStr) throws IOException {
        URL url = null;
        if (searchType == SEARCH_BY_KEYWORDS) {
            url = buildURLBasedOnKeywords(urlStr);
        }
        else if (searchType == SEARCH_BY_LINK) {
            url = buildURLBasedOnURL(urlStr);
        }

        // Create a request to Google Feed API, open the connection
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        // Read the input stream into a String
        InputStream inputStream = connection.getInputStream();
        if (inputStream == null) {
            return null;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder builder = new StringBuilder();

        String line = reader.readLine();
        int i = 0;
        while (line != null) {
            builder.append(line);
            line = reader.readLine();
        }

        if (builder.length() == 0) {
            return null;
        }

        return builder;
    }

    public static void insertItemIntoDatabase(Context context, String JSONRssString, long feedRowId) throws JSONException {
        JSONObject RssJSON = new JSONObject(JSONRssString);
        JSONObject responseDataJSON = RssJSON.getJSONObject("responseData");
        JSONObject feedJSON = responseDataJSON.getJSONObject("feed");
        JSONArray entriesJSON = feedJSON.getJSONArray("entries");

        for (int i = 0; i < entriesJSON.length(); i++) {
            JSONObject itemJSON = entriesJSON.getJSONObject(i);
            ContentValues itemValues = createContentValuesFromJSONItem(context, itemJSON, feedRowId);
            if (!itemAlreadyExist(context, itemJSON.getString(RSSFeedContract.ItemEntry.COLUMN_LINK))) {
                Uri uri = context.getContentResolver().insert(
                        RSSFeedContract.ItemEntry.CONTENT_URI,
                        itemValues
                );
            }
        }
    }

    private static boolean itemAlreadyExist(Context context, String itemLink) {
        // Check if exist
        Cursor itemsCursor = context.getContentResolver().query(
                RSSFeedContract.ItemEntry.CONTENT_URI,
                new String[]{RSSFeedContract.ItemEntry.COLUMN_FEED_ID},
                RSSFeedContract.ItemEntry.COLUMN_LINK + " = ?",
                new String[]{itemLink},
                null
        );
        if (itemsCursor.moveToFirst()) {
            return true;
        }
        else {
            return false;
        }
    }

    private static ContentValues createContentValuesFromJSONItem(Context context, JSONObject itemJSON, long feedRowId) throws JSONException {
        ContentValues itemValues = new ContentValues();
        itemValues.put(
                RSSFeedContract.ItemEntry.COLUMN_TITLE,
                itemJSON.getString(RSSFeedContract.ItemEntry.COLUMN_TITLE)
        );
        itemValues.put(
                RSSFeedContract.ItemEntry.COLUMN_LINK,
                itemJSON.getString(RSSFeedContract.ItemEntry.COLUMN_LINK)
        );
        itemValues.put(
                RSSFeedContract.ItemEntry.COLUMN_AUTHOR,
                itemJSON.getString(RSSFeedContract.ItemEntry.COLUMN_AUTHOR)
        );
        itemValues.put(
                RSSFeedContract.ItemEntry.COLUMN_READ,
                0
        );

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(JSON_DATE_FORMAT);
            Date date = simpleDateFormat.parse(itemJSON.getString(RSSFeedContract.ItemEntry.COLUMN_PUBLISHED_DATETEXT));
            simpleDateFormat = new SimpleDateFormat(DateConverter.DATE_FORMAT_IN_DB);

            itemValues.put(
                    RSSFeedContract.ItemEntry.COLUMN_PUBLISHED_DATETEXT,
                    simpleDateFormat.format(date)
            );
        } catch (ParseException e) {
            e.printStackTrace();
        }

        itemValues.put(
                RSSFeedContract.ItemEntry.COLUMN_CONTENT_SNIPPET,
                itemJSON.getString(RSSFeedContract.ItemEntry.COLUMN_CONTENT_SNIPPET)
        );
        itemValues.put(
                RSSFeedContract.ItemEntry.COLUMN_CONTENT,
                itemJSON.getString(RSSFeedContract.ItemEntry.COLUMN_CONTENT)
        );
        itemValues.put(
                RSSFeedContract.ItemEntry.COLUMN_SAVED,
                0
        );
        itemValues.put(
                RSSFeedContract.ItemEntry.COLUMN_FEED_ID,
                feedRowId
        );

        return itemValues;
    }


}
