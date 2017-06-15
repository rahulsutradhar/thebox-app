package one.thebox.android.Models.checkout;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by developers on 25/05/17.
 */

public class ProductDetail implements Serializable {

    private String name;

    @SerializedName("total_price")
    private String totalPrice;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }
}
