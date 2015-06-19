package example.com.sunreader.data;


import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.text.Html;
import android.util.Log;

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
import java.util.ArrayList;
import java.util.List;

import example.com.sunreader.value_object.RssFeed;

public class RssService {
    private static final String LOG_TAG = RssService.class.getSimpleName();
    public static final String SEARCH_BY_KEYWORDS = "load";
    public static final String SEARCH_BY_LINK = "url";

//    public static RssFeed getFeedFromJSONInSearchByLink(String JsonRssString) throws JSONException {
//        JSONObject RssJSON = new JSONObject(JsonRssString);
//        JSONObject responseDataJSON = RssJSON.getJSONObject("responseData");
//        JSONObject feedJSON = responseDataJSON.getJSONObject("feed");
//
//        RssFeed rssFeed = new RssFeed(
//                feedJSON.getString(RSSFeedContract.FeedEntry.COLUMN_TITLE),
//                feedJSON.getString(RSSFeedContract.FeedEntry.COLUMN_LINK),
//                feedJSON.getString(RSSFeedContract.FeedEntry.COLUMN_FEED_URL)
//        );
//
//        return rssFeed;
//    }

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
                    feedJSON.getString("url")
            ));
        }

        RssFeed[] rssFeedArr = new RssFeed[feeds.size()];
        feeds.toArray(rssFeedArr);
        return rssFeedArr;
    }

    public static URL buildURLBasedOnKeywords(String keywords) throws MalformedURLException {
        final String RSS_BASE_URL = "https://ajax.googleapis.com/ajax/services/feed/find?v=1.0&";
        final String QUERY_PARAM = "q";

        Uri builtUri = Uri.parse(RSS_BASE_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAM, keywords)
                .build();
        URL url = new URL(builtUri.toString());

        return url;
    }

    public static URL buildURLBasedOnURL(String urlStr) throws MalformedURLException {
        final String RSS_BASE_URL = "https://ajax.googleapis.com/ajax/services/feed/load?v=1.0&";
        final String QUERY_PARAM = "q";

        Uri builtUri = Uri.parse(RSS_BASE_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAM, urlStr)
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

        Log.v(LOG_TAG, "Start connecting");

        // Create a request to Google Feed API, open the connection
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        Log.v(LOG_TAG, "DONE CONNECT");


        // Read the input stream into a String
        InputStream inputStream = connection.getInputStream();
        Log.v(LOG_TAG, "DONE GET INPUT STREAM");
        if (inputStream == null) {
            Log.v(LOG_TAG, "Input stream is empty ");
            return null;
        }

        Log.v(LOG_TAG, "Connect successfully.");

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder builder = new StringBuilder();

        String line = reader.readLine();
        int i = 0;
        while (line != null) {
            Log.v(LOG_TAG, "loop");
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
                        RSSFeedContract.ItemEntry.COLUMN_PUBLISHED_DATETEXT,
                        itemJSON.getString(RSSFeedContract.ItemEntry.COLUMN_PUBLISHED_DATETEXT)
                );
                itemValues.put(
                        RSSFeedContract.ItemEntry.COLUMN_CONTENT_SNIPPET,
                        itemJSON.getString(RSSFeedContract.ItemEntry.COLUMN_CONTENT_SNIPPET)
                );
                itemValues.put(
                        RSSFeedContract.ItemEntry.COLUMN_CONTENT,
                        itemJSON.getString(RSSFeedContract.ItemEntry.COLUMN_CONTENT)
                );
                itemValues.put(
                        RSSFeedContract.ItemEntry.COLUMN_FEED_ID,
                        feedRowId
                );

                Log.v(LOG_TAG, itemValues.getAsString(RSSFeedContract.ItemEntry.COLUMN_TITLE) + " " + itemValues.getAsString(RSSFeedContract.ItemEntry.COLUMN_LINK));

                context.getContentResolver().insert(
                        RSSFeedContract.ItemEntry.CONTENT_URI,
                        itemValues
                );
            }
    }


