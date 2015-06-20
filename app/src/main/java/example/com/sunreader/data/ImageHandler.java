package example.com.sunreader.data;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;

public class ImageHandler {
    public static final String BASE_FAVICON_URL = "http://www.google.com/s2/favicons?domain=";

    Bitmap mBitmap = null;
    Context mContext;
    public ImageHandler(Context context) {
        mContext = context;
    }

    public void saveImage(String url, String dirName, String fileName) {
        new ImageDownloadAndSaveToStorage(url, dirName, fileName).execute();
    }

    public void displayIconOfFeed(String fileName, ImageView imgView) {
        Bitmap bitmap = new InternalStorageHandler(mContext).loadImageFromStorage(
                            InternalStorageHandler.FEED_ICON_DIRECTORY_NAME,
                            fileName
                        );
        imgView.setImageBitmap(bitmap);
    }
    public void displayImage(String url, ImageView imgView) {
        new ImageDownloadAndBindToView(url, imgView).execute();
    }

    public class ImageDownloadAndSaveToStorage extends AsyncTask<Void, Void, Bitmap> {
        String mUrl;
        String mDirName;
        String mFileName;

        public ImageDownloadAndSaveToStorage(String url, String dirName, String fileName) {
            mUrl = url;
            mDirName = dirName;
            mFileName = fileName;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {

            Bitmap bitmap = null;
            try {
                // Download Image from URL
                InputStream input = new java.net.URL(mUrl).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            new InternalStorageHandler(mContext).saveImage(result, mDirName, mFileName);
        }
    }

    public class ImageDownloadAndBindToView extends AsyncTask<Void, Void, Bitmap> {
        String mUrl;
        ImageView mImgView;

        public ImageDownloadAndBindToView(String url, ImageView imgView) {
            mUrl = url;
            mImgView = imgView;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {

            Bitmap bitmap = null;
            try {
                // Download Image from URL
                InputStream input = new java.net.URL(mUrl).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (mImgView != null)
                mImgView.setImageBitmap(result);
        }
    }


}
