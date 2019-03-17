package example.farhan.com.moviepocket.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "favorites")
public class Favorite {

    @PrimaryKey
    private int id;
    private String title;
    private String release_date;
    //@ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private String  image;
    @ColumnInfo(name = "vote_average")
    private String voteAverage;
    private String overview;
    @ColumnInfo(name = "is_favorite")
    private Boolean isFavorite;
    private String myRating;
    @ColumnInfo(name = "tag_line")
    private String tagLine;
    private String status;
    @ColumnInfo(name = "spoken_languages")
    private String spokenLanguages;
    private String runtime;
    private String budget;
    private String genres;
    private String revenue;
    @ColumnInfo(name = "production_companies")
    private String productionCompanies;
    private String comments;

    @Ignore
    public Favorite() {
    }

    public Favorite(int id, String title, String release_date, String image, String voteAverage, String overview, Boolean isFavorite, String myRating, String tagLine, String status, String spokenLanguages, String runtime, String budget, String genres, String revenue, String productionCompanies, String comments) {
        this.id = id;
        this.title = title;
        this.release_date = release_date;
        this.image = image;
        this.voteAverage = voteAverage;
        this.overview = overview;
        this.isFavorite = isFavorite;
        this.myRating = myRating;
        this.tagLine = tagLine;
        this.status = status;
        this.spokenLanguages = spokenLanguages;
        this.runtime = runtime;
        this.budget = budget;
        this.genres = genres;
        this.revenue = revenue;
        this.productionCompanies = productionCompanies;
        this.comments = comments;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public Boolean getFavorite() {
        return isFavorite;
    }

    public void setFavorite(Boolean favorite) {
        isFavorite = favorite;
    }

    public String getMyRating() {
        return myRating;
    }

    public void setMyRating(String myRating) {
        this.myRating = myRating;
    }

    public String getTagLine() {
        return tagLine;
    }

    public void setTagLine(String tagLine) {
        this.tagLine = tagLine;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSpokenLanguages() {
        return spokenLanguages;
    }

    public void setSpokenLanguages(String spokenLanguages) {
        this.spokenLanguages = spokenLanguages;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public String getRevenue() {
        return revenue;
    }

    public void setRevenue(String revenue) {
        this.revenue = revenue;
    }

    public String getProductionCompanies() {
        return productionCompanies;
    }

    public void setProductionCompanies(String productionCompanies) {
        this.productionCompanies = productionCompanies;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
