package example.com.sunreader.data;


import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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

public class RssService {
    private static final String LOG_TAG = RssService.class.getSimpleName();

    public static class RssFeedsDownloader extends AsyncTask<String, String, Void> {
        private Context mContext;
        public RssFeedsDownloader(Context context) {
            mContext = context;
        }

        /**
         * This method will extract Feed info from given JSON file. Then put all into
         * database through ContentProvider
         * @param JsonRssString
         * @return
         * @throws JSONException
         */
        private Void getFeedDataFromJSONAndSaveToDB(String JsonRssString) throws JSONException {
            JSONObject RssJSON = new JSONObject(JsonRssString);
            JSONObject responseDataJSON = RssJSON.getJSONObject("responseData");
            JSONObject feedJSON = responseDataJSON.getJSONObject("feed");
            JSONArray entriesJSON = feedJSON.getJSONArray("entries");

            // Insert feed into database
            ContentValues feedValues = new ContentValues();
            feedValues.put(
                    RSSFeedContract.FeedEntry.COLUMN_TITLE,
                    feedJSON.getString(RSSFeedContract.FeedEntry.COLUMN_TITLE)
            );
            feedValues.put(
                    RSSFeedContract.FeedEntry.COLUMN_LINK,
                    feedJSON.getString(RSSFeedContract.FeedEntry.COLUMN_LINK)
            );

            String feedUrl = feedJSON.getString(RSSFeedContract.FeedEntry.COLUMN_FEED_URL);
            feedValues.put(
                    RSSFeedContract.FeedEntry.COLUMN_FEED_URL,
                    feedUrl
            );

            // Check if this feed does exist
            Cursor feedCursor = mContext.getContentResolver().query(
                    RSSFeedContract.FeedEntry.CONTENT_URI,
                    new String[] {RSSFeedContract.FeedEntry._ID},
                    RSSFeedContract.FeedEntry.COLUMN_FEED_URL + " = ? ",
                    new String[] {feedUrl},
                    null
            );

            long feedRowId;
            if (feedCursor.moveToFirst()) {
                // This feed already exists
                // Return row id
                int feedColumnIndex = feedCursor.getColumnIndex(RSSFeedContract.FeedEntry._ID);
                feedRowId = feedCursor.getLong(feedColumnIndex);
            } else {
                // This feed does not exist, put it into databse
                Uri insertedFeedUri = mContext.getContentResolver().insert(
                        RSSFeedContract.FeedEntry.CONTENT_URI,
                        feedValues
                );
                feedRowId = ContentUris.parseId(insertedFeedUri);
            }

            //End insert feed ---------------------

            //Insert item
            ContentValues itemValues = new ContentValues();
            for (int i = 0; i < entriesJSON.length(); i++) {
                JSONObject itemJSON = entriesJSON.getJSONObject(i);
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

                mContext.getContentResolver().insert(
                        RSSFeedContract.ItemEntry.CONTENT_URI,
                        itemValues
                );
            }
            //End insert item ----------------------
            return null;
        }
        @Override
        protected Void doInBackground(String... urlStrs) {
            try {
                publishProgress();
                StringBuilder builder = downloadFeedJSON(urlStrs[0]);
                getFeedDataFromJSONAndSaveToDB(builder.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        private StringBuilder downloadFeedJSON(String urlStr) throws IOException {
            final String RSS_BASE_URL = "https://ajax.googleapis.com/ajax/services/feed/load?v=1.0&";
            final String QUERY_PARAM = "q";

            Log.v(LOG_TAG, "Start connecting");

            Uri builtUri = Uri.parse(RSS_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, urlStr)
                    .build();
            URL url = new URL(builtUri.toString());

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

        @Override
        protected void onProgressUpdate(String... values) {
            Toast.makeText(mContext, "Downloading...", Toast.LENGTH_SHORT).show();
        }

//        @Override
//        protected void onPostExecute(RssItem[] rssItems) {
//            ArrayList<String> feedTitles = new ArrayList<String>();
//            for (RssItem item : rssItems) {
//                feedTitles.add(item.getTitle());
//            }
//
//            mFeedItemsAdapter = new ArrayAdapter<String>(
//                    getActivity(),
//                    R.layout.list_rss_feeds,
//                    R.id.list_rss_feed_textview,
//                    feedTitles
//            );
//
//            ListView listView = (ListView) mRootView.findViewById(R.id.listview_feeds);
//            listView.setAdapter(mFeedItemsAdapter);
//        }
    }
}
