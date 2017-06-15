package one.thebox.android.api.RequestBodies.subscribeitem;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by developers on 07/06/17.
 */

public class UpdateItemConfigSubscribeItemRequest implements Serializable {

    @SerializedName("itemconfig_uuid")
    private String itemConfigUuid;

    public UpdateItemConfigSubscribeItemRequest(String itemConfigUuid) {
        this.itemConfigUuid = itemConfigUuid;
    }

    public String getItemConfigUuid() {
        return itemConfigUuid;
    }

    public void setItemConfigUuid(String itemConfigUuid) {
        this.itemConfigUuid = itemConfigUuid;
    }
}
