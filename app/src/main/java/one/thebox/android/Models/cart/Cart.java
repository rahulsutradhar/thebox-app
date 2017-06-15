package one.thebox.android.Models.cart;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by developers on 01/06/17.
 */

public class Cart implements Serializable {

    @SerializedName("item_uuid")
    private String boxItemUuid;

    private int quantity;

    @SerializedName("itemconfig_uuid")
    private String itemConfigUuid;

    public Cart(String boxItemUuid, int quantity, String itemConfigUuid) {
        this.boxItemUuid = boxItemUuid;
        this.quantity = quantity;
        this.itemConfigUuid = itemConfigUuid;
    }

    public String getBoxItemUuid() {
        return boxItemUuid;
    }

    public void setBoxItemUuid(String boxItemUuid) {
        this.boxItemUuid = boxItemUuid;
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
