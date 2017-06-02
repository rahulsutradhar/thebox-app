package one.thebox.android.services.cart;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;

import one.thebox.android.Helpers.cart.ProductQuantity;
import one.thebox.android.api.RequestBodies.cart.CartItemRequest;
import one.thebox.android.api.Responses.cart.CartItemResponse;
import one.thebox.android.app.TheBox;
import one.thebox.android.fragment.CartFragment;
import one.thebox.android.util.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by developers on 01/06/17.
 */

public class CartHelperService implements ServiceConnection {

    private static boolean cartSyncRunning = false;
    private SyncCartService syncCartService;

    CartHelperService() {

    }

    public static void updateCartToServer(Context context, final Fragment fragment) {
        TheBox.getAPIService()
                .syncCart(PrefUtils.getToken(context), new CartItemRequest(ProductQuantity.getProductQuantities()))
                .enqueue(new Callback<CartItemResponse>() {
                    @Override
                    public void onResponse(Call<CartItemResponse> call, Response<CartItemResponse> response) {

                        try {
                            if (response.isSuccessful()) {
                                if (fragment != null) {
                                    ((CartFragment) fragment).setCartUpdateServerResponse(true, response.body());
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            if (fragment != null) {
                                ((CartFragment) fragment).setCartUpdateServerResponse(false, null);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<CartItemResponse> call, Throwable t) {
                        if (fragment != null) {
                            ((CartFragment) fragment).setCartUpdateServerResponse(false, null);
                        }
                    }
                });
    }


    public static boolean isCartSyncRunning() {
        return cartSyncRunning;
    }

    public static void setCartSyncRunning(boolean cartSyncRunning) {
        CartHelperService.cartSyncRunning = cartSyncRunning;
    }


    /**
     * Start Service
     */
    public static void startCartService(Context context) {
        Intent intent = new Intent(context, SyncCartService.class);
        context.startService(intent);
        setCartSyncRunning(true);
    }

    public static void stopCartService(Context context, boolean shallMakelastCall) {
        Intent intent = new Intent(context, SyncCartService.class);
        context.stopService(intent);
        setCartSyncRunning(false);

        if (shallMakelastCall) {
            //last final call for the cart sync
            updateCartToServer(context, null);
        }
    }


    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        SyncCartService.CartBinder cartBinder = (SyncCartService.CartBinder) service;
        syncCartService = cartBinder.getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        syncCartService = null;
    }


    /**
     * Check if Service is running or not when added
     */
    public static void checkServiceRunningWhenAdded(Context context) {
        if (!isCartSyncRunning()) {
            if (ProductQuantity.getCartSize() > 0) {
                startCartService(context);
            }
        }
    }

    /**
     * Check if Service is running ot not when remove
     */
    public static void checkServiceRunningWhenRemoved(Context context, boolean shallMakelastCall) {
        if (isCartSyncRunning()) {
            if (ProductQuantity.getCartSize() == 0) {
                stopCartService(context, shallMakelastCall);
            }
        } else {
            if (ProductQuantity.getCartSize() > 0) {
                startCartService(context);
            } else {
                //make a last call to sync the empty cart
                updateCartToServer(context, null);
            }
        }
    }


}
