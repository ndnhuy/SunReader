package example.com.sunreader;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;

import java.util.ArrayList;

import example.com.sunreader.controller.DetailItemPagerAdapter;
import example.com.sunreader.controller.FeedNamesViewController;
import example.com.sunreader.controller.ItemState;
import example.com.sunreader.data.RSSFeedContract;
import example.com.sunreader.data.SharedFileHandler;

public class DetailItemActivity extends ActionBarActivity {
    public static final String ITEM_ID = "item_id";

    private DetailItemPagerAdapter mDetailItemPagerAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_item_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Get item ID
        Intent intent = getIntent();
        int itemId = intent.getIntExtra(ITEM_ID, -1);

        // Create a list of item's IDs would be in the pager
        long feedID = SharedFileHandler.getSharedPrefFile(this).getInt(FeedItemsFragment.FEED_ID_ARG, -1);
        Cursor cursor = getCompatibleCursorForFeedID(feedID);
        ArrayList<Integer> itemIDs = new ArrayList<Integer>();
        while (cursor.moveToNext()) {
            // Copy data from cursor to array
            itemIDs.add(cursor.getInt(0));
        }

        mDetailItemPagerAdapter = new DetailItemPagerAdapter(itemIDs, getSupportFragmentManager());
        ViewPager viewPager = (ViewPager) findViewById(R.id.detail_item_pager);
        viewPager.setAdapter(mDetailItemPagerAdapter);
        viewPager.setCurrentItem(itemIDs.indexOf(itemId));
    }

    private Cursor getCompatibleCursorForFeedID(long feedID) {
        if (feedID == FeedNamesViewController.HOME_ID) {
            return  this.getContentResolver().query(
                    RSSFeedContract.ItemEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    RSSFeedContract.ItemEntry.COLUMN_PUBLISHED_DATETEXT + " DESC"
            );
        }
        else if (feedID == FeedNamesViewController.SAVED_FOR_LATER_ID){
            return  this.getContentResolver().query(
                    RSSFeedContract.ItemEntry.CONTENT_URI,
                    new String[]{RSSFeedContract.ItemEntry._ID},
                    RSSFeedContract.ItemEntry.COLUMN_SAVED + " = ?",
                    new String[]{Integer.toString(ItemState.SAVED)},
                    RSSFeedContract.ItemEntry.COLUMN_PUBLISHED_DATETEXT + " DESC"
            );
        }
        else {
            return this.getContentResolver().query(
                    RSSFeedContract.ItemEntry.CONTENT_URI,
                    new String[]{RSSFeedContract.ItemEntry._ID},
                    RSSFeedContract.ItemEntry.COLUMN_FEED_ID + " = ?",
                    new String[]{Long.toString(feedID)},
                    RSSFeedContract.ItemEntry.COLUMN_PUBLISHED_DATETEXT + " DESC"
            );
        }

    }

}
