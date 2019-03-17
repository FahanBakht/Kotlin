package example.farhan.com.moviepocket.model;

public class Rating {

    private String Key;
    private float rating;
    private String name;

    public Rating() {
    }

    public Rating(String name, float rating) {
        this.name = name;
        this.rating = rating;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
