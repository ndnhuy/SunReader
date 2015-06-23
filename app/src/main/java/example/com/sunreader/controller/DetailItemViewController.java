package example.com.sunreader.controller;


import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.webkit.WebView;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;

import example.com.sunreader.DetailItemActivity;
import example.com.sunreader.R;
import example.com.sunreader.data.RSSFeedContract;

public class DetailItemViewController implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String LOG_TAG = DetailItemActivity.class.getSimpleName();
    Activity mActivity;

    public DetailItemViewController(Activity activity) {
        mActivity = activity;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        int itemId = -1;
        Bundle receivedBundle = args;
        if (receivedBundle != null) {
            itemId = receivedBundle.getInt(DetailItemActivity.ITEM_ID);
        }
        return new CursorLoader(
                mActivity,
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

        bindInformationToViews(data);
    }

    private void bindInformationToViews(Cursor data) {
        // Setup details view
        String rawDetail = data.getString(data.getColumnIndex(RSSFeedContract.ItemEntry.COLUMN_CONTENT));
        byte[] bytes = null;
        String html = "";
        try {
            bytes = rawDetail.getBytes("UTF-8");
            html = new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Log.v(LOG_TAG, "URL: " + html);
        WebView view = (WebView) mActivity.findViewById(R.id.detail_textview);
        view.loadData(html, "text/html", "utf-8");

        // Setup title view
        bindTextToTextView(
                (TextView) mActivity.findViewById(R.id.title_detail_item_textview),
                data.getString(data.getColumnIndex(RSSFeedContract.ItemEntry.COLUMN_TITLE))
        );

        bindTextToTextView(
                (TextView) mActivity.findViewById(R.id.date_detail_item_textview),
                data.getString(data.getColumnIndex(RSSFeedContract.ItemEntry.COLUMN_PUBLISHED_DATETEXT))
        );

        bindTextToTextView(
                (TextView) mActivity.findViewById(R.id.author_detail_item_textview),
                data.getString(data.getColumnIndex(RSSFeedContract.ItemEntry.COLUMN_AUTHOR))
        );
    }
    private void bindTextToTextView(TextView v, String text) {
        v.setText(text);
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


}





//view.setHtmlFromString(html, false);
//        Spanned htmlSpan = Html.fromHtml(html, new Html.ImageGetter() {
//            @Override
//            public Drawable getDrawable(String url) {
//                // Download bitmap
//                Bitmap bitmap = null;
//                try {
//                    // Download Image from URL
//                    InputStream input = new java.net.URL(url).openStream();
//                    // Decode Bitmap
//                    bitmap = BitmapFactory.decodeStream(input);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    return null;
//                }
//
//                Drawable drawable = new BitmapDrawable(getResources(), bitmap);
//                drawable.setBounds(0, 0, 45,45);
//
//                return drawable;
//            }
//        }, null);
//
//        view.setText(htmlSpan);
//        view.invalidate();

//URLImageParser parser = new URLImageParser(view, getActivity());
// Spanned htmlSpan = Html.fromHtml(html, parser, null);
// view.setText(htmlSpan);



//        TextView textView = (TextView)mRootView.findViewById(R.id.detail_item_fragment_textview);
//        textView.setText(detail);