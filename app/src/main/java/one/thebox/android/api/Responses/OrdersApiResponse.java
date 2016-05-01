package one.thebox.android.api.Responses;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import one.thebox.android.Models.Order;
import one.thebox.android.api.ApiResponse;

/**
 * Created by Ajeet Kumar Meena on 28-04-2016.
 */
public class OrdersApiResponse extends ApiResponse implements Serializable {

    @SerializedName("success")
    private boolean success;
    @SerializedName("info")
    private String info;
    @SerializedName("orders")
    private ArrayList<Order> orders;

    public boolean isSuccess() {
        return success;
    }

    public String getInfo() {
        return info;
    }

    public ArrayList<Order> getOrders() {
        return orders;
    }
}