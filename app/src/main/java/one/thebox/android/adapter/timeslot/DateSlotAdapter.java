package one.thebox.android.adapter.timeslot;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import java.sql.Time;
import java.util.ArrayList;

import one.thebox.android.Models.timeslot.TimeSlot;
import one.thebox.android.R;
import one.thebox.android.adapter.base.BaseRecyclerAdapter;

/**
 * Created by Ajeet Kumar Meena on 17-04-2016.
 */
public class DateSlotAdapter extends BaseRecyclerAdapter {

    private ArrayList<TimeSlot> timeSlots;

    private OnDateSelected onDateSelected;
    private int selectedDatePosition;

    public DateSlotAdapter(Context context, ArrayList<TimeSlot> timeSlots, int selectedDatePosition, OnDateSelected onDateSelected) {
        super(context);
        this.timeSlots = timeSlots;
        this.selectedDatePosition = selectedDatePosition;
        this.onDateSelected = onDateSelected;
    }

    @Override
    protected ItemHolder getItemHolder(View view) {
        return new ItemViewHolder(view);
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
    public void onBindViewItemHolder(ItemHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int temp = selectedDatePosition;
                selectedDatePosition = position;
                notifyItemChanged(selectedDatePosition);
                notifyItemChanged(temp);

                if (onDateSelected != null) {
                    onDateSelected.onDateSelected(timeSlots.get(position), position);
                }
            }
        });
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.setupView(timeSlots.get(position));
    }

    @Override
    public void onBindViewHeaderHolder(HeaderHolder holder, int position) {

    }

    @Override
    public void onBindViewFooterHolder(FooterHolder holder, int position) {

    }

    @Override
    public int getItemsCount() {
        return timeSlots.size();
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

    /**
     * Interface for communication
     */
    public interface OnDateSelected {
        void onDateSelected(TimeSlot timeSlot, int position);
    }

    class ItemViewHolder extends ItemHolder {

        TextView timeTextView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            timeTextView = (TextView) itemView.findViewById(R.id.text_view);
        }

        public void setupView(TimeSlot timeSlot) {
            try {
                timeTextView.setText(timeSlot.getDate());

                if (selectedDatePosition == getAdapterPosition()) {
                    timeTextView.setTextColor(mContext.getResources().getColor(R.color.accent));
                    timeTextView.setTypeface(null, Typeface.BOLD);
                } else {
                    timeTextView.setTextColor(mContext.getResources().getColor(R.color.primary_text_color));
                    timeTextView.setTypeface(null, Typeface.NORMAL);
                }
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            }
        }
    }
}
