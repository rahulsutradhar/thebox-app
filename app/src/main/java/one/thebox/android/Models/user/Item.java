package one.thebox.android.Models.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by nbansal2211 on 17/12/16.
 */
public class Item {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("brand")
    @Expose
    private String brand;
    @SerializedName("savings")
    @Expose
    private Integer savings;
    @SerializedName("smart_item")
    @Expose
    private Boolean smart_item;
    @SerializedName("category_id")
    @Expose
    private Integer category_id;
    @SerializedName("created_at")
    @Expose
    private String created_at;
    @SerializedName("updated_at")
    @Expose
    private String updated_at;
    @SerializedName("photo_file_name")
    @Expose
    private Object photo_file_name;
    @SerializedName("photo_content_type")
    @Expose
    private Object photo_content_type;
    @SerializedName("photo_file_size")
    @Expose
    private Object photo_file_size;
    @SerializedName("photo_updated_at")
    @Expose
    private Object photo_updated_at;
    @SerializedName("merchant_id")
    @Expose
    private Integer merchant_id;
    @SerializedName("internal_id")
    @Expose
    private String internal_id;
    @SerializedName("priority")
    @Expose
    private Integer priority;
    @SerializedName("no_of_sku")
    @Expose
    private Integer no_of_sku;
    @SerializedName("in_stock")
    @Expose
    private Boolean in_stock;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public Integer getSavings() {
        return savings;
    }

    public void setSavings(Integer savings) {
        this.savings = savings;
    }

    public Boolean getSmart_item() {
        return smart_item;
    }

    public void setSmart_item(Boolean smart_item) {
        this.smart_item = smart_item;
    }

    public Integer getCategory_id() {
        return category_id;
    }

    public void setCategory_id(Integer category_id) {
        this.category_id = category_id;
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

    public Object getPhoto_file_name() {
        return photo_file_name;
    }

    public void setPhoto_file_name(Object photo_file_name) {
        this.photo_file_name = photo_file_name;
    }

    public Object getPhoto_content_type() {
        return photo_content_type;
    }

    public void setPhoto_content_type(Object photo_content_type) {
        this.photo_content_type = photo_content_type;
    }

    public Object getPhoto_file_size() {
        return photo_file_size;
    }

    public void setPhoto_file_size(Object photo_file_size) {
        this.photo_file_size = photo_file_size;
    }

    public Object getPhoto_updated_at() {
        return photo_updated_at;
    }

    public void setPhoto_updated_at(Object photo_updated_at) {
        this.photo_updated_at = photo_updated_at;
    }

    public Integer getMerchant_id() {
        return merchant_id;
    }

    public void setMerchant_id(Integer merchant_id) {
        this.merchant_id = merchant_id;
    }

    public String getInternal_id() {
        return internal_id;
    }

    public void setInternal_id(String internal_id) {
        this.internal_id = internal_id;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getNo_of_sku() {
        return no_of_sku;
    }

    public void setNo_of_sku(Integer no_of_sku) {
        this.no_of_sku = no_of_sku;
    }

    public Boolean getIn_stock() {
        return in_stock;
    }

    public void setIn_stock(Boolean in_stock) {
        this.in_stock = in_stock;
    }

}
