package one.thebox.android.Events;

/**
 * Created by Ajeet Kumar Meena on 20-04-2016.
 */
public class ItemAddEvent {
    int noOfItems;

    public ItemAddEvent(int noOfItems) {
        this.noOfItems = noOfItems;
    }

    public int getNoOfItems() {
        return noOfItems;
    }

    public void setNoOfItems(int noOfItems) {
        this.noOfItems = noOfItems;
    }
}
