package one.thebox.android.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import one.thebox.android.Models.items.BoxItem;

public class UserItem extends RealmObject implements Serializable {
    @Ignore
    public static final String FIELD_ID = "id";

    @PrimaryKey
    @SerializedName("id")
    private int id;

    @SerializedName("usercategory_id")
    private int userCategoryId;

    @SerializedName("item_id")
    private int selectedItemId;

    @SerializedName("user_id")
    private int userId;


    @SerializedName("next_delivery_scheduled_at")
    private String nextDeliveryScheduledAt;

    @SerializedName("still_subscribed")
    private boolean stillSubscribed;

    @SerializedName("itemconfig_id")
    private int selectedConfigId;


    @SerializedName("box_id")
    private int boxId;

    @SerializedName("cart_id")
    private int cartId;


    @SerializedName("savings_title")
    private String selectedItemConfigSavingsTitle;

    @Ignore
    private int orderItemQty;

    @Ignore
    private int orderId;

    public int getBoxId() {
        return boxId;
    }

    public void setBoxId(int boxId) {
        this.boxId = boxId;
    }

    public int getOrderItemQty() {
        return orderItemQty;
    }

    public void setOrderItemQty(int orderItemQty) {
        this.orderItemQty = orderItemQty;
    }

    public UserItem() {
    }

    public UserItem(int id, int userCategoryId, int selectedItemId,
                    int userId, int quantity, String nextDeliveryScheduledAt,
                    boolean stillSubscribed, int selectedConfigId, BoxItem boxItem, String arrivingAt, String selectedItemConfigSavingsTitle) {
        this.id = id;
        this.userCategoryId = userCategoryId;
        this.selectedItemId = selectedItemId;
        this.userId = userId;
        this.quantity = quantity;
        this.nextDeliveryScheduledAt = nextDeliveryScheduledAt;
        this.stillSubscribed = stillSubscribed;
        this.selectedConfigId = selectedConfigId;
        this.boxItem = boxItem;
        this.arrivingAt = arrivingAt;
        this.selectedItemConfigSavingsTitle = selectedItemConfigSavingsTitle;
    }

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserCategoryId() {
        return userCategoryId;
    }

    public void setUserCategoryId(int userCategoryId) {
        this.userCategoryId = userCategoryId;
    }

    public int getSelectedItemId() {
        return selectedItemId;
    }

    public void setSelectedItemId(int selectedItemId) {
        this.selectedItemId = selectedItemId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }


    public String getNextDeliveryScheduledAt() {
        return nextDeliveryScheduledAt;
    }

    public void setNextDeliveryScheduledAt(String nextDeliveryScheduledAt) {
        this.nextDeliveryScheduledAt = nextDeliveryScheduledAt;
    }

    public boolean getStillSubscribed() {
        return stillSubscribed;
    }

    public void setStillSubscribed(boolean stillSubscribed) {
        this.stillSubscribed = stillSubscribed;
    }

    public int getSelectedConfigId() {
        return selectedConfigId;
    }

    public void setSelectedConfigId(int selectedConfigId) {
        this.selectedConfigId = selectedConfigId;
    }


    public String getSelectedItemConfigSavingsTitle() {
        return selectedItemConfigSavingsTitle;
    }

    public void setSelectedItemConfigSavingsTitle(String selectedItemConfigSavingsTitle) {
        this.selectedItemConfigSavingsTitle = selectedItemConfigSavingsTitle;
    }

    public BoxItem getFakeBoxItemObject() {
        BoxItem boxItem = this.getBoxItem();
        boxItem.setUserItemId(this.getId());
        boxItem.setQuantity(this.getQuantity());
        boxItem.setSelectedItemConfig(boxItem.getItemConfigById(this.getSelectedConfigId()));
        return boxItem;
    }

    public int getTotalPriceForAnOrder() {
        ItemConfig selectedItemConfig = getBoxItem().getItemConfigById(selectedConfigId);
        return selectedItemConfig.getPrice() * getOrderItemQty();
    }

    public int getTotalPrice() {
        ItemConfig selectedItemConfig = getBoxItem().getItemConfigById(selectedConfigId);
        return selectedItemConfig.getPrice() * getQuantity();
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }


    /**
     * Refactor
     */
    private String uuid;

    private int quantity;

    @SerializedName("subscribed_saving_text")
    private String subscribedSavingText;

    @SerializedName("arriving_at")
    private String arrivingAt;

    @SerializedName("itemconfig")
    private ItemConfig selectedItemConfig;

    @SerializedName("item")
    private BoxItem boxItem;


    /************************************
     * Getter Setter
     ************************************/
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getArrivingAt() {
        return arrivingAt;
    }

    public void setArrivingAt(String arrivingAt) {
        this.arrivingAt = arrivingAt;
    }

    public boolean isStillSubscribed() {
        return stillSubscribed;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getSubscribedSavingText() {
        return subscribedSavingText;
    }

    public void setSubscribedSavingText(String subscribedSavingText) {
        this.subscribedSavingText = subscribedSavingText;
    }

    public ItemConfig getSelectedItemConfig() {
        return selectedItemConfig;
    }

    public void setSelectedItemConfig(ItemConfig selectedItemConfig) {
        this.selectedItemConfig = selectedItemConfig;
    }

    public BoxItem getBoxItem() {
        return boxItem;
    }

    public void setBoxItem(BoxItem boxItem) {
        this.boxItem = boxItem;
    }
}
