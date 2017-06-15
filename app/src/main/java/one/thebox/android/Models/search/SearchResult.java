package one.thebox.android.Models.search;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Ajeet Kumar Meena on 13-04-2016.
 * <p>
 * Modified by Developers on 9/06/2017.
 */
public class SearchResult implements Serializable {

    @SerializedName("category_uuid")
    private String categoryUuid;

    @SerializedName("box_uuid")
    private String boxUuid;

    @SerializedName("box_title")
    private String boxTitle;

    private String title;

    @SerializedName("category")
    private boolean isCategory;

    public String getCategoryUuid() {
        return categoryUuid;
    }

    public void setCategoryUuid(String categoryUuid) {
        this.categoryUuid = categoryUuid;
    }

    public String getBoxUuid() {
        return boxUuid;
    }

    public void setBoxUuid(String boxUuid) {
        this.boxUuid = boxUuid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBoxTitle() {
        return boxTitle;
    }

    public void setBoxTitle(String boxTitle) {
        this.boxTitle = boxTitle;
    }

    public boolean isCategory() {
        return isCategory;
    }

    public void setCategory(boolean category) {
        isCategory = category;
    }
}
