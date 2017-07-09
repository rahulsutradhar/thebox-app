package one.thebox.android.Models.items;

import android.widget.Toast;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.TreeMap;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import one.thebox.android.Models.IntStringObject;
import one.thebox.android.app.TheBox;
import one.thebox.android.util.IntStringComparator;

/**
 * Created by Ajeet Kumar Meena on 22-04-2016.
 */
public class BoxItem extends RealmObject implements Serializable {

    @PrimaryKey
    private String uuid;

    @SerializedName("title")
    private String title;

    @SerializedName("brand")
    private String brand;

    @SerializedName("itemconfigs")
    RealmList<ItemConfig> itemConfigs;

    @SerializedName("in_stock")
    private boolean inStock;

    @SerializedName("no_of_sku")
    private int noOfSku;

    @SerializedName("no_of_options")
    private String noOfOptions;

    private int quantity;

    private ItemConfig selectedItemConfig;

    /**
     * Used to display Product
     */
    @SerializedName("my_item")
    private UserItem userItem;

    @Ignore
    private boolean showCategorySuggestion = false;

    /**
     * Used to determine the view of the card
     */
    @Ignore
    private int itemViewType;

    /************************************************
     * Helper Methods
     ************************************************/

    /**
     * Find the Smallest ItemConfig which is in stock
     */
    public ItemConfig getSmallestInStockItemConfig() {
        ItemConfig smallestItemConfig = null;
        boolean isInStockExist = false;

        try {
            for (ItemConfig itemConfig : itemConfigs) {
                if (itemConfig.isInStock()) {
                    smallestItemConfig = itemConfig;
                    isInStockExist = true;
                    break;
                }
            }

            if (isInStockExist == false) {
                smallestItemConfig = itemConfigs.first();
            }

            for (int i = 0; i < itemConfigs.size(); i++) {
                if (itemConfigs.get(i).getSubscriptionType() < smallestItemConfig.getSubscriptionType()) {
                    //check if item config is in stock
                    if (itemConfigs.get(i).isInStock()) {
                        smallestItemConfig = itemConfigs.get(i);
                    }
                }
            }
        } catch (NullPointerException n) {
            n.printStackTrace();
        }
        return smallestItemConfig;
    }

    /**
     * Select all item config which matches the initial selected Item Config
     */
    public RealmList<ItemConfig> getItemConfigsBySelectedItemConfig() {
        if (selectedItemConfig == null) {
            selectedItemConfig = itemConfigs.first();
        }
        RealmList<ItemConfig> tempItemConfigs = new RealmList<>();
        for (int i = 0; i < itemConfigs.size(); i++) {
            if (itemConfigs.get(i).getSize() == selectedItemConfig.getSize()
                    && itemConfigs.get(i).getSizeUnit().equalsIgnoreCase(selectedItemConfig.getSizeUnit())
                    && itemConfigs.get(i).getItemType().equalsIgnoreCase(selectedItemConfig.getItemType())
                    && itemConfigs.get(i).getQuantity() == selectedItemConfig.getQuantity()) {
                tempItemConfigs.add(itemConfigs.get(i));
            }
        }
        return tempItemConfigs;
    }

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

    public ItemConfig getSelectedItemConfig() {
        return this.selectedItemConfig;
    }

    public void setSelectedItemConfig(ItemConfig selectedItemConfig) {
        this.selectedItemConfig = selectedItemConfig;
    }

    public int getNoOfSku() {
        return noOfSku;
    }

    public void setNoOfSku(int noOfSku) {
        this.noOfSku = noOfSku;
    }

    public String getNoOfOptions() {
        return noOfOptions;
    }

    public void setNoOfOptions(String noOfOptions) {
        this.noOfOptions = noOfOptions;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isShowCategorySuggestion() {
        return showCategorySuggestion;
    }

    public void setShowCategorySuggestion(boolean showCategorySuggestion) {
        this.showCategorySuggestion = showCategorySuggestion;
    }

    public UserItem getUserItem() {
        return userItem;
    }

    public void setUserItem(UserItem userItem) {
        this.userItem = userItem;
    }

    public int getItemViewType() {
        return itemViewType;
    }

    public void setItemViewType(int itemViewType) {
        this.itemViewType = itemViewType;
    }
}
