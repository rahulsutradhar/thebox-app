package one.thebox.android.api.Responses.order;

import java.io.Serializable;

import one.thebox.android.Models.order.Order;
import one.thebox.android.api.ApiResponse;

/**
 * Created by developers on 06/06/17.
 */

public class RescheduleOrderResponse extends ApiResponse implements Serializable {

    private Order order;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
