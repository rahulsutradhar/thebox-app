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
public class MergeSubscriptionResponse extends ApiResponse implements Serializable {

    @SerializedName("useritem")
    UserItem userItem;
    @SerializedName("success")
    private boolean success;
    @SerializedName("info")
    private String info;
    @SerializedName("orders")
    private RealmList<Order> orders;


    public UserItem getUserItem() {
        return userItem;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getInfo() {
        return info;
    }

    public RealmList<Order> getOrders() {
        return orders;
    }

    public void setOrders(RealmList<Order> orders) {
        this.orders = orders;
    }

}
