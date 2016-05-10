package one.thebox.android.api.Responses;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import one.thebox.android.Models.Order;
import one.thebox.android.api.ApiResponse;

/**
 * Created by Ajeet Kumar Meena on 10-05-2016.
 */
public class AdjustDeliveryResponse extends ApiResponse implements Serializable {
    @SerializedName("success")
    private boolean success;
    @SerializedName("info")
    private String info;
    @SerializedName("next_order")
    private ArrayList<Order> nextOrder;
    @SerializedName("orders_before_next_order")
    private ArrayList<Order> ordersBeforeNextOrder;

    public boolean isSuccess() {
        return success;
    }

    public String getInfo() {
        return info;
    }

    public ArrayList<Order> getNextOrder() {
        return nextOrder;
    }

    public ArrayList<Order> getOrdersBeforeNextOrder() {
        return ordersBeforeNextOrder;
    }
}
