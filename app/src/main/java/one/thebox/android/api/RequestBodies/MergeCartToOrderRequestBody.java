package one.thebox.android.api.RequestBodies;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by 32 on 28-05-2016.
 */

public class MergeCartToOrderRequestBody implements Serializable{
    @SerializedName("order_id")
    private int mergeOrderId;
    private String lat;
    private String lng;

    public MergeCartToOrderRequestBody(int mergeOrderId, String lat, String lng) {
        this.mergeOrderId = mergeOrderId;
        this.lat = lat;
        this.lng = lng;
    }
}
