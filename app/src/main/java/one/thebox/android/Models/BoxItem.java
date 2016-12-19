package one.thebox.android.Models;

import android.widget.Toast;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import one.thebox.android.R;
import one.thebox.android.app.MyApplication;
import one.thebox.android.util.IntStringComparator;

/**
 * Created by Ajeet Kumar Meena on 22-04-2016.
 */
public class BoxItem extends RealmObject implements Serializable {

    @SerializedName("quantity")
    private int quantity;
    @Ignore
    private int userItemId;
    @PrimaryKey
    @SerializedName("id")
    private int id;
    @SerializedName("title")
    private String title;
    @SerializedName("brand")
    private String brand;
    @SerializedName("savings")
    private int savings;
    @SerializedName("in_stock")
    private boolean in_stock;
    @SerializedName("no_of_sku")
    private int no_of_sku;
    @SerializedName("smart_item")
    private boolean isSmartItems;
    @SerializedName("category_id")
    private int categoryId;
//    @SerializedName("photo_file_name")
//    private String photoFileName;
//    @SerializedName("photo_content_type")
//    private String photoContentType;
    @SerializedName("itemconfigs")
    private RealmList<ItemConfig> itemConfigs;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public boolean is_in_stock() {
        return in_stock;
    }

    public void set_in_stock(boolean it_is_in_stock) {
        in_stock = it_is_in_stock;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

//    public String getPhotoFileName() {
////        return photoFileName;
////    }
////
////    public void setPhotoFileName(String photoFileName) {
////        this.photoFileName = photoFileName;
////    }

//    public String getPhotoContentType() {
//        return photoContentType;
//    }
//
//    public void setPhotoContentType(String photoContentType) {
//        this.photoContentType = photoContentType;
//    }

    public RealmList<ItemConfig> getItemConfigs() {
        return itemConfigs;
    }

    public void setItemConfigs(RealmList<ItemConfig> itemConfigs) {
        this.itemConfigs = itemConfigs;
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
            String subscriptionType = itemConfigs.get(i).getSubscriptionType();
            int subscriptionTypeUnit = itemConfigs.get(i).getSubscriptionTypeUnit();
            IntStringObject key = new IntStringObject(subscriptionTypeUnit, subscriptionType);
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
            selectedItemConfig = itemConfigs.get(0);
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
            Toast.makeText(MyApplication.getInstance(), "Item config is null for id " + this.getId(), Toast.LENGTH_SHORT).show();
        } else {
            if (itemConfig.getSizeUnit() == null ||
                    itemConfig.getItemType() == null) {
                Toast.makeText(MyApplication.getInstance(), "Item fields are null for " + this.getId(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public ItemConfig getItemConfigByFrequencyAndItemConfig(ItemConfig itemConfig, String frequency) {
        for (ItemConfig tempItemConfig : itemConfigs) {
            if (tempItemConfig.getSubscriptionType().equals(frequency)
                    && itemConfig.getSize() == tempItemConfig.getSize()
                    && itemConfig.getSizeUnit().equals(tempItemConfig.getSizeUnit())
                    && itemConfig.getPrice() == itemConfig.getPrice()) {
                return tempItemConfig;
            }
        }
        return null;
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

        ItemConfig smallestItemConfig = itemConfigs.get(0);


        for (int i = 0; i < itemConfigs.size(); i++) {
            if (itemConfigs.get(i).getSubscriptionTypeUnit() < smallestItemConfig.getSubscriptionTypeUnit()) {
                smallestItemConfig = itemConfigs.get(i);
            }
        }
        return smallestItemConfig;
    }

    @Override
    public boolean equals(Object o) {
        BoxItem boxItem = (BoxItem) o;
        return boxItem != null
                && userItemId == boxItem.getUserItemId()
                && id == boxItem.getId()
                && title.equals(boxItem.getTitle())
                && brand.equals(boxItem.getBrand())
                && savings == boxItem.getSavings()
                && isSmartItems == boxItem.isSmartItems()
                && categoryId == boxItem.getCategoryId()
                && itemConfigs.equals(boxItem.getItemConfigs());
    }
}
