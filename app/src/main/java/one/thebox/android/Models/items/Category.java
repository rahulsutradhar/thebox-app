package one.thebox.android.Models.items;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Ajeet Kumar Meena on 21-04-2016.
 */
public class Category extends RealmObject implements Serializable {

    @PrimaryKey
    private String uuid;

    private String title;

    private String minititle;

    @SerializedName("icon_url")
    private String categoryImage;

    @SerializedName("no_of_items")
    private int numberOfItem;

    private int priority;

    @SerializedName("savings_text")
    private String savingsText;

    @SerializedName("items")
    private RealmList<BoxItem> boxItems;

    @SerializedName("subscribed_text")
    private String subscribedText;


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

    public String getSavingsText() {
        return savingsText;
    }

    public void setSavingsText(String savingsText) {
        this.savingsText = savingsText;
    }

    public RealmList<BoxItem> getBoxItems() {
        return boxItems;
    }

    public void setBoxItems(RealmList<BoxItem> boxItems) {
        this.boxItems = boxItems;
    }

    public String getMinititle() {
        return minititle;
    }

    public void setMinititle(String minititle) {
        this.minititle = minititle;
    }

    public String getSubscribedText() {
        return subscribedText;
    }

    public void setSubscribedText(String subscribedText) {
        this.subscribedText = subscribedText;
    }
}
