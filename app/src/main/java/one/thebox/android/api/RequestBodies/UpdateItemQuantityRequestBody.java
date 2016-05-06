package one.thebox.android.api.RequestBodies;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Ajeet Kumar Meena on 26-04-2016.
 */
public class UpdateItemQuantityRequestBody implements Serializable {
    @SerializedName("useritem")
    UserItem userItem;

    public UpdateItemQuantityRequestBody(UserItem userItem) {
        this.userItem = userItem;
    }

    public static class UserItem {
        private int id;
        private int quantity;

        public UserItem(int id, int quantity) {
            this.id = id;
            this.quantity = quantity;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
}
