package one.thebox.android.Models.timeslot;

import java.io.Serializable;

/**
 * Created by developers on 04/05/17.
 */

public class TimeSlotInformation implements Serializable {

    private String title;

    private String content;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
