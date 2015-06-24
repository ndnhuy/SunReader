package example.com.sunreader.controller;


import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;

import example.com.sunreader.data.RSSFeedContract;
import example.com.sunreader.data.RssService;

public class ItemsUpdater extends AsyncTask<Void, Void, Void> {
    private Activity mActivity;
    private long mFeedId;



    public ItemsUpdater(Activity activity, long feedId) {
        mActivity = activity;
        mFeedId = feedId;
    }

    @Override
    protected void onPreExecute() {
        Toast.makeText(mActivity, "Loading...", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected Void doInBackground(Void... voids) {

        try {
            if (mFeedId == FeedNamesViewController.HOME_ID) {
                // Get id and link of all feeds
                Cursor cursor = mActivity.getContentResolver().query(
                        RSSFeedContract.FeedEntry.CONTENT_URI,
                        new String[]{RSSFeedContract.FeedEntry._ID, RSSFeedContract.FeedEntry.COLUMN_FEED_URL},
                        null,
                        null,
                        null
                );
                while (cursor.moveToNext()) {
                    String feedData = RssService.downloadFeedJSON(
                            RssService.SEARCH_BY_LINK,
                            cursor.getString(cursor.getColumnIndex(RSSFeedContract.FeedEntry.COLUMN_FEED_URL))
                    ).toString();
                    RssService.insertItemIntoDatabase(
                                    mActivity,
                                    feedData,
                                    cursor.getLong(cursor.getColumnIndex(RSSFeedContract.FeedEntry._ID))
                            );
                }


            }
            else if (mFeedId == FeedNamesViewController.SAVED_FOR_LATER_ID) {
                // nothing to do
            }
            else {
                updateSpecificFeed();
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void updateSpecificFeed() throws IOException, JSONException {
        String feedData = null;
        // Get feed url based on feed id
        Cursor cursor = mActivity.getContentResolver().query(
                RSSFeedContract.FeedEntry.buildFeedUri(mFeedId),
                new String[]{RSSFeedContract.FeedEntry.COLUMN_FEED_URL},
                null,
                null,
                null
        );
        if (!cursor.moveToFirst())
            return;

        String feedLink = cursor.getString(0);

        feedData = RssService.downloadFeedJSON(
                RssService.SEARCH_BY_LINK,
                feedLink).toString();
        RssService.insertItemIntoDatabase(mActivity, feedData, mFeedId);
    }
    @Override
    protected void onPostExecute(Void aVoid) {

    }
}