package example.com.sunreader;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.io.InputStream;

import example.com.sunreader.controller.ItemsUpdater;
import example.com.sunreader.controller.ItemsViewController;
import example.com.sunreader.data.InternalStorageHandler;
import example.com.sunreader.data.RSSFeedContract;


public class FeedItemsFragment extends Fragment {
    private static final int FEED_LOADER = 0;
    private static final String LOG_TAG = FeedItemsFragment.class.getSimpleName();
    public static final String FEED_ID_ARG = "feed_id";

    private View mRootView;
    private ItemsViewController mItemsViewController;
    private SimpleCursorAdapter mFeedItemsAdapter = null;


    public FeedItemsFragment() {

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.feed_items_list_fragment, container, false);

        mFeedItemsAdapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.one_item_in_list,
                null,
                new String[]{RSSFeedContract.ItemEntry.COLUMN_TITLE,
                                RSSFeedContract.ItemEntry.COLUMN_CONTENT_SNIPPET,
                                RSSFeedContract.ItemEntry.COLUMN_AUTHOR,
                                RSSFeedContract.ItemEntry.COLUMN_PUBLISHED_DATETEXT},
                new int[]{R.id.title_item_in_list_textview,
                        R.id.contentsnippet_item_in_list_textview,
                        R.id.author_item_in_list_textview,
                        R.id.date_item_in_list_textview},
                0
        );

        ListView listView = (ListView) mRootView.findViewById(R.id.listview_feeds);
        listView.setAdapter(mFeedItemsAdapter);


        mItemsViewController = new ItemsViewController(getActivity(), mFeedItemsAdapter);
        listView.setOnItemClickListener(mItemsViewController);
        listView.setOnItemLongClickListener(mItemsViewController);

        mFeedItemsAdapter.setViewBinder(mItemsViewController.createItemsViewBinder());


        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(FEED_LOADER, getArguments(), mItemsViewController);

        // Set pull to refresh
        final SwipeRefreshLayout swipeLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipe_container);

        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Get id from shared file
                SharedPreferences sharedFile = getActivity().getSharedPreferences(
                        getActivity().getString(R.string.reference_file_key),
                        Context.MODE_PRIVATE
                );
                int feedId = sharedFile.getInt(FeedItemsFragment.FEED_ID_ARG, -1);

                FeedItemsFragment feedItemsFragment = new FeedItemsFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(FeedItemsFragment.FEED_ID_ARG, feedId);
                feedItemsFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, feedItemsFragment)
                        .commit();


                new ItemsUpdater(getActivity(), feedId).execute();
            }
        });

        super.onActivityCreated(savedInstanceState);
    }

    //TODO test
    // DownloadImage AsyncTask
    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... URL) {

            String imageURL = URL[0];

            Bitmap bitmap = null;
            try {
                // Download Image from URL
                InputStream input = new java.net.URL(imageURL).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // Set the bitmap into ImageView
            //TODO test save and load image in internal storage
            InternalStorageHandler handler = new InternalStorageHandler(getActivity());
            String dirPath = new InternalStorageHandler(getActivity()).saveImage(result, InternalStorageHandler.FEED_ICON_DIRECTORY_NAME, "2.jpg");

            Log.v(LOG_TAG, "Dir path: " + dirPath);

            Bitmap loadedBmp = handler.loadImageFromStorage(InternalStorageHandler.FEED_ICON_DIRECTORY_NAME, "2.jpg");

        }
    }


}
