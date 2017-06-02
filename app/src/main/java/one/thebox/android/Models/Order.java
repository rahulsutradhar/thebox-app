package one.thebox.android.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

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

    @SerializedName("paid")
    private boolean paid;

    @SerializedName("delivered")
    private boolean delivered;

    @SerializedName("open")
    private boolean open;

    @SerializedName("total_price")
    private float totalPrice;

    @SerializedName("delivery_charges")
    private float deliveryCharges;

    @SerializedName("tax")
    private float tax;

    @SerializedName("cart")
    private boolean cart;

    @SerializedName("useritems")
    private RealmList<UserItem> userItems;

    @SerializedName("useritem_quantites")
    private RealmList<Invoice> useritem_quantites;

    @SerializedName("cod")
    private boolean cod;

    @SerializedName("successfull")
    private boolean successful;

    @SerializedName("payment_amount_remaining")
    private int payment_amount_remaining;

    @SerializedName("order_date")
    private String orderDate;

    @SerializedName("delivery_slot_duration")
    private String deliverySlot;

    public Order() {
    }

    public Order(Order order) {
        this.id = order.getId();
        this.userId = order.getUserId();
        this.addressId = order.getAddressId();
        this.deliveryScheduleAt = order.getDeliveryScheduleAt();
        this.paid = order.isPaid();
        this.delivered = order.isDelivered();
        this.open = order.isOpen();
        this.totalPrice = order.getTotalPrice();
        this.deliveryCharges = order.getDeliveryCharges();
        this.tax = order.getDeliveryCharges();
        this.cart = order.isCart();
        this.payment_amount_remaining = order.get_payment_amount_remaining();
        if (order.getUserItems() != null) {
            this.userItems = new RealmList<>();
            this.userItems.addAll(order.getUserItems());
        }

        if (order.getUserItemQuantities() != null) {
            this.useritem_quantites = new RealmList<>();
            this.useritem_quantites.addAll(order.getUserItemQuantities());
        }


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
        this.deliveryCharges = deliveryCharges;
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

    public float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
    }

    public float getDeliveryCharges() {
        return deliveryCharges;
    }

    public void setDeliveryCharges(float deliveryCharges) {
        this.deliveryCharges = deliveryCharges;
    }

    public float getTax() {
        return tax;
    }

    public void setTax(float tax) {
        this.tax = tax;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
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

    public boolean isCod() {
        return cod;
    }

    public void setCod(boolean cod) {
        this.cod = cod;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getDeliverySlot() {
        return deliverySlot;
    }

    public void setDeliverySlot(String deliverySlot) {
        this.deliverySlot = deliverySlot;
    }


    /**
     * Refactor
     */
    @PrimaryKey
    private String uuid;

    private String date;

    @SerializedName("time_slot")
    private String timeSlot;

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
}
