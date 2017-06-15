package one.thebox.android.api.Responses.order;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import one.thebox.android.Models.order.Order;
import one.thebox.android.Models.order.OrderItem;
import one.thebox.android.api.ApiResponse;

/**
 * Created by developers on 06/06/17.
 */

public class UpdateQuantityOrderItemResponse extends ApiResponse implements Serializable {

    private boolean deleted;

    @SerializedName("invoice")
    private OrderItem orderItem;

    private Order order;

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public OrderItem getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
