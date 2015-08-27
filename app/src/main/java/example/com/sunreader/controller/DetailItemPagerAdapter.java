package example.com.sunreader.controller;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.HashMap;
import java.util.List;

import example.com.sunreader.DetailItemActivity;
import example.com.sunreader.DetailItemFragment;

public class DetailItemPagerAdapter extends FragmentStatePagerAdapter {
    List<Integer> mItemIDs;
    HashMap<Integer, DetailItemFragment> pagerCollection = new HashMap<Integer, DetailItemFragment>();
    public DetailItemPagerAdapter(List<Integer> itemIDs, FragmentManager fm) {
        super(fm);
        mItemIDs = itemIDs;
    }

    @Override
    public Fragment getItem(int position) {
        if (pagerCollection.containsKey(mItemIDs.get(position))) {
            return pagerCollection.get(mItemIDs.get(position));
        }
        else {
            Bundle bundle = new Bundle();
            bundle.putInt(DetailItemActivity.ITEM_ID, mItemIDs.get(position));
            DetailItemFragment detailItemFragment = new DetailItemFragment();
            detailItemFragment.setArguments(bundle);

            pagerCollection.put(mItemIDs.get(position), detailItemFragment);

            return detailItemFragment;
        }
    }

    @Override
    public int getCount() {
        return mItemIDs.size();
    }
}
