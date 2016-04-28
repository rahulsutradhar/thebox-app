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
    private PriceAndSize selectedPriceAndSize;
    private String selectedFrequency;

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

    public PriceAndSize getSelectedPriceAndSize() {
        return selectedPriceAndSize;
    }

    public void setSelectedPriceAndSize(PriceAndSize selectedPriceAndSize) {
        this.selectedPriceAndSize = selectedPriceAndSize;
    }

    public String getSelectedFrequency() {
        return selectedFrequency;
    }

    public void setSelectedFrequency(String selectedFrequency) {
        this.selectedFrequency = selectedFrequency;
    }

    public List<ItemConfig> getItemConfigs() {
        return itemConfigs;
    }

    public void setItemConfigs(List<ItemConfig> itemConfigs) {
        this.itemConfigs = itemConfigs;
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
    }

    public HashMap<String, ArrayList<PriceAndSize>> getPriceAndSizeHashMap() {
        HashMap<String, ArrayList<PriceAndSize>> frequencyHashMap = new HashMap<>();
        for (int i = 0; i < itemConfigs.size(); i++) {
            if (frequencyHashMap.containsKey(itemConfigs.get(i).subscriptionType)) {
                ArrayList<PriceAndSize> priceAndSizes = frequencyHashMap.get(itemConfigs.get(i).subscriptionType);
                priceAndSizes.add(new PriceAndSize(itemConfigs.get(i).getPrice(), itemConfigs.get(i).getSize(), itemConfigs.get(i).getSizeUnit()));
                frequencyHashMap.put(itemConfigs.get(i).subscriptionType, priceAndSizes);
            } else {
                ArrayList<PriceAndSize> priceAndSizes = new ArrayList<>();
                priceAndSizes.add(new PriceAndSize(itemConfigs.get(i).getPrice(), itemConfigs.get(i).getSize(), itemConfigs.get(i).getSizeUnit()));
                frequencyHashMap.put(itemConfigs.get(i).subscriptionType, priceAndSizes);
            }
        }
        return frequencyHashMap;
    }

    public ArrayList<PriceSizeFrequency> getPriceSizeFrequencies(int size, String sizeUnit, HashMap<String, ArrayList<PriceAndSize>> hashMap) {
        ArrayList<PriceSizeFrequency> priceSizeFrequencies = new ArrayList<>();

        for (String key : hashMap.keySet()) {
            ArrayList<PriceAndSize> priceAndSizes = hashMap.get(key);
            String frequency = key;
            for (PriceAndSize priceAndSize : priceAndSizes) {
                if (priceAndSize.getSize() == size && priceAndSize.getSizeUnit().equals(sizeUnit)) {
                    priceSizeFrequencies.add(new PriceSizeFrequency(priceAndSize.getPrice(), priceAndSize.getSize(), priceAndSize.getSizeUnit(), frequency));
                }
            }
        }
        return priceSizeFrequencies;
    }

    public int getItemConfigId() {
        for (ItemConfig itemConfig : itemConfigs) {
            if (itemConfig.getSubscriptionType().equals(selectedFrequency)
                    && itemConfig.getSizeUnit().equals(selectedPriceAndSize.getSizeUnit())
                    && itemConfig.getSize() == selectedPriceAndSize.getSize()
                    && itemConfig.getPrice() == selectedPriceAndSize.getPrice()) {
                return itemConfig.getId();
            }
        }
        return 0;
    }

    public int getItemConfigId(String selectedFrequency, PriceAndSize selectedPriceAndSize) {
        for (ItemConfig itemConfig : itemConfigs) {
            if (itemConfig.getSubscriptionType().equals(selectedFrequency)
                    && itemConfig.getSizeUnit().equals(selectedPriceAndSize.getSizeUnit())
                    && itemConfig.getSize() == selectedPriceAndSize.getSize()
                    && itemConfig.getPrice() == selectedPriceAndSize.getPrice()) {
                return itemConfig.getId();
            }
        }
        return 0;
    }

    public static class PriceAndSize {
        private int price;
        private int size;
        private String sizeUnit;

        public PriceAndSize(int price, int size, String sizeUnit) {
            this.price = price;
            this.size = size;
            this.sizeUnit = sizeUnit;
        }

        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
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
    }

    public static class PriceSizeFrequency {
        private int price;
        private int size;
        private String sizeUnit;
        private String frequency;

        public PriceSizeFrequency(int price, int size, String sizeUnit, String frequency) {
            this.price = price;
            this.size = size;
            this.sizeUnit = sizeUnit;
            this.frequency = frequency;
        }


        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
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

        public String getFrequency() {
            return frequency;
        }

        public void setFrequency(String frequency) {
            this.frequency = frequency;
        }
    }

    public ItemConfig getItemConfigById(int id) {
        for (ItemConfig itemConfig : itemConfigs) {
            if (itemConfig.getId() == id) {
                return itemConfig;
            }
        }
        return null;
    }
}
