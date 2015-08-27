package example.com.sunreader;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import example.com.sunreader.controller.DetailItemViewController;
import example.com.sunreader.controller.ItemState;
import example.com.sunreader.data.RSSFeedContract;

public class DetailItemFragment extends Fragment {
    private String LOG_TAG = DetailItemActivity.class.getSimpleName();


    private DetailItemViewController mDetailItemViewController;
    private final int DETAIL_ITEM_LOADER = 0;
    private View mRootView;

    public DetailItemFragment() {

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreateView in DetailItemFragment");
        if (mDetailItemViewController == null) {
            mRootView = inflater.inflate(R.layout.detail_item_fragment, container, false);
            mDetailItemViewController = new DetailItemViewController(getActivity(), mRootView);
        }
        else {
            mRootView = mDetailItemViewController.getRootView();
        }
        getLoaderManager().initLoader(DETAIL_ITEM_LOADER, getArguments(), mDetailItemViewController);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail_activity_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        ShareActionProvider mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareItemIntent());
        }
        else {
            Log.d(LOG_TAG, "Share Action Provider is null?");
        }

        // Set up action_save butotn
        menuItem = menu.findItem(R.id.action_save);
        if (getArguments() != null) {
            long itemId = getArguments().getInt(DetailItemActivity.ITEM_ID);
            Cursor cursor = getActivity().getContentResolver().query(
                    RSSFeedContract.ItemEntry.buildItemUri(itemId),
                    new String[] {RSSFeedContract.ItemEntry.COLUMN_SAVED},
                    null,
                    null,
                    null
            );
            if (cursor.moveToFirst()) {
                if (cursor.getInt(0) == ItemState.SAVED) {
                    menuItem.setIcon(R.mipmap.ic_saved_for_later);
                    menuItem.setTitle("Unsaved");
                }
            }
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        long itemId = 0;
        if (getArguments() != null) {
            itemId = getArguments().getInt(DetailItemActivity.ITEM_ID);
        }
        if (item.getItemId() == R.id.action_save) {

            if (clickToSave(item)) {
                ItemState.saveForLater(getActivity(), itemId);
                item.setIcon(getResources().getDrawable(R.mipmap.ic_saved_for_later));
                item.setTitle(getString(R.string.unsaved));
            } else if (clickToUnsave(item)) {
                ItemState.unsaved(getActivity(), itemId);
                item.setIcon(getResources().getDrawable(R.mipmap.ic_to_save_for_later));
                item.setTitle(getString(R.string.saved));
            }
        }
        else if (item.getItemId() == R.id.action_unread) {
            ItemState.markAsUnread(getActivity(), itemId);
            Toast.makeText(getActivity(), "Unread", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean clickToSave(MenuItem item) {
        return item.getTitle().toString().equals(getString(R.string.saved));
    }

    private boolean clickToUnsave(MenuItem item) {
        return item.getTitle().toString().equals((getString(R.string.unsaved)));
    }

    private Intent createShareItemIntent() {
        // Get link from database
        int itemId = -1;
        if (getArguments() != null) {
            itemId = getArguments().getInt(DetailItemActivity.ITEM_ID);
        }

        Cursor cursor = getActivity().getContentResolver().query(
                RSSFeedContract.ItemEntry.buildItemUri(itemId),
                new String[] {RSSFeedContract.ItemEntry.COLUMN_LINK},
                null,
                null,
                null
        );

        String sharedMessage = "#SunReader";
        if (cursor.moveToFirst()) {
            sharedMessage = cursor.getString(0);
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, sharedMessage);
        return shareIntent;
    }
}
