package one.thebox.android.api.Responses;

import java.io.Serializable;

import one.thebox.android.Models.Order;
import one.thebox.android.api.ApiResponse;

/**
 * Created by 32 on 19-05-2016.
 */
public class CartResponse extends ApiResponse implements Serializable {
    private boolean success;
    private String info;
    private Order cart;

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

    public Order getCart() {
        return cart;
    }

    public void setCart(Order cart) {
        this.cart = cart;
    }
}
