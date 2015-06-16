package example.com.sunreader;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import example.com.sunreader.controller.ItemsViewController;
import example.com.sunreader.data.RSSFeedContract;


public class FeedItemsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int FEED_LOADER = 0;
    private static final String LOG_TAG = FeedItemsFragment.class.getSimpleName();


    private View mRootView;
    private SimpleCursorAdapter mFeedItemsAdapter = null;


    public FeedItemsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.feed_items_list_fragment, container, false);
       // new RssService.RssFeedsDownloader(getActivity()).execute("http://www.androidcentral.com/rss.xml");

        mFeedItemsAdapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.one_item_in_list,
                null,
                new String[] {RSSFeedContract.ItemEntry.COLUMN_TITLE},
                new int[] {R.id.item_in_list_textview},
                0
        );

        ListView listView = (ListView) mRootView.findViewById(R.id.listview_feeds);
        listView.setAdapter(mFeedItemsAdapter);
        listView.setOnItemClickListener(new ItemsViewController(getActivity(), mFeedItemsAdapter));

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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(FEED_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        int feedId = -1;
        Bundle feedIdArg = getArguments();
        if (feedIdArg != null) {
            feedId = feedIdArg.getInt(RSSFeedContract.ItemEntry.COLUMN_FEED_ID);
        }
        return new CursorLoader(
                getActivity(),
                RSSFeedContract.ItemEntry.buildItemWithFeedId(feedId),
                new String[] {RSSFeedContract.ItemEntry._ID, RSSFeedContract.ItemEntry.COLUMN_TITLE},
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mFeedItemsAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mFeedItemsAdapter.swapCursor(null);
    }
}
