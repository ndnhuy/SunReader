package example.com.sunreader.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Spanned;
import android.util.Log;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

public class PicassoImageTarget implements Target {
    private String LOG_TAG = PicassoImageTarget.class.getSimpleName();
    public static List<Target> imageTargets = new ArrayList<Target>();

    private PlaceHolderDrawable mPlaceHolderDrawable = new PlaceHolderDrawable();
    private Context mContext;
    private TextView mTextView;
    private Spanned mSpanned;

    public PicassoImageTarget(Context context, TextView textView, Spanned spanned) {
        mContext = context;
        mTextView = textView;
        mSpanned = spanned;
    }

    @Override

    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        Log.v(LOG_TAG, "onBitmapLoaded()");
        int desireWidth = bitmap.getWidth();
        int desireHeight = bitmap.getHeight();
        Drawable drawable = new BitmapDrawable(mContext.getResources(), Bitmap.createScaledBitmap(bitmap, desireWidth, desireHeight, false));
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

        mPlaceHolderDrawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        mPlaceHolderDrawable.setDrawable(drawable);

        mTextView.setText(mSpanned);

    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {
        Log.v(LOG_TAG, "onBitmapFailed()");
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {
        Log.v(LOG_TAG, "onPrepareLoad()");
    }

    public PlaceHolderDrawable getPlaceHolderDrawable() {
        return mPlaceHolderDrawable;
    }

    class PlaceHolderDrawable extends BitmapDrawable {
        private Drawable mDrawable;

        @Override
        public void draw(Canvas canvas) {
            if (mDrawable != null) {
                Log.v(LOG_TAG, "redraw");
                mDrawable.draw(canvas);
            }
        }

        public Drawable getDrawable() {
            return mDrawable;
        }

        public void setDrawable(Drawable drawable) {
            mDrawable = drawable;
        }
    }
}
