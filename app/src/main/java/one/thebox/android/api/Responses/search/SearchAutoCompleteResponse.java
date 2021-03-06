package one.thebox.android.api.Responses.search;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import one.thebox.android.Models.search.SearchResult;
import one.thebox.android.api.ApiResponse;

/**
 * Created by Ajeet Kumar Meena on 21-04-2016.
 */
public class SearchAutoCompleteResponse extends ApiResponse implements Serializable {

    @SerializedName("category")
    private ArrayList<SearchResult> searchResults;

    public ArrayList<SearchResult> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(ArrayList<SearchResult> searchResults) {
        this.searchResults = searchResults;
    }
}
