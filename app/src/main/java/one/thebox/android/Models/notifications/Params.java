package one.thebox.android.Models.notifications;

import com.google.gson.annotations.SerializedName;

/**
 * Created by developers on 15/02/17.
 */

public class Params {

    /**
     * Category id for Box
     */
    @SerializedName("cat_id")
    private int categoryId;

    /**
     * Box name
     */
    @SerializedName("box_name")
    private String boxName;

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getBoxName() {
        return boxName;
    }

    public void setBoxName(String boxName) {
        this.boxName = boxName;
    }
}
