package one.thebox.android.api.Responses;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import one.thebox.android.Models.reschedule.Reschedule;
import one.thebox.android.api.ApiResponse;

/**
 * Created by developers on 01/04/17.
 */

public class RescheduleResponse extends ApiResponse implements Serializable {

    private String title;

    @SerializedName("next_arriving_at")
    private String nextArrivingAt;

    @SerializedName("next_delivery_at")
    private String nextDeliveryAt;

    @SerializedName("data")
    private ArrayList<Reschedule> reschedules;

    /*******************************
     * Getter Setter
     *******************************/
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNextArrivingAt() {
        return nextArrivingAt;
    }

    public void setNextArrivingAt(String nextArrivingAt) {
        this.nextArrivingAt = nextArrivingAt;
    }

    public String getNextDeliveryAt() {
        return nextDeliveryAt;
    }

    public void setNextDeliveryAt(String nextDeliveryAt) {
        this.nextDeliveryAt = nextDeliveryAt;
    }

    public ArrayList<Reschedule> getReschedules() {
        return reschedules;
    }

    public void setReschedules(ArrayList<Reschedule> reschedules) {
        this.reschedules = reschedules;
    }
}
