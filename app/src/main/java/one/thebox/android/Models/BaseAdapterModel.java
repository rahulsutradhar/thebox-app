package one.thebox.android.Models;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by nbansal2211 on 17/12/16.
 */

public class BaseAdapterModel implements Serializable {
    private int viewType;

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }
}