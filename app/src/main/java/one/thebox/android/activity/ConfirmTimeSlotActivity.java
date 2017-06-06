package one.thebox.android.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import one.thebox.android.Helpers.OrderHelper;
import one.thebox.android.Models.AddressAndOrder;
import one.thebox.android.Models.order.Order;
import one.thebox.android.Models.User;
import one.thebox.android.Models.address.Address;
import one.thebox.android.Models.timeslot.Slot;
import one.thebox.android.Models.timeslot.TimeSlot;
import one.thebox.android.Models.timeslot.TimeSlotInformation;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.BoxLoader;
import one.thebox.android.ViewHelper.TimeSlotBottomSheet;
import one.thebox.android.adapter.MergeOrderAdapter;
import one.thebox.android.adapter.timeslot.TimeSlotAdapter;
import one.thebox.android.api.RequestBodies.RescheduleRequestBody;
import one.thebox.android.api.Responses.RescheduleResponseBody;
import one.thebox.android.api.Responses.TimeSlotResponse;
import one.thebox.android.app.Constants;
import one.thebox.android.app.TheBox;
import one.thebox.android.util.CoreGsonUtils;
import one.thebox.android.util.DateTimeUtil;
import one.thebox.android.util.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmTimeSlotActivity extends BaseActivity {

    public static final String EXTRA_ADDRESS_AND_ORDERS = "extra_address_and_orders";
    private static final String IS_RESCHEDULING = "is_rescheduling";
    private static final String RESCHEDULE_ORDER_ID = "reschedule_order_id";
    boolean isCart = false;
    private ArrayList<AddressAndOrder> addressAndOrders;
    private RealmList<Order> mergeOrders;
    private TextView proceedToPayment;
    private LinearLayout timeHolderLinearLayout;
    private RelativeLayout layoutInformation;
    private Date currentSelectedDate;
    private TextView timeSlotTextView, textViewSelectDate, timeSlotInformationTitle;
    private Date nextSlotDate;
    private RecyclerView timeSlotRecyclerView;
    private Order currentSelectedOrder;
    private ImageView dropDownIcon;
    private MergeOrderAdapter mergeOrderAdapter;

    private ArrayList<TimeSlot> timeSlots;
    private ArrayList<Slot> slots;
    private TimeSlot selectedTimeSlot;
    private int selectedDatePosition;
    private Slot selectedSlot;
    private int selectedSlotPosition = -1;
    private TimeSlotAdapter timeSlotAdapter;
    private int orderId = 0;
    private TimeSlotInformation timeSlotInformation;

    private boolean isMerge;
    private Address address;
    private boolean isFromOrder;
    private boolean isReschedule;
    private Order order;
    private Order selectedMergeOrder;
    private ArrayList<Order> orders;


    /**
     * Refactor
     */
    public static Intent newInstance(Context context, boolean isMerge) {
        return new Intent(context, ConfirmTimeSlotActivity.class)
                .putExtra(Constants.EXTRA_IS_CART_MERGING, isMerge);
    }

    public static Intent newInstance(Context context, boolean isMerge, Address address) {
        return new Intent(context, ConfirmTimeSlotActivity.class)
                .putExtra(Constants.EXTRA_IS_CART_MERGING, isMerge)
                .putExtra(Constants.EXTRA_SELECTED_ADDRESS, CoreGsonUtils.toJson(address));
    }

    public static Intent newInstance(Context context, Order order, boolean isFromOrder, boolean isReschedule) {
        return new Intent(context, ConfirmTimeSlotActivity.class)
                .putExtra(Constants.EXTRA_ORDER, CoreGsonUtils.toJson(order))
                .putExtra(Constants.EXTRA_IS_FROM_ORDER, isFromOrder)
                .putExtra(Constants.EXTRA_IS_RESCHEDULE, isReschedule);
    }


    /**
     * Old
     */
    public static Intent newInstance(Context context, ArrayList<AddressAndOrder> addressAndOrders, boolean is_rescheduling) {
        return new Intent(context, ConfirmTimeSlotActivity.class)
                .putExtra(EXTRA_ADDRESS_AND_ORDERS, CoreGsonUtils.toJson(addressAndOrders))
                .putExtra(IS_RESCHEDULING, is_rescheduling);
    }

    public static Intent newInstance(Context context, ArrayList<AddressAndOrder> addressAndOrders, int orderId, boolean is_rescheduling) {
        return new Intent(context, ConfirmTimeSlotActivity.class)
                .putExtra(EXTRA_ADDRESS_AND_ORDERS, CoreGsonUtils.toJson(addressAndOrders))
                .putExtra(RESCHEDULE_ORDER_ID, orderId)
                .putExtra(IS_RESCHEDULING, is_rescheduling);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVariable();

        if (isFromOrder) {
            //reschedule
            if (isReschedule) {

            } else {
                //pay from orders; Arriving on
                setContentView(R.layout.activity_confirm_time_slot);
                setTitle("Select Time Slot");
                initViewsCasePayForOrder();
            }

        } else {
            if (isMerge) {
                //merge cart
                setContentView(R.layout.confirm_time_slot_when_user_have_orders);
                setTitle("Merge with future deliveries");
                initViewsCaseMergeDeliveries();
            } else {
                //Case First Order
                setContentView(R.layout.activity_confirm_time_slot);
                setTitle("Select Time Slot");
                initViewCaseFirstOrder();
            }
        }

        /*if (is_rescheduling == true) {
            setContentView(R.layout.activity_confirm_time_slot);
            setTitle("Reschedule Order");
            initViewCase4();
        } else {
            if (hasPreviousOrder() && isCart()) {
                setContentView(R.layout.confirm_time_slot_when_user_have_orders);
                setTitle("Merge with future deliveries");
                initViewsCaseMergeDeliveries();
                setupMergeDeliveryRecyclerView();

            } else if (!hasPreviousOrder() && isCart()) {
                setContentView(R.layout.activity_confirm_time_slot);
                setTitle("Select Time Slot");
                initViewCase2();
            } else {
                //pay from orders; Arriving on
                setContentView(R.layout.activity_confirm_time_slot);
                setTitle("Select Time Slot");
                initViewsCasePayForOrder();
            }
        }*/
    }


    /**
     * Order placing First time; Or Pay from cart
     */
    public void initViewCaseFirstOrder() {
        layoutInformation = (RelativeLayout) findViewById(R.id.information);
        textViewSelectDate = (TextView) findViewById(R.id.text_view_select_date);
        dropDownIcon = (ImageView) findViewById(R.id.drop_down_icon);
        timeHolderLinearLayout = (LinearLayout) findViewById(R.id.holder_time);
        timeSlotTextView = (TextView) findViewById(R.id.time_slot_text_view);
        timeSlotRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_time_slots);
        timeSlotInformationTitle = (TextView) findViewById(R.id.timeslotInformationText);
        proceedToPayment = (TextView) findViewById(R.id.button_proceed_to_payment);

        //Fetch Time slot data
        fetchGeneralTimeSlot();

        timeHolderLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showBottomSheet();
                if (timeSlots != null) {
                    showDateBottomSheet();
                }
            }
        });

        proceedToPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectedSlot != null) {
                    /**
                     * Save Clever Tap Event; TimeSlotsFromCart
                     */
                    /*setCleverTapEventTimeSlotsFromCart();*/
                    startActivity(ConfirmPaymentDetailsActivity.getInstance(ConfirmTimeSlotActivity.this, isMerge, address, selectedSlot.getTimestamp()));
                }
            }
        });

    }


    /**
     * Case: Merge with Deliveries
     */
    private void initViewsCaseMergeDeliveries() {
        timeSlotRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_time_slots);
        proceedToPayment = (TextView) findViewById(R.id.button_proceed_to_payment);
        proceedToPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if (selectedMergeOrder == null) {
                        selectedMergeOrder = orders.get(0);
                    }
                    startActivity(ConfirmPaymentDetailsActivity.getInstance(ConfirmTimeSlotActivity.this, isMerge, address, selectedMergeOrder.getUuid()));

                } catch (Exception e) {
                    e.printStackTrace();
                }

                /**
                 * Save CleverTapEvent; TimeSlotMergeWithDeliveries
                 */
                /*setCleverTapEventTimeSlotsMergeWithDeliveries(mergeOrderAdapter.getOrders().get(mergeOrderAdapter.getCurrentSelection()).getId());

                startActivity(ConfirmPaymentDetailsActivity.getInstance(ConfirmTimeSlotActivity.this,
                        addressAndOrders,
                        mergeOrderAdapter.getOrders().get(mergeOrderAdapter.getCurrentSelection()).getId(), true));*/
            }
        });

        //fetch time slots when you have orders
        fetchTimeSlotForMergeDeliveries();
    }

    /**
     * API request for Merge Timeslot
     */
    public void fetchTimeSlotForMergeDeliveries() {
        final BoxLoader dialog = new BoxLoader(this).show();

        TheBox.getAPIService()
                .getMergeTimeSlot(PrefUtils.getToken(this))
                .enqueue(new Callback<TimeSlotResponse>() {
                    @Override
                    public void onResponse(Call<TimeSlotResponse> call, Response<TimeSlotResponse> response) {
                        dialog.dismiss();
                        try {
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    orders = response.body().getOrders();
                                    if (orders != null) {
                                        setupMergeDeliveryRecyclerView(orders);
                                    }
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<TimeSlotResponse> call, Throwable t) {

                    }
                });

    }

    /**
     * Setup Merge with deliveries Recyclerview
     */
    public void setupMergeDeliveryRecyclerView(ArrayList<Order> orders) {
        mergeOrderAdapter = new MergeOrderAdapter(this, this, orders);
        timeSlotRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        timeSlotRecyclerView.setAdapter(mergeOrderAdapter);
    }


    /**
     * Case: Pay from Order
     */
    public void initViewsCasePayForOrder() {
        layoutInformation = (RelativeLayout) findViewById(R.id.information);
        textViewSelectDate = (TextView) findViewById(R.id.text_view_select_date);
        dropDownIcon = (ImageView) findViewById(R.id.drop_down_icon);
        timeHolderLinearLayout = (LinearLayout) findViewById(R.id.holder_time);
        timeSlotTextView = (TextView) findViewById(R.id.time_slot_text_view);
        timeSlotInformationTitle = (TextView) findViewById(R.id.timeslotInformationText);

        timeHolderLinearLayout.setOnClickListener(null);
        dropDownIcon.setVisibility(View.GONE);
        textViewSelectDate.setText("Arriving on:");
        timeSlotRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_time_slots);
        proceedToPayment = (TextView) findViewById(R.id.button_proceed_to_payment);

        //fetch slots for orders
        fetchTimeSlotForOrder();

        proceedToPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if (selectedSlot != null) {
                        Toast.makeText(TheBox.getAppContext(), "Amount - " + order.getAmountToPay(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ConfirmTimeSlotActivity.this, PaymentOptionActivity.class);
                        intent.putExtra(Constants.EXTRA_ORDER, CoreGsonUtils.toJson(order));
                        intent.putExtra(Constants.EXTRA_TIMESLOT_SELECTED, selectedSlot.getTimestamp());
                        intent.putExtra(Constants.EXTRA_IS_FROM_ORDER, true);
                        intent.putExtra(Constants.EXTRA_AMOUNT_TO_PAY, String.valueOf(order.getAmountToPay()));
                        startActivity(intent);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Case; Reschedule Order
     */
    public void initViewCase4() {
        orderId = getIntent().getIntExtra(RESCHEDULE_ORDER_ID, 0);
        layoutInformation = (RelativeLayout) findViewById(R.id.information);
        textViewSelectDate = (TextView) findViewById(R.id.text_view_select_date);
        dropDownIcon = (ImageView) findViewById(R.id.drop_down_icon);
        timeHolderLinearLayout = (LinearLayout) findViewById(R.id.holder_time);
        timeSlotTextView = (TextView) findViewById(R.id.time_slot_text_view);
        timeSlotInformationTitle = (TextView) findViewById(R.id.timeslotInformationText);
        timeSlotRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_time_slots);
        proceedToPayment = (TextView) findViewById(R.id.button_proceed_to_payment);
        proceedToPayment.setText("Reschedule");

        timeHolderLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timeSlots != null) {
                    showDateBottomSheet();
                }
            }
        });

        //fetch time slot data
        fetchRescheduleTimeSlot();

        proceedToPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedSlot != null) {
                    //api call for rescheduling
                    rescheduleOrder();
                    /**
                     * save CleverTap Event; TimeSlotsRescheduleOrder
                     */
                    setCleverTapEventTimeSlotsRescheduleOrder();
                }
            }
        });
    }


    private void initMergeOrders() {
        mergeOrders = new RealmList<>();
        Realm realm = TheBox.getRealm();
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

    public void setCart(boolean cart) {
        isCart = cart;
    }

    private boolean isCart() {
        return isCart;
    }

    /**
     * Get Time Slots
     */
    public void fetchGeneralTimeSlot() {
        final BoxLoader dialog = new BoxLoader(this).show();

        TheBox.getAPIService().getTimeSlots(PrefUtils.getToken(this))
                .enqueue(new Callback<TimeSlotResponse>() {
                    @Override
                    public void onResponse(Call<TimeSlotResponse> call, Response<TimeSlotResponse> response) {
                        try {
                            dialog.dismiss();
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    timeSlotInformation = response.body().getTimeSlotInformation();
                                    //set the initial data for time slot
                                    setInitialSlotData(response.body().getData());
                                    setTimeSlotInformation();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<TimeSlotResponse> call, Throwable t) {
                        dialog.dismiss();

                    }
                });
    }

    /**
     * Set Timeslot Initial Data
     */
    public void setInitialSlotData(ArrayList<TimeSlot> timeSlots) {
        try {
            this.timeSlots = timeSlots;
            this.selectedDatePosition = 0;
            if (timeSlots != null) {
                selectedTimeSlot = timeSlots.get(selectedDatePosition);
                timeSlotTextView.setText(selectedTimeSlot.getDate());

                //set Initial data for slots
                setInitialTimeSlotData(selectedTimeSlot.getSlots());
            }

        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
    }

    /**
     * Set Initial Slot data for Order
     */
    public void setInitialSlotDataForOrder(String date, ArrayList<Slot> slots) {
        try {
            if (date != null) {
                timeSlotTextView.setText(date);

                //set Initial dat for slots
                setInitialTimeSlotData(slots);
            }

        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }

    }

    /**
     * List ot show TimeSLot - dates
     */
    public void showDateBottomSheet() {
        new TimeSlotBottomSheet(ConfirmTimeSlotActivity.this, timeSlots, selectedDatePosition, new TimeSlotBottomSheet.OnDatePicked() {
            @Override
            public void onDatePicked(TimeSlot timeSlot, int position) {

                selectedTimeSlot = timeSlot;
                selectedDatePosition = position;
                timeSlotTextView.setText(timeSlot.getDate());

                //set time slot recyclerview on date slected
                setInitialTimeSlotData(timeSlot.getSlots());
            }
        }).showDateSlotBottomSheet();
    }

    /**
     * Set Initial Data for Slot Time
     */
    public void setInitialTimeSlotData(ArrayList<Slot> slots) {
        try {
            if (slots != null) {
                if (slots.size() > 0) {
                    this.slots = slots;
                    if (selectedSlotPosition == -1) {
                        this.selectedSlotPosition = 0;
                    } else if (selectedSlotPosition >= slots.size()) {
                        this.selectedSlotPosition = 0;
                    }
                    //this is the latest selected time slot
                    this.selectedSlot = slots.get(selectedSlotPosition);

                    setUpSlotRecyclerview();
                }
            }

        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
    }

    /**
     * SetUpSlot Recyclerview
     */
    public void setUpSlotRecyclerview() {
        if (timeSlotAdapter == null || null == timeSlotRecyclerView.getAdapter()) {
            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            timeSlotRecyclerView.setLayoutManager(linearLayoutManager);
            timeSlotAdapter = new TimeSlotAdapter(this, new TimeSlotAdapter.OnTimeSlotSelected() {
                @Override
                public void onTimeSlotSelected(Slot slot, int position) {
                    selectedSlot = slot;
                    selectedSlotPosition = position;
                }
            });
            timeSlotAdapter.setSlots(slots);
            timeSlotAdapter.setSelectedSlotPosition(selectedSlotPosition);
            timeSlotRecyclerView.setAdapter(timeSlotAdapter);

        } else {
            timeSlotAdapter.setSlots(slots);
            timeSlotAdapter.setSelectedSlotPosition(selectedSlotPosition);
        }
    }

    /**
     * Fetch Reschedule Time Slot
     */
    public void fetchRescheduleTimeSlot() {
        final BoxLoader dialog = new BoxLoader(this).show();

        TheBox.getAPIService().getRescheduleTimeSlot(orderId, "reschedule")
                .enqueue(new Callback<TimeSlotResponse>() {
                    @Override
                    public void onResponse(Call<TimeSlotResponse> call, Response<TimeSlotResponse> response) {
                        try {
                            dialog.dismiss();
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    timeSlotInformation = response.body().getTimeSlotInformation();

                                    //set the initial data for time slot
                                    setInitialSlotData(response.body().getData());
                                    setTimeSlotInformation();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<TimeSlotResponse> call, Throwable t) {
                        dialog.dismiss();

                    }
                });
    }

    /**
     * Reschedule API
     */
    public void rescheduleOrder() {
        final BoxLoader dialog = new BoxLoader(this).show();
        TheBox.getAPIService().reschedulethisOrder(PrefUtils.getToken(this), new RescheduleRequestBody(selectedSlot.getTimestamp(), orderId))
                .enqueue(new Callback<RescheduleResponseBody>() {
                    @Override
                    public void onResponse(Call<RescheduleResponseBody> call, Response<RescheduleResponseBody> response) {
                        dialog.dismiss();
                        if (response.body() != null) {
                            if (response.body().isSuccess()) {

                                Toast.makeText(ConfirmTimeSlotActivity.this, response.body().getInfo(), Toast.LENGTH_SHORT).show();
                                OrderHelper.addAndNotify(response.body().getOrders());
                                startActivity(new Intent(ConfirmTimeSlotActivity.this, MainActivity.class).putExtra(MainActivity.EXTRA_ATTACH_FRAGMENT_NO, 2));
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

    /**
     * Time Slots for particular order
     */
    public void fetchTimeSlotForOrder() {
        final BoxLoader dialog = new BoxLoader(this).show();
        TheBox.getAPIService()
                .getTimeSlotForOrder(PrefUtils.getToken(this), order.getUuid())
                .enqueue(new Callback<TimeSlotResponse>() {
                    @Override
                    public void onResponse(Call<TimeSlotResponse> call, Response<TimeSlotResponse> response) {
                        try {
                            dialog.dismiss();
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    timeSlotInformation = response.body().getTimeSlotInformation();
                                    //set Initial data for slots
                                    setInitialSlotDataForOrder(response.body().getDate(), response.body().getSlots());

                                    setTimeSlotInformation();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<TimeSlotResponse> call, Throwable t) {
                        dialog.dismiss();
                    }
                });

    }


    /**
     * Initialize values
     */
    private void initVariable() {
        try {

            isMerge = getIntent().getBooleanExtra(Constants.EXTRA_IS_CART_MERGING, false);
            address = CoreGsonUtils.fromJson(getIntent().getStringExtra(Constants.EXTRA_SELECTED_ADDRESS), Address.class);
            order = CoreGsonUtils.fromJson(getIntent().getStringExtra(Constants.EXTRA_ORDER), Order.class);
            isReschedule = getIntent().getBooleanExtra(Constants.EXTRA_IS_RESCHEDULE, false);
            isFromOrder = getIntent().getBooleanExtra(Constants.EXTRA_IS_FROM_ORDER, false);

            if (isMerge) {
                //get the selected Address
                User user = PrefUtils.getUser(this);
                if (user.getAddresses() != null) {
                    address = user.getAddresses().first();
                }
            }
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }


    public void setTimeSlotInformation() {
        if (timeSlotInformation != null) {
            layoutInformation.setVisibility(View.VISIBLE);
            timeSlotInformationTitle.setText(timeSlotInformation.getTitle());
            layoutInformation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setUpInformationDialog();
                }
            });
        } else {
            layoutInformation.setOnClickListener(null);
            layoutInformation.setVisibility(View.GONE);

        }
    }

    public void setUpInformationDialog() {
        try {
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCanceledOnTouchOutside(true);
            dialog.setContentView(R.layout.dialog_common_details);
            dialog.getWindow().getAttributes().width = LinearLayout.LayoutParams.FILL_PARENT;
            dialog.show();

            TextView header = (TextView) dialog.findViewById(R.id.header_title);
            TextView content = (TextView) dialog.findViewById(R.id.text_content);
            TextView okayButtonText = (TextView) dialog.findViewById(R.id.okay);
            RelativeLayout okayButton = (RelativeLayout) dialog.findViewById(R.id.holder_okay_button);

            header.setText(Html.fromHtml(timeSlotInformation.getPopupTitle()));
            content.setText(Html.fromHtml(timeSlotInformation.getContent()));
            okayButtonText.setText("OK");

            okayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Date getNextSlotDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 1);
        return c.getTime();
    }

    /**
     * Clever Tap Events
     * <p>
     * TimeSlot from Cart
     */
    public void setCleverTapEventTimeSlotsFromCart() {
        try {
            HashMap<String, Object> objectHashMap = new HashMap<>();
            if (addressAndOrders != null) {
                if (addressAndOrders.size() > 0) {
                    objectHashMap.put("order_id", addressAndOrders.get(0).getOrderId());
                }
            }
            objectHashMap.put("order_date", selectedTimeSlot.getDate());
            objectHashMap.put("order_time_slot", selectedSlot.getName());
            objectHashMap.put("order_time_stamp", selectedSlot.getTimestamp());

            TheBox.getCleverTap().event.push("time_slots_from_cart", objectHashMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCleverTapEventTimeSlotsRescheduleOrder() {
        try {
            HashMap<String, Object> objectHashMap = new HashMap<>();
            objectHashMap.put("order_id", orderId);
            objectHashMap.put("order_date", selectedTimeSlot.getDate());
            objectHashMap.put("order_time_slot", selectedSlot.getName());
            objectHashMap.put("order_time_stamp", selectedSlot.getTimestamp());

            TheBox.getCleverTap().event.push("time_slots_reschedule_order", objectHashMap);
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
    }

    public void setCleverTapEventTimeSlotsMergeWithDeliveries(int mergeOrderId) {
        HashMap<String, Object> objectHashMap = new HashMap<>();
        objectHashMap.put("merge_order_id", mergeOrderId);
        if (addressAndOrders != null) {
            if (addressAndOrders.size() > 0) {
                objectHashMap.put("order_date", addressAndOrders.get(0).getDateString());
            }
        }
        TheBox.getCleverTap().event.push("time_slots_merge_with_deliveries", objectHashMap);
    }

    public Order getSelectedMergeOrder() {
        return selectedMergeOrder;
    }

    public void setSelectedMergeOrder(Order selectedMergeOrder) {
        this.selectedMergeOrder = selectedMergeOrder;
    }
}
