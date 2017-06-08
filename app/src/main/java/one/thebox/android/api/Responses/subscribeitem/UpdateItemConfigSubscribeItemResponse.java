package one.thebox.android.api.Responses.subscribeitem;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import one.thebox.android.Models.items.SubscribeItem;
import one.thebox.android.api.ApiResponse;

/**
 * Created by developers on 07/06/17.
 */

public class UpdateItemConfigSubscribeItemResponse extends ApiResponse implements Serializable {

    @SerializedName("subscription")
    private SubscribeItem subscribeItem;

    public SubscribeItem getSubscribeItem() {
        return subscribeItem;
    }

    public void setSubscribeItem(SubscribeItem subscribeItem) {
        this.subscribeItem = subscribeItem;
    }
}

