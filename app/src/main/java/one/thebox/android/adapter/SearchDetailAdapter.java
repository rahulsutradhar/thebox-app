
package one.thebox.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;

import io.realm.RealmList;
import one.thebox.android.Events.ShowSpecialCardEvent;
import one.thebox.android.Events.ShowTabTutorialEvent;
import one.thebox.android.Events.UpdateOrderItemEvent;
import one.thebox.android.Helpers.CartHelper;
import one.thebox.android.Helpers.OrderHelper;
import one.thebox.android.Models.BoxItem;
import one.thebox.android.Models.Category;
import one.thebox.android.Models.Invoice;
import one.thebox.android.Models.ItemConfig;
import one.thebox.android.Models.UserItem;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.Announcement;
import one.thebox.android.ViewHelper.BoxLoader;
import one.thebox.android.ViewHelper.DelayDeliveryBottomSheet;
import one.thebox.android.ViewHelper.ShowCaseHelper;
import one.thebox.android.ViewHelper.WrapContentLinearLayoutManager;
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
import one.thebox.android.app.MyApplication;
import one.thebox.android.fragment.EditItemFragment;
import one.thebox.android.fragment.SearchDetailFragment;
import one.thebox.android.fragment.SizeAndFrequencyBottomSheetDialogFragment;
import one.thebox.android.util.CoreGsonUtils;
import one.thebox.android.util.DateTimeUtil;
import one.thebox.android.util.DisplayUtil;
import one.thebox.android.util.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SEARCH_ITEM = 0;
    private static final int VIEW_TYPE_USER_ITEM = 1;
    private static final int VIEW_TYPE_ORDER_ITEM = 2;
    private RealmList<BoxItem> boxItems = new RealmList<>();
    private RealmList<UserItem> userItems = new RealmList<>();
    private RealmList<Invoice> useritems_quantities = new RealmList<>();
    private Context mContext;
    private boolean shouldRemoveBoxItemOnEmptyQuantity;
    private boolean hasUneditableUserItem;
    private int currentPositionOfSuggestedCategory = -1;
    private int positionInViewPager = -1;
    private int order_id;
    private OnUserItemChange onUserItemChange;

    public RealmList<UserItem> getUserItems() {
        return userItems;
    }

    public void setUserItems(RealmList<UserItem> userItems) {
        this.userItems = userItems;
    }

    public RealmList<Invoice>  getUserItemQuantities() {
        return useritems_quantities;
    }

    public void setUserItemQuantities(int order_id,RealmList<Invoice> useritems_quantities) {
        this.useritems_quantities = useritems_quantities;
        this.order_id = order_id;
    }

    public int getOrderId() {return order_id;}

    public void setOrderId(int order_id) {
        this.order_id = order_id;
    }

    public RealmList<BoxItem> getBoxItems() {
        return boxItems;
    }

    public void setBoxItems(RealmList<BoxItem> boxItems) {
        this.boxItems = boxItems;
    }

    public SearchDetailAdapter(Context context) {
        this.mContext = context;
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

    public void setBoxItems(RealmList<BoxItem> boxItems, RealmList<UserItem> userItems) {
        if (boxItems != null) {
            this.boxItems = boxItems;
        }
        if (userItems != null) {
            this.userItems = userItems;

        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case VIEW_TYPE_USER_ITEM: {
                View itemView = LayoutInflater.from(MyApplication.getInstance()).inflate(R.layout.item_user_item, parent, false);
                return new UserItemViewHolder(itemView);
            }
            case VIEW_TYPE_SEARCH_ITEM: {
                View itemView = LayoutInflater.from(MyApplication.getInstance()).inflate(R.layout.item_search_detail_items, parent, false);
                return new SearchedItemViewHolder(itemView);
            }
            case VIEW_TYPE_ORDER_ITEM: {
                View itemView = LayoutInflater.from(MyApplication.getInstance()).inflate(R.layout.item_order_item, parent, false);
                return new OrderItemViewHolder(itemView);
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SearchedItemViewHolder) {
            bindSearchViewHolder(holder, position - (userItems == null ? 0 : userItems.size()));
        } else if (holder instanceof UserItemViewHolder)  {
            bindMyItemViewHolder(holder, position);
        } else {
            bindOrderItemViewHolder(holder, position);
        }
    }

    private void bindOrderItemViewHolder(final RecyclerView.ViewHolder holder, int position){
        OrderItemViewHolder itemViewHolder = (OrderItemViewHolder) holder;
        userItems.get(position).getBoxItem().setSelectedItemConfig(
                userItems.get(position).getBoxItem().getItemConfigById(userItems.get(position).getSelectedConfigId()
                ));
        itemViewHolder.setViews(useritems_quantities,userItems.get(position), position);
    }

    private void bindMyItemViewHolder(final RecyclerView.ViewHolder holder, int position) {
        UserItemViewHolder itemViewHolder = (UserItemViewHolder) holder;
        userItems.get(position).getBoxItem().setSelectedItemConfig(
                userItems.get(position).getBoxItem().getItemConfigById(userItems.get(position).getSelectedConfigId()
                ));
        itemViewHolder.setViews(userItems.get(position), position);
    }

    private void bindSearchViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final SearchedItemViewHolder searchedItemViewHolder = (SearchedItemViewHolder) holder;
        if (boxItems.get(position).getSelectedItemConfig() == null) {
            boxItems.get(position).setSelectedItemConfig(boxItems.get(position).getSmallestInStockItemConfig());
        }
        searchedItemViewHolder.setViews(boxItems.get(position), position, false);
    }

    @Override
    public int getItemCount() {
        return (boxItems == null ? 0 : boxItems.size()) + (userItems == null ? 0 : userItems.size());
    }

    @Override
    public int getItemViewType(int position) {
        int  index;
        if (useritems_quantities.isEmpty()) {
            index = position < (userItems == null ? 0 : userItems.size()) ? VIEW_TYPE_USER_ITEM : VIEW_TYPE_SEARCH_ITEM;
        }
        else {
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
            changeButton = (TextView) itemView.findViewById(R.id.button_change);
            price = (TextView) itemView.findViewById(R.id.price);
            frequency = (TextView) itemView.findViewById(R.id.frequency);
        }

        private void setViews(final RealmList<Invoice> useritems_quantities, final UserItem userItem, final int arrayListPosition) {


            for (Invoice i : useritems_quantities){
                if(i.getUseritem_id() == userItem.getId() ){
                    quantity_for_this_order = i.getInvoice_quantity();
                    noOfItemSelected.setText(String.valueOf(i.getInvoice_quantity()));
                }
            }

            quantityHolder.setVisibility(View.VISIBLE);

            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateQuantity(getAdapterPosition(),quantity_for_this_order + 1);
                }
            });

            subtractButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (quantity_for_this_order > 1) {
                        updateQuantity(getAdapterPosition(), quantity_for_this_order - 1);
                    } else if (quantity_for_this_order == 1) {
                        MaterialDialog dialog = new MaterialDialog.Builder(mContext).
                                title("Remove " + userItem.getBoxItem().getTitle())
                                .positiveText("Cancel")
                                .negativeText("Remove")
                                .content( userItem.getBoxItem().getTitle() + " will be removed from this order. Are you sure?")
                                .callback(new MaterialDialog.ButtonCallback() {
                                    @Override
                                    public void onPositive(MaterialDialog materialDialog) {

                                        // Dismiss the dialog
                                        materialDialog.cancel();
                                    }

                                    @Override
                                    public void onNegative(MaterialDialog materialDialog) {

                                        // Making update call
                                        updateQuantity(getAdapterPosition(), 0);

                                    }
                                })
                                .build();
                        dialog.getWindow().getAttributes().windowAnimations = R.style.MyAnimation_Window;
                        dialog.show();
                    } else {
                        Toast.makeText(MyApplication.getInstance(), "Item count could not be negative", Toast.LENGTH_SHORT).show();
                    }

                }
            });


            ItemConfig itemConfig = userItem.getBoxItem().getItemConfigById(userItem.getSelectedConfigId());

            price.setText("Rs " + itemConfig.getPrice() * quantity_for_this_order);
            frequency.setText("Repeat every " + itemConfig.getSubscriptionType().toLowerCase());

            productName.setText(userItem.getBoxItem().getTitle());

            if (itemConfig.getCorrectQuantity().equals("NA")) {
                config.setText(itemConfig.getSize() + " " + itemConfig.getSizeUnit());
            } else {
                config.setText(itemConfig.getCorrectQuantity() + " x " +
                        itemConfig.getSize() + " " + itemConfig.getSizeUnit());
            }

            savings.setText(userItem.getBoxItem().getSavings() + " Rs saved per month");
            if (isHasUneditableUserItem()) {
                arrivingTime.setVisibility(View.GONE);

            }

            if (userItem.getNextDeliveryScheduledAt() == null || userItem.getNextDeliveryScheduledAt().isEmpty()) {
                arrivingTime.setText("Item is added to your cart");

            } else {
                    long days = DateTimeUtil.getDifferenceAsDay(Calendar.getInstance().getTime(), DateTimeUtil.convertStringToDate(userItem.getNextDeliveryScheduledAt()));
                    if (days <= 1) {
                        int hours = (int) DateTimeUtil.getDifferenceAsHours(Calendar.getInstance().getTime(), DateTimeUtil.convertStringToDate(userItem.getNextDeliveryScheduledAt()));
                        if (DateTimeUtil.isArrivingToday(hours)) {
                            arrivingTime.setText("Arriving Today");
                        } else {
                            arrivingTime.setText("Arriving Tomorrow");
                        }
                    } else {
                        arrivingTime.setText("Arriving in " + days + " days");
                    }
            }

            Glide.with(mContext)
                    .load(itemConfig.getPhotoUrl())
                    .centerCrop()
                    .crossFade()
                    .into(productImageView);


