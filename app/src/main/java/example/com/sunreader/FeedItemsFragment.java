package example.com.sunreader;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import example.com.sunreader.value_object.RssFeed;
import example.com.sunreader.value_object.RssItem;


public class FeedItemsFragment extends Fragment {

    private View mRootView;
    private ArrayAdapter<String> mFeedItemsAdapter = null;

    private static final String LOG_TAG = FeedItemsFragment.class.getSimpleName();
    public FeedItemsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.rss_reader_fragment, container, false);
        new RssFeedsDownloader().execute("http://www.androidcentral.com/rss.xml");

//        ListView listView = (ListView) mRootView.findViewById(R.id.listview_feeds);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                String feedTitle = (String) adapterView.getItemAtPosition(position);
//                // Call detail item activity
//                Intent intent = new Intent(getActivity(), DetailItemActivity.class);
//                intent.putExtra(DetailItemActivity.MY_INTENT_MESSAGE, feedTitle);
//                getActivity().startActivity(intent);
//            }
//        });

        return mRootView;
    }

    private class RssFeedsDownloader extends AsyncTask<String, String, RssFeed> {
        private RssFeed getFeedDataFromJSON(String JsonRssString) throws JSONException {
            JSONObject RssJSON = new JSONObject(JsonRssString);
            JSONObject responseDataJSON = RssJSON.getJSONObject("responseData");
            JSONObject feedJSON = responseDataJSON.getJSONObject("feed");
            JSONArray entriesJSON = feedJSON.getJSONArray("entries");

            RssFeed feed = new RssFeed(
                    feedJSON.getString("title"),
                    feedJSON.getString("link"),
                    feedJSON.getString("feedUrl"),
                    null
            );
            for (int i = 0; i < entriesJSON.length(); i++) {
                JSONObject itemJSON = entriesJSON.getJSONObject(i);
                RssItem rssItem = new RssItem(
                        itemJSON.getString("title"),
                        itemJSON.getString("link"),
                        itemJSON.getString("content")
                );
                feed.putItem(rssItem);

                Log.v(LOG_TAG + "/getFeedDataFromJSON(..)", rssItem.getTitle() + rssItem.getLink() + rssItem.getContent());
            }


            return feed;
        }
        @Override
        protected RssFeed doInBackground(String... urlStrs) {
            try {
                final String RSS_BASE_URL = "https://ajax.googleapis.com/ajax/services/feed/load?v=1.0&";
                final String QUERY_PARAM = "q";

                Log.v(LOG_TAG, "Start connecting");

                Uri builtUri = Uri.parse(RSS_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, urlStrs[0])
                        .build();
                URL url = new URL(builtUri.toString());

                // Create a request to Google Feed API, open the connection
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                Log.v(LOG_TAG, "DONE CONNECT");


                // Read the input stream into a String
                InputStream inputStream = connection.getInputStream();
                Log.v(LOG_TAG, "DONE GET INPUT STREAM");
                if (inputStream == null) {
                    Log.v(LOG_TAG, "Input stream is empty ");
                    return null;
                }

                Log.v(LOG_TAG, "Connect successfully.");

                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder builder = new StringBuilder();

                String line = reader.readLine();
                int i = 0;
                while (line != null) {
                    Log.v(LOG_TAG, "loop");
                    builder.append(line);
                    line = reader.readLine();
                }

                if (builder.length() == 0) {
                    return null;
                }

                return getFeedDataFromJSON(builder.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            Toast.makeText(getActivity(), "Downloading...", Toast.LENGTH_SHORT).show();
        }

//        @Override
//        protected void onPostExecute(RssItem[] rssItems) {
//            ArrayList<String> feedTitles = new ArrayList<String>();
//            for (RssItem item : rssItems) {
//                feedTitles.add(item.getTitle());
//            }
//
//            mFeedItemsAdapter = new ArrayAdapter<String>(
//                    getActivity(),
//                    R.layout.list_rss_feeds,
//                    R.id.list_rss_feed_textview,
//                    feedTitles
//            );
//
//            ListView listView = (ListView) mRootView.findViewById(R.id.listview_feeds);
//            listView.setAdapter(mFeedItemsAdapter);
//        }
    }
}
