package one.thebox.android.adapter;

import android.app.Fragment;
import android.content.Context;
import android.preference.PreferenceActivity;
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
public class SubBillItemsAdapter extends BaseRecyclerAdapter {

    private ArrayList<BillItem.SubBillItem> subBillItems = new ArrayList<>();
    private OnSubItemClickEvent onSubItemClickEvent;

    public SubBillItemsAdapter(Context context, OnSubItemClickEvent onSubItemClickEvent) {
        super(context);
        mViewType = RECYCLER_VIEW_TYPE_HEADER;
        this.onSubItemClickEvent = onSubItemClickEvent;
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
    protected ItemHolder getItemHolder(View view, int position) {
        return null;
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
    public void onBindViewItemHolder(final ItemHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subBillItems.get(position).setSelected(!subBillItems.get(position).isSelected());
                onSubItemClickEvent.onSubItemClickEvent();
                notifyItemChanged(holder.getAdapterPosition());
            }
        });
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.setViewHolder(subBillItems.get(position));
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
        return R.layout.item_item_bill;
    }

    @Override
    protected int getHeaderLayoutId() {
        return R.layout.header_item_item_bill;
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
        private LinearLayout parentLinearLayout;

        public ItemViewHolder(View itemView) {
            super(itemView);
            parentLinearLayout = (LinearLayout) itemView.findViewById(R.id.parent_linear_layout);
        }

        public void setViewHolder(BillItem.SubBillItem subBillItem) {
            if (subBillItem.isSelected()) {
                parentLinearLayout.setBackgroundColor(mContext.getResources().getColor(R.color.manatee));
            } else {
                parentLinearLayout.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            }
        }
    }

    class HeaderViewHolder extends HeaderHolder {
        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface OnSubItemClickEvent {
        void onSubItemClickEvent();
    }
}
