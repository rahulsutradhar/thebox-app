package one.thebox.android.adapter.subscription;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.RequestManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import one.thebox.android.Events.EventUnsubscribedSubscribedItem;
import one.thebox.android.Events.UpdateSavingsEvent;
import one.thebox.android.Events.UpdateUpcomingDeliveriesEvent;
import one.thebox.android.Models.items.ItemConfig;
import one.thebox.android.Models.items.UserItem;
import one.thebox.android.Models.items.SubscribeItem;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.BoxLoader;
import one.thebox.android.ViewHelper.DelayDeliveryBottomSheetFragment;
import one.thebox.android.adapter.base.BaseRecyclerAdapter;
import one.thebox.android.api.RequestBodies.subscribeitem.UpdateItemConfigSubscribeItemRequest;
import one.thebox.android.api.RequestBodies.subscribeitem.UpdateQuantitySubscribeItemRequest;
import one.thebox.android.api.Responses.subscribeitem.UpdateItemConfigSubscribeItemResponse;
import one.thebox.android.api.Responses.subscribeitem.UpdateQuantitySubscribeItemResponse;
import one.thebox.android.app.Constants;
import one.thebox.android.app.TheBox;
import one.thebox.android.fragment.EditItemFragment;
import one.thebox.android.fragment.SizeAndFrequencyBottomSheetDialogFragment;
import one.thebox.android.util.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by developers on 08/06/17.
 */

public class SubscribeItemAdapter extends BaseRecyclerAdapter {

    private Context context;
    /**
     * GLide Request Manager
     */
    private RequestManager glideRequestManager;

    /**
     * Interface
     */
    private SubscribeItemAdapter.OnSubscribeItemChange onSubscribeItemChange;

    /**
     * User Subscribed Item
     */
    private List<SubscribeItem> subscribeItems = new ArrayList<>();


    public SubscribeItemAdapter(Context context, RequestManager glideRequestManager) {
        super(context);
        this.context = context;
        this.glideRequestManager = glideRequestManager;
    }

    public List<SubscribeItem> getSubscribeItems() {
        return subscribeItems;
    }

    public void setSubscribeItems(List<SubscribeItem> subscribeItems) {
        this.subscribeItems = subscribeItems;
    }

    /**
     * Interface Called from SubscriptionAdapter
     *
     * @param onSubscribeItemChange
     */
    public void addOnSubscribeItemChangeListener(SubscribeItemAdapter.OnSubscribeItemChange onSubscribeItemChange) {
        this.onSubscribeItemChange = onSubscribeItemChange;
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
        return null;
    }

    @Override
    protected FooterHolder getFooterHolder(View view) {
        return null;
    }

    @Override
    public void onBindViewItemHolder(ItemHolder holder, int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.setViews(subscribeItems.get(position), position);

    }

    @Override
    public void onBindViewHeaderHolder(HeaderHolder holder, int position) {

    }

    @Override
    public void onBindViewFooterHolder(FooterHolder holder, int position) {

    }

