package one.thebox.android.Events;

import one.thebox.android.api.Responses.SearchAutoCompleteResponse;

/**
 * Created by Ajeet Kumar Meena on 21-04-2016.
 */
public class SearchEvent {
    private String query;
    private SearchAutoCompleteResponse searchAutoCompleteResponse;

    public SearchEvent(String query, SearchAutoCompleteResponse searchAutoCompleteResponse) {
        this.query = query;
        this.searchAutoCompleteResponse = searchAutoCompleteResponse;
    }

    public SearchAutoCompleteResponse getSearchAutoCompleteResponse() {
        return searchAutoCompleteResponse;
    }

    public void setSearchAutoCompleteResponse(SearchAutoCompleteResponse searchAutoCompleteResponse) {
        this.searchAutoCompleteResponse = searchAutoCompleteResponse;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
