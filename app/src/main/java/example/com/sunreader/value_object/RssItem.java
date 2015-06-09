package example.com.sunreader.value_object;


public class RssItem {

    private final String title;
    private final String link;
    private final String content;

    public RssItem(String title, String link, String description) {
        this.title = title;
        this.link = link;
        this.content = description;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getContent() {
        return content;
    }
}
