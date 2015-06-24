package example.com.sunreader.data;


import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class InternalStorageHandler {
    private final String LOG_TAG = InternalStorageHandler.class.getSimpleName();
    public static final String FEED_ICON_DIRECTORY_NAME = "feed_img";
    public static final String ITEM_IMAGE_DIRECTORY_NAME = "item_img";

    private Context mContext;


    public InternalStorageHandler(Context context) {
        mContext = context;
    }

    public String saveImage(Bitmap bitmap, String dirName, String name) {
        ContextWrapper cw = new ContextWrapper(mContext);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir(dirName, Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, name);


        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(mypath);

            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {

            e.printStackTrace();
        }
        return directory.getAbsolutePath();
    }

    public Bitmap loadImageFromStorage(String dirName, String name)
    {
        ContextWrapper cw = new ContextWrapper(mContext);
        try {
            File directory = cw.getDir(dirName, Context.MODE_PRIVATE);

            File f = new File(directory, name);
            FileInputStream fis = new FileInputStream(f);
            Bitmap b = BitmapFactory.decodeStream(fis);
            return b;
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public boolean deleteFile(String dirName, String fileName) {
        File dir = mContext.getDir(dirName, Context.MODE_PRIVATE);
        File fileTodelete = new File(dir, fileName);
        if (fileTodelete.exists()) {
            fileTodelete.delete();
            return true;
        }

        return false;
    }


}
