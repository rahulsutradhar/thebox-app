package one.thebox.android.api.RequestBodies.cart;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import one.thebox.android.Models.cart.CartProduct;

/**
 * Created by developers on 01/06/17.
 */

public class CartItemRequest implements Serializable {

    @SerializedName("carts")
    private ArrayList<CartProduct> cartProducts;

    public CartItemRequest(ArrayList<CartProduct> cartProducts) {
        this.cartProducts = cartProducts;
    }

    public ArrayList<CartProduct> getCartProducts() {
        return cartProducts;
    }

    public void setCartProducts(ArrayList<CartProduct> cartProducts) {
        this.cartProducts = cartProducts;
    }
}
