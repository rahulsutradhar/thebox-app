package one.thebox.android.Models.saving;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by developers on 22/03/17.
 */

public class TotalItem extends RealmObject implements Serializable {

    private String title;

    private String value;

    public TotalItem() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
