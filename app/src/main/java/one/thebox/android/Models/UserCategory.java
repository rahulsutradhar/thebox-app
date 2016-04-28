package one.thebox.android.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserCategory implements Serializable {
    @SerializedName("id")
    private int id;
    @SerializedName("useritems")
    private List<UserItem> userItems;
    @SerializedName("category")
    private Category category;

    public UserCategory(int id, ArrayList<UserItem> userItems, Category category) {
        this.id = id;
        this.userItems = userItems;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<UserItem> getUserItems() {
        return userItems;
    }

    public void setUserItems(ArrayList<UserItem> userItems) {
        this.userItems = userItems;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

}
