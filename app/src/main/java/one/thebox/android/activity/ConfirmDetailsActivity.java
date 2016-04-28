package one.thebox.android.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import one.thebox.android.Models.Address;
import one.thebox.android.Models.BillItem;
import one.thebox.android.R;
import one.thebox.android.adapter.EditDeliveryAddressAdapter;
import one.thebox.android.adapter.SelectDeliveryAddressAdapter;
import one.thebox.android.adapter.PaymentDetailAdapter;

public class ConfirmDetailsActivity extends BaseActivity {
    private RecyclerView recyclerViewPaymentDetail, recyclerViewDeliveryAddress, recyclerViewTimeSlots;
    private PaymentDetailAdapter paymentDetailAdapter;
    private SelectDeliveryAddressAdapter selectDeliveryAddressAdapter;
    private EditDeliveryAddressAdapter editDeliveryAddressAdapter;
    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_details);
        setTitle("Confirm Details Activity");
        initViews();
        setupRecyclerAdapter();
    }

    private void setupRecyclerAdapter() {
        paymentDetailAdapter = new PaymentDetailAdapter(this);
        editDeliveryAddressAdapter = new EditDeliveryAddressAdapter(this);
        selectDeliveryAddressAdapter = new SelectDeliveryAddressAdapter(this);
        for (int i = 0; i < 10; i++) {
            selectDeliveryAddressAdapter.addAddress(new Address());
            editDeliveryAddressAdapter.addAddress(new Address());
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
        recyclerViewDeliveryAddress.setAdapter(selectDeliveryAddressAdapter);
    }

    private void initViews() {
        recyclerViewPaymentDetail = (RecyclerView) findViewById(R.id.recycler_view_payment_detail);
        recyclerViewDeliveryAddress = (RecyclerView) findViewById(R.id.recycler_view_delivery_address);
        checkBox = (CheckBox) findViewById(R.id.check_box);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    recyclerViewDeliveryAddress.setAdapter(editDeliveryAddressAdapter);
                } else {
                    recyclerViewDeliveryAddress.setAdapter(selectDeliveryAddressAdapter);
                }
            }
        });
    }
}
