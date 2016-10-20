package one.thebox.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.api.Api;
import com.razorpay.Checkout;

import org.json.JSONObject;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
import one.thebox.android.Helpers.CartHelper;
import one.thebox.android.Helpers.OrderHelper;
import one.thebox.android.Models.AddressAndOrder;
import one.thebox.android.Models.Order;
import one.thebox.android.Models.User;
import one.thebox.android.R;
import one.thebox.android.Services.UpdateOrderService;
import one.thebox.android.ViewHelper.BoxLoader;
import one.thebox.android.adapter.PaymentDetailAdapter;
import one.thebox.android.api.ApiResponse;
import one.thebox.android.api.RequestBodies.MergeCartToOrderRequestBody;
import one.thebox.android.api.RequestBodies.PaymentRequestBody;
import one.thebox.android.api.Responses.PaymentResponse;
import one.thebox.android.app.MyApplication;
import one.thebox.android.util.Constants;
import one.thebox.android.util.CoreGsonUtils;
import one.thebox.android.util.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmPaymentDetailsActivity extends BaseActivity {
    private static final String EXTRA_ARRAY_LIST_ORDER = "array_list_order";
    private static final String EXTRA_MERGE_ORDER_ID = "merge_order_id";
    private static final String EXTRA_TOTAL_CART_AMOUNT="total_cart_amount";

    private Order cart;
    private Context context;
    private RecyclerView recyclerViewPaymentDetail;
    private PaymentDetailAdapter paymentDetailAdapter;
    private ArrayList<AddressAndOrder> addressAndOrders;
    private TextView payButton;
    private User user;
    private int mergeOrderId;

    public static Intent getInstance(Context context, ArrayList<AddressAndOrder> addressAndOrders) {
        Intent intent = new Intent(context, ConfirmPaymentDetailsActivity.class);
        intent.putExtra(EXTRA_ARRAY_LIST_ORDER, CoreGsonUtils.toJson(addressAndOrders));
        return intent;
    }

    public static Intent getInstance(Context context, ArrayList<AddressAndOrder> addressAndOrders, int mergeOrderId) {
        Intent intent = new Intent(context, ConfirmPaymentDetailsActivity.class);
        intent.putExtra(EXTRA_ARRAY_LIST_ORDER, CoreGsonUtils.toJson(addressAndOrders));
        intent.putExtra(EXTRA_MERGE_ORDER_ID, mergeOrderId);
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
        mergeOrderId = getIntent().getIntExtra(EXTRA_MERGE_ORDER_ID, 0);

        int cartId = PrefUtils.getUser(this).getCartId();
        Realm realm = MyApplication.getRealm();
        Order cart = realm.where(Order.class)
                .notEqualTo(Order.FIELD_ID, 0)
                .equalTo(Order.FIELD_ID, cartId).findFirst();
        if(cart!=null) {
            this.cart = realm.copyFromRealm(cart);
        }
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


        if (mergeOrderId == 0) {
            payButton.setText("Total: Rs " + paymentDetailAdapter.getFinalPaymentAmount() + "\n" + "Pay");
        }
        else {
            payButton.setText("Total: Rs " + cart.getTotalPriceOfUserItems() + "\n" + "Pay");
                }

    }

    private void initViews() {
        recyclerViewPaymentDetail = (RecyclerView) findViewById(R.id.recycler_view_payment_detail);
        payButton = (TextView) findViewById(R.id.button_pay);
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ConfirmPaymentDetailsActivity.this,PaymentOptionActivity.class);
                if (mergeOrderId == 0) {
                    intent.putExtra(EXTRA_TOTAL_CART_AMOUNT,String.valueOf(paymentDetailAdapter.getFinalPaymentAmount()));
                }
                else {
                    intent.putExtra(EXTRA_TOTAL_CART_AMOUNT,String.valueOf(cart.getTotalPriceOfUserItems()));
                }

                intent.putExtra(EXTRA_ARRAY_LIST_ORDER,getIntent().getStringExtra(EXTRA_ARRAY_LIST_ORDER));
                intent.putExtra(EXTRA_MERGE_ORDER_ID,getIntent().getIntExtra(EXTRA_MERGE_ORDER_ID, 0));
                startActivity(intent);

//                startPayment(String.valueOf(paymentDetailAdapter.getFinalPaymentAmount()*100),"Subscribe and Forget",user.getEmail(),user.getPhoneNumber(),user.getName());

//                if (mergeOrderId == 0) {
//                    pay();
//                } else {
//                    mergeCartToOrder();
//                }
            }
        });
    }

