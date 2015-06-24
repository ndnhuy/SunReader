package example.com.sunreader.data;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;

import example.com.sunreader.R;

public class ImageHandler {
    private final String LOG_TAG = ImageHandler.class.getSimpleName();
    public static final String BASE_FAVICON_URL = "http://www.google.com/s2/favicons?domain=";

    Bitmap mBitmap = null;
    Context mContext;
    public ImageHandler(Context context) {
        mContext = context;
    }


    public String extractThumnailFromContent(int itemId) {
        // Get content based on the id
        Cursor cursor = mContext.getContentResolver().query(
                RSSFeedContract.ItemEntry.buildItemUri(itemId),
                new String[] {RSSFeedContract.ItemEntry.COLUMN_CONTENT},
                null,
                null,
                null
        );
        if (cursor.moveToFirst()) {
            String rawContent = cursor.getString(0);
            byte[] bytes = null;
            String htmlContent = "";
            try {
                bytes = rawContent.getBytes("UTF-8");
                htmlContent = new String(bytes, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            // Extract the first image from the content
            Document doc = Jsoup.parse(htmlContent);
            Elements elements = doc.getElementsByTag("img");
            String srcImage = "";
            if (elements.size() > 0)
                srcImage = elements.get(0).absUrl("src");

            return srcImage;
        }
        return "";
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


    public void loadingImageFromFileAndBindToView(String dirName, String fileName, ImageView imgView) {
        new LoadingImageFromFileAndBindToView(dirName, fileName, imgView).execute();
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
                Log.v(LOG_TAG, "DOWNLOAD IMAGES");
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
            if (result != null) {
                Log.v(LOG_TAG, "DOWNLOAD IMAGES SUCCESSFUL");
                new InternalStorageHandler(mContext).saveImage(result, mDirName, mFileName);
            }
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


    public class LoadingImageFromFileAndBindToView extends AsyncTask<Void, Void, Bitmap> {
        private final String LOG_TAG = LoadingImageFromFileAndBindToView.class.getSimpleName();

        private final WeakReference<ImageView> imageViewRef;

        String mDirName;
        String mFileName;
        ImageView mImgView;


        public LoadingImageFromFileAndBindToView(String dirName, String fileName, ImageView imgView) {
                mDirName = dirName;
                mFileName = fileName;
                mImgView = imgView;
                imageViewRef = new WeakReference<ImageView>(imgView);
        }


        @Override
        protected Bitmap doInBackground(Void... voids) {
            Bitmap bmp = null;
            Log.v(LOG_TAG, "LOADING THUMNAILS");
            bmp = new InternalStorageHandler(mContext).loadImageFromStorage(
                    mDirName,
                    mFileName
            );

            return bmp;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (imageViewRef != null && bitmap != null) {
                final ImageView imgView = imageViewRef.get();
                if (imgView != null) {
                    imgView.setImageBitmap(bitmap);
                    Log.v(LOG_TAG, "LOADING THUMNAILS SUCCESSFUL");
                }
            }
            else {
                final ImageView imgView = imageViewRef.get();
                if (imgView != null)
                    imgView.setImageResource(R.mipmap.ic_launcher);
            }
        }
    }


}
