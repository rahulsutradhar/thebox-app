package one.thebox.android.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Ajeet Kumar Meena on 22-04-2016.
 */
public class BoxItem implements Serializable {
    private int size;
    private int frequency;
    private int quantity;
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
    private List<ItemConfig> itemConfigs;
    @SerializedName("photo_url")
    private String photoUrl;
    private ItemConfig selectedItemConfig;
    private ArrayList<Category> suggestedCategory = new ArrayList<>();
    private int horizontalOffsetOfRecyclerView;

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

    public ArrayList<Category> getSuggestedCategory() {
        return suggestedCategory;
    }

    public void setSuggestedCategory(ArrayList<Category> suggestedCategory) {
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

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
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

    public List<ItemConfig> getItemConfigs() {
        return itemConfigs;
    }

    public void setItemConfigs(List<ItemConfig> itemConfigs) {
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

    public HashMap<String, ArrayList<ItemConfig>> getFrequencyItemConfigHashMap() {
        HashMap<String, ArrayList<ItemConfig>> frequencyItemConfigHashMap = new HashMap<>();
        for (int i = 0; i < itemConfigs.size(); i++) {
            String key = itemConfigs.get(i).getSubscriptionType();
            if (frequencyItemConfigHashMap.get(key) == null || frequencyItemConfigHashMap.get(key).isEmpty()) {
                ArrayList<ItemConfig> tempItemConfigs = new ArrayList<>();
                tempItemConfigs.add(itemConfigs.get(i));
                frequencyItemConfigHashMap.put(key, tempItemConfigs);
            } else {
                ArrayList<ItemConfig> tempItemConfig = frequencyItemConfigHashMap.get(key);
                tempItemConfig.add(itemConfigs.get(i));
                frequencyItemConfigHashMap.put(key, tempItemConfig);
            }
        }
        return frequencyItemConfigHashMap;
    }

    public ArrayList<ItemConfig> getItemConfigsBySelectedItemConfig() {
        if (selectedItemConfig == null) {
            selectedItemConfig = itemConfigs.get(0);
        }
        ArrayList<ItemConfig> tempItemConfigs = new ArrayList<>();
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

    public class ItemConfig {
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
    }
}
