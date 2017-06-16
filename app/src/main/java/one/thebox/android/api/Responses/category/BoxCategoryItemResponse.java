package one.thebox.android.api.Responses.category;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import one.thebox.android.Models.Meta;
import one.thebox.android.Models.items.Category;
import one.thebox.android.api.ApiResponse;

/**
 * Created by developers on 29/05/17.
 */

public class BoxCategoryItemResponse extends ApiResponse implements Serializable {

    private Category category;

    @SerializedName("meta_info")
    private Meta meta;

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }
}
