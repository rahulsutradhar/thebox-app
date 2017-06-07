package one.thebox.android.Events;

import java.util.ArrayList;

import io.realm.RealmList;
import one.thebox.android.Models.saving.Saving;

/**
 * Created by developers on 07/04/17.
 */

public class UpdateSavingsEvent {

    private boolean emptyState;

    public UpdateSavingsEvent() {

    }

    public boolean isEmptyState() {
        return emptyState;
    }

    public void setEmptyState(boolean emptyState) {
        this.emptyState = emptyState;
    }
}
