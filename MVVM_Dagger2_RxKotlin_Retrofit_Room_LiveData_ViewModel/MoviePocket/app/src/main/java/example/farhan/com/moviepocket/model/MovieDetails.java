package example.farhan.com.moviepocket.model;

import java.util.List;

public class MovieDetails {

    private String title;
    private String release_date;
    private String tagline;
    private String poster_path;
    private Double vote_average;
    private String overview;
    private String status;
    private int revenue;
    private int budget;
    private int runtime;
    private int id;
    private List<SpokenLanguage> spoken_languages;
    private List<ProductionCompanies> production_companies;
    private List<Genre> genres;
    private Videos videos;
    private Credits credits;

    public MovieDetails() {
    }

    public MovieDetails(String title, String release_date, String tagline, String poster_path, Double vote_average, String overview, String status, int revenue, int budget, int runtime, int id, List<SpokenLanguage> spoken_languages, List<ProductionCompanies> production_companies, List<Genre> genres, Videos videos, Credits credits) {
        this.title = title;
        this.release_date = release_date;
        this.tagline = tagline;
        this.poster_path = poster_path;
        this.vote_average = vote_average;
        this.overview = overview;
        this.status = status;
        this.revenue = revenue;
        this.budget = budget;
        this.runtime = runtime;
        this.id = id;
        this.spoken_languages = spoken_languages;
        this.production_companies = production_companies;
        this.genres = genres;
        this.videos = videos;
        this.credits = credits;
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

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public Double getVote_average() {
        return vote_average;
    }

    public void setVote_average(Double vote_average) {
        this.vote_average = vote_average;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getRevenue() {
        return revenue;
    }

    public void setRevenue(int revenue) {
        this.revenue = revenue;
    }

    public int getBudget() {
        return budget;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<SpokenLanguage> getSpoken_languages() {
        return spoken_languages;
    }

    public void setSpoken_languages(List<SpokenLanguage> spoken_languages) {
        this.spoken_languages = spoken_languages;
    }

    public List<ProductionCompanies> getProduction_companies() {
        return production_companies;
    }

    public void setProduction_companies(List<ProductionCompanies> production_companies) {
        this.production_companies = production_companies;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public Videos getVideos() {
        return videos;
    }

    public void setVideos(Videos videos) {
        this.videos = videos;
    }

    public Credits getCredits() {
        return credits;
    }

    public void setCredits(Credits credits) {
        this.credits = credits;
    }
}
