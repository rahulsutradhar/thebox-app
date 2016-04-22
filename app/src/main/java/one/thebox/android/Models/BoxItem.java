package one.thebox.android.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Ajeet Kumar Meena on 22-04-2016.
 */
public class BoxItem implements Serializable {
    private int size;
    private int frequency;
    private int noOfItemsSelected;
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


    public int getNoOfItemsSelected() {
        return noOfItemsSelected;
    }

    public void setNoOfItemsSelected(int noOfItemsSelected) {
        this.noOfItemsSelected = noOfItemsSelected;
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

    class ItemConfig {
        @SerializedName("size")
        private int size;
        @SerializedName("size_unit")
        private String sizeUnit;
        @SerializedName("itemtype")
        private int itemType;
        @SerializedName("price")
        private String price;
        @SerializedName("subscription_type")
        private String subscriptionType;
    }
}
