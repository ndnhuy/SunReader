package example.com.sunreader.data;


import java.text.ParseException;
import java.text.SimpleDateFormat;
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
}