//    public static class RssFeedsDownloader extends AsyncTask<String, String, Void> {
//        private Context mContext;
//        public RssFeedsDownloader(Context context) {
//            mContext = context;
//        }
//
//        /**
//         * This method will extract Feed info from given JSON file. Then put all into
//         * database through ContentProvider
//         * @param JsonRssString
//         * @return
//         * @throws JSONException
//         */
//        private Void getFeedDataFromJSONAndSaveToDB(String JsonRssString) throws JSONException {
//            JSONObject RssJSON = new JSONObject(JsonRssString);
//            JSONObject responseDataJSON = RssJSON.getJSONObject("responseData");
//            JSONObject feedJSON = responseDataJSON.getJSONObject("feed");
//            JSONArray entriesJSON = feedJSON.getJSONArray("entries");
//
//            long feedRowId = insertFeedIntoDatabase(feedJSON);
//            insertItemIntoDatabase(entriesJSON, feedRowId);
//
//            return null;
//        }
//
//        private void insertItemIntoDatabase(JSONArray entriesJSON, long feedRowId) throws JSONException {
//            for (int i = 0; i < entriesJSON.length(); i++) {
//                JSONObject itemJSON = entriesJSON.getJSONObject(i);
//
//                ContentValues itemValues = new ContentValues();
//                itemValues.put(
//                        RSSFeedContract.ItemEntry.COLUMN_TITLE,
//                        itemJSON.getString(RSSFeedContract.ItemEntry.COLUMN_TITLE)
//                );
//                itemValues.put(
//                        RSSFeedContract.ItemEntry.COLUMN_LINK,
//                        itemJSON.getString(RSSFeedContract.ItemEntry.COLUMN_LINK)
//                );
//                itemValues.put(
//                        RSSFeedContract.ItemEntry.COLUMN_AUTHOR,
//                        itemJSON.getString(RSSFeedContract.ItemEntry.COLUMN_AUTHOR)
//                );
//                itemValues.put(
//                        RSSFeedContract.ItemEntry.COLUMN_PUBLISHED_DATETEXT,
//                        itemJSON.getString(RSSFeedContract.ItemEntry.COLUMN_PUBLISHED_DATETEXT)
//                );
//                itemValues.put(
//                        RSSFeedContract.ItemEntry.COLUMN_CONTENT_SNIPPET,
//                        itemJSON.getString(RSSFeedContract.ItemEntry.COLUMN_CONTENT_SNIPPET)
//                );
//                itemValues.put(
//                        RSSFeedContract.ItemEntry.COLUMN_CONTENT,
//                        itemJSON.getString(RSSFeedContract.ItemEntry.COLUMN_CONTENT)
//                );
//                itemValues.put(
//                        RSSFeedContract.ItemEntry.COLUMN_FEED_ID,
//                        feedRowId
//                );
//
//                Log.v(LOG_TAG, itemValues.getAsString(RSSFeedContract.ItemEntry.COLUMN_TITLE) + " " + itemValues.getAsString(RSSFeedContract.ItemEntry.COLUMN_LINK));
//
//                mContext.getContentResolver().insert(
//                        RSSFeedContract.ItemEntry.CONTENT_URI,
//                        itemValues
//                );
//            }
//        }
//
//        private long insertFeedIntoDatabase(JSONObject feedJSON) throws JSONException {
//            ContentValues feedValues = new ContentValues();
//            feedValues.put(
//                    RSSFeedContract.FeedEntry.COLUMN_TITLE,
//                    feedJSON.getString(RSSFeedContract.FeedEntry.COLUMN_TITLE)
//            );
//            feedValues.put(
//                    RSSFeedContract.FeedEntry.COLUMN_LINK,
//                    feedJSON.getString(RSSFeedContract.FeedEntry.COLUMN_LINK)
//            );
//
//            String feedUrl = feedJSON.getString(RSSFeedContract.FeedEntry.COLUMN_FEED_URL);
//            feedValues.put(
//                    RSSFeedContract.FeedEntry.COLUMN_FEED_URL,
//                    feedUrl
//            );
//
//            // Check if this feed does exist
//            Cursor feedCursor = mContext.getContentResolver().query(
//                    RSSFeedContract.FeedEntry.CONTENT_URI,
//                    new String[] {RSSFeedContract.FeedEntry._ID},
//                    RSSFeedContract.FeedEntry.COLUMN_FEED_URL + " = ? ",
//                    new String[] {feedUrl},
//                    null
//            );
//
//            long feedRowId;
//            if (feedCursor.moveToFirst()) {
//                // This feed already exists
//                // Return row id
//                int feedColumnIndex = feedCursor.getColumnIndex(RSSFeedContract.FeedEntry._ID);
//                feedRowId = feedCursor.getLong(feedColumnIndex);
//            } else {
//                // This feed does not exist, put it into databse
//                Uri insertedFeedUri = mContext.getContentResolver().insert(
//                        RSSFeedContract.FeedEntry.CONTENT_URI,
//                        feedValues
//                );
//                feedRowId = ContentUris.parseId(insertedFeedUri);
//            }
//
//            return feedRowId;
//        }
//
//        @Override
//        protected Void doInBackground(String... urlStrs) {
//            try {
//                publishProgress();
//                StringBuilder builder = downloadFeedJSON(urlStrs[0]);
//                getFeedDataFromJSONAndSaveToDB(builder.toString());
//
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            return null;
//        }
//
//
//
//        @Override
//        protected void onProgressUpdate(String... values) {
//            Toast.makeText(mContext, "Downloading...", Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//        }

        //        @Override
//        protected void onPostExecute(RssItem[] rssItems) {
//            ArrayList<String> feedTitles = new ArrayList<String>();
//            for (RssItem item : rssItems) {
//                feedTitles.add(item.getTitle());
//            }
//
//            mFeedItemsAdapter = new ArrayAdapter<String>(
//                    getActivity(),
//                    R.layout.one_item_in_list,
//                    R.id.list_rss_feed_textview,
//                    feedTitles
//            );
//
//            ListView listView = (ListView) mRootView.findViewById(R.id.listview_feeds);
//            listView.setAdapter(mFeedItemsAdapter);
//        }
}
