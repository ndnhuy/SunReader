package example.com.sunreader.controller;


import android.app.Activity;
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

    public FeedNamesViewController(Activity activity, SimpleCursorAdapter feedNamesAdapter, FragmentManager fragmentManager) {
        mActivity = activity;
        mFeedNamesAdapter = feedNamesAdapter;
        mFragmentManager = fragmentManager;
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Toast.makeText(mActivity.getApplicationContext(), "Click on feed name", Toast.LENGTH_SHORT).show();
        Cursor cursor = mFeedNamesAdapter.getCursor();

        // Create fragment with bundle contains ID of selected feed
        Bundle feedIdBundle = new Bundle();
        feedIdBundle.putInt(FeedItemsFragment.FEED_ID_ARG, cursor.getInt(0));

        FeedItemsFragment feedItemsFragment = new FeedItemsFragment();
        feedItemsFragment.setArguments(feedIdBundle);
        mFragmentManager
                .beginTransaction()
                .replace(R.id.container, feedItemsFragment)
                .commit();

        ((DrawerLayout) mActivity.findViewById(R.id.drawer_layout)).closeDrawers();

        // Update items
        new ItemsDownloader(cursor.getString(2), cursor.getInt(0)).execute();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                mActivity,
                RSSFeedContract.FeedEntry.CONTENT_URI,
                new String[]{RSSFeedContract.FeedEntry._ID, RSSFeedContract.FeedEntry.COLUMN_TITLE, RSSFeedContract.FeedEntry.COLUMN_FEED_URL},
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

    private class ItemsDownloader extends AsyncTask<Void, Void, Void> {
        private String mFeedLink;
        private long mFeedId;

        public ItemsDownloader(String feedLink, long feedId) {
            mFeedLink = feedLink;
            mFeedId = feedId;
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
    }
}
