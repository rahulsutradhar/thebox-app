package one.thebox.android.adapter.orders;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import one.thebox.android.Models.order.Order;
import one.thebox.android.R;
import one.thebox.android.activity.ConfirmTimeSlotActivity;
import one.thebox.android.activity.OrderItemsActivity;
import one.thebox.android.adapter.base.BaseRecyclerAdapter;
import one.thebox.android.fragment.UpComingOrderFragment;
import one.thebox.android.fragment.order.OrderHistoryFragment;


/**
 * Created by Ajeet Kumar Meena on 15-04-2016.
 * <p>
 * Modified by Developers on 06/06/2017.
 */
public class UpcomingOrderAdapter extends BaseRecyclerAdapter {

    private Context context;
    private ArrayList<Order> orders = new ArrayList<>();
    private UpComingOrderFragment upComingOrderFragment;
    private OrderHistoryFragment orderHistoryFragment;
    private String titleOrderCreatedUpto, descriptionOrderCreatedUpto;
    private boolean lastOrders;

    public UpcomingOrderAdapter(Context context, UpComingOrderFragment upComingOrderFragment, ArrayList<Order> orders) {
        super(context);
        this.context = context;
        this.upComingOrderFragment = upComingOrderFragment;
        this.orders = orders;
        mViewType = RECYCLER_VIEW_TYPE_NORMAL;

    }

    public UpcomingOrderAdapter(Context context, ArrayList<Order> orders, OrderHistoryFragment orderHistoryFragment) {
        super(context);
        this.context = context;
        this.orderHistoryFragment = orderHistoryFragment;
        this.orders = orders;
        mViewType = RECYCLER_VIEW_TYPE_NORMAL;

    }

    public ArrayList<Order> getOrders() {
        return orders;
    }

    public void setOrders(ArrayList<Order> orders) {
        this.orders = orders;
    }

    public String getTitleOrderCreatedUpto() {
        return titleOrderCreatedUpto;
    }

    public void setTitleOrderCreatedUpto(String titleOrderCreatedUpto) {
        this.titleOrderCreatedUpto = titleOrderCreatedUpto;
    }

    public String getDescriptionOrderCreatedUpto() {
        return descriptionOrderCreatedUpto;
    }

    public void setDescriptionOrderCreatedUpto(String descriptionOrderCreatedUpto) {
        this.descriptionOrderCreatedUpto = descriptionOrderCreatedUpto;
    }

    public boolean isLastOrders() {
        return lastOrders;
    }

    public void setLastOrders(boolean lastOrders) {
        this.lastOrders = lastOrders;
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
        return new FooterViewHolder(view);
    }

    @Override
    public void onBindViewItemHolder(final ItemHolder holder, final int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.setViewHolder(orders.get(position), position);
    }

    @Override
    public void onBindViewHeaderHolder(HeaderHolder holder, int position) {
    }

    @Override
    public void onBindViewFooterHolder(FooterHolder holder, int position) {
        FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
        footerViewHolder.setViews();
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
        return R.layout.card_footer_order_created_upto;
    }


    /**
     * Called from Upcoming Order Fragment
     * and Order History Fragment
     *
     * @param order
     * @param position
     */
    public void updateOrder(Order order, int position) {
        if (position != -1 && position < orders.size()) {
            orders.set(position, order);
            notifyItemChanged(position);
            notifyDataSetChanged();
        }
    }

    /**
     * ItemView Holder
     */
    class ItemViewHolder extends BaseRecyclerAdapter.ItemHolder {

        private TextView dateTextView, scheduleText, itemsNameTextView, amountTobePaidTextView,
                viewItemsTextView, timeSlot, month, message, reschedule_order_button, completeOrderButton;
        private LinearLayout linearLayout, holderViewItem;
        private CardView cardView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            dateTextView = (TextView) itemView.findViewById(R.id.text_date);
            scheduleText = (TextView) itemView.findViewById(R.id.schedule_text);
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
            completeOrderButton = (TextView) itemView.findViewById(R.id.button_payment_complete);
        }

