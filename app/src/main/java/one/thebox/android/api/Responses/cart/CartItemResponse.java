package one.thebox.android.api.Responses.cart;

import java.io.Serializable;
import java.util.ArrayList;

import one.thebox.android.Models.Order;
import one.thebox.android.api.ApiResponse;

/**
 * Created by developers on 01/06/17.
 */

public class CartItemResponse extends ApiResponse implements Serializable {

    private boolean merge;

    private ArrayList<Order> orders;

    public boolean isMerge() {
        return merge;
    }

    public void setMerge(boolean merge) {
        this.merge = merge;
    }

    public ArrayList<Order> getOrders() {
        return orders;
    }

    public void setOrders(ArrayList<Order> orders) {
        this.orders = orders;
    }
}

