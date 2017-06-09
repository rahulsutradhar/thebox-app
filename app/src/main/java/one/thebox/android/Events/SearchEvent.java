package one.thebox.android.Events;

import java.util.ArrayList;

import one.thebox.android.Models.search.SearchResult;
import one.thebox.android.api.Responses.SearchAutoCompleteResponse;

/**
 * Created by Ajeet Kumar Meena on 21-04-2016.
 */
public class SearchEvent {
    /**
     * Search Query
     */
    private String query;

    /**
     * Search Result
     */
    private ArrayList<SearchResult> searchResults;

    public SearchEvent(String query, ArrayList<SearchResult> searchResults) {
        this.query = query;
        this.searchResults = searchResults;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public ArrayList<SearchResult> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(ArrayList<SearchResult> searchResults) {
        this.searchResults = searchResults;
    }
}
