package one.thebox.android.Models.timeslot;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by developers on 04/05/17.
 */

public class TimeSlotInformation implements Serializable {

    private String title;

    @SerializedName("popup_title")
    private String popupTitle;

    private String content;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPopupTitle() {
        return popupTitle;
    }

    public void setPopupTitle(String popupTitle) {
        this.popupTitle = popupTitle;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
