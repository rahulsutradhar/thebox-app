package one.thebox.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;

import java.util.ArrayList;
import java.util.List;

import one.thebox.android.Models.ExploreItem;
import one.thebox.android.Models.UserItem;
import one.thebox.android.Models.user.OrderedUserItem;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.MontserratTextView;
import one.thebox.android.ViewHelper.ShowcaseHelper;
import one.thebox.android.activity.MainActivity;
import one.thebox.android.api.RestClient;
import one.thebox.android.app.TheBox;
import one.thebox.android.util.CoreGsonUtils;
import one.thebox.android.util.PrefUtils;

/**
 * Created by Ajeet Kumar Meena on 11-04-2016.
 */
public class MyBoxRecyclerAdapter extends BaseRecyclerAdapter {

    private String monthly_bill;
    private String total_no_of_items;
    private int stickyHeaderHeight = 0;

    /**
     * GLide Request Manager
     */
    private RequestManager glideRequestManager;

    /**
     * User Ordered Item
     */
    private List<OrderedUserItem> orderedUserItems = new ArrayList<>();

    public MyBoxRecyclerAdapter(Context context, RequestManager glideRequestManager) {
        super(context);
        this.glideRequestManager = glideRequestManager;
    }

    public List<OrderedUserItem> getOrderedUserItems() {
        return orderedUserItems;
    }

    public void setOrderedUserItems(List<OrderedUserItem> orderedUserItems) {
        this.orderedUserItems.clear();
        this.orderedUserItems.addAll(orderedUserItems);
        notifyDataSetChanged();
    }

    public String getMonthly_bill() {
        return monthly_bill;
    }

    public void setMonthly_bill(String monthly_bill) {
        this.monthly_bill = monthly_bill;
    }

    public String getTotal_no_of_items() {
        return total_no_of_items;
    }

    public void setTotal_no_of_items(String total_no_of_items) {
        this.total_no_of_items = total_no_of_items;
    }

    public int getStickyHeaderHeight() {
        return stickyHeaderHeight;
    }

    public void setStickyHeaderHeight(int stickyHeaderHeight) {
        this.stickyHeaderHeight = stickyHeaderHeight;
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


        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.setViews(orderedUserItems.get(position), position);

        if (PrefUtils.getBoolean(TheBox.getInstance(), "home_tutorial", true) && (!RestClient.is_in_development)) {
            new ShowcaseHelper((Activity) mContext, 3)
                    .show("My Boxes", "Edit and keep track of all items being delivered to you regularly", holder.itemView)
                    .setOnCompleteListener(new ShowcaseHelper.OnCompleteListener() {
                        @Override
                        public void onComplete() {
                            PrefUtils.putBoolean(TheBox.getInstance(), "home_tutorial", false);
                            new ShowcaseHelper((Activity) mContext, 3)
                                    .show("My Boxes", "Edit and keep track of all items being delivered to you regularly", holder.itemView);

                        }
                    });
        }
    }

    @Override
    public void onBindViewHeaderHolder(BaseRecyclerAdapter.HeaderHolder holder, int position) {
        HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
        headerViewHolder.setViews("12344", "34");
    }

    @Override
    public void onBindViewFooterHolder(FooterHolder holder, int position) {

    }

    @Override
    public int getItemsCount() {
        return orderedUserItems.size();
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_subscriptions;
    }

    @Override
    protected int getHeaderLayoutId() {
        return R.layout.item_subscriptions_savings_card;
    }

    @Override
    protected int getItemLayoutId(int position) {
        return 0;
    }

    @Override
    protected int getFooterLayoutId() {
        return 0;
    }

    public class ItemViewHolder extends BaseRecyclerAdapter.ItemHolder {
        private SearchDetailAdapter userItemRecyclerAdapter;
        private RecyclerView recyclerViewUserItems;
        private TextView title;

        private LinearLayoutManager verticalLinearLayoutManager;
        private View.OnClickListener openBoxListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String exploreItemString = CoreGsonUtils.toJson(new ExploreItem(orderedUserItems.get(getAdapterPosition()).getBoxId(),
                        orderedUserItems.get(getAdapterPosition()).getTitle()));

                mContext.startActivity(new Intent(mContext, MainActivity.class)
                        .putExtra(MainActivity.EXTRA_ATTACH_FRAGMENT_DATA, exploreItemString)
                        .putExtra(MainActivity.EXTRA_ATTACH_FRAGMENT_NO, 5));
            }
        };

        private View.OnClickListener viewItemsListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyItemChanged(getAdapterPosition());
            }
        };

        public ItemViewHolder(View itemView) {
            super(itemView);
            this.recyclerViewUserItems = (RecyclerView) itemView.findViewById(R.id.useritem_list_recycler_view);
            this.title = (TextView) itemView.findViewById(R.id.title);

            recyclerViewUserItems.setNestedScrollingEnabled(false);
            recyclerViewUserItems.setItemViewCacheSize(20);
            recyclerViewUserItems.setDrawingCacheEnabled(true);
            recyclerViewUserItems.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

            this.verticalLinearLayoutManager = new LinearLayoutManager(mContext);

        }

        public void setViews(final OrderedUserItem orderedUserItem, final int position) {

            try {
                if (orderedUserItem.getUserItems() == null || orderedUserItem.getUserItems().isEmpty()) {
                    this.recyclerViewUserItems.setVisibility(View.GONE);
                    this.title.setVisibility(View.GONE);
                } else {
                    this.recyclerViewUserItems.setVisibility(View.VISIBLE);
                    this.recyclerViewUserItems.setLayoutManager(verticalLinearLayoutManager);
                    this.title.setVisibility(View.VISIBLE);

                    //set title the box category
                    this.title.setText(orderedUserItem.getTitle());

                    this.userItemRecyclerAdapter = new SearchDetailAdapter(mContext, glideRequestManager);
                    this.userItemRecyclerAdapter.setBoxItems(null, orderedUserItem.getUserItems());
                    this.userItemRecyclerAdapter.addOnUserItemChangeListener(new SearchDetailAdapter.OnUserItemChange() {
                        @Override
                        public void onUserItemChange(List<UserItem> userItems) {

                            orderedUserItems.get(position).setAllUserItems(userItems);
                            setViews(orderedUserItems.get(position), position);

                        }
                    });
                    this.recyclerViewUserItems.setAdapter(userItemRecyclerAdapter);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class HeaderViewHolder extends BaseRecyclerAdapter.HeaderHolder {

        private MontserratTextView monthly_bill;
        private MontserratTextView total_no_of_items;


        public HeaderViewHolder(View itemView) {
            super(itemView);
            this.monthly_bill = (MontserratTextView) itemView.findViewById(R.id.monthly_bill);
            this.total_no_of_items = (MontserratTextView) itemView.findViewById(R.id.total_no_of_items);
        }

        public void setViews(String monthly_bill, String total_no_of_items) {

            this.monthly_bill.setText(monthly_bill);
            this.total_no_of_items.setText(total_no_of_items);

        }
    }
}



