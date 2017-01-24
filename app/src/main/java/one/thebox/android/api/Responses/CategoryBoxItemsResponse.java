package one.thebox.android.api.Responses;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

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
    @SerializedName("data")
    private ItemsData itemsData;


    @SerializedName("selected_category")
    private Category category;
    @SerializedName("rest_of_the_categories_in_box")
    private ArrayList<Category> categories;
    @SerializedName("box_name")
    private String boxName;
    @SerializedName("total_count")
    private int totalCount;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public String getBoxName() {
        return boxName;
    }

    public ArrayList<UserItem> getMyBoxItems() {
        if (myBoxItems == null) {
            if (itemsData != null) {
                return itemsData.getMyBoxItems();
            }
        }
        return myBoxItems;
    }

    public void setMyBoxItems(ArrayList<UserItem> myBoxItems) {
        this.myBoxItems = myBoxItems;
    }

    public ArrayList<BoxItem> getNormalBoxItems() {
        if (normalBoxItems == null) {
            if (itemsData != null) {
                return itemsData.getNormalBoxItems();
            }
        }
        return normalBoxItems;
    }

    public void setNormalBoxItems(ArrayList<BoxItem> normalBoxItems) {
        this.normalBoxItems = normalBoxItems;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<Category> categories) {
        this.categories = categories;
    }

    public Category getSelectedCategory() {
        return category;
    }

    public ArrayList<Category> getRestCategories() {
        return categories;
    }

    private class ItemsData {
        @SerializedName("my_items")
        private ArrayList<UserItem> myBoxItems;
        @SerializedName("items")
        private ArrayList<BoxItem> normalBoxItems;

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
    }
}
