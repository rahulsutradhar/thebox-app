package one.thebox.android.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import one.thebox.android.Models.items.BoxItem;

/**
 * Created by Ajeet Kumar Meena on 25-04-2016.
 */
public class Order extends RealmObject implements Serializable {

    @Ignore
    public static final String FIELD_ID = "id";
    @Ignore
    public static final String FIELD_IS_CART = "cart";

    @SerializedName("id")
    private int id;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("address_id")
    private int addressId;

    @SerializedName("delivery_scheduled_at")
    private String deliveryScheduleAt;

    @SerializedName("delivered")
    private boolean delivered;

    @SerializedName("open")
    private boolean open;


    @SerializedName("tax")
    private float tax;

    @SerializedName("cart")
    private boolean cart;

    @SerializedName("useritems")
    private RealmList<UserItem> userItems;

    @SerializedName("useritem_quantites")
    private RealmList<Invoice> useritem_quantites;

    @SerializedName("payment_amount_remaining")
    private int payment_amount_remaining;

    public Order() {
    }

    public Order(Order order) {

    }

    public Order(int id, int userId, int addressId, String deliveryScheduleAt, boolean paid, boolean delivered, boolean open, float totalPrice, float deliveryCharges, float tax, boolean cart, RealmList<UserItem> userItems, RealmList<Invoice> useritem_quantites, int payment_amount_remaining) {
        this.id = id;
        this.userId = userId;
        this.addressId = addressId;
        this.deliveryScheduleAt = deliveryScheduleAt;
        this.paid = paid;
        this.delivered = delivered;
        this.open = open;
        this.totalPrice = totalPrice;
        this.tax = tax;
        this.cart = cart;
        this.userItems = userItems;
        this.useritem_quantites = useritem_quantites;
        this.payment_amount_remaining = payment_amount_remaining;
    }

    public boolean is_partial_payment_amount_remaining() {
        if (this.payment_amount_remaining > 0) {
            return true;
        } else {
            return false;
        }
    }

    public int get_payment_amount_remaining() {
        return payment_amount_remaining;
    }

    public RealmList<UserItem> getUserItems() {
        return userItems;
    }

    public void setUserItems(RealmList<UserItem> userItems) {
        this.userItems = userItems;
    }

    public RealmList<Invoice> getUserItemQuantities() {
        return useritem_quantites;
    }

    public void setUserItemQuantities(RealmList<Invoice> useritem_quantites) {
        this.useritem_quantites = useritem_quantites;
    }

    public RealmList<BoxItem> getBoxItemsObjectFromUserItem() {
        RealmList<BoxItem> boxItems = new RealmList<>();
        for (UserItem userItem : userItems) {
            BoxItem boxItem = userItem.getBoxItem();
            boxItem.setUserItemId(userItem.getId());
            boxItem.setQuantity(userItem.getQuantity());
            boxItem.setSelectedItemConfig(boxItem.getItemConfigById(userItem.getSelectedConfigId()));
            boxItem.setSavingsTitle(userItem.getSelectedItemConfigSavingsTitle());
            boxItems.add(boxItem);
        }
        return boxItems;
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

    public boolean isCart() {
        return cart;
    }

    public void setCart(boolean cart) {
        this.cart = cart;
    }

    public String getItemString() {
        String itemString = "";
        if (userItems == null || userItems.isEmpty()) {
            return "Empty order";
        }
        for (int i = 0; i < userItems.size(); i++) {
            if (i == 3) {
                itemString = itemString.substring(0, itemString.length() - 2);
                itemString = itemString + "...+ " + userItems.size() + " items";
                return itemString;
            }
            itemString = userItems.get(i).getBoxItem().getTitle() + ", " + itemString;
        }
        itemString = itemString.substring(0, itemString.length() - 2);
        return itemString;
    }

    public float getTax() {
        return tax;
    }

    public void setTax(float tax) {
        this.tax = tax;
    }


    public boolean isDelivered() {
        return delivered;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public int getTotalPriceOfUserItems() {
        int total = 0;
        for (int i = 0; i < userItems.size(); i++) {
            total = total + userItems.get(i).getTotalPriceForAnOrder();
        }
        return total;
    }

    public int getTotalPriceOfUserItemsForCart() {
        int total = 0;
        for (int i = 0; i < userItems.size(); i++) {
            total = total + userItems.get(i).getTotalPrice();
        }
        return total;
    }


    /**
     * Refactor
     */
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

    @SerializedName("amount_to_pay")
    private float amountToPay;

    @SerializedName("payment_text")
    private String paymentText;


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

    public float getAmountToPay() {
        return amountToPay;
    }

    public void setAmountToPay(float amountToPay) {
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


}
