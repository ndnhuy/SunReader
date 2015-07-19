package example.com.sunreader.controller;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import java.util.ArrayList;
import java.util.List;

import example.com.sunreader.FeedItemsFragment;

public class ItemListPagerAdapter extends FragmentStatePagerAdapter {
    public static List<Integer> feedIDs = new ArrayList<Integer>();
    public ItemListPagerAdapter(FragmentManager fm) {
        super(fm);

    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt(FeedItemsFragment.FEED_ID_ARG, feedIDs.get(position));
        FeedItemsFragment feedItemsFragment = new FeedItemsFragment();
        feedItemsFragment.setArguments(bundle);
        return feedItemsFragment;
    }

    @Override
    public int getItemPosition(Object object){
        return PagerAdapter.POSITION_NONE;
    }
    @Override
    public int getCount() {
        return feedIDs.size();
    }

    public static void addFeedIdToBeShown(int feedId) {

        feedIDs.add(feedId);
    }

    public static void removeFeedId(int feedId) {
        feedIDs.remove(feedIDs.indexOf(feedId));
    }


}
