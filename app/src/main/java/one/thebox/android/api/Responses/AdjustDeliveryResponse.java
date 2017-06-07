package one.thebox.android.api.Responses;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmList;
import one.thebox.android.Models.order.Order;
import one.thebox.android.api.ApiResponse;

/**
 * Created by Ajeet Kumar Meena on 10-05-2016.
 */
public class AdjustDeliveryResponse extends ApiResponse implements Serializable {
    @SerializedName("success")
    private boolean success;
    @SerializedName("info")
    private String info;
    @SerializedName("orders_after_next_order")
    private RealmList<Order> nextOrder;
    @SerializedName("orders_before_next_order")
    private RealmList<Order> ordersBeforeNextOrder;

    public boolean isSuccess() {
        return success;
    }

    public String getInfo() {
        return info;
    }

    public RealmList<Order> getNextOrder() {
        return nextOrder;
    }

    public RealmList<Order> getOrdersBeforeNextOrder() {
        return ordersBeforeNextOrder;
    }


}
