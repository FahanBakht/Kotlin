package example.farhan.com.moviepocket.interfaces;

// Helper Interface to Get AsyncTask Response back to Main Activity to handel
public interface AsyncTaskResponse<T> {

    void processStart();

    void processFinish(T t);

}
