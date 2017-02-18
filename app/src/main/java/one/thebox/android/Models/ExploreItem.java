package one.thebox.android.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import one.thebox.android.app.TheBox;
import one.thebox.android.util.PrefUtils;

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
    private static String PREF_DEFAULT_POSITION_OF_VIEW_PAGER = "pref_default_position_of_view_pager";

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

    public int getDefaultPositionOfViewPager() {
        return PrefUtils.getInt(TheBox.getInstance(), PREF_DEFAULT_POSITION_OF_VIEW_PAGER + getTitle(), 0);
    }

    public void setDefaultPositionOfViewPager(int viewPagerPosition) {
        PrefUtils.putInt(TheBox.getInstance(), PREF_DEFAULT_POSITION_OF_VIEW_PAGER + getTitle(), viewPagerPosition);
    }

    public static int getDefaultPositionOfViewPager(String boxName) {
        return PrefUtils.getInt(TheBox.getInstance(), PREF_DEFAULT_POSITION_OF_VIEW_PAGER + boxName, 0);
    }

    public static void setDefaultPositionOfViewPager(String boxName, int viewPagerPosition) {
        PrefUtils.putInt(TheBox.getInstance(), PREF_DEFAULT_POSITION_OF_VIEW_PAGER + boxName, viewPagerPosition);
    }
}
