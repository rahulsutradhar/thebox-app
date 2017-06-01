package one.thebox.android.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by developers on 01/06/17.
 */

public class Cart implements Serializable {

    @SerializedName("item_uuid")
    private String itemUuid;

    private int quanity;

    @SerializedName("itemconfig_uuid")
    private String itemConfigUuid;

    public Cart(String itemUuid, int quanity, String itemConfigUuid) {
        this.itemUuid = itemUuid;
        this.quanity = quanity;
        this.itemConfigUuid = itemConfigUuid;
    }

    public String getItemUuid() {
        return itemUuid;
    }

    public void setItemUuid(String itemUuid) {
        this.itemUuid = itemUuid;
    }

    public int getQuanity() {
        return quanity;
    }

    public void setQuanity(int quanity) {
        this.quanity = quanity;
    }

    public String getItemConfigUuid() {
        return itemConfigUuid;
    }

    public void setItemConfigUuid(String itemConfigUuid) {
        this.itemConfigUuid = itemConfigUuid;
    }
}
