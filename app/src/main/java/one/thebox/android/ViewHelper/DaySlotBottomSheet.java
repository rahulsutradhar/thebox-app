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

import one.thebox.android.R;
import one.thebox.android.adapter.timeslot.DaySlotAdapter;

/**
 * Created by Ajeet Kumar Meena on 29-04-2016.
 */
public class DaySlotBottomSheet {

    private Activity context;
    private BottomSheetDialog bottomSheetDialog;
    private View bottomSheet;
    private Date startDate;
    private OnDayPicked onDayPicked;
    private RecyclerView recyclerViewDay;
    private DaySlotAdapter daySlotAdapter;

    // Defined Array values to show in ListView
    String[] daySlots = new String[] { "Android List View",
            "Adapter implementation",
            "Simple List View In Android",
            "Create List View Android",
            "Android UserItemResponse",
            "List View Source Code",
            "List View Array Adapter",
            "Android UserItemResponse List View"
    };

    public DaySlotBottomSheet(Activity context, Date date, OnDayPicked onDayPicked) {
        this.context = context;
        this.onDayPicked = onDayPicked;
        this.startDate = date;
    }

    public void show() {


        bottomSheet = context.getLayoutInflater().inflate(R.layout.layout_time_slot, null);
        bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(bottomSheet);
        bottomSheetDialog.show();
        recyclerViewDay = (RecyclerView) bottomSheet.findViewById(R.id.recycler_view_day);
        recyclerViewDay.setLayoutManager(new LinearLayoutManager(context));

        daySlotAdapter = new DaySlotAdapter(context, new DaySlotAdapter.OnDaySlotSelected() {
            @Override
            public void OnDaySlotSelected(String timeSlot) {
                    String dayMonth = daySlotAdapter.getDayStrings()[daySlotAdapter.getCurrentSelection()];
                    onDayPicked.OnDayPicked(dayMonth);
                    bottomSheetDialog.dismiss();
            }
        });
        daySlotAdapter.setDayStrings(daySlots);
        recyclerViewDay.setAdapter(daySlotAdapter);
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

    public interface OnDayPicked {
        void OnDayPicked(String Day);
    }
}
