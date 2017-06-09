package one.thebox.android.api.RequestBodies.cart;

import java.io.Serializable;
import java.util.ArrayList;

import one.thebox.android.Models.cart.Cart;

/**
 * Created by developers on 02/06/17.
 */

public class PaymentSummaryRequest implements Serializable {

    private ArrayList<Cart> carts;

    public PaymentSummaryRequest(ArrayList<Cart> carts){
        this.carts = carts;
    }

    public ArrayList<Cart> getCarts() {
        return carts;
    }

    public void setCarts(ArrayList<Cart> carts) {
        this.carts = carts;
    }
}
