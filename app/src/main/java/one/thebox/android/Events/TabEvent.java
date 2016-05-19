package one.thebox.android.Events;

/**
 * Created by Ajeet kumar Meena on 02-05-2016.
 */
public class TabEvent {
    int numberOfItemsInCart;

    public TabEvent(int numberOfItemsInCart) {
        this.numberOfItemsInCart = numberOfItemsInCart;
    }

    public int getNumberOfItemsInCart() {
        return numberOfItemsInCart;
    }

    public void setNumberOfItemsInCart(int numberOfItemsInCart) {
        this.numberOfItemsInCart = numberOfItemsInCart;
    }
}
