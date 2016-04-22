package one.thebox.android.api.Responses;

import java.io.Serializable;
import java.util.List;

import one.thebox.android.Models.Categories;
import one.thebox.android.Models.Items;
import one.thebox.android.api.ApiResponse;

/**
 * Created by Ajeet Kumar Meena on 21-04-2016.
 */
public class SearchAutoCompleteResponse extends ApiResponse implements Serializable {
    private List<String> items;
    private List<String> categories;

    public SearchAutoCompleteResponse(List<String> items, List<String> categories) {
        this.items = items;
        this.categories = categories;
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }
}
