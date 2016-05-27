package one.thebox.android.Helpers;

import org.greenrobot.eventbus.EventBus;

import io.realm.Realm;
import io.realm.RealmList;
import one.thebox.android.Events.TabEvent;
import one.thebox.android.Models.Order;
import one.thebox.android.Models.UserItem;
import one.thebox.android.api.Responses.CartResponse;
import one.thebox.android.app.MyApplication;
import one.thebox.android.util.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Ajeet Kumar Meena on 19-05-2016.
 */
public class CartHelper {
    private static final String EXTRA_HAS_SAVED_ORDER = "extra_has_saved_order";

    public static void saveCartItemsIfRequire() {
        if (!PrefUtils.getBoolean(MyApplication.getInstance(), EXTRA_HAS_SAVED_ORDER, false)) {
            saveOrders();
        }
    }

    private static void saveOrders() {
        MyApplication.getAPIService().getMyCart(PrefUtils.getToken(MyApplication.getInstance()))
                .enqueue(new Callback<CartResponse>() {
                    @Override
                    public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                        if (response.body() != null) {
                            saveOrdersToRealm(response.body().getCart());
                            PrefUtils.putBoolean(MyApplication.getInstance(), EXTRA_HAS_SAVED_ORDER, true);
                        }
                    }

                    @Override
                    public void onFailure(Call<CartResponse> call, Throwable t) {

                    }
                });
    }

    private static void saveOrdersToRealm(final Order order) {
        final Realm superRealm = MyApplication.getRealm();
        superRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(order);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                //Toast.makeText(getActivity(), "success", Toast.LENGTH_SHORT).showTimeSlotBottomSheet();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                // Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).showTimeSlotBottomSheet();
            }
        });

    }

    public static int getNumberOfItemsInCart() {
        Order order = MyApplication.getRealm()
                .where(Order.class)
                .equalTo(Order.FIELD_ID, PrefUtils.getUser(MyApplication.getInstance()).getCartId())
                .findFirst();
        if (order == null || order.getId() == 0) {
            return 0;
        } else {
            return order.getUserItems().size();
        }
    }

    public static void addOrUpdateUserItem(final UserItem userItem) {
        if (userItem.getNextDeliveryScheduledAt() != null) {
            return;
        }
        final int cartId = PrefUtils.getUser(MyApplication.getInstance()).getCartId();
        Realm realm = MyApplication.getRealm();
        realm.executeTransactionAsync
                (new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Order order = realm.where(Order.class).equalTo(Order.FIELD_ID, cartId).findFirst();
                        RealmList<UserItem> userItems = order.getUserItems();
                        boolean arrayContainsUserItem = false;
                        for (int i = 0; i < userItems.size(); i++) {
                            if (userItem.getId() == userItems.get(i).getId()) {
                                userItems.set(i, userItem);
                                arrayContainsUserItem = true;
                                break;
                            }
                        }
                        if (!arrayContainsUserItem) {
                            userItems.add(userItem);
                        }
                        EventBus.getDefault().post(new TabEvent(userItems.size()));
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {

                    }
                });

    }

    public static void removeUserItem(final int userItemId) {
        final int cartId = PrefUtils.getUser(MyApplication.getInstance()).getCartId();
        Realm realm = MyApplication.getRealm();
        realm.executeTransactionAsync
                (new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Order order = realm.where(Order.class).equalTo(Order.FIELD_ID, cartId).findFirst();
                        RealmList<UserItem> userItems = order.getUserItems();
                        boolean arrayContainsUserItem = false;
                        for (int i = 0; i < userItems.size(); i++) {
                            if (userItemId == userItems.get(i).getId()) {
                                if (userItems.get(i).getNextDeliveryScheduledAt() != null) {
                                    return;
                                }
                                userItems.remove(i);
                            }
                        }
                        EventBus.getDefault().post(new TabEvent(userItems.size()));
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {

                    }
                });
    }

    public static void clearCart() {
        final int cartId = PrefUtils.getUser(MyApplication.getInstance()).getCartId();
        Realm realm = MyApplication.getRealm();
        realm.executeTransactionAsync
                (new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Order order = realm.where(Order.class).equalTo(Order.FIELD_ID, cartId).findFirst();
                        order.getUserItems().clear();
                        EventBus.getDefault().post(new TabEvent(order.getUserItems().size()));
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {

                    }
                });
    }
}
