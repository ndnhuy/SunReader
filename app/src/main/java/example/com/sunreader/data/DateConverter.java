package example.com.sunreader.data;


import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConverter {
    public static String getReadableDate(long dateTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm");
        Date date = new Date(dateTime);
        return dateFormat.format(date);
    }
}
