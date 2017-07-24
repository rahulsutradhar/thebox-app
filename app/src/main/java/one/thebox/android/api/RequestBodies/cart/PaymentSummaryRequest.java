package one.thebox.android.api.RequestBodies.cart;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import one.thebox.android.Models.cart.CartProduct;

/**
 * Created by developers on 02/06/17.
 */

public class PaymentSummaryRequest implements Serializable {

    @SerializedName("carts")
    private ArrayList<CartProduct> cartProducts;

    public PaymentSummaryRequest(ArrayList<CartProduct> cartProducts) {
        this.cartProducts = cartProducts;
    }

    public ArrayList<CartProduct> getCartProducts() {
        return cartProducts;
    }

    public void setCartProducts(ArrayList<CartProduct> cartProducts) {
        this.cartProducts = cartProducts;
    }
}
