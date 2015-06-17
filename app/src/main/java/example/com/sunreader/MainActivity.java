package example.com.sunreader;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import example.com.sunreader.controller.FeedNamesViewController;
import example.com.sunreader.data.RSSFeedContract;
import example.com.sunreader.data.RssService;


public class MainActivity extends ActionBarActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int FEED_NAME_LOADER = 1;
    ActionBarDrawerToggle mDrawerToggle;
    DrawerLayout mDrawerLayout;
    SimpleCursorAdapter mFeedNamesAdapter;
    FeedNamesViewController mFeedNamesViewController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getIntent() != null) {
            handleIntent(getIntent());
        }

        setUpBasicUI();

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
        mFeedNamesViewController = new FeedNamesViewController(this, mFeedNamesAdapter, getSupportFragmentManager());
        listView.setOnItemClickListener(mFeedNamesViewController);
        getSupportLoaderManager().initLoader(FEED_NAME_LOADER, null, mFeedNamesViewController);

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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(
                        getComponentName()
                ));
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
    protected void onResume() {
        getSupportLoaderManager().restartLoader(FEED_NAME_LOADER, null, mFeedNamesViewController);
        super.onResume();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Toast.makeText(this, query, Toast.LENGTH_SHORT).show();
        }
    }
}
