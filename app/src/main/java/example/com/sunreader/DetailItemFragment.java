package example.com.sunreader;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailItemFragment extends Fragment {
    private String LOG_TAG = DetailItemActivity.class.getSimpleName();
    private String mFeedTitle;

    public DetailItemFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG + "/onCreate()", "mFeedTitle = " + mFeedTitle);
        super.onCreate(savedInstanceState);
        mFeedTitle = getArguments().getString(DetailItemActivity.MY_INTENT_MESSAGE);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.detail_item_fragment, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.detail_item_fragment_textview);
        textView.setText(mFeedTitle);
        return rootView;
    }

    public static Fragment newInstance(String feedTitle) {
        DetailItemFragment detailItemFragment = new DetailItemFragment();
        Bundle bundle = new Bundle();
        bundle.putString(DetailItemActivity.MY_INTENT_MESSAGE, feedTitle);
        detailItemFragment.setArguments(bundle);
        return detailItemFragment;
    }
}
