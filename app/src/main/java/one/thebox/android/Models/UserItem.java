package one.thebox.android.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class UserItem extends RealmObject implements Serializable {
    @Ignore
    public static final String FIELD_ID = "id";

    @PrimaryKey
    @SerializedName("id")
    private int id;
    @SerializedName("usercategory_id")
    private int userCategoryId;
    @SerializedName("item_id")
    private int itemId;
    @SerializedName("user_id")
    private int userId;
    @SerializedName("quantity")
    private int quantity;
    @SerializedName("next_delivery_scheduled_at")
    private String nextDeliveryScheduledAt;
    @SerializedName("still_subscribed")
    private String stillSubscribed;
    @SerializedName("itemconfig_id")
    private int selectedConfigId;
    @SerializedName("item")
    private BoxItem boxItem;
    @SerializedName("cart_id")
    private int cartId;

    public UserItem() {
    }

    public UserItem(int id, int userCategoryId, int itemId, int userId, int quantity, String nextDeliveryScheduledAt, String stillSubscribed, int selectedConfigId, BoxItem boxItem) {
        this.id = id;
        this.userCategoryId = userCategoryId;
        this.itemId = itemId;
        this.userId = userId;
        this.quantity = quantity;
        this.nextDeliveryScheduledAt = nextDeliveryScheduledAt;
        this.stillSubscribed = stillSubscribed;
        this.selectedConfigId = selectedConfigId;
        this.boxItem = boxItem;
    }

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    @Override
    public boolean equals(Object o) {
        UserItem userItem = (UserItem) o;
        return userItem != null && this.id == userItem.getId()
                && userCategoryId == userItem.getUserCategoryId()
                && itemId == userItem.getItemId() && userId == userItem.getUserId()
                && quantity == userItem.getQuantity()
                && nextDeliveryScheduledAt.equals(userItem.getNextDeliveryScheduledAt())
                && stillSubscribed.equals(userItem.getStillSubscribed())
                && selectedConfigId == userItem.getSelectedConfigId()
                && boxItem.equals(userItem.getBoxItem())
                && cartId == userItem.getCartId();


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

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getNextDeliveryScheduledAt() {
        return nextDeliveryScheduledAt;
    }

    public void setNextDeliveryScheduledAt(String nextDeliveryScheduledAt) {
        this.nextDeliveryScheduledAt = nextDeliveryScheduledAt;
    }

    public String getStillSubscribed() {
        return stillSubscribed;
    }

    public void setStillSubscribed(String stillSubscribed) {
        this.stillSubscribed = stillSubscribed;
    }

    public int getSelectedConfigId() {
        return selectedConfigId;
    }

    public void setSelectedConfigId(int selectedConfigId) {
        this.selectedConfigId = selectedConfigId;
    }

    public BoxItem getBoxItem() {
        return boxItem;
    }

    public void setBoxItem(BoxItem boxItem) {
        this.boxItem = boxItem;
    }

    public BoxItem getFakeBoxItemObject() {
        BoxItem boxItem = this.getBoxItem();
        boxItem.setUserItemId(this.getId());
        boxItem.setQuantity(this.getQuantity());
        boxItem.setSelectedItemConfig(boxItem.getItemConfigById(this.getSelectedConfigId()));
        return boxItem;
    }
}
