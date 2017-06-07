package one.thebox.android.Models.items;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;
import one.thebox.android.Models.ItemConfig;

/**
 * Created by developers on 05/06/17.
 */

public class SubscribeItem extends RealmObject implements Serializable {

    private String uuid;

    private int quantity;

    @SerializedName("subscribed_saving_text")
    private String subscribedSavingText;

    @SerializedName("arriving_at")
    private String arrivingAt;

    @SerializedName("itemconfig")
    private ItemConfig selectedItemConfig;

    @SerializedName("item")
    private BoxItem boxItem;

    public SubscribeItem() {

    }

    /************************************
     * Getter Setter
     ************************************/
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSubscribedSavingText() {
        return subscribedSavingText;
    }

    public void setSubscribedSavingText(String subscribedSavingText) {
        this.subscribedSavingText = subscribedSavingText;
    }

    public String getArrivingAt() {
        return arrivingAt;
    }

    public void setArrivingAt(String arrivingAt) {
        this.arrivingAt = arrivingAt;
    }

    public ItemConfig getSelectedItemConfig() {
        return selectedItemConfig;
    }

    public void setSelectedItemConfig(ItemConfig selectedItemConfig) {
        this.selectedItemConfig = selectedItemConfig;
    }

    public BoxItem getBoxItem() {
        return boxItem;
    }

    public void setBoxItem(BoxItem boxItem) {
        this.boxItem = boxItem;
    }
}
