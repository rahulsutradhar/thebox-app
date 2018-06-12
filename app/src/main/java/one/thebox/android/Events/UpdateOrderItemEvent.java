package one.thebox.android.Events;

import one.thebox.android.Models.order.Order;

/**
 * Created by Ajeet Kumar Meena on 12-06-2016.
 */

public class UpdateOrderItemEvent {

    /**
     * Total Price
     */
    private Order order;

    /**
     * Update Upcoming Fragment
     */
    private int reloadUpcomingFragment;

    public UpdateOrderItemEvent() {

    }

    public int getReloadUpcomingFragment() {
        return reloadUpcomingFragment;
    }

    public void setReloadUpcomingFragment(int reloadUpcomingFragment) {
        this.reloadUpcomingFragment = reloadUpcomingFragment;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
