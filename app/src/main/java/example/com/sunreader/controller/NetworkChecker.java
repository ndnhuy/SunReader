package example.com.sunreader.controller;


import android.content.Context;
import android.net.ConnectivityManager;
import android.widget.Toast;

public class NetworkChecker {
    public static boolean check(Context context) {
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnectedOrConnecting()) {
            return true;
        }
        else {
            // No connectivity - Show alert
            Toast.makeText(context, "No Network. Please check the connection", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
