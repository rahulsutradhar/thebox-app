package one.thebox.android.adapter;

import android.content.Context;
import android.view.View;

import java.util.ArrayList;

import one.thebox.android.Models.Box;
import one.thebox.android.R;

/**
 * Created by Ajeet Kumar Meena on 12-04-2016.
 */
public class SwapAdapter extends BaseRecyclerAdapter {

    ArrayList<Box.BoxItem> boxItems = new ArrayList<>();

    public SwapAdapter(Context context) {
        super(context);
        mViewType = RECYCLER_VIEW_TYPE_HEADER;
    }

    public void addBoxItems(Box.BoxItem boxItem) {
        boxItems.add(boxItem);
    }

    public ArrayList<Box.BoxItem> getBoxItems() {
        return boxItems;
    }

    public void setBoxItems(ArrayList<Box.BoxItem> boxItems) {
        this.boxItems = boxItems;
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

    }

    @Override
    public void onBindViewHeaderHolder(HeaderHolder holder, int position) {

    }

    @Override
    public void onBindViewFooterHolder(FooterHolder holder, int position) {

    }

    @Override
    public int getItemsCount() {
        return boxItems.size();
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_swap;
    }

    @Override
    protected int getHeaderLayoutId() {
        return R.layout.header_swap_items;
    }

    @Override
    protected int getFooterLayoutId() {
        return 0;
    }

    public class ItemViewHolder extends ItemHolder {

        public ItemViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class HeaderViewHolder extends HeaderHolder{

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }
}
