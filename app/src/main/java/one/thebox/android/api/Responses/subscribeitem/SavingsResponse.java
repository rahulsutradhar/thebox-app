package one.thebox.android.api.Responses.subscribeitem;

import java.io.Serializable;
import java.util.ArrayList;

import one.thebox.android.Models.saving.Saving;
import one.thebox.android.api.ApiResponse;

/**
 * Created by developers on 06/06/17.
 */

public class SavingsResponse extends ApiResponse implements Serializable {

    private ArrayList<Saving> savings;

    public ArrayList<Saving> getSavings() {
        return savings;
    }

    public void setSavings(ArrayList<Saving> savings) {
        this.savings = savings;
    }
}
