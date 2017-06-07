package one.thebox.android.Events;

import one.thebox.android.Models.items.Box;

/**
 * Created by developers on 07/06/17.
 */

public class DisplayProductForBoxEvent {

    private Box box;

    public DisplayProductForBoxEvent(Box box) {
        this.box = box;
    }

    public Box getBox() {
        return box;
    }

    public void setBox(Box box) {
        this.box = box;
    }
}
