package one.thebox.android.ViewHelper;

import android.app.Activity;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

import one.thebox.android.Models.timeslot.TimeSlot;
import one.thebox.android.R;
import one.thebox.android.adapter.timeslot.DateSlotAdapter;

/**
 * Created by Ajeet Kumar Meena on 29-04-2016.
 */
public class TimeSlotBottomSheet {

    private Activity context;
    private BottomSheetDialog bottomSheetDialog;
    private View bottomSheet;
    private RecyclerView recyclerViewDay;
    private DateSlotAdapter dateSlotAdapterDay;
    private OnDatePicked onDatePicked;
    private ArrayList<TimeSlot> timeSlots;
    private int selectedDatePosition;

    /**
     * Constructor
     * TimeSlot
     */
    public TimeSlotBottomSheet(Activity context, ArrayList<TimeSlot> timeSlots, int selectedDatePosition, OnDatePicked onDatePicked) {
        this.context = context;
        this.timeSlots = timeSlots;
        this.selectedDatePosition = selectedDatePosition;
        this.onDatePicked = onDatePicked;
    }

    /**
     * Show date Slot Bottom Sheet
     */
    public void showDateSlotBottomSheet() {
        bottomSheet = context.getLayoutInflater().inflate(R.layout.layout_time_slot, null);
        recyclerViewDay = (RecyclerView) bottomSheet.findViewById(R.id.recycler_view_day);
        bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(bottomSheet);
        bottomSheetDialog.show();

        setupRecyclerViewForDates();
    }

    public void setupRecyclerViewForDates() {
        recyclerViewDay.setLayoutManager(new LinearLayoutManager(context));
        dateSlotAdapterDay = new DateSlotAdapter(context, timeSlots, selectedDatePosition, new DateSlotAdapter.OnDateSelected() {
            @Override
            public void onDateSelected(TimeSlot timeSlot, int position) {
                onDatePicked.onDatePicked(timeSlot, position);
                bottomSheetDialog.dismiss();
            }
        });
        recyclerViewDay.setAdapter(dateSlotAdapterDay);
        recyclerViewDay.scrollToPosition(selectedDatePosition);

    }

    public interface OnDatePicked {
        void onDatePicked(TimeSlot timeSlot, int position);
    }
}
