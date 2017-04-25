package one.thebox.android.Events;

import java.util.ArrayList;

import io.realm.RealmList;
import one.thebox.android.Models.saving.Saving;

/**
 * Created by developers on 07/04/17.
 */

public class UpdateSavingsEvent {

    private ArrayList<Saving> savings;

    public UpdateSavingsEvent(ArrayList<Saving> savings) {
        this.savings = savings;
    }

    public ArrayList<Saving> getSavings() {
        return savings;
    }

    public void setSavings(ArrayList<Saving> savings) {
        this.savings = savings;
    }
}
