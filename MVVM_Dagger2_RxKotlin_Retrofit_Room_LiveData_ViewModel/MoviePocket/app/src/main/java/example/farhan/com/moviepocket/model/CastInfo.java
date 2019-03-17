package example.farhan.com.moviepocket.model;

public class CastInfo {

    private String name;
    private String biography;
    private String place_of_birth;
    private String birthday;

    public CastInfo() {
    }

    public CastInfo(String name, String biography, String place_of_birth, String birthday) {
        this.name = name;
        this.biography = biography;
        this.place_of_birth = place_of_birth;
        this.birthday = birthday;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getPlace_of_birth() {
        return place_of_birth;
    }

    public void setPlace_of_birth(String place_of_birth) {
        this.place_of_birth = place_of_birth;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
}
