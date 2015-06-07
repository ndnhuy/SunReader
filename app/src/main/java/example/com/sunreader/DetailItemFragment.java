package example.com.sunreader;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailItemFragment extends Fragment {

    private String mFeedTitle;

    public DetailItemFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFeedTitle = getArguments().getString("FEED_TITLE");
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
        bundle.putString("FEED_TITLE", feedTitle);
        detailItemFragment.setArguments(bundle);
        return detailItemFragment;
    }
}
