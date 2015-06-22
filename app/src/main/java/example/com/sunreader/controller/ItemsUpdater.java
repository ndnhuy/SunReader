package example.com.sunreader.controller;


import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;

import example.com.sunreader.data.RssService;

public class ItemsUpdater extends AsyncTask<Void, Void, Void> {
    private Activity mActivity;
    private String mFeedLink;
    private long mFeedId;



    public ItemsUpdater(Activity activity, String feedLink, long feedId) {
        mActivity = activity;
        mFeedLink = feedLink;
        mFeedId = feedId;
    }

    @Override
    protected void onPreExecute() {
        Toast.makeText(mActivity, "Loading...", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        String feedData = null;
        try {
            feedData = RssService.downloadFeedJSON(
                    RssService.SEARCH_BY_LINK,
                    mFeedLink).toString();

            RssService.insertItemIntoDatabase(mActivity, feedData, mFeedId);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {

    }
}