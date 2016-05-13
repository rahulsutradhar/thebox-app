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
    @SerializedName("useritems")
    private RealmList<UserItem> userItems;
    @SerializedName("category")
    private Category category;

    public UserCategory() {
    }

    public UserCategory(int id, RealmList<UserItem> userItems, Category category) {
        this.id = id;
        this.userItems = userItems;
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (o.getClass() != getClass()) {
            return false;
        }
        UserCategory userCategory = (UserCategory) o;
        if (userCategory.getId() != this.id) {
            return false;
        }
        if (!userCategory.getCategory().equals(category)) {
            return false;
        }
        return !((userCategory.getUserItems().size() != this.getUserItems().size()) &&
                (!userCategory.getUserItems().equals(getUserItems())));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
