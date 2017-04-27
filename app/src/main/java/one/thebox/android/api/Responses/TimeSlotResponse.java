package one.thebox.android.api.Responses;

import java.util.ArrayList;

import one.thebox.android.Models.timeslot.Slot;
import one.thebox.android.Models.timeslot.TimeSlot;
import one.thebox.android.api.ApiResponse;

/**
 * Created by developers on 26/04/17.
 */

public class TimeSlotResponse extends ApiResponse {

    private ArrayList<TimeSlot> data;

    private String date;

    private ArrayList<Slot> slots;

    public TimeSlotResponse() {

    }

    public ArrayList<TimeSlot> getData() {
        return data;
    }

    public void setData(ArrayList<TimeSlot> data) {
        this.data = data;
    }

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
