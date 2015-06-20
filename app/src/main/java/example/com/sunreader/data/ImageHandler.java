package example.com.sunreader.data;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;

public class ImageHandler {
    Bitmap mBitmap = null;
    ImageView mImgView;
    public ImageHandler() {
    }



    public void displayImage(String url, ImageView imgView) {
        new ImageDownloadAndBindToView(url, imgView).execute();
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
            mImgView.setImageBitmap(result);
        }
    }


}
