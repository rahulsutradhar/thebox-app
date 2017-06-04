package one.thebox.android.api.Responses.cart;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import one.thebox.android.Models.checkout.PurchaseData;
import one.thebox.android.api.ApiResponse;

/**
 * Created by developers on 02/06/17.
 */

public class PaymentSummaryResponse extends ApiResponse implements Serializable {

    @SerializedName("data")
    private ArrayList<PurchaseData> purchaseDatas;

    @SerializedName("total_price")
    private String amountToPay;

    public ArrayList<PurchaseData> getPurchaseDatas() {
        return purchaseDatas;
    }

    public void setPurchaseDatas(ArrayList<PurchaseData> purchaseDatas) {
        this.purchaseDatas = purchaseDatas;
    }

    public String getAmountToPay() {
        return amountToPay;
    }

    public void setAmountToPay(String amountToPay) {
        this.amountToPay = amountToPay;
    }

}
