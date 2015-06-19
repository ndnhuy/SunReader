package example.com.sunreader.controller;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;

import example.com.sunreader.R;
import example.com.sunreader.data.RSSFeedContract;
import example.com.sunreader.data.RssService;
import example.com.sunreader.value_object.RssFeed;


public class SearchResultController extends AsyncTask<String, Void, RssFeed[]> {

    private Context mContext;
    private ArrayAdapter<RssFeed> mFeedsAdapter;

    public SearchResultController(Context context, ArrayAdapter<RssFeed> feedsAdapter) {
        mContext = context;
        mFeedsAdapter = feedsAdapter;
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
        Toast.makeText(mContext, "Updating...", Toast.LENGTH_SHORT);
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(RssFeed[] rssFeeds) {
        super.onPostExecute(rssFeeds);

        for (int i = 0; i < rssFeeds.length; i++) {
            mFeedsAdapter.add(rssFeeds[i]);
        }
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

            mContext.getContentResolver().insert(
                    RSSFeedContract.FeedEntry.CONTENT_URI,
                    feedValues
            );
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
