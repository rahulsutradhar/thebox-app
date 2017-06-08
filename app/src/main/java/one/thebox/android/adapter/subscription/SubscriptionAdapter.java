package one.thebox.android.adapter.subscription;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.RequestManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import one.thebox.android.Events.DisplayProductForSavingsEvent;
import one.thebox.android.Models.ExploreItem;
import one.thebox.android.Models.UserItem;
import one.thebox.android.Models.items.SubscribeItem;
import one.thebox.android.Models.saving.Saving;
import one.thebox.android.Models.user.Subscription;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.ShowcaseHelper;
import one.thebox.android.activity.MainActivity;
import one.thebox.android.adapter.SearchDetailAdapter;
import one.thebox.android.adapter.base.BaseRecyclerAdapter;
import one.thebox.android.api.RestClient;
import one.thebox.android.app.TheBox;
import one.thebox.android.util.CoreGsonUtils;
import one.thebox.android.util.PrefUtils;

/**
 * Created by Ajeet Kumar Meena on 11-04-2016.
 * <p>
 * Updated by Developers on 05-06-2017.
 */
public class SubscriptionAdapter extends BaseRecyclerAdapter {

    private Saving saving;
    private int stickyHeaderHeight = 0;
    private Context context;

    /**
     * GLide Request Manager
     */
    private RequestManager glideRequestManager;

    /**
     * User Subscription
     */
    private ArrayList<Subscription> subscriptions = new ArrayList<>();

    public SubscriptionAdapter(Context context, RequestManager glideRequestManager) {
        super(context);
        this.context = context;
        this.glideRequestManager = glideRequestManager;
    }

    public ArrayList<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setSubscriptions(ArrayList<Subscription> subscriptions) {
        this.subscriptions.clear();
        this.subscriptions.addAll(subscriptions);
        notifyDataSetChanged();
    }

    public Saving getSaving() {
        return saving;
    }

    public void setSaving(Saving saving) {
        this.saving = saving;
        notifyDataSetChanged();
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
        itemViewHolder.setViews(subscriptions.get(position), position);

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
        return subscriptions.size();
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
        private SubscribeItemAdapter subscribeItemAdapter;
        private RecyclerView recyclerViewUserItems;
        private TextView title;

        private LinearLayoutManager verticalLinearLayoutManager;

        public ItemViewHolder(View itemView) {
            super(itemView);
            this.recyclerViewUserItems = (RecyclerView) itemView.findViewById(R.id.subscription_list_recycler_view);
            this.title = (TextView) itemView.findViewById(R.id.title);

            recyclerViewUserItems.setNestedScrollingEnabled(false);
            recyclerViewUserItems.setItemViewCacheSize(20);
            recyclerViewUserItems.setDrawingCacheEnabled(true);
            recyclerViewUserItems.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

            this.verticalLinearLayoutManager = new LinearLayoutManager(mContext);

        }

        public void setViews(final Subscription subscription, final int position) {

            try {
                if (subscription.getSubscribeItems() == null || subscription.getSubscribeItems().isEmpty()) {
                    this.recyclerViewUserItems.setVisibility(View.GONE);
                    this.title.setVisibility(View.GONE);
                    subscriptions.remove(position);
                    notifyItemRemoved(position);
                    notifyDataSetChanged();

                } else {
                    this.recyclerViewUserItems.setVisibility(View.VISIBLE);
                    this.recyclerViewUserItems.setLayoutManager(verticalLinearLayoutManager);
                    this.title.setVisibility(View.VISIBLE);

                    //set title the box category
                    this.title.setText(subscription.getTitle());

                    this.subscribeItemAdapter = new SubscribeItemAdapter(mContext, glideRequestManager);
                    this.subscribeItemAdapter.setSubscribeItems(subscription.getSubscribeItems());
                    this.subscribeItemAdapter.addOnSubscribeItemChangeListener(new SearchDetailAdapter.OnSubscribeItemChange() {
                        @Override
                        public void onSubscribeItem(List<SubscribeItem> subscribeItems) {
                            subscriptions.get(position).setSubscribeItems(subscribeItems);
                            setViews(subscriptions.get(position), position);
                        }
                    });
                    this.recyclerViewUserItems.setAdapter(subscribeItemAdapter);
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
        private RelativeLayout subscribeNext;

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
            subscribeNext = (RelativeLayout) itemView.findViewById(R.id.subscribe_next);
        }

        public void setViews(final Saving saving) {
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

                subscribeNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //pass info to StoreFragment and display products
                        EventBus.getDefault().post(new DisplayProductForSavingsEvent(saving.getSuggestedBoxUuid(), saving.getSuggestionBoxName()));
                    }
                });

            } catch (Exception e) {

            }

        }

        /**
         * Clvertap Event
         */

        public void setCleverTapEventSubscribeNextSavings(Saving saving) {
            try {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("box_id", saving.getSuggestionBoxId());
                hashMap.put("box_name", saving.getSuggestionBoxName());

                TheBox.getCleverTap().event.push("subscribe_next_savings", hashMap);


            } catch (Exception e) {

            }
        }
    }


}



