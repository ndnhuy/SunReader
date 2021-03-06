package example.com.sunreader.controller;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;

import org.json.JSONException;

import java.io.IOException;

import example.com.sunreader.MessageFragment;
import example.com.sunreader.R;
import example.com.sunreader.data.ImageHandler;
import example.com.sunreader.data.InternalStorageHandler;
import example.com.sunreader.data.RSSFeedContract;
import example.com.sunreader.data.RssService;
import example.com.sunreader.value_object.RssFeed;


public class SearchResultController extends AsyncTask<String, Void, RssFeed[]> {
    private final String LOG_TAG = SearchResultController.class.getSimpleName();
    private Activity mContext;
    private ArrayAdapter<RssFeed> mFeedsAdapter;
    private ProgressDialog loadingDialog;
    private FragmentManager mFragmentManager;
    public SearchResultController(Activity context, ArrayAdapter<RssFeed> feedsAdapter, FragmentManager fragmentManager) {
        mContext = context;
        mFeedsAdapter = feedsAdapter;
        loadingDialog = new ProgressDialog(mContext);
        mFragmentManager = fragmentManager;
    }

    @Override
    protected RssFeed[] doInBackground(String... urlStrs) {
        try {
            String JSONStr = RssService.downloadFeedJSON(
                    RssService.SEARCH_BY_KEYWORDS, urlStrs[0]).toString();

            return RssService.getFeedFromJSONInSearchByKeyword(JSONStr);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        loadingDialog.setMessage("Loading...");
        loadingDialog.show();
    }

    @Override
    protected void onPostExecute(RssFeed[] rssFeeds) {
        if (rssFeeds == null) {
            mFragmentManager.beginTransaction()
                    .replace(R.id.container, MessageFragment.createWithMessage("No results."))
                    .commit();
            return;
        }

        if (rssFeeds.length == 0) {
            mFragmentManager.beginTransaction()
                    .replace(R.id.container, MessageFragment.createWithMessage("No results."))
                    .commit();
            return;
        }


        for (int i = 0; i < rssFeeds.length; i++) {
            mFeedsAdapter.add(rssFeeds[i]);
        }

        loadingDialog.dismiss();

    }

    public AddButtonOnClickListener createAddButtonOnClickListener(int position) {
        return new AddButtonOnClickListener(position);
    }

    public class AddButtonOnClickListener implements View.OnClickListener {

        private int mPosition;
        public AddButtonOnClickListener(int positionInListView) {
            mPosition = positionInListView;
        }

        @Override
        public void onClick(View view) {
            // Change image of imagebutton to done
            ImageButton imageButton = (ImageButton) view;
            if (imageButton.getTag().toString().equals("added")) {
                return;
            }

            imageButton.setImageResource(R.mipmap.ic_done_grey);

            // Get RssFeed object, save it to database
            RssFeed feed = mFeedsAdapter.getItem(mPosition);
            ContentValues feedValues = new ContentValues();
            feedValues.put(RSSFeedContract.FeedEntry.COLUMN_TITLE, feed.getName());
            feedValues.put(RSSFeedContract.FeedEntry.COLUMN_LINK, feed.getLink());
            feedValues.put(RSSFeedContract.FeedEntry.COLUMN_FEED_URL, feed.getFeedUrl());

            //TODO do data-thing in UI thread, this is so not good
            Uri uri = mContext.getContentResolver().insert(
                    RSSFeedContract.FeedEntry.CONTENT_URI,
                    feedValues
            );
            // Download icon and save it to internal storage
            new ImageHandler(mContext).saveImage(ImageHandler.BASE_FAVICON_URL + feed.getLink(),
                    InternalStorageHandler.FEED_ICON_DIRECTORY_NAME,
                    Long.toString(ContentUris.parseId(uri)) + ".jpg");

            ItemListPagerAdapter.feedIDs.add(ItemListPagerAdapter.feedIDs.size() - 1, (int) ContentUris.parseId(uri));
            //TODO delete here
            Log.v(LOG_TAG, ItemListPagerAdapter.feedIDs.toString());
        }
    }

    //    @Override
//    protected SearchRequest doInBackground(String... urls) {
//        try {
//
//            RssFeed rssFeed = RssService.getFeedFromJSON(
//                    RssService.downloadFeedJSON(urls[0]).toString()
//            );
//            return rssFeed;
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }



}
