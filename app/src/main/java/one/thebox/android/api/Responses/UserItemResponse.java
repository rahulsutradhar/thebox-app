package one.thebox.android.api.Responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import one.thebox.android.Models.user.OrderedUserItem;

/**
 * Created by nbansal2211 on 17/12/16.
 */
public class UserItemResponse {

    @SerializedName("data")
    @Expose
    private ArrayList<OrderedUserItem> orderedUserItems;

    public ArrayList<OrderedUserItem> getOrderedUserItems() {
        return orderedUserItems;
    }

    public void setOrderedUserItems(ArrayList<OrderedUserItem> orderedUserItems) {
        this.orderedUserItems = orderedUserItems;
    }
}
