package example.farhan.com.moviepocket.model;

import java.util.List;

public class Videos {

    private List<Results> results;

    public Videos() {
    }

    public Videos(List<Results> results) {
        this.results = results;
    }

    public List<Results> getResults() {
        return results;
    }

    public void setResults(List<Results> results) {
        this.results = results;
    }

}
