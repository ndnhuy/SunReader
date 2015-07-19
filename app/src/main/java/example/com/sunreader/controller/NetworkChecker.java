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
//            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
//            alertDialog.setTitle("Warning");
//            alertDialog.setMessage("No Network. Please check the connection.");
//            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//            alertDialog.show();
            return false;
        }
    }
}
