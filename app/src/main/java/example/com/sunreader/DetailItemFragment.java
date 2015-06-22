package example.com.sunreader;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import java.io.UnsupportedEncodingException;

import example.com.sunreader.data.RSSFeedContract;

public class DetailItemFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private String LOG_TAG = DetailItemActivity.class.getSimpleName();

    private final int DETAIL_ITEM_LOADER = 0;
    private View mRootView;

    public DetailItemFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.detail_item_fragment, container, false);
        // new RssService.RssFeedsDownloader(getActivity()).execute("http://www.androidcentral.com/rss.xml")
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_ITEM_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        int itemId = -1;
        Bundle receivedBundle = getArguments();
        if (receivedBundle != null) {
            itemId = receivedBundle.getInt(DetailItemActivity.ITEM_ID);
        }
        return new CursorLoader(
                getActivity(),
                RSSFeedContract.ItemEntry.buildItemUri(itemId),
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (!data.moveToFirst())
            return;


        String rawDetail = data.getString(data.getColumnIndex(RSSFeedContract.ItemEntry.COLUMN_CONTENT));
        byte[] bytes = null;
        String s = "";
        try {
            bytes = rawDetail.getBytes("UTF-8");
            s = new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Spanned detail = Html.fromHtml(s);
//        TextView textView = (TextView)mRootView.findViewById(R.id.detail_item_fragment_textview);
//        textView.setText(detail);
        WebView view = (WebView) mRootView.findViewById(R.id.webview);
        view.loadData(s,"text/html", null);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
