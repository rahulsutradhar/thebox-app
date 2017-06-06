package one.thebox.android.adapter.orders;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.realm.RealmList;
import one.thebox.android.Helpers.OrderHelper;
import one.thebox.android.Models.AddressAndOrder;
import one.thebox.android.Models.Order;
import one.thebox.android.R;
import one.thebox.android.activity.ConfirmTimeSlotActivity;
import one.thebox.android.activity.OrderItemsActivity;
import one.thebox.android.adapter.base.BaseRecyclerAdapter;
import one.thebox.android.app.Constants;
import one.thebox.android.util.DateTimeUtil;


/**
 * Created by Ajeet Kumar Meena on 15-04-2016.
 * <p>
 * Modified by Developers on 06/06/2017.
 */
public class UpcomingOrderAdapter extends BaseRecyclerAdapter {

    private ArrayList<Order> orders = new ArrayList<>();

    public UpcomingOrderAdapter(Context context, ArrayList<Order> orders) {
        super(context);
        this.orders = orders;
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
        itemViewHolder.setViewHolder(orders.get(position), position);
        itemViewHolder.holderViewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mContext.startActivity(OrderItemsActivity.newInstance(mContext, orders.get(position).getId(), orders.get(position).getOrderDate()));
                } catch (Exception e) {
                    e.printStackTrace();
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
        return R.layout.item_order;
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

        private TextView dateTextView, text_order_state, itemsNameTextView, amountTobePaidTextView, viewItemsTextView, timeSlot, month, message, reschedule_order_button;
        private LinearLayout linearLayout, holderViewItem;
        private CardView cardView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            dateTextView = (TextView) itemView.findViewById(R.id.text_date);
            text_order_state = (TextView) itemView.findViewById(R.id.text_order_state);
            reschedule_order_button = (TextView) itemView.findViewById(R.id.reschdule_order_button);
            itemsNameTextView = (TextView) itemView.findViewById(R.id.text_items_name);
            amountTobePaidTextView = (TextView) itemView.findViewById(R.id.text_amount_to_be_paid);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.holder);
            viewItemsTextView = (TextView) itemView.findViewById(R.id.text_view_view_items);
            holderViewItem = (LinearLayout) itemView.findViewById(R.id.holder_view_items);
            timeSlot = (TextView) itemView.findViewById(R.id.time_slot);
            month = (TextView) itemView.findViewById(R.id.month);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            message = (TextView) itemView.findViewById(R.id.message);
        }

        public void setViewHolder(final Order order, final int position) {

            try {
                // Rescheduling the order
                if (position == 0) {
                    reschedule_order_button.setVisibility(View.VISIBLE);
                    reschedule_order_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (order != null) {
                                RealmList<Order> orders = new RealmList<>();
                                orders.add(order);

                                //open time slots activity
                                mContext.startActivity(ConfirmTimeSlotActivity.newInstance(mContext, OrderHelper.getAddressAndOrder(orders), order.getId(), true));
                            }
                        }
                    });
                } else {
                    reschedule_order_button.setVisibility(View.GONE);
                }

                if (order.getOrderDate() != null) {
                    dateTextView.setText(order.getOrderDate());
                } else {
                    dateTextView.setText("");
                }

                if (order.getDeliverySlotDuration() != null) {
                    timeSlot.setText(order.getDeliverySlotDuration());
                } else {
                    timeSlot.setText("");
                }

                //set the month value
                month.setVisibility(View.GONE);

                itemsNameTextView.setText(order.getNoOfItems() + " items");

                // Order has been unsuccessfull
                // State 3a
               /* if (!order.isSuccessful()) {
                    message.setText("Order was not confirmed by the user");
                    message.setTextColor(mContext.getResources().getColor(R.color.secondary_text_color));
                    text_order_state.setText("Was scheduled on");
                    amountTobePaidTextView.setVisibility(View.GONE);
                }*/
                // Order has been closed
                // State 3b
                if (!order.isOpen()) {
                    message.setText("Thank you for choosing us");
                    message.setTextColor(mContext.getResources().getColor(R.color.secondary_text_color));
                    amountTobePaidTextView.setOnClickListener(null);
                    if (order.isCod()) {
                        amountTobePaidTextView.setText(Constants.RUPEE_SYMBOL + " " + Math.round(order.getTotalPrice()) + " paid via COD");
                    } else {
                        amountTobePaidTextView.setText(Constants.RUPEE_SYMBOL + " " + Math.round(order.getTotalPrice()) + " paid Online");
                    }

                }
                // Order was choosen to be cod and was delivered but payment was not collected
                //<TODO> Change this to "Pay Online" Button after Payment Gateway integration
                // State 2a'
                else if (order.isCod() && order.isDelivered() && !order.isPaid()) {
                    message.setText("Payment Pending. Please pay next delivery.");
                    message.setTextColor(mContext.getResources().getColor(R.color.md_red_500));
                    amountTobePaidTextView.setText(Constants.RUPEE_SYMBOL + " " + Math.round(order.getTotalPrice()) + " to be paid");
                    amountTobePaidTextView.setOnClickListener(null);
                }
                // Order was choosen to be cod and is not delivered yet
                // State 2a
                else if (order.isCod() && !order.isDelivered()) {
                    message.setText("Please pay the delivery boy");
                    message.setTextColor(mContext.getResources().getColor(R.color.md_orange_500));
                    amountTobePaidTextView.setText(Constants.RUPEE_SYMBOL + " " + Math.round(order.getTotalPrice()) + " to be paid");
                    amountTobePaidTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            RealmList<Order> orders = new RealmList<>();
                            orders.add(order);
                            //open time slots activity
                            mContext.startActivity(ConfirmTimeSlotActivity.newInstance(mContext, OrderHelper.getAddressAndOrder(orders), false));
                        }
                    });
                }
                // Order was paid online atleast once (more items may have been added and is not delivered yet
                else if (order.isPaid() && !order.isCod() && !order.isDelivered()) {
                    if (order.is_partial_payment_amount_remaining()) {

                        // State 2b
                        // COD was selected while merging items to this order
                        // OR
                        // This order was paid online earlier and items were subscribed to an earlier order and have been added to this order to complete subscription

                        message.setText("Partial payment complete. " + Constants.RUPEE_SYMBOL + " " + Math.round(order.getTotalPrice() - order.get_payment_amount_remaining()) +
                                " were paid online. New items worth " + Math.round(order.get_payment_amount_remaining()) + " were added to your subscription");
                        message.setTextColor(mContext.getResources().getColor(R.color.md_blue_500));
                        amountTobePaidTextView.setBackgroundColor(Color.WHITE);
                        amountTobePaidTextView.setText(Constants.RUPEE_SYMBOL + " " + Math.round(order.get_payment_amount_remaining()) + " to be paid via COD");
                        amountTobePaidTextView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                RealmList<Order> orders = new RealmList<>();
                                orders.add(order);
                                //open time slots activity
                                mContext.startActivity(ConfirmTimeSlotActivity.newInstance(mContext, OrderHelper.getAddressAndOrder(orders), false));
                            }
                        });
                    } else {
                        //State 2c
                        message.setText("Payment complete");
                        message.setTextColor(mContext.getResources().getColor(R.color.md_blue_500));
                        amountTobePaidTextView.setBackgroundColor(Color.WHITE);
                        amountTobePaidTextView.setText(Constants.RUPEE_SYMBOL + " " + Math.round(order.getTotalPrice()) + " paid online");
                    }
                }
                //Order made not confirmed yet
                //State 1
                else if (!order.isPaid() && !order.isCod()) {
                    message.setText("Please confirm delivery");
                    message.setTextColor(mContext.getResources().getColor(R.color.md_orange_800));
                    amountTobePaidTextView.setText("Pay " + Constants.RUPEE_SYMBOL + " " + Math.round(order.getTotalPrice()));
                    amountTobePaidTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            RealmList<Order> orders = new RealmList<>();
                            orders.add(order);
                            //open time slots activity
                            mContext.startActivity(ConfirmTimeSlotActivity.newInstance(mContext, OrderHelper.getAddressAndOrder(orders), false));
                        }
                    });
                }
            } catch (IndexOutOfBoundsException i) {
                i.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
