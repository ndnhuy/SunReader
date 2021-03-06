package example.com.sunreader.controller;


import android.app.Activity;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import example.com.sunreader.FeedItemsFragment;
import example.com.sunreader.R;
import example.com.sunreader.data.InternalStorageHandler;
import example.com.sunreader.data.RSSFeedContract;
import example.com.sunreader.data.SharedFileHandler;

public class FeedNamesViewController implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemLongClickListener {
    private final String LOG_TAG = FeedNamesViewController.class.getSimpleName();
    private Activity mActivity;
    private SimpleCursorAdapter mFeedNamesAdapter;
    private FragmentManager mFragmentManager;


    public static final int HOME_ID = -1;
    public static final int SAVED_FOR_LATER_ID = -2;

    public static final int COLUMN_ID_INDEX = 0;
    public static final int COLUMN_TITLE_INDEX = 1;
    public static final int COLUMN_FEEDURL_INDEX = 2;
    public static final int COLUMN_LINK_INDEX = 3;

    public FeedNamesViewController(Activity activity, SimpleCursorAdapter feedNamesAdapter, FragmentManager fragmentManager) {
        mActivity = activity;
        mFeedNamesAdapter = feedNamesAdapter;
        mFragmentManager = fragmentManager;

//        mFeedNamesAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
//            @Override
//            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
//                return false;
//            }
//        });
    }
    private void updateUnderlyingItems(int feedId) {
        new ItemsUpdater(mActivity, feedId).execute();
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((DrawerLayout) mActivity.findViewById(R.id.drawer_layout)).closeDrawers();
            }
        }, 500);

        int curentFeedId = mFeedNamesAdapter.getCursor().getInt(COLUMN_ID_INDEX);
        // Update underlying contents
        updateUnderlyingItems(curentFeedId);

        // Change action bar title
        ((ActionBarActivity)mActivity).getSupportActionBar().setSubtitle(
                mFeedNamesAdapter.getCursor().getString(COLUMN_TITLE_INDEX));

        SharedFileHandler.saveFeedIdToSharedPrefFile(mActivity, curentFeedId);
        SharedFileHandler.saveFeedTitleToSharedPrefFile(mActivity, mFeedNamesAdapter.getCursor().getString(COLUMN_TITLE_INDEX));

        ViewPager viewPager = (ViewPager) mActivity.findViewById(R.id.feed_pager);
        viewPager.setCurrentItem(ItemListPagerAdapter.feedIDs.indexOf(curentFeedId));
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
        int currentFeedId = mFeedNamesAdapter.getCursor().getInt(COLUMN_ID_INDEX);
        if (currentFeedId == FeedNamesViewController.HOME_ID || currentFeedId == FeedNamesViewController.SAVED_FOR_LATER_ID) {
            return false;
        }

        PopupMenu popup = new PopupMenu(mActivity, view);
        popup.getMenuInflater()
                .inflate(R.menu.feed_popup_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                String feedId = mFeedNamesAdapter.getCursor().getString(COLUMN_ID_INDEX);
                new DeleteFeedHandler(feedId).execute();
                return true;
            }
        });
        popup.show();
        return true;
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
        // add headers for side bar
        String[] columnNames = data.getColumnNames();
        MatrixCursor matrixCursor = new MatrixCursor(columnNames, 1);

        String[] headers = new String[]{"HOME", "SAVED FOR LATER"};
        int[] headerIDs = new int[]{HOME_ID, SAVED_FOR_LATER_ID};
        for (int i = 0; i < headers.length; i++) {
            MatrixCursor.RowBuilder rowBuilder = matrixCursor.newRow();
            rowBuilder.add(headerIDs[i]);
            rowBuilder.add(headers[i]);
            rowBuilder.add("");
            rowBuilder.add("");
        }

        while (data.moveToNext()) {
            MatrixCursor.RowBuilder rowBuilder = matrixCursor.newRow();
            for (String columnName : columnNames) {
                int columnIndex = data.getColumnIndex(columnName);
                String row = data.getString(columnIndex);
                rowBuilder.add(row);
            }
        }
        mFeedNamesAdapter.swapCursor(matrixCursor);


//        // Update pager
//        if (!data.moveToFirst()) return;
//
//        Ite
//        ItemListPagerAdapter.feedIDs.add(data.getInt(COLUMN_ID_INDEX));
//        while (data.moveToNext()) {
//            ItemListPagerAdapter.feedIDs.add(data.getInt(COLUMN_ID_INDEX));
//        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mFeedNamesAdapter.swapCursor(null);
    }


    private class DeleteFeedHandler extends AsyncTask<Void, Void, Void> {
        String feedId;

        public DeleteFeedHandler(String feedId) {
            this.feedId = feedId;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            mActivity.getContentResolver().delete(
                    RSSFeedContract.FeedEntry.CONTENT_URI,
                    RSSFeedContract.FeedEntry._ID + " = ?",
                    new String[]{feedId}
            );
            new InternalStorageHandler(mActivity).deleteFile(
                    InternalStorageHandler.FEED_ICON_DIRECTORY_NAME,
                    feedId + ".jpg"
            );

            // Delete all thumbnails in directory
            Cursor cursor = mActivity.getContentResolver().query(
                    RSSFeedContract.ItemEntry.CONTENT_URI,
                    new String[] {RSSFeedContract.ItemEntry._ID},
                    RSSFeedContract.ItemEntry.COLUMN_FEED_ID + " = ?",
                    new String[] {feedId},
                    null
            );

            boolean completedDeletingItemsInDB = false;
            while (cursor.moveToNext()) {
                if (completedDeletingItemsInDB == false) {
                    int rowsDeleted = mActivity.getContentResolver().delete(
                            RSSFeedContract.ItemEntry.CONTENT_URI,
                            RSSFeedContract.ItemEntry.COLUMN_FEED_ID + " = ?",
                            new String[] {feedId}
                    );
                    completedDeletingItemsInDB = true;
                }
                new InternalStorageHandler(mActivity).deleteFile(
                        InternalStorageHandler.ITEM_IMAGE_DIRECTORY_NAME,
                        cursor.getString(COLUMN_ID_INDEX) + ".jpg"
                );
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(mActivity, "Delete successfully", Toast.LENGTH_SHORT).show();


            ViewPager viewPager = (ViewPager) mActivity.findViewById(R.id.feed_pager);
            int pos = ItemListPagerAdapter.feedIDs.indexOf(Integer.parseInt(feedId));
            if (viewPager.getCurrentItem() == pos) {
                if (pos == ItemListPagerAdapter.feedIDs.size() - 2) {
                    viewPager.setCurrentItem(pos - 1);
                    new SetActionBarTitleTask(mActivity, ((ActionBarActivity)mActivity).getSupportActionBar())
                            .execute(ItemListPagerAdapter.feedIDs.get(pos - 1));
                }
                else {
                    new SetActionBarTitleTask(mActivity, ((ActionBarActivity)mActivity).getSupportActionBar())
                            .execute(ItemListPagerAdapter.feedIDs.get(pos + 1));
                }
            }



            ItemListPagerAdapter.feedIDs.remove(pos);
            viewPager.getAdapter().notifyDataSetChanged();

        }
    }

}
