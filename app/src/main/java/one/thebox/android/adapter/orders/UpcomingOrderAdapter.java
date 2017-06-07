package one.thebox.android.adapter.orders;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.haha.perflib.Main;

import java.util.ArrayList;

import io.realm.RealmList;
import one.thebox.android.Helpers.OrderHelper;
import one.thebox.android.Models.order.Order;
import one.thebox.android.R;
import one.thebox.android.activity.ConfirmTimeSlotActivity;
import one.thebox.android.activity.MainActivity;
import one.thebox.android.activity.OrderItemsActivity;
import one.thebox.android.adapter.base.BaseRecyclerAdapter;
import one.thebox.android.app.Constants;
import one.thebox.android.fragment.UpComingOrderFragment;


/**
 * Created by Ajeet Kumar Meena on 15-04-2016.
 * <p>
 * Modified by Developers on 06/06/2017.
 */
public class UpcomingOrderAdapter extends BaseRecyclerAdapter {

    private Context context;
    private ArrayList<Order> orders = new ArrayList<>();
    private UpComingOrderFragment upComingOrderFragment;

    public UpcomingOrderAdapter(Context context, UpComingOrderFragment upComingOrderFragment, ArrayList<Order> orders) {
        super(context);
        this.context = context;
        this.upComingOrderFragment = upComingOrderFragment;
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


    public void updateOrder(Order order, int position) {
        if (position != -1 && position < orders.size()) {
            orders.set(position, order);
            notifyItemChanged(position);
            notifyDataSetChanged();
        }
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
                            upComingOrderFragment.startActivityForResult(ConfirmTimeSlotActivity.newInstance(mContext, order, true, true), 4);
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

                if (order.getNoOfItems() > 0) {
                    holderViewItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (upComingOrderFragment != null) {
                                upComingOrderFragment.startActivityForResult(OrderItemsActivity.newInstance(context, order, position), 4);
                            }
                        }
                    });
                }

                message.setText(order.getReminderText());
                amountTobePaidTextView.setText(order.getPaymentText());

                if (order.isPaymentComplete()) {
                    amountTobePaidTextView.setClickable(false);
                    message.setTextColor(mContext.getResources().getColor(R.color.md_blue_500));
                    amountTobePaidTextView.setBackgroundColor(Color.WHITE);
                } else {
                    amountTobePaidTextView.setClickable(true);
                    if (order.isCod() && !order.isPaid()) {
                        message.setTextColor(mContext.getResources().getColor(R.color.md_red_500));
                    } else {
                        message.setTextColor(mContext.getResources().getColor(R.color.secondary_text_color));
                    }
                }
                amountTobePaidTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //move to payment
                        context.startActivity(ConfirmTimeSlotActivity.newInstance(mContext, order, true, false));
                    }
                });

            } catch (IndexOutOfBoundsException i) {
                i.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
