package one.thebox.android.api.Responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import one.thebox.android.Models.saving.Saving;
import one.thebox.android.Models.user.Subscription;

/**
 * Created by nbansal2211 on 17/12/16.
 */
public class UserItemResponse {

    @SerializedName("data")
    @Expose
    private ArrayList<Subscription> subscriptions;

    private ArrayList<Saving> savings;

    public UserItemResponse() {

    }

    public ArrayList<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(ArrayList<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public ArrayList<Saving> getSavings() {
        return savings;
    }

    public void setSavings(ArrayList<Saving> savings) {
        this.savings = savings;
    }
}
