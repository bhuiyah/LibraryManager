import java.io.Serializable;

public class Entry implements Serializable {
  private String last_checked_out;
  private String title;
  private String genre;
  private String author;
  private String available;
  private String media_type;

  public Entry(String last_checked_out, String title, String genre, String author, String available, String media_type) {
    this.last_checked_out = last_checked_out;
    this.title = title;
    this.genre = genre;
    this.author = author;
    this.available = available;
    this.media_type = media_type;
  }

  public String getLast_checked_out() {
    return last_checked_out;
  }

  public void setLast_checked_out(String last_checked_out) {
    this.last_checked_out = last_checked_out;
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
}