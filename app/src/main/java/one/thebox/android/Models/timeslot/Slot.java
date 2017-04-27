package one.thebox.android.Models.timeslot;

import java.io.Serializable;

/**
 * Created by developers on 26/04/17.
 */

public class Slot implements Serializable {

    private String name;

    private long timestamp;

    /*********************************************
     * Getter Setter
     *********************************************/
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
