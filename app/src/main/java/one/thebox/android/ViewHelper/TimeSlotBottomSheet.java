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
        daySlots.add("Today");
        daySlots.add("Tomorrow");
        try {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MMM-dd");
            Date endDate = fmt.parse("2016-12-30");
            Date startDate = fmt.parse(fmt.format(calendar.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }


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
        timeSlotAdapterSlots = new TimeSlotAdapter(context);
        timeSlotAdapterSlots.setTimeStrings(timeSlots);
        recyclerViewDay.setAdapter(timeSlotAdapterDay);
        recyclerViewTimeSlot.setAdapter(timeSlotAdapterSlots);
    }

    public interface OnTimePicked {
        void onTimePicked(Date date);
    }




  /*  private static ArrayList<String> iterateBetweenDates(Date startDate,Date endDate) {
        Calendar startCalender = Calendar.getInstance();
        startCalender.setTime(startDate);
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(endDate);
        for(; startCalender.compareTo(endCalendar)<=0;
            startCalender.add(Calendar.DATE, 1)) {
            int date = startCalender.get(Calendar.DAY_OF_MONTH);
            SimpleDateFormat month_date = new SimpleDateFormat("MMM");
            String month_name = month_date.format(startCalender.getTime());
        }

    }*/


}
