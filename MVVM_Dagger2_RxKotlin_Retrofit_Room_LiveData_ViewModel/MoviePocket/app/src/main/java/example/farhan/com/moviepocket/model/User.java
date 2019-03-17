package example.farhan.com.moviepocket.model;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String uId;
    private String name;
    private String email;
    private String profileImageUrl;
    private String address;
    private String phone;
    private String favoriteMovie;

    public User() {
    }

    public User(String uId, String name, String email, String profileImageUrl) {
        this.uId = uId;
        this.name = name;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
    }

    public User(String uId, String name, String email, String profileImageUrl, String address, String phone, String favoriteMovie) {
        this.uId = uId;
        this.name = name;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.address = address;
        this.phone = phone;
        this.favoriteMovie = favoriteMovie;
    }

    protected User(Parcel in) {
        uId = in.readString();
        name = in.readString();
        email = in.readString();
        profileImageUrl = in.readString();
        address = in.readString();
        phone = in.readString();
        favoriteMovie = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFavoriteMovie() {
        return favoriteMovie;
    }

    public void setFavoriteMovie(String favoriteMovie) {
        this.favoriteMovie = favoriteMovie;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uId);
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(profileImageUrl);
        dest.writeString(address);
        dest.writeString(phone);
        dest.writeString(favoriteMovie);
    }
}
