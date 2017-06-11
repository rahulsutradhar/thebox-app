
package one.thebox.android.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.RequestManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import io.realm.RealmList;
import one.thebox.android.Events.UpdateSubscribeItemEvent;
import one.thebox.android.Events.UpdateUpcomingDeliveriesEvent;
import one.thebox.android.Helpers.cart.CartHelper;
import one.thebox.android.Models.items.Box;
import one.thebox.android.Models.items.BoxItem;
import one.thebox.android.Models.items.Category;
import one.thebox.android.Models.items.ItemConfig;
import one.thebox.android.Models.items.UserItem;
import one.thebox.android.Models.items.SubscribeItem;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.BoxLoader;
import one.thebox.android.ViewHelper.DelayDeliveryBottomSheetFragment;
import one.thebox.android.ViewHelper.WrapContentLinearLayoutManager;
import one.thebox.android.activity.FullImageActivity;
import one.thebox.android.api.RequestBodies.subscribeitem.UpdateItemConfigSubscribeItemRequest;
import one.thebox.android.api.RequestBodies.subscribeitem.UpdateQuantitySubscribeItemRequest;
import one.thebox.android.api.Responses.subscribeitem.UpdateItemConfigSubscribeItemResponse;
import one.thebox.android.api.Responses.subscribeitem.UpdateQuantitySubscribeItemResponse;
import one.thebox.android.app.Constants;
import one.thebox.android.app.TheBox;
import one.thebox.android.fragment.EditItemFragment;
import one.thebox.android.fragment.SizeAndFrequencyBottomSheetDialogFragment;
import one.thebox.android.services.cart.CartHelperService;
import one.thebox.android.util.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static one.thebox.android.app.Constants.VIEW_TYPE_SEARCH_ITEM;

