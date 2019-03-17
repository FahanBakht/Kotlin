package example.farhan.com.moviepocket.model;

public class Cast {

    private long id;
    private String profile_path;
    private String character;

    public Cast() {
    }

    public Cast(long id, String profile_path, String character) {
        this.id = id;
        this.profile_path = profile_path;
        this.character = character;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProfile_path() {
        return profile_path;
    }

    public void setProfile_path(String profile_path) {
        this.profile_path = profile_path;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }
}
