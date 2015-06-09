package example.com.sunreader.value_object;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RssFeed {
    String mTitle;
    String mLink;
    String mFeedUrl;
    List<RssItem> mItems = null;

    public RssFeed() {
        mItems = new ArrayList<RssItem>();
    }
    public RssFeed(String title, String link, String feedUrl, RssItem[] items) {
        mItems = new ArrayList<RssItem>();
        mTitle = title;
        mLink = link;
        mFeedUrl = feedUrl;
        if (items != null) {
            mItems = new ArrayList<RssItem>(Arrays.asList(items));
        }
    }

    public void putItem(RssItem item) {
        mItems.add(item);
    }

    public void setLink(String link) {
        mLink = link;
    }
    public String getLink() {
        return mLink;
    }

    public void setName(String name) {
        mTitle = name;
    }
    public String getName() {
        return mTitle;
    }

    public void setmFeedUrl(String feedUrl) {
        mFeedUrl = feedUrl;
    }
    public String getmFeedUrl() {
        return mFeedUrl;
    }
}
