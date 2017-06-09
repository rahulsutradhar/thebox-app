package one.thebox.android.Models.update;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import io.realm.RealmList;
import one.thebox.android.Models.Cart;
import one.thebox.android.Models.User;
import one.thebox.android.Models.items.Box;
import one.thebox.android.Models.items.BoxItem;
import one.thebox.android.Models.items.CartItem;

/**
 * Created by nbansal2211 on 03/01/17.
 * <p>
 * Modified by Developers on 02/06/17.
 */

public class Setting implements Serializable {

    private boolean new_version_available;

    private boolean force_update;

    @SerializedName("update_popup_details")
    private UpdatePopupDetails updatePopupDetails;

    @SerializedName("show_details")
    private CommonPopupDetails commonPopupDetails;

    @SerializedName("user_data_available")
    private boolean userDataAvailable;

    @SerializedName("address_available")
    private boolean addressAvailable;

    private User user;

    private ArrayList<Box> boxes;

    @SerializedName("cart")
    private ArrayList<CartItem> cartItems;


    /***************************************
     * Methods
     ***************************************/

    /**
     * For Realm Database, sync
     *
     * @return
     */
    public RealmList<BoxItem> getParsedCartItems() {
        RealmList<BoxItem> boxItems = new RealmList<>();

        for (CartItem cartItem : getCartItems()) {
            BoxItem boxItem = cartItem.getBoxItem();
            boxItem.setQuantity(cartItem.getQuantity());
            boxItem.setSelectedItemConfig(cartItem.getSelectedItemconfig());
            boxItems.add(boxItem);
        }

        return boxItems;
    }

    /**
     * For Product Quantitites; sync
     *
     * @return
     */
    public ArrayList<Cart> getParsedCartUuids() {
        ArrayList<Cart> carts = new ArrayList<>();

        for (CartItem cartItem : getCartItems()) {
            Cart cart = new Cart(cartItem.getBoxUuid(), cartItem.getQuantity(), cartItem.getItemconfigUuid());
            carts.add(cart);
        }

        return carts;
    }


    /***************************************
     * Getter Setter
     ***************************************/

    public UpdatePopupDetails getUpdatePopupDetails() {
        return updatePopupDetails;
    }

    public void setUpdatePopupDetails(UpdatePopupDetails updatePopupDetails) {
        this.updatePopupDetails = updatePopupDetails;
    }

    public boolean isNew_version_available() {
        return new_version_available;
    }

    public void setNew_version_available(boolean new_version_available) {
        this.new_version_available = new_version_available;
    }

    public boolean isForce_update() {
        return force_update;
    }

    public void setForce_update(boolean force_update) {
        this.force_update = force_update;
    }

    public boolean isUserDataAvailable() {
        return userDataAvailable;
    }

    public void setUserDataAvailable(boolean userDataAvailable) {
        this.userDataAvailable = userDataAvailable;
    }

    public CommonPopupDetails getCommonPopupDetails() {
        return commonPopupDetails;
    }

    public void setCommonPopupDetails(CommonPopupDetails commonPopupDetails) {
        this.commonPopupDetails = commonPopupDetails;
    }

    public boolean isAddressAvailable() {
        return addressAvailable;
    }

    public void setAddressAvailable(boolean addressAvailable) {
        this.addressAvailable = addressAvailable;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ArrayList<Box> getBoxes() {
        return boxes;
    }

    public void setBoxes(ArrayList<Box> boxes) {
        this.boxes = boxes;
    }

    public ArrayList<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(ArrayList<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

}
