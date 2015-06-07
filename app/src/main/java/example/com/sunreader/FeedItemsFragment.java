package example.com.sunreader;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import example.com.sunreader.data.RssParser;
import example.com.sunreader.value_object.RssItem;


public class FeedItemsFragment extends Fragment {

    private View mRootView;
    private ArrayAdapter<String> mFeedItemsAdapter;

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
        new RssFeedsDownloader().execute("http://www.pcworld.com/index.rss");

        ListView listView = (ListView) mRootView.findViewById(R.id.listview_feeds);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String feedTitle = (String) adapterView.getItemAtPosition(position);
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, DetailItemFragment.newInstance(feedTitle))
                        .commit();
            }
        });

        return mRootView;
    }

    private class RssFeedsDownloader extends AsyncTask<String, Void, RssItem[]> {

        @Override
        protected RssItem[] doInBackground(String... urlStrs) {
            List<RssItem> rssItems = null;
            RssParser parser = new RssParser();
            try {
                rssItems = parser.parse(
                        new URL(urlStrs[0])
                                .openConnection().getInputStream()
                );
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            RssItem[] rssItemsArr = new RssItem[rssItems.size()];
            rssItems.toArray(rssItemsArr);


            return rssItemsArr;
        }

        @Override
        protected void onPostExecute(RssItem[] rssItems) {
            ArrayList<String> feedTitles = new ArrayList<String>();
            for (RssItem item : rssItems) {
                feedTitles.add(item.getTitle());
            }

            mFeedItemsAdapter = new ArrayAdapter<String>(
                    getActivity(),
                    R.layout.list_rss_feeds,
                    R.id.list_rss_feed_textview,
                    feedTitles
            );

            ListView listView = (ListView) mRootView.findViewById(R.id.listview_feeds);
            listView.setAdapter(mFeedItemsAdapter);
        }
    }
}
