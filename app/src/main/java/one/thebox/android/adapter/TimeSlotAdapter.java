package one.thebox.android.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import one.thebox.android.R;

/**
 * Created by Ajeet Kumar Meena on 17-04-2016.
 */
public class TimeSlotAdapter extends BaseRecyclerAdapter {

    ArrayList<String> timeStrings = new ArrayList<>();
    private int currentSelection;
    private OnTimeSlotSelected onTimeSlotSelected;

    public TimeSlotAdapter(Context context) {
        super(context);
    }

    public TimeSlotAdapter(Context context, OnTimeSlotSelected onTimeSlotSelected) {
        super(context);
        this.onTimeSlotSelected = onTimeSlotSelected;
    }

    public ArrayList<String> getTimeStrings() {
        return timeStrings;
    }

    public void setTimeStrings(ArrayList<String> timeStrings) {
        this.timeStrings = timeStrings;
    }

    public int getCurrentSelection() {
        return currentSelection;
    }

    public void setCurrentSelection(int currentSelection) {
        this.currentSelection = currentSelection;
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
                int temp = currentSelection;
                currentSelection = position;
                notifyItemChanged(currentSelection);
                notifyItemChanged(temp);
                if (onTimeSlotSelected != null) {
                    onTimeSlotSelected.onTimeSlotSelected(timeStrings.get(position));
                }
            }
        });
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.setupView(timeStrings.get(position));
    }

    @Override
    public void onBindViewHeaderHolder(HeaderHolder holder, int position) {

    }

    @Override
    public void onBindViewFooterHolder(FooterHolder holder, int position) {

    }

    @Override
    public int getItemsCount() {
        return timeStrings.size();
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

        TextView timeTextView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            timeTextView = (TextView) itemView.findViewById(R.id.text_view);
        }

        public void setupView(String time) {
            timeTextView.setText(time);
            if (currentSelection == getAdapterPosition()) {
                timeTextView.setTextColor(mContext.getResources().getColor(R.color.accent));
                timeTextView.setTypeface(null, Typeface.BOLD);
            } else {
                timeTextView.setTextColor(mContext.getResources().getColor(R.color.dim_gray));
                timeTextView.setTypeface(null, Typeface.NORMAL);
            }
        }
    }

    public interface OnTimeSlotSelected {
        void onTimeSlotSelected(String timeSlot);
    }
}
