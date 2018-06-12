package one.thebox.android.api.RequestBodies.payment;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import one.thebox.android.Models.mycart.Cart;

/**
 * Created by developers on 06/06/17.
 */

public class MakePaymentRequest implements Serializable {
    private boolean cod;

    @SerializedName("address_uuid")
    private String addressUuid;

    @SerializedName("timestamp")
    private long timeSlotTimeStamp;

    private ArrayList<Cart> carts;

    @SerializedName("merge")
    private boolean isMerge;

    @SerializedName("razorpay_payment_id")
    private String razorpayId;

    private double amount;

    @SerializedName("order_uuid")
    private String orderUuid;

    /**
     * COD : Cart
     *
     * @param cod
     * @param isMerge
     * @param addressUuid
     * @param timeSlotTimeStamp
     * @param carts
     */
    public MakePaymentRequest(boolean cod, boolean isMerge, String addressUuid, long timeSlotTimeStamp, ArrayList<Cart> carts) {
        this.cod = cod;
        this.isMerge = isMerge;
        this.addressUuid = addressUuid;
        this.timeSlotTimeStamp = timeSlotTimeStamp;
        this.carts = carts;
    }

    /**
     * COD : Merge Delibvery
     *
     * @param cod
     * @param isMerge
     * @param addressUuid
     * @param orderUuid
     * @param carts
     */
    public MakePaymentRequest(boolean cod, boolean isMerge, String addressUuid, String orderUuid, ArrayList<Cart> carts) {
        this.cod = cod;
        this.isMerge = isMerge;
        this.addressUuid = addressUuid;
        this.orderUuid = orderUuid;
        this.carts = carts;
    }

    /**
     * ONLINE : Cart
     *
     * @param cod
     * @param isMerge
     * @param razorpayId
     * @param addressUuid
     * @param timeSlotTimeStamp
     * @param carts
     */
    public MakePaymentRequest(boolean cod, boolean isMerge, String razorpayId, String addressUuid, long timeSlotTimeStamp, ArrayList<Cart> carts) {
        this.cod = cod;
        this.isMerge = isMerge;
        this.razorpayId = razorpayId;
        this.addressUuid = addressUuid;
        this.timeSlotTimeStamp = timeSlotTimeStamp;
        this.carts = carts;
    }

    /**
     * ONLINE : Merge Delivery
     *
     * @param cod
     * @param isMerge
     * @param razorpayId
     * @param addressUuid
     * @param orderUuid
     * @param carts
     */
    public MakePaymentRequest(boolean cod, boolean isMerge, String razorpayId, String addressUuid, String orderUuid, ArrayList<Cart> carts) {
        this.cod = cod;
        this.isMerge = isMerge;
        this.razorpayId = razorpayId;
        this.addressUuid = addressUuid;
        this.orderUuid = orderUuid;
        this.carts = carts;
    }

    /**
     * COD : Pay for Order
     *
     * @param cod
     * @param orderUuid
     * @param timeSlotTimeStamp
     * @param amount
     */
    public MakePaymentRequest(boolean cod, String orderUuid, long timeSlotTimeStamp, double amount) {
        this.cod = cod;
        this.orderUuid = orderUuid;
        this.timeSlotTimeStamp = timeSlotTimeStamp;
        this.amount = amount;

    }

    /**
     * ONLINE : Pay for Order
     *
     * @param cod
     * @param orderUuid
     * @param timeSlotTimeStamp
     * @param amount
     */
    public MakePaymentRequest(boolean cod, String razorpayId, String orderUuid, long timeSlotTimeStamp, double amount) {
        this.cod = cod;
        this.razorpayId = razorpayId;
        this.timeSlotTimeStamp = timeSlotTimeStamp;
        this.orderUuid = orderUuid;
        this.amount = amount;

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

    public boolean isMerge() {
        return isMerge;
    }

    public void setMerge(boolean merge) {
        isMerge = merge;
    }

    public String getRazorpayId() {
        return razorpayId;
    }

    public void setRazorpayId(String razorpayId) {
        this.razorpayId = razorpayId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getOrderUuid() {
        return orderUuid;
    }

    public void setOrderUuid(String orderUuid) {
        this.orderUuid = orderUuid;
    }
}
