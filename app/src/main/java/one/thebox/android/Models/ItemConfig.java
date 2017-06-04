package one.thebox.android.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ItemConfig extends RealmObject implements Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("correct_quantity")
    private String correctQuantity;


    @SerializedName("savings_title")
    private String savingsTitle;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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


    public String getSavingsTitle() {
        return savingsTitle;
    }

    public void setSavingsTitle(String savingsTitle) {
        this.savingsTitle = savingsTitle;
    }


    public ItemConfig() {
    }

    /**
     * Refactor
     */
    @PrimaryKey
    private String uuid;

    @SerializedName("photo_url")
    private String itemImage;

    private int quantity;

    private int size;

    @SerializedName("size_unit")
    private String sizeUnit;

    @SerializedName("subscriptiontype")
    private int subscriptionType;

    @SerializedName("subscription_text")
    private String subscriptionText;

    @SerializedName("item_type")
    private String itemType;

    @SerializedName("price")
    private int price;

    private int priority;

    private int savings;

    @SerializedName("savings_text")
    private String savingsText;

    @SerializedName("mrp_text")
    private String mrpText;

    @SerializedName("monthly_savings_text")
    private String monthlySavingsText;

    @SerializedName("in_stock")
    private boolean inStock;


    /************************************************
     * Getter Setter
     ************************************************/


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

    public String getSavingsText() {
        return savingsText;
    }

    public void setSavingsText(String savingsText) {
        this.savingsText = savingsText;
    }

    public String getMrpText() {
        return mrpText;
    }

    public void setMrpText(String mrpText) {
        this.mrpText = mrpText;
    }

    public String getMonthlySavingsText() {
        return monthlySavingsText;
    }

    public void setMonthlySavingsText(String monthlySavingsText) {
        this.monthlySavingsText = monthlySavingsText;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getItemImage() {
        return itemImage;
    }

    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(int subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getSavings() {
        return savings;
    }

    public void setSavings(int savings) {
        this.savings = savings;
    }

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }
}