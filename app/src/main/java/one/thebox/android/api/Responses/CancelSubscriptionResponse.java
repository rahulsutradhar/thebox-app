package one.thebox.android.api.Responses;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmList;
import one.thebox.android.Models.Order;
import one.thebox.android.Models.UserItem;
import one.thebox.android.api.ApiResponse;

/**
 * Created by Ajeet Kumar Meena on 05-05-2016.
 */
public class CancelSubscriptionResponse extends ApiResponse implements Serializable {
    @SerializedName("success")
    private boolean success;
    @SerializedName("info")
    private String info;
    @SerializedName("cancel_subscription_reason")
    private CancelReason cancelReason;
    @SerializedName("useritem")
    private UserItem userItem;
    @SerializedName("orders")
    private RealmList<Order> orders;

    public UserItem getUserItem() {
        return userItem;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public CancelReason getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(CancelReason cancelReason) {
        this.cancelReason = cancelReason;
    }

    public RealmList<Order> getOrders() {
        return orders;
    }

    public void setOrders(RealmList<Order> orders) {
        this.orders = orders;
    }

    public static class CancelReason {
        @SerializedName("id")
        private int id;
        @SerializedName("reason")
        private String reason;
        @SerializedName("useritem_id")
        private String userItemId;
        @SerializedName("cancelled_item_id")
        private String cancelItemId;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public String getUserItemId() {
            return userItemId;
        }

        public void setUserItemId(String userItemId) {
            this.userItemId = userItemId;
        }

        public String getCancelItemId() {
            return cancelItemId;
        }

        public void setCancelItemId(String cancelItemId) {
            this.cancelItemId = cancelItemId;
        }
    }
}
