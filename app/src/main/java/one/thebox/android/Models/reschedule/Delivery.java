package one.thebox.android.Models.reschedule;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by developers on 01/04/17.
 */

public class Delivery implements Serializable {

    @SerializedName("order_uuid")
    private String orderUuid;

    @SerializedName("delivery_date")
    private String deliveryDate;

    @SerializedName("arriving_at")
    private String arrivingAt;

    /*******************************
     * Getter Setter
     *******************************/
    public String getOrderUuid() {
        return orderUuid;
    }

    public void setOrderUuid(String orderUuid) {
        this.orderUuid = orderUuid;
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
