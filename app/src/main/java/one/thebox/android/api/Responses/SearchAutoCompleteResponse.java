package one.thebox.android.api.Responses;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import one.thebox.android.Models.Category;
import one.thebox.android.api.ApiResponse;

/**
 * Created by Ajeet Kumar Meena on 21-04-2016.
 */
public class SearchAutoCompleteResponse extends ApiResponse implements Serializable {
    private List<String> items;
    private List<Category> categories;

    public SearchAutoCompleteResponse(List<String> items, List<Category> categories) {
        this.items = items;
        this.categories = categories;
    }

    public List<String> getItems() {
        if (this.items == null || this.items.size() == 0) {
            items = new ArrayList<>();
            for (Category cat : categories) {
                items.addAll(cat.getItems());
            }
        }
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}
