package one.thebox.android.api.Responses;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import one.thebox.android.Models.Category;
import one.thebox.android.Models.UserCategory;
import one.thebox.android.Models.UserItem;
import one.thebox.android.api.ApiResponse;

/**
 * Created by Ajeet Kumar Meena on 26-04-2016.
 */
public class UpdateItemConfigResponse extends ApiResponse implements Serializable {

    @SerializedName("success")
    private boolean success;
    @SerializedName("info")
    private String info;
    @SerializedName("useritem")
    private UserItem userItem;
    @SerializedName("rest_of_the_categories")
    private List<Category> restCategories;

    public List<Category> getRestCategories() {
        return restCategories;
    }

    public void setRestCategories(List<Category> restCategories) {
        this.restCategories = restCategories;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public UserItem getUserItem() {
        return userItem;
    }

    public void setUserItem(UserItem userItem) {
        this.userItem = userItem;
    }
}
