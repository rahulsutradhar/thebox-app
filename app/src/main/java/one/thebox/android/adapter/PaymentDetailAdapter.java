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
            itemText.setText(order.getItemString() + " Rs");
            amountText.setText(order.getTotalPrice() + " Rs");
        }
    }

    class FooterViewHolder extends FooterHolder {

        private TextView deliveryCharge, tax, amount;

        public FooterViewHolder(View itemView) {
            super(itemView);
            deliveryCharge = (TextView) itemView.findViewById(R.id.delivery_charges);
            tax = (TextView) itemView.findViewById(R.id.tax);
            amount = (TextView) itemView.findViewById(R.id.amount);
            deliveryCharge.setText("0" + " Rs");
            tax.setText(getTotalTax() + " Rs");
            amount.setText(getTotalPrice() + " Rs");

        }
    }

    public float getTotalDeliverCharges() {
        float total = 0;
        for (Order order : orders) {
            total = Float.valueOf(order.getDeliveryCharges()) + total;
        }
        return total;
    }

    public float getTotalTax() {
        float total = 0;
        for (Order order : orders) {
            total = Float.valueOf(order.getTax()) + total;
        }
        return total;
    }

    public float getTotalPrice() {
        float total = 0;
        for (Order order : orders) {
            total = Float.valueOf(order.getTotalPrice()) + total;
        }
        return total;
    }
}