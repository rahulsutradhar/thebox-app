package one.thebox.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.cocosw.bottomsheet.BottomSheet;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;

import one.thebox.android.Events.ItemAddEvent;
import one.thebox.android.Models.BoxItem;
import one.thebox.android.Models.Category;
import one.thebox.android.Models.DeliverySlot;
import one.thebox.android.Models.UserItem;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.ChangeSizeDialogViewHelper;
import one.thebox.android.api.ApiResponse;
import one.thebox.android.api.RequestBodies.AddToMyBoxRequestBody;
import one.thebox.android.api.RequestBodies.UpdateItemConfigurationRequest;
import one.thebox.android.api.RequestBodies.UpdateItemQuantityRequestBody;
import one.thebox.android.api.Responses.AddToMyBoxResponse;
import one.thebox.android.api.Responses.CategoryBoxItemsResponse;
import one.thebox.android.api.Responses.UpdateItemConfigResponse;
import one.thebox.android.app.MyApplication;
import one.thebox.android.util.NumberWordConverter;
import one.thebox.android.util.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchDetailAdapter extends BaseRecyclerAdapter {

    private ArrayList<BoxItem> boxItems = new ArrayList<>();
    private ArrayList<BoxItem> tempBoxItems = new ArrayList<>();
    private ArrayList<Category> categories = new ArrayList<>();
    private int currentSelectedHeaderPosition = 0;
    private ArrayList<UserItem> userItems = new ArrayList<>();
    private ArrayList<UserItem> tempUserItems = new ArrayList<>();

    public SearchDetailAdapter(Context context) {
        super(context);
        mViewType = RECYCLER_VIEW_TYPE_HEADER;
        setManyItemViewTypeAdapter(true);
    }

    public void addBoxItem(BoxItem boxItem) {
        boxItems.add(boxItem);
    }

    public ArrayList<BoxItem> getBoxItems() {
        return boxItems;
    }

    public void setBoxItems(ArrayList<BoxItem> boxItems, ArrayList<UserItem> userItems, ArrayList<Category> categories) {
        this.boxItems = boxItems;
        this.categories = categories;
        this.userItems = userItems;
        this.tempUserItems.addAll(userItems);
        this.tempBoxItems.addAll(boxItems);
    }

    @Override
    protected ItemHolder getItemHolder(View view) {
        return null;
    }

    @Override
    protected ItemHolder getItemHolder(View view, int position) {
        Log.d("Test ItemHolder", String.valueOf(adapterCurrentPosition));
        if (adapterCurrentPosition < boxItems.size()) {
            return new SearchedItemViewHolder(view);
        } else {
            return new MyItemViewHolder(view);
        }
    }

    @Override
    protected HeaderHolder getHeaderHolder(View view) {
        return new ItemViewHeaderHolder(view);
    }

    @Override
    protected FooterHolder getFooterHolder(View view) {
        return null;
    }

    @Override
    public void onBindViewItemHolder(ItemHolder holder, int position) {
        Log.d("Test BindView", String.valueOf(adapterCurrentPosition));
        if (adapterCurrentPosition < boxItems.size()) {
            if (holder instanceof MyItemViewHolder) {
                return;
            }
            bindSearchViewHolder(holder, position);
        } else {
            if (holder instanceof SearchedItemViewHolder) {
                return;
            }
            bindMyItemViewHolder(holder, position - boxItems.size());
        }
    }

    private void bindMyItemViewHolder(ItemHolder holder, int position) {
        MyItemViewHolder itemViewHolder = (MyItemViewHolder) holder;
        itemViewHolder.setViews(userItems.get(position));
    }

    private void bindSearchViewHolder(final ItemHolder holder, final int position) {
        SearchedItemViewHolder searchedItemViewHolder = (SearchedItemViewHolder) holder;
        if (boxItems.get(position).getSelectedItemConfig() == null) {
            boxItems.get(position).setSelectedItemConfig(boxItems.get(position).getItemConfigById(0));
        }
        searchedItemViewHolder.setViews(boxItems.get(position), position);
        searchedItemViewHolder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (boxItems.get(position).getUserItemId() == 0) {
                    addItemToBox(holder.getAdapterPosition());
                } else {
                    updateQuantity(position, boxItems.get(position).getQuantity() + 1);
                }
            }
        });
        searchedItemViewHolder.subtractButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (boxItems.get(position).getQuantity() > 0) {
                    updateQuantity(position, boxItems.get(position).getQuantity() - 1);
                } else {
                    Toast.makeText(mContext, "Item count could not be negative", Toast.LENGTH_SHORT).show();
                }

            }
        });
        searchedItemViewHolder.changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ChangeSizeDialogViewHelper(mContext, new ChangeSizeDialogViewHelper.OnSizeAndFrequencySelected() {
                    @Override
                    public void onSizeAndFrequencySelected(BoxItem.ItemConfig itemConfig) {
                        if (boxItems.get(position).getUserItemId() == 0) {
                            boxItems.get(position).setSelectedItemConfig(itemConfig);
                            notifyItemChanged(holder.getAdapterPosition());
                        } else {
                            changeConfig(position, boxItems.get(position).getSelectedItemConfig().getId());
                        }
                    }
                }).show(boxItems.get(position));
            }
        });
    }

    public void addItemToBox(final int position) {
        final MaterialDialog dialog = new MaterialDialog.Builder(mContext).progressIndeterminateStyle(true).progress(true, 0).show();
        MyApplication.getAPIService().addToMyBox(PrefUtils.getToken(mContext),
                new AddToMyBoxRequestBody(
                        new AddToMyBoxRequestBody.Item(boxItems.get(position - 1).getId()),
                        new AddToMyBoxRequestBody.ItemConfig(boxItems.get(position - 1).getSelectedItemConfig().getId())))
                .enqueue(new Callback<AddToMyBoxResponse>() {
                    @Override
                    public void onResponse(Call<AddToMyBoxResponse> call, Response<AddToMyBoxResponse> response) {
                        dialog.dismiss();
                        if (response.body() != null) {
                            if (response.body().isSuccess()) {
                                Toast.makeText(mContext, response.body().getInfo(), Toast.LENGTH_SHORT).show();
                                boxItems.get(position - 1).setUserItemId(response.body().getUserItem().getId());
                                boxItems.get(position - 1).setQuantity(boxItems.get(position - 1).getQuantity() + 1);
                                // CustomToast.show(mContext, "Total Savings: 300 Rs per month");
                                notifyItemChanged(position);
                                int count = 0;
                                for (int i = 0; i < boxItems.size(); i++) {
                                    if (boxItems.get(i).getQuantity() > 0) {
                                        count++;
                                    }
                                    EventBus.getDefault().post(new ItemAddEvent(count));
                                }
                            } else {
                                Toast.makeText(mContext, response.body().getInfo(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<AddToMyBoxResponse> call, Throwable t) {
                        dialog.dismiss();
                    }
                });

    }

    public void updateQuantity(final int position, final int quantity) {
        final MaterialDialog dialog = new MaterialDialog.Builder(mContext).progressIndeterminateStyle(true).progress(true, 0).show();
        MyApplication.getAPIService().updateQuantity(PrefUtils.getToken(mContext), new UpdateItemQuantityRequestBody(new UpdateItemQuantityRequestBody.UserItem(boxItems.get(position).getUserItemId(), quantity)))
                .enqueue(new Callback<UpdateItemConfigResponse>() {
                    @Override
                    public void onResponse(Call<UpdateItemConfigResponse> call, Response<UpdateItemConfigResponse> response) {
                        dialog.cancel();
                        if (response.body() != null) {
                            if (response.body().isSuccess()) {
                                boxItems.get(position).setQuantity(quantity);
                                notifyItemChanged(position + 1);
                                int count = 0;
                                for (int i = 0; i < boxItems.size(); i++) {
                                    if (boxItems.get(i).getQuantity() > 0) {
                                        count++;
                                    }
                                }
                                EventBus.getDefault().post(new ItemAddEvent(count));
                                Toast.makeText(mContext, response.body().getInfo(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mContext, response.body().getInfo(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UpdateItemConfigResponse> call, Throwable t) {
                        dialog.cancel();
                    }
                });
    }

    public void changeConfig(final int position, final int itemConfigId) {
        final MaterialDialog dialog = new MaterialDialog.Builder(mContext).progressIndeterminateStyle(true).progress(true, 0).show();
        MyApplication.getAPIService().updateItemConfig(PrefUtils.getToken(mContext), new UpdateItemConfigurationRequest
                (new UpdateItemConfigurationRequest.UserItem(boxItems.get(position).getUserItemId()), new UpdateItemConfigurationRequest.ItemConfig(itemConfigId)))
                .enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        dialog.cancel();
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        dialog.cancel();
                    }
                });
    }

    @Override
    public void onBindViewHeaderHolder(HeaderHolder holder, int position) {
        ItemViewHeaderHolder itemViewHeaderHolder = (ItemViewHeaderHolder) holder;
        itemViewHeaderHolder.setViewHolder();
    }

    @Override
    public void onBindViewFooterHolder(FooterHolder holder, int position) {

    }

    @Override
    public int getItemsCount() {
        return boxItems.size() + userItems.size();
    }

    @Override
    protected int getItemLayoutId() {
        return 0;
    }

    @Override
    protected int getHeaderLayoutId() {
        return R.layout.header_search_detail;
    }

    @Override
    protected int getItemLayoutId(int position) {
        if (position < boxItems.size()) {
            return R.layout.item_search_result_my_items;
        } else {
            return R.layout.item_expanded_list;
        }
    }

    @Override
    protected int getFooterLayoutId() {
        return 0;
    }

    public class ItemViewHeaderHolder extends HeaderHolder {

        private RecyclerView recyclerView;
        private SearchCategoryAdapter searchCategoryAdapter;


        public ItemViewHeaderHolder(View itemView) {
            super(itemView);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.recycler_view);
        }

        public void setViewHolder() {
            searchCategoryAdapter = new SearchCategoryAdapter(mContext, currentSelectedHeaderPosition, new SearchCategoryAdapter.OnHeaderCategoryChange() {
                @Override
                public void onHeaderCategoryChange(Category category, final int positionSelected) {
                    currentSelectedHeaderPosition = positionSelected;
                    if (currentSelectedHeaderPosition == 0) {
                        boxItems.clear();
                        userItems.clear();
                        boxItems.addAll(tempBoxItems);
                        userItems.addAll(tempUserItems);
                        notifyDataSetChanged();

                    } else {
                        final MaterialDialog dialog = new MaterialDialog.Builder(mContext).progressIndeterminateStyle(true).progress(true, 0).show();
                        MyApplication.getAPIService().getCategoryBoxItems(PrefUtils.getToken(mContext),
                                category.getId())
                                .enqueue(new Callback<CategoryBoxItemsResponse>() {
                                    @Override
                                    public void onResponse(Call<CategoryBoxItemsResponse> call, Response<CategoryBoxItemsResponse> response) {
                                        dialog.dismiss();
                                        if (response.body() != null) {
                                            boxItems.clear();
                                            userItems.clear();
                                            boxItems.addAll(new ArrayList<BoxItem>(response.body().getNormalBoxItems()));
                                            userItems.addAll(new ArrayList<UserItem>(response.body().getMyBoxItems()));
                                            notifyDataSetChanged();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<CategoryBoxItemsResponse> call, Throwable t) {
                                        dialog.dismiss();
                                    }
                                });
                    }

                }
            });
            searchCategoryAdapter.setCategories(categories);
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            recyclerView.setAdapter(searchCategoryAdapter);
        }
    }

    public class SearchedItemViewHolder extends ItemHolder {

        private RecyclerView recyclerViewSavings;
        private RecyclerView recyclerViewFrequency;
        private MyBoxRecyclerAdapter.RemainingCategoryAdapter remainingCategoryAdapter;
        //  private ArrayList<Box.SmartItem> smartItems = new ArrayList<>();
        private TextView addButton, subtractButton;
        private TextView changeButton, noOfItemSelected;
        private LinearLayout savingHolder, savingAmountHolder;
        private TextView productName, productBrand, size, savings;
        private ImageView productImage;
        private FrequencyAndPriceAdapter frequencyAndPriceAdapter;


        public SearchedItemViewHolder(View itemView) {
            super(itemView);
            recyclerViewSavings = (RecyclerView) itemView.findViewById(R.id.smart_item_recycler_view);
            addButton = (TextView) itemView.findViewById(R.id.button_add);
            subtractButton = (TextView) itemView.findViewById(R.id.button_subtract);
            changeButton = (TextView) itemView.findViewById(R.id.button_change);
            noOfItemSelected = (TextView) itemView.findViewById(R.id.no_of_item_selected);
            savingHolder = (LinearLayout) itemView.findViewById(R.id.saving_holder);
            recyclerViewFrequency = (RecyclerView) itemView.findViewById(R.id.recycler_view_frequency);
            productName = (TextView) itemView.findViewById(R.id.product_name);
            productBrand = (TextView) itemView.findViewById(R.id.product_brand);
            size = (TextView) itemView.findViewById(R.id.text_view_size);
            productImage = (ImageView) itemView.findViewById(R.id.product_image);
            savings = (TextView) itemView.findViewById(R.id.text_view_savings);
            savingAmountHolder = (LinearLayout) itemView.findViewById(R.id.holder_saving_amount);
        }

        private void setupRecyclerViewFrequency(BoxItem boxItem, final int position) {
            // hash map of frequency and corresponding PriceSizeAndSizeUnit ArrayList.
            ArrayList<BoxItem.ItemConfig> itemConfigs = boxItem.getItemConfigsBySelectedItemConfig();
            int currentSelection = 0;
            for (int i = 0; i < itemConfigs.size(); i++) {
                if (boxItem.getSelectedItemConfig() == itemConfigs.get(i)) {
                    currentSelection = i;
                    break;
                }
            }
            recyclerViewFrequency.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            frequencyAndPriceAdapter = new FrequencyAndPriceAdapter(mContext, currentSelection, new FrequencyAndPriceAdapter.OnItemConfigChange() {
                @Override
                public void onItemConfigItemChange(BoxItem.ItemConfig selectedItemConfig) {
                    boxItems.get(position).setSelectedItemConfig(selectedItemConfig);
                }
            });
            frequencyAndPriceAdapter.setItemConfigs(itemConfigs);
            recyclerViewFrequency.setAdapter(frequencyAndPriceAdapter);
        }

        public void setViews(BoxItem boxItem, int position) {
            Picasso.with(mContext).load(boxItem.getPhotoUrl()).into(productImage);
            //setupRecyclerViewSavings();
            setupRecyclerViewFrequency(boxItem, position);
            noOfItemSelected.setText(String.valueOf(boxItem.getQuantity()));
           /* if (boxItem.getQuantity() > 0) {
                savingHolder.setVisibility(View.VISIBLE);
            } else {
                savingHolder.setVisibility(View.GONE);
            }*/
            productName.setText(boxItem.getTitle());
            productBrand.setText(boxItem.getBrand());
            if (boxItem.getSavings() == 0) {
                savingAmountHolder.setVisibility(View.GONE);
            } else {
                savingAmountHolder.setVisibility(View.VISIBLE);
                savings.setText(boxItem.getSavings() + " Rs Savings");
            }
            if (boxItem.getItemConfigs() != null && !boxItem.getItemConfigs().isEmpty()) {
                size.setText(boxItem.getItemConfigs().get(0).getSize() + " " + boxItem.getItemConfigs().get(0).getSizeUnit());
            }
        }

        private void setupRecyclerViewSavings() {
          /*  for (int i = 0; i < 6; i++) {
                smartItems.add(new Box.SmartItem());
            }
            remainingCategoryAdapter = new MyBoxRecyclerAdapter.RemainingCategoryAdapter(mContext, smartItems);
            recyclerViewSavings.setLayoutManager(new GridLayoutManager(mContext, 3));
            recyclerViewSavings.setAdapter(remainingCategoryAdapter);*/
        }
    }

    public class MyItemViewHolder extends ItemHolder {
        private TextView adjustButton, productName, brand, deliveryTime, arrivingTime, config, savings;
        private ImageView productImageView;

        public MyItemViewHolder(View itemView) {
            super(itemView);
            adjustButton = (TextView) itemView.findViewById(R.id.adjust);
            productName = (TextView) itemView.findViewById(R.id.product_name);
            brand = (TextView) itemView.findViewById(R.id.product_brand);
            deliveryTime = (TextView) itemView.findViewById(R.id.delivery_time);
            arrivingTime = (TextView) itemView.findViewById(R.id.arriving_time);
            config = (TextView) itemView.findViewById(R.id.config);
            savings = (TextView) itemView.findViewById(R.id.savings);
            productImageView = (ImageView) itemView.findViewById(R.id.product_image_view);
        }

        public void setViews(final UserItem boxItem) {
            final BoxItem.ItemConfig itemConfig = boxItem.getBoxItem().getItemConfigById(boxItem.getSelectedConfigId());
            adjustButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new BottomSheet.Builder((Activity) mContext).sheet(R.menu.menu_adjust).listener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case R.id.change_size: {

                                    new ChangeSizeDialogViewHelper(mContext, new ChangeSizeDialogViewHelper.OnSizeAndFrequencySelected() {
                                        @Override
                                        public void onSizeAndFrequencySelected(BoxItem.ItemConfig selectedItemConfig) {

                                        }
                                    }).show(boxItem.getBoxItem());
                                    break;
                                }
                              /*  case R.id.change_quantity: {
                                    break;
                                }*/
                                case R.id.change_frequency: {
                                    break;
                                }
                               /* case R.id.swap_with_similar_product: {
                                    openSwipeBottomSheet();
                                    break;
                                }*/
                                case R.id.delay_delivery: {
                                    openDelayDeliveryDialog();
                                    break;
                                }
                            }
                        }

                    }).show();
                }
            });
            productName.setText(boxItem.getBoxItem().getTitle());
            config.setText(NumberWordConverter.convert(boxItem.getQuantity()) + " " +
                    itemConfig.getSize() + itemConfig.getSizeUnit() + ", " + itemConfig.getPrice() + " Rs " + itemConfig.getSubscriptionType());
            brand.setText(boxItem.getBoxItem().getBrand());
            savings.setText(boxItem.getBoxItem().getSavings() + " Rs saved per month");
            if (boxItem.getNextDeliveryScheduledAt() == null || boxItem.getNextDeliveryScheduledAt().isEmpty()) {
                deliveryTime.setText("Deliver to you on frequency of every " + itemConfig.getSubscriptionType());
                arrivingTime.setText("Item has added to your cart");
            }
            Picasso.with(mContext).load(boxItem.getBoxItem().getPhotoUrl()).into(productImageView);
        }

        private void openDelayDeliveryDialog() {
            View bottomSheet = ((Activity) mContext).getLayoutInflater().inflate(R.layout.layout_bottom_sheet, null);
            DeliverySlotsAdapter deliverySlotsAdapter = new DeliverySlotsAdapter(mContext);
            for (int i = 0; i < 10; i++)
                deliverySlotsAdapter.addDeliveryItems(new DeliverySlot());
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
            RecyclerView recyclerView = (RecyclerView) bottomSheet.findViewById(R.id.recycler_view);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            recyclerView.setAdapter(deliverySlotsAdapter);
            bottomSheetDialog.setContentView(bottomSheet);
            bottomSheetDialog.show();
        }

        private void openSwipeBottomSheet() {
           /* View bottomSheet = ((Activity) mContext).getLayoutInflater().inflate(R.layout.layout_bottom_sheet, null);
            SwapAdapter swapAdapter = new SwapAdapter(mContext);
            for (int i = 0; i < 10; i++)
                swapAdapter.addUserItem(new Box.BoxItem());
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
            RecyclerView recyclerView = (RecyclerView) bottomSheet.findViewById(R.id.recycler_view);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            recyclerView.setAdapter(swapAdapter);
            bottomSheetDialog.setContentView(bottomSheet);
            bottomSheetDialog.show();*/
        }
    }
}
