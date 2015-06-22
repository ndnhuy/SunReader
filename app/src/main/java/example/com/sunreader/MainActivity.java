package example.com.sunreader;

import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import example.com.sunreader.controller.FeedNamesViewController;
import example.com.sunreader.data.ImageHandler;
import example.com.sunreader.data.RSSFeedContract;


public class MainActivity extends ActionBarActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int FEED_NAME_LOADER = 1;

    ActionBarDrawerToggle mDrawerToggle;
    DrawerLayout mDrawerLayout;
    SimpleCursorAdapter mFeedNamesAdapter;
    FeedNamesViewController mFeedNamesViewController;
    MenuItem searchMenuItem;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        // Set onViewBinding
        mFeedNamesAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                switch (columnIndex) {
                    case FeedNamesViewController.COLUMN_TITLE_INDEX: {
                        ((TextView) view).setText(cursor.getString(FeedNamesViewController.COLUMN_TITLE_INDEX));
                        break;
                    }
                }

                // Load icon from internal storagee
                Log.v("TEST", "LINK " + cursor.getString(FeedNamesViewController.COLUMN_ID_INDEX));
                ViewGroup viewGroup = (ViewGroup) view.getParent();
                ImageView imgView = (ImageView) viewGroup.findViewById(R.id.feed_icon_imageview);
                if (imgView != null) {
                    new ImageHandler(getApplicationContext()).displayIconOfFeed
                            (
                                    cursor.getString(FeedNamesViewController.COLUMN_ID_INDEX) + ".jpg",
                                    imgView
                                    );

                }
                else {
                    Log.e(LOG_TAG, "ImageView is null");
                }

                return false;
            }
        });

        getSupportLoaderManager().initLoader(FEED_NAME_LOADER, null, mFeedNamesViewController);


        if (savedInstanceState == null) {
            FeedItemsFragment feedItemsFragment = new FeedItemsFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(FeedItemsFragment.FEED_ID_ARG, -1);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, feedItemsFragment)
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

        searchMenuItem = menu.findItem(R.id.search);

        SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(searchMenuItem);
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
        switch (id) {
            case R.id.action_settings: {
                Log.v(LOG_TAG, "CLICK on setting");
                break;
            }
            case R.id.action_refresh: {
                // Get id from shared file
                SharedPreferences sharedFile = this.getSharedPreferences(
                        this.getString(R.string.reference_file_key),
                        Context.MODE_PRIVATE
                );
                int feedId = sharedFile.getInt(FeedItemsFragment.FEED_ID_ARG, -1);

                FeedItemsFragment feedItemsFragment = new FeedItemsFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(FeedItemsFragment.FEED_ID_ARG, feedId);
                feedItemsFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, feedItemsFragment)
                        .commit();

                //Toast.makeText(this, Integer.toString(feedId), Toast.LENGTH_SHORT).show();
                break;
            }
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        getSupportLoaderManager().restartLoader(FEED_NAME_LOADER, null, mFeedNamesViewController);

        if (searchMenuItem != null)
            MenuItemCompat.collapseActionView(searchMenuItem);

        super.onResume();
    }



}
