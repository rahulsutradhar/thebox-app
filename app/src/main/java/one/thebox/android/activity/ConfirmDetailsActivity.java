package one.thebox.android.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;

import one.thebox.android.Models.Address;
import one.thebox.android.Models.BillItem;
import one.thebox.android.R;
import one.thebox.android.adapter.DeliveryAddressAdapter;
import one.thebox.android.adapter.PaymentDetailAdapter;

public class ConfirmDetailsActivity extends BaseActivity {
    private RecyclerView recyclerViewPaymentDetail, recyclerViewDeliveryAddress, recyclerViewTimeSlots;
    private PaymentDetailAdapter paymentDetailAdapter;
    private DeliveryAddressAdapter deliveryAddressAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_details);
        initViews();
        setupRecyclerAdapter();
    }

    private void setupRecyclerAdapter() {
        paymentDetailAdapter = new PaymentDetailAdapter(this);
        deliveryAddressAdapter = new DeliveryAddressAdapter(this);
        for (int i = 0; i < 10; i++) {
            deliveryAddressAdapter.addAddress(new Address());
            paymentDetailAdapter.addSubBillItem(new BillItem.SubBillItem());
        }
        recyclerViewDeliveryAddress.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPaymentDetail.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDeliveryAddress.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                int action = e.getAction();
                switch (action) {
                    case MotionEvent.ACTION_MOVE:
                        rv.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
        recyclerViewPaymentDetail.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                int action = e.getAction();
                switch (action) {
                    case MotionEvent.ACTION_MOVE:
                        rv.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
        recyclerViewPaymentDetail.setAdapter(paymentDetailAdapter);
        recyclerViewDeliveryAddress.setAdapter(deliveryAddressAdapter);
    }

    private void initViews() {
        recyclerViewPaymentDetail = (RecyclerView) findViewById(R.id.recycler_view_payment_detail);
        recyclerViewDeliveryAddress = (RecyclerView) findViewById(R.id.recycler_view_delivery_address);
    }

    @Override
    void onClick(int id) {

    }
}
