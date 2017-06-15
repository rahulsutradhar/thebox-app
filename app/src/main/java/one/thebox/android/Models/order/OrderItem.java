package one.thebox.android.Models.order;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import one.thebox.android.Models.items.ItemConfig;
import one.thebox.android.Models.items.BoxItem;

/**
 * Created by developers on 06/06/17.
 */

public class OrderItem implements Serializable {

    private String uuid;

    private int quantity;

    private int price;

    private int mrp;

    private boolean paid;

    @SerializedName("savings_text")
    private String savingsText;

    @SerializedName("item")
    private BoxItem boxItem;

    @SerializedName("itemconfig")
    private ItemConfig selectedItemConfig;

    /********************************
     * GETTER SETTER
     ********************************/

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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getMrp() {
        return mrp;
    }

    public void setMrp(int mrp) {
        this.mrp = mrp;
    }

    public BoxItem getBoxItem() {
        return boxItem;
    }

    public void setBoxItem(BoxItem boxItem) {
        this.boxItem = boxItem;
    }

    public ItemConfig getSelectedItemConfig() {
        return selectedItemConfig;
    }

    public void setSelectedItemConfig(ItemConfig selectedItemConfig) {
        this.selectedItemConfig = selectedItemConfig;
    }

    public String getSavingsText() {
        return savingsText;
    }

    public void setSavingsText(String savingsText) {
        this.savingsText = savingsText;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }
}
