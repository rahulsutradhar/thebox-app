package one.thebox.android.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class Box extends RealmObject implements Serializable {
    @Ignore
    public static final String FIELD_ID = "id";
    @Ignore
    public static final String FIELD_USER_ID = "userId";
    @Ignore
    public static final String FIELD_REMAINING_CATEGORIES = "remainingCategories";
    @Ignore
    public static final String FIELD_USER_CATEGORIES = "userCategories";
    @Ignore
    public static final String FIELD_BOX = "box";

    @PrimaryKey
    @SerializedName("id")
    private int id;
    @SerializedName("user_id")
    private int userId;
    @SerializedName("box_id")
    private int boxId;
    @SerializedName("remaining_categories")
    private RealmList<Category> remainingCategories;
    @SerializedName("usercategories_with_at_least_one_item")
    private RealmList<UserCategory> userCategories;
    @SerializedName("box")
    private BoxDetail box;
    @Ignore
    private List<UserItem> allItemsInTheBox;


    public Box() {
    }

    public Box(int id, int userId, int boxId, RealmList<Category> remainingCategories, RealmList<UserCategory> userCategories, BoxDetail box) {
        this.id = id;
        this.userId = userId;
        this.boxId = boxId;
        this.remainingCategories = remainingCategories;
        this.userCategories = userCategories;
        this.box = box;
    }

    @Override
    public boolean equals(Object o) {
        Box box = (Box) o;
        if (box.getId() != this.getId()) {
            return false;
        }
        if (box.getUserId() != this.getUserId()) {
            return false;
        }
        if (!box.getRemainingCategories().equals(this.getRemainingCategories())) {
            return false;
        }

        if (!(box.getUserCategories().equals(this.getUserCategories()))) {
            return false;
        }

        return box.getBoxDetail().equals(this.getBoxDetail());
    }

    public String getSubTitle() {
        return "You have " + getAllItemInTheBox().size() + " items in the box";
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

    public RealmList<Category> getRemainingCategories() {
        return remainingCategories;
    }

    public void setRemainingCategories(RealmList<Category> remainingCategories) {
        this.remainingCategories = remainingCategories;
    }

    public RealmList<UserCategory> getUserCategories() {
        return userCategories;
    }

    public void setUserCategories(RealmList<UserCategory> userCategories) {
        this.userCategories = userCategories;
    }

    public BoxDetail getBoxDetail() {
        return box;
    }

    public List<UserItem> getAllItemInTheBox() {
        if (allItemsInTheBox == null) {
            allItemsInTheBox = new RealmList<>();
            for (UserCategory userCategory : userCategories) {
                allItemsInTheBox.addAll(userCategory.getUserItems());
            }
            return allItemsInTheBox;
        }
        return allItemsInTheBox;
    }

    public void setAllItemsInTheBox(List<UserItem> allItemsInTheBox) {
        this.allItemsInTheBox = allItemsInTheBox;
    }

    public BoxDetail getBox() {
        return box;
    }

    public void setBox(BoxDetail box) {
        this.box = box;
    }
}
