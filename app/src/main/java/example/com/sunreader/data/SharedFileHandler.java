package example.com.sunreader.data;


import android.content.Context;
import android.content.SharedPreferences;

import example.com.sunreader.R;

public class SharedFileHandler {
    public static SharedPreferences getSharedPrefFile(Context context) {
        return  context.getSharedPreferences(
                context.getString(R.string.reference_file_key),
                Context.MODE_PRIVATE
        );
    }
}
