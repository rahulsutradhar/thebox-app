package one.thebox.android.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

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

    @SerializedName("title")
    private String title;

    @SerializedName("minititle")
    private String minititle;

    @SerializedName("box_id")
    private int boxId;

    @SerializedName("no_of_items")
    private int noOfItems;

    @SerializedName("icon_url")
    private String iconUrl;

    @Ignore
    @SerializedName("items")
    private List<String> items;

    @SerializedName("avg_savings")
    private String averageSavings;

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMinititle() {
        return minititle;
    }

    public void setMinititle(String minititle) {
        this.minititle = minititle;
    }

 /*   @Override
    public String getAverageSavings() {
        return averageSavings;
    }

    public void setAverageSavings(String averageSavings) {
        this.averageSavings = averageSavings;
    }

    @Override
    public boolean equals(Object o) {
        Category category = (Category) o;
        return this.id == category.getId() && this.noOfItems == category.getNoOfItems()
                && this.title.equals(category.getTitle()) && this.iconUrl.equals(category.getIconUrl());
    }*/
}
