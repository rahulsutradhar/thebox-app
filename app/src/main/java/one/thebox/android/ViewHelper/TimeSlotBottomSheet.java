package one.thebox.android.ViewHelper;

import android.app.Activity;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.realm.RealmList;
import one.thebox.android.Models.Order;
import one.thebox.android.R;
import one.thebox.android.adapter.base.BaseRecyclerAdapter;
import one.thebox.android.adapter.OrdersItemAdapter;
import one.thebox.android.adapter.TimeSlotAdapter;
import one.thebox.android.util.Constants;
import one.thebox.android.util.DateTimeUtil;

/**
 * Created by Ajeet Kumar Meena on 29-04-2016.
 */
public class TimeSlotBottomSheet {

    private Activity context;
    private BottomSheetDialog bottomSheetDialog;
    private View bottomSheet;
    private OnTimePicked onTimePicked;
    private RecyclerView recyclerViewDay;
    private ArrayList<String> timeSlots = new ArrayList<>();
    private ArrayList<String> daySlots = new ArrayList<>();
    private TimeSlotAdapter timeSlotAdapterDay;
    private int currentSelectedPosition;
    private Date currentSelectedDate;
    private Date endDate = null;
    private Date startDate = null;

    public TimeSlotBottomSheet(Activity context, Date startDate, Date currentSelectedDate, OnTimePicked onTimePicked) {
        this.context = context;
        this.onTimePicked = onTimePicked;
        this.startDate = startDate;
        this.currentSelectedDate = currentSelectedDate;
    }

    public void showTimeSlotBottomSheet() {
        bottomSheet = context.getLayoutInflater().inflate(R.layout.layout_time_slot, null);
        bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(bottomSheet);
        bottomSheetDialog.show();
        recyclerViewDay = (RecyclerView) bottomSheet.findViewById(R.id.recycler_view_day);
        recyclerViewDay.setLayoutManager(new LinearLayoutManager(context));
        showTimeBottomSheet();
    }

    public void showOrderSlotBottomSheet(RealmList<Order> mergeOrders) {
        bottomSheet = context.getLayoutInflater().inflate(R.layout.layout_time_slot, null);
        bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(bottomSheet);
        bottomSheetDialog.show();
        recyclerViewDay = (RecyclerView) bottomSheet.findViewById(R.id.recycler_view_day);
        recyclerViewDay.setLayoutManager(new LinearLayoutManager(context));
        showOrderBottomSheet(mergeOrders);
    }

    private void showOrderBottomSheet(final RealmList<Order> mergeOrders) {
        OrdersItemAdapter ordersItemAdapter = new OrdersItemAdapter(context, mergeOrders);
        ordersItemAdapter.setTimeSlotOrderAdapter(true);
        ordersItemAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                onTimePicked.onTimePicked(null, mergeOrders.get(position));
                bottomSheetDialog.dismiss();
            }
        });
        recyclerViewDay.setAdapter(ordersItemAdapter);
    }

    private void showTimeBottomSheet() {


        try {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
            endDate = fmt.parse("2017-12-30");
            startDate = fmt.parse(fmt.format(startDate));

            Date prevSelectedDate = fmt.parse(fmt.format(currentSelectedDate));
            //Adding date
            calendar.setTime(startDate);
            calendar.add(Calendar.DATE, 7);
            endDate = fmt.parse(fmt.format(calendar.getTime()));
            currentSelectedPosition = (int) getDifferenceDays(startDate, prevSelectedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        daySlots.addAll(getDateArrayList(startDate, endDate));

        for (int i = 0; i < Constants.DATE_RANGE.length; i++) {
            timeSlots.add(Constants.DATE_RANGE[i]);
        }


        timeSlotAdapterDay = new TimeSlotAdapter(context);
        timeSlotAdapterDay.setTimeStrings(daySlots);
        timeSlotAdapterDay.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(int position) {

                try {
                    int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                    String dayMonth = timeSlotAdapterDay.getTimeStrings().get(position);
                    String[] strings = dayMonth.split(",");
                    int day = Integer.parseInt(strings[0]);
                    int month = 0;
                    month = getMonthInt(strings[1].trim());
                    Date date = getDate(currentYear, month, day);
                    onTimePicked.onTimePicked(date, null);
                    bottomSheetDialog.dismiss();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        timeSlotAdapterDay.setCurrentSelection(currentSelectedPosition);
        recyclerViewDay.setAdapter(timeSlotAdapterDay);
        recyclerViewDay.scrollToPosition(currentSelectedPosition);
    }

    public static long getDifferenceDays(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    public int getMonthInt(String monthName) throws ParseException {
        Date date = new SimpleDateFormat("MMM").parse(monthName);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH);
    }

    public Date getDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private ArrayList<String> getDateArrayList(Date startDate, Date endDate) {
        int position = 0;
        ArrayList<String> stringArrayList = new ArrayList<>();
        Calendar startCalender = Calendar.getInstance();
        startCalender.setTime(startDate);
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(endDate);
        for (; startCalender.compareTo(endCalendar) <= 0;
             startCalender.add(Calendar.DATE, 1)) {
            int date = startCalender.get(Calendar.DAY_OF_MONTH);
            SimpleDateFormat month_date = new SimpleDateFormat("MMM");
            String month_name = month_date.format(startCalender.getTime());
            if (currentSelectedPosition == 0 && currentSelectedDate != null) {
                if (DateTimeUtil.checkIfSameDayMonthYear(startCalender.getTime(), currentSelectedDate)) {
                    currentSelectedPosition = position;
                }

            }
            position++;
            stringArrayList.add(date + ", " + month_name);
        }
        return stringArrayList;
    }

    public interface OnTimePicked {
        void onTimePicked(Date date, Order order);
    }
}
