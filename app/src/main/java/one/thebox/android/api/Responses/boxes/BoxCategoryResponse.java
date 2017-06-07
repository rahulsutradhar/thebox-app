package one.thebox.android.api.Responses.boxes;

import java.io.Serializable;
import java.util.ArrayList;

import one.thebox.android.Models.Category;
import one.thebox.android.api.ApiResponse;

/**
 * Created by developers on 07/06/17.
 */

public class BoxCategoryResponse extends ApiResponse implements Serializable {

    private ArrayList<Category> categories;

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<Category> categories) {
        this.categories = categories;
    }
}
