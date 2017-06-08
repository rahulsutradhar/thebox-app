package one.thebox.android.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import one.thebox.android.Models.items.BoxItem;

/**
 * Created by Ajeet Kumar Meena on 21-04-2016.
 */
public class Category extends RealmObject implements Serializable {
    @Ignore
    public static final String FIELD_ID = "id";
    @Ignore
    public static final String FIELD_TITLE = "title";
    @Ignore
    public static final String FIELD_BOX_ID = "box_id";
    @Ignore
    public static final String FIELD_NO_OF_ITEMS = "no_of_items";
    @Ignore
    public static final String FIELD_ICON_URL = "icon_url";

    @PrimaryKey
    @SerializedName("id")
    private int id;

    @SerializedName("box_id")
    private int boxId;

    private int noOfItems;

    private String iconUrl;


    public Category() {
    }

    public Category(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public Category(int id, String title, String minititle, int boxId, int noOfItems) {
        this.id = id;
        this.title = title;
        this.minititle = minititle;
        this.boxId = boxId;
        this.noOfItems = noOfItems;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public int getBoxId() {
        return boxId;
    }

    public void setBoxId(int boxId) {
        this.boxId = boxId;
    }

    public int getNoOfItems() {
        return noOfItems;
    }

    public void setNoOfItems(int noOfItems) {
        this.noOfItems = noOfItems;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }



    /**
     * Refactor
     */
    private String uuid;

    private String title;

    private String minititle;

    @SerializedName("icon_url")
    private String categoryImage;

    @SerializedName("no_of_items")
    private int numberOfItem;

    private int priority;

    @SerializedName("avg_savings")
    private String averageSavings;

    @SerializedName("items")
    private RealmList<BoxItem> boxItems;


    /************************************************
     * Getter Setter
     ************************************************/

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }

    public int getNumberOfItem() {
        return numberOfItem;
    }

    public void setNumberOfItem(int numberOfItem) {
        this.numberOfItem = numberOfItem;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getAverageSavings() {
        return averageSavings;
    }

    public void setAverageSavings(String averageSavings) {
        this.averageSavings = averageSavings;
    }

    public RealmList<BoxItem> getBoxItems() {
        return boxItems;
    }

    public void setBoxItems(RealmList<BoxItem> boxItems) {
        this.boxItems = boxItems;
    }
}
