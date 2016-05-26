package one.thebox.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
import one.thebox.android.Helpers.CartHelper;
import one.thebox.android.Models.AddressAndOrder;
import one.thebox.android.Models.Order;
import one.thebox.android.Models.User;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.BoxLoader;
import one.thebox.android.adapter.PaymentDetailAdapter;
import one.thebox.android.api.RequestBodies.PaymentRequestBody;
import one.thebox.android.api.Responses.PaymentResponse;
import one.thebox.android.app.MyApplication;
import one.thebox.android.util.CoreGsonUtils;
import one.thebox.android.util.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmPaymentDetailsActivity extends BaseActivity {
    private static final String EXTRA_ARRAY_LIST_ORDER = "array_list_order";
    private RecyclerView recyclerViewPaymentDetail;
    private PaymentDetailAdapter paymentDetailAdapter;
    private CheckBox checkBox;
    private ArrayList<AddressAndOrder> addressAndOrders;
    private TextView payButton;
    private User user;

    public static Intent getInstance(Context context, ArrayList<AddressAndOrder> addressAndOrders) {
        Intent intent = new Intent(context, ConfirmPaymentDetailsActivity.class);
        intent.putExtra(EXTRA_ARRAY_LIST_ORDER, CoreGsonUtils.toJson(addressAndOrders));
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_details);
        user = PrefUtils.getUser(this);
        setTitle("Payment Details");
        initViews();
        initVariables();
        setupRecyclerAdapter();
    }

    private void initVariables() {
        String ordersString = getIntent().getStringExtra(EXTRA_ARRAY_LIST_ORDER);
        addressAndOrders = CoreGsonUtils.fromJsontoArrayList(ordersString, AddressAndOrder.class);
    }

    private void setupRecyclerAdapter() {
        ArrayList<Order> orders = new ArrayList<>();
        for (AddressAndOrder addressAndOrder : addressAndOrders) {
            orders.add(addressAndOrder.getOrder());
        }
        paymentDetailAdapter = new PaymentDetailAdapter(this);
        paymentDetailAdapter.setOrders(orders);
        recyclerViewPaymentDetail.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPaymentDetail.setAdapter(paymentDetailAdapter);
    }

    private void initViews() {
        recyclerViewPaymentDetail = (RecyclerView) findViewById(R.id.recycler_view_payment_detail);
        payButton = (TextView) findViewById(R.id.button_pay);
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pay();
            }
        });
    }

    private void pay() {
        final BoxLoader dialog =   new BoxLoader(this).show();
        MyApplication.getAPIService().payOrders(PrefUtils.getToken(this), new PaymentRequestBody(addressAndOrders))
                .enqueue(new Callback<PaymentResponse>() {
                    @Override
                    public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                        dialog.dismiss();
                        if (response.body() != null) {
                            if (response.body().isSuccess()) {
                                Toast.makeText(ConfirmPaymentDetailsActivity.this, response.body().getInfo(), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ConfirmPaymentDetailsActivity.this, MainActivity.class).putExtra(MainActivity.EXTRA_ATTACH_FRAGMENT_NO, 1));
                                storeToRealm(response.body().getOrders());
                                CartHelper.clearCart();
                                finish();
                            } else {
                                Toast.makeText(ConfirmPaymentDetailsActivity.this, response.body().getInfo(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<PaymentResponse> call, Throwable t) {
                        dialog.dismiss();
                    }
                });
    }

    private void storeToRealm(RealmList<Order> orders) {
        final Realm superRealm = MyApplication.getRealm();
        for (final Order order : orders) {
            superRealm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate(order);
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    //Toast.makeText(getActivity(), "success", Toast.LENGTH_SHORT).show();
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    // Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }


}
