package one.thebox.android.api.Responses;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import io.realm.RealmList;
import one.thebox.android.Models.Category;
import one.thebox.android.Models.Order;
import one.thebox.android.Models.UserCategory;
import one.thebox.android.Models.UserItem;
import one.thebox.android.api.ApiResponse;

/**
 * Created by Ajeet Kumar Meena on 26-04-2016.
 */
public class UpdateItemConfigResponse extends ApiResponse implements Serializable {

    @SerializedName("success")
    private boolean success;

    @SerializedName("info")
    private String info;

    @SerializedName("useritem")
    private UserItem userItem;

    @SerializedName("rest_of_the_categories")
    private List<Category> restCategories;

    @SerializedName("orders")
    private RealmList<Order> orders;

    @SerializedName("cart")
    private Order cart;

    @SerializedName("box_id")
    private int boxId;

    public Order getCart() {
        return cart;
    }

    public void setCart(Order cart) {
        this.cart = cart;
    }

    public int getBoxId() {
        return boxId;
    }

    public void setBoxId(int boxId) {
        this.boxId = boxId;
    }

    public List<Category> getRestCategories() {
        return restCategories;
    }

    public void setRestCategories(List<Category> restCategories) {
        this.restCategories = restCategories;
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

    public UserItem getUserItem() {
        if (userItem != null) {
            userItem.setBoxId(boxId);
        }
        return userItem;
    }

    public void setUserItem(UserItem userItem) {
        this.userItem = userItem;
    }

    public RealmList<Order> getOrders() {
        return orders;
    }

    public void set_cart(Order cart) {
        this.cart = cart;
    }

    public Order get_cart() {
        return cart;
    }

    public void setOrders(RealmList<Order> orders) {
        this.orders = orders;
    }
}
