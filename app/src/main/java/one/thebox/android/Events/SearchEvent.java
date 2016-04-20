package one.thebox.android.Events;

/**
 * Created by Ajeet Kumar Meena on 21-04-2016.
 */
public class SearchEvent {
    private String query;

    public SearchEvent(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
