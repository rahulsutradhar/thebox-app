package one.thebox.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import one.thebox.android.Models.AddressAndOrder;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.TimeSlotBottomSheet;
import one.thebox.android.adapter.EditTimeSlotAdapter;
import one.thebox.android.util.Constants;
import one.thebox.android.util.CoreGsonUtils;
import one.thebox.android.util.DateTimeUtil;

public class ConfirmTimeSlotActivity extends BaseActivity {

    public static final String EXTRA_ADDRESS_AND_ORDERS = "extra_address_and_orders";
    boolean isCart;
    private ArrayList<AddressAndOrder> addressAndOrders;
    private CheckBox checkBox;
    private TextView proceedToPayment;
    private LinearLayout timeHolderLinearLayout;
    private RecyclerView recyclerView;
    private boolean ordersHaveDifferentTime;
    private Date currentSelectedDate;
    private TextView timeSlotTextView;
    private EditTimeSlotAdapter editTimeSlotAdapter;
    private Date nextSlotDate;

    public static Intent newInstance(Context context, ArrayList<AddressAndOrder> addressAndOrders) {
        return new Intent(context, ConfirmTimeSlotActivity.class).putExtra(EXTRA_ADDRESS_AND_ORDERS, CoreGsonUtils.toJson(addressAndOrders));
    }

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
        nextSlotDate = getNextSlotDate(Calendar.getInstance().getTime());
        for (int i = 0; i < addressAndOrders.size(); i++) {
            addressAndOrders.get(i).setOderDate(nextSlotDate);
            if (addressAndOrders.get(i).getOrder().isCart()) {
                isCart = true;
            }
        }
    }

    private void initViews() {
        timeSlotTextView = (TextView) findViewById(R.id.time_slot_text_view);
        timeSlotTextView.setText(AddressAndOrder.getDateString(nextSlotDate));

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
        if (isCart) {
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
        } else {
            timeHolderLinearLayout.setOnClickListener(null);
        }

        proceedToPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ordersHaveDifferentTime) {
                    startActivity(ConfirmPaymentDetailsActivity.getInstance(ConfirmTimeSlotActivity.this, editTimeSlotAdapter.getAddressAndOrders()));
                } else {
                    startActivity(ConfirmPaymentDetailsActivity.getInstance(ConfirmTimeSlotActivity.this, addressAndOrders));
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setupRecyclerView() {
        editTimeSlotAdapter = new EditTimeSlotAdapter(this, isCart);
        editTimeSlotAdapter.setAddressAndOrders(addressAndOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(editTimeSlotAdapter);
    }

    public Date getNextSlotDate(Date date) {
        for (int i = 0; i < Constants.DATE_RANGE.length - 1; i++) {
            String hourStart = Constants.DATE_RANGE[i].substring(0, 2);
            String minuteStart = Constants.DATE_RANGE[i].substring(3, 5);
            String am_pm_start = Constants.DATE_RANGE[i].substring(5, 6);
            Date dateStart = setHourAndMinuteToCurrentDate(hourStart, minuteStart, am_pm_start);
            String hourEnd = Constants.DATE_RANGE[i + 1].substring(0, 2);
            String minuteEnd = Constants.DATE_RANGE[i + 1].substring(3, 5);
            String am_pm_end = Constants.DATE_RANGE[i + 1].substring(5, 6);
            Date dateEnd = setHourAndMinuteToCurrentDate(hourEnd, minuteEnd, am_pm_start);
            // i.e last time slot then return next day first slot.
            if (i == Constants.DATE_RANGE.length - 2) {
                hourStart = Constants.DATE_RANGE[i].substring(0, 2);
                minuteStart = Constants.DATE_RANGE[i].substring(3, 5);
                am_pm_start = Constants.DATE_RANGE[i].substring(5, 6);
                Date nextDate = setHourAndMinuteToCurrentDate(hourStart, minuteStart, am_pm_start);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(nextDate);
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                return calendar.getTime();
            }
            if (DateTimeUtil.checkIsLieBetweenDates(date, dateStart, dateEnd)) {
                return dateEnd;
            }
        }
        return null;
    }

    public Date setHourAndMinuteToCurrentDate(String hour, String minute, String am_pm) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, Integer.parseInt(hour));
        calendar.set(Calendar.MINUTE, Integer.parseInt(minute));
        if (am_pm.equals("A")) {
            calendar.set(Calendar.AM_PM, Calendar.AM);
        } else {
            calendar.set(Calendar.AM_PM, Calendar.PM);
        }
        return calendar.getTime();
    }
}
