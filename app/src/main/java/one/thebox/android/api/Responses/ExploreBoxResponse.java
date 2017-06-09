package one.thebox.android.api.Responses;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import one.thebox.android.Models.items.BoxItem;
import one.thebox.android.Models.items.Category;
import one.thebox.android.Models.items.UserItem;
import one.thebox.android.api.ApiResponse;

/**
 * Created by Ajeet Kumar Meena on 26-04-2016.
 */
public class ExploreBoxResponse extends ApiResponse implements Serializable {
    @SerializedName("normal_items")
    private ArrayList<BoxItem> normalItems;
    @SerializedName("my_items")
    private ArrayList<UserItem> myItems;
    @SerializedName("selected_category")
    private Category selectedCategory;
    @SerializedName("rest_of_the_categories_in_box")
    private ArrayList<Category> restCategories;

    public ArrayList<BoxItem> getNormalItems() {
        return normalItems;
    }

    public void setNormalItems(ArrayList<BoxItem> normalItems) {
        this.normalItems = normalItems;
    }

    public ArrayList<UserItem> getMyItems() {
        return myItems;
    }

    public void setMyItems(ArrayList<UserItem> myItems) {
        this.myItems = myItems;
    }

    public Category getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(Category selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    public ArrayList<Category> getRestCategories() {
        return restCategories;
    }

    public void setRestCategories(ArrayList<Category> restCategories) {
        this.restCategories = restCategories;
    }
}
