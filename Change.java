import java.io.Serializable;

public class Change implements Serializable {
    private String method;
    private Place place;

    public Change(String method, Place place) {
        this.method = method;
        this.place = place;
    }

    public String getMethod() {
        return method;
    }


    public Place getPlace() {
        return place;
    }

}
