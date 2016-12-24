package one.thebox.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.RealmList;
import one.thebox.android.Models.Invoice;
import one.thebox.android.Models.Order;
import one.thebox.android.Models.UserItem;
import one.thebox.android.R;

public class PaymentDetailAdapter extends BaseRecyclerAdapter {

    private Context context;
    private ArrayList<Order> orders = new ArrayList<>();
    private List<UserItem> userItems = new ArrayList<>();
    private boolean shouldShowPreviouslyAddedItemHeader = true;
    private HashMap<Integer, List<UserItem>> orderIdVsUserItems = new HashMap<>();

    public PaymentDetailAdapter(Context context) {
        super(context);
        this.context = context;
        mViewType = RECYCLER_VIEW_TYPE_FOOTER;
    }

    public ArrayList<Order> getOrders() {
        return orders;
    }

    public void setOrders(ArrayList<Order> orders) {
        this.orders.clear();
        this.orders.addAll(orders);
        for (Order order : this.orders) {
            List<UserItem> items = new ArrayList<>();
            for (UserItem item : order.getUserItems()) {
                item.setOrderId(order.getId());
                item.setOrderItemQty(getItemQuantityForOrderId(order, item.getId()));
                items.add(item);

            }
            orderIdVsUserItems.put(order.getId(), items);
            userItems.addAll(items);
        }
    }

    private int getItemQuantityForOrderId(Order order, int userItemId) {
        for (Invoice i : order.getUserItemQuantities()) {
            if (i.getUseritem_id() == userItemId) {
                return i.getInvoice_quantity();
            }
        }
        return 0;
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
        private TextView itemText, amountText, section_heading, payment_status;

        public ItemViewHolder(View itemView) {
            super(itemView);
            itemText = (TextView) itemView.findViewById(R.id.item_text);
            amountText = (TextView) itemView.findViewById(R.id.amount_text);
            section_heading = (TextView) itemView.findViewById(R.id.section_heading);
            payment_status = (TextView) itemView.findViewById(R.id.payment_status);
        }

        public void setViewHolder(UserItem userItem) {
            itemText.setText(userItem.getBoxItem().getTitle());
            amountText.setText("Rs " + userItem.getTotalPriceForAnOrder());
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
                        section_heading.setVisibility(View.VISIBLE);
                        section_heading.setText("Newly Added Items");
                    }

                } else if (getAdapterPosition() != 0 && userItem.getNextDeliveryScheduledAt() != null && shouldShowPreviouslyAddedItemHeader) {
                    shouldShowPreviouslyAddedItemHeader = false;
                    section_heading.setVisibility(View.VISIBLE);
                    section_heading.setText("Previous Items");

                    if (orders.get(1).isPaid()) {
                        payment_status.setVisibility(View.VISIBLE);
                        payment_status.setText("Payment done");
                    }

                }
            }
        }
    }

    class FooterViewHolder extends FooterHolder {

        private TextView deliveryCharge, amount;

        public FooterViewHolder(View itemView) {
            super(itemView);
            deliveryCharge = (TextView) itemView.findViewById(R.id.delivery_charges);
            amount = (TextView) itemView.findViewById(R.id.amount);
            if (getTotalDeliverCharges() == 0) {
                deliveryCharge.setText("Free");
                deliveryCharge.setTextColor(context.getResources().getColor(R.color.md_green_800));
            } else {
                deliveryCharge.setText("Rs " + getTotalDeliverCharges());
            }
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
            if (!order.isPaid()) {
                total = getTotalPriceOfOrder(order) + total;
            }
        }
        return total;
    }

    private float getTotalPriceOfOrder(Order order) {
        float total = 0;
        List<UserItem> items = orderIdVsUserItems.get(order.getId());
        if (items != null)
            for (UserItem item : items) {
                total = total + item.getTotalPriceForAnOrder();
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