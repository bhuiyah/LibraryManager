import java.io.Serializable;

public class Entry implements Serializable {
  private String last_checked_out;
  private String title;
  private String genre;
  private String author;
  private boolean checked_out;

  public Entry(String last_checked_out, String title, String genre, String author, boolean checked_out) {
    this.last_checked_out = last_checked_out;
    this.title = title;
    this.genre = genre;
    this.author = author;
    this.checked_out = checked_out;
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

  public boolean isChecked_out() {
    return checked_out;
  }

  public void setChecked_out(boolean checked_out) {
    this.checked_out = checked_out;
  }

  @Override
  public String toString() {
    return "Entry{" +
            "last_checked_out='" + last_checked_out + '\'' +
            ", title='" + title + '\'' +
            ", genre='" + genre + '\'' +
            ", author='" + author + '\'' +
            ", checked_out=" + checked_out +
            '}';
  }
}