package one.thebox.android.api.RequestBodies;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import io.realm.RealmList;
import one.thebox.android.Models.items.BoxItem;
import one.thebox.android.Models.items.Category;
import one.thebox.android.Models.items.UserItem;
import one.thebox.android.api.ApiResponse;

/**
 * Created by 32 on 24-04-2016.
 */
public class SearchDetailResponse extends ApiResponse implements Serializable {

    @SerializedName("search_items")
    private BoxItem searchedItem;

    @SerializedName("my_search_items")
    private UserItem mySearchItem;

    @SerializedName("searched_category")
    private Category searchedCategory;

    @SerializedName("rest_of_the_categories_in_box")
    private List<Category> restCategories;

    @SerializedName("box_name")
    private String boxName;

    @SerializedName("normal_items")
    private RealmList<BoxItem> normalItems;

    @SerializedName("my_non_searched_items")
    private RealmList<UserItem> myNonSearchedItems;

    public RealmList<BoxItem> getNormalItems() {
        return normalItems;
    }

    public String getBoxName() {
        return boxName;
    }

    public RealmList<UserItem> getMyNonSearchedItems() {
        return myNonSearchedItems;
    }

    public void setBoxName(String boxName) {
        this.boxName = boxName;
    }

    public BoxItem getSearchedItem() {
        return searchedItem;
    }

    public void setSearchedItem(BoxItem searchedItem) {
        this.searchedItem = searchedItem;
    }

    public UserItem getMySearchItem() {
        return mySearchItem;
    }

    public void setMySearchItem(UserItem mySearchItem) {
        this.mySearchItem = mySearchItem;
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
