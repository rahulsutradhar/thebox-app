package one.thebox.android.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import io.realm.RealmList;
import one.thebox.android.Models.Order;
import one.thebox.android.Models.UserItem;
import one.thebox.android.R;

public class PaymentDetailAdapter extends BaseRecyclerAdapter {

    private ArrayList<Order> orders = new ArrayList<>();
    private RealmList<UserItem> userItems = new RealmList<>();
    private boolean shouldShowPreviouslyAddedItemHeader = true;

    public PaymentDetailAdapter(Context context) {
        super(context);
        mViewType = RECYCLER_VIEW_TYPE_FOOTER;
    }

    public ArrayList<Order> getOrders() {
        return orders;
    }

    public void setOrders(ArrayList<Order> orders) {
        this.orders.addAll(orders);
        for (Order order : orders) {
            userItems.addAll(order.getUserItems());
        }
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
    public void onBindViewItemHolder(ItemHolder holder, int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.setViewHolder(userItems.get(position));
    }

    @Override
    public void onBindViewHeaderHolder(HeaderHolder holder, int position) {

    }

    @Override
    public void onBindViewFooterHolder(FooterHolder holder, int position) {

    }

    @Override
    public int getItemsCount() {
        return userItems.size();
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_payment_detail;
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
        return R.layout.footer_payment_detail;
    }

    class ItemViewHolder extends ItemHolder {
        private TextView itemText, amountText, itemInfo;

        public ItemViewHolder(View itemView) {
            super(itemView);
            itemText = (TextView) itemView.findViewById(R.id.item_text);
            amountText = (TextView) itemView.findViewById(R.id.amount_text);
            itemInfo = (TextView) itemView.findViewById(R.id.item_info);
        }

        public void setViewHolder(UserItem userItem) {
            itemText.setText(userItem.getBoxItem().getTitle());
            amountText.setText("Rs " + userItem.getTotalPrice());
            if (userItem.getNextDeliveryScheduledAt() == null) {
                itemText.setTextColor(mContext.getResources().getColor(R.color.black));
                amountText.setTextColor(mContext.getResources().getColor(R.color.md_red_700));
            } else {
                itemText.setTextColor(mContext.getResources().getColor(R.color.primary_text_color));
                amountText.setTextColor(mContext.getResources().getColor(R.color.accent));
            }
            if (orders.size() > 1) {
                if (getAdapterPosition() == 0) {
                    if (userItem.getNextDeliveryScheduledAt() == null) {
                        itemInfo.setVisibility(View.VISIBLE);
                        itemInfo.setText("Newly Added Items");
                    }

                } else if (getAdapterPosition() != 0 && userItem.getNextDeliveryScheduledAt() != null && shouldShowPreviouslyAddedItemHeader) {
                    shouldShowPreviouslyAddedItemHeader = false;
                    itemInfo.setVisibility(View.VISIBLE);
                    itemInfo.setText("Previous Items");
                } else {
                    itemInfo.setVisibility(View.GONE);
                }
            } else {
                itemInfo.setVisibility(View.GONE);
            }
        }
    }

    class FooterViewHolder extends FooterHolder {

        private TextView deliveryCharge, amount;

        public FooterViewHolder(View itemView) {
            super(itemView);
            deliveryCharge = (TextView) itemView.findViewById(R.id.delivery_charges);
            amount = (TextView) itemView.findViewById(R.id.amount);
            deliveryCharge.setText("Rs " + getTotalDeliverCharges());
            amount.setText("Rs " + getFinalPaymentAmount());
        }
    }

    public float getTotalDeliverCharges() {
        return 0;
    }

    public float getTotalTax() {

        return 0;
    }

    public float getTotalPrice() {
        float total = 0;
        for (Order order : orders) {
            if (!order.isCart()) {
                total = order.getTotalPriceOfUserItems() + total;
            } else {
                total = total + order.getTotalPriceOfUserItems();
            }
        }
        return total;
    }

    public float getTotalPrice(Order order) {

        if (!order.isCart()) {
            return order.getTotalPrice();
        } else {
            int deliveryCharges = 0;
            for (int i = 0; i < order.getUserItems().size(); i++) {
                deliveryCharges = deliveryCharges + order.getUserItems()
                        .get(i).getBoxItem().getItemConfigById(
                                order.getUserItems().get(i).getSelectedConfigId()
                        ).getPrice() * order.getUserItems().get(i).getQuantity();
            }
            return deliveryCharges;
        }
    }

    public float getFinalPaymentAmount() {
        return getTotalDeliverCharges() + getTotalPrice() + getTotalTax();
    }
}