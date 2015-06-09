package example.com.sunreader.data;


import android.os.AsyncTask;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import example.com.sunreader.value_object.RssItem;

public class RssService extends AsyncTask<URL, String, RssItem[]> {

    @Override
    protected RssItem[] doInBackground(URL... URLs) {
        List<RssItem> rssItems = null;
        RssParser parser = new RssParser();
        try {
            publishProgress();
            URLConnection urlConnection = URLs[0].openConnection();
            InputStream inputStream = urlConnection.getInputStream();
            rssItems = parser.parse(inputStream);


        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        RssItem[] rssItemsArr = new RssItem[rssItems.size()];
        rssItems.toArray(rssItemsArr);


        return rssItemsArr;
    }
}
