package one.thebox.android.Events;

import one.thebox.android.Models.UserItem;

/**
 * Created by Ajeet Kumar Meena on 12-06-2016.
 */

public class UpdateOrderItemEvent {

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
}
