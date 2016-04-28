package one.thebox.android.api.Responses;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import one.thebox.android.Models.Box;
import one.thebox.android.api.ApiResponse;

/**
 * Created by Ajeet Kumar Meena on 26-04-2016.
 */
public class MyBoxResponse extends ApiResponse implements Serializable {

    @SerializedName("userbox")
    private List<Box> boxes;

    public MyBoxResponse(List<Box> boxes) {
        this.boxes = boxes;
    }

    public List<Box> getBoxes() {
        return boxes;
    }

    public void setBoxes(List<Box> boxes) {
        this.boxes = boxes;
    }
}
