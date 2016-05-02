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

import one.thebox.android.Models.TimeSlot;
import one.thebox.android.R;
import one.thebox.android.adapter.TimeSlotAdapter;
import one.thebox.android.util.Constants;

/**
 * Created by Ajeet Kumar Meena on 29-04-2016.
 */
public class TimeSlotBottomSheet {

    private Activity context;
    private BottomSheetDialog bottomSheetDialog;
    private View bottomSheet;
    private Date startDate;
    private OnTimePicked onTimePicked;
    private RecyclerView recyclerViewDay;
    private RecyclerView recyclerViewTimeSlot;
    private ArrayList<String> timeSlots = new ArrayList<>();
    private ArrayList<String> daySlots = new ArrayList<>();
    private TimeSlotAdapter timeSlotAdapterDay;
    private TimeSlotAdapter timeSlotAdapterSlots;

    public TimeSlotBottomSheet(Activity context, Date date, OnTimePicked onTimePicked) {
        this.context = context;
        this.onTimePicked = onTimePicked;
        this.startDate = date;
    }

    public void show() {
        Date endDate = null;
        Date startDate = null;
        daySlots.add("Today");
        daySlots.add("Tomorrow");
        try {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
            endDate = fmt.parse("2016-12-30");
            startDate = fmt.parse(fmt.format(calendar.getTime()));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        daySlots = getDateArrayList(startDate, endDate);
/*
        daySlots.add((date + 2) + ", " + month_name);
*/
        for (int i = 0; i < Constants.DATE_RANGE.length; i++) {
            timeSlots.add(Constants.DATE_RANGE[i]);
        }
        bottomSheet = context.getLayoutInflater().inflate(R.layout.layout_time_slot, null);
        bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(bottomSheet);
        bottomSheetDialog.show();
        recyclerViewDay = (RecyclerView) bottomSheet.findViewById(R.id.recycler_view_day);
        recyclerViewTimeSlot = (RecyclerView) bottomSheet.findViewById(R.id.recycler_view_time);
        recyclerViewDay.setLayoutManager(new LinearLayoutManager(context));
        recyclerViewTimeSlot.setLayoutManager(new LinearLayoutManager(context));
        timeSlotAdapterDay = new TimeSlotAdapter(context);
        timeSlotAdapterDay.setTimeStrings(daySlots);
        timeSlotAdapterSlots = new TimeSlotAdapter(context, new TimeSlotAdapter.OnTimeSlotSelected() {
            @Override
            public void onTimeSlotSelected(String timeSlot) {

                try {
                    int currentYear = 2016;
                    String dayMonth = timeSlotAdapterDay.getTimeStrings().get(timeSlotAdapterDay.getCurrentSelection());
                    String[] strings = dayMonth.split(",");
                    int day = Integer.parseInt(strings[0]);
                    int month = getMonthInt(strings[1].trim());
                    Date date = getDate(currentYear, month, day);
                    onTimePicked.onTimePicked(getDateWithTimeSlot(date, timeSlot));
                    bottomSheetDialog.dismiss();

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        });
        timeSlotAdapterSlots.setTimeStrings(timeSlots);
        recyclerViewDay.setAdapter(timeSlotAdapterDay);
        recyclerViewTimeSlot.setAdapter(timeSlotAdapterSlots);
    }

    public interface OnTimePicked {
        void onTimePicked(Date date);
    }

    public Date getDateWithTimeSlot(Date date, String timeSlot) {
        int hour = Integer.parseInt(timeSlot.substring(0, 2));
        int minute = Integer.parseInt(timeSlot.substring(3, 5));
        String am_pm = timeSlot.substring(6, 7);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR, hour);
        cal.set(Calendar.MINUTE, minute);
        if (am_pm.equals("A")) {
            cal.set(Calendar.AM_PM, Calendar.AM);
        } else {
            cal.set(Calendar.AM_PM, Calendar.PM);
        }
        return cal.getTime();
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
            stringArrayList.add(date + ", " + month_name);
        }
        return stringArrayList;
    }
}
