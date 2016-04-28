package one.thebox.android.api.RequestBodies;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import one.thebox.android.api.ApiResponse;

/**
 * Created by Ajeet Kumar Meena on 26-04-2016.
 */
public class UpdateItemConfigurationRequest implements Serializable {

    @SerializedName("useritem")
    private UserItem userItem;

    @SerializedName("itemconfig")
    private ItemConfig itemConfig;

    public UpdateItemConfigurationRequest(UserItem userItem, ItemConfig itemConfig) {
        this.userItem = userItem;
        this.itemConfig = itemConfig;
    }

    public static class UserItem {
        int id;

        public UserItem(int id) {
            this.id = id;
        }
    }

    public static class ItemConfig {
        int id;

        public ItemConfig(int id) {
            this.id = id;
        }
    }
}
