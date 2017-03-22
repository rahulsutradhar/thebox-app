package one.thebox.android.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ItemConfig extends RealmObject implements Serializable {
    @PrimaryKey
    @SerializedName("id")
    private int id;

    @SerializedName("size_or_quantity")
    private int size;

    @SerializedName("size_unit")
    private String sizeUnit;

    @SerializedName("item_type")
    private String itemType;

    @SerializedName("price")
    private int price;

    @SerializedName("subscription_type")
    private String subscriptionType;

    @SerializedName("subscription_text")
    private String subscriptionText;

    @SerializedName("photo_url")
    private String photoUrl;

    @SerializedName("subscriptiontype")
    private int subscriptionTypeUnit;

    @SerializedName("correct_quantity")
    private String correctQuantity;

    @SerializedName("in_stock")
    private boolean in_stock;

    @SerializedName("savings_title")
    private String savingsTitle;

    public ItemConfig() {
    }

    public int getSubscriptionTypeUnit() {
        return subscriptionTypeUnit;
    }

    public void setSubscriptionTypeUnit(int subscriptionTypeUnit) {
        this.subscriptionTypeUnit = subscriptionTypeUnit;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getSizeUnit() {
        return sizeUnit;
    }

    public void setSizeUnit(String sizeUnit) {
        this.sizeUnit = sizeUnit;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public boolean is_in_stock() {
        return in_stock;
    }

    public void set_in_stock(boolean it_is_in_stock) {
        in_stock = it_is_in_stock;
    }

    public void setSubscriptionType(String subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    @Override
    public boolean equals(Object object) {
        ItemConfig itemConfig = (ItemConfig) object;
        return this.id == itemConfig.getId();
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getCorrectQuantity() {
        return correctQuantity;
    }

    public void setCorrectQuantity(String correctQuantity) {
        this.correctQuantity = correctQuantity;
    }

    public String getSubscriptionText() {
        return subscriptionText;
    }

    public void setSubscriptionText(String subscriptionText) {
        this.subscriptionText = subscriptionText;
    }

    public boolean isIn_stock() {
        return in_stock;
    }

    public void setIn_stock(boolean in_stock) {
        this.in_stock = in_stock;
    }

    public String getSavingsTitle() {
        return savingsTitle;
    }

    public void setSavingsTitle(String savingsTitle) {
        this.savingsTitle = savingsTitle;
    }
}