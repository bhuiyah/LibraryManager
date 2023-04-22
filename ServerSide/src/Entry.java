import java.io.Serializable;

public class Entry implements Serializable {
    private String title;
    private String genre;
    private String author;
    private String available;
    private String media_type;
    private int count;
    private String url;
    private String description;

    public Entry(String title, String genre, String author, String available, String media_type, int count, String url, String description) {
        this.title = title;
        this.genre = genre;
        this.author = author;
        this.available = available;
        this.media_type = media_type;
        this.count = count;
        this.url = url;
        this.description = description;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }

    public String getMedia_type() {
        return media_type;
    }

    public void setMedia_type(String media_type) {
        this.media_type = media_type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}