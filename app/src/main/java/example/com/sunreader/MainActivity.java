package example.com.sunreader;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import example.com.sunreader.data.RSSFeedContract;
import example.com.sunreader.data.RssService;


public class MainActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int FEED_NAME_LOADER = 1;
    ActionBarDrawerToggle mDrawerToggle;
    DrawerLayout mDrawerLayout;
    SimpleCursorAdapter mFeedNamesAdapter;
    ArrayAdapter<String> mFeedArrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpBasicUI();

//        //TEST
//        mFeedArrayAdapter = new ArrayAdapter<String>(
//                this,
//                R.layout.one_feed_name_in_list,
//                R.id.feed_name_textview,
//                new ArrayList<String>(Arrays.asList(new String[] {
//                        "ONE", "TWO", "THREE"
//                }))
//
//        );
//        ListView listView = (ListView) findViewById(R.id.left_drawer);
//        listView.setAdapter(mFeedArrayAdapter);
//        //END TEST---------


        // Set up loader for side bar
        mFeedNamesAdapter = new SimpleCursorAdapter(
                this,
                R.layout.one_feed_name_in_list,
                null,
                new String[] {RSSFeedContract.FeedEntry.COLUMN_TITLE},
                new int[] {R.id.feed_name_textview}
        );
        ListView listView = (ListView) findViewById(R.id.left_drawer);
        listView.setAdapter(mFeedNamesAdapter);

        getSupportLoaderManager().initLoader(FEED_NAME_LOADER, null, this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new FeedItemsFragment())
                    .commit();
        }
    }

    private void setUpBasicUI() {

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.mipmap.ic_drawer,
                R.string.drawer_open,
                R.string.drawer_close
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }


        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Log.v(LOG_TAG, "CLICK on setting");
            new RssService.RssFeedsDownloader(this).execute("http://www.androidauthority.com/feed/");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {

//        mFeedNamesAdapter = new SimpleCursorAdapter(
//                this,
//                R.layout.one_feed_name_in_list,
//                null,
//                new String[] {RSSFeedContract.FeedEntry.COLUMN_TITLE},
//                new int[] {R.id.feed_name_textview}
//        );
//        ListView listView = (ListView) findViewById(R.id.left_drawer);
//        listView.setAdapter(mFeedNamesAdapter);

        return super.onCreateView(parent, name, context, attrs);
    }

    @Override
    protected void onResume() {
        getSupportLoaderManager().restartLoader(FEED_NAME_LOADER, null, this);
        super.onResume();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "inside onCreateLoader");
        return new CursorLoader(
                this,
                RSSFeedContract.FeedEntry.CONTENT_URI,
                new String[]{RSSFeedContract.FeedEntry._ID, RSSFeedContract.FeedEntry.COLUMN_TITLE},
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "inside onLoadFinished");
        mFeedNamesAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mFeedNamesAdapter.swapCursor(null);
    }
}
