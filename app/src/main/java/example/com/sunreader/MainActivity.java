package example.com.sunreader;

import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
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
import example.com.sunreader.controller.ItemListPagerAdapter;
import example.com.sunreader.controller.ItemsUpdater;
import example.com.sunreader.controller.NetworkChecker;
import example.com.sunreader.controller.SetActionBarTitleTask;
import example.com.sunreader.data.ImageHandler;
import example.com.sunreader.data.RSSFeedContract;
import example.com.sunreader.data.RefreshHandler;
import example.com.sunreader.data.SharedFileHandler;


public class MainActivity extends ActionBarActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int FEED_NAME_LOADER = 1;

    ActionBarDrawerToggle mDrawerToggle;
    DrawerLayout mDrawerLayout;
    SimpleCursorAdapter mFeedNamesAdapter;
    FeedNamesViewController mFeedNamesViewController;
    MenuItem searchMenuItem;
    ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NetworkChecker.check(this);
        // Update all feeds
        new ItemsUpdater(this, -1).execute();
        setUpBasicUI();
        setUpNavDrawer();

        //TODO LOG_TAG here
        Log.v(LOG_TAG, "onCreate");
        // Get all feeds id
        Cursor cursor = this.getContentResolver().query(
                RSSFeedContract.FeedEntry.CONTENT_URI,
                new String[] {RSSFeedContract.FeedEntry._ID},
                null,
                null,
                RSSFeedContract.FeedEntry._ID + " ASC"
        );
        // Create a list of feed's IDs would be in the pager
        ItemListPagerAdapter.feedIDs.add(FeedNamesViewController.HOME_ID);
        while (cursor.moveToNext()) {
            // Copy data from cursor to array
            ItemListPagerAdapter.feedIDs.add(cursor.getInt(0));
        }
        ItemListPagerAdapter.feedIDs.add(FeedNamesViewController.SAVED_FOR_LATER_ID);

        ItemListPagerAdapter itemListPagerAdapter = new ItemListPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.feed_pager);
        mViewPager.setAdapter(itemListPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                //TODO delete here
                Log.v(LOG_TAG, "onPageSelected(): " + position);
                    int feedId = ItemListPagerAdapter.feedIDs.get(position);
                    SharedFileHandler.saveFeedIdToSharedPrefFile(getApplicationContext(), feedId);

                    // Update
                    new SetActionBarTitleTask(getApplicationContext(), getSupportActionBar()).execute(feedId);
                    new ItemsUpdater(getBaseContext(), feedId).execute();
            }


            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }




    private void setUpBasicUI() {

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setSubtitle("HOME");

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

    private void setUpNavDrawer() {
        // Setup side bar
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
        listView.setOnItemLongClickListener(mFeedNamesViewController);
        // Set onViewBinding
        mFeedNamesAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                switch (columnIndex) {
                    case FeedNamesViewController.COLUMN_TITLE_INDEX: {
                        ((TextView) view).setText(cursor.getString(FeedNamesViewController.COLUMN_TITLE_INDEX));
                        ViewGroup viewGroup = (ViewGroup) view.getParent();
                        viewGroup.setBackgroundResource(0);
                        ImageView imgView = (ImageView) viewGroup.findViewById(R.id.feed_icon_imageview);
                        if (imgView != null) {
                            long feedId = cursor.getLong(FeedNamesViewController.COLUMN_ID_INDEX);
                            if (feedId == FeedNamesViewController.HOME_ID) {
                                imgView.setImageResource(R.mipmap.ic_show_all);
                            } else if (feedId == FeedNamesViewController.SAVED_FOR_LATER_ID) {
                                imgView.setImageResource(R.mipmap.ic_show_saved);
                                viewGroup.setBackgroundResource(R.drawable.box_layout);
                            } else {

                                new ImageHandler(getApplicationContext()).displayIconOfFeed
                                        (
                                                cursor.getString(FeedNamesViewController.COLUMN_ID_INDEX) + ".jpg",
                                                imgView
                                        );
                            }
                        } else {
                            Log.e(LOG_TAG, "ImageView is null");
                        }
                        break;
                    }
                }
                return false;
            }
        });
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
            case R.id.search: {
                mDrawerLayout.closeDrawers();
                break;
            }
            case R.id.action_refresh: {
                RefreshHandler.refreshCurrentFeed(this);
                break;
            }
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {

    }

    @Override
    protected void onResume() {
        //TODO LOG_TAG here
        Log.v(LOG_TAG, "onResume()");
        if (getSupportLoaderManager().getLoader(FEED_NAME_LOADER) != null) {
            getSupportLoaderManager().restartLoader(FEED_NAME_LOADER, null, mFeedNamesViewController);
        } else {
            getSupportLoaderManager().initLoader(FEED_NAME_LOADER, null, mFeedNamesViewController);
        }

        // Reload fragment
        // Get id from shared file
        SharedPreferences sharedFile = this.getSharedPreferences(
                this.getString(R.string.reference_file_key),
                Context.MODE_PRIVATE
        );
        int feedId = sharedFile.getInt(FeedItemsFragment.FEED_ID_ARG, -1);


        mViewPager.getAdapter().notifyDataSetChanged();

        //TODO delete
//        Bundle feedIdBundle = new Bundle();
//        feedIdBundle.putInt(FeedItemsFragment.FEED_ID_ARG, feedId);
//        FeedItemsFragment feedItemsFragment = new FeedItemsFragment();
//        feedItemsFragment.setArguments(feedIdBundle);
//        getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.container, feedItemsFragment)
//                .commit();


        if (searchMenuItem != null)
            MenuItemCompat.collapseActionView(searchMenuItem);

        super.onResume();
    }


}
