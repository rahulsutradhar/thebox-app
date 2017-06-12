package one.thebox.android.Models.items;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class Box extends RealmObject implements Serializable {

    @PrimaryKey
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
