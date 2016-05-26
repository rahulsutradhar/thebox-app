package one.thebox.android.Models;

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
import one.thebox.android.util.IntStringComparator;

/**
 * Created by Ajeet Kumar Meena on 22-04-2016.
 */
public class BoxItem extends RealmObject implements Serializable {

    @Ignore
    private int quantity;
    @Ignore
    private int userItemId;
    @SerializedName("id")
    private int id;
    @SerializedName("title")
    private String title;
    @SerializedName("brand")
    private String brand;
    @SerializedName("savings")
    private int savings;
    @SerializedName("smart_item")
    private boolean isSmartItems;
    @SerializedName("category_id")
    private int categoryId;
    @SerializedName("photo_file_name")
    private String photoFileName;
    @SerializedName("photo_content_type")
    private String photoContentType;
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
        return selectedItemConfig;
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

    public String getPhotoFileName() {
        return photoFileName;
    }

    public void setPhotoFileName(String photoFileName) {
        this.photoFileName = photoFileName;
    }

    public String getPhotoContentType() {
        return photoContentType;
    }

    public void setPhotoContentType(String photoContentType) {
        this.photoContentType = photoContentType;
    }

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
        HashMap<IntStringObject, RealmList<ItemConfig>> frequencyItemConfigHashMap = new HashMap<>();
        for (int i = 0; i < itemConfigs.size(); i++) {
            String subscriptionType = itemConfigs.get(i).getSubscriptionType();
            int subscriptionTypeUnit = itemConfigs.get(i).getSubscriptionTypeUnit();
            IntStringObject key = new IntStringObject(subscriptionTypeUnit,subscriptionType);
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
        TreeMap<IntStringObject, RealmList<ItemConfig>> sorted = new TreeMap<>(new IntStringComparator());
        sorted.putAll(frequencyItemConfigHashMap);
        return sorted;
    }

    public RealmList<ItemConfig> getItemConfigsBySelectedItemConfig() {
        if (selectedItemConfig == null) {
            selectedItemConfig = itemConfigs.get(0);
        }
        RealmList<ItemConfig> tempItemConfigs = new RealmList<>();
        for (int i = 0; i < itemConfigs.size(); i++) {
            if (itemConfigs.get(i).getSize() == selectedItemConfig.getSize() && itemConfigs.get(i).getSizeUnit().equals(selectedItemConfig.getSizeUnit())) {
                tempItemConfigs.add(itemConfigs.get(i));
            }
        }
        return tempItemConfigs;
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
}
