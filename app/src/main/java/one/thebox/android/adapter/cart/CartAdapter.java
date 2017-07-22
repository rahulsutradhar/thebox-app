package one.thebox.android.adapter.cart;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;

import java.util.ArrayList;
import java.util.List;

import one.thebox.android.Models.cart.Cart;
import one.thebox.android.Models.items.BoxItem;
import one.thebox.android.R;
import one.thebox.android.adapter.base.BaseRecyclerAdapter;
import one.thebox.android.fragment.CartFragment;

/**
 * Created by developers on 22/07/17.
 */

public class CartAdapter extends BaseRecyclerAdapter {

    private ArrayList<Cart> carts = new ArrayList<>();
    /**
     * GLide Request Manager
     */
    private RequestManager glideRequestManager;
    private CartFragment cartFragment;

    private Context context;

    public CartAdapter(Context context, RequestManager glideRequestManager, CartFragment cartFragment) {
        super(context);
        this.context = context;
        this.glideRequestManager = glideRequestManager;
        this.cartFragment = cartFragment;
    }

    public ArrayList<Cart> getCarts() {
        return carts;
    }

    public void setCarts(ArrayList<Cart> carts) {
        this.carts.clear();
        this.carts.addAll(carts);
        notifyDataSetChanged();
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
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.setViews(carts.get(position), position);
    }

    @Override
    public void onBindViewHeaderHolder(HeaderHolder holder, int position) {

    }

    @Override
    public void onBindViewFooterHolder(FooterHolder holder, int position) {

    }

    @Override
    public int getItemsCount() {
        return carts.size();
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.card_item_cart;
    }

    @Override
    protected int getItemLayoutId(int position) {
        return 0;
    }

    @Override
    protected int getHeaderLayoutId() {
        return 0;
    }

    @Override
    protected int getFooterLayoutId() {
        return 0;
    }

    private class HeaderViewHolder extends BaseRecyclerAdapter.HeaderHolder {
        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class ItemViewHolder extends BaseRecyclerAdapter.ItemHolder {

        private TextView boxTitle;
        private RecyclerView recyclerView;
        private CartItemAdapter cartItemAdapter;
        private LinearLayoutManager verticalLinearLayoutManager;

        public ItemViewHolder(View itemView) {
            super(itemView);
            boxTitle = (TextView) itemView.findViewById(R.id.box_title);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.cart_items);
            this.verticalLinearLayoutManager = new LinearLayoutManager(mContext);
        }

        public void setViews(final Cart cart, final int position) {
            try {
                if (cart.getBoxItems() == null || cart.getBoxItems().isEmpty()) {
                    boxTitle.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    removeItemFromPosition(position);
                } else {
                    boxTitle.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    boxTitle.setText(cart.getBoxTitle());
                    recyclerView.setLayoutManager(verticalLinearLayoutManager);

                    cartItemAdapter = new CartItemAdapter(context, glideRequestManager, cartFragment);
                    cartItemAdapter.setBoxItems(cart.getBoxItems());
                    cartItemAdapter.addOnBoxItemChangeListener(new CartItemAdapter.OnBoxItemChange() {
                        @Override
                        public void onBoxItem(List<BoxItem> boxItemList) {
                            if (boxItemList != null) {
                                if (!boxItemList.isEmpty()) {
                                    carts.get(position).setBoxItems(boxItemList);
                                    setViews(carts.get(position), position);
                                } else {
                                    removeItemFromPosition(position);
                                }
                            } else {
                                removeItemFromPosition(position);
                            }

                        }
                    });
                    recyclerView.setAdapter(cartItemAdapter);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void removeItemFromPosition(int position) {
            carts.remove(position);
            notifyItemRemoved(position);
            notifyDataSetChanged();
        }
    }
}
