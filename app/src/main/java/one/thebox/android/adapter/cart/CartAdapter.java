package one.thebox.android.adapter.cart;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;

import java.util.ArrayList;
import java.util.List;

import one.thebox.android.Models.mycart.CartProductDetail;
import one.thebox.android.Models.items.Box;
import one.thebox.android.Models.items.BoxItem;
import one.thebox.android.R;
import one.thebox.android.adapter.base.BaseRecyclerAdapter;
import one.thebox.android.fragment.CartFragment;

/**
 * Created by developers on 22/07/17.
 */

public class CartAdapter extends BaseRecyclerAdapter {

    private ArrayList<CartProductDetail> cartProductDetails = new ArrayList<>();
    private ArrayList<Box> suggestedBoxes = new ArrayList<>();
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

    public ArrayList<CartProductDetail> getCartProductDetails() {
        return cartProductDetails;
    }

    public void setCartProductDetails(ArrayList<CartProductDetail> cartProductDetails) {
        this.cartProductDetails.clear();
        this.cartProductDetails.addAll(cartProductDetails);
        notifyDataSetChanged();
    }

    public ArrayList<Box> getSuggestedBoxes() {
        return suggestedBoxes;
    }

    public void setSuggestedBoxes(ArrayList<Box> suggestedBoxes) {
        this.suggestedBoxes = suggestedBoxes;
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
        itemViewHolder.setViews(cartProductDetails.get(position), position);
    }

    @Override
    public void onBindViewHeaderHolder(HeaderHolder holder, int position) {
        HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
        headerViewHolder.setViews(suggestedBoxes);

    }

    @Override
    public void onBindViewFooterHolder(FooterHolder holder, int position) {

    }

    @Override
    public int getItemsCount() {
        return cartProductDetails.size();
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
        return R.layout.card_suggested_boxes_cart;
    }

    @Override
    protected int getFooterLayoutId() {
        return 0;
    }

    private class HeaderViewHolder extends BaseRecyclerAdapter.HeaderHolder {

        private RecyclerView recyclerViewSuggestedBox;
        private SuggestedBoxAdapter suggestedBoxAdapter;
        private LinearLayoutManager horizontalLinearLayoutManager;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            recyclerViewSuggestedBox = (RecyclerView) itemView.findViewById(R.id.suggested_box_list);
            this.horizontalLinearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        }

        public void setViews(ArrayList<Box> suggestedBoxes) {
            if (suggestedBoxAdapter == null || null == recyclerViewSuggestedBox.getAdapter()) {
                suggestedBoxAdapter = new SuggestedBoxAdapter(context, glideRequestManager, cartFragment);
                recyclerViewSuggestedBox.setLayoutManager(horizontalLinearLayoutManager);
                suggestedBoxAdapter.setSuggestedBoxes(suggestedBoxes);
                suggestedBoxAdapter.setViewType(RECYCLER_VIEW_TYPE_NORMAL);
                recyclerViewSuggestedBox.setAdapter(suggestedBoxAdapter);
                SnapHelper snapHelper = new PagerSnapHelper();
                snapHelper.attachToRecyclerView(recyclerViewSuggestedBox);
            } else {
                suggestedBoxAdapter.setSuggestedBoxes(suggestedBoxes);
                suggestedBoxAdapter.setViewType(RECYCLER_VIEW_TYPE_NORMAL);
            }
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

        public void setViews(final CartProductDetail cartProductDetail, final int position) {
            try {
                if (cartProductDetail.getBoxItems() == null || cartProductDetail.getBoxItems().isEmpty()) {
                    boxTitle.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    removeItemFromPosition(position);
                } else {
                    boxTitle.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    boxTitle.setText(cartProductDetail.getBoxTitle());
                    recyclerView.setLayoutManager(verticalLinearLayoutManager);

                    cartItemAdapter = new CartItemAdapter(context, glideRequestManager, cartFragment);
                    cartItemAdapter.setBoxItems(cartProductDetail.getBoxItems());
                    cartItemAdapter.addOnBoxItemChangeListener(new CartItemAdapter.OnBoxItemChange() {
                        @Override
                        public void onBoxItem(List<BoxItem> boxItemList) {
                            if (boxItemList != null) {
                                if (!boxItemList.isEmpty()) {
                                    cartProductDetails.get(position).setBoxItems(boxItemList);
                                    setViews(cartProductDetails.get(position), position);
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
            cartProductDetails.remove(position);
            notifyItemRemoved(position);
            notifyDataSetChanged();
        }
    }
}
