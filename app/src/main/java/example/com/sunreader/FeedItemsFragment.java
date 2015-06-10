package example.com.sunreader;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import example.com.sunreader.data.RssService;


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
        new RssService.RssFeedsDownloader(getActivity()).execute("http://www.androidcentral.com/rss.xml");


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


}
