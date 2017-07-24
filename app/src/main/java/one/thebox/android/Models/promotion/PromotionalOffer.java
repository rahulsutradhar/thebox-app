package one.thebox.android.Models.promotion;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by developers on 24/07/17.
 */

public class PromotionalOffer implements Serializable {

    private String uuid;
    private String title;
    private int priority;

    @SerializedName("first_time")
    private boolean firstTime;

    private boolean checked;


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

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isFirstTime() {
        return firstTime;
    }

    public void setFirstTime(boolean firstTime) {
        this.firstTime = firstTime;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
