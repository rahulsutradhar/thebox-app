package one.thebox.android.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class UserCategory extends RealmObject implements Serializable {

    @Ignore
    public static final String FIELD_ID = "id";

    @PrimaryKey
    @SerializedName("id")
    private int id;

    @SerializedName("no_of_items")
    private int no_of_items;

    @SerializedName("noncart_susbcribed_useritems")
    private RealmList<UserItem> userItems;

    @SerializedName("category")
    private Category category;

    public UserCategory() {
        this.id = 0;
        this.userItems = null;
        this.category = null;
        this.no_of_items = 0;
    }

    public UserCategory(int id, int no_of_items, RealmList<UserItem> userItems, Category category) {
        this.id = id;
        this.userItems = userItems;
        this.category = category;
        this.no_of_items = no_of_items;
    }

    @Override
    public boolean equals(Object o) {
        UserCategory userCategory = (UserCategory) o;
        if (userCategory.getId() != this.id) {
            return false;
        }
        if (!userCategory.getCategory().equals(category)) {
            return false;
        }
        return userCategory.getUserItems().equals(getUserItems());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNo_of_items() {
        return no_of_items;
    }

    public void setNo_of_items(int no_of_items) {
        this.no_of_items = no_of_items;
    }

    public RealmList<UserItem> getUserItems() {
        return userItems;
    }

    public void setUserItems(RealmList<UserItem> userItems) {
        this.userItems = userItems;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
