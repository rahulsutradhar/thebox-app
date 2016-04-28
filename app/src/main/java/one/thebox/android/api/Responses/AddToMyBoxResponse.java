package one.thebox.android.api.Responses;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import one.thebox.android.Models.Category;
import one.thebox.android.Models.Order;
import one.thebox.android.Models.UserItem;
import one.thebox.android.api.ApiResponse;

/**
 * Created by Ajeet kumar Meena on 25-04-2016.
 */
public class AddToMyBoxResponse extends ApiResponse implements Serializable {

    @SerializedName("success")
    private boolean success;
    @SerializedName("info")
    private String info;
    @SerializedName("useritem")
    private UserItem userItem;
    @SerializedName("rest_of_the_categories_in_the_box")
    private ArrayList<Category> restOfTheCategoriesInTheBox;
    @SerializedName("rest_of_the_categories_in_other_box")
    private ArrayList<Category> restOfTheCategoriesInOtherBox;

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

    public ArrayList<Category> getRestOfTheCategoriesInTheBox() {
        return restOfTheCategoriesInTheBox;
    }

    public void setRestOfTheCategoriesInTheBox(ArrayList<Category> restOfTheCategoriesInTheBox) {
        this.restOfTheCategoriesInTheBox = restOfTheCategoriesInTheBox;
    }

    public ArrayList<Category> getRestOfTheCategoriesInOtherBox() {
        return restOfTheCategoriesInOtherBox;
    }

    public void setRestOfTheCategoriesInOtherBox(ArrayList<Category> restOfTheCategoriesInOtherBox) {
        this.restOfTheCategoriesInOtherBox = restOfTheCategoriesInOtherBox;
    }
}