package one.thebox.android.Events;

import one.thebox.android.Models.items.BoxItem;

/**
 * Created by developers on 10/06/17.
 */

public class SyncCartItemEvent {

    private int eventType;

    private BoxItem boxItem;

    public SyncCartItemEvent(int eventType, BoxItem boxItem) {
        this.eventType = eventType;
        this.boxItem = boxItem;
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public BoxItem getBoxItem() {
        return boxItem;
    }

    public void setBoxItem(BoxItem boxItem) {
        this.boxItem = boxItem;
    }
}
