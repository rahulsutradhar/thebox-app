
package one.thebox.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.realm.RealmList;
import one.thebox.android.Events.ShowTabTutorialEvent;
import one.thebox.android.Events.UpdateOrderItemEvent;
import one.thebox.android.Events.UpdateUpcomingDeliveriesEvent;
import one.thebox.android.Helpers.CartHelper;
import one.thebox.android.Helpers.OrderHelper;
import one.thebox.android.Models.BoxItem;
import one.thebox.android.Models.Category;
import one.thebox.android.Models.Invoice;
import one.thebox.android.Models.ItemConfig;
import one.thebox.android.Models.Order;
import one.thebox.android.Models.UserItem;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.Announcement;
import one.thebox.android.ViewHelper.BoxLoader;
import one.thebox.android.ViewHelper.DelayDeliveryBottomSheet;
import one.thebox.android.ViewHelper.ShowcaseHelper;
import one.thebox.android.ViewHelper.WrapContentLinearLayoutManager;
import one.thebox.android.activity.FullImageActivity;
import one.thebox.android.activity.MainActivity;
import one.thebox.android.api.RequestBodies.AddToMyBoxRequestBody;
import one.thebox.android.api.RequestBodies.CancelSubscriptionRequest;
import one.thebox.android.api.RequestBodies.UpdateItemConfigurationRequest;
import one.thebox.android.api.RequestBodies.UpdateItemQuantityRequestBody;
import one.thebox.android.api.RequestBodies.UpdateOrderItemQuantityRequestBody;
import one.thebox.android.api.Responses.AddToMyBoxResponse;
import one.thebox.android.api.Responses.CancelSubscriptionResponse;
import one.thebox.android.api.Responses.UpdateItemConfigResponse;
import one.thebox.android.api.Responses.UpdateOrderItemResponse;
import one.thebox.android.api.RestClient;
import one.thebox.android.app.TheBox;
import one.thebox.android.fragment.EditItemFragment;
import one.thebox.android.fragment.SearchDetailFragment;
import one.thebox.android.fragment.SizeAndFrequencyBottomSheetDialogFragment;
import one.thebox.android.util.DateTimeUtil;
import one.thebox.android.util.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SEARCH_ITEM = 0;
    private static final int VIEW_TYPE_USER_ITEM = 1;
    private static final int VIEW_TYPE_ORDER_ITEM = 2;
    private List<BoxItem> boxItems = new ArrayList<>();
    private List<UserItem> userItems = new ArrayList<>();
    private List<Invoice> useritems_quantities = new ArrayList<>();
    private Context mContext;
    private boolean shouldRemoveBoxItemOnEmptyQuantity;
    private boolean hasUneditableUserItem;
    private boolean hide_quantity_selector_in_this_order_item_view = false;
    private int currentPositionOfSuggestedCategory = -1;
    private int positionInViewPager = -1;
    private int order_id;
    private OnUserItemChange onUserItemChange;
    private Order order;
    private List<Category> suggestedCategories = new ArrayList<>();
    private int boxId;

    /**
     * GLide Request Manager
     */
    private RequestManager glideRequestManager;

    public List<UserItem> getUserItems() {
        return userItems;
    }

    public void setUserItems(RealmList<UserItem> userItems) {
        this.userItems = userItems;
    }

    public List<Invoice> getUserItemQuantities() {
        return useritems_quantities;
    }

    public void setUserItemQuantities(int order_id, RealmList<Invoice> useritems_quantities) {
        this.useritems_quantities = useritems_quantities;
        this.order_id = order_id;
    }

    private void setSuggestedCategoriesAndBoxId(int boxId, List<Category> suggestedCategories) {
        this.boxId = boxId;
        this.suggestedCategories.clear();
        this.suggestedCategories.addAll(suggestedCategories);
    }

    public int getOrderId() {
        return order_id;
    }

    public void setOrderId(int order_id) {
        this.order_id = order_id;
    }

    public List<BoxItem> getBoxItems() {
        return boxItems;
    }

    public void setBoxItems(RealmList<BoxItem> boxItems) {
        this.boxItems = boxItems;
    }

    public SearchDetailAdapter(Context context, RequestManager glideRequestManager) {
        this.mContext = context;
        this.glideRequestManager = glideRequestManager;
    }

    public boolean isHide_quantity_selector_in_this_order_item_view() {
        return hide_quantity_selector_in_this_order_item_view;
    }

    public void setHide_quantity_selector_in_this_order_item_view(boolean hide_quantity_selector_in_this_order_item_view) {
        this.hide_quantity_selector_in_this_order_item_view = hide_quantity_selector_in_this_order_item_view;
    }

    public boolean isHasUneditableUserItem() {
        return hasUneditableUserItem;
    }

    public void setHasUneditableUserItem(boolean hasUneditableUserItem) {
        this.hasUneditableUserItem = hasUneditableUserItem;
    }

    public void addOnUserItemChangeListener(OnUserItemChange onUserItemChange) {
        this.onUserItemChange = onUserItemChange;
    }

    public int getPositionInViewPager() {
        return positionInViewPager;
    }

    public void setPositionInViewPager(int positionInViewPager) {
        this.positionInViewPager = positionInViewPager;
    }

    public void setShouldRemoveBoxItemOnEmptyQuantity(boolean shouldRemoveBoxItemOnEmptyQuantity) {
        this.shouldRemoveBoxItemOnEmptyQuantity = shouldRemoveBoxItemOnEmptyQuantity;
    }

    public void setBoxItems(List<BoxItem> boxItems, List<UserItem> userItems) {
        if (boxItems != null) {
            this.boxItems = boxItems;
        }
        if (userItems != null) {
            this.userItems = userItems;
        }
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case VIEW_TYPE_USER_ITEM: {
                View itemView = LayoutInflater.from(TheBox.getInstance()).inflate(R.layout.item_user_item, parent, false);
                return new UserItemViewHolder(itemView);
            }
            case VIEW_TYPE_SEARCH_ITEM: {
                View itemView = LayoutInflater.from(TheBox.getInstance()).inflate(R.layout.item_search_detail_items, parent, false);
                return new SearchedItemViewHolder(itemView);
            }
            case VIEW_TYPE_ORDER_ITEM: {
                View itemView = LayoutInflater.from(TheBox.getInstance()).inflate(R.layout.item_order_item, parent, false);
                return new OrderItemViewHolder(itemView);
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SearchedItemViewHolder) {
            bindSearchViewHolder(holder, position - (userItems == null ? 0 : userItems.size()));
        } else if (holder instanceof UserItemViewHolder) {
            bindMyItemViewHolder(holder, position);
        } else {
            bindOrderItemViewHolder(holder, position);
        }
    }

    private void bindOrderItemViewHolder(final RecyclerView.ViewHolder holder, int position) {
        OrderItemViewHolder itemViewHolder = (OrderItemViewHolder) holder;
        //check if adapter holds correct position
        if (position != RecyclerView.NO_POSITION) {
            userItems.get(position).getBoxItem().setSelectedItemConfig(
                    userItems.get(position).getBoxItem().getItemConfigById(userItems.get(position).getSelectedConfigId()
                    ));
            itemViewHolder.setViews(useritems_quantities, userItems.get(position), position);
        }
    }

    private void bindMyItemViewHolder(final RecyclerView.ViewHolder holder, int position) {
        UserItemViewHolder itemViewHolder = (UserItemViewHolder) holder;
        //check if adapter holds correct position
        if (position != RecyclerView.NO_POSITION) {
            userItems.get(position).getBoxItem().setSelectedItemConfig(
                    userItems.get(position).getBoxItem().getItemConfigById(userItems.get(position).getSelectedConfigId()
                    ));
            itemViewHolder.setViews(userItems.get(position), position);
        }
    }

    private void bindSearchViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final SearchedItemViewHolder searchedItemViewHolder = (SearchedItemViewHolder) holder;
        //check if adapter holds correct position
        if (position != RecyclerView.NO_POSITION) {
            if (boxItems.get(position).getSelectedItemConfig() == null) {
                boxItems.get(position).setSelectedItemConfig(boxItems.get(position).getSmallestInStockItemConfig());
            }
            searchedItemViewHolder.setViews(boxItems.get(position), position, false);
        }
    }

    @Override
    public int getItemCount() {
        return (boxItems == null ? 0 : boxItems.size()) + (userItems == null ? 0 : userItems.size());
    }

    @Override
    public int getItemViewType(int position) {
        int index;
        if (useritems_quantities.isEmpty()) {
            index = position < (userItems == null ? 0 : userItems.size()) ? VIEW_TYPE_USER_ITEM : VIEW_TYPE_SEARCH_ITEM;
        } else {
            index = VIEW_TYPE_ORDER_ITEM;
        }

        return index;
    }

    private class OrderItemViewHolder extends RecyclerView.ViewHolder {

        private TextView adjustButton, productName, brand,
                arrivingTime, config, savings, addButton, subtractButton, noOfItemSelected, changeButton, frequency, price;
        private ImageView productImageView;
        private LinearLayout quantityHolder;
        private int quantity_for_this_order = 0;

        public OrderItemViewHolder(View itemView) {
            super(itemView);
            productName = (TextView) itemView.findViewById(R.id.product_name);
            arrivingTime = (TextView) itemView.findViewById(R.id.arriving_time);
            config = (TextView) itemView.findViewById(R.id.config);
            savings = (TextView) itemView.findViewById(R.id.savings);
            productImageView = (ImageView) itemView.findViewById(R.id.product_image_view);
            addButton = (TextView) itemView.findViewById(R.id.button_add);
            subtractButton = (TextView) itemView.findViewById(R.id.button_subtract);
            noOfItemSelected = (TextView) itemView.findViewById(R.id.no_of_item_selected);
            quantityHolder = (LinearLayout) itemView.findViewById(R.id.layout_quantity_holder);
            price = (TextView) itemView.findViewById(R.id.price);
            frequency = (TextView) itemView.findViewById(R.id.frequency);
        }

        private void setViews(final List<Invoice> useritems_quantities, final UserItem userItem, final int arrayListPosition) {


            try {
                for (Invoice i : useritems_quantities) {
                    if (i.getUseritem_id() == userItem.getId()) {
                        quantity_for_this_order = i.getInvoice_quantity();
                        noOfItemSelected.setText(String.valueOf(i.getInvoice_quantity()));
                    }
                }

                if (isHide_quantity_selector_in_this_order_item_view()) {

                    quantityHolder.setVisibility(View.GONE);
                    addButton.setVisibility(View.GONE);
                    subtractButton.setVisibility(View.GONE);

                } else {
                    quantityHolder.setVisibility(View.VISIBLE);

                    addButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            updateQuantity(arrayListPosition, quantity_for_this_order + 1);
                        }
                    });

                    subtractButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (quantity_for_this_order > 1) {
                                updateQuantity(arrayListPosition, quantity_for_this_order - 1);
                            } else if (quantity_for_this_order == 1) {
                                MaterialDialog dialog = new MaterialDialog.Builder(mContext).
                                        title("Remove " + userItem.getBoxItem().getTitle())
                                        .positiveText("Cancel")
                                        .negativeText("Remove")
                                        .content(userItem.getBoxItem().getTitle() + " will be removed from this order. Are you sure?")
                                        .callback(new MaterialDialog.ButtonCallback() {
                                            @Override
                                            public void onPositive(MaterialDialog materialDialog) {

                                                // Dismiss the dialog
                                                materialDialog.cancel();
                                            }

                                            @Override
                                            public void onNegative(MaterialDialog materialDialog) {

                                                // Making update call
                                                if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                                                    updateQuantity(getAdapterPosition(), 0);
                                                }

                                            }
                                        })
                                        .build();
                                dialog.getWindow().getAttributes().windowAnimations = R.style.MyAnimation_Window;
                                dialog.show();
                            } else {
                                Toast.makeText(TheBox.getInstance(), "Item count could not be negative", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }


                ItemConfig itemConfig = userItem.getBoxItem().getItemConfigById(userItem.getSelectedConfigId());

                price.setText("Rs " + itemConfig.getPrice() * quantity_for_this_order);
                frequency.setText("Repeat " + itemConfig.getSubscriptionText().toLowerCase());

                productName.setText(userItem.getBoxItem().getTitle());

                if (itemConfig.getCorrectQuantity().equals("NA")) {
                    config.setText(itemConfig.getSize() + " " + itemConfig.getSizeUnit());
                } else {
                    config.setText(itemConfig.
                            getCorrectQuantity() + " x " +
                            itemConfig.getSize() + " " + itemConfig.getSizeUnit());
                }

                savings.setText(userItem.getBoxItem().getSavings() + " Rs saved per month");


                if (isHasUneditableUserItem()) {
                    arrivingTime.setVisibility(View.GONE);

                }

                if (userItem.getNextDeliveryScheduledAt() == null || userItem.getNextDeliveryScheduledAt().isEmpty()) {
                    arrivingTime.setText("Item is added to your cart");

                } else {
                    arrivingTime.setText(userItem.getArrivingAt());
                }

                //image loading
                glideRequestManager.load(itemConfig.getPhotoUrl())
                        .centerCrop()
                        .crossFade()
                        .into(productImageView);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void updateQuantity(final int position, final int quantity) throws IllegalStateException {
            final BoxLoader dialog = new BoxLoader(mContext).show();
            TheBox.getAPIService().updateOrderQuantity(PrefUtils.getToken(TheBox.getInstance()), new UpdateOrderItemQuantityRequestBody(order_id, userItems.get(getAdapterPosition()).getId(), quantity))
                    .enqueue(new Callback<UpdateOrderItemResponse>() {
                        @Override
                        public void onResponse(Call<UpdateOrderItemResponse> call, Response<UpdateOrderItemResponse> response) {
                            dialog.dismiss();
                            try {
                                if (response.isSuccessful()) {
                                    if (response.body() != null) {
                                        if (response.body().isSuccess()) {

                                            if (quantity >= 1) {
                                                setUserItemQuantities(response.body().getOrder().getId(), response.body().getOrder().getUserItemQuantities());
                                                setUserItems(response.body().getOrder().getUserItems());
                                                notifyItemChanged(getAdapterPosition());
                                            } else {
                                                setUserItemQuantities(response.body().getOrder().getId(), response.body().getOrder().getUserItemQuantities());
                                                setUserItems(response.body().getOrder().getUserItems());
                                                notifyItemRemoved(getAdapterPosition());

                                                //to update the UI in Upcoing fragment when the user removes item from ordered Item
                                                PrefUtils.putBoolean(mContext, "UPDATE_UI_UPCOMING_FRAGMENT", true);
                                            }
                                            OrderHelper.addAndNotify(response.body().getOrder());
                                            EventBus.getDefault().post(new UpdateOrderItemEvent());

                                            Toast.makeText(TheBox.getInstance(), response.body().getInfo(), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(TheBox.getInstance(), response.body().getInfo(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                } else {
                                    //handle error
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<UpdateOrderItemResponse> call, Throwable t) {
                            dialog.dismiss();
                        }
                    });
        }


    }

    private class SearchedItemViewHolder extends RecyclerView.ViewHolder {

        private RecyclerView recyclerViewSavings;
        private RecyclerView recyclerViewFrequency;
        private StoreRecyclerAdapter.RemainingCategoryAdapter remainingCategoryAdapter;
        private TextView addButton, subtractButton;
        private TextView noOfItemSelected, repeat_every, out_of_stock;
        private LinearLayout savingHolder, savingAmountHolder;
        private TextView productName, productBrand, size, savings, no_of_options_holder;
        private ImageView productImage;
        private FrequencyAndPriceAdapter frequencyAndPriceAdapter;
        private LinearLayout addButtonViewHolder, updateQuantityViewHolder;
        private int position;

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
            savings = (TextView) itemView.findViewById(R.id.text_view_savings);
            savingAmountHolder = (LinearLayout) itemView.findViewById(R.id.holder_saving_amount);
            addButtonViewHolder = (LinearLayout) itemView.findViewById(R.id.holder_add_button);
            updateQuantityViewHolder = (LinearLayout) itemView.findViewById(R.id.holder_adjust_quantity);
        }

        private void setViewsBasedOnStock(boolean isOutOfStock) {
            if (isOutOfStock) {
                addButtonViewHolder.setVisibility(View.GONE);
                addButton.setVisibility(View.GONE);
                subtractButton.setVisibility(View.GONE);
                savingHolder.setVisibility(View.GONE);
                repeat_every.setVisibility(View.GONE);
                out_of_stock.setVisibility(View.VISIBLE);

                // Disable the change button
                no_of_options_holder.setTextColor(TheBox.getInstance().getResources().getColor(R.color.dim_gray));
            } else {
                addButtonViewHolder.setVisibility(View.VISIBLE);
                addButton.setVisibility(View.VISIBLE);
                subtractButton.setVisibility(View.VISIBLE);
                savingHolder.setVisibility(View.VISIBLE);
                repeat_every.setVisibility(View.VISIBLE);
                out_of_stock.setVisibility(View.GONE);
                // Disable the change button
                no_of_options_holder.setTextColor(TheBox.getInstance().getResources().getColor(R.color.dim_gray));
            }
        }

        private void setupRecyclerViewFrequency(final BoxItem boxItem, final int position, boolean shouldScrollToPosition) {
            // hash map of frequency and corresponding PriceSizeAndSizeUnit ArrayList.
            if (userItems == null || userItems.isEmpty()) {
                getAdapterPosition();
            }

            RealmList<ItemConfig> itemConfigs = boxItem.getItemConfigsBySelectedItemConfig();
            Collections.sort(itemConfigs, new Comparator<ItemConfig>() {
                @Override
                public int compare(ItemConfig lhs, ItemConfig rhs) {
                    if (lhs.getSubscriptionTypeUnit() > rhs.getSubscriptionTypeUnit()) {
                        return 1;
                    } else if (lhs.getSubscriptionTypeUnit() < rhs.getSubscriptionTypeUnit()) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            });
            int selectedPosition = 0;
            for (int i = 0; i < itemConfigs.size(); i++) {
                if (boxItem.getSelectedItemConfig().equals(itemConfigs.get(i))) {
                    selectedPosition = i;
                    break;
                }
            }

            WrapContentLinearLayoutManager linearLayoutManager = new WrapContentLinearLayoutManager(TheBox.getInstance(), LinearLayoutManager.HORIZONTAL, false);
            if (!shouldScrollToPosition) {
                linearLayoutManager.scrollToPositionWithOffset(0, -boxItems.get(position).getHorizontalOffsetOfRecyclerView());
            }
            recyclerViewFrequency.setLayoutManager(linearLayoutManager);
            frequencyAndPriceAdapter = new FrequencyAndPriceAdapter(TheBox.getInstance(), selectedPosition, new FrequencyAndPriceAdapter.OnItemConfigChange() {
                @Override
                public void onItemConfigItemChange(ItemConfig selectedItemConfig) {
                    if (boxItems.get(position).getUserItemId() != 0) {
                        changeConfig(position, selectedItemConfig.getId());
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
            recyclerViewFrequency.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int temp = boxItems.get(position).getHorizontalOffsetOfRecyclerView();
                    temp = temp + dx;
                    boxItems.get(position).setHorizontalOffsetOfRecyclerView(temp);
                }
            });
            if (shouldScrollToPosition) {
                linearLayoutManager.scrollToPositionWithOffset(selectedPosition, 0);
            }
        }

        private void setupRecyclerViewSuggestedCategories(List<Category> suggestedCategories) {
            remainingCategoryAdapter = new StoreRecyclerAdapter.RemainingCategoryAdapter(TheBox.getInstance(), suggestedCategories, glideRequestManager);
            remainingCategoryAdapter.setSearchDetailItemFragment(true);
            recyclerViewSavings.setLayoutManager(new LinearLayoutManager(TheBox.getInstance(), LinearLayoutManager.HORIZONTAL, false));
            recyclerViewSavings.setAdapter(remainingCategoryAdapter);
        }

        public void setViews(final BoxItem boxItem, int arrayListPosition, final boolean shouldScrollToPosition) {

            try {
                this.position = arrayListPosition;
                productName.setText(boxItem.getTitle());
                productBrand.setText(boxItem.getBrand());

                //Updating Savings
                if (boxItem.getSavings() == 0) {
                    savingAmountHolder.setVisibility(View.GONE);
                } else {
                    savingAmountHolder.setVisibility(View.VISIBLE);
                    savings.setText(boxItem.getSavings() + " Rs Savings");
                }

                //Updating no. of SKU's
                if (boxItem.getNo_of_sku() < 2) {
                    no_of_options_holder.setVisibility(View.GONE);
                } else if (boxItem.getNo_of_sku() == 2) {
                    no_of_options_holder.setVisibility(View.VISIBLE);
                    no_of_options_holder.setText(" + 1 more option");
                } else {
                    no_of_options_holder.setVisibility(View.VISIBLE);
                    no_of_options_holder.setText(" + " + (boxItem.getNo_of_sku() - 1) + " more options");
                }


                if (boxItem.getItemConfigs() != null && !boxItem.getItemConfigs().isEmpty()) {
                    if (boxItem.getSelectedItemConfig().getCorrectQuantity().equals("NA")) {
                        size.setText(boxItem.getSelectedItemConfig().getSize() + " " + boxItem.getSelectedItemConfig().getSizeUnit() + " " + boxItem.getSelectedItemConfig().getItemType());
                    } else {
                        size.setText(boxItem.getSelectedItemConfig().getCorrectQuantity() + " x " + boxItem.getSelectedItemConfig().getSize() + " " + boxItem.getSelectedItemConfig().getSizeUnit() + " " + boxItem.getSelectedItemConfig().getItemType());
                    }
                }

                glideRequestManager.load(boxItem.getSelectedItemConfig().getPhotoUrl())
                        .centerCrop()
                        .crossFade()
                        .into(productImage);

                productImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = boxItem.getSelectedItemConfig().getPhotoUrl();
                        FullImageActivity.showImage(url, mContext);
                    }
                });


                no_of_options_holder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final SizeAndFrequencyBottomSheetDialogFragment dialogFragment = SizeAndFrequencyBottomSheetDialogFragment.newInstance(boxItem);
                        dialogFragment.show(((AppCompatActivity) mContext).getSupportFragmentManager()
                                , SizeAndFrequencyBottomSheetDialogFragment.TAG);
                        dialogFragment.attachListener(new SizeAndFrequencyBottomSheetDialogFragment.OnSizeAndFrequencySelected() {
                            @Override
                            public void onSizeAndFrequencySelected(ItemConfig selectedItemConfig) {
                                dialogFragment.dismiss();
                                if (boxItem.getUserItemId() == 0) {
                                    boxItem.setSelectedItemConfig(selectedItemConfig);
                                    setViews(boxItem, getAdapterPosition(), true);
                                } else {
                                    changeConfig(getAdapterPosition(), selectedItemConfig.getId());
                                }
                            }
                        });
                    }
                });

                // Checking if item is in stock
                if (boxItem.is_in_stock()) {
                    setViewsBasedOnStock(false);
                    addButtonViewHolder.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addItemToBox(position);
                        }
                    });
                    addButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            updateQuantity(position, boxItem.getQuantity() + 1);
                        }
                    });
                    subtractButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (boxItem.getQuantity() > 0) {
                                updateQuantity(position, boxItem.getQuantity() - 1);
                            } else {
                                Toast.makeText(TheBox.getInstance(), "Item count could not be negative", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    setupRecyclerViewFrequency(boxItem, position, shouldScrollToPosition);

                    noOfItemSelected.setText(String.valueOf(boxItem.getQuantity()));

                    if (boxItem.getId() == boxId && !suggestedCategories.isEmpty() && getAdapterPosition() == currentPositionOfSuggestedCategory) {
                        setupRecyclerViewSuggestedCategories(suggestedCategories);
                        savingHolder.setVisibility(View.VISIBLE);
                    } else {
                        savingHolder.setVisibility(View.GONE);
                    }

                    if (boxItem.getQuantity() == 0) {
                        addButtonViewHolder.setVisibility(View.VISIBLE);
                        updateQuantityViewHolder.setVisibility(View.GONE);
                        if (positionInViewPager == SearchDetailFragment.POSITION_OF_VIEW_PAGER) {
                            if (getAdapterPosition() == 0) {
                                if ((PrefUtils.getBoolean(TheBox.getInstance(), "move", true)) && (!RestClient.is_in_development)) {
                                    moveRecyclerView(true);
                                }
                                if ((PrefUtils.getBoolean(TheBox.getInstance(), "store_tutorial", true)) && (!RestClient.is_in_development)) {
                                    new ShowcaseHelper((Activity) mContext, 1).setTopPadding(20).show("Repeat", "Swipe right or left to select how soon to repeat", recyclerViewFrequency)
                                            .setOnCompleteListener(new ShowcaseHelper.OnCompleteListener() {
                                                @Override
                                                public void onComplete() {
                                                    PrefUtils.putBoolean(TheBox.getInstance(), "move", false);
                                                    PrefUtils.putBoolean(TheBox.getInstance(), "store_tutorial", false);
                                                    new ShowcaseHelper((Activity) mContext, 2)
                                                            .show("Add Item", "Add your favourite item to cart", addButtonViewHolder)
                                                            .setOnCompleteListener(new ShowcaseHelper.OnCompleteListener() {
                                                                @Override
                                                                public void onComplete() {
                                                                    EventBus.getDefault().post(new ShowTabTutorialEvent());
                                                                }
                                                            });
                                                }
                                            });
                                    moveRecyclerView(true);
                                }
                            }
                        }
                    } else {
                        addButtonViewHolder.setVisibility(View.GONE);
                        updateQuantityViewHolder.setVisibility(View.VISIBLE);
                    }
                }

                // If Item is not in stock
                else {
                    setViewsBasedOnStock(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void addItemToBox(final int position) {
            final BoxLoader dialog = new BoxLoader(mContext).show();
            TheBox.getAPIService().addToMyBox(PrefUtils.getToken(TheBox.getInstance()),
                    new AddToMyBoxRequestBody(
                            new AddToMyBoxRequestBody.Item(boxItems.get(position).getId()),
                            new AddToMyBoxRequestBody.ItemConfig(boxItems.get(position).getSelectedItemConfig().getId())))
                    .enqueue(new Callback<AddToMyBoxResponse>() {
                        @Override
                        public void onResponse(Call<AddToMyBoxResponse> call, Response<AddToMyBoxResponse> response) {
                            dialog.dismiss();
                            try {
                                if (response.isSuccessful()) {
                                    if (response.body() != null) {
                                        if (response.body().isSuccess()) {
                                            boxItems.get(position).setUserItemId(response.body().getUserItem().getId());
                                            boxItems.get(position).setQuantity(boxItems.get(position).getQuantity() + 1);

                                            boxId = boxItems.get(position).getId();
                                            suggestedCategories.clear();
                                            suggestedCategories.addAll(response.body().getRestOfTheCategoriesInTheBox());
                                            suggestedCategories.addAll(response.body().getRestOfTheCategoriesInOtherBox());
                                            currentPositionOfSuggestedCategory = position;

                                            notifyItemChanged(position);
                                            CartHelper.addOrUpdateUserItem(response.body().getUserItem(), response.body().get_cart());
                                        } else {
                                            Toast.makeText(TheBox.getInstance(), response.body().getInfo(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<AddToMyBoxResponse> call, Throwable t) {
                            dialog.dismiss();
                        }
                    });

        }

        private void updateQuantity(int position, final int quantity) throws ArrayIndexOutOfBoundsException {
            final BoxLoader dialog = new BoxLoader(mContext).show();
            int userItemId;
            if (userItems == null || userItems.isEmpty()) {
                position = getAdapterPosition();
            }
            userItemId = boxItems.get(position).getUserItemId();
            final int finalPosition = position;
            TheBox.getAPIService().updateQuantity(PrefUtils.getToken(TheBox.getInstance()), new UpdateItemQuantityRequestBody(new UpdateItemQuantityRequestBody.UserItem(userItemId, quantity)))
                    .enqueue(new Callback<UpdateItemConfigResponse>() {
                        @Override
                        public void onResponse(Call<UpdateItemConfigResponse> call, Response<UpdateItemConfigResponse> response) {
                            dialog.dismiss();
                            try {
                                if (response.isSuccessful()) {
                                    if (response.body() != null) {
                                        if (response.body().isSuccess()) {
                                            boxItems.get(finalPosition).setQuantity(quantity);
                                            if (quantity >= 1) {
                                                response.body().getUserItem().setBoxItem(boxItems.get(finalPosition));

                                                //Updating the SearchDetailitem fragment's data if change is made in cart
                                                //SearchDetailItemsFragment.update_boxitem(response.body().getUserItem().getSelectedItemId(),response.body().getUserItem().getQuantity());

                                                CartHelper.addOrUpdateUserItem(response.body().getUserItem(), response.body().get_cart());
                                                notifyItemChanged(getAdapterPosition());
                                            } else {
                                                CartHelper.removeUserItem(boxItems.get(finalPosition).getUserItemId(), response.body().get_cart());
                                                if (shouldRemoveBoxItemOnEmptyQuantity) {
                                                    boxItems.remove(finalPosition);
                                                    notifyItemRemoved(getAdapterPosition());
                                                    notifyItemRangeChanged(getAdapterPosition(), getItemCount());

                                                } else {
                                                    boxItems.get(finalPosition).setUserItemId(0);
                                                    notifyItemChanged(getAdapterPosition());
                                                }
                                            }
                                            Toast.makeText(TheBox.getInstance(), response.body().getInfo(), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(TheBox.getInstance(), response.body().getInfo(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<UpdateItemConfigResponse> call, Throwable t) {
                            dialog.dismiss();
                        }
                    });
        }

        private void changeConfig(final int position, final int itemConfigId) {
            final BoxLoader dialog = new BoxLoader(mContext).show();
            TheBox.getAPIService().updateItemConfig(PrefUtils.getToken(TheBox.getInstance()), new UpdateItemConfigurationRequest
                    (new UpdateItemConfigurationRequest.UserItem(boxItems.get(position).getUserItemId()), new UpdateItemConfigurationRequest.ItemConfig(itemConfigId)))
                    .enqueue(new Callback<UpdateItemConfigResponse>() {
                        @Override
                        public void onResponse(Call<UpdateItemConfigResponse> call, Response<UpdateItemConfigResponse> response) {
                            dialog.dismiss();
                            try {
                                if (response.isSuccessful()) {
                                    if (response.body() != null) {
                                        if (response.body().isSuccess()) {
                                            RealmList<Category> suggestionsCategories = boxItems.get(position).getSuggestedCategory();
                                            boxItems.set(position, response.body().getUserItem().getFakeBoxItemObject());
                                            boxItems.get(position).setSuggestedCategory(suggestionsCategories);
                                            notifyItemChanged(getAdapterPosition());
                                            CartHelper.addOrUpdateUserItem(response.body().getUserItem(), response.body().get_cart());
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<UpdateItemConfigResponse> call, Throwable t) {
                            dialog.dismiss();
                        }
                    });
        }


        public void moveRecyclerView(final boolean finalPosition) {
            if (PrefUtils.getBoolean(TheBox.getInstance(), "move", true)) {
                if (finalPosition)
                    recyclerViewFrequency.smoothScrollToPosition(frequencyAndPriceAdapter.getItemsCount());
                else {
                    recyclerViewFrequency.smoothScrollToPosition(0);
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        moveRecyclerView(!finalPosition);
                    }
                }, 500);
            }
        }
    }

    private class UserItemViewHolder extends RecyclerView.ViewHolder {

        private TextView productName, brand,
                arrivingTime, config, addButton, subtractButton, noOfItemSelected, frequency, price, edit;
        private ImageView productImageView;
        private LinearLayout quantityHolder;

        public UserItemViewHolder(View itemView) {
            super(itemView);
            productName = (TextView) itemView.findViewById(R.id.product_name);
            arrivingTime = (TextView) itemView.findViewById(R.id.arriving_time);
            config = (TextView) itemView.findViewById(R.id.config);
            productImageView = (ImageView) itemView.findViewById(R.id.product_image_view);
            addButton = (TextView) itemView.findViewById(R.id.button_add);
            subtractButton = (TextView) itemView.findViewById(R.id.button_subtract);
            noOfItemSelected = (TextView) itemView.findViewById(R.id.no_of_item_selected);
            quantityHolder = (LinearLayout) itemView.findViewById(R.id.layout_quantity_holder);
            price = (TextView) itemView.findViewById(R.id.price);
            frequency = (TextView) itemView.findViewById(R.id.frequency);
            edit = (TextView) itemView.findViewById(R.id.user_item_edit_button);
        }

        private void setViews(final UserItem userItem, final int arrayListPosition) {

            quantityHolder.setVisibility(View.VISIBLE);

            /**
             *  Edit Subscription Botton Dialog upon clicking Edit Subscription
             */

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
                                    //Change Item Config
                                    final SizeAndFrequencyBottomSheetDialogFragment dialogFragment = SizeAndFrequencyBottomSheetDialogFragment.newInstance(userItem.getBoxItem());
                                    dialogFragment.show(((AppCompatActivity) mContext).getSupportFragmentManager()
                                            , SizeAndFrequencyBottomSheetDialogFragment.TAG);
                                    dialogFragment.attachListener(new SizeAndFrequencyBottomSheetDialogFragment.OnSizeAndFrequencySelected() {
                                        @Override
                                        public void onSizeAndFrequencySelected(ItemConfig selectedItemConfig) {
                                            dialogFragment.dismiss();
                                            changeConfig(getAdapterPosition(), selectedItemConfig.getId());
                                        }
                                    });
                                    break;
                                case 2:
                                    //Reschedule
                                    new DelayDeliveryBottomSheet((Activity) mContext, new DelayDeliveryBottomSheet.OnDelayActionCompleted() {
                                        @Override
                                        public void onDelayActionCompleted(UserItem userItem) {

                                            try {

                                                if (userItem == null) {
                                                    userItems.remove(arrayListPosition);
                                                    if (onUserItemChange != null) {
                                                        onUserItemChange.onUserItemChange(userItems);
                                                    }
                                                    notifyItemRemoved(getAdapterPosition());
                                                } else {
                                                    userItems.set(arrayListPosition, userItem);
                                                    if (onUserItemChange != null) {
                                                        onUserItemChange.onUserItemChange(userItems);
                                                    }
                                                    notifyItemChanged(getAdapterPosition());
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).show(userItem);
                                    break;
                                case 3:
                                    //cancel subscription
                                    if (userItem.getQuantity() > 0) {
                                        MaterialDialog dialog = new MaterialDialog.Builder(mContext).
                                                title("Unsubscribe " + userItem.getBoxItem().getTitle()).
                                                positiveText("Cancel")
                                                .negativeText("Unsubscribe").
                                                        onNegative(new MaterialDialog.SingleButtonCallback() {
                                                            @Override
                                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                                if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                                                                    openCancelDialog(userItem, getAdapterPosition());
                                                                }
                                                            }
                                                        }).content("Unsubscribing " + userItem.getBoxItem().getTitle() + " will remove it from all subsequent orders. Are you sure you want to unsubscribe?").build();
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
            });

            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (PrefUtils.getBoolean(TheBox.getInstance(), "update_quantity_announcemnet", true)) {

                        Announcement confirm_change_is_not_intentded_for_a_particular_order = new Announcement(mContext, 0);
                        confirm_change_is_not_intentded_for_a_particular_order.build_it();
                        confirm_change_is_not_intentded_for_a_particular_order
                                .setPositiveButton(confirm_change_is_not_intentded_for_a_particular_order.getPositive_button_text_res_id(), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        PrefUtils.putBoolean(TheBox.getInstance(), "update_quantity_announcemnet", false);
                                        if (userItem.getQuantity() == 0) {
                                            addItemToBox(getAdapterPosition());
                                        } else {
                                            updateQuantity(getAdapterPosition(), userItem.getQuantity() + 1);
                                        }
                                    }
                                })
                                .setNegativeButton(confirm_change_is_not_intentded_for_a_particular_order.getNegativeText_button_text_res_id(), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        PrefUtils.putBoolean(TheBox.getInstance(), "update_quantity_announcemnet", false);
                                        Intent intent = new Intent(mContext, MainActivity.class)
                                                .putExtra(MainActivity.EXTRA_ATTACH_FRAGMENT_NO, 2);
                                        mContext.startActivity(intent);
                                    }
                                })
                                .show();

                    } else {
                        if (userItem.getQuantity() == 0) {
                            addItemToBox(getAdapterPosition());
                        } else {
                            updateQuantity(getAdapterPosition(), userItem.getQuantity() + 1);
                        }
                    }
                }
            });
            subtractButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (PrefUtils.getBoolean(TheBox.getInstance(), "update_quantity_announcemnet", true)) {

                        Announcement confirm_change_is_not_intentded_for_a_particular_order = new Announcement(mContext, 0);
                        confirm_change_is_not_intentded_for_a_particular_order.build_it();
                        confirm_change_is_not_intentded_for_a_particular_order
                                .setPositiveButton(confirm_change_is_not_intentded_for_a_particular_order.getPositive_button_text_res_id(), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {

                                        PrefUtils.putBoolean(TheBox.getInstance(), "update_quantity_announcemnet", false);

                                        if (userItem.getQuantity() > 1) {
                                            if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                                                updateQuantity(getAdapterPosition(), userItem.getQuantity() - 1);
                                            }
                                        } else if (userItem.getQuantity() == 1) {
                                            MaterialDialog dialog_unsubscribe = new MaterialDialog.Builder(mContext).
                                                    title("Unsubscribe " + userItem.getBoxItem().getTitle()).
                                                    positiveText("Cancel")
                                                    .negativeText("Unsubscribe").
                                                            onNegative(new MaterialDialog.SingleButtonCallback() {
                                                                @Override
                                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                                    if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                                                                        openCancelDialog(userItem, getAdapterPosition());
                                                                    }
                                                                }
                                                            }).content("Unsubscribing " + userItem.getBoxItem().getTitle() + " will remove it from all subsequent orders. Are you sure you want to unsubscribe?").build();
                                            dialog_unsubscribe.getWindow().getAttributes().windowAnimations = R.style.MyAnimation_Window;
                                            dialog_unsubscribe.show();
                                        } else {
                                            Toast.makeText(TheBox.getInstance(), "Item count could not be negative", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                })
                                .setNegativeButton(confirm_change_is_not_intentded_for_a_particular_order.getNegativeText_button_text_res_id(), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        PrefUtils.putBoolean(TheBox.getInstance(), "update_quantity_announcemnet", false);
                                        Intent intent = new Intent(mContext, MainActivity.class)
                                                .putExtra(MainActivity.EXTRA_ATTACH_FRAGMENT_NO, 11);
                                        mContext.startActivity(intent);
                                    }
                                })
                                .show();

                    } else {
                        if (userItem.getQuantity() > 1) {
                            if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                                updateQuantity(getAdapterPosition(), userItem.getQuantity() - 1);
                            }
                        } else if (userItem.getQuantity() == 1) {
                            MaterialDialog dialog = new MaterialDialog.Builder(mContext).
                                    title("Unsubscribe " + userItem.getBoxItem().getTitle()).
                                    positiveText("Cancel")
                                    .negativeText("Unsubscribe").
                                            onNegative(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                    if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                                                        openCancelDialog(userItem, getAdapterPosition());
                                                    }
                                                }
                                            }).content("Unsubscribing " + userItem.getBoxItem().getTitle() + " will remove it from all subsequent orders. Are you sure you want to unsubscribe?").build();
                            dialog.getWindow().getAttributes().windowAnimations = R.style.MyAnimation_Window;
                            dialog.show();
                        } else {
                            Toast.makeText(TheBox.getInstance(), "Item count could not be negative", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            });

            try {

                noOfItemSelected.setText(String.valueOf(userItem.getQuantity()));
                ItemConfig itemConfig = userItem.getBoxItem().getItemConfigById(userItem.getSelectedConfigId());
                price.setText("Rs " + itemConfig.getPrice() * userItem.getQuantity());
                frequency.setText("Repeat " + itemConfig.getSubscriptionText().toLowerCase());

                productName.setText(userItem.getBoxItem().getTitle());

                if (itemConfig.getCorrectQuantity().equals("NA")) {
                    config.setText(itemConfig.getSize() + " " + itemConfig.getSizeUnit());
                } else {
                    config.setText(itemConfig.getCorrectQuantity() + " x " +
                            itemConfig.getSize() + " " + itemConfig.getSizeUnit());
                }

                if (isHasUneditableUserItem()) {
                    arrivingTime.setVisibility(View.GONE);
                }
                if (userItem.getNextDeliveryScheduledAt() == null || userItem.getNextDeliveryScheduledAt().isEmpty()) {
                    arrivingTime.setText("Item is added to your cart");
                } else {
                    arrivingTime.setText(userItem.getArrivingAt());
                }

                glideRequestManager.load(itemConfig.getPhotoUrl())
                        .centerCrop()
                        .crossFade()
                        .into(productImageView);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        private void addItemToBox(final int position) throws IllegalStateException {
            final BoxLoader dialog = new BoxLoader(mContext).show();
            TheBox.getAPIService().addToMyBox(PrefUtils.getToken(TheBox.getInstance()),
                    new AddToMyBoxRequestBody(
                            new AddToMyBoxRequestBody.Item(userItems.get(position).getBoxItem().getId()),
                            new AddToMyBoxRequestBody.ItemConfig(userItems.get(position).getBoxItem().getSelectedItemConfig().getId())))
                    .enqueue(new Callback<AddToMyBoxResponse>() {
                        @Override
                        public void onResponse(Call<AddToMyBoxResponse> call, Response<AddToMyBoxResponse> response) {
                            dialog.dismiss();
                            try {
                                if (response.isSuccessful()) {
                                    if (response.body() != null) {
                                        if (response.body().isSuccess()) {
                                            Toast.makeText(TheBox.getInstance(), response.body().getInfo(), Toast.LENGTH_SHORT).show();
                                            CartHelper.addOrUpdateUserItem(response.body().getUserItem(), null);
                                        } else {
                                            Toast.makeText(TheBox.getInstance(), response.body().getInfo(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                } else {
                                    //parse error
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<AddToMyBoxResponse> call, Throwable t) {
                            dialog.dismiss();
                        }
                    });

        }

        private void updateQuantity(final int position, final int quantity) throws IllegalStateException {
            final BoxLoader dialog = new BoxLoader(mContext).show();

            TheBox.getAPIService().updateQuantity(PrefUtils.getToken(TheBox.getInstance()), new UpdateItemQuantityRequestBody(new UpdateItemQuantityRequestBody.UserItem(userItems.get(position).getId(), quantity)))
                    .enqueue(new Callback<UpdateItemConfigResponse>() {
                        @Override
                        public void onResponse(Call<UpdateItemConfigResponse> call, Response<UpdateItemConfigResponse> response) {
                            dialog.dismiss();
                            try {
                                if (response.isSuccessful()) {
                                    if (response.body() != null) {
                                        if (response.body().isSuccess()) {
                                            int prevQuantity = userItems.get(position).getQuantity();
                                            UserItem item = response.body().getUserItem();
                                            item.setBoxId(response.body().getBoxId());
                                            if (quantity >= 1) {
                                                CartHelper.addOrUpdateUserItem(item, null);
                                                notifyItemChanged(getAdapterPosition());
                                            } else {
                                                CartHelper.removeUserItem(userItems.get(position).getId(), null);
                                                notifyItemRemoved(getAdapterPosition());
                                            }
                                            if (onUserItemChange != null) {
                                                onUserItemChange.onUserItemChange(userItems);
                                            }

                                            OrderHelper.updateUserItemAndNotifiy(item);

                                            Toast.makeText(TheBox.getInstance(), response.body().getInfo(), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(TheBox.getInstance(), response.body().getInfo(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                } else {
                                    //handle error
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<UpdateItemConfigResponse> call, Throwable t) {
                            dialog.dismiss();
                        }
                    });
        }

        private void changeConfig(final int position, final int itemConfigId) {
            final BoxLoader dialog = new BoxLoader(mContext).show();
            TheBox.getAPIService().updateItemConfig(PrefUtils.getToken(TheBox.getInstance()), new UpdateItemConfigurationRequest
                    (new UpdateItemConfigurationRequest.UserItem(userItems.get(position).getId()), new UpdateItemConfigurationRequest.ItemConfig(itemConfigId)))
                    .enqueue(new Callback<UpdateItemConfigResponse>() {
                        @Override
                        public void onResponse(Call<UpdateItemConfigResponse> call, Response<UpdateItemConfigResponse> response) {
                            dialog.dismiss();
                            try {
                                if (response.isSuccessful()) {
                                    if (response.body() != null) {
                                        if (response.body().isSuccess()) {
                                            UserItem item = response.body().getUserItem();
                                            userItems.set(position, item);
                                            if (onUserItemChange != null) {
                                                onUserItemChange.onUserItemChange(userItems);
                                            }
                                            notifyItemChanged(getAdapterPosition());
                                            if (response.body().getUserItem().getNextDeliveryScheduledAt() == null
                                                    || response.body().getUserItem().getNextDeliveryScheduledAt().isEmpty()) {
                                                CartHelper.addOrUpdateUserItem(response.body().getUserItem(), null);
                                            }

                                            OrderHelper.updateUserItemAndNotifiy(item);
                                        }
                                    }
                                } else {
                                    //handle error
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<UpdateItemConfigResponse> call, Throwable t) {
                            dialog.dismiss();
                        }
                    });
        }

        private void openCancelDialog(final UserItem userItem, final int positionInArrayList) throws IllegalStateException {
            MaterialDialog dialog = new MaterialDialog.Builder(mContext).
                    title("Cancel Subscription").
                    customView(R.layout.layout_cancel_subscription, true).
                    positiveText("Cancel Subscription").onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                    View customView = dialog.getCustomView();
                    RadioGroup radioGroup = (RadioGroup) customView.findViewById(R.id.radio_group);
                    int radioButtonID = radioGroup.getCheckedRadioButtonId();
                    View radioButton = radioGroup.findViewById(radioButtonID);
                    int idx = radioGroup.indexOfChild(radioButton);
                    RadioButton r = (RadioButton) radioGroup.getChildAt(idx);
                    if (r == null) {
                        Toast.makeText(TheBox.getInstance(), "Select one of the given reasons", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String selectedtext = r.getText().toString();

                    final BoxLoader loader = new BoxLoader(mContext).show();
                    TheBox.getAPIService().cancelSubscription(PrefUtils.getToken(TheBox.getInstance())
                            , new CancelSubscriptionRequest(userItem.getId(), selectedtext))
                            .enqueue(new Callback<CancelSubscriptionResponse>() {
                                @Override
                                public void onResponse(Call<CancelSubscriptionResponse> call, Response<CancelSubscriptionResponse> response) {
                                    loader.dismiss();
                                    try {
                                        if (response.isSuccessful()) {
                                            if (response.body() != null) {
                                                if (response.body().isSuccess()) {
                                                    UserItem item = response.body().getUserItem();
                                                    userItems.remove(positionInArrayList);
                                                    if (onUserItemChange != null) {
                                                        onUserItemChange.onUserItemChange(userItems);
                                                    }
                                                    notifyItemRemoved(positionInArrayList);
                                                    dialog.dismiss();
                                                    CartHelper.removeUserItem(userItem.getId(), null);
                                                    OrderHelper.addAndNotify(response.body().getOrders());
                                                    Toast.makeText(TheBox.getInstance(), response.body().getInfo(), Toast.LENGTH_SHORT).show();

                                                } else {
                                                    Toast.makeText(TheBox.getInstance(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Toast.makeText(TheBox.getInstance(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            //handle error
                                            Toast.makeText(TheBox.getInstance(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        loader.dismiss();
                                        dialog.dismiss();
                                        Toast.makeText(TheBox.getAppContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<CancelSubscriptionResponse> call, Throwable t) {
                                    loader.dismiss();
                                    Toast.makeText(TheBox.getAppContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }).autoDismiss(false).build();


            dialog.getWindow().getAttributes().windowAnimations = R.style.MyAnimation_Window;
            dialog.show();
        }
    }

    public interface OnUserItemChange {
        void onUserItemChange(List<UserItem> userItems);
    }
}
