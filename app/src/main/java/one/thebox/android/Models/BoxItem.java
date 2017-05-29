package one.thebox.android.Models;

import android.util.Log;
import android.widget.Toast;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.TreeMap;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import one.thebox.android.app.TheBox;
import one.thebox.android.util.IntStringComparator;

/**
 * Created by Ajeet Kumar Meena on 22-04-2016.
 */
public class BoxItem extends RealmObject implements Serializable {

    @SerializedName("quantity")
    private int quantity;

    @Ignore
    private int userItemId;

    @SerializedName("savings_title")
    private String savingsTitle;

    @PrimaryKey
    @SerializedName("id")
    private int id;


    @SerializedName("brand")
    private String brand;

    @SerializedName("savings")
    private int savings;


    @SerializedName("no_of_sku")
    private int no_of_sku;

    @SerializedName("smart_item")
    private boolean isSmartItems;

    @SerializedName("category_id")
    private int categoryId;


    @SerializedName("photo_url")
    private String photoUrl;

    @Ignore
    private ItemConfig selectedItemConfig;
    private RealmList<Category> suggestedCategory = new RealmList<>();

    @Ignore
    private int horizontalOffsetOfRecyclerView;

    public BoxItem() {
    }

    public ItemConfig getSelectedItemConfig() {
        return this.selectedItemConfig;
    }

    public void setSelectedItemConfig(ItemConfig selectedItemConfig) {
        this.selectedItemConfig = selectedItemConfig;
    }

    public int getHorizontalOffsetOfRecyclerView() {
        return horizontalOffsetOfRecyclerView;
    }

    public void setHorizontalOffsetOfRecyclerView(int horizontalOffsetOfRecyclerView) {
        this.horizontalOffsetOfRecyclerView = horizontalOffsetOfRecyclerView;
    }

    public RealmList<Category> getSuggestedCategory() {
        return suggestedCategory;
    }

    public void setSuggestedCategory(RealmList<Category> suggestedCategory) {
        this.suggestedCategory = suggestedCategory;
    }

    public int getUserItemId() {
        return userItemId;
    }

    public void setUserItemId(int userItemId) {
        this.userItemId = userItemId;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public int getSavings() {
        return savings;
    }

    public void setSavings(int savings) {
        this.savings = savings;
    }

    public int getNo_of_sku() {
        return no_of_sku;
    }

    public void setNo_of_sku(int no_of_sku) {
        this.no_of_sku = no_of_sku;
    }

    public boolean isSmartItems() {
        return isSmartItems;
    }

    public void setSmartItems(boolean smartItems) {
        isSmartItems = smartItems;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getSavingsTitle() {
        return savingsTitle;
    }

    public void setSavingsTitle(String savingsTitle) {
        this.savingsTitle = savingsTitle;
    }

    public ItemConfig getItemConfigById(int id) {
        for (ItemConfig itemConfig : itemConfigs) {
            if (itemConfig.getId() == id) {
                return itemConfig;
            }
        }
        return null;
    }

    public TreeMap<IntStringObject, RealmList<ItemConfig>> getFrequencyItemConfigHashMap() {
        TreeMap<IntStringObject, RealmList<ItemConfig>> frequencyItemConfigHashMap = new TreeMap<>(new IntStringComparator());
        for (int i = 0; i < itemConfigs.size(); i++) {
            String subscriptionText = itemConfigs.get(i).getSubscriptionText();
            int subscriptionTypeUnit = itemConfigs.get(i).getSubscriptionType();
            IntStringObject key = new IntStringObject(subscriptionTypeUnit, subscriptionText);

            if (frequencyItemConfigHashMap.get(key) == null || frequencyItemConfigHashMap.get(key).isEmpty()) {
                RealmList<ItemConfig> tempItemConfigs = new RealmList<>();
                tempItemConfigs.add(itemConfigs.get(i));
                frequencyItemConfigHashMap.put(key, tempItemConfigs);
            } else {
                RealmList<ItemConfig> tempItemConfig = frequencyItemConfigHashMap.get(key);
                tempItemConfig.add(itemConfigs.get(i));
                frequencyItemConfigHashMap.put(key, tempItemConfig);
            }
        }
        return frequencyItemConfigHashMap;
    }

    public RealmList<ItemConfig> getItemConfigsBySelectedItemConfig() {
        if (selectedItemConfig == null) {
            selectedItemConfig = itemConfigs.first();
        }
        RealmList<ItemConfig> tempItemConfigs = new RealmList<>();
        for (int i = 0; i < itemConfigs.size(); i++) {
            if (itemConfigs.get(i).getSize() == selectedItemConfig.getSize()
                    && itemConfigs.get(i).getSizeUnit().equals(selectedItemConfig.getSizeUnit())
                    && itemConfigs.get(i).getItemType().equals(selectedItemConfig.getItemType())
                    && itemConfigs.get(i).getCorrectQuantity().equals(selectedItemConfig.getCorrectQuantity())) {
                tempItemConfigs.add(itemConfigs.get(i));
            }
        }
        return tempItemConfigs;
    }

    private void checkAndPrintIfNull(ItemConfig itemConfig) {
        if (itemConfig == null) {
            Toast.makeText(TheBox.getInstance(), "Item config is null for id " + this.getId(), Toast.LENGTH_SHORT).show();
        } else {
            if (itemConfig.getSizeUnit() == null ||
                    itemConfig.getItemType() == null) {
                Toast.makeText(TheBox.getInstance(), "Item fields are null for " + this.getId(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    public int getSelectedConfigPosition() {
        for (int i = 0; i < itemConfigs.size(); i++) {
            if (getSelectedItemConfig() == itemConfigs.get(i)) {
                return i;
            }
        }
        return 0;
    }

    public ItemConfig getSmallestInStockItemConfig() {
        ItemConfig smallestItemConfig = null;
        boolean isInStockExist = false;
        try {

            for (ItemConfig itemConfig : itemConfigs) {
               /* if (itemConfig.is_in_stock()) {
                    smallestItemConfig = itemConfig;
                    isInStockExist = true;
                    break;
                }*/
            }

            if (isInStockExist == false) {
                smallestItemConfig = itemConfigs.first();
            }

            for (int i = 0; i < itemConfigs.size(); i++) {
                if (itemConfigs.get(i).getSubscriptionType() < smallestItemConfig.getSubscriptionType()) {
                    //check if item config is in stock
                    /*if (itemConfigs.get(i).is_in_stock()) {
                        smallestItemConfig = itemConfigs.get(i);
                    }*/
                }
            }
        } catch (NullPointerException n) {
            n.printStackTrace();
        }
        return smallestItemConfig;
    }


    /**
     * Refactor
     */

    private String uuid;

    @SerializedName("title")
    private String title;

    @SerializedName("itemconfigs")
    RealmList<ItemConfig> itemConfigs;

    @SerializedName("in_stock")
    private boolean inStock;


    /************************************************
     * Getter Setter
     ************************************************/

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public RealmList<ItemConfig> getItemConfigs() {
        return itemConfigs;
    }

    public void setItemConfigs(RealmList<ItemConfig> itemConfigs) {
        this.itemConfigs = itemConfigs;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }
}
