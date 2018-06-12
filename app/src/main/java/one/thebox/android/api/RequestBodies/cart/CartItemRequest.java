package one.thebox.android.api.RequestBodies.cart;

import java.io.Serializable;
import java.util.ArrayList;

import one.thebox.android.Models.mycart.Cart;

/**
 * Created by developers on 01/06/17.
 */

public class CartItemRequest implements Serializable {

    private ArrayList<Cart> carts;

    public CartItemRequest(ArrayList<Cart> carts) {
        this.carts = carts;
    }

    public ArrayList<Cart> getCarts() {
        return carts;
    }

    public void setCarts(ArrayList<Cart> carts) {
        this.carts = carts;
    }
}
