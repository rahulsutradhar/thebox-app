package one.thebox.android.Events;

import one.thebox.android.Models.UserItem;

/**
 * Created by Ajeet Kumar Meena on 12-06-2016.
 */

public class UpdateOrderItemEvent {
    private UserItem userItem;
    private int position;

    public UpdateOrderItemEvent(UserItem userItem, int position) {
        this.userItem = userItem;
        this.position = position;
    }

    public UserItem getUserItem() {
        return userItem;
    }

    public void setUserItem(UserItem userItem) {
        this.userItem = userItem;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
