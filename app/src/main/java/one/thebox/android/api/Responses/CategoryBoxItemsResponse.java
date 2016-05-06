package one.thebox.android.api.Responses;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import one.thebox.android.Models.BoxItem;
import one.thebox.android.Models.Category;
import one.thebox.android.Models.UserItem;
import one.thebox.android.api.ApiResponse;

/**
 * Created by Ajeet Kumar Meena on 25-04-2016.
 */
public class CategoryBoxItemsResponse extends ApiResponse implements Serializable {
    @SerializedName("my_items")
    private ArrayList<UserItem> myBoxItems;
    @SerializedName("normal_items")
    private ArrayList<BoxItem> normalBoxItems;
    @SerializedName("selected_category")
    private Category category;
    @SerializedName("rest_of_the_categories_in_box")
    private ArrayList<Category> categories;

    public ArrayList<UserItem> getMyBoxItems() {
        return myBoxItems;
    }

    public void setMyBoxItems(ArrayList<UserItem> myBoxItems) {
        this.myBoxItems = myBoxItems;
    }

    public ArrayList<BoxItem> getNormalBoxItems() {
        return normalBoxItems;
    }

    public void setNormalBoxItems(ArrayList<BoxItem> normalBoxItems) {
        this.normalBoxItems = normalBoxItems;
    }

    public Category getCategory() {
        return category;
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public Category getSelectedCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public ArrayList<Category> getRestCategories() {
        return categories;
    }

    public void setCategories(ArrayList<Category> categories) {
        this.categories = categories;
    }
}
