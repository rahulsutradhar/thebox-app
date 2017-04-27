package one.thebox.android.api.Responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import one.thebox.android.Models.saving.Saving;
import one.thebox.android.Models.user.OrderedUserItem;

/**
 * Created by nbansal2211 on 17/12/16.
 */
public class UserItemResponse {

    @SerializedName("data")
    @Expose
    private ArrayList<OrderedUserItem> orderedUserItems;

    private ArrayList<Saving> savings;

    public UserItemResponse() {

    }

    public ArrayList<OrderedUserItem> getOrderedUserItems() {
        return orderedUserItems;
    }

    public void setOrderedUserItems(ArrayList<OrderedUserItem> orderedUserItems) {
        this.orderedUserItems = orderedUserItems;
    }

    public ArrayList<Saving> getSavings() {
        return savings;
    }

    public void setSavings(ArrayList<Saving> savings) {
        this.savings = savings;
    }
}
