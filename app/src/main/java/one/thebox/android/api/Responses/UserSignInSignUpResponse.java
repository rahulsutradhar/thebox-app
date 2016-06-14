package one.thebox.android.api.Responses;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import one.thebox.android.Models.Order;
import one.thebox.android.Models.User;
import one.thebox.android.api.ApiResponse;

/**
 * Created by Ajeet Kumar Meena on 19-04-2016.
 */
public class UserSignInSignUpResponse extends ApiResponse implements Serializable{
    @SerializedName("success")
    private boolean success;
    @SerializedName("info")
    private String info;
    @SerializedName("user")
    private User user;
    @SerializedName("cart")
    private Order cart;
    public UserSignInSignUpResponse(boolean success, String info, User user) {
        this.success = success;
        this.info = info;
        this.user = user;
    }

    public Order getCart() {
        return cart;
    }

    public void setCart(Order cart) {
        this.cart = cart;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
