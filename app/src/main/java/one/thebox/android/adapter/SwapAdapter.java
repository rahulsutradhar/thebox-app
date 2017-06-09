package one.thebox.android.adapter;

import android.content.Context;
import android.view.View;

import java.util.ArrayList;

import one.thebox.android.Models.items.UserItem;
import one.thebox.android.R;
import one.thebox.android.adapter.base.BaseRecyclerAdapter;

/**
 * Created by Ajeet Kumar Meena on 12-04-2016.
 */
public class SwapAdapter extends BaseRecyclerAdapter {

    ArrayList<UserItem> userItems = new ArrayList<>();

    public SwapAdapter(Context context) {
        super(context);
        mViewType = RECYCLER_VIEW_TYPE_HEADER;
    }

    public void addUserItem(UserItem userItem) {
        userItems.add(userItem);
    }

    public ArrayList<UserItem> getUserItems() {
        return userItems;
    }

    public void setUserItems(ArrayList<UserItem> userItems) {
        this.userItems = userItems;
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
        return userItems.size();
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
    protected int getItemLayoutId(int position) {
        return 0;
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
