package one.thebox.android.Models.cart;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by developers on 01/06/17.
 */

public class Cart implements Serializable {

    @SerializedName("item_uuid")
    private String itemUuid;

    private int quantity;

    @SerializedName("itemconfig_uuid")
    private String itemConfigUuid;

    public Cart(String itemUuid, int quantity, String itemConfigUuid) {
        this.itemUuid = itemUuid;
        this.quantity = quantity;
        this.itemConfigUuid = itemConfigUuid;
    }

    public String getItemUuid() {
        return itemUuid;
    }

    public void setItemUuid(String itemUuid) {
        this.itemUuid = itemUuid;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getItemConfigUuid() {
        return itemConfigUuid;
    }

    public void setItemConfigUuid(String itemConfigUuid) {
        this.itemConfigUuid = itemConfigUuid;
    }
}
