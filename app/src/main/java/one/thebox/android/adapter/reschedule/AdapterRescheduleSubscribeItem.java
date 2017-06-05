package one.thebox.android.adapter.reschedule;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import one.thebox.android.Models.UserItem;
import one.thebox.android.Models.items.SubscribeItem;
import one.thebox.android.Models.reschedule.Delivery;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.DelayDeliveryBottomSheetFragment;
import one.thebox.android.adapter.base.BaseRecyclerAdapter;
import one.thebox.android.api.RequestBodies.MergeSubscriptionRequest;
import one.thebox.android.api.Responses.MergeSubscriptionResponse;
import one.thebox.android.app.TheBox;
import one.thebox.android.fragment.reshedule.FragmentRescheduleSubscribeItem;
import one.thebox.android.util.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by developers on 03/04/17.
 */

public class AdapterRescheduleSubscribeItem extends BaseRecyclerAdapter {

    private ArrayList<Delivery> deliveries;
    private Context context;
    private FragmentRescheduleSubscribeItem fragmentRescheduleSubscribeItem;
    private SubscribeItem subscribeItem;
    private DelayDeliveryBottomSheetFragment.OnDelayActionCompleted onDelayActionCompleted;
    private String mergeDescription;

    /**
     * Constructor
     */
    public AdapterRescheduleSubscribeItem(Context context, FragmentRescheduleSubscribeItem fragmentRescheduleSubscribeItem, ArrayList<Delivery> deliveries,
                                          SubscribeItem subscribeItem, DelayDeliveryBottomSheetFragment.OnDelayActionCompleted onDelayActionCompleted,
                                          String mergeDescription) {
        super(context);
        this.context = context;
        this.fragmentRescheduleSubscribeItem = fragmentRescheduleSubscribeItem;
        this.deliveries = deliveries;
        this.subscribeItem = subscribeItem;
        this.onDelayActionCompleted = onDelayActionCompleted;
        this.mergeDescription = mergeDescription;
        notifyDataSetChanged();
    }

    public ArrayList<Delivery> getDeliveries() {
        return deliveries;
    }

    public void setDeliveries(ArrayList<Delivery> deliveries) {
        this.deliveries = deliveries;
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
        return new ItemHeaderViewHolder(view);
    }

    @Override
    protected FooterHolder getFooterHolder(View view) {
        return null;
    }

    @Override
    public void onBindViewItemHolder(ItemHolder holder, int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.setView(deliveries.get(position));
    }

    @Override
    public void onBindViewHeaderHolder(HeaderHolder holder, int position) {
    }

    @Override
    public void onBindViewFooterHolder(FooterHolder holder, int position) {

    }

    @Override
    public int getItemsCount() {
        return deliveries.size();
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.card_item_reschedule_date;
    }

    @Override
    protected int getItemLayoutId(int position) {
        return 0;
    }

    @Override
    protected int getHeaderLayoutId() {
        return R.layout.card_item_reschudule_header;
    }

    @Override
    protected int getFooterLayoutId() {
        return 0;
    }

    private class ItemViewHolder extends BaseRecyclerAdapter.ItemHolder {

        private TextView textViewDeliveryDate, textViewArrivingAt, mergeTextView;
        private RelativeLayout merge;


        public ItemViewHolder(View itemView) {
            super(itemView);
            merge = (RelativeLayout) itemView.findViewById(R.id.holder_add_button);
            textViewArrivingAt = (TextView) itemView.findViewById(R.id.arriving_at_text);
            textViewDeliveryDate = (TextView) itemView.findViewById(R.id.delivery_date_text);
            mergeTextView = (TextView) itemView.findViewById(R.id.merge);
        }

        public void setView(final Delivery delivery) {

            if (delivery.getDeliveryDate() != null) {
                textViewDeliveryDate.setText(delivery.getDeliveryDate());
            }

            if (delivery.getArrivingAt() != null) {
                textViewArrivingAt.setText(delivery.getArrivingAt());
            }

            mergeTextView.setText("Merge");
            //Holder merge click event
            merge.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    requestMergeDeliveries(delivery);
                }
            });

        }

        public void requestMergeDeliveries(final Delivery delivery) {

            fragmentRescheduleSubscribeItem.showLoader();
            merge.setClickable(false);
            mergeTextView.setText("Merging");


            TheBox.getAPIService()
                    .mergeSubscribeItemWithOrder(PrefUtils.getToken(context), subscribeItem.getUuid(),
                            new MergeSubscriptionRequest(delivery.getOrderUuid(), ""))
                    .enqueue(new Callback<MergeSubscriptionResponse>() {
                        @Override
                        public void onResponse(Call<MergeSubscriptionResponse> call, Response<MergeSubscriptionResponse> response) {
                            fragmentRescheduleSubscribeItem.hideLoader();
                            merge.setClickable(false);
                            mergeTextView.setText("Merged");
                            try {
                                if (response.isSuccessful()) {
                                    if (response.body() != null) {
                                        onDelayActionCompleted.onDelayActionCompleted(response.body().getSubscribeItem());

                                        //setClevertap Event
                                        //setCleverTapEventRescheduleDelivery(response.body().getUserItem(), delivery);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(Call<MergeSubscriptionResponse> call, Throwable t) {
                            fragmentRescheduleSubscribeItem.hideLoader();
                            merge.setClickable(true);
                            mergeTextView.setText("Merge");
                        }
                    });
        }

        /**
         * Clever Tap Event
         */
        public void setCleverTapEventRescheduleDelivery(UserItem userItem, Delivery delivery) {
            try {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("user_item_id", userItem.getId());
                hashMap.put("box_item_id", userItem.getBoxItem().getId());
                hashMap.put("title", userItem.getBoxItem().getTitle());
                hashMap.put("brand", userItem.getBoxItem().getBrand());
                hashMap.put("item_config_id", userItem.getSelectedConfigId());
                hashMap.put("quantitiy", userItem.getQuantity());
                hashMap.put("category", userItem.getBoxItem().getCategoryId());
                hashMap.put("reschedule_type", "merge");
                hashMap.put("merge_order_uuid", delivery.getOrderUuid());
                hashMap.put("delivery_date", delivery.getDeliveryDate());

                TheBox.getCleverTap().event.push("reschedule_delivery", hashMap);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private class ItemHeaderViewHolder extends BaseRecyclerAdapter.HeaderHolder {

        private TextView textView;

        public ItemHeaderViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.merge_description_text);
            if (mergeDescription != null) {
                if (!mergeDescription.isEmpty()) {
                    textView.setText(mergeDescription);
                } else {
                    textView.setVisibility(View.GONE);
                }
            } else {
                textView.setVisibility(View.GONE);
            }
        }
    }

}
