package one.thebox.android.adapter;

import android.content.Context;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.RealmList;
import one.thebox.android.Models.AddressAndOrder;
import one.thebox.android.Models.Order;
import one.thebox.android.R;
import one.thebox.android.util.DateTimeUtil;


/**
 * Created by Ajeet Kumar Meena on 15-04-2016.
 */
public class MergeOrderAdapter extends BaseRecyclerAdapter {

    private RealmList<Order> orders = new RealmList<>();
    private boolean isTimeSlotOrderAdapter;
    private int currentSelection;

    public MergeOrderAdapter(Context context, RealmList<Order> orders) {
        super(context);
        this.orders = orders;
      /*  if (shouldHaveOrders()) {
            mViewType = RECYCLER_VIEW_TYPE_HEADER;
        } else {*/
        mViewType = RECYCLER_VIEW_TYPE_NORMAL;
       /* }*/
    }

    public int getCurrentSelection() {
        return currentSelection;
    }

    public void setCurrentSelection(int currentSelection) {
        this.currentSelection = currentSelection;
    }

    public boolean isTimeSlotOrderAdapter() {
        return isTimeSlotOrderAdapter;
    }

    public void setTimeSlotOrderAdapter(boolean timeSlotOrderAdapter) {
        isTimeSlotOrderAdapter = timeSlotOrderAdapter;
    }

    private boolean shouldHaveOrders() {
        for (Order order : orders) {
            if (!order.isCart()) {
                return true;
            }
        }
        return false;
    }

    public void addBillItem(Order order) {
        orders.add(order);
    }

    public RealmList<Order> getOrders() {
        return orders;
    }

    public void setOrders(RealmList<Order> orders) {
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
            }
        });
    }

    @Override
    public void onBindViewHeaderHolder(HeaderHolder holder, int position) {
        // HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
        //  headerViewHolder.setViews(!isAnyItemSelected);
    }

    @Override
    public void onBindViewFooterHolder(FooterHolder holder, int position) {

    }

    @Override
    public int getItemsCount() {
        return orders.size()>=4?4:orders.size();
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
                Date date = DateTimeUtil.convertStringToDate(order.getDeliveryScheduleAt());
                dateTextView.setText(AddressAndOrder.getDateStringWithoutSlot(date));
                timeSlot.setText(AddressAndOrder.getSlotString(new SimpleDateFormat("hh").format(date)));
                radioButton.setClickable(false);
                if (getAdapterPosition() == currentSelection) {
                    radioButton.setChecked(true);
                } else {
                    radioButton.setChecked(false);
                }
        }
    }
}
