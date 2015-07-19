package example.com.sunreader.data;


import android.content.Context;
import android.content.SharedPreferences;

import example.com.sunreader.FeedItemsFragment;
import example.com.sunreader.R;

public class SharedFileHandler {
    public static SharedPreferences getSharedPrefFile(Context context) {
        return  context.getSharedPreferences(
                context.getString(R.string.reference_file_key),
                Context.MODE_PRIVATE
        );
    }

    public static void saveFeedIdToSharedPrefFile(Context context, int feedID) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.reference_file_key),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(FeedItemsFragment.FEED_ID_ARG, feedID);
        editor.commit();
    }

    public static void saveFeedTitleToSharedPrefFile(Context context, String feedTitle) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.reference_file_key),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(FeedItemsFragment.FEED_TITLE_ARG, feedTitle);
        editor.commit();
    }
}
