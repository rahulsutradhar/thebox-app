package one.thebox.android.Models.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import one.thebox.android.Models.BaseAdapterModel;
import one.thebox.android.app.AppConstants;

/**
 * Created by nbansal2211 on 17/12/16.
 */
public class User_item extends BaseAdapterModel implements Serializable {
    @Ignore
    public static final String FIELD_ID = "id";

    @Override
    public int getViewType() {
        return AppConstants.ViewType.USER_MY_BOX_ITEM;
    }

    @PrimaryKey
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("quantity")
    @Expose
    private Integer quantity;
    @SerializedName("photo_url")
    @Expose
    private String photo_url;
    @SerializedName("next_delivery_scheduled_at")
    @Expose
    private String next_delivery_scheduled_at;
    @SerializedName("delivery_status")
    @Expose
    private String delivery_status;
    @SerializedName("item")
    @Expose
    private Item item;
    @SerializedName("item_config")
    @Expose
    private Item_config item_config;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public String getNext_delivery_scheduled_at() {
        return next_delivery_scheduled_at;
    }

    public void setNext_delivery_scheduled_at(String next_delivery_scheduled_at) {
        this.next_delivery_scheduled_at = next_delivery_scheduled_at;
    }

    public String getDelivery_status() {
        return delivery_status;
    }

    public void setDelivery_status(String delivery_status) {
        this.delivery_status = delivery_status;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Item_config getItem_config() {
        return item_config;
    }

    public void setItem_config(Item_config item_config) {
        this.item_config = item_config;
    }

}
