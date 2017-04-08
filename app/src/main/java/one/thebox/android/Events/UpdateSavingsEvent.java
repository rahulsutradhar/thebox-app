package one.thebox.android.Events;

import io.realm.RealmList;
import one.thebox.android.Models.saving.Saving;

/**
 * Created by developers on 07/04/17.
 */

public class UpdateSavingsEvent {

    private RealmList<Saving> savings;

    public UpdateSavingsEvent(RealmList<Saving> savings) {
        this.savings = savings;
    }

    public RealmList<Saving> getSavings() {
        return savings;
    }

    public void setSavings(RealmList<Saving> savings) {
        this.savings = savings;
    }
}
