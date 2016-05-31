package one.thebox.android.api.RequestBodies;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by 32 on 28-05-2016.
 */

public class MergeCartToOrderRequestBody implements Serializable{
    @SerializedName("order_id")
    int mergeOrderId;

    public MergeCartToOrderRequestBody(int mergeOrderId) {
        this.mergeOrderId = mergeOrderId;
    }
}
