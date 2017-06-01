package one.thebox.android.services.cart;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import one.thebox.android.Helpers.cart.ProductQuantity;
import one.thebox.android.api.RequestBodies.cart.CartItemRequest;
import one.thebox.android.api.Responses.cart.CartItemResponse;
import one.thebox.android.app.TheBox;
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

    public static void updateCartToServer(Context context) {
        TheBox.getAPIService()
                .syncCart(PrefUtils.getToken(context), new CartItemRequest(ProductQuantity.getProductQuantities()))
                .enqueue(new Callback<CartItemResponse>() {
                    @Override
                    public void onResponse(Call<CartItemResponse> call, Response<CartItemResponse> response) {

                        try {
                            if (response.isSuccessful()) {
                                Log.d("CART_UPDATED", " Successfull " + response.message());
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.d("CART_UPDATED", " Exception");
                        }
                    }

                    @Override
                    public void onFailure(Call<CartItemResponse> call, Throwable t) {
                        Log.d("CART_UPDATED", " Failed");
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

    public static void stopCartService(Context context) {
        Intent intent = new Intent(context, SyncCartService.class);
        context.stopService(intent);
        setCartSyncRunning(false);

        //last final call for the cart sync
        updateCartToServer(context);
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
    public static void checkServiceRunningWhenRemoved(Context context) {
        if (isCartSyncRunning()) {
            if (ProductQuantity.getCartSize() == 0) {
                stopCartService(context);
            }
        }
    }


}
