package one.thebox.android.Models.reschedule;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by developers on 01/04/17.
 */

public class Delivery implements Serializable {

    @SerializedName("order_id")
    private int orderId;

    @SerializedName("delivery_date")
    private String deliveryDate;

    @SerializedName("arriving_at")
    private String arrivingAt;

    /*******************************
     * Getter Setter
     *******************************/
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getArrivingAt() {
        return arrivingAt;
    }

    public void setArrivingAt(String arrivingAt) {
        this.arrivingAt = arrivingAt;
    }
}
