package example.com.sunreader.controller;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;

import example.com.sunreader.DetailItemActivity;
import example.com.sunreader.R;
import example.com.sunreader.data.DateConverter;
import example.com.sunreader.data.PicassoImageTarget;
import example.com.sunreader.data.RSSFeedContract;

public class DetailItemViewController implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String LOG_TAG = DetailItemActivity.class.getSimpleName();
    private Spanned spanned;
    Activity mActivity;
    private View mRootView;
    private TextView mTextView;
    private Spanned mSpanned;

    public DetailItemViewController(Activity activity, View rootView) {
        mActivity = activity;
        mRootView = rootView;
    }

    public View getRootView() {
        return mRootView;
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
        //TODO Print rootView hashcode
        Log.v(LOG_TAG, "mRootView in DetailItemViewController: " + mRootView.hashCode());
        // Setup details view
        String htmlDetail = data.getString(data.getColumnIndex(RSSFeedContract.ItemEntry.COLUMN_CONTENT));

        if (mTextView == null) {
            mTextView = (TextView) mRootView.findViewById(R.id.detail_textview);
        }
        if (mSpanned == null) {
            mSpanned = Html.fromHtml(htmlDetail, new ImageGetter(mTextView, spanned), null);
        }
        mTextView.setText(spanned);

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

    class ImageGetter implements Html.ImageGetter {
        TextView mTextView;
        Spanned mSpanned;

        public ImageGetter(TextView textView, Spanned sp) {
            mTextView = textView;
            mSpanned = sp;
        }
        @Override
        public Drawable getDrawable(String s) {
            Log.v(LOG_TAG, "Source: " + s);
//            PicassoImageTarget.imageTargets.add(new PicassoImageTarget(mActivity, mTextView, mSpanned));
//            PicassoImageTarget imageTarget = (PicassoImageTarget) PicassoImageTarget.imageTargets.get(
//                    PicassoImageTarget.imageTargets.size() - 1);
            PicassoImageTarget imageTarget = new PicassoImageTarget(mActivity, mTextView, mSpanned);
            mTextView.setTag(imageTarget);
            Picasso.with(mActivity)
                    .load(s)
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(imageTarget);

            return imageTarget.getPlaceHolderDrawable();
        }
    }

}