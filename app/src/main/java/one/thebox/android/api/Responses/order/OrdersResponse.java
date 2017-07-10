package one.thebox.android.api.Responses.order;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Vector;

import one.thebox.android.Models.order.Calender;
import one.thebox.android.Models.order.Order;
import one.thebox.android.api.ApiResponse;

/**
 * Created by developers on 06/06/17.
 */

public class OrdersResponse extends ApiResponse implements Serializable {

    private ArrayList<Order> orders;

    @SerializedName("months")
    private Vector<Calender> calenders;

    public OrdersResponse() {

    }

    public ArrayList<Order> getOrders() {
        return orders;
    }

    public void setOrders(ArrayList<Order> orders) {
        this.orders = orders;
    }

    public Vector<Calender> getCalenders() {
        return calenders;
    }

    public void setCalenders(Vector<Calender> calenders) {
        this.calenders = calenders;
    }
}
