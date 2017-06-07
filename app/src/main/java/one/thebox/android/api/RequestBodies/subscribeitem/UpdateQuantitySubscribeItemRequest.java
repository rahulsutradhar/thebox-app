package one.thebox.android.api.RequestBodies.subscribeitem;

import java.io.Serializable;

/**
 * Created by developers on 06/06/17.
 */

public class UpdateQuantitySubscribeItemRequest implements Serializable {

    private int quantity;

    public UpdateQuantitySubscribeItemRequest(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