public class SearchDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<BoxItem> boxItems = new ArrayList<>();
    private Context mContext;
    private int positionInViewPager = -1;
    private RealmList<Category> suggestedCategories = new RealmList<>();

    /**
     * GLide Request Manager
     */
    private RequestManager glideRequestManager;

    public RealmList<Category> getSuggestedCategories() {
        return suggestedCategories;
    }

    public void setSuggestedCategories(RealmList<Category> suggestedCategories) {
        this.suggestedCategories = suggestedCategories;
    }

    public List<BoxItem> getBoxItems() {
        return boxItems;
    }

    public void setBoxItems(List<BoxItem> boxItems) {
        this.boxItems = boxItems;
        notifyDataSetChanged();
    }

    public SearchDetailAdapter(Context context, RequestManager glideRequestManager) {
        this.mContext = context;
        this.glideRequestManager = glideRequestManager;
    }

    public int getPositionInViewPager() {
        return positionInViewPager;
    }

    public void setPositionInViewPager(int positionInViewPager) {
        this.positionInViewPager = positionInViewPager;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case Constants.VIEW_TYPE_SUBSCRIBE_ITEM: {
                //subscribe item
                View itemView = LayoutInflater.from(TheBox.getInstance()).inflate(R.layout.item_subscribe_item, parent, false);
                return new SubscribeItemViewHolder(itemView);
            }
            case VIEW_TYPE_SEARCH_ITEM: {
                //product item
                View itemView = LayoutInflater.from(TheBox.getInstance()).inflate(R.layout.item_search_detail_items, parent, false);
                return new SearchedItemViewHolder(itemView);
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof SearchedItemViewHolder) {
            bindSearchViewHolder(holder, position);
        } else if (holder instanceof SubscribeItemViewHolder) {
            bindSubscribeItemViewHolder(holder, position);
        }
    }

    /**
     * User Subscribe Item
     */
    private void bindSubscribeItemViewHolder(final RecyclerView.ViewHolder holder, int position) {
        SubscribeItemViewHolder itemViewHolder = (SubscribeItemViewHolder) holder;
        //check if adapter holds correct position
        if (position != RecyclerView.NO_POSITION) {
            itemViewHolder.setViews(boxItems.get(position), boxItems.get(position).getUserItem().getSubscribeItem(), position);
        }
    }

    /**
     * Search Product
     */
    private void bindSearchViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final SearchedItemViewHolder searchedItemViewHolder = (SearchedItemViewHolder) holder;

        if (boxItems.get(position).getSelectedItemConfig() == null) {
            boxItems.get(position).setSelectedItemConfig(boxItems.get(position).getSmallestInStockItemConfig());
        }
        searchedItemViewHolder.setViews(boxItems.get(position), position);

    }

    @Override
    public int getItemCount() {
        return boxItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = Constants.VIEW_TYPE_SEARCH_ITEM;
        switch (boxItems.get(position).getItemViewType()) {
            case Constants.VIEW_TYPE_SEARCH_ITEM:
                viewType = Constants.VIEW_TYPE_SEARCH_ITEM;
                break;
            case Constants.VIEW_TYPE_SUBSCRIBE_ITEM:
                viewType = Constants.VIEW_TYPE_SUBSCRIBE_ITEM;
                break;
        }
        return viewType;
    }

    /**
     * Search Item; Or Product ITEM
     */
    private class SearchedItemViewHolder extends RecyclerView.ViewHolder {

        private RecyclerView recyclerViewSavings;
        private RecyclerView recyclerViewFrequency;
        private RemainingCategoryAdapter remainingCategoryAdapter;
        private TextView addButton, subtractButton;
        private TextView noOfItemSelected, repeat_every, out_of_stock;
        private LinearLayout savingHolder;
        private TextView productName, productBrand, size, no_of_options_holder;
        private ImageView productImage;
        private FrequencyAndPriceAdapter frequencyAndPriceAdapter;
        private LinearLayout updateQuantityViewHolder;
        private RelativeLayout holderSubscribeButton;
        private int position;
        private TextView savingsTitle, savingsItemConfig, mrp;

        private SearchedItemViewHolder(View itemView) {
            super(itemView);
            recyclerViewSavings = (RecyclerView) itemView.findViewById(R.id.relatedCategories);
            recyclerViewSavings.setItemViewCacheSize(20);
            recyclerViewSavings.setDrawingCacheEnabled(true);
            recyclerViewSavings.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

            addButton = (TextView) itemView.findViewById(R.id.button_add);
            subtractButton = (TextView) itemView.findViewById(R.id.button_subtract);
            noOfItemSelected = (TextView) itemView.findViewById(R.id.no_of_item_selected);
            repeat_every = (TextView) itemView.findViewById(R.id.repeat_every);
            out_of_stock = (TextView) itemView.findViewById(R.id.out_of_stock);
            savingHolder = (LinearLayout) itemView.findViewById(R.id.saving_holder);
            recyclerViewFrequency = (RecyclerView) itemView.findViewById(R.id.recycler_view_frequency);
            recyclerViewFrequency.setItemViewCacheSize(20);
            recyclerViewFrequency.setDrawingCacheEnabled(true);
            recyclerViewFrequency.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            productName = (TextView) itemView.findViewById(R.id.product_name);
            productBrand = (TextView) itemView.findViewById(R.id.product_brand);
            size = (TextView) itemView.findViewById(R.id.text_view_size);
            no_of_options_holder = (TextView) itemView.findViewById(R.id.no_of_options);
            productImage = (ImageView) itemView.findViewById(R.id.product_image);

            holderSubscribeButton = (RelativeLayout) itemView.findViewById(R.id.holder_subscribe_button);
            updateQuantityViewHolder = (LinearLayout) itemView.findViewById(R.id.holder_adjust_quantity);
            savingsTitle = (TextView) itemView.findViewById(R.id.savings_title);
            savingsItemConfig = (TextView) itemView.findViewById(R.id.savings_item_confid);
            mrp = (TextView) itemView.findViewById(R.id.mrp);


        }

        private void setViewsBasedOnStock(boolean isOutOfStock, BoxItem boxItem, int position) {
            //true
            if (isOutOfStock) {
                holderSubscribeButton.setVisibility(View.GONE);
                updateQuantityViewHolder.setVisibility(View.GONE);
                out_of_stock.setVisibility(View.VISIBLE);

                repeat_every.setVisibility(View.GONE);
                savingHolder.setVisibility(View.GONE);

                // Disable the change button
                no_of_options_holder.setTextColor(TheBox.getInstance().getResources().getColor(R.color.dim_gray));
            } else {

                repeat_every.setVisibility(View.VISIBLE);
                out_of_stock.setVisibility(View.GONE);
                // Disable the change button
                no_of_options_holder.setTextColor(TheBox.getInstance().getResources().getColor(R.color.dim_gray));


                //check the quantitiy and show ui
                if (boxItem.getQuantity() > 0) {
                    holderSubscribeButton.setVisibility(View.GONE);
                    updateQuantityViewHolder.setVisibility(View.VISIBLE);
                    noOfItemSelected.setText(String.valueOf(boxItem.getQuantity()));
                } else {
                    holderSubscribeButton.setVisibility(View.VISIBLE);
                    updateQuantityViewHolder.setVisibility(View.GONE);
                    noOfItemSelected.setText(String.valueOf(0));
                }

                if (boxItem.isShowCategorySuggestion()) {
                    if (suggestedCategories != null) {
                        if (suggestedCategories.size() > 0) {
                            savingHolder.setVisibility(View.VISIBLE);
                            setupRecyclerViewSuggestedCategories(suggestedCategories);
                        } else {
                            savingHolder.setVisibility(View.GONE);
                        }
                    } else {
                        savingHolder.setVisibility(View.GONE);
                    }
                    boxItem.setShowCategorySuggestion(false);
                } else {

                }
            }
        }

        private void setupRecyclerViewFrequency(final BoxItem boxItem, final int position) {
            // hash map of frequency and corresponding PriceSizeAndSizeUnit ArrayList.
            RealmList<ItemConfig> itemConfigs = boxItem.getItemConfigsBySelectedItemConfig();
            Collections.sort(itemConfigs, new Comparator<ItemConfig>() {
                @Override
                public int compare(ItemConfig lhs, ItemConfig rhs) {
                    if (lhs.getSubscriptionType() > rhs.getSubscriptionType()) {
                        return 1;
                    } else if (lhs.getSubscriptionType() < rhs.getSubscriptionType()) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            });
            int selectedPosition = 0;

            for (int i = 0; i < itemConfigs.size(); i++) {
                if (boxItem.getSelectedItemConfig().getUuid().equalsIgnoreCase(itemConfigs.get(i).getUuid())) {
                    selectedPosition = i;
                }

            }

            WrapContentLinearLayoutManager linearLayoutManager = new WrapContentLinearLayoutManager(TheBox.getInstance(), LinearLayoutManager.HORIZONTAL, false);

            //ItemConfig with similar size to selected ItemConfig frequency

            recyclerViewFrequency.setLayoutManager(linearLayoutManager);
            frequencyAndPriceAdapter = new FrequencyAndPriceAdapter(TheBox.getInstance(), selectedPosition, new FrequencyAndPriceAdapter.OnItemConfigChange() {
                @Override
                public void onItemConfigItemChange(ItemConfig selectedItemConfig) {
                    if (!boxItem.getUuid().isEmpty() && boxItem.getQuantity() > 0) {
                        updateItemConfigInCart(boxItem, selectedItemConfig, position);
                    } else {
                        boxItems.get(position).setSelectedItemConfig(selectedItemConfig);
                        notifyItemChanged(getAdapterPosition());
                    }
                }
            });
            frequencyAndPriceAdapter.setItemConfigs(itemConfigs);
            frequencyAndPriceAdapter.setQuantity(boxItem.getQuantity());
            recyclerViewFrequency.setAdapter(frequencyAndPriceAdapter);
            recyclerViewFrequency.setHasFixedSize(true);
            frequencyAndPriceAdapter.notifyDataSetChanged();
            linearLayoutManager.scrollToPosition(selectedPosition);
            SnapHelper snapHelper = new LinearSnapHelper();
            snapHelper.attachToRecyclerView(recyclerViewFrequency);

        }

        private void setupRecyclerViewSuggestedCategories(RealmList<Category> suggestedCategories) {

            remainingCategoryAdapter = new RemainingCategoryAdapter(
                    TheBox.getInstance(), suggestedCategories, glideRequestManager);
            remainingCategoryAdapter.setSearchDetailItemFragment(true);
            recyclerViewSavings.setLayoutManager(new LinearLayoutManager(TheBox.getInstance(),
                    LinearLayoutManager.HORIZONTAL, false));
            recyclerViewSavings.setAdapter(remainingCategoryAdapter);

        }

        public void setViews(final BoxItem boxItem, int arrayListPosition) {

            try {
                this.position = arrayListPosition;
                productName.setText(boxItem.getTitle());

                if (!boxItem.getBrand().isEmpty()) {
                    productBrand.setVisibility(View.VISIBLE);
                    productBrand.setText(boxItem.getBrand());
                } else {
                    productBrand.setText("");
                    productBrand.setVisibility(View.GONE);
                }

                //Updating no. of SKU's
                if (boxItem.getNoOfSku() < 2) {
                    no_of_options_holder.setVisibility(View.GONE);
                } else {
                    no_of_options_holder.setVisibility(View.VISIBLE);
                    no_of_options_holder.setText(boxItem.getNoOfOptions());
                }

                //Size of the ItemConfig selected
                if (boxItem.getItemConfigs() != null && !boxItem.getItemConfigs().isEmpty()) {
                    size.setText(String.valueOf(boxItem.getSelectedItemConfig().getSize()) + " " + boxItem.getSelectedItemConfig().getSizeUnit()
                            + " " + boxItem.getSelectedItemConfig().getItemType());
                }

                glideRequestManager.load(boxItem.getSelectedItemConfig().getItemImage())
                        .centerCrop()
                        .crossFade()
                        .into(productImage);

                //Monthly Savings Item Config
                if (boxItem.getSelectedItemConfig().getMonthlySavingsText() != null) {
                    if (!boxItem.getSelectedItemConfig().getMonthlySavingsText().isEmpty()) {
                        savingsTitle.setVisibility(View.VISIBLE);
                        savingsTitle.setText(boxItem.getSelectedItemConfig().getMonthlySavingsText());
                    } else {
                        savingsTitle.setText("");
                        savingsTitle.setVisibility(View.GONE);
                    }
                } else {
                    savingsTitle.setText("");
                    savingsTitle.setVisibility(View.GONE);
                }

                //savings ItemConfig
                if (boxItem.getSelectedItemConfig().getSavingsText() != null) {
                    if (!boxItem.getSelectedItemConfig().getSavingsText().isEmpty()) {
                        savingsItemConfig.setText(boxItem.getSelectedItemConfig().getSavingsText());
                        savingsItemConfig.setVisibility(View.VISIBLE);
                    } else {
                        savingsItemConfig.setText("");
                        savingsItemConfig.setVisibility(View.GONE);
                    }
                } else {
                    savingsItemConfig.setText("");
                    savingsItemConfig.setVisibility(View.GONE);
                }

                //mrp
                if (boxItem.getSelectedItemConfig().getMrpText() != null) {
                    if (!boxItem.getSelectedItemConfig().getMrpText().isEmpty()) {
                        mrp.setText(boxItem.getSelectedItemConfig().getMrpText());
                        mrp.setVisibility(View.VISIBLE);
                    } else {
                        mrp.setText("");
                        mrp.setVisibility(View.GONE);
                    }
                } else {
                    mrp.setText("");
                    mrp.setVisibility(View.GONE);
                }

                productImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = boxItem.getSelectedItemConfig().getItemImage();
                        FullImageActivity.showImage(url, mContext);
                    }
                });

                no_of_options_holder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        displayNumberOfOption(boxItem, position);

                    }
                });

                // Checking if item is in stock
                if (boxItem.isInStock()) {
                    setViewsBasedOnStock(false, boxItem, position);

                    holderSubscribeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addItemToCart(boxItem, position);
                        }
                    });
                    addButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            updateQuantityInCart(boxItem, boxItem.getQuantity() + 1, position);
                        }
                    });

                    subtractButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (boxItem.getQuantity() > 0 && boxItem.getQuantity() == 1) {
                                removeItemFromCart(boxItem, position);
                            } else {
                                updateQuantityInCart(boxItem, boxItem.getQuantity() - 1, position);
                            }
                        }
                    });

                    setupRecyclerViewFrequency(boxItem, position);
                }
                // If Item is not in stock
                else {
                    setViewsBasedOnStock(true, boxItem, position);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void displayNumberOfOption(final BoxItem boxItem, final int position) {
            final SizeAndFrequencyBottomSheetDialogFragment dialogFragment = new
                    SizeAndFrequencyBottomSheetDialogFragment(boxItem.getItemConfigs(), boxItem.getSelectedItemConfig());
            dialogFragment.show(((AppCompatActivity) mContext).getSupportFragmentManager()
                    , SizeAndFrequencyBottomSheetDialogFragment.TAG);

            dialogFragment.attachListener(new SizeAndFrequencyBottomSheetDialogFragment.OnSizeAndFrequencySelected() {
                @Override
                public void onSizeAndFrequencySelected(ItemConfig selectedItemConfig) {
                    dialogFragment.dismiss();

                    if (!boxItem.getUuid().isEmpty() && boxItem.getQuantity() > 0) {
                        updateItemConfigInCart(boxItem, selectedItemConfig, position);
                    } else {
                        boxItems.get(position).setSelectedItemConfig(selectedItemConfig);
                        notifyItemChanged(position);
                    }
                }
            });
        }

        /**
         * Add BoxItem to Cart
         */
        private void addItemToCart(BoxItem boxItem, int position) {
            boxItem.setQuantity(1);
            boxItem.setShowCategorySuggestion(true);
            boxItems.set(position, boxItem);
            notifyItemChanged(position);
            CartHelper.addBoxItemToCart(boxItem);

            //check for background service
            CartHelperService.checkServiceRunningWhenAdded(mContext);
        }

        /**
         * Remove BoxItem from Cart
         */
        private void removeItemFromCart(BoxItem boxItem, int position) {
            boxItem.setQuantity(0);
            boxItem.setShowCategorySuggestion(false);
            boxItems.set(position, boxItem);
            notifyItemChanged(position);
            CartHelper.removeItemFromCart(boxItem);

            //check for background service
            CartHelperService.checkServiceRunningWhenRemoved(mContext, true);
        }

        /**
         * Update Quantity of BoxItem in Cart
         */
        private void updateQuantityInCart(BoxItem boxItem, int quantity, int position) {
            boxItem.setQuantity(quantity);
            boxItem.setShowCategorySuggestion(false);
            boxItems.set(position, boxItem);
            notifyItemChanged(position);
            CartHelper.updateQuantityInCart(boxItem, quantity);

            //check for background service
            CartHelperService.checkServiceRunningWhenAdded(mContext);
        }

        /**
         * Update ItemConfig in Cart
         */
        private void updateItemConfigInCart(BoxItem boxItem, ItemConfig selectedItemConfig, int position) {
            CartHelper.updateItemConfigInCart(boxItem, selectedItemConfig);
            boxItem.setSelectedItemConfig(selectedItemConfig);
            boxItem.setShowCategorySuggestion(false);
            boxItems.set(position, boxItem);
            notifyItemChanged(position);

            //check for background service
            CartHelperService.checkServiceRunningWhenAdded(mContext);
        }

        /**
         * CleverTap Event;
         * <p>
         * Item Added to Cart; Subscribed
         */
        public void setCleverTapEventItemAddedToCart(BoxItem boxItem) {
            TheBox.getCleverTap().event.push("item_added_to_cart", getParam(boxItem));
        }

        public void setCleverTapEventItemRemoveFromCart(BoxItem boxItem) {
            TheBox.getCleverTap().event.push("item_remove_from_cart", getParam(boxItem));
        }

        public HashMap getParam(BoxItem boxItem) {
            HashMap<String, Object> hashMap = new HashMap<>();
            try {
                hashMap.put("box_item_id", boxItem.getId());
                hashMap.put("title", boxItem.getTitle());
                hashMap.put("brand", boxItem.getBrand());
                hashMap.put("category_id", boxItem.getCategoryId());
                hashMap.put("item_config_id", boxItem.getSelectedItemConfig().getId());
                hashMap.put("item_config_name", boxItem.getSelectedItemConfig().getSizeUnit() + ", " +
                        boxItem.getSelectedItemConfig().getItemType());
                hashMap.put("item_config_subscription", boxItem.getSelectedItemConfig().getSubscriptionText());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return hashMap;
        }

    }

    /**
     * Subscribe Item
     */
    private class SubscribeItemViewHolder extends RecyclerView.ViewHolder {

        private TextView productName, arrivingTime, config, addButton,
                subtractButton, noOfItemSelected, frequency, price, editSubscription, savingtextView;
        private ImageView productImageView;
        private RelativeLayout quantityHolder;

        public SubscribeItemViewHolder(View itemView) {
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

        private void setViews(final BoxItem boxItem, final SubscribeItem subscribeItem, final int position) {

            try {
                quantityHolder.setVisibility(View.VISIBLE);
                noOfItemSelected.setText(String.valueOf(subscribeItem.getQuantity()));
                ItemConfig selectedItemConfig = subscribeItem.getSelectedItemConfig();
                price.setText(Constants.RUPEE_SYMBOL + " " + (subscribeItem.getQuantity() * selectedItemConfig.getPrice()));
                frequency.setText("Repeat " + selectedItemConfig.getSubscriptionText().toLowerCase());

                productName.setText(subscribeItem.getBoxItem().getTitle());

                config.setText(selectedItemConfig.getSize() + " " + selectedItemConfig.getSizeUnit() + " " + selectedItemConfig.getItemType());
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
                    doEditSubscription(boxItem, subscribeItem, position);

                }
            });

            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //increase quantity
                    updateQuantity(boxItem, subscribeItem, position, (subscribeItem.getQuantity() + 1));
                }
            });
            subtractButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (subscribeItem.getQuantity() > 1) {
                        //decrease quantity
                        updateQuantity(boxItem, subscribeItem, position, (subscribeItem.getQuantity() - 1));
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
                                                updateQuantity(boxItem, subscribeItem, position, 0);
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
        private void doEditSubscription(final BoxItem boxItem, final SubscribeItem subscribeItem, final int position) {
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
                                    updateItemConfig(boxItem, subscribeItem, position, selectedItemConfig);

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
                                    boxItem.getUserItem().setSubscribeItem(subscribeItem);
                                    boxItems.set(position, boxItem);
                                    notifyItemChanged(position);

                                    if (deliveryBottomSheet != null) {
                                        deliveryBottomSheet.dismiss();
                                    }
                                    updateHomeTabs();
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
                                                        updateQuantity(boxItem, subscribeItem, position, 0);
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
        private void updateQuantity(final BoxItem boxItem, final SubscribeItem subscribeItem, final int position, final int quantity) {
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
                                            /**
                                             * Update Subscribe Item to Search Item
                                             */
                                            boxItem.getUserItem().setSubscribeItemAvailable(false);
                                            boxItem.setItemViewType(Constants.VIEW_TYPE_SEARCH_ITEM);
                                            notifyItemChanged(position);
                                            Toast.makeText(TheBox.getAppContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();

                                        } else {
                                            //update item quantity and savings
                                            subscribeItem.setQuantity(response.body().getSubscribeItem().getQuantity());
                                            if (response.body().getSubscribeItem().getSubscribedSavingText() != null) {
                                                if (!response.body().getSubscribeItem().getSubscribedSavingText().isEmpty()) {
                                                    subscribeItem.setSubscribedSavingText(response.body().getSubscribeItem().getSubscribedSavingText());
                                                }
                                            }
                                            boxItem.getUserItem().setSubscribeItem(subscribeItem);
                                            boxItems.set(position, boxItem);
                                            //  subscribeItems.set(position, subscribeItem);
                                            notifyItemChanged(position);
                                        }
                                        updateHomeTabs();
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
        private void updateItemConfig(final BoxItem boxItem, final SubscribeItem subscribeItem, final int position, final ItemConfig selectedItemConfig) {
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

                                        boxItem.getUserItem().setSubscribeItem(response.body().getSubscribeItem());
                                        boxItems.set(position, boxItem);
                                        notifyItemChanged(position);

                                        updateHomeTabs();
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


        public void updateHomeTabs() {
            //do subscription call and update the data
            EventBus.getDefault().post(new UpdateSubscribeItemEvent());
            //fetch orders to update the list
            EventBus.getDefault().post(new UpdateUpcomingDeliveriesEvent());
        }

        /**
         * Clever tab Event Cancel Subscription
         */
        public void setCleverTapEventCancelSubscription(UserItem userItem) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("user_item_id", userItem.getId());
            hashMap.put("box_item_id", userItem.getBoxItem().getId());
            hashMap.put("title", userItem.getBoxItem().getTitle());
            hashMap.put("brand", userItem.getBoxItem().getBrand());
            hashMap.put("item_config_id", userItem.getSelectedConfigId());
            hashMap.put("quantitiy", userItem.getQuantity());
            hashMap.put("category", userItem.getBoxItem().getCategoryId());

            TheBox.getCleverTap().event.push("cancel_subscription", hashMap);
        }

    }


    /**
     * Update on Event Passed from Cart
     * <p>
     * When Cart Update Quantity; Increase or Decrease
     */
    public synchronized void updateQuantityEvent(BoxItem updatedBoxItem) {
        int index = 0;
        if (getBoxItems() != null) {
            for (BoxItem boxItem : getBoxItems()) {
                if (boxItem.getItemViewType() == Constants.VIEW_TYPE_SEARCH_ITEM) {
                    if (boxItem.getUuid().equalsIgnoreCase(updatedBoxItem.getUuid())) {
                        //update the quantity
                        boxItem.setQuantity(updatedBoxItem.getQuantity());
                        boxItem.setShowCategorySuggestion(false);
                        boxItems.set(index, boxItem);
                        notifyItemChanged(index);
                        break;
                    }
                }
                index++;
            }
        }
    }

    /**
     * When Cart Remove Quantity
     */
    public synchronized void removeQuantityEvent(BoxItem updatedBoxItem) {
        int index = 0;
        if (getBoxItems() != null) {
            for (BoxItem boxItem : getBoxItems()) {
                if (boxItem.getItemViewType() == Constants.VIEW_TYPE_SEARCH_ITEM) {
                    if (boxItem.getUuid().equalsIgnoreCase(updatedBoxItem.getUuid())) {
                        //update the quantity
                        boxItem.setQuantity(0);
                        boxItem.setShowCategorySuggestion(false);
                        boxItems.set(index, boxItem);
                        notifyItemChanged(index);
                        break;
                    }
                }
                index++;
            }
        }
    }

    /**
     * When Cart Update ItemConfig
     */
    public synchronized void updateItemConfigEvent(BoxItem updatedBoxItem) {
        int index = 0;
        if (getBoxItems() != null) {
            for (BoxItem boxItem : getBoxItems()) {
                if (boxItem.getItemViewType() == Constants.VIEW_TYPE_SEARCH_ITEM) {
                    if (boxItem.getUuid().equalsIgnoreCase(updatedBoxItem.getUuid())) {
                        //update the item config
                        boxItem.setSelectedItemConfig(updatedBoxItem.getSelectedItemConfig());
                        boxItem.setShowCategorySuggestion(false);
                        boxItems.set(index, boxItem);
                        notifyItemChanged(index);
                        break;
                    }
                }
                index++;
            }
        }
    }

}
