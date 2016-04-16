package one.thebox.android.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

import one.thebox.android.Models.BillItem;
import one.thebox.android.R;
import one.thebox.android.activity.MainActivity;
import one.thebox.android.fragment.BillsFragment;

/**
 * Created by Ajeet Kumar Meena on 15-04-2016.
 */
public class BillsItemAdapter extends BaseRecyclerAdapter implements SubBillItemsAdapter.OnSubItemClickEvent {

    private ArrayList<BillItem> billItems = new ArrayList<>();
    private boolean isAnyItemSelected = false;

    public BillsItemAdapter(Context context) {
        super(context);
        mViewType = RECYCLER_VIEW_TYPE_HEADER;
    }

    public boolean isAnyItemSelected() {
        return isAnyItemSelected;
    }

    public void setAnyItemSelected(boolean anyItemSelected) {
        isAnyItemSelected = anyItemSelected;
    }

    public void addBillItem(BillItem billItem) {
        billItems.add(billItem);
    }

    public ArrayList<BillItem> getBillItems() {
        return billItems;
    }

    public void setBillItems(ArrayList<BillItem> billItems) {
        this.billItems = billItems;
    }

    @Override
    protected ItemHolder getItemHolder(View view) {
        return new ItemViewHolder(view);
    }

    @Override
    protected HeaderHolder getHeaderHolder(View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    protected FooterHolder getFooterHolder(View view) {
        return null;
    }

    @Override
    public void onBindViewItemHolder(ItemHolder holder, int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.setViewHolder(billItems.get(position).getSubBillItems());
    }

    @Override
    public void onBindViewHeaderHolder(HeaderHolder holder, int position) {
        HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
        headerViewHolder.setViews(!isAnyItemSelected);
    }

    @Override
    public void onBindViewFooterHolder(FooterHolder holder, int position) {

    }

    @Override
    public int getItemsCount() {
        return billItems.size();
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_bill;
    }

    @Override
    protected int getHeaderLayoutId() {
        return R.layout.header_item_bill;
    }

    @Override
    protected int getFooterLayoutId() {
        return 0;
    }

    @Override
    public void onSubItemClickEvent() {
        isAnyItemSelected = false;
        outerLoop:
        for (BillItem billItem : billItems) {
            for (BillItem.SubBillItem subBillItem : billItem.getSubBillItems()) {
                if (subBillItem.isSelected()) {
                    isAnyItemSelected = true;
                    android.support.v4.app.Fragment fragment = ((MainActivity) mContext).getSupportFragmentManager().findFragmentById(R.id.frame);
                    if (fragment instanceof BillsFragment) {
                        ((BillsFragment) fragment).setButtonState(false);
                    }
                    break outerLoop;
                }
            }
        }
        if (!isAnyItemSelected) {
            android.support.v4.app.Fragment fragment = ((MainActivity) mContext).getSupportFragmentManager().findFragmentById(R.id.frame);
            if (fragment instanceof BillsFragment) {
                ((BillsFragment) fragment).setButtonState(true);
            }
        }
        notifyItemChanged(0);
    }


    class ItemViewHolder extends ItemHolder {
        private RecyclerView recyclerView;
        private SubBillItemsAdapter subBillItemsAdapter;

        public ItemViewHolder(View itemView) {
            super(itemView);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.recycler_view);
        }

        public void setViewHolder(ArrayList<BillItem.SubBillItem> subBillItems) {
            subBillItemsAdapter = new SubBillItemsAdapter(mContext, BillsItemAdapter.this);
            subBillItemsAdapter.setSubBillItems(subBillItems);
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            recyclerView.setAdapter(subBillItemsAdapter);
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
}
