package example.com.sunreader.data;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateConverter {
    public static final String DATE_FORMAT_IN_DB = "yyyy-MM-dd HH:mm:SS";
    public static final String DATE_FORMAT_READABLE = "MMM dd, yyyy HH:mm";
    public static String getReadableDate(String dateStr) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_IN_DB);
        Date date = dateFormat.parse(dateStr);
        dateFormat = new SimpleDateFormat(DATE_FORMAT_READABLE);
        return dateFormat.format(date);
    }

    public static String getDistanceTimeFromNow(String dateStr) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_IN_DB);
        Date date = dateFormat.parse(dateStr);
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        long time = now.getTime() - date.getTime();
        return convertFromSeconds(time/1000);
    }

    public static String convertFromSeconds(long seconds) {
        long minute = seconds/60;
        if (minute >= 60) {
            long hour = minute/60;
            if (hour >= 60) {
                long day = hour/24;
                return day + " days";
            }
            else {
                return hour + " hours";
            }
        }
        else {
            return minute + " minutes";
        }
    }
}
