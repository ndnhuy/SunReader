package example.com.sunreader.controller;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import example.com.sunreader.DetailItemActivity;
import example.com.sunreader.FeedItemsFragment;
import example.com.sunreader.data.ImageHandler;
import example.com.sunreader.data.InternalStorageHandler;
import example.com.sunreader.data.RSSFeedContract;

public class ItemsViewController implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {
    Activity mActivity;
    private SimpleCursorAdapter mFeedItemsAdapter;

    public ItemsViewController(Activity activity, SimpleCursorAdapter feedItemsAdapter) {
        mActivity = activity;
        mFeedItemsAdapter = feedItemsAdapter;
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Toast.makeText(mActivity, "Click on item", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(mActivity, DetailItemActivity.class)
            .putExtra(DetailItemActivity.ITEM_ID,
                    mFeedItemsAdapter.getCursor().getInt(0));
            mActivity.startActivity(intent);

        //TODO TEST
        new ImageHandler(mActivity).saveImage("http://www.google.com/s2/favicons?domain=http://www.androidcentral.com",
                InternalStorageHandler.FEED_ICON_DIRECTORY_NAME,
                "1.jpg");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        int feedId = -1;
        if (args != null) {
            feedId = args.getInt(FeedItemsFragment.FEED_ID_ARG);
        }
        if (feedId != -1) {
            return new CursorLoader(
                    mActivity,
                    RSSFeedContract.ItemEntry.buildItemWithFeedId(feedId),
                    new String[] {RSSFeedContract.ItemEntry._ID, RSSFeedContract.ItemEntry.COLUMN_TITLE},
                    null,
                    null,
                    RSSFeedContract.ItemEntry.COLUMN_PUBLISHED_DATETEXT + " DESC"
            );
        }
        else {
            // Load all items
            return new CursorLoader(
                    mActivity,
                    RSSFeedContract.ItemEntry.CONTENT_URI,
                    new String[] {RSSFeedContract.ItemEntry._ID, RSSFeedContract.ItemEntry.COLUMN_TITLE},
                    null,
                    null,
                    RSSFeedContract.ItemEntry.COLUMN_PUBLISHED_DATETEXT + " DESC"
            );
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mFeedItemsAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mFeedItemsAdapter.swapCursor(null);
    }
}
