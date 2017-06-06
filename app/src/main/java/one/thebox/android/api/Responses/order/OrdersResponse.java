package one.thebox.android.api.Responses.order;

import java.io.Serializable;
import java.util.ArrayList;

import one.thebox.android.Models.Order;
import one.thebox.android.api.ApiResponse;

/**
 * Created by developers on 06/06/17.
 */

public class OrdersResponse extends ApiResponse implements Serializable {

    private ArrayList<Order> orders;

    public OrdersResponse() {

    }

    public ArrayList<Order> getOrders() {
        return orders;
    }

    public void setOrders(ArrayList<Order> orders) {
        this.orders = orders;
    }
}
