package one.thebox.android.Helpers.cart;

import android.content.Context;

import java.util.ArrayList;

import one.thebox.android.Models.cart.CartProduct;
import one.thebox.android.Models.items.BoxItem;
import one.thebox.android.Models.items.ItemConfig;
import one.thebox.android.services.cart.CartHelperService;

/**
 * Created by developers on 01/06/17.
 */

public class ProductQuantity {

    private static ArrayList<CartProduct> productQuantities = new ArrayList<>();

    /**
     * Add Product
     */
    public static void addProduct(BoxItem boxItem) {
        productQuantities.add(new CartProduct(boxItem.getUuid(), boxItem.getQuantity(), boxItem.getSelectedItemConfig().getUuid()));
    }

    /**
     * Set product
     */
    public static void setProduct(BoxItem boxItem, int position) {
        productQuantities.set(position, new CartProduct(boxItem.getUuid(), boxItem.getQuantity(), boxItem.getSelectedItemConfig().getUuid()));
    }

    /**
     * Add new Product
     */
    public static void addNewProduct(BoxItem boxItem) {

        if (getCartSize() > 0) {
            int index = 0;
            boolean flag = false;
            for (CartProduct cartProduct : getProductQuantities()) {
                if (cartProduct.getBoxItemUuid().equalsIgnoreCase(boxItem.getUuid())) {
                    flag = true;
                    setProduct(boxItem, index);
                    break;
                }
                index++;
            }

            if (flag == false) {
                addProduct(boxItem);
            }
        } else {
            addProduct(boxItem);
        }
    }


    /**
     * Remove Product
     */
    public static void removeProduct(BoxItem boxItem) {
        int index = 0;
        for (CartProduct cartProduct : productQuantities) {
            if (cartProduct.getBoxItemUuid().equalsIgnoreCase(boxItem.getUuid())) {
                productQuantities.remove(index);
                break;
            }
            index++;
        }
    }

    /**
     * Update Product Quantity
     */
    public static void updateQuantity(BoxItem boxItem, int quantity) {
        int index = 0;
        for (CartProduct cartProduct : productQuantities) {
            if (cartProduct.getBoxItemUuid().equalsIgnoreCase(boxItem.getUuid())) {
                productQuantities.get(index).setQuantity(quantity);
                break;
            }
            index++;
        }
    }

    /**
     * Update Selected ItemConfig
     */
    public static void updateItemConfig(BoxItem boxItem, ItemConfig selectedItemConfig) {
        int index = 0;
        for (CartProduct cartProduct : productQuantities) {
            if (cartProduct.getBoxItemUuid().equalsIgnoreCase(boxItem.getUuid())) {
                productQuantities.get(index).setItemConfigUuid(selectedItemConfig.getUuid());
                break;
            }
            index++;
        }
    }

    /**
     * Synced With CartProduct Item; fetched in Setting call
     */
    public static void syncedWithCart(ArrayList<CartProduct> cartProducts, Context context) {
        trash();
        setProductQuantities(cartProducts);

        //check if service is running or not
        CartHelperService.checkServiceRunningWhenAdded(context);
    }

    /**
     * Remove All item
     */
    public static void trash() {
        productQuantities.clear();
    }

    /**
     * cart Size
     */
    public static int getCartSize() {
        return productQuantities.size();
    }


    public static ArrayList<CartProduct> getProductQuantities() {
        return productQuantities;
    }

    public static void setProductQuantities(ArrayList<CartProduct> productQuantities) {
        ProductQuantity.productQuantities = productQuantities;
    }
}
