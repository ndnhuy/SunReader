package example.com.sunreader;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;


public class RssReaderFragment extends Fragment {

    public RssReaderFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.rss_reader_fragment, container, false);

        ArrayAdapter<String> feedsAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_rss_feeds,
                R.id.list_rss_feed_textview,
                new ArrayList<String>(Arrays.asList(new String[] {
                        "A",
                        "B",
                        "C"
                })
        ));

        ListView listView = (ListView) rootView.findViewById(R.id.listview_feeds);
        listView.setAdapter(feedsAdapter);
        return rootView;
    }
}
