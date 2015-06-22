package example.com.sunreader.controller;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import example.com.sunreader.DetailItemActivity;
import example.com.sunreader.FeedItemsFragment;
import example.com.sunreader.R;
import example.com.sunreader.data.ImageHandler;
import example.com.sunreader.data.InternalStorageHandler;
import example.com.sunreader.data.RSSFeedContract;

public class ItemsViewController implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {
    private final String LOG_TAG = ItemsViewController.class.getSimpleName();

    Activity mActivity;
    private SimpleCursorAdapter mFeedItemsAdapter;

    public final static int COLUMN_ID_INDEX = 0;
    public final static int COLUMN_TITLE_INDEX = 1;
    public final static int COLUMN_CONTENT_SNIPPET_INDEX = 2;


    public ItemsViewController(Activity activity, SimpleCursorAdapter feedItemsAdapter) {
        mActivity = activity;
        mFeedItemsAdapter = feedItemsAdapter;
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent intent = new Intent(mActivity, DetailItemActivity.class)
            .putExtra(DetailItemActivity.ITEM_ID,
                    mFeedItemsAdapter.getCursor().getInt(0));
            mActivity.startActivity(intent);
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
                    new String[] {RSSFeedContract.ItemEntry._ID,
                            RSSFeedContract.ItemEntry.COLUMN_TITLE,
                            RSSFeedContract.ItemEntry.COLUMN_CONTENT_SNIPPET},
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
                    new String[] {RSSFeedContract.ItemEntry._ID,
                            RSSFeedContract.ItemEntry.COLUMN_TITLE,
                            RSSFeedContract.ItemEntry.COLUMN_CONTENT_SNIPPET},
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


    public SimpleCursorAdapter.ViewBinder createItemsViewBinder() {
        return new ItemsViewBinder();
    }

    public class ItemsViewBinder implements SimpleCursorAdapter.ViewBinder {


        @Override
        public boolean setViewValue(View view, final Cursor cursor, int columnIndex) {
//            Bitmap bitmap = new InternalStorageHandler(mActivity).loadImageFromStorage(
//                    InternalStorageHandler.ITEM_IMAGE_DIRECTORY_NAME,
//                    cursor.getString(COLUMN_ID_INDEX) + ".jpg"
//            );
//            if (bitmap != null) {
//                imgView.setImageBitmap(bitmap);
//            }

            switch (columnIndex) {
                case COLUMN_CONTENT_SNIPPET_INDEX: {
                    ((TextView) view).setText(cursor.getString(COLUMN_CONTENT_SNIPPET_INDEX));
                    Log.v(LOG_TAG, "SetViewValue");
                    // Bind image to view
                    ViewGroup parentView = (ViewGroup) view.getParent();
                    LinearLayout linearLayout = (LinearLayout) parentView;
                    LinearLayout parent = (LinearLayout) linearLayout.getParent();
                    ImageView imgView = (ImageView) parent.findViewById(R.id.item_imageview);
                    if (imgView == null) {
                        Log.e(LOG_TAG, "Cannot retrieve ImageView");
                        return false;
                    }

                    new ImageHandler(mActivity).loadingImageFromFileAndBindToView(
                            InternalStorageHandler.ITEM_IMAGE_DIRECTORY_NAME,
                            cursor.getString(COLUMN_ID_INDEX) + ".jpg",
                            imgView
                    );
                    return true;
                }
                case COLUMN_TITLE_INDEX: {
                    ((TextView) view).setText(cursor.getString(COLUMN_TITLE_INDEX));
                    return true;
                }
            }


            return false;
        }

    }
}
