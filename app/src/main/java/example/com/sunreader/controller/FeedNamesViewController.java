package example.com.sunreader.controller;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;

import example.com.sunreader.FeedItemsFragment;
import example.com.sunreader.R;
import example.com.sunreader.data.RSSFeedContract;
import example.com.sunreader.data.RssService;

public class FeedNamesViewController implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {
    private Activity mActivity;
    private SimpleCursorAdapter mFeedNamesAdapter;
    private FragmentManager mFragmentManager;

    public static final int COLUMN_ID_INDEX = 0;
    public static final int COLUMN_TITLE_INDEX = 1;
    public static final int COLUMN_FEEDURL_INDEX = 2;
    public static final int COLUMN_LINK_INDEX = 3;

    public FeedNamesViewController(Activity activity, SimpleCursorAdapter feedNamesAdapter, FragmentManager fragmentManager) {
        mActivity = activity;
        mFeedNamesAdapter = feedNamesAdapter;
        mFragmentManager = fragmentManager;

        mFeedNamesAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                return false;
            }
        });
    }
    public void updateUnderlyingItems(String feedUrl, int feedId) {
        new ItemsUpdater(feedUrl, feedId).execute();
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        // Replace the current fragment in main view with new one.
        mFragmentManager
                .beginTransaction()
                .replace(R.id.container, createFragmentContainsItemsOfSelectedFeed())
                .commit();

        ((DrawerLayout) mActivity.findViewById(R.id.drawer_layout)).closeDrawers();

        // Update underlying contents
        updateUnderlyingItems(mFeedNamesAdapter.getCursor().getString(COLUMN_FEEDURL_INDEX),
                mFeedNamesAdapter.getCursor().getInt(COLUMN_ID_INDEX));

        SharedPreferences sharedPref = mActivity.getSharedPreferences(
                mActivity.getString(R.string.reference_file_key),
                Context.MODE_PRIVATE);

        //TODO Save feed id to shared reference file
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(FeedItemsFragment.FEED_ID_ARG, mFeedNamesAdapter.getCursor().getInt(COLUMN_ID_INDEX));
        editor.commit();

//        //TODO get feedid from shared reference file
//        SharedPreferences sharedFile = mActivity.getSharedPreferences(
//                mActivity.getString(R.string.reference_file_key),
//                Context.MODE_PRIVATE);
//        int feedId = sharedFile.getInt(FeedItemsFragment.FEED_ID_ARG, -1);
//        Toast.makeText(mActivity, "Feed ID: " + feedId, Toast.LENGTH_SHORT).show();
    }

    private FeedItemsFragment createFragmentContainsItemsOfSelectedFeed() {
        // Create fragment with bundle contains ID of selected feed
        Bundle feedIdBundle = new Bundle();
        feedIdBundle.putInt(FeedItemsFragment.FEED_ID_ARG, mFeedNamesAdapter.getCursor().getInt(COLUMN_ID_INDEX));
        FeedItemsFragment feedItemsFragment = new FeedItemsFragment();
        feedItemsFragment.setArguments(feedIdBundle);

        return feedItemsFragment;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                mActivity,
                RSSFeedContract.FeedEntry.CONTENT_URI,
                new String[]{RSSFeedContract.FeedEntry._ID,
                        RSSFeedContract.FeedEntry.COLUMN_TITLE,
                        RSSFeedContract.FeedEntry.COLUMN_FEED_URL,
                        RSSFeedContract.FeedEntry.COLUMN_LINK},
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mFeedNamesAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mFeedNamesAdapter.swapCursor(null);
    }

    public class ItemsUpdater extends AsyncTask<Void, Void, Void> {
        private String mFeedLink;
        private long mFeedId;

        ProgressDialog dialog = new ProgressDialog(mActivity);
        public ItemsUpdater(String feedLink, long feedId) {
            mFeedLink = feedLink;
            mFeedId = feedId;
        }

        @Override
        protected void onPreExecute() {
            Toast.makeText(mActivity, "Loading...", Toast.LENGTH_SHORT);
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
}
