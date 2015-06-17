package example.com.sunreader;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import example.com.sunreader.controller.SearchResultController;
import example.com.sunreader.value_object.RssFeed;

public class SearchResultFragment extends Fragment {

    public static final String QUERY = "query";
    private static final int FEED_LOADER = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.search_results_fragment, container, false);

        final ResultsAdapter resultsAdapter = new ResultsAdapter(getActivity(), new ArrayList<RssFeed>(Arrays.asList(
                new RssFeed[] {
                        new RssFeed("1", "11", "111"),
                        new RssFeed("2", "22", "222"),
                        new RssFeed("3", "33", "333"),
                }
        )));

        ListView listView = (ListView) rootView.findViewById(R.id.results_listview);
        listView.setAdapter(resultsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                RssFeed rssFeed = (RssFeed) resultsAdapter.getItem(i);
                Toast.makeText(getActivity(), rssFeed.getLink(), Toast.LENGTH_SHORT).show();
            }
        });
        if (getArguments() != null) {
            new SearchResultController(getActivity(), resultsAdapter)
                    .execute(getArguments().getString(QUERY));
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

            TextView feedNameTextView = (TextView) convertView.findViewById(R.id.feed_name_textview);
            feedNameTextView.setText(feed.getName());

            return convertView;
        }
    }
}
