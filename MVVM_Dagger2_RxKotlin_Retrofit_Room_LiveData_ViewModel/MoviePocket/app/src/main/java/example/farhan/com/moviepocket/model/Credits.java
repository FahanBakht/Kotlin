package example.farhan.com.moviepocket.model;

import java.util.List;

public class Credits {

    public Credits() {
    }

    private List<Cast> cast;

    public Credits(List<Cast> cast) {
        this.cast = cast;
    }

    public List<Cast> getCast() {
        return cast;
    }

    public void setCast(List<Cast> cast) {
        this.cast = cast;
    }
}
