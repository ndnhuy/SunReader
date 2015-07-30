package example.com.sunreader.value_object;

public class RssFeed {
    String mTitle;
    String mLink;
    String mFeedUrl;
    String mThumbnailUrl;

    public RssFeed() {}

    public RssFeed(String title, String link, String feedUrl, String thumbnailUrl) {
        mTitle = title;
        mLink = link;
        mFeedUrl = feedUrl;
        mThumbnailUrl = thumbnailUrl;
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

    public void setThumbnailUrl(String thumbnailUrl) {
        mThumbnailUrl = thumbnailUrl;
    }
    public String getThumbnailUrl() {
        return mThumbnailUrl;
    }
}
