package one.thebox.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;

import org.greenrobot.eventbus.EventBus;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import one.thebox.android.Events.OnHomeTabChangeEvent;
import one.thebox.android.Models.ExploreItem;
import one.thebox.android.Models.UserItem;
import one.thebox.android.Models.saving.Saving;
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

    private Saving saving;
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

    public Saving getSaving() {
        return saving;
    }

    public void setSaving(Saving saving) {
        this.saving = saving;
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
        headerViewHolder.setViews(saving);
    }

    @Override
    public void onBindViewFooterHolder(BaseRecyclerAdapter.FooterHolder holder, int position) {
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

        private TextView monthlyBillText, monthlyBillValue, totalMonthlySavingsText,
                totalMonthlySavingsValue, totalItemsText, totalItemValue;
        private TextView subscriptionBoxTitle, subscriptionBoxDesc;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            monthlyBillText = (TextView) itemView.findViewById(R.id.monthly_bill_text);
            monthlyBillValue = (TextView) itemView.findViewById(R.id.monthly_bill_value);
            totalMonthlySavingsText = (TextView) itemView.findViewById(R.id.total_monthly_savings_text);
            totalMonthlySavingsValue = (TextView) itemView.findViewById(R.id.total_monthly_savings_value);
            totalItemsText = (TextView) itemView.findViewById(R.id.total_items_text);
            totalItemValue = (TextView) itemView.findViewById(R.id.total_items_value);
            subscriptionBoxTitle = (TextView) itemView.findViewById(R.id.subscription_box_title);
            subscriptionBoxDesc = (TextView) itemView.findViewById(R.id.subscription_box_description);
        }

        public void setViews(Saving saving) {
            try {
                if (saving.getMonthlyBill() != null) {
                    monthlyBillText.setText(saving.getMonthlyBill().getTitle());
                    monthlyBillValue.setText(saving.getMonthlyBill().getValue());
                }

                if (saving.getMonthlySaving() != null) {
                    totalMonthlySavingsText.setText(saving.getMonthlySaving().getTitle());
                    totalMonthlySavingsValue.setText(saving.getMonthlySaving().getValue());
                }

                if (saving.getTotalItem() != null) {
                    totalItemsText.setText(saving.getTotalItem().getTitle());
                    totalItemValue.setText(saving.getTotalItem().getValue());
                }

                if (saving.getSuggestionBoxTitle() != null) {
                    subscriptionBoxTitle.setText(Html.fromHtml(saving.getSuggestionBoxTitle()));
                }

                subscriptionBoxDesc.setText(saving.getSuggestionBoxDescription());
            } catch (Exception e) {

            }

        }
    }


}



