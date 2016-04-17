package one.thebox.android.adapter;

import android.content.Context;
import android.view.View;

import java.util.ArrayList;

import one.thebox.android.Models.Box;
import one.thebox.android.R;

/**
 * Created by Ajeet Kumar Meena on 17-04-2016.
 */
public class OrderDetailAdapter extends BaseRecyclerAdapter{

    private ArrayList<Box.BoxItem> boxItems = new ArrayList<>();

    public OrderDetailAdapter(Context context) {
        super(context);
    }

    public void addBoxItem(Box.BoxItem boxItem) {
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
        return null;
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
        return R.layout.item_order_details;
    }

    @Override
    protected int getHeaderLayoutId() {
        return 0;
    }

    @Override
    protected int getFooterLayoutId() {
        return 0;
    }

    private class ItemViewHolder extends  ItemHolder{

        public ItemViewHolder(View itemView) {
            super(itemView);
        }
    }
}
