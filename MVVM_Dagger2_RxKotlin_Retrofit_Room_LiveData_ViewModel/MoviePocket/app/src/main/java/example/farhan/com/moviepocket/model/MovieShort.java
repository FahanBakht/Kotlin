package example.farhan.com.moviepocket.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public class MovieShort implements Parcelable,Comparable<MovieShort> {
    private long id;
    private String poster_path;
    private String title;
    private float vote_average;
    private String release_date;

    public MovieShort() {
    }

    public MovieShort(long id, String poster_path, String title, float vote_average, String release_date) {
        this.id = id;
        this.poster_path = poster_path;
        this.title = title;
        this.vote_average = vote_average;
        this.release_date = release_date;
    }

    protected MovieShort(Parcel in) {
        id = in.readLong();
        poster_path = in.readString();
        title = in.readString();
        vote_average = in.readFloat();
        release_date = in.readString();
    }

    public static final Creator<MovieShort> CREATOR = new Creator<MovieShort>() {
        @Override
        public MovieShort createFromParcel(Parcel in) {
            return new MovieShort(in);
        }

        @Override
        public MovieShort[] newArray(int size) {
            return new MovieShort[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getVote_average() {
        return vote_average;
    }

    public void setVote_average(float vote_average) {
        this.vote_average = vote_average;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    @Override
    public int compareTo(@NonNull MovieShort o) {
        if (vote_average < o.vote_average) {
            return 1;
        }
        else if (vote_average >  o.vote_average) {
            return -1;
        }
        else {
            return 0;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(poster_path);
        dest.writeString(title);
        dest.writeFloat(vote_average);
        dest.writeString(release_date);
    }
}
