package one.thebox.android.Models.mycart;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import one.thebox.android.Models.items.ItemConfig;
import one.thebox.android.Models.items.BoxItem;

/**
 * Created by developers on 09/06/17.
 */

public class CartItem implements Serializable {

    private String uuid;

    private int quantity;

    @SerializedName("itemconfig_uuid")
    private String itemconfigUuid;

    @SerializedName("item_uuid")
    private String boxItemUuid;

    @SerializedName("selected_itemconfig")
    private ItemConfig selectedItemconfig;

    @SerializedName("item")
    private BoxItem boxItem;


    /**************************************
     * GETTER SETTER
     **************************************/

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

    public String getItemconfigUuid() {
        return itemconfigUuid;
    }

    public void setItemconfigUuid(String itemconfigUuid) {
        this.itemconfigUuid = itemconfigUuid;
    }

    public String getBoxItemUuid() {
        return boxItemUuid;
    }

    public void setBoxItemUuid(String boxItemUuid) {
        this.boxItemUuid = boxItemUuid;
    }

    public ItemConfig getSelectedItemconfig() {
        return selectedItemconfig;
    }

    public void setSelectedItemconfig(ItemConfig selectedItemconfig) {
        this.selectedItemconfig = selectedItemconfig;
    }

    public BoxItem getBoxItem() {
        return boxItem;
    }

    public void setBoxItem(BoxItem boxItem) {
        this.boxItem = boxItem;
    }
}
