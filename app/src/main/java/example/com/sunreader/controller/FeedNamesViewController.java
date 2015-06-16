package example.com.sunreader.controller;


import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import example.com.sunreader.FeedItemsFragment;
import example.com.sunreader.R;
import example.com.sunreader.data.RSSFeedContract;

public class FeedNamesViewController implements AdapterView.OnItemClickListener {
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
        Toast.makeText(mActivity.getApplicationContext(), "Click on feed name", Toast.LENGTH_LONG).show();
        Cursor cursor = mFeedNamesAdapter.getCursor();
        // Create fragment with bundle contains ID of selected feed
        Bundle feedIdBundle = new Bundle();
        feedIdBundle.putInt(RSSFeedContract.ItemEntry.COLUMN_FEED_ID, cursor.getInt(0));

        FeedItemsFragment feedItemsFragment = new FeedItemsFragment();
        feedItemsFragment.setArguments(feedIdBundle);
        mFragmentManager
                .beginTransaction()
                .replace(R.id.container, feedItemsFragment)
                .commit();

        ((DrawerLayout) mActivity.findViewById(R.id.drawer_layout)).closeDrawers();
    }
}
