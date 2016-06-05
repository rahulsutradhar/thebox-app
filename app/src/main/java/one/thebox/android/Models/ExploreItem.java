package one.thebox.android.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Ajeet Kumar Meena on 13-04-2016.
 */
public class ExploreItem implements Serializable {
    @SerializedName("id")
    private int id;
    @SerializedName("title")
    private String title;
    @SerializedName("category_string")
    private String subTitle;
    @SerializedName("total_items")
    private int totalItems;
    @SerializedName("photo_url")
    private String imageUrl;
    @SerializedName("box_type")
    private int boxType;

    public ExploreItem(int id, String title, String subTitle, int totalItems, String imageUrl, int boxType) {
        this.id = id;
        this.title = title;
        this.subTitle = subTitle;
        this.totalItems = totalItems;
        this.imageUrl = imageUrl;
        this.boxType = boxType;
    }

    public ExploreItem(int id, String title) {
        this.id = id;
        this.title = title;
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

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getBoxType() {
        return boxType;
    }

    public void setBoxType(int boxType) {
        this.boxType = boxType;
    }
}
