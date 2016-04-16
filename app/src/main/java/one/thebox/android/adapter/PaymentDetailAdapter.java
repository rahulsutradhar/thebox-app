package one.thebox.android.adapter;

import android.content.Context;
import android.view.View;

import java.util.ArrayList;

import one.thebox.android.Models.BillItem;
import one.thebox.android.R;

public class PaymentDetailAdapter extends BaseRecyclerAdapter {

    private ArrayList<BillItem.SubBillItem> subBillItems = new ArrayList<>();

    public PaymentDetailAdapter(Context context) {
        super(context);
        mViewType = RECYCLER_VIEW_TYPE_FOOTER;
    }

    public void addSubBillItem(BillItem.SubBillItem subBillItem) {
        subBillItems.add(subBillItem);
    }

    public ArrayList<BillItem.SubBillItem> getSubBillItems() {
        return subBillItems;
    }

    public void setSubBillItems(ArrayList<BillItem.SubBillItem> subBillItems) {
        this.subBillItems = subBillItems;
    }

    @Override
    protected ItemHolder getItemHolder(View view) {
        return new ItemViewHolder(view);
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

    }

    @Override
    public void onBindViewHeaderHolder(HeaderHolder holder, int position) {

    }

    @Override
    public void onBindViewFooterHolder(FooterHolder holder, int position) {

    }

    @Override
    public int getItemsCount() {
        return subBillItems.size();
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
    protected int getFooterLayoutId() {
        return R.layout.footer_payment_detail;
    }

    class ItemViewHolder extends ItemHolder {

        public ItemViewHolder(View itemView) {
            super(itemView);
        }
    }

    class FooterViewHolder extends FooterHolder {

        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }
}