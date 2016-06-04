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
public class DaySlotAdapter extends BaseRecyclerAdapter {

    String [] dayStrings ;
    private int currentSelection;
    private OnDaySlotSelected onDaySlotSelected;

    public DaySlotAdapter(Context context) {
        super(context);
    }

    public DaySlotAdapter(Context context, OnDaySlotSelected onDaySlotSelected) {
        super(context);
        this.onDaySlotSelected = onDaySlotSelected;
    }

    public String [] getDayStrings() {
        return dayStrings;
    }

    public void setDayStrings(String [] dayStrings) {
        this.dayStrings = dayStrings;
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
                if (onDaySlotSelected != null) {
                    onDaySlotSelected.OnDaySlotSelected(dayStrings[position]);
                }
            }
        });
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.setupView(dayStrings[position]);
    }

    @Override
    public void onBindViewHeaderHolder(HeaderHolder holder, int position) {

    }

    @Override
    public void onBindViewFooterHolder(FooterHolder holder, int position) {

    }

    @Override
    public int getItemsCount() {
        return dayStrings.length;
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

    public interface OnDaySlotSelected {
        void OnDaySlotSelected(String timeSlot);
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
                timeTextView.setTextColor(mContext.getResources().getColor(R.color.primary_text_color));
                timeTextView.setTypeface(null, Typeface.NORMAL);
            }
        }
    }
}