    @Override
    public int getItemsCount() {
        return subscribeItems.size();
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_subscribe_item;
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

    private class ItemViewHolder extends BaseRecyclerAdapter.ItemHolder {

        private TextView productName, arrivingTime, config, addButton,
                subtractButton, noOfItemSelected, frequency, price, editSubscription, savingtextView;
        private ImageView productImageView;
        private RelativeLayout quantityHolder;

        public ItemViewHolder(View itemView) {
            super(itemView);
            productName = (TextView) itemView.findViewById(R.id.product_name);
            arrivingTime = (TextView) itemView.findViewById(R.id.arriving_time);
            config = (TextView) itemView.findViewById(R.id.config);
            productImageView = (ImageView) itemView.findViewById(R.id.product_image_view);
            addButton = (TextView) itemView.findViewById(R.id.button_add);
            subtractButton = (TextView) itemView.findViewById(R.id.button_subtract);
            noOfItemSelected = (TextView) itemView.findViewById(R.id.no_of_item_selected);
            quantityHolder = (RelativeLayout) itemView.findViewById(R.id.layout_quantity_holder);
            price = (TextView) itemView.findViewById(R.id.price);
            frequency = (TextView) itemView.findViewById(R.id.frequency);
            editSubscription = (TextView) itemView.findViewById(R.id.user_item_edit_button);
            savingtextView = (TextView) itemView.findViewById(R.id.text_view_savings);
        }

        private void setViews(final SubscribeItem subscribeItem, final int position) {

            try {
                quantityHolder.setVisibility(View.VISIBLE);
                noOfItemSelected.setText(String.valueOf(subscribeItem.getQuantity()));
                ItemConfig selectedItemConfig = subscribeItem.getSelectedItemConfig();
                price.setText(Constants.RUPEE_SYMBOL + " " + (subscribeItem.getQuantity() * selectedItemConfig.getPrice()));
                frequency.setText("Repeat " + selectedItemConfig.getSubscriptionText().toLowerCase());

                productName.setText(subscribeItem.getBoxItem().getTitle());

                //item config Size
                if (selectedItemConfig.getSize() == 0) {
                    config.setText(selectedItemConfig.getQuantity() + " " + selectedItemConfig.getSizeUnit() + " " + selectedItemConfig.getItemType());
                } else {
                    config.setText(selectedItemConfig.getSize() + " " + selectedItemConfig.getSizeUnit() + " " + selectedItemConfig.getItemType());
                }
                arrivingTime.setText(subscribeItem.getArrivingAt());


                //saving for selected item config
                if (subscribeItem.getSubscribedSavingText() != null) {
                    if (!subscribeItem.getSubscribedSavingText().isEmpty()) {
                        savingtextView.setVisibility(View.VISIBLE);
                        savingtextView.setText(subscribeItem.getSubscribedSavingText());
                    } else {
                        savingtextView.setVisibility(View.GONE);
                        savingtextView.setText("");
                    }
                } else {
                    savingtextView.setVisibility(View.GONE);
                    savingtextView.setText("");
                }

                glideRequestManager.load(selectedItemConfig.getItemImage())
                        .centerCrop()
                        .crossFade()
                        .into(productImageView);
            } catch (Exception e) {
                e.printStackTrace();
            }


            /**
             *  Edit Subscription Botton Dialog upon clicking Edit Subscription
             */

            editSubscription.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //edit subscription
                    doEditSubscription(subscribeItem, position);

                }
            });

            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //increase quantity
                    updateQuantity(subscribeItem, position, (subscribeItem.getQuantity() + 1));
                }
            });
            subtractButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (subscribeItem.getQuantity() > 1) {
                        //decrease quantity
                        updateQuantity(subscribeItem, position, (subscribeItem.getQuantity() - 1));
                    }//Delete Subscribe Item
                    else if (subscribeItem.getQuantity() == 1) {
                        MaterialDialog dialog = new MaterialDialog.Builder(mContext).
                                title("Unsubscribe " + subscribeItem.getBoxItem().getTitle()).
                                positiveText("Cancel")
                                .negativeText("Unsubscribe").
                                        onNegative(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                //remove or delete Subscribe Item
                                                updateQuantity(subscribeItem, position, 0);
                                            }
                                        }).content("Unsubscribing " + subscribeItem.getBoxItem().getTitle() + " will remove it from all subsequent orders. Are you sure you want to unsubscribe?").build();
                        dialog.getWindow().getAttributes().windowAnimations = R.style.MyAnimation_Window;
                        dialog.show();
                    } else {
                        Toast.makeText(TheBox.getInstance(), "Item count could not be negative", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        /**
         * Edit Subscription
         */
        private void doEditSubscription(final SubscribeItem subscribeItem, final int position) {
            final EditItemFragment dialogFragment = EditItemFragment.newInstance();

            dialogFragment.show(((AppCompatActivity) mContext).getSupportFragmentManager()
                    , EditItemFragment.TAG);
            dialogFragment.attachListener(new EditItemFragment.OnEditItemoptionSelected() {
                @Override
                public void onEditItemoptionSelected(int actionUserItemSubscription) {

                    dialogFragment.dismiss();

                    // true if change_size was clicked
                    // false otherwise
                    switch (actionUserItemSubscription) {
                        case 1:
                            //Update ItemConfig
                            subscribeItem.getBoxItem().setSelectedItemConfig(subscribeItem.getSelectedItemConfig());

                            final SizeAndFrequencyBottomSheetDialogFragment dialogFragment = new SizeAndFrequencyBottomSheetDialogFragment(
                                    subscribeItem.getBoxItem().getItemConfigs(), subscribeItem.getSelectedItemConfig());
                            dialogFragment.show(((AppCompatActivity) mContext).getSupportFragmentManager()
                                    , SizeAndFrequencyBottomSheetDialogFragment.TAG);
                            dialogFragment.attachListener(new SizeAndFrequencyBottomSheetDialogFragment.OnSizeAndFrequencySelected() {
                                @Override
                                public void onSizeAndFrequencySelected(ItemConfig selectedItemConfig) {
                                    dialogFragment.dismiss();
                                    //request server and update Item Config
                                    updateItemConfig(subscribeItem, position, selectedItemConfig);

                                }
                            });
                            break;
                        case 2:
                            //Reschedule
                            final DelayDeliveryBottomSheetFragment deliveryBottomSheet = DelayDeliveryBottomSheetFragment.newInstance(subscribeItem);
                            deliveryBottomSheet.show(((AppCompatActivity) mContext).getSupportFragmentManager(), DelayDeliveryBottomSheetFragment.TAG);
                            deliveryBottomSheet.attachListener(new DelayDeliveryBottomSheetFragment.OnDelayActionCompleted() {
                                @Override
                                public void onDelayActionCompleted(SubscribeItem updatedSubscribeItem) {

                                    //update the arriving at Text on the existing list object
                                    subscribeItem.setArrivingAt(updatedSubscribeItem.getArrivingAt());
                                    subscribeItems.set(position, subscribeItem);

                                    if (onSubscribeItemChange != null) {
                                        onSubscribeItemChange.onSubscribeItem(subscribeItems);
                                    }
                                    notifyItemChanged(getAdapterPosition());

                                    if (deliveryBottomSheet != null) {
                                        deliveryBottomSheet.dismiss();
                                    }
                                    //fetch orders to update the list
                                    EventBus.getDefault().post(new UpdateUpcomingDeliveriesEvent());
                                }
                            });
                            break;
                        case 3:
                            //cancel subscription
                            if (subscribeItem.getQuantity() > 0) {
                                MaterialDialog dialog = new MaterialDialog.Builder(mContext).
                                        title("Unsubscribe " + subscribeItem.getBoxItem().getTitle()).
                                        positiveText("Cancel")
                                        .negativeText("Unsubscribe").
                                                onNegative(new MaterialDialog.SingleButtonCallback() {
                                                    @Override
                                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                        //Remove Subscribe Item
                                                        updateQuantity(subscribeItem, position, 0);
                                                    }
                                                }).content("Unsubscribing " + subscribeItem.getBoxItem().getTitle() + " will remove it from all subsequent orders. Are you sure you want to unsubscribe?").build();
                                dialog.getWindow().getAttributes().windowAnimations = R.style.MyAnimation_Window;
                                dialog.show();
                            } else {
                                //error handling
                            }

                            break;
                    }
                }
            });


        }

        /**
         * Update Quantity for Subscribe Item; ALso remove Subscribe Item
         *
         * @param subscribeItem
         * @param position
         * @param quantity
         */
        private void updateQuantity(final SubscribeItem subscribeItem, final int position, final int quantity) {
            final BoxLoader dialog = new BoxLoader(mContext).show();

            TheBox.getAPIService()
                    .updateQuantitySubscribeItem(PrefUtils.getToken(TheBox.getAppContext()),
                            subscribeItem.getUuid(), new UpdateQuantitySubscribeItemRequest(quantity))
                    .enqueue(new Callback<UpdateQuantitySubscribeItemResponse>() {
                        @Override
                        public void onResponse(Call<UpdateQuantitySubscribeItemResponse> call, Response<UpdateQuantitySubscribeItemResponse> response) {
                            dialog.dismiss();
                            try {
                                if (response.isSuccessful()) {
                                    if (response.body() != null) {

                                        /**
                                         * check if Subscribe Item has been delted or not;
                                         * SubscribeItem id DELETED
                                         */
                                        if (response.body().isDeleted()) {
                                            //remove item
                                            subscribeItems.remove(position);
                                            notifyItemRemoved(position);
                                            notifyDataSetChanged();
                                            //display message to users
                                            Toast.makeText(TheBox.getAppContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();

                                            /**
                                             * Set Clever tab Event CanCel Subscription
                                             */
                                            setCleverTapEventCancelSubscription(subscribeItem);

                                            /**
                                             * Update Box List
                                             * called on Store Fragment
                                             */
                                            EventBus.getDefault().post(new EventUnsubscribedSubscribedItem());

                                        } else {
                                            //update item quantity and savings
                                            subscribeItem.setQuantity(response.body().getSubscribeItem().getQuantity());
                                            //update price
                                            subscribeItem.getSelectedItemConfig().setPrice(response.body().getSubscribeItem().getSelectedItemConfig().getPrice());
                                            if (response.body().getSubscribeItem().getSubscribedSavingText() != null) {
                                                if (!response.body().getSubscribeItem().getSubscribedSavingText().isEmpty()) {
                                                    subscribeItem.setSubscribedSavingText(response.body().getSubscribeItem().getSubscribedSavingText());
                                                }
                                            }
                                            subscribeItems.set(position, subscribeItem);
                                            notifyItemChanged(position);
                                        }
                                        //notify Subscription Adapter about the change
                                        if (onSubscribeItemChange != null) {
                                            onSubscribeItemChange.onSubscribeItem(subscribeItems);
                                        }

                                        //do saving call and update the data
                                        EventBus.getDefault().post(new UpdateSavingsEvent());
                                        //fetch orders to update the list
                                        EventBus.getDefault().post(new UpdateUpcomingDeliveriesEvent());
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(TheBox.getAppContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<UpdateQuantitySubscribeItemResponse> call, Throwable t) {
                            dialog.dismiss();
                            Toast.makeText(TheBox.getAppContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        /**
         * Update ItemConfig for Subscribe Item
         *
         * @param subscribeItem
         * @param position
         * @param selectedItemConfig
         */
        private void updateItemConfig(final SubscribeItem subscribeItem, final int position, final ItemConfig selectedItemConfig) {
            final BoxLoader dialog = new BoxLoader(mContext).show();

            TheBox.getAPIService()
                    .updateItemConfigSubscribeItem(PrefUtils.getToken(TheBox.getAppContext()),
                            subscribeItem.getUuid(), new UpdateItemConfigSubscribeItemRequest(selectedItemConfig.getUuid()))
                    .enqueue(new Callback<UpdateItemConfigSubscribeItemResponse>() {
                        @Override
                        public void onResponse(Call<UpdateItemConfigSubscribeItemResponse> call, Response<UpdateItemConfigSubscribeItemResponse> response) {
                            dialog.dismiss();
                            try {
                                if (response.isSuccessful()) {
                                    if (response.body().isStatus()) {

                                        subscribeItems.set(position, response.body().getSubscribeItem());
                                        notifyItemChanged(position);

                                        //notify Subscription Adapter about the change
                                        if (onSubscribeItemChange != null) {
                                            onSubscribeItemChange.onSubscribeItem(subscribeItems);
                                        }

                                        //do saving call and update the data
                                        EventBus.getDefault().post(new UpdateSavingsEvent());
                                        //fetch orders to update the list
                                        EventBus.getDefault().post(new UpdateUpcomingDeliveriesEvent());

                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(TheBox.getAppContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<UpdateItemConfigSubscribeItemResponse> call, Throwable t) {
                            dialog.dismiss();
                            Toast.makeText(TheBox.getAppContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        /**
         * Clever tab Event Cancel Subscription
         */
        public void setCleverTapEventCancelSubscription(SubscribeItem subscribeItem) {
            try {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("subscribe_item_uuid", subscribeItem.getUuid());
                hashMap.put("title", subscribeItem.getBoxItem().getTitle());
                hashMap.put("box_item_uuid", subscribeItem.getBoxItem().getUuid());
                hashMap.put("item_config_uuid", subscribeItem.getSelectedItemConfig().getUuid());
                hashMap.put("item_config_name", subscribeItem.getSelectedItemConfig().getSize() + " " +
                        subscribeItem.getSelectedItemConfig().getSizeUnit() + ", " + subscribeItem.getSelectedItemConfig().getItemType());
                hashMap.put("item_config_subscription", subscribeItem.getSelectedItemConfig().getSubscriptionText());


                TheBox.getCleverTap().event.push("cancel_subscription", hashMap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Interface to Communicate with SubscribeAdapter
     */
    public interface OnSubscribeItemChange {
        void onSubscribeItem(List<SubscribeItem> subscribeItems);
    }

}
