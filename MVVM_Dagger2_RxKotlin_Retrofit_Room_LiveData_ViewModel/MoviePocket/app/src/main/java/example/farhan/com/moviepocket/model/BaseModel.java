package example.farhan.com.moviepocket.model;

import java.util.ArrayList;

public class BaseModel<T> {
    private int page;
    private int total_pages;
    private long total_results;
    private ArrayList<T> results;

    public ArrayList<T> getResults() {
        return results;
    }

    public long getTotal_results() {
        return total_results;
    }

    public int getTotal_pages() {
        return total_pages;
    }

    public int getPage() {
        return page;
    }
}