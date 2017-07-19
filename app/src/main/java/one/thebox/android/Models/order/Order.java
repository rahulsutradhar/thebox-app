package one.thebox.android.Models.order;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Ajeet Kumar Meena on 25-04-2016.
 */
public class Order extends RealmObject implements Serializable {

    @PrimaryKey
    private String uuid;

    /**
     * For Time slots
     */
    private String date;

    @SerializedName("time_slot")
    private String timeSlot;

    /**
     * For displaying orders
     */
    @SerializedName("order_date")
    private String orderDate;

    @SerializedName("delivery_slot_duration")
    private String deliverySlotDuration;

    @SerializedName("total_price")
    private float totalPrice;

    @SerializedName("no_of_items")
    private int noOfItems;

    private boolean cod;

    private boolean paid;

    private boolean delivered;

    @SerializedName("amount_to_pay")
    private double amountToPay;

    @SerializedName("payment_text")
    private String paymentText;

    @SerializedName("reminder_text")
    private String reminderText;

    @SerializedName("payment_complete")
    private boolean paymentComplete;

    @SerializedName("scheduled_text")
    private String scheduledText;


    /****************************************
     * Getter Setter
     ****************************************/
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getDeliverySlotDuration() {
        return deliverySlotDuration;
    }

    public void setDeliverySlotDuration(String deliverySlotDuration) {
        this.deliverySlotDuration = deliverySlotDuration;
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getNoOfItems() {
        return noOfItems;
    }

    public void setNoOfItems(int noOfItems) {
        this.noOfItems = noOfItems;
    }

    public double getAmountToPay() {
        return amountToPay;
    }

    public void setAmountToPay(double amountToPay) {
        this.amountToPay = amountToPay;
    }

    public String getPaymentText() {
        return paymentText;
    }

    public void setPaymentText(String paymentText) {
        this.paymentText = paymentText;
    }

    public boolean isCod() {
        return cod;
    }

    public void setCod(boolean cod) {
        this.cod = cod;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public String getReminderText() {
        return reminderText;
    }

    public void setReminderText(String reminderText) {
        this.reminderText = reminderText;
    }

    public boolean isPaymentComplete() {
        return paymentComplete;
    }

    public void setPaymentComplete(boolean paymentComplete) {
        this.paymentComplete = paymentComplete;
    }

    public boolean isDelivered() {
        return delivered;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }

    public String getScheduledText() {
        return scheduledText;
    }

    public void setScheduledText(String scheduledText) {
        this.scheduledText = scheduledText;
    }
}
