package one.thebox.android.api.Responses.category;

import java.io.Serializable;

import one.thebox.android.Models.Category;
import one.thebox.android.api.ApiResponse;

/**
 * Created by developers on 29/05/17.
 */

public class BoxCategoryItemResponse extends ApiResponse implements Serializable {

    private Category category;

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
