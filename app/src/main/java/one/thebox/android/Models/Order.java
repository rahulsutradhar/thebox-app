package one.thebox.android.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

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

    @PrimaryKey
    @SerializedName("id")
    private int id;
    @SerializedName("user_id")
    private int userId;
    @SerializedName("address_id")
    private int addressId;
    @SerializedName("delivery_scheduled_at")
    private String deliveryScheduleAt;
    @SerializedName("paid")
    private String paid;
    @SerializedName("delivered")
    private String delivered;
    @SerializedName("open")
    private String open;
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

    public Order() {
    }

    public Order(Order order) {
        this.id = order.getId();
        this.userId = order.getUserId();
        this.addressId = order.getAddressId();
        this.deliveryScheduleAt = order.getDeliveryScheduleAt();
        this.paid = order.getPaid();
        this.delivered = order.getDelivered();
        this.open = order.getOpen();
        this.totalPrice = order.getTotalPrice();
        this.deliveryCharges = order.getDeliveryCharges();
        this.tax = order.getDeliveryCharges();
        this.cart = order.isCart();
        if (order.getUserItems() != null) {
            this.userItems = new RealmList<>();
            this.userItems.addAll(order.getUserItems());
        }
    }

    public Order(int id, int userId, int addressId, String deliveryScheduleAt, String paid, String delivered, String open, float totalPrice, float deliveryCharges, float tax, boolean cart, RealmList<UserItem> userItems) {
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
    }

    public RealmList<UserItem> getUserItems() {
        return userItems;
    }

    public void setUserItems(RealmList<UserItem> userItems) {
        this.userItems = userItems;
    }

    public RealmList<BoxItem> getBoxItemsObjectFromUserItem() {
        RealmList<BoxItem> boxItems = new RealmList<>();
        for (UserItem userItem : userItems) {
            BoxItem boxItem = userItem.getBoxItem();
            boxItem.setUserItemId(userItem.getId());
            boxItem.setQuantity(userItem.getQuantity());
            boxItem.setSelectedItemConfig(boxItem.getItemConfigById(userItem.getSelectedConfigId()));
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
}
