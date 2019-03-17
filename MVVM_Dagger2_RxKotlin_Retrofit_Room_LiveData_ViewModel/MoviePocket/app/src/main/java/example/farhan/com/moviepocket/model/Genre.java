package example.farhan.com.moviepocket.model;

public class Genre {

    private String name;

    public Genre() {
    }

    public Genre(String genres) {
        this.name = genres;
    }

    public String getGenres() {
        return name;
    }

    public void setGenres(String genres) {
        this.name = genres;
    }
}


