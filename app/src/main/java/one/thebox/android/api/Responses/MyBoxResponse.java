package one.thebox.android.api.Responses;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmList;
import one.thebox.android.Models.Box;
import one.thebox.android.api.ApiResponse;

/**
 * Created by Ajeet Kumar Meena on 26-04-2016.
 */
public class MyBoxResponse extends ApiResponse implements Serializable {

    @SerializedName("userbox")
    private RealmList<Box> boxes;

    public MyBoxResponse(RealmList<Box> boxes) {
        this.boxes = boxes;
    }

    public RealmList<Box> getBoxes() {
        return boxes;
    }

    public void setBoxes(RealmList<Box> boxes) {
        this.boxes = boxes;
    }
}
