package one.thebox.android.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ItemConfig extends RealmObject implements Serializable {
    @PrimaryKey
    @SerializedName("id")
    private int id;
    @SerializedName("size")
    private int size;
    @SerializedName("size_unit")
    private String sizeUnit;
    @SerializedName("itemtype")
    private int itemType;
    @SerializedName("price")
    private int price;
    @SerializedName("subscription_type")
    private String subscriptionType;
    @SerializedName("photo_url")
    private String photoUrl;

    public ItemConfig() {
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

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
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
}