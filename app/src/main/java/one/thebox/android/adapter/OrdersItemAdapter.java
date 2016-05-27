package one.thebox.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;

import io.realm.RealmList;
import one.thebox.android.Models.AddressAndOrder;
import one.thebox.android.Models.Order;
import one.thebox.android.R;
import one.thebox.android.activity.ConfirmAddressActivity;
import one.thebox.android.activity.OrderItemsActivity;
import one.thebox.android.util.DateTimeUtil;


/**
 * Created by Ajeet Kumar Meena on 15-04-2016.
 */
public class OrdersItemAdapter extends BaseRecyclerAdapter {

    private RealmList<Order> orders = new RealmList<>();
    private boolean isTimeSlotOrderAdapter;

    public OrdersItemAdapter(Context context, RealmList<Order> orders) {
        super(context);
        this.orders = orders;
      /*  if (shouldHaveOrders()) {
            mViewType = RECYCLER_VIEW_TYPE_HEADER;
        } else {*/
        mViewType = RECYCLER_VIEW_TYPE_NORMAL;
       /* }*/
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
        itemViewHolder.viewItemsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(OrderItemsActivity.newInstance(mContext, orders.get(position).getId()));
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

        private TextView dateTextView, itemsNameTextView, amountTobePaidTextView, viewItemsTextView;
        private LinearLayout linearLayout;

        public ItemViewHolder(View itemView) {
            super(itemView);
            dateTextView = (TextView) itemView.findViewById(R.id.text_date);
            itemsNameTextView = (TextView) itemView.findViewById(R.id.text_items_name);
            amountTobePaidTextView = (TextView) itemView.findViewById(R.id.text_amount_to_be_paid);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.holder);
            viewItemsTextView = (TextView) itemView.findViewById(R.id.text_view_view_items);
        }

        public void setViewHolder(final Order order) {
            try {
                dateTextView.setText(AddressAndOrder.getDateString(DateTimeUtil.convertStringToDate(order.getDeliveryScheduleAt())));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            itemsNameTextView.setText("You have " + order.getUserItems().size() + " items in the order");
            if(isTimeSlotOrderAdapter) {
                amountTobePaidTextView.setText("Merge");
            } else {
                if (order.isPaid()) {
                    amountTobePaidTextView.setText("Paid");
                    amountTobePaidTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(mContext, "Order have been paid", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    amountTobePaidTextView.setText("Pay Rs " + order.getTotalPrice());
                    amountTobePaidTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            RealmList<Order> orders = new RealmList<>();
                            orders.add(order);
                            mContext.startActivity(ConfirmAddressActivity.getInstance(mContext, orders));
                        }
                    });
                }
            }
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
