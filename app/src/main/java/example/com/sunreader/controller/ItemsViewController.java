package example.com.sunreader.controller;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import example.com.sunreader.DetailItemActivity;

public class ItemsViewController implements AdapterView.OnItemClickListener {
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
    }
}
