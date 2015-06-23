package example.com.sunreader;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import example.com.sunreader.controller.DetailItemViewController;

public class DetailItemFragment extends Fragment {
    private String LOG_TAG = DetailItemActivity.class.getSimpleName();


    private DetailItemViewController mDetailItemViewController;
    private final int DETAIL_ITEM_LOADER = 0;
    private View mRootView;

    public DetailItemFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDetailItemViewController = new DetailItemViewController(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.detail_item_fragment, container, false);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_ITEM_LOADER, getArguments(), mDetailItemViewController);
        super.onActivityCreated(savedInstanceState);
    }

}
