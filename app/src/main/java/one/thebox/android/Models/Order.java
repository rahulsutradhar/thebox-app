package one.thebox.android.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Ajeet Kumar Meena on 25-04-2016.
 */
public class Order implements Serializable {

    @SerializedName("id")
    private int id;
    @SerializedName("user_id")
    private int userId;
    @SerializedName("address_id")
    private int addressId;
    @SerializedName("delivery_schedule_at")
    private String deliveryScheduleAt;
    @SerializedName("paid")
    private String paid;
    @SerializedName("delivered")
    private String delivered;
    @SerializedName("open")
    private String open;
    @SerializedName("total_price")
    private String totalPrice;
    @SerializedName("delivery_charges")
    private String deliveryCharges;
    @SerializedName("tax")
    private String tax;
    @SerializedName("cart")
    private boolean cart;

    public Order(int id, int userId, int addressId, String deliveryScheduleAt, String paid, String delivered, String open, String totalPrice, String deliveryCharges, String tax, boolean cart) {
        this.id = id;
        this.userId = userId;
        this.addressId = addressId;
        this.deliveryScheduleAt = deliveryScheduleAt;
        this.paid = paid;
        this.delivered = delivered;
        this.open = open;
        this.totalPrice = totalPrice;
        this.deliveryCharges = deliveryCharges;
        this.tax = tax;
        this.cart = cart;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public String getDeliveryScheduleAt() {
        return deliveryScheduleAt;
    }

    public void setDeliveryScheduleAt(String deliveryScheduleAt) {
        this.deliveryScheduleAt = deliveryScheduleAt;
    }

    public String getPaid() {
        return paid;
    }

    public void setPaid(String paid) {
        this.paid = paid;
    }

    public String getDelivered() {
        return delivered;
    }

    public void setDelivered(String delivered) {
        this.delivered = delivered;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getDeliveryCharges() {
        return deliveryCharges;
    }

    public void setDeliveryCharges(String deliveryCharges) {
        this.deliveryCharges = deliveryCharges;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public boolean isCart() {
        return cart;
    }

    public void setCart(boolean cart) {
        this.cart = cart;
    }
}
