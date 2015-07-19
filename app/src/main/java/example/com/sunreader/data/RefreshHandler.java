package example.com.sunreader.data;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import example.com.sunreader.FeedItemsFragment;
import example.com.sunreader.R;
import example.com.sunreader.controller.ItemsUpdater;

public class RefreshHandler {
    public static void refreshCurrentFeed(Activity activity) {
        // Get id from shared file
        SharedPreferences sharedFile = activity.getSharedPreferences(
                activity.getString(R.string.reference_file_key),
                Context.MODE_PRIVATE
        );
        int feedId = sharedFile.getInt(FeedItemsFragment.FEED_ID_ARG, -1);

        new ItemsUpdater(activity, feedId).execute();
    }
}
