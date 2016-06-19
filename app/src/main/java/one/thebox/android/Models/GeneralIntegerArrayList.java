package one.thebox.android.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by vaibhav on 19/06/16.
 */
public class GeneralIntegerArrayList extends RealmObject implements Serializable {
    @PrimaryKey
    @SerializedName("category_id")
    public Integer category_id;

    public GeneralIntegerArrayList() {
    }

    public GeneralIntegerArrayList(int category_id) {
        this.category_id = category_id;
    }

    public int getId() {
        return category_id;
    }

    public void setId(int id) {
        this.category_id = category_id;
    }
}
