package example.com.sunreader.value_object;

public class RssFeed {
    String mTitle;
    String mLink;
    String mFeedUrl;
//    List<RssItem> mItems = null;
//
//    public RssFeed() {
//        mItems = new ArrayList<RssItem>();
//    }
//    public RssFeed(String title, String link, String feedUrl, RssItem[] items) {
//        mItems = new ArrayList<RssItem>();
//        mTitle = title;
//        mLink = link;
//        mFeedUrl = feedUrl;
//        if (items != null) {
//            mItems = new ArrayList<RssItem>(Arrays.asList(items));
//        }
//    }

//    public void putItem(RssItem item) {
//        mItems.add(item);
//    }

    public RssFeed() {}

    public RssFeed(String title, String link, String feedUrl) {
        mTitle = title;
        mLink = link;
        mFeedUrl = feedUrl;
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

    public void setFeedUrl(String feedUrl) {
        mFeedUrl = feedUrl;
    }
    public String getFeedUrl() {
        return mFeedUrl;
    }
}
