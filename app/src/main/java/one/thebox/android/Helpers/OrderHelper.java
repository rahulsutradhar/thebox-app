package one.thebox.android.Helpers;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import org.greenrobot.eventbus.EventBus;

import io.realm.Realm;
import io.realm.RealmList;
import one.thebox.android.Events.UpdateUpcomingDeliveriesEvent;
import one.thebox.android.Models.Order;
import one.thebox.android.Models.UserItem;
import one.thebox.android.api.Responses.OrdersApiResponse;
import one.thebox.android.app.MyApplication;
import one.thebox.android.fragment.SearchDetailFragment;
import one.thebox.android.util.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Ajeet Kumar Meena on 15-06-2016.
 */

public class OrderHelper {
    public static void addAndNotify(RealmList<Order> orders) {
        if (orders == null || orders.isEmpty()) {
            return;
        }
        saveToRealm(orders);
    }

    public static void getOrderAndNotify() {
        MyApplication.getAPIService().getMyOrders(PrefUtils.getToken(MyApplication.getInstance())).enqueue(
                new Callback<OrdersApiResponse>() {
                    @Override
                    public void onResponse(Call<OrdersApiResponse> call, Response<OrdersApiResponse> response) {
                        if (response.body() != null) {
                            if (response.body().isSuccess()) {
                                addAndNotify(response.body().getOrders());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<OrdersApiResponse> call, Throwable t) {

                    }
                }
        );
    }

    private static void saveToRealm(final RealmList<Order> orders) {
        Realm realm = MyApplication.getRealm();
        realm.executeTransactionAsync
                (new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.copyToRealmOrUpdate(orders);
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        sendUpdateOrderItemBroadcast();
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {

                    }
                });

    }

    private static void sendUpdateOrderItemBroadcast() {
        EventBus.getDefault().post(new UpdateUpcomingDeliveriesEvent(1));
    }
}
