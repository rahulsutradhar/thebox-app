package one.thebox.android.api.Responses;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import one.thebox.android.Models.ExploreItem;
import one.thebox.android.api.ApiResponse;

/**
 * Created by Ajeet Kumar Meena on 22-04-2016.
 */
public class ExploreItemResponse extends ApiResponse implements Serializable {

    private List<ExploreItem> boxes;

    public ExploreItemResponse(ArrayList<ExploreItem> boxes) {
        this.boxes = boxes;
    }

    public List<ExploreItem> getBoxes() {
        return boxes;
    }

    public void setBoxes(List<ExploreItem> boxes) {
        this.boxes = boxes;
    }
}
