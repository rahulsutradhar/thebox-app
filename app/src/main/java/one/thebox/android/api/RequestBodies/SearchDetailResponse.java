package one.thebox.android.api.RequestBodies;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import one.thebox.android.Models.BoxItem;
import one.thebox.android.Models.Category;
import one.thebox.android.Models.UserItem;
import one.thebox.android.api.ApiResponse;

/**
 * Created by 32 on 24-04-2016.
 */
public class SearchDetailResponse extends ApiResponse implements Serializable {

    @SerializedName("search_items")
    private List<BoxItem> searchedItems;
    @SerializedName("my_search_items")
    private List<UserItem> mySearchItems;
    @SerializedName("my_non_searched_items")
    private List<UserItem> myNonSearchedItems;
    @SerializedName("searched_category")
    private Category searchedCategory;
    @SerializedName("rest_of_the_categories_in_box")
    private List<Category> restCategories;

    public List<BoxItem> getSearchedItems() {
        return searchedItems;
    }

    public void setSearchedItems(List<BoxItem> searchedItems) {
        this.searchedItems = searchedItems;
    }

    public List<UserItem> getMySearchItems() {
        return mySearchItems;
    }

    public void setMySearchItems(List<UserItem> mySearchItems) {
        this.mySearchItems = mySearchItems;
    }

    public List<UserItem> getMyNonSearchedItems() {
        return myNonSearchedItems;
    }

    public void setMyNonSearchedItems(List<UserItem> myNonSearchedItems) {
        this.myNonSearchedItems = myNonSearchedItems;
    }

    public Category getSearchedCategory() {
        return searchedCategory;
    }

    public void setSearchedCategory(Category searchedCategory) {
        this.searchedCategory = searchedCategory;
    }

    public List<Category> getRestCategories() {
        return restCategories;
    }

    public void setRestCategories(List<Category> restCategories) {
        this.restCategories = restCategories;
    }
}
