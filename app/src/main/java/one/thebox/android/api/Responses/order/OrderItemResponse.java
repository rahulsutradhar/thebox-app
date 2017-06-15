package one.thebox.android.api.Responses.order;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import one.thebox.android.Models.order.OrderItem;
import one.thebox.android.api.ApiResponse;

/**
 * Created by developers on 06/06/17.
 */

public class OrderItemResponse extends ApiResponse implements Serializable {

    @SerializedName("orders")
    private ArrayList<OrderItem> orderItems;

    @SerializedName("changes_applicable")
    private boolean changesApplicable;

    public ArrayList<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(ArrayList<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public boolean isChangesApplicable() {
        return changesApplicable;
    }

    public void setChangesApplicable(boolean changesApplicable) {
        this.changesApplicable = changesApplicable;
    }
}
