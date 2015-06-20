package example.com.sunreader;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import example.com.sunreader.controller.SearchResultController;
import example.com.sunreader.data.ImageHandler;
import example.com.sunreader.data.RSSFeedContract;
import example.com.sunreader.value_object.RssFeed;

public class SearchResultFragment extends Fragment {

    public static final String QUERY = "query";
    private static final int FEED_LOADER = 0;

    private SearchResultController mSearchResultController;
    private ResultsAdapter resultsAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.search_results_fragment, container, false);

        resultsAdapter = new ResultsAdapter(getActivity(), new ArrayList<RssFeed>());

        ListView listView = (ListView) rootView.findViewById(R.id.results_listview);
        listView.setAdapter(resultsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                RssFeed rssFeed = (RssFeed) resultsAdapter.getItem(i);
                Toast.makeText(getActivity(), rssFeed.getLink(), Toast.LENGTH_SHORT).show();
            }
        });

        mSearchResultController = new SearchResultController(getActivity(), resultsAdapter);
        if (getArguments() != null) {
            mSearchResultController.execute(getArguments().getString(QUERY));
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }




    private class ResultsAdapter extends ArrayAdapter<RssFeed> {

        public ResultsAdapter(Context context, List<RssFeed> feeds) {
            super(context, 0, feeds);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            RssFeed feed = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.one_feed_item_in_results_list, parent, false);

            }

            // Setup icon
            ImageView imgView = (ImageView) convertView.findViewById(R.id.icon_imageview);
            String baseUrl = "http://www.google.com/s2/favicons?domain=";
            new ImageHandler().displayImage(baseUrl + feed.getLink(), imgView);

//            Log.v("SearchResultFragment", baseUrl + feed.getLink());

            TextView feedNameTextView = (TextView) convertView.findViewById(R.id.feed_name_textview);
            feedNameTextView.setText(feed.getName());

            ImageButton imageButton = (ImageButton) convertView.findViewById(R.id.add_imagebutton);
            if (doesExistInDatabase(feed)) {
                imageButton.setTag("added");
                imageButton.setImageResource(R.mipmap.ic_done_grey);
            } else {
                final View finalConvertView = convertView;
                imageButton.setOnClickListener(mSearchResultController.createAddButtonOnClickListener(position));
            }

            return convertView;
        }

        private boolean doesExistInDatabase(RssFeed feed) {
            Cursor cursor = getActivity().getContentResolver().query(
                    RSSFeedContract.FeedEntry.CONTENT_URI,
                    new String[] {RSSFeedContract.FeedEntry._ID},
                    RSSFeedContract.FeedEntry.COLUMN_FEED_URL + " = ?",
                    new String[] {feed.getFeedUrl()},
                    null
            );



            if (cursor.moveToFirst())
                return true;
            else
                return false;
        }
    }
}
