package one.thebox.android.api.RequestBodies;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import one.thebox.android.Models.Order;

/**
 * Created by vaibhav on 18/07/16.
 */
public class UpdateOrderItemQuantityRequestBody implements Serializable {
    @SerializedName("order_id")
    int order_id;
    @SerializedName("useritem_id")
    int useritem_id;
    @SerializedName("quantity")
    int quantity;

    public UpdateOrderItemQuantityRequestBody(int order_id,int useritem_id, int quantity) {
        this.order_id = order_id;
        this.useritem_id = useritem_id;
        this.quantity = quantity;
    }

}
