package one.thebox.android.api.RequestBodies;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Ajeet Kumar Meena on 05-05-2016.
 * <p>
 * Updated by Developers on 05/06/2017.
 */
public class MergeSubscriptionRequest implements Serializable {


    @SerializedName("order_uuid")
    private String orderUuid;

    @SerializedName("reason_uuid")
    private String reasonUuid;

    public MergeSubscriptionRequest(String orderUuid, String reasonUuid) {
        this.reasonUuid = reasonUuid;
        this.orderUuid = orderUuid;
    }

    public String getOrderUuid() {
        return orderUuid;
    }

    public void setOrderUuid(String orderUuid) {
        this.orderUuid = orderUuid;
    }

    public String getReasonUuid() {
        return reasonUuid;
    }

    public void setReasonUuid(String reasonUuid) {
        this.reasonUuid = reasonUuid;
    }
}
