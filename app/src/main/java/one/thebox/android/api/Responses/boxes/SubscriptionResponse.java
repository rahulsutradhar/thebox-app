package one.thebox.android.api.Responses.boxes;

import java.io.Serializable;
import java.util.ArrayList;

import one.thebox.android.Models.saving.Saving;
import one.thebox.android.Models.items.Subscription;
import one.thebox.android.api.ApiResponse;

/**
 * Created by developers on 05/06/17.
 */

public class SubscriptionResponse extends ApiResponse implements Serializable {

    private ArrayList<Saving> savings;

    private ArrayList<Subscription> subscriptions;

    public SubscriptionResponse() {

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
