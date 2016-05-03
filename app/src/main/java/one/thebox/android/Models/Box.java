package one.thebox.android.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Box implements Serializable {
    @SerializedName("id")
    private int id;
    @SerializedName("user_id")
    private int userId;
    @SerializedName("box_id")
    private int boxId;
    @SerializedName("remaining_categories")
    private List<Category> remainingCategories;
    @SerializedName("usercategories")
    private List<UserCategory> userCategories;
    @SerializedName("box")
    private BoxDetail box;
    private boolean isExpandedListVisible;


    public Box(int id, int userId, int boxId, List<Category> remainingCategories, List<UserCategory> userCategories, BoxDetail box) {
        this.id = id;
        this.userId = userId;
        this.boxId = boxId;
        this.remainingCategories = remainingCategories;
        this.userCategories = userCategories;
        this.box = box;
    }

    public String getSubTitle() {
        String itemString = "";
        for (int i = 0; i < getAllItemInTheBox().size(); i++) {
            if (i == 3) {
                itemString = itemString.substring(0, itemString.length() - 2);
                itemString = itemString + "...+ " + (getAllItemInTheBox().size()-4) + " items";
                return itemString;
            }
            itemString = getAllItemInTheBox().get(i).getBoxItem().getTitle() + ", " + itemString;
        }
        itemString = itemString.substring(0, itemString.length() - 2);
        return itemString;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getBoxId() {
        return boxId;
    }

    public void setBoxId(int boxId) {
        this.boxId = boxId;
    }

    public List<Category> getRemainingCategories() {
        return remainingCategories;
    }

    public void setRemainingCategories(List<Category> remainingCategories) {
        this.remainingCategories = remainingCategories;
    }

    public List<UserCategory> getUserCategories() {
        return userCategories;
    }

    public void setUserCategories(List<UserCategory> userCategories) {
        this.userCategories = userCategories;
    }

    public BoxDetail getBoxDetail() {
        return box;
    }

    public void setBox(BoxDetail box) {
        this.box = box;
    }

    public boolean isExpandedListVisible() {
        return isExpandedListVisible;
    }

    public void setExpandedListVisible(boolean expandedListVisible) {
        isExpandedListVisible = expandedListVisible;
    }

    public ArrayList<UserItem> getAllItemInTheBox() {
        ArrayList<UserItem> userItems = new ArrayList<>();
        for (UserCategory userCategory : userCategories) {
            userItems.addAll(userCategory.getUserItems());
        }
        return userItems;
    }
}
