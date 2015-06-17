package example.com.sunreader.controller;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;

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



    public static class SearchRequest {
        private String mSearchType;
        private String mQuery;

        public SearchRequest(String searchType, String query) {
            mSearchType = searchType;
            mQuery = query;
        }

        public void setSearchType(String searchType) {
            mSearchType = searchType;
        }

        public String getSearchType() {
            return mSearchType;
        }

        public void setQuery(String query) {
            mQuery = query;
        }

        public String getQuery() {
            return mQuery;
        }
    }
}
