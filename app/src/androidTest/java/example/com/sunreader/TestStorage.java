package example.com.sunreader;


import android.test.AndroidTestCase;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class TestStorage extends AndroidTestCase {
    public void test() throws IOException {
        String html = "<div><div><div><p>2013 Moto X owners, your wait is finally over: Android 5.1 Lollipop is on its way.</p> <p><a href=\"http://www.androidcentral.com/2013-moto-x-android-51-update-begins-rolling-out-us-brazil-and-rogers\" title=\"2013 Moto X Android 5.1 update begins rolling out in the U.S., Brazil, and Canada\"><img src=\"http://www.androidcentral.com/sites/androidcentral.com/files/styles/large_wm_blw/public/article_images/2015/03/moto-x-2013-lollipop.jpg?itok=UHHekwwW\"></a></p> <p>Motorola's David Schuster once again took to Google Plus today to announce that the <a href=\"http://www.androidcentral.com/lollipop\">Android 5.1 Lollipop</a> update for the <a href=\"http://www.androidcentral.com/moto-x-2013\">2013 Moto X</a> is now rolling out for U.S. and Brazil retail devices, along with the Rogers variant in Canada.</p> </div></div></div><img width=\"1\" height=\"1\" src=\"http://androidcentral.com.feedsportal.com/c/33995/f/616884/s/47683104/sc/15/mf.gif\" border=\"0\"><br><br><br><a href=\"http://rc.feedsportal.com/r/228766881757/u/49/f/616884/c/33995/s/47683104/sc/15/rc/1/rc.htm\" rel=\"nofollow\"><img src=\"http://rc.feedsportal.com/r/228766881757/u/49/f/616884/c/33995/s/47683104/sc/15/rc/1/rc.img\" border=\"0\"></a><br><a href=\"http://rc.feedsportal.com/r/228766881757/u/49/f/616884/c/33995/s/47683104/sc/15/rc/2/rc.htm\" rel=\"nofollow\"><img src=\"http://rc.feedsportal.com/r/228766881757/u/49/f/616884/c/33995/s/47683104/sc/15/rc/2/rc.img\" border=\"0\"></a><br><a href=\"http://rc.feedsportal.com/r/228766881757/u/49/f/616884/c/33995/s/47683104/sc/15/rc/3/rc.htm\" rel=\"nofollow\"><img src=\"http://rc.feedsportal.com/r/228766881757/u/49/f/616884/c/33995/s/47683104/sc/15/rc/3/rc.img\" border=\"0\"></a><br><br><a href=\"http://da.feedsportal.com/r/228766881757/u/49/f/616884/c/33995/s/47683104/sc/15/a2.htm\"><img src=\"http://da.feedsportal.com/r/228766881757/u/49/f/616884/c/33995/s/47683104/sc/15/a2.img\" border=\"0\"></a><img width=\"1\" height=\"1\" src=\"http://pi.feedsportal.com/r/228766881757/u/49/f/616884/c/33995/s/47683104/sc/15/a2t.img\" border=\"0\"><img src=\"http://feeds.feedburner.com/~r/androidcentral/~4/5k2-5p0r6S4\" height=\"1\" width=\"1\" alt=\"\">\"";
        Document doc = Jsoup.parse(html);
        Elements elements = doc.getElementsByTag("img");
        for (Element e : elements) {
            String src = e.absUrl("src");
            Log.v("TEST JSOUP", src);
        }
    }
}
