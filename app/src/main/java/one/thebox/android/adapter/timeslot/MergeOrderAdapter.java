package one.thebox.android.adapter.timeslot;

import android.content.Context;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;

import one.thebox.android.Models.order.Order;
import one.thebox.android.R;
import one.thebox.android.activity.ConfirmTimeSlotActivity;
import one.thebox.android.adapter.base.BaseRecyclerAdapter;


/**
 * Created by Ajeet Kumar Meena on 15-04-2016.
 */
public class MergeOrderAdapter extends BaseRecyclerAdapter {

    private ArrayList<Order> orders;
    private boolean isTimeSlotOrderAdapter;
    private int currentSelection = 0;
    private ConfirmTimeSlotActivity activity;

    public MergeOrderAdapter(Context context, ConfirmTimeSlotActivity activity, ArrayList<Order> orders) {
        super(context);
        this.orders = orders;
        this.activity = activity;
        mViewType = RECYCLER_VIEW_TYPE_NORMAL;
    }

    public ArrayList<Order> getOrders() {
        return orders;
    }

    public void setOrders(ArrayList<Order> orders) {
        this.orders = orders;
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
    public void onBindViewItemHolder(final ItemHolder holder, final int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.setViewHolder(orders.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int temp = currentSelection;
                currentSelection = position;
                notifyItemChanged(currentSelection);
                notifyItemChanged(temp);

                if (activity != null) {
                    ((ConfirmTimeSlotActivity) activity).setSelectedMergeOrder(orders.get(currentSelection));
                }
            }
        });
    }

    @Override
    public void onBindViewHeaderHolder(HeaderHolder holder, int position) {
    }

    @Override
    public void onBindViewFooterHolder(FooterHolder holder, int position) {

    }

    @Override
    public int getItemsCount() {
        return orders.size();
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_merge_order;
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

        private TextView dateTextView, timeSlot;
        private RadioButton radioButton;

        public ItemViewHolder(View itemView) {
            super(itemView);
            dateTextView = (TextView) itemView.findViewById(R.id.text_date);
            timeSlot = (TextView) itemView.findViewById(R.id.time_slot);
            radioButton = (RadioButton) itemView.findViewById(R.id.radio_button);
        }

        public void setViewHolder(final Order order) {
            dateTextView.setText(order.getDate());
            timeSlot.setText(order.getTimeSlot());
            radioButton.setClickable(false);
            if (getAdapterPosition() == currentSelection) {
                radioButton.setChecked(true);
            } else {
                radioButton.setChecked(false);
            }
        }
    }
}
