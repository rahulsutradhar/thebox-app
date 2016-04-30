package one.thebox.android.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import one.thebox.android.Models.AddressAndOrder;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.TimeSlotBottomSheet;
import one.thebox.android.adapter.EditTimeSlotAdapter;
import one.thebox.android.util.CoreGsonUtils;

public class ConfirmTimeSlotActivity extends BaseActivity {

    private ArrayList<AddressAndOrder> addressAndOrders;
    private CheckBox checkBox;
    private TextView proceedToPayment;
    private LinearLayout timeHolderLinearLayout;
    private RecyclerView recyclerView;
    public static final String EXTRA_ADDRESS_AND_ORDERS = "extra_address_and_orders";
    private boolean ordersHaveDifferentTime;
    private Date currentSelectedDate;
    private TextView timeSlotTextView;
    private EditTimeSlotAdapter editTimeSlotAdapter;
    private Date currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_time_slot);
        setTitle("Select Time Slots");
        initVariable();
        initViews();
    }

    private void initVariable() {
        addressAndOrders = CoreGsonUtils.fromJsontoArrayList(getIntent().getStringExtra(EXTRA_ADDRESS_AND_ORDERS), AddressAndOrder.class);
        currentDate = Calendar.getInstance().getTime();
        for (int i = 0; i < addressAndOrders.size(); i++) {
            addressAndOrders.get(i).setOderDate(currentDate);
        }
    }

    public static Intent newInstance(Context context, ArrayList<AddressAndOrder> addressAndOrders) {
        return new Intent(context, ConfirmTimeSlotActivity.class).putExtra(EXTRA_ADDRESS_AND_ORDERS, CoreGsonUtils.toJson(addressAndOrders));
    }

    private void initViews() {
        timeSlotTextView = (TextView) findViewById(R.id.time_slot_text_view);
        timeSlotTextView.setText(AddressAndOrder.getDateString(currentDate));
        timeHolderLinearLayout = (LinearLayout) findViewById(R.id.holder_time);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        checkBox = (CheckBox) findViewById(R.id.check_box);
        proceedToPayment = (TextView) findViewById(R.id.button_proceed_to_payment);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ordersHaveDifferentTime = isChecked;
                if (isChecked) {
                    recyclerView.setVisibility(View.VISIBLE);
                    timeHolderLinearLayout.setVisibility(View.GONE);
                    setupRecyclerView();
                } else {
                    recyclerView.setVisibility(View.GONE);
                    timeHolderLinearLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        timeHolderLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimeSlotBottomSheet(ConfirmTimeSlotActivity.this, Calendar.getInstance().getTime(), new TimeSlotBottomSheet.OnTimePicked() {
                    @Override
                    public void onTimePicked(Date date) {
                        currentSelectedDate = date;
                        timeSlotTextView.setText(AddressAndOrder.getDateString(date));
                        Toast.makeText(ConfirmTimeSlotActivity.this, date.toString(), Toast.LENGTH_SHORT).show();
                    }
                }).show();
            }
        });
        proceedToPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ordersHaveDifferentTime) {
                    startActivity(ConfirmPaymentDetailsActivity.getInstance(ConfirmTimeSlotActivity.this, editTimeSlotAdapter.getAddressAndOrders()));
                } else {
                    for (int i = 0; i < addressAndOrders.size(); i++) {
                        addressAndOrders.get(i).setOderDate(currentSelectedDate);
                    }
                    startActivity(ConfirmPaymentDetailsActivity.getInstance(ConfirmTimeSlotActivity.this, addressAndOrders));
                }
            }
        });
    }

    private void setupRecyclerView() {
        editTimeSlotAdapter = new EditTimeSlotAdapter(this);
        editTimeSlotAdapter.setAddressAndOrders(addressAndOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(editTimeSlotAdapter);
    }


}
