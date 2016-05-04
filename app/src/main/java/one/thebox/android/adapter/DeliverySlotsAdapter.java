package one.thebox.android.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import one.thebox.android.Models.AddressAndOrder;
import one.thebox.android.Models.BoxItem;
import one.thebox.android.Models.DeliverySlot;
import one.thebox.android.Models.Order;
import one.thebox.android.Models.UserItem;
import one.thebox.android.R;
import one.thebox.android.util.DateTimeUtil;

/**
 * Created by Ajeet Kumar Meena on 12-04-2016.
 */
public class DeliverySlotsAdapter extends BaseRecyclerAdapter {

    ArrayList<Order> orders = new ArrayList<>();
    private UserItem userItem;

    public DeliverySlotsAdapter(Context context, UserItem userItem) {
        super(context);
        this.userItem = userItem;
        mViewType = RECYCLER_VIEW_TYPE_HEADER_FOOTER;
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
        return new FooterViewHolder(view);
    }

    @Override
    public void onBindViewItemHolder(ItemHolder holder, int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.setViews(orders.get(position));
    }

    @Override
    public void onBindViewHeaderHolder(HeaderHolder holder, int position) {
        HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
        headerViewHolder.setViewHolder(userItem);
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
        return R.layout.item_delivery_slots;
    }

    @Override
    protected int getHeaderLayoutId() {
        return R.layout.header_delay_delivery;
    }

    @Override
    protected int getItemLayoutId(int position) {
        return 0;
    }

    @Override
    protected int getFooterLayoutId() {
        return R.layout.footer_deliver_slots;
    }

    public class ItemViewHolder extends ItemHolder {
        private TextView timeTextView, arrivingTextView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            timeTextView = (TextView) itemView.findViewById(R.id.time_text_view);
            arrivingTextView = (TextView) itemView.findViewById(R.id.arriving_time_text_view);
        }

        public void setViews(Order order) {
            Date orderDate = null;
            try {
                orderDate = DateTimeUtil.convertStringToDate
                        (order.getDeliveryScheduleAt());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            timeTextView.setText(AddressAndOrder.getDateString(orderDate));
            arrivingTextView.setText("Arriving in " + DateTimeUtil.getDifferenceAsDay(
                    Calendar.getInstance().getTime(), orderDate
            ) + " days");
        }
    }

    public class HeaderViewHolder extends HeaderHolder {
        TextView deliveryTextView, arrivingTextView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            deliveryTextView = (TextView) itemView.findViewById(R.id.delivery_schedule_text_view);
            arrivingTextView = (TextView) itemView.findViewById(R.id.arriving_text_view);
        }

        public void setViewHolder(UserItem userItem) {
            Date orderDate = null;
            try {
                orderDate = DateTimeUtil.convertStringToDate
                        (userItem.getNextDeliveryScheduledAt());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (userItem.getNextDeliveryScheduledAt() == null) {
                arrivingTextView.setText("This item is in cart. Order this item now.");
            } else {
                arrivingTextView.setText("Arriving in " + DateTimeUtil.getDifferenceAsDay(
                        Calendar.getInstance().getTime(), orderDate
                ) + " days");
            }
            BoxItem.ItemConfig itemConfig = userItem.getBoxItem().getItemConfigById(userItem.getSelectedConfigId());
            deliveryTextView
                    .setText("Delivered to you on frequency of every " + itemConfig.getSubscriptionType());

        }

    }

    public class FooterViewHolder extends FooterHolder {
        private TextView buttonCancel;
        private TextView buttonDeliverInNextCycle;

        public FooterViewHolder(View itemView) {
            super(itemView);
            buttonCancel = (TextView) itemView.findViewById(R.id.button_cancel);
            buttonDeliverInNextCycle = (TextView) itemView.findViewById(R.id.button_deliver_in_next_cycle);
        }


        public void setViews() {
            buttonCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openCancelDialog();
                }
            });
            buttonDeliverInNextCycle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDeliverInNextCycleDialog();
                }
            });

        }

        private void openDeliverInNextCycleDialog() {
            MaterialDialog dialog = new MaterialDialog.Builder(mContext).
                    title("Skip Delivery").
                    customView(R.layout.layout_skip_delivery, true).
                    positiveText("Submit").onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                }
            }).build();
            dialog.getWindow().getAttributes().windowAnimations = R.style.MyAnimation_Window;
            dialog.show();
        }

        private void openCancelDialog() {
            MaterialDialog dialog = new MaterialDialog.Builder(mContext).
                    title("Cancel Subscription").
                    customView(R.layout.layout_cancel_subscription, true).
                    positiveText("Submit").onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                }
            }).build();
            dialog.getWindow().getAttributes().windowAnimations = R.style.MyAnimation_Window;
            dialog.show();
        }
    }

}
