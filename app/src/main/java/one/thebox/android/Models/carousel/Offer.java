package one.thebox.android.Models.carousel;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by developers on 28/03/17.
 */

public class Offer extends RealmObject implements Serializable {

    @SerializedName("start_date")
    private String startDate;

    @SerializedName("end_date")
    private String endDate;

    private boolean enable;

    private int priority;

    @SerializedName("image_url")
    private String imageUrl;

    /***********************************************
     * Getter Setter
     ***********************************************/

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
