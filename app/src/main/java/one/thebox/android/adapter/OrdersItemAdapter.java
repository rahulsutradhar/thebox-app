package one.thebox.android.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.realm.RealmList;
import one.thebox.android.Models.AddressAndOrder;
import one.thebox.android.Models.Order;
import one.thebox.android.R;
import one.thebox.android.activity.ConfirmAddressActivity;
import one.thebox.android.activity.OrderItemsActivity;
import one.thebox.android.app.MyApplication;
import one.thebox.android.util.Constants;
import one.thebox.android.util.DateTimeUtil;
import one.thebox.android.util.PrefUtils;

import static one.thebox.android.R.id.month;


/**
 * Created by Ajeet Kumar Meena on 15-04-2016.
 */
public class OrdersItemAdapter extends BaseRecyclerAdapter {

    private RealmList<Order> orders = new RealmList<>();
    private boolean isTimeSlotOrderAdapter;
    private ArrayList<Integer> monthPrintPosition = new ArrayList<>();

    public OrdersItemAdapter(Context context, RealmList<Order> orders) {
        super(context);
        this.orders = orders;

        try {
            String previousMonth = "";
            for (int i = 0; i < orders.size(); i++) {
                Order order = orders.get(i);
                Date date = DateTimeUtil.convertStringToDate(order.getDeliveryScheduleAt());
                String currentMonth = new SimpleDateFormat("MMMM").format(date);
                if (!previousMonth.equals(currentMonth)) {
                    monthPrintPosition.add(i);
                }
                previousMonth = currentMonth;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (PrefUtils.getBoolean(MyApplication.getInstance(), Constants.PREF_IS_ORDER_IS_LOADING, false)) {
            mViewType = RECYCLER_VIEW_TYPE_FOOTER;
        } else {
            mViewType = RECYCLER_VIEW_TYPE_NORMAL;
        }
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
        return new FooterViewHolder(view);
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
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(OrderItemsActivity.newInstance(mContext, orders.get(position).getId()));
            }
        });
        itemViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
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
        if (PrefUtils.getBoolean(MyApplication.getInstance(), Constants.PREF_IS_ORDER_IS_LOADING, false)) {
            return orders.size() >= 1 ? 1 : 0;
        } else {
            return orders.size();
        }
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
        return R.layout.footer_order_loader;
    }

    class ItemViewHolder extends ItemHolder {

        private TextView dateTextView, itemsNameTextView, amountTobePaidTextView, viewItemsTextView, timeSlot, month, message;
        private LinearLayout linearLayout;
        private CardView cardView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            dateTextView = (TextView) itemView.findViewById(R.id.text_date);
            itemsNameTextView = (TextView) itemView.findViewById(R.id.text_items_name);
            amountTobePaidTextView = (TextView) itemView.findViewById(R.id.text_amount_to_be_paid);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.holder);
            viewItemsTextView = (TextView) itemView.findViewById(R.id.text_view_view_items);
            timeSlot = (TextView) itemView.findViewById(R.id.time_slot);
            month = (TextView) itemView.findViewById(R.id.month);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            message = (TextView) itemView.findViewById(R.id.message);
        }

        public void setViewHolder(final Order order) {

            try {
                Date date = DateTimeUtil.convertStringToDate(order.getDeliveryScheduleAt());
                dateTextView.setText(AddressAndOrder.getDateStringWithoutSlot(date));
                timeSlot.setText(AddressAndOrder.getSlotString(new SimpleDateFormat("hh").format(date)));
                String currentMonth = new SimpleDateFormat("MMMM").format(date);
                if (monthPrintPosition.contains(getAdapterPosition())) {
                    month.setVisibility(View.VISIBLE);
                    month.setText(currentMonth);
                } else {
                    month.setVisibility(View.GONE);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
            itemsNameTextView.setText(order.getUserItems().size() + " items in the order");

            if (!order.isOpen()) {
                message.setText("Thank you for choosing us");
                message.setTextColor(mContext.getResources().getColor(R.color.secondary_text_color));
                amountTobePaidTextView.setText("Rs " + order.getTotalPrice() + " paid via COD");
                amountTobePaidTextView.setOnClickListener(null);
            } else if (order.isCod()) {
                message.setText("Please pay the amount to delivery boy");
                message.setTextColor(mContext.getResources().getColor(R.color.md_red_500));
                amountTobePaidTextView.setText("Rs " + order.getTotalPrice() + " COD to be paid");
                amountTobePaidTextView.setOnClickListener(null);
            } else if (order.isPaid()) {
                message.setText("Payment Confirm");
                message.setTextColor(mContext.getResources().getColor(R.color.secondary_text_color));
                amountTobePaidTextView.setText("Rs " + order.getTotalPrice() + " paid online");
            } else if (!order.isPaid() && !order.isCod()) {
                message.setText("Please confirm delivery");
                message.setTextColor(mContext.getResources().getColor(R.color.black));
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

    class FooterViewHolder extends FooterHolder {

        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }
}
