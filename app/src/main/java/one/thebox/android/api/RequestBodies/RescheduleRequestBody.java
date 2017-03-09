package one.thebox.android.api.RequestBodies;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
import java.util.ArrayList;

import one.thebox.android.Models.AddressAndOrder;

/**
 * Created by vaibhav on 15/08/16.
 */
public class RescheduleRequestBody implements Serializable {

    @SerializedName("date")
    private Date reschedule_to;

    @SerializedName("order_id")
    private int orderId;

    public RescheduleRequestBody(Date reschedule_to, int orderId) {
        this.reschedule_to = reschedule_to;
        this.orderId = orderId;
    }
}


