package one.thebox.android.api.Responses;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import one.thebox.android.Models.items.SubscribeItem;
import one.thebox.android.api.ApiResponse;

/**
 * Created by Ajeet Kumar Meena on 05-05-2016.
 * <p>
 * Updated by Developers on 05/06/2017.
 */
public class MergeSubscriptionResponse extends ApiResponse implements Serializable {

    @SerializedName("subscription")
    private SubscribeItem subscribeItem;

    public SubscribeItem getSubscribeItem() {
        return subscribeItem;
    }

    public void setSubscribeItem(SubscribeItem subscribeItem) {
        this.subscribeItem = subscribeItem;
    }
}