//            Picasso.with(MyApplication.getInstance()).load(itemConfig.getPhotoUrl()).fit().into(productImageView);
            //Picasso.with(MyApplication.getInstance()).load(itemConfig.getPhotoUrl()).resize(116,116).into(productImageView);
        }

        private void updateQuantity(final int position, final int quantity) throws IllegalStateException {
            final BoxLoader dialog = new BoxLoader(mContext).show();
            MyApplication.getAPIService().updateOrderQuantity(PrefUtils.getToken(MyApplication.getInstance()), new UpdateOrderItemQuantityRequestBody(order_id,userItems.get(position).getId(),quantity))
                    .enqueue(new Callback<UpdateOrderItemResponse>() {
                        @Override
                        public void onResponse(Call<UpdateOrderItemResponse> call, Response<UpdateOrderItemResponse> response) {
                            dialog.dismiss();
                            if (response.body() != null) {
                                if (response.body().isSuccess()) {

                                    if (quantity >= 1) {
                                        setUserItemQuantities(response.body().getOrder().getId(),response.body().getOrder().getUserItemQuantities());
                                        setUserItems(response.body().getOrder().getUserItems());
                                        notifyItemChanged(getAdapterPosition());
                                    } else {
                                        setUserItemQuantities(response.body().getOrder().getId(),response.body().getOrder().getUserItemQuantities());
                                        setUserItems(response.body().getOrder().getUserItems());
                                        notifyItemRemoved(getAdapterPosition());
                                    }
                                    OrderHelper.addAndNotify(response.body().getOrder());
                                    EventBus.getDefault().post(new UpdateOrderItemEvent());
                                    Toast.makeText(MyApplication.getInstance(), response.body().getInfo(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MyApplication.getInstance(), response.body().getInfo(), Toast.LENGTH_SHORT).show();
                                }
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
        private TextView changeButton, noOfItemSelected, repeat_every, out_of_stock;
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
            changeButton = (TextView) itemView.findViewById(R.id.button_change);
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

        private void setupRecyclerViewFrequency(final BoxItem boxItem, final int position, boolean shouldScrollToPosition) {
            // hash map of frequency and corresponding PriceSizeAndSizeUnit ArrayList.
            if(userItems==null || userItems.isEmpty()) {
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

            WrapContentLinearLayoutManager linearLayoutManager = new WrapContentLinearLayoutManager(MyApplication.getInstance(), LinearLayoutManager.HORIZONTAL, false);
            if (!shouldScrollToPosition) {
                linearLayoutManager.scrollToPositionWithOffset(0, -boxItems.get(position).getHorizontalOffsetOfRecyclerView());
            }
            recyclerViewFrequency.setLayoutManager(linearLayoutManager);
            frequencyAndPriceAdapter = new FrequencyAndPriceAdapter(MyApplication.getInstance(), selectedPosition, new FrequencyAndPriceAdapter.OnItemConfigChange() {
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

        private void setupRecyclerViewSuggestedCategories(RealmList<Category> suggestedCategories) {
            remainingCategoryAdapter = new StoreRecyclerAdapter.RemainingCategoryAdapter(MyApplication.getInstance(), suggestedCategories);
            remainingCategoryAdapter.setSearchDetailItemFragment(true);
            recyclerViewSavings.setLayoutManager(new LinearLayoutManager(MyApplication.getInstance(), LinearLayoutManager.HORIZONTAL, false));
            recyclerViewSavings.setAdapter(remainingCategoryAdapter);
        }

        public void setViews(final BoxItem boxItem, int arrayListPosition, final boolean shouldScrollToPosition) {

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
                changeButton.setVisibility(View.GONE);
            } else if (boxItem.getNo_of_sku() == 2) {
                no_of_options_holder.setVisibility(View.VISIBLE);
                changeButton.setVisibility(View.VISIBLE);
                no_of_options_holder.setText(" + 1 option");
            } else {
                no_of_options_holder.setVisibility(View.VISIBLE);
                changeButton.setVisibility(View.VISIBLE);
                no_of_options_holder.setText(" + " + (boxItem.getNo_of_sku() - 1) + " options");
            }


            if (boxItem.getItemConfigs() != null && !boxItem.getItemConfigs().isEmpty()) {
                if (boxItem.getSelectedItemConfig().getCorrectQuantity().equals("NA")) {
                    size.setText(boxItem.getSelectedItemConfig().getSize() + " " + boxItem.getSelectedItemConfig().getSizeUnit() + " " + boxItem.getSelectedItemConfig().getItemType());
                } else {
                    size.setText(boxItem.getSelectedItemConfig().getCorrectQuantity() + " x " + boxItem.getSelectedItemConfig().getSize() + " " + boxItem.getSelectedItemConfig().getSizeUnit() + " " + boxItem.getSelectedItemConfig().getItemType());
                }
            }
//            Picasso.with(MyApplication.getInstance()).load(boxItem.getSelectedItemConfig().getPhotoUrl()).fit().into(productImage);
            Glide.with(mContext)
                    .load(boxItem.getSelectedItemConfig().getPhotoUrl())
                    .centerCrop()
                    .crossFade()
                    .into(productImage);


            changeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final SizeAndFrequencyBottomSheetDialogFragment dialogFragment = SizeAndFrequencyBottomSheetDialogFragment.newInstance(boxItems.get(position));
                    dialogFragment.show(((AppCompatActivity) mContext).getSupportFragmentManager()
                            , SizeAndFrequencyBottomSheetDialogFragment.TAG);
                    dialogFragment.attachListener(new SizeAndFrequencyBottomSheetDialogFragment.OnSizeAndFrequencySelected() {
                        @Override
                        public void onSizeAndFrequencySelected(ItemConfig selectedItemConfig) {
                            dialogFragment.dismiss();
                            if (boxItem.getUserItemId() == 0) {
                                boxItem.setSelectedItemConfig(selectedItemConfig);
                                setViews(boxItem, position, true);
                            } else {
                                changeConfig(position, selectedItemConfig.getId());
                            }
                        }
                    });
                }
            });

            // Checking if item is in stock
            if (boxItem.is_in_stock()) {

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
                            Toast.makeText(MyApplication.getInstance(), "Item count could not be negative", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                setupRecyclerViewFrequency(boxItem, position, shouldScrollToPosition);

                noOfItemSelected.setText(String.valueOf(boxItem.getQuantity()));

                if (boxItem.getSuggestedCategory() != null && !boxItem.getSuggestedCategory().isEmpty() && position == currentPositionOfSuggestedCategory) {
                    setupRecyclerViewSuggestedCategories(boxItem.getSuggestedCategory());
                    savingHolder.setVisibility(View.VISIBLE);
                } else {
                    savingHolder.setVisibility(View.GONE);
                }
                if (boxItem.getQuantity() == 0) {
                    addButtonViewHolder.setVisibility(View.VISIBLE);
                    updateQuantityViewHolder.setVisibility(View.GONE);
                    if (positionInViewPager == SearchDetailFragment.POSITION_OF_VIEW_PAGER) {
                        if (getAdapterPosition() == 0) {
                            if (PrefUtils.getBoolean(MyApplication.getInstance(), "move", true)) {
                                moveRecyclerView(true);
                            }
                            if ((PrefUtils.getBoolean(MyApplication.getInstance(), "store_tutorial", true)) && (!RestClient.is_in_development) ) {
                                new ShowCaseHelper((Activity) mContext, 1).setTopPadding(20).show("Repeat", "Swipe right or left to select how soon to repeat", recyclerViewFrequency)
                                        .setOnCompleteListener(new ShowCaseHelper.OnCompleteListener() {
                                            @Override
                                            public void onComplete() {
                                                PrefUtils.putBoolean(MyApplication.getInstance(), "move", false);
                                                PrefUtils.putBoolean(MyApplication.getInstance(), "store_tutorial", false);
                                                new ShowCaseHelper((Activity) mContext, 2)
                                                        .show("Add Item", "Add your favourite item to cart", addButtonViewHolder)
                                                        .setOnCompleteListener(new ShowCaseHelper.OnCompleteListener() {
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
            else{
                addButtonViewHolder.setVisibility(View.GONE);
                addButton.setVisibility(View.GONE);
                subtractButton.setVisibility(View.GONE);
                savingHolder.setVisibility(View.GONE);
                repeat_every.setVisibility(View.GONE);
                out_of_stock.setVisibility(View.VISIBLE);

                // Disable the change button
                changeButton.setTextColor(MyApplication.getInstance().getResources().getColor(R.color.dim_gray));
            }
        }

        private void addItemToBox(final int position) {
            final BoxLoader dialog = new BoxLoader(mContext).show();
            MyApplication.getAPIService().addToMyBox(PrefUtils.getToken(MyApplication.getInstance()),
                    new AddToMyBoxRequestBody(
                            new AddToMyBoxRequestBody.Item(boxItems.get(position).getId()),
                            new AddToMyBoxRequestBody.ItemConfig(boxItems.get(position).getSelectedItemConfig().getId())))
                    .enqueue(new Callback<AddToMyBoxResponse>() {
                        @Override
                        public void onResponse(Call<AddToMyBoxResponse> call, Response<AddToMyBoxResponse> response) {
                            dialog.dismiss();
                            if (response.body() != null) {
                                if (response.body().isSuccess()) {
                                    //Toast.makeText(MyApplication.getInstance(), response.body().getInfo(), Toast.LENGTH_SHORT).show();
                                    boxItems.get(position).setUserItemId(response.body().getUserItem().getId());
                                    boxItems.get(position).setQuantity(boxItems.get(position).getQuantity() + 1);
                                    RealmList<Category> suggestedCategories = new RealmList<Category>();
                                    suggestedCategories.addAll(response.body().getRestOfTheCategoriesInTheBox());
                                    suggestedCategories.addAll(response.body().getRestOfTheCategoriesInOtherBox());
                                    boxItems.get(position).setSuggestedCategory(suggestedCategories);
                                    CartHelper.addOrUpdateUserItem(response.body().getUserItem(), response.body().get_cart());
                                    int temp = currentPositionOfSuggestedCategory;
                                    currentPositionOfSuggestedCategory = position;
                                    if (temp != -1) {
                                        boxItems.get(temp).setSuggestedCategory(new RealmList<Category>());
                                        notifyItemChanged(temp);
                                    }
                                    notifyItemChanged(getAdapterPosition());
                                } else {
                                    Toast.makeText(MyApplication.getInstance(), response.body().getInfo(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<AddToMyBoxResponse> call, Throwable t) {
                            dialog.dismiss();
                        }
                    });

        }

        private void updateQuantity(int position, final int quantity) {
            final BoxLoader dialog = new BoxLoader(mContext).show();
            int userItemId;
            if (userItems == null || userItems.isEmpty()) {
                position = getAdapterPosition();
            }
            userItemId = boxItems.get(position).getUserItemId();
            final int finalPosition = position;
            MyApplication.getAPIService().updateQuantity(PrefUtils.getToken(MyApplication.getInstance()), new UpdateItemQuantityRequestBody(new UpdateItemQuantityRequestBody.UserItem(userItemId, quantity)))
                    .enqueue(new Callback<UpdateItemConfigResponse>() {
                        @Override
                        public void onResponse(Call<UpdateItemConfigResponse> call, Response<UpdateItemConfigResponse> response) {
                            dialog.dismiss();
                            if (response.body() != null) {
                                if (response.body().isSuccess()) {
                                    boxItems.get(finalPosition).setQuantity(quantity);
                                    if (quantity >= 1) {
                                        response.body().getUserItem().setBoxItem(boxItems.get(finalPosition));
                                        CartHelper.addOrUpdateUserItem(response.body().getUserItem(), response.body().get_cart());
                                        notifyItemChanged(getAdapterPosition());
                                    } else {
                                        CartHelper.removeUserItem(boxItems.get(finalPosition).getUserItemId(), response.body().get_cart());
                                        if (shouldRemoveBoxItemOnEmptyQuantity) {
                                            boxItems.remove(getAdapterPosition());
                                            notifyItemRemoved(getAdapterPosition());
                                        } else {
                                            boxItems.get(finalPosition).setUserItemId(0);
                                            notifyItemChanged(getAdapterPosition());
                                        }
                                    }
                                    Toast.makeText(MyApplication.getInstance(), response.body().getInfo(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MyApplication.getInstance(), response.body().getInfo(), Toast.LENGTH_SHORT).show();
                                }
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
            MyApplication.getAPIService().updateItemConfig(PrefUtils.getToken(MyApplication.getInstance()), new UpdateItemConfigurationRequest
                    (new UpdateItemConfigurationRequest.UserItem(boxItems.get(position).getUserItemId()), new UpdateItemConfigurationRequest.ItemConfig(itemConfigId)))
                    .enqueue(new Callback<UpdateItemConfigResponse>() {
                        @Override
                        public void onResponse(Call<UpdateItemConfigResponse> call, Response<UpdateItemConfigResponse> response) {
                            dialog.dismiss();
                            if (response.body() != null) {
                                RealmList<Category> suggestionsCategories = boxItems.get(position).getSuggestedCategory();
                                boxItems.set(position, response.body().getUserItem().getFakeBoxItemObject());
                                boxItems.get(position).setSuggestedCategory(suggestionsCategories);
                                notifyItemChanged(getAdapterPosition());
                                CartHelper.addOrUpdateUserItem(response.body().getUserItem(), response.body().get_cart());
                            }
                        }

                        @Override
                        public void onFailure(Call<UpdateItemConfigResponse> call, Throwable t) {
                            dialog.dismiss();
                        }
                    });
        }


        public void moveRecyclerView(final boolean finalPosition) {
            if (PrefUtils.getBoolean(MyApplication.getInstance(), "move", true)) {
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
                arrivingTime, config, addButton, subtractButton, noOfItemSelected, frequency, price;
        private ImageView productImageView,edit;
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
            edit = (ImageView) itemView.findViewById(R.id.user_item_edit_button);
        }

        private void setViews(final UserItem userItem, final int arrayListPosition) {
            quantityHolder.setVisibility(View.VISIBLE);

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final EditItemFragment dialogFragment = EditItemFragment.newInstance();
                    dialogFragment.show(((AppCompatActivity) mContext).getSupportFragmentManager()
                            , EditItemFragment.TAG);
                    dialogFragment.attachListener(new EditItemFragment.OnEditItemoptionSelected() {
                        @Override
                        public void onEditItemoptionSelected(Boolean change_size_or_reschedule){

                            dialogFragment.dismiss();

                            // true if change_size was clicked
                            // false otherwise
                            if (change_size_or_reschedule){

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

                            }
                            else{

                                new DelayDeliveryBottomSheet((Activity) mContext, new DelayDeliveryBottomSheet.OnDelayActionCompleted() {
                                    @Override
                                    public void onDelayActionCompleted(UserItem userItem) {
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
                                    }
                                }).show(userItem);

                            }

                        }
                    });
                }
            });

            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (PrefUtils.getBoolean(MyApplication.getInstance(), "update_quantity_announcemnet", true)) {

                        Announcement confirm_change_is_not_intentded_for_a_particular_order = new Announcement(mContext, 0);
                        confirm_change_is_not_intentded_for_a_particular_order.build_it();
                        confirm_change_is_not_intentded_for_a_particular_order
                                .setPositiveButton(confirm_change_is_not_intentded_for_a_particular_order.getPositive_button_text_res_id(), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        PrefUtils.putBoolean(MyApplication.getInstance(), "update_quantity_announcemnet", false);
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
                                        PrefUtils.putBoolean(MyApplication.getInstance(), "update_quantity_announcemnet", false);
                                        Intent intent = new Intent(mContext, MainActivity.class)
                                                .putExtra(MainActivity.EXTRA_ATTACH_FRAGMENT_NO, 11);
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


                    if (PrefUtils.getBoolean(MyApplication.getInstance(), "update_quantity_announcemnet", true)) {

                        Announcement confirm_change_is_not_intentded_for_a_particular_order = new Announcement(mContext, 0);
                        confirm_change_is_not_intentded_for_a_particular_order.build_it();
                        confirm_change_is_not_intentded_for_a_particular_order
                                .setPositiveButton(confirm_change_is_not_intentded_for_a_particular_order.getPositive_button_text_res_id(), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {

                                        PrefUtils.putBoolean(MyApplication.getInstance(), "update_quantity_announcemnet", false);

                                        if (userItem.getQuantity() > 1) {
                                            updateQuantity(getAdapterPosition(), userItem.getQuantity() - 1);
                                        } else if (userItem.getQuantity() == 1) {
                                            MaterialDialog dialog_unsubscribe = new MaterialDialog.Builder(mContext).
                                                    title("Unsubscribe " + userItem.getBoxItem().getTitle()).
                                                    positiveText("Cancel")
                                                    .negativeText("Unsubscribe").
                                                            onNegative(new MaterialDialog.SingleButtonCallback() {
                                                                @Override
                                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                                    openCancelDialog(userItem, getAdapterPosition());
                                                                }
                                                            }).content("Unsubscribing " + userItem.getBoxItem().getTitle() + " will remove it from all subsequent orders. Are you sure you want to unsubscribe?").build();
                                            dialog_unsubscribe.getWindow().getAttributes().windowAnimations = R.style.MyAnimation_Window;
                                            dialog_unsubscribe.show();
                                        } else {
                                            Toast.makeText(MyApplication.getInstance(), "Item count could not be negative", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                })
                                .setNegativeButton(confirm_change_is_not_intentded_for_a_particular_order.getNegativeText_button_text_res_id(), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        PrefUtils.putBoolean(MyApplication.getInstance(), "update_quantity_announcemnet", false);
                                        Intent intent = new Intent(mContext, MainActivity.class)
                                                .putExtra(MainActivity.EXTRA_ATTACH_FRAGMENT_NO, 11);
                                        mContext.startActivity(intent);
                                    }
                                })
                                .show();

                    } else {
                        if (userItem.getQuantity() > 1) {
                            updateQuantity(getAdapterPosition(), userItem.getQuantity() - 1);
                        } else if (userItem.getQuantity() == 1) {
                            MaterialDialog dialog = new MaterialDialog.Builder(mContext).
                                    title("Unsubscribe " + userItem.getBoxItem().getTitle()).
                                    positiveText("Cancel")
                                    .negativeText("Unsubscribe").
                                            onNegative(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                    openCancelDialog(userItem, getAdapterPosition());
                                                }
                                            }).content("Unsubscribing " + userItem.getBoxItem().getTitle() + " will remove it from all subsequent orders. Are you sure you want to unsubscribe?").build();
                            dialog.getWindow().getAttributes().windowAnimations = R.style.MyAnimation_Window;
                            dialog.show();
                        } else {
                            Toast.makeText(MyApplication.getInstance(), "Item count could not be negative", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            });
            noOfItemSelected.setText(String.valueOf(userItem.getQuantity()));
            ItemConfig itemConfig = userItem.getBoxItem().getItemConfigById(userItem.getSelectedConfigId());
            //userItem.getBoxItem().getSelectedItemConfig();
            price.setText("Rs " + itemConfig.getPrice() * userItem.getQuantity());
            frequency.setText("Repeat every " + itemConfig.getSubscriptionType().toLowerCase());

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
                    long days = DateTimeUtil.getDifferenceAsDay(Calendar.getInstance().getTime(), DateTimeUtil.convertStringToDate(userItem.getNextDeliveryScheduledAt()));
                    if (days <= 1) {
                        int hours = (int) DateTimeUtil.getDifferenceAsHours(Calendar.getInstance().getTime(), DateTimeUtil.convertStringToDate(userItem.getNextDeliveryScheduledAt()));
                        if (DateTimeUtil.isArrivingToday(hours)) {
                            arrivingTime.setText("Arriving Today");
                        } else {
                            arrivingTime.setText("Arriving Tomorrow");
                        }
                    } else {
                        arrivingTime.setText("Arriving in " + days + " days");
                    }
            }
            Glide.with(mContext)
                    .load(itemConfig.getPhotoUrl())
                    .centerCrop()
                    .crossFade()
                    .into(productImageView);

//            Picasso.with(MyApplication.getInstance()).load(itemConfig.getPhotoUrl()).fit().into(productImageView);
            //Picasso.with(MyApplication.getInstance()).load(itemConfig.getPhotoUrl()).resize(116,116).into(productImageView);
        }

        private void addItemToBox(final int position) throws IllegalStateException {
            final BoxLoader dialog = new BoxLoader(mContext).show();
            MyApplication.getAPIService().addToMyBox(PrefUtils.getToken(MyApplication.getInstance()),
                    new AddToMyBoxRequestBody(
                            new AddToMyBoxRequestBody.Item(userItems.get(position).getBoxItem().getId()),
                            new AddToMyBoxRequestBody.ItemConfig(userItems.get(position).getBoxItem().getSelectedItemConfig().getId())))
                    .enqueue(new Callback<AddToMyBoxResponse>() {
                        @Override
                        public void onResponse(Call<AddToMyBoxResponse> call, Response<AddToMyBoxResponse> response) {
                            dialog.dismiss();
                            if (response.body() != null) {
                                if (response.body().isSuccess()) {
                                    Toast.makeText(MyApplication.getInstance(), response.body().getInfo(), Toast.LENGTH_SHORT).show();
                                    userItems.set(position, response.body().getUserItem());
                                    notifyItemChanged(getAdapterPosition());
                                    CartHelper.addOrUpdateUserItem(response.body().getUserItem(),null);
                                } else {
                                    Toast.makeText(MyApplication.getInstance(), response.body().getInfo(), Toast.LENGTH_SHORT).show();
                                }
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
            MyApplication.getAPIService().updateQuantity(PrefUtils.getToken(MyApplication.getInstance()), new UpdateItemQuantityRequestBody(new UpdateItemQuantityRequestBody.UserItem(userItems.get(position).getId(), quantity)))
                    .enqueue(new Callback<UpdateItemConfigResponse>() {
                        @Override
                        public void onResponse(Call<UpdateItemConfigResponse> call, Response<UpdateItemConfigResponse> response) {
                            dialog.dismiss();
                            if (response.body() != null) {
                                if (response.body().isSuccess()) {
                                    int prevQuantity = userItems.get(position).getQuantity();
                                    if (quantity >= 1) {
                                        userItems.get(position).setQuantity(quantity);
                                        CartHelper.addOrUpdateUserItem(response.body().getUserItem(),null);
                                        notifyItemChanged(getAdapterPosition());
                                    } else {
                                        CartHelper.removeUserItem(userItems.get(position).getId(),null);
                                        userItems.remove(position);
                                        notifyItemRemoved(getAdapterPosition());
                                    }
                                    if (onUserItemChange != null) {
                                        onUserItemChange.onUserItemChange(userItems);
                                    }
                                    OrderHelper.addAndNotify(response.body().getOrders());
                                    EventBus.getDefault().post(new UpdateOrderItemEvent());
                                    Toast.makeText(MyApplication.getInstance(), response.body().getInfo(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MyApplication.getInstance(), response.body().getInfo(), Toast.LENGTH_SHORT).show();
                                }
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
            MyApplication.getAPIService().updateItemConfig(PrefUtils.getToken(MyApplication.getInstance()), new UpdateItemConfigurationRequest
                    (new UpdateItemConfigurationRequest.UserItem(userItems.get(position).getId()), new UpdateItemConfigurationRequest.ItemConfig(itemConfigId)))
                    .enqueue(new Callback<UpdateItemConfigResponse>() {
                        @Override
                        public void onResponse(Call<UpdateItemConfigResponse> call, Response<UpdateItemConfigResponse> response) {
                            dialog.dismiss();
                            if (response.body() != null) {
                                userItems.set(position, response.body().getUserItem());
                                if (onUserItemChange != null) {
                                    onUserItemChange.onUserItemChange(userItems);
                                }
                                notifyItemChanged(getAdapterPosition());
                                if (response.body().getUserItem().getNextDeliveryScheduledAt() == null
                                        || response.body().getUserItem().getNextDeliveryScheduledAt().isEmpty()) {
                                    CartHelper.addOrUpdateUserItem(response.body().getUserItem(),null);
                                }
                                OrderHelper.addAndNotify(response.body().getOrders());
                                if (getAdapterPosition() != -1)
                                    EventBus.getDefault().post(new UpdateOrderItemEvent());
                            }
                        }

                        @Override
                        public void onFailure(Call<UpdateItemConfigResponse> call, Throwable t) {
                            dialog.dismiss();
                        }
                    });
        }

        private void openCancelDialog(final UserItem userItem, final int positionInArrayList) {
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
                        Toast.makeText(MyApplication.getInstance(), "Select one of the given reasons", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String selectedtext = r.getText().toString();

                    final BoxLoader loader = new BoxLoader(mContext).show();
                    MyApplication.getAPIService().cancelSubscription(PrefUtils.getToken(MyApplication.getInstance())
                            , new CancelSubscriptionRequest(userItem.getId(), selectedtext))
                            .enqueue(new Callback<CancelSubscriptionResponse>() {
                                @Override
                                public void onResponse(Call<CancelSubscriptionResponse> call, Response<CancelSubscriptionResponse> response) {
                                    loader.dismiss();
                                    if (response.body() != null) {
                                        Toast.makeText(MyApplication.getInstance(), response.body().getInfo(), Toast.LENGTH_SHORT).show();
                                        if (response.body().isSuccess()) {
                                            userItems.remove(getAdapterPosition());
                                            if (onUserItemChange != null) {
                                                onUserItemChange.onUserItemChange(userItems);
                                            }
                                            notifyItemRemoved(getAdapterPosition());
                                            dialog.dismiss();
                                            OrderHelper.addAndNotify(response.body().getOrders());
                                            EventBus.getDefault().post(new UpdateOrderItemEvent());
                                            
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<CancelSubscriptionResponse> call, Throwable t) {
                                    loader.dismiss();
                                }
                            });
                }
            }).autoDismiss(false).build();


            dialog.getWindow().getAttributes().windowAnimations = R.style.MyAnimation_Window;
            dialog.show();
        }
    }

    public interface OnUserItemChange {
        void onUserItemChange(RealmList<UserItem> userItems);
    }
}
//=======
//package one.thebox.android.adapter;
//
//import android.app.Activity;
//import android.content.Context;
//import android.support.annotation.NonNull;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RadioButton;
//import android.widget.RadioGroup;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.afollestad.materialdialogs.DialogAction;
//import com.afollestad.materialdialogs.MaterialDialog;
//import com.squareup.picasso.Picasso;
//
//import org.greenrobot.eventbus.EventBus;
//
//import java.text.ParseException;
//import java.util.Calendar;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.List;
//
//import io.realm.RealmList;
//import one.thebox.android.Events.ShowTabTutorialEvent;
//import one.thebox.android.Events.TabEvent;
//import one.thebox.android.Helpers.CartHelper;
//import one.thebox.android.Models.BoxItem;
//import one.thebox.android.Models.Category;
//import one.thebox.android.Models.ItemConfig;
//import one.thebox.android.Models.UserItem;
//import one.thebox.android.R;
//import one.thebox.android.ViewHelper.BoxLoader;
//import one.thebox.android.ViewHelper.DelayDeliveryBottomSheet;
//import one.thebox.android.ViewHelper.ShowcaseHelper;
//import one.thebox.android.ViewHelper.WrapContentLinearLayoutManager;
//import one.thebox.android.api.RequestBodies.AddToMyBoxRequestBody;
//import one.thebox.android.api.RequestBodies.CancelSubscriptionRequest;
//import one.thebox.android.api.RequestBodies.UpdateItemConfigurationRequest;
//import one.thebox.android.api.RequestBodies.UpdateItemQuantityRequestBody;
//import one.thebox.android.api.Responses.AddToMyBoxResponse;
//import one.thebox.android.api.Responses.CancelSubscriptionResponse;
//import one.thebox.android.api.Responses.UpdateItemConfigResponse;
//import one.thebox.android.app.MyApplication;
//import one.thebox.android.fragment.SizeAndFrequencyBottomSheetDialogFragment;
//import one.thebox.android.util.DateTimeUtil;
//import one.thebox.android.util.PrefUtils;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class SearchDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//
//    private static final int VIEW_TYPE_SEARCH_ITEM = 0;
//    private static final int VIEW_TYPE_USER_ITEM = 1;
//    private RealmList<BoxItem> boxItems = new RealmList<>();
//    private RealmList<UserItem> userItems = new RealmList<>();
//    private Context mContext;
//    private boolean shouldRemoveBoxItemOnEmptyQuantity;
//    private boolean hasUneditableUserItem;
//    private int currentPositionOfSuggestedCategory = -1;
//    private int positionInViewPager = -1;
//
//    public SearchDetailAdapter(Context context) {
//        this.mContext = context;
//    }
//
//    public boolean isHasUneditableUserItem() {
//        return hasUneditableUserItem;
//    }
//
//    public void setHasUneditableUserItem(boolean hasUneditableUserItem) {
//        this.hasUneditableUserItem = hasUneditableUserItem;
//    }
//
//    public int getPositionInViewPager() {
//        return positionInViewPager;
//    }
//
//    public void setPositionInViewPager(int positionInViewPager) {
//        this.positionInViewPager = positionInViewPager;
//    }
//
//    public void setShouldRemoveBoxItemOnEmptyQuantity(boolean shouldRemoveBoxItemOnEmptyQuantity) {
//        this.shouldRemoveBoxItemOnEmptyQuantity = shouldRemoveBoxItemOnEmptyQuantity;
//    }
//
//    public void setBoxItems(RealmList<BoxItem> boxItems, RealmList<UserItem> userItems) {
//        if (boxItems != null) {
//            this.boxItems = boxItems;
//        }
//        if (this.userItems != null) {
//            this.userItems = userItems;
//        }
//    }
//
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//
//        switch (viewType) {
//            case VIEW_TYPE_USER_ITEM: {
//                View itemView = LayoutInflater.from(MyApplication.getInstance()).inflate(R.layout.item_user_item, parent, false);
//                return new UserItemViewHolder(itemView);
//            }
//            case VIEW_TYPE_SEARCH_ITEM: {
//                View itemView = LayoutInflater.from(MyApplication.getInstance()).inflate(R.layout.item_search_detail_items, parent, false);
//                return new SearchedItemViewHolder(itemView);
//            }
//        }
//        return null;
//    }
//
//    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        if (holder instanceof SearchedItemViewHolder) {
//            bindSearchViewHolder(holder, position - (userItems == null ? 0 : userItems.size()));
//        } else {
//            bindMyItemViewHolder(holder, position);
//        }
//    }
//
//    private void bindMyItemViewHolder(final RecyclerView.ViewHolder holder, int position) {
//        UserItemViewHolder itemViewHolder = (UserItemViewHolder) holder;
//        userItems.get(position).getBoxItem().setSelectedItemConfig(
//                userItems.get(position).getBoxItem().getItemConfigById(userItems.get(position).getSelectedConfigId()
//                ));
//        itemViewHolder.setViews(userItems.get(position), position);
//    }
//
//    private void bindSearchViewHolder(final RecyclerView.ViewHolder holder, final int position) {
//        final SearchedItemViewHolder searchedItemViewHolder = (SearchedItemViewHolder) holder;
//        if (boxItems.get(position).getSelectedItemConfig() == null) {
//            boxItems.get(position).setSelectedItemConfig(boxItems.get(position).getItemConfigById(0));
//        }
//        searchedItemViewHolder.setViews(boxItems.get(position), position, false);
//    }
//
//    @Override
//    public int getItemCount() {
//        return (boxItems == null ? 0 : boxItems.size()) + (userItems == null ? 0 : userItems.size());
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        return position < (userItems == null ? 0 : userItems.size()) ? VIEW_TYPE_USER_ITEM : VIEW_TYPE_SEARCH_ITEM;
//    }
//
//    private class SearchedItemViewHolder extends RecyclerView.ViewHolder {
//
//        private RecyclerView recyclerViewSavings;
//        private RecyclerView recyclerViewFrequency;
//        private MyBoxRecyclerAdapter.RemainingCategoryAdapter remainingCategoryAdapter;
//        private TextView addButton, subtractButton;
//        private TextView changeButton, noOfItemSelected;
//        private LinearLayout savingHolder, savingAmountHolder;
//        private TextView productName, productBrand, size, savings;
//        private ImageView productImage;
//        private FrequencyAndPriceAdapter frequencyAndPriceAdapter;
//        private LinearLayout addButtonViewHolder, updateQuantityViewHolder;
//        private int position;
//
//        private SearchedItemViewHolder(View itemView) {
//            super(itemView);
//            recyclerViewSavings = (RecyclerView) itemView.findViewById(R.id.relatedCategories);
//            addButton = (TextView) itemView.findViewById(R.id.button_add);
//            subtractButton = (TextView) itemView.findViewById(R.id.button_subtract);
//            changeButton = (TextView) itemView.findViewById(R.id.button_change);
//            noOfItemSelected = (TextView) itemView.findViewById(R.id.no_of_item_selected);
//            savingHolder = (LinearLayout) itemView.findViewById(R.id.saving_holder);
//            recyclerViewFrequency = (RecyclerView) itemView.findViewById(R.id.recycler_view_frequency);
//            productName = (TextView) itemView.findViewById(R.id.product_name);
//            productBrand = (TextView) itemView.findViewById(R.id.product_brand);
//            size = (TextView) itemView.findViewById(R.id.text_view_size);
//            productImage = (ImageView) itemView.findViewById(R.id.product_image);
//            savings = (TextView) itemView.findViewById(R.id.text_view_savings);
//            savingAmountHolder = (LinearLayout) itemView.findViewById(R.id.holder_saving_amount);
//            addButtonViewHolder = (LinearLayout) itemView.findViewById(R.id.holder_add_button);
//            updateQuantityViewHolder = (LinearLayout) itemView.findViewById(R.id.holder_adjust_quantity);
//        }
//
//        private void setupRecyclerViewFrequency(final BoxItem boxItem, final int position, boolean shouldScrollToPosition) {
//            // hash map of frequency and corresponding PriceSizeAndSizeUnit ArrayList.
//            RealmList<ItemConfig> itemConfigs = boxItem.getItemConfigsBySelectedItemConfig();
//            Collections.sort(itemConfigs, new Comparator<ItemConfig>() {
//                @Override
//                public int compare(ItemConfig lhs, ItemConfig rhs) {
//                    if (lhs.getSubscriptionTypeUnit() > rhs.getSubscriptionTypeUnit()) {
//                        return 1;
//                    } else if (lhs.getSubscriptionTypeUnit() < rhs.getSubscriptionTypeUnit()) {
//                        return -1;
//                    } else {
//                        return 0;
//                    }
//                }
//            });
//            int selectedPosition = 0;
//            for (int i = 0; i < itemConfigs.size(); i++) {
//                if (boxItem.getSelectedItemConfig().equals(itemConfigs.get(i))) {
//                    selectedPosition = i;
//                    break;
//                }
//            }
//
//            WrapContentLinearLayoutManager linearLayoutManager = new WrapContentLinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
//            if (!shouldScrollToPosition) {
//                linearLayoutManager.scrollToPositionWithOffset(0, -boxItems.get(position).getHorizontalOffsetOfRecyclerView());
//            }
//            recyclerViewFrequency.setLayoutManager(linearLayoutManager);
//            frequencyAndPriceAdapter = new FrequencyAndPriceAdapter(mContext, selectedPosition, new FrequencyAndPriceAdapter.OnItemConfigChange() {
//                @Override
//                public void onItemConfigItemChange(ItemConfig selectedItemConfig) {
//                    if (boxItems.get(position).getUserItemId() != 0) {
//                        changeConfig(position, selectedItemConfig.getId());
//                    } else {
//                        boxItems.get(position).setSelectedItemConfig(selectedItemConfig);
//                        notifyItemChanged(getAdapterPosition());
//                    }
//                }
//            });
//            frequencyAndPriceAdapter.setItemConfigs(itemConfigs);
//            recyclerViewFrequency.setAdapter(frequencyAndPriceAdapter);
//            recyclerViewFrequency.setOnScrollListener(new RecyclerView.OnScrollListener() {
//                @Override
//                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                    super.onScrolled(recyclerView, dx, dy);
//                    int temp = boxItems.get(position).getHorizontalOffsetOfRecyclerView();
//                    temp = temp + dx;
//                    boxItems.get(position).setHorizontalOffsetOfRecyclerView(temp);
//                }
//            });
//            if (shouldScrollToPosition) {
//                linearLayoutManager.scrollToPositionWithOffset(selectedPosition, 0);
//            }
//        }
//
//        private void setupRecyclerViewSuggestedCategories(RealmList<Category> suggestedCategories) {
//            remainingCategoryAdapter = new MyBoxRecyclerAdapter.RemainingCategoryAdapter(mContext, suggestedCategories);
//            remainingCategoryAdapter.setSearchDetailItemFragment(true);
//            recyclerViewSavings.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
//            recyclerViewSavings.setAdapter(remainingCategoryAdapter);
//        }
//
//        public void setViews(final BoxItem boxItem, int arrayListPosition, boolean shouldScrollToPosition) {
//            this.position = arrayListPosition;
//            addButtonViewHolder.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    addItemToBox(position);
//                }
//            });
//            addButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    updateQuantity(position, boxItem.getQuantity() + 1);
//                }
//            });
//            subtractButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (boxItem.getQuantity() > 0) {
//                        updateQuantity(position, boxItem.getQuantity() - 1);
//                    } else {
//                        Toast.makeText(mContext, "Item count could not be negative", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//            changeButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    final SizeAndFrequencyBottomSheetDialogFragment dialogFragment = SizeAndFrequencyBottomSheetDialogFragment.newInstance(boxItems.get(position));
//                    dialogFragment.show(((AppCompatActivity) mContext).getSupportFragmentManager()
//                            , SizeAndFrequencyBottomSheetDialogFragment.TAG);
//                    dialogFragment.attachListener(new SizeAndFrequencyBottomSheetDialogFragment.OnSizeAndFrequencySelected() {
//                        @Override
//                        public void onSizeAndFrequencySelected(ItemConfig selectedItemConfig) {
//                            dialogFragment.dismiss();
//                            if (boxItem.getUserItemId() == 0) {
//                                boxItem.setSelectedItemConfig(selectedItemConfig);
//                                setViews(boxItem, position, true);
//                            } else {
//                                changeConfig(position, selectedItemConfig.getId());
//                            }
//                        }
//                    });
//                }
//            });
//            setupRecyclerViewFrequency(boxItem, position, shouldScrollToPosition);
//            noOfItemSelected.setText(String.valueOf(boxItem.getQuantity()));
//            if (boxItem.getSuggestedCategory() != null && !boxItem.getSuggestedCategory().isEmpty() && position == currentPositionOfSuggestedCategory) {
//                setupRecyclerViewSuggestedCategories(boxItem.getSuggestedCategory());
//                savingHolder.setVisibility(View.VISIBLE);
//            } else {
//                savingHolder.setVisibility(View.GONE);
//            }
//            if (boxItem.getQuantity() == 0) {
//                addButtonViewHolder.setVisibility(View.VISIBLE);
//                updateQuantityViewHolder.setVisibility(View.GONE);
//                if(positionInViewPager == 0) {
//                    new ShowcaseHelper((Activity) mContext, 1).show("Repeat", "Swipe right or left to select how soon to repeat", recyclerViewFrequency)
//                            .setOnCompleteListener(new ShowcaseHelper.OnCompleteListener() {
//                                @Override
//                                public void onComplete() {
//                                    new ShowcaseHelper((Activity) mContext, 2)
//                                            .show("Add Item", "Add your favourite item to cart", addButtonViewHolder)
//                                            .setOnCompleteListener(new ShowcaseHelper.OnCompleteListener() {
//                                                @Override
//                                                public void onComplete() {
//                                                    EventBus.getDefault().post(new ShowTabTutorialEvent());
//                                                }
//                                            });
//                                }
//                            });
//                }
//            } else {
//                addButtonViewHolder.setVisibility(View.GONE);
//                updateQuantityViewHolder.setVisibility(View.VISIBLE);
//            }
//            productName.setText(boxItem.getTitle());
//            productBrand.setText(boxItem.getBrand());
//            if (boxItem.getSavings() == 0) {
//                savingAmountHolder.setVisibility(View.GONE);
//            } else {
//                savingAmountHolder.setVisibility(View.VISIBLE);
//                savings.setText(boxItem.getSavings() + " Rs Savings");
//            }
//            if (boxItem.getItemConfigs() != null && !boxItem.getItemConfigs().isEmpty()) {
//                size.setText(boxItem.getSelectedItemConfig().getSize() + " " + boxItem.getSelectedItemConfig().getSizeUnit() + " " + boxItem.getSelectedItemConfig().getItemType());
//            }
//            Picasso.with(mContext).load(boxItem.getSelectedItemConfig().getPhotoUrl()).into(productImage);
//        }
//
//        private void addItemToBox(final int position) {
//            final BoxLoader dialog = new BoxLoader(mContext).show();
//            MyApplication.getAPIService().addToMyBox(PrefUtils.getToken(mContext),
//                    new AddToMyBoxRequestBody(
//                            new AddToMyBoxRequestBody.Item(boxItems.get(position).getId()),
//                            new AddToMyBoxRequestBody.ItemConfig(boxItems.get(position).getSelectedItemConfig().getId())))
//                    .enqueue(new Callback<AddToMyBoxResponse>() {
//                        @Override
//                        public void onResponse(Call<AddToMyBoxResponse> call, Response<AddToMyBoxResponse> response) {
//                            dialog.dismiss();
//                            if (response.body() != null) {
//                                if (response.body().isSuccess()) {
//                                    Toast.makeText(mContext, response.body().getInfo(), Toast.LENGTH_SHORT).show();
//                                    boxItems.get(position).setUserItemId(response.body().getUserItem().getId());
//                                    boxItems.get(position).setQuantity(boxItems.get(position).getQuantity() + 1);
//                                    RealmList<Category> suggestedCategories = new RealmList<Category>();
//                                    suggestedCategories.addAll(response.body().getRestOfTheCategoriesInTheBox());
//                                    suggestedCategories.addAll(response.body().getRestOfTheCategoriesInOtherBox());
//                                    boxItems.get(position).setSuggestedCategory(suggestedCategories);
//                                    CartHelper.addOrUpdateUserItem(response.body().getUserItem());
//                                    int temp = currentPositionOfSuggestedCategory;
//                                    currentPositionOfSuggestedCategory = position;
//                                    if (temp != -1) {
//                                        boxItems.get(temp).setSuggestedCategory(new RealmList<Category>());
//                                        notifyItemChanged(temp);
//                                    }
//                                    notifyItemChanged(getAdapterPosition());
//                                } else {
//                                    Toast.makeText(mContext, response.body().getInfo(), Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<AddToMyBoxResponse> call, Throwable t) {
//                            dialog.dismiss();
//                        }
//                    });
//
//        }
//
//        private void updateQuantity(final int position, final int quantity) {
//            final BoxLoader dialog = new BoxLoader(mContext).show();
//            MyApplication.getAPIService().updateQuantity(PrefUtils.getToken(mContext), new UpdateItemQuantityRequestBody(new UpdateItemQuantityRequestBody.UserItem(boxItems.get(position).getUserItemId(), quantity)))
//                    .enqueue(new Callback<UpdateItemConfigResponse>() {
//                        @Override
//                        public void onResponse(Call<UpdateItemConfigResponse> call, Response<UpdateItemConfigResponse> response) {
//                            dialog.dismiss();
//                            if (response.body() != null) {
//                                if (response.body().isSuccess()) {
//                                    int prevQuantity = boxItems.get(position).getQuantity();
//                                    boxItems.get(position).setQuantity(quantity);
//                                    if (quantity >= 1) {
//                                        RealmList<ItemConfig> itemConfigs = boxItems.get(position).getItemConfigs();
//                                        for (int i = 0; i < itemConfigs.size(); i++) {
//                                            itemConfigs.get(i).setPrice((int) (itemConfigs.get(i).getPrice() * ((float) quantity / (float) prevQuantity)));
//                                        }
//                                        boxItems.get(position).setItemConfigs(itemConfigs);
//                                        response.body().getUserItem().setBoxItem(boxItems.get(position));
//                                        CartHelper.addOrUpdateUserItem(response.body().getUserItem());
//                                        notifyItemChanged(getAdapterPosition());
//                                    } else {
//                                        CartHelper.removeUserItem(boxItems.get(position).getUserItemId());
//                                        if (shouldRemoveBoxItemOnEmptyQuantity) {
//                                            boxItems.remove(position);
//                                            notifyItemRemoved(getAdapterPosition());
//                                        } else {
//                                            boxItems.get(position).setUserItemId(0);
//                                            notifyItemChanged(getAdapterPosition());
//                                        }
//                                    }
//                                    Toast.makeText(mContext, response.body().getInfo(), Toast.LENGTH_SHORT).show();
//                                } else {
//                                    Toast.makeText(mContext, response.body().getInfo(), Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<UpdateItemConfigResponse> call, Throwable t) {
//                            dialog.dismiss();
//                        }
//                    });
//        }
//
//        private void changeConfig(final int position, final int itemConfigId) {
//            final BoxLoader dialog = new BoxLoader(mContext).show();
//            MyApplication.getAPIService().updateItemConfig(PrefUtils.getToken(mContext), new UpdateItemConfigurationRequest
//                    (new UpdateItemConfigurationRequest.UserItem(boxItems.get(position).getUserItemId()), new UpdateItemConfigurationRequest.ItemConfig(itemConfigId)))
//                    .enqueue(new Callback<UpdateItemConfigResponse>() {
//                        @Override
//                        public void onResponse(Call<UpdateItemConfigResponse> call, Response<UpdateItemConfigResponse> response) {
//                            dialog.dismiss();
//                            if (response.body() != null) {
//                                RealmList<Category> suggestionsCategories = boxItems.get(position).getSuggestedCategory();
//                                boxItems.set(position, response.body().getUserItem().getFakeBoxItemObject());
//                                boxItems.get(position).setSuggestedCategory(suggestionsCategories);
//                                notifyItemChanged(getAdapterPosition());
//                                CartHelper.addOrUpdateUserItem(response.body().getUserItem());
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<UpdateItemConfigResponse> call, Throwable t) {
//                            dialog.dismiss();
//                        }
//                    });
//        }
//    }
//
//    private class UserItemViewHolder extends RecyclerView.ViewHolder {
//
//        private TextView adjustButton, productName, brand,
//                arrivingTime, config, savings, addButton, subtractButton, noOfItemSelected, changeButton, frequency, price;
//        private ImageView productImageView;
//        private LinearLayout quantityHolder;
//
//        public UserItemViewHolder(View itemView) {
//            super(itemView);
//            adjustButton = (TextView) itemView.findViewById(R.id.adjust);
//            productName = (TextView) itemView.findViewById(R.id.product_name);
//            brand = (TextView) itemView.findViewById(R.id.product_brand);
//            arrivingTime = (TextView) itemView.findViewById(R.id.arriving_time);
//            config = (TextView) itemView.findViewById(R.id.config);
//            savings = (TextView) itemView.findViewById(R.id.savings);
//            productImageView = (ImageView) itemView.findViewById(R.id.product_image_view);
//            addButton = (TextView) itemView.findViewById(R.id.button_add);
//            subtractButton = (TextView) itemView.findViewById(R.id.button_subtract);
//            noOfItemSelected = (TextView) itemView.findViewById(R.id.no_of_item_selected);
//            quantityHolder = (LinearLayout) itemView.findViewById(R.id.layout_quantity_holder);
//            changeButton = (TextView) itemView.findViewById(R.id.button_change);
//            price = (TextView) itemView.findViewById(R.id.price);
//            frequency = (TextView) itemView.findViewById(R.id.frequency);
//        }
//
//        private void setViews(final UserItem userItem, final int arrayListPosition) {
//            quantityHolder.setVisibility(View.VISIBLE);
//            adjustButton.setVisibility(View.VISIBLE);
//            changeButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    final SizeAndFrequencyBottomSheetDialogFragment dialogFragment = SizeAndFrequencyBottomSheetDialogFragment.newInstance(userItem.getBoxItem());
//                    dialogFragment.show(((AppCompatActivity) mContext).getSupportFragmentManager()
//                            , SizeAndFrequencyBottomSheetDialogFragment.TAG);
//                    dialogFragment.attachListener(new SizeAndFrequencyBottomSheetDialogFragment.OnSizeAndFrequencySelected() {
//                        @Override
//                        public void onSizeAndFrequencySelected(ItemConfig selectedItemConfig) {
//                            dialogFragment.dismiss();
//                            changeConfig(arrayListPosition, selectedItemConfig.getId());
//                        }
//                    });
//                }
//            });
//            addButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (userItem.getQuantity() == 0) {
//                        addItemToBox(arrayListPosition);
//                    } else {
//                        updateQuantity(arrayListPosition, userItem.getQuantity() + 1);
//                    }
//                }
//            });
//            subtractButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (userItem.getQuantity() > 1) {
//                        updateQuantity(arrayListPosition, userItem.getQuantity() - 1);
//                    } else if (userItem.getQuantity() == 1) {
//                        MaterialDialog dialog = new MaterialDialog.Builder(mContext).
//                                title("Unsubscribe " + userItem.getBoxItem().getTitle()).
//                                positiveText("Cancel")
//                                .negativeText("Unsubscribe").
//                                        onNegative(new MaterialDialog.SingleButtonCallback() {
//                                            @Override
//                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                                openCancelDialog(userItem, arrayListPosition);
//                                            }
//                                        }).content("By unsubscribing " + userItem.getBoxItem().getTitle() + " will remove it from all subsequent orders. Are you sure you want to unsubscribe?").build();
//                        dialog.getWindow().getAttributes().windowAnimations = R.style.MyAnimation_Window;
//                        dialog.show();
//                    } else {
//                        Toast.makeText(mContext, "Item count could not be negative", Toast.LENGTH_SHORT).show();
//                    }
//
//                }
//            });
//            noOfItemSelected.setText(String.valueOf(userItem.getQuantity()));
//            ItemConfig itemConfig = userItem.getBoxItem().getItemConfigById(userItem.getSelectedConfigId());
//            //userItem.getBoxItem().getSelectedItemConfig();
//            price.setText("Rs " + itemConfig.getPrice());
//            frequency.setText("Repeats every " + itemConfig.getSubscriptionType().toLowerCase());
//            adjustButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    new DelayDeliveryBottomSheet((Activity) mContext, new DelayDeliveryBottomSheet.OnDelayActionCompleted() {
//                        @Override
//                        public void onDelayActionCompleted(UserItem userItem) {
//                            if (userItem == null) {
//                                userItems.remove(arrayListPosition);
//                                notifyItemRemoved(getAdapterPosition());
//                            } else {
//                                userItems.set(arrayListPosition, userItem);
//                                notifyItemChanged(getAdapterPosition());
//                            }
//                        }
//                    }).show(userItem);
//                }
//            });
//
//
//            productName.setText(userItem.getBoxItem().getTitle());
//            config.setText(itemConfig.getSize() + " " + itemConfig.getSizeUnit());
//            brand.setText(userItem.getBoxItem().getBrand());
//            savings.setText(userItem.getBoxItem().getSavings() + " Rs saved per month");
//            if (userItem.getNextDeliveryScheduledAt() == null || userItem.getNextDeliveryScheduledAt().isEmpty()) {
//                arrivingTime.setText("Item has added to your cart");
//            } else {
//                try {
//                    arrivingTime.setText("Arriving in " + DateTimeUtil.getDifferenceAsDay(Calendar.getInstance().getTime(), DateTimeUtil.convertStringToDate(userItem.getNextDeliveryScheduledAt())) + " days");
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//            }
//            Picasso.with(mContext).load(itemConfig.getPhotoUrl()).into(productImageView);
//        }
//
//        private void addItemToBox(final int position) throws IllegalStateException {
//            final BoxLoader dialog = new BoxLoader(mContext).show();
//            MyApplication.getAPIService().addToMyBox(PrefUtils.getToken(mContext),
//                    new AddToMyBoxRequestBody(
//                            new AddToMyBoxRequestBody.Item(userItems.get(position).getBoxItem().getId()),
//                            new AddToMyBoxRequestBody.ItemConfig(userItems.get(position).getBoxItem().getSelectedItemConfig().getId())))
//                    .enqueue(new Callback<AddToMyBoxResponse>() {
//                        @Override
//                        public void onResponse(Call<AddToMyBoxResponse> call, Response<AddToMyBoxResponse> response) {
//                            dialog.dismiss();
//                            if (response.body() != null) {
//                                if (response.body().isSuccess()) {
//                                    Toast.makeText(mContext, response.body().getInfo(), Toast.LENGTH_SHORT).show();
//                                    userItems.set(position, response.body().getUserItem());
//                                    notifyItemChanged(getAdapterPosition());
//                                    CartHelper.addOrUpdateUserItem(response.body().getUserItem());
//                                } else {
//                                    Toast.makeText(mContext, response.body().getInfo(), Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<AddToMyBoxResponse> call, Throwable t) {
//                            dialog.dismiss();
//                        }
//                    });
//
//        }
//
//        private void updateQuantity(final int position, final int quantity) throws IllegalStateException {
//            final BoxLoader dialog = new BoxLoader(mContext).show();
//            MyApplication.getAPIService().updateQuantity(PrefUtils.getToken(mContext), new UpdateItemQuantityRequestBody(new UpdateItemQuantityRequestBody.UserItem(userItems.get(position).getId(), quantity)))
//                    .enqueue(new Callback<UpdateItemConfigResponse>() {
//                        @Override
//                        public void onResponse(Call<UpdateItemConfigResponse> call, Response<UpdateItemConfigResponse> response) {
//                            dialog.dismiss();
//                            if (response.body() != null) {
//                                if (response.body().isSuccess()) {
//                                    int prevQuantity = userItems.get(position).getQuantity();
//                                    if (quantity >= 1) {
//                                        RealmList<ItemConfig> itemConfigs = userItems.get(position).getBoxItem().getItemConfigs();
//                                        for (int i = 0; i < itemConfigs.size(); i++) {
//                                            itemConfigs.get(i).setPrice((int) (itemConfigs.get(i).getPrice() * ((float) quantity / (float) prevQuantity)));
//                                        }
//                                        BoxItem boxItem = userItems.get(position).getBoxItem();
//                                        boxItem.setItemConfigs(itemConfigs);
//                                        userItems.get(position).setQuantity(quantity);
//                                        userItems.get(position).setBoxItem(boxItem);
//                                        CartHelper.addOrUpdateUserItem(response.body().getUserItem());
//                                        notifyItemChanged(getAdapterPosition());
//                                    } else {
//                                        CartHelper.removeUserItem(userItems.get(position).getId());
//                                        userItems.remove(position);
//                                        notifyItemRemoved(getAdapterPosition());
//                                    }
//                                    Toast.makeText(mContext, response.body().getInfo(), Toast.LENGTH_SHORT).show();
//                                } else {
//                                    Toast.makeText(mContext, response.body().getInfo(), Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<UpdateItemConfigResponse> call, Throwable t) {
//                            dialog.dismiss();
//                        }
//                    });
//        }
//
//        private void changeConfig(final int position, final int itemConfigId) {
//            final BoxLoader dialog = new BoxLoader(mContext).show();
//            MyApplication.getAPIService().updateItemConfig(PrefUtils.getToken(mContext), new UpdateItemConfigurationRequest
//                    (new UpdateItemConfigurationRequest.UserItem(userItems.get(position).getId()), new UpdateItemConfigurationRequest.ItemConfig(itemConfigId)))
//                    .enqueue(new Callback<UpdateItemConfigResponse>() {
//                        @Override
//                        public void onResponse(Call<UpdateItemConfigResponse> call, Response<UpdateItemConfigResponse> response) {
//                            dialog.dismiss();
//                            if (response.body() != null) {
//                                userItems.set(position, response.body().getUserItem());
//                                notifyItemChanged(getAdapterPosition());
//                                if (response.body().getUserItem().getNextDeliveryScheduledAt() == null
//                                        || response.body().getUserItem().getNextDeliveryScheduledAt().isEmpty()) {
//                                    CartHelper.addOrUpdateUserItem(response.body().getUserItem());
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<UpdateItemConfigResponse> call, Throwable t) {
//                            dialog.dismiss();
//                        }
//                    });
//        }
//
//        private void openCancelDialog(final UserItem userItem, final int positionInArrayList) {
//            MaterialDialog dialog = new MaterialDialog.Builder(mContext).
//                    title("Cancel Subscription").
//                    customView(R.layout.layout_cancel_subscription, true).
//                    positiveText("Cancel Subscription").onPositive(new MaterialDialog.SingleButtonCallback() {
//                @Override
//                public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
//                    View customView = dialog.getCustomView();
//                    RadioGroup radioGroup = (RadioGroup) customView.findViewById(R.id.radio_group);
//                    int radioButtonID = radioGroup.getCheckedRadioButtonId();
//                    View radioButton = radioGroup.findViewById(radioButtonID);
//                    int idx = radioGroup.indexOfChild(radioButton);
//                    RadioButton r = (RadioButton) radioGroup.getChildAt(idx);
//                    if (r == null) {
//                        Toast.makeText(mContext, "Select one of the given reasons", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                    String selectedtext = r.getText().toString();
//
//                    final BoxLoader loader = new BoxLoader(mContext).show();
//                    MyApplication.getAPIService().cancelSubscription(PrefUtils.getToken(mContext)
//                            , new CancelSubscriptionRequest(userItem.getId(), selectedtext))
//                            .enqueue(new Callback<CancelSubscriptionResponse>() {
//                                @Override
//                                public void onResponse(Call<CancelSubscriptionResponse> call, Response<CancelSubscriptionResponse> response) {
//                                    loader.dismiss();
//                                    if (response.body() != null) {
//                                        Toast.makeText(mContext, response.body().getInfo(), Toast.LENGTH_SHORT).show();
//                                        if (response.body().isSuccess()) {
//                                            userItems.remove(positionInArrayList);
//                                            notifyItemRemoved(getAdapterPosition());
//                                            dialog.dismiss();
//                                        }
//                                    }
//                                }
//
//                                @Override
//                                public void onFailure(Call<CancelSubscriptionResponse> call, Throwable t) {
//                                    loader.dismiss();
//                                }
//                            });
//                }
//            }).autoDismiss(false).build();
//
//
//            dialog.getWindow().getAttributes().windowAnimations = R.style.MyAnimation_Window;
//            dialog.show();
//        }
//    }
//}
//>>>>>>> adding_buttons_to_My_Boxes
