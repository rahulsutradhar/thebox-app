package one.thebox.android.Models.items;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import one.thebox.android.Models.BoxDetail;
import one.thebox.android.Models.UserCategory;

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
    private List<UserItem> allItemsInTheBox = new RealmList<>();


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

    /* @Override
     public boolean equals(Object o) {
         Box box = (Box) o;
         if (box.getId() != this.getId()) {
             return false;
         }
         if (box.getId() != this.getId()) {
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
 */
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
        if (allItemsInTheBox.isEmpty()) {
            for (UserCategory userCategory : userCategories) {
                allItemsInTheBox.addAll(userCategory.getUserItems());
            }
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


    /**
     * Refactor
     */

    private String uuid;

    private String title;

    @SerializedName("photo_url")
    private String boxImage;

    private int priority;

    @SerializedName("saving_title")
    private String savingTitle;

    private RealmList<Category> categories;


    /************************************************
     * Getter Setter
     ************************************************/

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBoxImage() {
        return boxImage;
    }

    public void setBoxImage(String boxImage) {
        this.boxImage = boxImage;
    }

    public RealmList<Category> getCategories() {
        return categories;
    }

    public void setCategories(RealmList<Category> categories) {
        this.categories = categories;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getSavingTitle() {
        return savingTitle;
    }

    public void setSavingTitle(String savingTitle) {
        this.savingTitle = savingTitle;
    }
}
