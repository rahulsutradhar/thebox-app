package one.thebox.android.adapter;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import one.thebox.android.Models.Order;
import one.thebox.android.R;


/**
 * Created by Ajeet Kumar Meena on 15-04-2016.
 */
public class OrdersItemAdapter extends BaseRecyclerAdapter {

    private ArrayList<Order> orders = new ArrayList<>();
    private String nextDeliveryDate;
    private String nextDeliveryDayPayment;
    private boolean shouldHaveHeader;

    public OrdersItemAdapter(Context context, ArrayList<Order> orders) {
        super(context);
        this.orders = orders;
        if (shouldHaveOrders()) {
            mViewType = RECYCLER_VIEW_TYPE_HEADER;
        } else {
            mViewType = RECYCLER_VIEW_TYPE_NORMAL;
        }
    }

    private boolean shouldHaveOrders() {
        for (Order order : orders) {
            if (!order.isCart()) {
                return true;
            }
        }
        return false;
    }

    public boolean isAnyItemSelected() {
        for (Order order : orders) {
            if (order.isSelected()) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Order> getSelectedOrders() {
        ArrayList<Order> selectedOrders = new ArrayList<>();
        for (Order order : orders) {
            if (order.isSelected())
                selectedOrders.add(order);
        }
        return selectedOrders;
    }

    public void addBillItem(Order order) {
        orders.add(order);
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
        return new HeaderViewHolder(view);
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
                orders.get(position).setSelected(!orders.get(position).isSelected());
                notifyItemChanged(holder.getAdapterPosition());
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
        return orders.size();
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_order;
    }

    @Override
    protected int getHeaderLayoutId() {
        return R.layout.header_item_bill;
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

        private TextView dateTextView, timeTextView, itemsNameTextView, amountTobePaidTextView;
        private LinearLayout linearLayout;

        public ItemViewHolder(View itemView) {
            super(itemView);
            dateTextView = (TextView) itemView.findViewById(R.id.text_date);
            timeTextView = (TextView) itemView.findViewById(R.id.text_time);
            itemsNameTextView = (TextView) itemView.findViewById(R.id.text_items_name);
            amountTobePaidTextView = (TextView) itemView.findViewById(R.id.text_amount_to_be_paid);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.holder);
        }

        public void setViewHolder(Order order) {
            if (order.isSelected()) {
                linearLayout.setBackgroundColor(mContext.getResources().getColor(R.color.light_white));
            } else {
                linearLayout.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            }
            if (order.isCart()) {
                dateTextView.setVisibility(View.GONE);
                timeTextView.setVisibility(View.GONE);
            } else {

            }
            itemsNameTextView.setText(order.getItemString());
            amountTobePaidTextView.setText(order.getTotalPrice() + " Rs");
        }
    }

    class HeaderViewHolder extends HeaderHolder {
        private LinearLayout payForWeekAndWeekLayout;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            payForWeekAndWeekLayout = (LinearLayout) itemView.findViewById(R.id.layout_pay_for_week_and_month);
        }

        public void setViews(boolean isHiddenPayForWeekLayout) {
            if (isHiddenPayForWeekLayout) {
                payForWeekAndWeekLayout.setVisibility(View.GONE);
            } else {
                payForWeekAndWeekLayout.setVisibility(View.VISIBLE);
            }
        }
    }
}
