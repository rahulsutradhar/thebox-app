package one.thebox.android.api.Responses;

import java.util.ArrayList;

import one.thebox.android.Models.timeslot.TimeSlot;
import one.thebox.android.api.ApiResponse;

/**
 * Created by developers on 26/04/17.
 */

public class TimeSlotResponse extends ApiResponse {

    private ArrayList<TimeSlot> data;

    public TimeSlotResponse() {

    }

    public ArrayList<TimeSlot> getData() {
        return data;
    }

    public void setData(ArrayList<TimeSlot> data) {
        this.data = data;
    }
}
