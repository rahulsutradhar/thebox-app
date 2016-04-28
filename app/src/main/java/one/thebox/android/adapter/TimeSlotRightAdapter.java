package one.thebox.android.adapter;

import android.content.Context;
import android.view.View;

import java.sql.Time;
import java.util.ArrayList;

import one.thebox.android.Models.TimeSlot;
import one.thebox.android.R;

/**
 * Created by Ajeet Kumar Meena on 17-04-2016.
 */
public class TimeSlotRightAdapter extends BaseRecyclerAdapter {

    ArrayList<TimeSlot> timeSlotArrayList = new ArrayList<>();

    public TimeSlotRightAdapter(Context context) {
        super(context);
    }

    public ArrayList<TimeSlot> getTimeSlotArrayList() {
        return timeSlotArrayList;
    }

    public void setTimeSlotArrayList(ArrayList<TimeSlot> timeSlotArrayList) {
        this.timeSlotArrayList = timeSlotArrayList;
    }

    @Override
    protected ItemHolder getItemHolder(View view) {
        return null;
    }

    @Override
    protected ItemHolder getItemHolder(View view, int position) {
        return null;
    }

    @Override
    protected HeaderHolder getHeaderHolder(View view) {
        return null;
    }

    @Override
    protected FooterHolder getFooterHolder(View view) {
        return null;
    }

    @Override
    public void onBindViewItemHolder(ItemHolder holder, int position) {

    }

    @Override
    public void onBindViewHeaderHolder(HeaderHolder holder, int position) {

    }

    @Override
    public void onBindViewFooterHolder(FooterHolder holder, int position) {

    }

    @Override
    public int getItemsCount() {
        return 0;
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_time_slot;
    }

    @Override
    protected int getHeaderLayoutId() {
        return 0;
    }

    @Override
    protected int getItemLayoutId(int position) {
        return 0;
    }

    @Override
    protected int getFooterLayoutId() {
        return 0;
    }

    class ItemViewHolder extends ItemHolder {

        public ItemViewHolder(View itemView) {
            super(itemView);
        }
    }
}
