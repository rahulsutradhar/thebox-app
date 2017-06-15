package one.thebox.android.adapter.timeslot;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import one.thebox.android.Models.timeslot.Slot;
import one.thebox.android.Models.timeslot.TimeSlot;
import one.thebox.android.R;
import one.thebox.android.adapter.base.BaseRecyclerAdapter;

/**
 * Created by developers on 26/04/17.
 */

public class TimeSlotAdapter extends BaseRecyclerAdapter {

    private Context context;
    private ArrayList<Slot> slots;
    private int selectedSlotPosition;
    private OnTimeSlotSelected onTimeSlotSelected;

    public TimeSlotAdapter(Context context, OnTimeSlotSelected onTimeSlotSelected) {
        super(context);
        this.onTimeSlotSelected = onTimeSlotSelected;
    }

    public ArrayList<Slot> getSlots() {
        return slots;
    }

    public void setSlots(ArrayList<Slot> slots) {
        this.slots = slots;
        notifyDataSetChanged();
    }

    public int getSelectedSlotPosition() {
        return selectedSlotPosition;
    }

    public void setSelectedSlotPosition(int selectedSlotPosition) {
        this.selectedSlotPosition = selectedSlotPosition;
        notifyDataSetChanged();
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
                int temp = selectedSlotPosition;
                selectedSlotPosition = position;
                notifyItemChanged(selectedSlotPosition);
                notifyItemChanged(temp);

                if (onTimeSlotSelected != null) {
                    onTimeSlotSelected.onTimeSlotSelected(slots.get(position), position);
                }
            }
        });
        TimeSlotAdapter.ItemViewHolder itemViewHolder = (TimeSlotAdapter.ItemViewHolder) holder;
        itemViewHolder.setupView(slots.get(position));
    }

    @Override
    public void onBindViewHeaderHolder(HeaderHolder holder, int position) {

    }

    @Override
    public void onBindViewFooterHolder(FooterHolder holder, int position) {

    }

    @Override
    public int getItemsCount() {
        return slots.size();
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_time_slot;
    }

    @Override
    protected int getItemLayoutId(int position) {
        return 0;
    }

    @Override
    protected int getHeaderLayoutId() {
        return 0;
    }

    @Override
    protected int getFooterLayoutId() {
        return 0;
    }

    public interface OnTimeSlotSelected {
        void onTimeSlotSelected(Slot slot, int position);
    }

    class ItemViewHolder extends ItemHolder {

        TextView timeTextView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            timeTextView = (TextView) itemView.findViewById(R.id.text_view);
        }

        public void setupView(Slot slot) {
            try {
                timeTextView.setText(slot.getName());

                if (selectedSlotPosition == getAdapterPosition()) {
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
