package one.thebox.android.Models.items;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;

public class UserItem extends RealmObject implements Serializable {

    @SerializedName("available")
    private boolean SubscribeItemAvailable;

    @SerializedName("subscription")
    private SubscribeItem subscribeItem;

    /************************************
     * Getter Setter
     ************************************/

    public boolean isSubscribeItemAvailable() {
        return SubscribeItemAvailable;
    }

    public void setSubscribeItemAvailable(boolean subscribeItemAvailable) {
        SubscribeItemAvailable = subscribeItemAvailable;
    }

    public SubscribeItem getSubscribeItem() {
        return subscribeItem;
    }

    public void setSubscribeItem(SubscribeItem subscribeItem) {
        this.subscribeItem = subscribeItem;
    }
}
