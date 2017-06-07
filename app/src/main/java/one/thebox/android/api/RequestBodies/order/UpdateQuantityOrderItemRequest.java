package one.thebox.android.api.RequestBodies.order;

import java.io.Serializable;

import one.thebox.android.api.RequestBodies.subscribeitem.UpdateQuantitySubscribeItemRequest;

/**
 * Created by developers on 06/06/17.
 */

public class UpdateQuantityOrderItemRequest implements Serializable {

    private int quantity;

    public UpdateQuantityOrderItemRequest(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
