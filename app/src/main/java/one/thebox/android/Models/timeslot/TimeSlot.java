package one.thebox.android.Models.timeslot;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by developers on 26/04/17.
 */

public class TimeSlot implements Serializable {

    private String date;

    private ArrayList<Slot> slots;

    /*********************************************
     * Getter Setter
     *********************************************/

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<Slot> getSlots() {
        return slots;
    }

    public void setSlots(ArrayList<Slot> slots) {
        this.slots = slots;
    }
}
