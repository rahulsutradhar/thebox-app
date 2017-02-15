package one.thebox.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import one.thebox.android.Helpers.CartHelper;
import one.thebox.android.Helpers.OrderHelper;
import one.thebox.android.Models.AddressAndOrder;
import one.thebox.android.Models.Order;
import one.thebox.android.R;
import one.thebox.android.Services.UpdateOrderService;
import one.thebox.android.ViewHelper.BoxLoader;
import one.thebox.android.ViewHelper.TimeSlotBottomSheet;
import one.thebox.android.adapter.MergeOrderAdapter;
import one.thebox.android.adapter.TimeSlotAdapter;
import one.thebox.android.api.RequestBodies.PaymentRequestBody;
import one.thebox.android.api.RequestBodies.RescheduleRequestBody;
import one.thebox.android.api.Responses.PaymentResponse;
import one.thebox.android.api.Responses.RescheduleResponseBody;
import one.thebox.android.app.MyApplication;
import one.thebox.android.util.Constants;
import one.thebox.android.util.CoreGsonUtils;
import one.thebox.android.util.DateTimeUtil;
import one.thebox.android.util.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmTimeSlotActivity extends BaseActivity {

    public static final String EXTRA_ADDRESS_AND_ORDERS = "extra_address_and_orders";
    private static final String IS_RESCHEDULING = "is_rescheduling";
    boolean isCart;
    private ArrayList<AddressAndOrder> addressAndOrders;
    private RealmList<Order> mergeOrders;
    private TextView proceedToPayment;
    private LinearLayout timeHolderLinearLayout;
    private Date currentSelectedDate;
    private TextView timeSlotTextView, textViewSelectDate;
    private Date nextSlotDate;
    private RecyclerView timeSlotRecyclerView;
    private TimeSlotAdapter timeSlotAdapter;
    private Order currentSelectedOrder;
    private ImageView dropDownIcon;
    private MergeOrderAdapter mergeOrderAdapter;
    private boolean is_rescheduling = false;

    public static Intent newInstance(Context context, ArrayList<AddressAndOrder> addressAndOrders, boolean is_rescheduling) {
        return new Intent(context, ConfirmTimeSlotActivity.class)
                .putExtra(EXTRA_ADDRESS_AND_ORDERS, CoreGsonUtils.toJson(addressAndOrders))
                .putExtra(IS_RESCHEDULING, is_rescheduling);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initMergeOrders();
        initVariable();

        if (is_rescheduling == true) {
            setContentView(R.layout.activity_confirm_time_slot);
            setTitle("Reschedule Order");
            initViewCase4();
            setupTimeSlotRecyclerView();
        } else {
            if (hasPreviousOrder() && isCart()) {
                setContentView(R.layout.confirm_time_slot_when_user_have_orders);
                setTitle("Merge with future deliveries");
                initViewsCase1();
                setupMergeDeliveryRecyclerView();
            } else if (!hasPreviousOrder() && isCart()) {
                setContentView(R.layout.activity_confirm_time_slot);
                setTitle("Select Time Slot");
                initViewCase2();
                setupTimeSlotRecyclerView();
            } else {
                setContentView(R.layout.activity_confirm_time_slot);
                setTitle("Select Time Slot");
                initViewsCase3();
                setupTimeSlotRecyclerView();
            }
        }
    }

    private void initViewsCase1() {
        timeSlotRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_time_slots);
        proceedToPayment = (TextView) findViewById(R.id.button_proceed_to_payment);
        proceedToPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addressAndOrders.size() == 2) {
                    addressAndOrders.set(1, new AddressAndOrder(addressAndOrders.get(0).getAddressId(), mergeOrderAdapter.getOrders().get(mergeOrderAdapter.getCurrentSelection()).getId()));
                } else {
                    addressAndOrders.add(new AddressAndOrder(addressAndOrders.get(0).getAddressId(), mergeOrderAdapter.getOrders().get(mergeOrderAdapter.getCurrentSelection()).getId()));
                }
                startActivity(ConfirmPaymentDetailsActivity.getInstance(ConfirmTimeSlotActivity.this,
                        addressAndOrders,
                        mergeOrderAdapter.getOrders().get(mergeOrderAdapter.getCurrentSelection()).getId()));
            }
        });
    }

    public void initViewCase2() {
        textViewSelectDate = (TextView) findViewById(R.id.text_view_select_date);
        dropDownIcon = (ImageView) findViewById(R.id.drop_down_icon);
        timeHolderLinearLayout = (LinearLayout) findViewById(R.id.holder_time);
        timeSlotTextView = (TextView) findViewById(R.id.time_slot_text_view);
        timeSlotTextView.setText(AddressAndOrder.getDateStringWithoutSlot(currentSelectedDate));

      /*  Log.i("Time Slot Confirm", "" + AddressAndOrder.getDateStringWithoutSlot(currentSelectedDate));*/
        timeHolderLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheet();
            }
        });
        timeSlotRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_time_slots);
        proceedToPayment = (TextView) findViewById(R.id.button_proceed_to_payment);
        proceedToPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date orderDate = getDateWithTimeSlot(currentSelectedDate,
                        timeSlotAdapter.getTimeStrings().get(timeSlotAdapter.getCurrentSelection()));
                for (int i = 0; i < addressAndOrders.size(); i++) {
                    addressAndOrders.get(i).setOderDate(orderDate);
                }
                startActivity(ConfirmPaymentDetailsActivity.getInstance(ConfirmTimeSlotActivity.this, addressAndOrders));
            }
        });
    }

    public void initViewsCase3() {
        textViewSelectDate = (TextView) findViewById(R.id.text_view_select_date);
        dropDownIcon = (ImageView) findViewById(R.id.drop_down_icon);
        timeHolderLinearLayout = (LinearLayout) findViewById(R.id.holder_time);
        timeSlotTextView = (TextView) findViewById(R.id.time_slot_text_view);
        currentSelectedDate = DateTimeUtil.convertStringToDate(addressAndOrders.get(0).getOrder().getDeliveryScheduleAt());
        timeSlotTextView.setText(AddressAndOrder.getDateStringWithoutSlot(currentSelectedDate));
        timeHolderLinearLayout.setOnClickListener(null);
        timeSlotTextView = (TextView) findViewById(R.id.time_slot_text_view);
        dropDownIcon.setVisibility(View.GONE);
        textViewSelectDate.setText("Arriving on:");
        timeSlotRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_time_slots);
        proceedToPayment = (TextView) findViewById(R.id.button_proceed_to_payment);
        proceedToPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date orderDate = getDateWithTimeSlot(currentSelectedDate,
                        timeSlotAdapter.getTimeStrings().get(timeSlotAdapter.getCurrentSelection()));
                for (int i = 0; i < addressAndOrders.size(); i++) {
                    addressAndOrders.get(i).setOderDate(orderDate);
                }
                startActivity(ConfirmPaymentDetailsActivity.getInstance(ConfirmTimeSlotActivity.this, addressAndOrders));
            }
        });
    }

    public void initViewCase4() {
        textViewSelectDate = (TextView) findViewById(R.id.text_view_select_date);
        dropDownIcon = (ImageView) findViewById(R.id.drop_down_icon);
        timeHolderLinearLayout = (LinearLayout) findViewById(R.id.holder_time);
        timeSlotTextView = (TextView) findViewById(R.id.time_slot_text_view);
        timeSlotTextView.setText(AddressAndOrder.getDateStringWithoutSlot(currentSelectedDate));
        timeHolderLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheet();
            }
        });
        timeSlotRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_time_slots);
        proceedToPayment = (TextView) findViewById(R.id.button_proceed_to_payment);
        proceedToPayment.setText("Reschedule");
        proceedToPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date reschedule_to = getDateWithTimeSlot(currentSelectedDate,
                        timeSlotAdapter.getTimeStrings().get(timeSlotAdapter.getCurrentSelection()));

                reSchedule(reschedule_to);
            }
        });
    }

    private void showBottomSheet() {
        new TimeSlotBottomSheet(ConfirmTimeSlotActivity.this, nextSlotDate, currentSelectedDate, new TimeSlotBottomSheet.OnTimePicked() {
            @Override
            public void onTimePicked(Date date, Order order) {
                currentSelectedDate = date;
                timeSlotTextView.setText(AddressAndOrder.getDateStringWithoutSlot(date));
            }
        }).showTimeSlotBottomSheet();
    }


    public void setupMergeDeliveryRecyclerView() {
        mergeOrderAdapter = new MergeOrderAdapter(this, mergeOrders);
        ArrayList<String> timeSlotArrayList = new ArrayList<>();
        timeSlotRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        timeSlotRecyclerView.setAdapter(mergeOrderAdapter);
    }

    private void initMergeOrders() {
        mergeOrders = new RealmList<>();
        Realm realm = MyApplication.getRealm();
        RealmQuery<Order> query = realm.where(Order.class);
        RealmResults<Order> realmResults = query.notEqualTo(Order.FIELD_ID, 0).equalTo(Order.FIELD_IS_CART, false).findAll();
        for (int i = 0; i < realmResults.size(); i++) {
            if (Calendar.getInstance().getTime()
                    .compareTo
                            (DateTimeUtil.convertStringToDate(realmResults.get(i).getDeliveryScheduleAt())) == -1) {
                mergeOrders.add(realmResults.get(i));
            }
        }


        if (mergeOrders != null && !mergeOrders.isEmpty()) {
            currentSelectedOrder = mergeOrders.get(0);
        }
    }

    private boolean hasPreviousOrder() {
        return !(mergeOrders == null || mergeOrders.isEmpty());
    }

    private boolean isCart() {
        return addressAndOrders.get(0).getOrder().isCart();
    }

    public void reSchedule(Date reschedule_to) {
        final BoxLoader dialog = new BoxLoader(this).show();
        MyApplication.getAPIService().reschedulethisOrder(PrefUtils.getToken(this), new RescheduleRequestBody(reschedule_to))
                .enqueue(new Callback<RescheduleResponseBody>() {
                    @Override
                    public void onResponse(Call<RescheduleResponseBody> call, Response<RescheduleResponseBody> response) {
                        dialog.dismiss();
                        if (response.body() != null) {
                            if (response.body().isSuccess()) {

                                Toast.makeText(ConfirmTimeSlotActivity.this, response.body().getInfo(), Toast.LENGTH_SHORT).show();

                                OrderHelper.addAndNotify(response.body().getOrders());

                                startActivity(new Intent(ConfirmTimeSlotActivity.this, MainActivity.class).putExtra(MainActivity.EXTRA_ATTACH_FRAGMENT_NO, 1));

                                finish();
                            } else {
                                Toast.makeText(ConfirmTimeSlotActivity.this, response.body().getInfo(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<RescheduleResponseBody> call, Throwable t) {
                        dialog.dismiss();
                    }
                });

    }

    private void setupTimeSlotRecyclerView() {
        timeSlotAdapter = new TimeSlotAdapter(this, new TimeSlotAdapter.OnTimeSlotSelected() {
            @Override
            public void onTimeSlotSelected(String timeSlot) {
                currentSelectedDate = getDateWithTimeSlot(currentSelectedDate, timeSlot);
            }
        });
        ArrayList<String> timeSlotArrayList = new ArrayList<>();
        for (int i = 0; i < Constants.DATE_RANGE.length; i++) {
            timeSlotArrayList.add(Constants.DATE_RANGE[i]);
        }
        timeSlotAdapter.setTimeStrings(timeSlotArrayList);
        timeSlotRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        timeSlotRecyclerView.setAdapter(timeSlotAdapter);
    }

    private void initVariable() {
        addressAndOrders = CoreGsonUtils.fromJsontoArrayList(getIntent().getStringExtra(EXTRA_ADDRESS_AND_ORDERS), AddressAndOrder.class);

        if (getIntent().getBooleanExtra(IS_RESCHEDULING, false)) {
            is_rescheduling = true;
            nextSlotDate = getNextSlotDate(DateTimeUtil.convertStringToDate(addressAndOrders.get(0).getOrder().getDeliveryScheduleAt()));
        } else {
            nextSlotDate = getNextSlotDate(Calendar.getInstance().getTime());
        }


        currentSelectedDate = nextSlotDate;
        for (int i = 0; i < addressAndOrders.size(); i++) {
            addressAndOrders.get(i).setOderDate(nextSlotDate);
            if (addressAndOrders.get(i).getOrder().isCart()) {
                isCart = true;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public Date getNextSlotDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 1);
        return c.getTime();
    }

    public Date setHourAndMinuteToCurrentDate(String hour, String minute, String am_pm) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentSelectedDate);
        calendar.set(Calendar.HOUR, Integer.parseInt(hour));
        calendar.set(Calendar.MINUTE, Integer.parseInt(minute));
        if (am_pm.equals("A")) {
            calendar.set(Calendar.AM_PM, Calendar.AM);
        } else {
            calendar.set(Calendar.AM_PM, Calendar.PM);
        }
        return calendar.getTime();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    public Date getDateWithTimeSlot(Date date, String timeSlot) {
        int hour = Integer.parseInt(timeSlot.substring(0, 2));
        int minute = Integer.parseInt(timeSlot.substring(3, 5));
        String am_pm = timeSlot.substring(6, 7);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, date.getYear() + 1900);
        cal.set(Calendar.MONTH, date.getMonth());
        cal.set(Calendar.DAY_OF_MONTH, date.getDate());
        //Date added_date = cal.getTime();
        if (am_pm.equals("A")) {
            //cal.set(Calendar.AM_PM, Calendar.AM);
            cal.set(Calendar.HOUR_OF_DAY, hour);
        } else {
            if (hour == 12) {
                cal.set(Calendar.HOUR_OF_DAY, hour);
            } else {
                Integer hr_format_hour = hour + 12;
                cal.set(Calendar.HOUR_OF_DAY, hr_format_hour);
            }
            //cal.set(Calendar.AM_PM, Calendar.PM);
        }


        //Date added_hour = cal.getTime();
        cal.set(Calendar.MINUTE, minute);
        //Date added_minute = cal.getTime();

        return cal.getTime();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