        public void setViewHolder(final Order order, final int position) {

            try {
                // Rescheduling the order
                if (upComingOrderFragment != null) {
                    if (position == 0) {
                        reschedule_order_button.setVisibility(View.VISIBLE);
                        reschedule_order_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                upComingOrderFragment.startActivityForResult(ConfirmTimeSlotActivity.newInstance(mContext, order, true, true), 5);
                            }
                        });
                    } else {
                        reschedule_order_button.setVisibility(View.GONE);
                    }
                } else {
                    reschedule_order_button.setVisibility(View.GONE);
                }

                if (order.getScheduledText() != null) {
                    if (!order.getScheduledText().isEmpty()) {
                        scheduleText.setText(order.getScheduledText());
                        if (order.getScheduledText().contains("Delivered on")) {
                            scheduleText.setTextColor(mContext.getResources().getColor(R.color.manatee));

                            //delivered but not paid
                            if (order.getColoCode() == 3) {
                                amountTobePaidTextView.setTextColor(mContext.getResources().getColor(R.color.black));
                            } else {
                                amountTobePaidTextView.setTextColor(mContext.getResources().getColor(R.color.manatee));
                            }
                        } else {
                            scheduleText.setTextColor(mContext.getResources().getColor(R.color.black));
                            amountTobePaidTextView.setTextColor(mContext.getResources().getColor(R.color.black));
                        }
                    }
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

                if (order.getNoOfItems() == 1) {
                    itemsNameTextView.setText(order.getNoOfItems() + " item");
                } else {
                    itemsNameTextView.setText(order.getNoOfItems() + " items");
                }

                if (order.getNoOfItems() > 0) {
                    holderViewItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (upComingOrderFragment != null) {
                                upComingOrderFragment.startActivityForResult(OrderItemsActivity.newInstance(context, order, position, false), 4);
                            } else {
                                orderHistoryFragment.startActivityForResult(OrderItemsActivity.newInstance(context, order, position, true), 4);
                            }
                        }
                    });
                }

                message.setText(order.getReminderText());
                amountTobePaidTextView.setText(order.getPaymentText());

                //set color
                setColorForMessage(order.getColoCode());

                if (order.isPaymentComplete()) {
                    completeOrderButton.setVisibility(View.GONE);
                } else {

                    completeOrderButton.setVisibility(View.VISIBLE);
                    completeOrderButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //move to payment
                            context.startActivity(ConfirmTimeSlotActivity.newInstance(mContext, order, true, false));
                        }
                    });
                }

            } catch (IndexOutOfBoundsException i) {
                i.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void setColorForMessage(int colorCode) {
            /**
             * Condition for colors
             * 1- Saffron
             * 2- Blue
             * 3- Red
             */
            switch (colorCode) {
                case 1:
                    message.setTextColor(mContext.getResources().getColor(R.color.neon_carrot));
                    break;
                case 2:
                    message.setTextColor(mContext.getResources().getColor(R.color.md_blue_500));
                    break;
                case 3:
                    message.setTextColor(mContext.getResources().getColor(R.color.accent));
                    break;
                default:
                    message.setTextColor(mContext.getResources().getColor(R.color.neon_carrot));
            }

        }
    }

    /**
     * Footer View Holder
     */
    class FooterViewHolder extends BaseRecyclerAdapter.FooterHolder {

        private TextView title, description;

        public FooterViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.order_create_upto_title);
            description = (TextView) itemView.findViewById(R.id.order_create_upto_desc);
        }

        public void setViews() {
            //title
            if (getTitleOrderCreatedUpto() != null) {
                if (!getTitleOrderCreatedUpto().isEmpty()) {
                    title.setText(getTitleOrderCreatedUpto());
                }
            }

            //description
            if (getDescriptionOrderCreatedUpto() != null) {
                if (!getDescriptionOrderCreatedUpto().isEmpty()) {
                    description.setText(getDescriptionOrderCreatedUpto());
                }
            }
        }
    }


}
