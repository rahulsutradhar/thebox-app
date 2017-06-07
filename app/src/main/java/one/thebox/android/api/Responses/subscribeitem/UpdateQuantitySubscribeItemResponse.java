package one.thebox.android.api.Responses.subscribeitem;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import one.thebox.android.Models.items.SubscribeItem;
import one.thebox.android.api.ApiResponse;

/**
 * Created by developers on 06/06/17.
 */

public class UpdateQuantitySubscribeItemResponse extends ApiResponse implements Serializable {

    private boolean deleted;

    @SerializedName("subscription")
    private SubscribeItem subscribeItem;

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public SubscribeItem getSubscribeItem() {
        return subscribeItem;
    }

    public void setSubscribeItem(SubscribeItem subscribeItem) {
        this.subscribeItem = subscribeItem;
    }
}
