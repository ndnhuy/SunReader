package example.com.sunreader.controller;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;

import example.com.sunreader.DetailItemActivity;
import example.com.sunreader.FeedItemsFragment;
import example.com.sunreader.R;
import example.com.sunreader.data.DateConverter;
import example.com.sunreader.data.ImageHandler;
import example.com.sunreader.data.RSSFeedContract;
import example.com.sunreader.data.SharedFileHandler;
import example.com.sunreader.libs.ImageLoader;

public class ItemsViewController implements AdapterView.OnItemClickListener,
                                            LoaderManager.LoaderCallbacks<Cursor>,
                                            SwipeRefreshLayout.OnRefreshListener,
                                                AdapterView.OnItemLongClickListener {
    private final String LOG_TAG = ItemsViewController.class.getSimpleName();

    Activity mActivity;
    private SimpleCursorAdapter mFeedItemsAdapter;

    public final static int COLUMN_ID_INDEX = 0;
    public final static int COLUMN_TITLE_INDEX = 1;
    public final static int COLUMN_CONTENT_SNIPPET_INDEX = 2;
    public final static int COLUMN_AUTHOR_INDEX = 3;
    public final static int COLUMN_DATE_INDEX = 4;
    public final static int COLUMN_READ_INDEX = 5;

    public ItemsViewController(Activity activity, SimpleCursorAdapter feedItemsAdapter) {
        mActivity = activity;
        mFeedItemsAdapter = feedItemsAdapter;
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        int itemId = mFeedItemsAdapter.getCursor().getInt(0);
        Intent intent = new Intent(mActivity, DetailItemActivity.class)
                                    .putExtra(DetailItemActivity.ITEM_ID, itemId);
        mActivity.startActivity(intent);

        ContentValues values = new ContentValues();
        values.put(RSSFeedContract.ItemEntry.COLUMN_READ, 1);

        // Mark as read
        mActivity.getContentResolver().update(
                RSSFeedContract.ItemEntry.CONTENT_URI,
                values,
                RSSFeedContract.ItemEntry._ID + " = ?",
                new String[]{Integer.toString(itemId)}
        );
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        String showMessage = "";
        if (SharedFileHandler.getSharedPrefFile(mActivity)
                .getInt(FeedItemsFragment.FEED_ID_ARG, -1)
                == FeedNamesViewController.SAVED_FOR_LATER_ID) {
            showMessage = "UNSAVED";
            ItemState.unsaved(mActivity, mFeedItemsAdapter.getCursor().getLong(COLUMN_ID_INDEX));
        }
        else {
            showMessage = "SAVED";
            ItemState.saveForLater(mActivity, mFeedItemsAdapter.getCursor().getLong(COLUMN_ID_INDEX));
        }
        Toast toast = Toast.makeText(mActivity, showMessage, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        int feedId = -1;
        if (args != null) {
            feedId = args.getInt(FeedItemsFragment.FEED_ID_ARG);
        }

        Uri uri;
        if (feedId == FeedNamesViewController.HOME_ID) {
            // Load all items
            uri = RSSFeedContract.ItemEntry.CONTENT_URI;

        }
        else if (feedId == FeedNamesViewController.SAVED_FOR_LATER_ID) {
            return new CursorLoader(
                    mActivity,
                    RSSFeedContract.ItemEntry.CONTENT_URI,
                    new String[]{RSSFeedContract.ItemEntry._ID,
                            RSSFeedContract.ItemEntry.COLUMN_TITLE,
                            RSSFeedContract.ItemEntry.COLUMN_CONTENT_SNIPPET,
                            RSSFeedContract.ItemEntry.COLUMN_AUTHOR,
                            RSSFeedContract.ItemEntry.COLUMN_PUBLISHED_DATETEXT,
                            RSSFeedContract.ItemEntry.COLUMN_READ},
                    RSSFeedContract.ItemEntry.COLUMN_SAVED + " = ?",
                    new String[] {"1"},
                    RSSFeedContract.ItemEntry.COLUMN_PUBLISHED_DATETEXT + " DESC"
            );
        }
        else {
            uri = RSSFeedContract.ItemEntry.buildItemWithFeedId(feedId);
        }
        return new CursorLoader(
                mActivity,
                uri,
                new String[]{RSSFeedContract.ItemEntry._ID,
                        RSSFeedContract.ItemEntry.COLUMN_TITLE,
                        RSSFeedContract.ItemEntry.COLUMN_CONTENT_SNIPPET,
                        RSSFeedContract.ItemEntry.COLUMN_AUTHOR,
                        RSSFeedContract.ItemEntry.COLUMN_PUBLISHED_DATETEXT,
                        RSSFeedContract.ItemEntry.COLUMN_READ},
                null,
                null,
                RSSFeedContract.ItemEntry.COLUMN_PUBLISHED_DATETEXT + " DESC"
        );
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

    @Override
    public void onRefresh() {

    }




    public class ItemsViewBinder implements SimpleCursorAdapter.ViewBinder {


        @Override
        public boolean setViewValue(View view, final Cursor cursor, int columnIndex) {
            switch (columnIndex) {
                case COLUMN_CONTENT_SNIPPET_INDEX: {

                    loadThumbnail(view, cursor.getInt(COLUMN_ID_INDEX));
                    ((TextView) view).setText(cursor.getString(COLUMN_CONTENT_SNIPPET_INDEX));

                    return true;
                }
                case COLUMN_TITLE_INDEX: {
                    ViewGroup parentView = (ViewGroup) view.getParent();
                    LinearLayout linearLayout = (LinearLayout) parentView;
                    LinearLayout parent = (LinearLayout) linearLayout.getParent();
                    ImageView imgView = (ImageView) parent.findViewById(R.id.read_mark_imgView);
                    if (thisItemIsRead(cursor)) {
                        if (imgView == null) {
                            Log.e(LOG_TAG, "Cannot retrieve ImageView");
                            return false;
                        }
                        imgView.setImageResource(R.mipmap.ic_mark_as_read);
                    }
                    else {
                        imgView.setImageResource(0);
                    }
                    ((TextView) view).setText(cursor.getString(COLUMN_TITLE_INDEX));
                    return true;
                }
                case COLUMN_AUTHOR_INDEX: {
                    ((TextView) view).setText(cursor.getString(COLUMN_AUTHOR_INDEX));
                    return true;
                }
                case COLUMN_DATE_INDEX: {
                    // Convert to readable date
                    try {
                        ((TextView) view).setText(
                                DateConverter.getReadableDate(cursor.getString(COLUMN_DATE_INDEX))
                        );
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return true;
                }

            }


            return false;
        }

        private boolean loadThumbnail(View view, int itemId) {
            ViewGroup parentView = (ViewGroup) view.getParent();
            LinearLayout linearLayout = (LinearLayout) parentView;
            LinearLayout parent = (LinearLayout) linearLayout.getParent();
            ImageView imgView = (ImageView) parent.findViewById(R.id.item_imageview);
            if (imgView == null) {
                Log.e(LOG_TAG, "Cannot retrieve ImageView");
                return false;
            }
            ImageLoader imageLoader = ImageLoaderSingleton.getInstance(mActivity);
            imageLoader.DisplayImage(
                    new ImageHandler(mActivity).extractThumnailFromContent(itemId),
                    imgView
            );
            return true;
        }

        private boolean thisItemIsRead(Cursor cussor) {
            return new Integer(cussor.getInt(COLUMN_READ_INDEX)).equals(1);
        }

    }
}
