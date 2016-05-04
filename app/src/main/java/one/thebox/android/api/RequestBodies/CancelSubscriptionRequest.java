package one.thebox.android.api.RequestBodies;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Ajeet Kumar Meena on 05-05-2016.
 */
public class CancelSubscriptionRequest implements Serializable {

    @SerializedName("useritem")
    private UserItem userItem;

    public CancelSubscriptionRequest(int id, String reason) {
        this.userItem = new UserItem(id, reason);
    }

    class UserItem {
        @SerializedName("id")
        private int id;
        @SerializedName("reason")
        private String reason;

        public UserItem(int id, String reason) {
            this.id = id;
            this.reason = reason;
        }
    }
}
