package one.thebox.android.api.RequestBodies;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Ajeet Kumar Meena on 05-05-2016.
 */
public class MergeSubscriptionRequest implements Serializable {

    @SerializedName("useritem")
    private UserItem userItem;
    @SerializedName("order")
    private Order order;

    public MergeSubscriptionRequest(int userItemId, int orderId) {
        this.userItem = new UserItem(userItemId);
        this.order = new Order(orderId);
    }

    class UserItem {
        int id;

        public UserItem(int id) {
            this.id = id;
        }
    }

    class Order {
        int id;

        public Order(int id) {
            this.id = id;
        }
    }
}
