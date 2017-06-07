package one.thebox.android.api.Responses.boxes;

import java.io.Serializable;

import io.realm.RealmList;
import one.thebox.android.Models.items.Box;
import one.thebox.android.api.ApiResponse;

/**
 * Created by developers on 28/05/17.
 */

public class BoxResponse extends ApiResponse implements Serializable {

    private RealmList<Box> boxes;

    public BoxResponse() {
    }

    public RealmList<Box> getBoxes() {
        return boxes;
    }

    public void setBoxes(RealmList<Box> boxes) {
        this.boxes = boxes;
    }
}
