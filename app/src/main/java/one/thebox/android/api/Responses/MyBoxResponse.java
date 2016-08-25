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

    @SerializedName("next_order_status")
    private String next_order_status;

    public MyBoxResponse(RealmList<Box> boxes) {
        this.boxes = boxes;
        this.next_order_status = next_order_status;
    }

    public RealmList<Box> getBoxes() {
        return boxes;
    }

    public void setBoxes(RealmList<Box> boxes) {
        this.boxes = boxes;
    }

    public String getNextOrderStatus() {
        return next_order_status;
    }

    public void setNextOrderStatus(String next_order_status) {
        this.boxes = boxes;
    }
}
