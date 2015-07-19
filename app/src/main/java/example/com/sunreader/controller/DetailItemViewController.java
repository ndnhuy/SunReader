package example.com.sunreader.controller;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;

import example.com.sunreader.DetailItemActivity;
import example.com.sunreader.R;
import example.com.sunreader.data.DateConverter;
import example.com.sunreader.data.RSSFeedContract;

public class DetailItemViewController implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String LOG_TAG = DetailItemActivity.class.getSimpleName();
    Activity mActivity;
    private View mRootView;

    public DetailItemViewController(Activity activity, View rootView) {
        mActivity = activity;
        mRootView = rootView;
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
    public void onLoadFinished(Loader<Cursor> loader, final Cursor data) {

        if (!data.moveToFirst())
            return;

        try {
            bindInformationToViews(data);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Set vistwebsite button click
        final Button button = (Button) mActivity.findViewById(R.id.visitwebsite_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = data.getString(data.getColumnIndex(RSSFeedContract.ItemEntry.COLUMN_LINK));
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                mActivity.startActivity(browserIntent);
                if (!url.startsWith("http://") && !url.startsWith("https://"))
                    url = "http://" + url;
            }
        });
    }

    private void bindInformationToViews(Cursor data) throws ParseException {
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

        WebView view = (WebView) mRootView.findViewById(R.id.detail_textview);
        WebSettings ws = view.getSettings();
        ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        ws.getPluginState();
        ws.setPluginState(WebSettings.PluginState.ON);
        ws.setJavaScriptEnabled(true);

        view.loadData(html, "text/html", "utf-8");

        bindTextToTextView(
                (TextView) mRootView.findViewById(R.id.title_detail_item_textview),
                data.getString(data.getColumnIndex(RSSFeedContract.ItemEntry.COLUMN_TITLE))
        );


        String dateStr = data.getString(data.getColumnIndex(RSSFeedContract.ItemEntry.COLUMN_PUBLISHED_DATETEXT));
        bindTextToTextView(
                (TextView) mRootView.findViewById(R.id.date_detail_item_textview),
                DateConverter.getReadableDate(dateStr)
        );

        bindTextToTextView(
                (TextView) mRootView.findViewById(R.id.author_detail_item_textview),
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