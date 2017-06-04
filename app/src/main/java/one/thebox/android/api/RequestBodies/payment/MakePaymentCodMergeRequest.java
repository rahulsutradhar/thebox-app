package one.thebox.android.api.RequestBodies.payment;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import one.thebox.android.Models.Cart;

/**
 * Created by developers on 04/06/17.
 */

public class MakePaymentCodMergeRequest implements Serializable {


    private boolean cod;

    @SerializedName("address_uuid")
    private String addressUuid;

    private ArrayList<Cart> carts;

    @SerializedName("merge")
    private boolean isMerge;

    @SerializedName("order_uuid")
    private String orderUuid;

    /**
     * COD : Merge Delibvery
     *
     * @param cod
     * @param isMerge
     * @param addressUuid
     * @param orderUuid
     * @param carts
     */
    public MakePaymentCodMergeRequest(boolean cod, boolean isMerge, String addressUuid, String orderUuid, ArrayList<Cart> carts) {
        this.cod = cod;
        this.isMerge = isMerge;
        this.addressUuid = addressUuid;
        this.orderUuid = orderUuid;
        this.carts = carts;
    }

    public boolean isCod() {
        return cod;
    }

    public void setCod(boolean cod) {
        this.cod = cod;
    }

    public String getAddressUuid() {
        return addressUuid;
    }

    public void setAddressUuid(String addressUuid) {
        this.addressUuid = addressUuid;
    }

    public ArrayList<Cart> getCarts() {
        return carts;
    }

    public void setCarts(ArrayList<Cart> carts) {
        this.carts = carts;
    }

    public boolean isMerge() {
        return isMerge;
    }

    public void setMerge(boolean merge) {
        isMerge = merge;
    }

    public String getOrderUuid() {
        return orderUuid;
    }

    public void setOrderUuid(String orderUuid) {
        this.orderUuid = orderUuid;
    }

}
