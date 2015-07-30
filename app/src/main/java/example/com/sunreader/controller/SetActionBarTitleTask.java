package example.com.sunreader.controller;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;

import example.com.sunreader.data.RSSFeedContract;

public class SetActionBarTitleTask extends AsyncTask<Integer, Void, String> {
    ActionBar mActionBar;
    Context mContext;
    public SetActionBarTitleTask(Context context, ActionBar actionBar) {
        mActionBar = actionBar;
        mContext = context;
    }
    @Override
    protected void onPreExecute() {
    }

    @Override
    protected String doInBackground(Integer... feedIDs) {
        int feedId = feedIDs[0];
        if (feedId == FeedNamesViewController.HOME_ID) {
            return "HOME";
        } else if (feedId == FeedNamesViewController.SAVED_FOR_LATER_ID) {
            return "SAVED FOR LATER";
        } else {
            Cursor cursor = mContext.getContentResolver().query(
                    RSSFeedContract.FeedEntry.buildFeedUri(feedId),
                    new String[]{RSSFeedContract.FeedEntry.COLUMN_TITLE},
                    null,
                    null,
                    null
            );
            if (!cursor.moveToFirst()) return "";

            return cursor.getString(0);
        }
    }

    @Override
    protected void onPostExecute(String s) {
        if (mActionBar != null)
            mActionBar.setSubtitle(s);
    }
}
