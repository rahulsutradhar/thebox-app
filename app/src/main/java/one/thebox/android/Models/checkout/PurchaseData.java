package one.thebox.android.Models.checkout;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by developers on 25/05/17.
 */

public class PurchaseData implements Serializable {

    private String title;

    @SerializedName("data")
    private ArrayList<ProductDetail> productDetails;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<ProductDetail> getProductDetails() {
        return productDetails;
    }

    public void setProductDetails(ArrayList<ProductDetail> productDetails) {
        this.productDetails = productDetails;
    }
}