//    private void mergeCartToOrder() {
//        final BoxLoader dialog = new BoxLoader(this).show();
//        MyApplication.getAPIService().mergeCartItemToOrder(PrefUtils.getToken(this), new MergeCartToOrderRequestBody(mergeOrderId))
//                .enqueue(new Callback<PaymentResponse>() {
//                    @Override
//                    public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
//                        dialog.dismiss();
//                        if (response.body() != null) {
//                            if (response.body().isSuccess()) {
//                                PrefUtils.putBoolean(MyApplication.getInstance(), Constants.PREF_IS_ORDER_IS_LOADING, true);
//                                Toast.makeText(ConfirmPaymentDetailsActivity.this, response.body().getInfo(), Toast.LENGTH_SHORT).show();
//                                CartHelper.clearCart();
//                                RealmList<Order> orders = new RealmList<>();
//                                orders.add(response.body().getOrders());
//                                OrderHelper.addAndNotify(orders);
//                                startActivity(new Intent(ConfirmPaymentDetailsActivity.this, MainActivity.class).putExtra(MainActivity.EXTRA_ATTACH_FRAGMENT_NO, 1));
//                                startService(new Intent(ConfirmPaymentDetailsActivity.this, UpdateOrderService.class));
//                                finish();
//                            } else {
//                                Toast.makeText(ConfirmPaymentDetailsActivity.this, response.body().getInfo(), Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<PaymentResponse> call, Throwable t) {
//                        dialog.dismiss();
//                    }
//                });
//    }
//
//    private void pay() {
//        final BoxLoader dialog = new BoxLoader(this).show();
//        MyApplication.getAPIService().payOrders(PrefUtils.getToken(this), new PaymentRequestBody(addressAndOrders))
//                .enqueue(new Callback<PaymentResponse>() {
//                    @Override
//                    public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
//                        dialog.dismiss();
//                        if (response.body() != null) {
//                            if (response.body().isSuccess()) {
//                                PrefUtils.putBoolean(MyApplication.getInstance(), Constants.PREF_IS_ORDER_IS_LOADING, true);
//                                Toast.makeText(ConfirmPaymentDetailsActivity.this, response.body().getInfo(), Toast.LENGTH_SHORT).show();
//                                CartHelper.clearCart();
//                                RealmList<Order> orders = new RealmList<>();
//                                orders.add(response.body().getOrders());
//                                OrderHelper.addAndNotify(orders);
//                                startActivity(new Intent(ConfirmPaymentDetailsActivity.this, MainActivity.class).putExtra(MainActivity.EXTRA_ATTACH_FRAGMENT_NO, 1));
//                                startService(new Intent(ConfirmPaymentDetailsActivity.this, UpdateOrderService.class));
//                                finish();
//                            } else {
//                                Toast.makeText(ConfirmPaymentDetailsActivity.this, response.body().getInfo(), Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<PaymentResponse> call, Throwable t) {
//                        dialog.dismiss();
//                    }
//                });
//    }
//
//
//
//
//
//
//    public void startPayment(String amount,String description,String email,String phonenumber,String name){
//
//        /**
//         * Put your key id generated in Razorpay dashboard here
//         */
//        String your_key_id = "rzp_test_5C5hEs59JtakdV";
//
//        Checkout razorpayCheckout = new Checkout();
//        razorpayCheckout.setKeyID(your_key_id);
//
//        /**
//         * Image for checkout form can passed as reference to a drawable
//         */
//        razorpayCheckout.setImage(R.mipmap.ic_launcher);
//
//        /**
//         * Reference to current activity
//         */
//        Activity activity = this;
//
//        try{
//            JSONObject options = new JSONObject("{" +
//                    "description: '"+description+"'," +
//                    "currency: 'INR'}"
//            );
//
//            options.put("amount", amount);
//            options.put("name", "The Box Cart");
//            options.put("prefill", new JSONObject("{email: '"+email+"', contact: '"+phonenumber+"', name: '"+name+"'}"));
//
//            razorpayCheckout.open(activity, options);
//
//        } catch(Exception e){
//            e.printStackTrace();
//        }
//    }
//
//    public void onPaymentSuccess(String razorpayPaymentID){
//
//        if (mergeOrderId == 0) {
//            pay();
//        } else {
//            mergeCartToOrder();
//        }
//
//
////        try {
////            Toast.makeText(this, "Payment Successful: " + razorpayPaymentID, Toast.LENGTH_SHORT).show();
////        }
////        catch (Exception e){
////            Log.e("com.merchant", e.getMessage(), e);
////        }
//    }
//
//
//    public void onPaymentError(int code, String response){
//        try {
//            Toast.makeText(this, "Payment failed: " + Integer.toString(code) + " " + response, Toast.LENGTH_SHORT).show();
//        }
//        catch (Exception e){
//            Log.e("com.merchant", e.getMessage(), e);
//        }
//    }


}
