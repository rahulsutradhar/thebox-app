package one.thebox.android.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import one.thebox.android.Models.Order;
import one.thebox.android.R;

public class PaymentDetailAdapter extends BaseRecyclerAdapter {

    private ArrayList<Order> orders = new ArrayList<>();

    public PaymentDetailAdapter(Context context) {
        super(context);
        mViewType = RECYCLER_VIEW_TYPE_FOOTER;
    }

    public ArrayList<Order> getOrders() {
        return orders;
    }

    public void setOrders(ArrayList<Order> orders) {
        this.orders.addAll(orders);
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
        itemViewHolder.setViewHolder(orders.get(position));
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
        private TextView itemText, amountText;

        public ItemViewHolder(View itemView) {
            super(itemView);
            itemText = (TextView) itemView.findViewById(R.id.item_text);
            amountText = (TextView) itemView.findViewById(R.id.amount_text);
        }

        public void setViewHolder(Order order) {
            itemText.setText(order.getItemString());
            amountText.setText("Rs " + getTotalPrice(order));
        }
    }

    class FooterViewHolder extends FooterHolder {

        private TextView deliveryCharge, tax, amount;

        public FooterViewHolder(View itemView) {
            super(itemView);
            deliveryCharge = (TextView) itemView.findViewById(R.id.delivery_charges);
            amount = (TextView) itemView.findViewById(R.id.amount);
            deliveryCharge.setText("Rs " + getTotalDeliverCharges());
            tax.setText("Rs " + getTotalTax());
            amount.setText("Rs " + getFinalPaymentAmount());

        }
    }

    public float getTotalDeliverCharges() {
        float total = 0;
        for (Order order : orders) {
            total = order.getDeliveryCharges() + total;
        }
        return total;
    }

    public float getTotalTax() {

        return 0;
    }

    public float getTotalPrice() {
        float total = 0;
        for (Order order : orders) {
            if (!order.isCart()) {
                total = order.getTotalPrice() + total;
            } else {
                int deliveryCharges = 0;
                for (int i = 0; i < order.getUserItems().size(); i++) {
                    deliveryCharges = deliveryCharges + order.getUserItems()
                            .get(i).getBoxItem().getItemConfigById(
                                    order.getUserItems().get(i).getSelectedConfigId()
                            ).getPrice();
                }
                total = deliveryCharges + total;
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
                        ).getPrice();
            }
            return deliveryCharges;
        }
    }

    public float getFinalPaymentAmount() {
        return getTotalDeliverCharges() + getTotalPrice() + getTotalTax();
    }
}