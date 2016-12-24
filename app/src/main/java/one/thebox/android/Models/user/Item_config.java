package one.thebox.android.Models.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by nbansal2211 on 17/12/16.
 */
public class Item_config {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("size")
    @Expose
    private Integer size;
    @SerializedName("sizeunit")
    @Expose
    private Integer sizeunit;
    @SerializedName("itemtype")
    @Expose
    private Integer itemtype;
    @SerializedName("subscriptiontype")
    @Expose
    private Integer subscriptiontype;
    @SerializedName("price")
    @Expose
    private Integer price;
    @SerializedName("item_id")
    @Expose
    private Integer item_id;
    @SerializedName("created_at")
    @Expose
    private String created_at;
    @SerializedName("updated_at")
    @Expose
    private String updated_at;
    @SerializedName("mrp")
    @Expose
    private Integer mrp;
    @SerializedName("photo_file_name")
    @Expose
    private String photo_file_name;
    @SerializedName("photo_content_type")
    @Expose
    private String photo_content_type;
    @SerializedName("photo_file_size")
    @Expose
    private Integer photo_file_size;
    @SerializedName("photo_updated_at")
    @Expose
    private String photo_updated_at;
    @SerializedName("quantity")
    @Expose
    private Integer quantity;
    @SerializedName("bb_link")
    @Expose
    private String bb_link;
    @SerializedName("zn_link")
    @Expose
    private String zn_link;
    @SerializedName("priority")
    @Expose
    private Integer priority;
    @SerializedName("in_stock")
    @Expose
    private Boolean in_stock;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getSizeunit() {
        return sizeunit;
    }

    public void setSizeunit(Integer sizeunit) {
        this.sizeunit = sizeunit;
    }

    public Integer getItemtype() {
        return itemtype;
    }

    public void setItemtype(Integer itemtype) {
        this.itemtype = itemtype;
    }

    public Integer getSubscriptiontype() {
        return subscriptiontype;
    }

    public void setSubscriptiontype(Integer subscriptiontype) {
        this.subscriptiontype = subscriptiontype;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getItem_id() {
        return item_id;
    }

    public void setItem_id(Integer item_id) {
        this.item_id = item_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public Integer getMrp() {
        return mrp;
    }

    public void setMrp(Integer mrp) {
        this.mrp = mrp;
    }

    public String getPhoto_file_name() {
        return photo_file_name;
    }

    public void setPhoto_file_name(String photo_file_name) {
        this.photo_file_name = photo_file_name;
    }

    public String getPhoto_content_type() {
        return photo_content_type;
    }

    public void setPhoto_content_type(String photo_content_type) {
        this.photo_content_type = photo_content_type;
    }

    public Integer getPhoto_file_size() {
        return photo_file_size;
    }

    public void setPhoto_file_size(Integer photo_file_size) {
        this.photo_file_size = photo_file_size;
    }

    public String getPhoto_updated_at() {
        return photo_updated_at;
    }

    public void setPhoto_updated_at(String photo_updated_at) {
        this.photo_updated_at = photo_updated_at;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getBb_link() {
        return bb_link;
    }

    public void setBb_link(String bb_link) {
        this.bb_link = bb_link;
    }

    public String getZn_link() {
        return zn_link;
    }

    public void setZn_link(String zn_link) {
        this.zn_link = zn_link;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Boolean getIn_stock() {
        return in_stock;
    }

    public void setIn_stock(Boolean in_stock) {
        this.in_stock = in_stock;
    }

}
