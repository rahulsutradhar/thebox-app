package one.thebox.android.api.RequestBodies.payment;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import one.thebox.android.Models.Cart;

/**
 * Created by developers on 04/06/17.
 */

public class MakePaymentRequest implements Serializable {

    private boolean cod;

    @SerializedName("address_uuid")
    private String addressUuid;

    @SerializedName("timestamp")
    private long timeSlotTimeStamp;

    private ArrayList<Cart> carts;

    public MakePaymentRequest(boolean cod, String addressUuid, long timeSlotTimeStamp, ArrayList<Cart> carts) {
        this.cod = cod;
        this.addressUuid = addressUuid;
        this.timeSlotTimeStamp = timeSlotTimeStamp;
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

    public long getTimeSlotTimeStamp() {
        return timeSlotTimeStamp;
    }

    public void setTimeSlotTimeStamp(long timeSlotTimeStamp) {
        this.timeSlotTimeStamp = timeSlotTimeStamp;
    }

    public ArrayList<Cart> getCarts() {
        return carts;
    }

    public void setCarts(ArrayList<Cart> carts) {
        this.carts = carts;
    }
}
