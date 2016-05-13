package one.thebox.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.cocosw.bottomsheet.BottomSheet;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;
import java.util.Calendar;

import io.realm.RealmList;
import one.thebox.android.Events.ItemAddEvent;
import one.thebox.android.Models.BoxItem;
import one.thebox.android.Models.Category;
import one.thebox.android.Models.ItemConfig;
import one.thebox.android.Models.UserItem;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.DelayDeliveryBottomSheet;
import one.thebox.android.api.ApiResponse;
import one.thebox.android.api.RequestBodies.AddToMyBoxRequestBody;
import one.thebox.android.api.RequestBodies.UpdateItemConfigurationRequest;
import one.thebox.android.api.RequestBodies.UpdateItemQuantityRequestBody;
import one.thebox.android.api.Responses.AddToMyBoxResponse;
import one.thebox.android.api.Responses.UpdateItemConfigResponse;
import one.thebox.android.app.MyApplication;
import one.thebox.android.fragment.SizeAndFrequencyBottomSheetDialogFragment;
import one.thebox.android.util.DateTimeUtil;
import one.thebox.android.util.NumberWordConverter;
import one.thebox.android.util.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SEARCH_ITEM = 0;
    private static final int VIEW_TYPE_MY_ITEM = 1;
    private RealmList<BoxItem> boxItems = new RealmList<>();
    private RealmList<UserItem> userItems = new RealmList<>();
    private Context mContext;
    private boolean isSearchDetailItemFragment;

    public SearchDetailAdapter(Context context) {
        this.mContext = context;
    }

    public boolean isSearchDetailItemFragment() {
        return isSearchDetailItemFragment;
    }

    public void setSearchDetailItemFragment(boolean searchDetailItemFragment) {
        isSearchDetailItemFragment = searchDetailItemFragment;
    }

    public void setBoxItems(RealmList<BoxItem> boxItems, RealmList<UserItem> userItems) {
        this.boxItems = boxItems;
        this.userItems = userItems;
    }

    public void addItemToBox(final int position) {
        final MaterialDialog dialog = new MaterialDialog.Builder(mContext).progressIndeterminateStyle(true).progress(true, 0).show();
        MyApplication.getAPIService().addToMyBox(PrefUtils.getToken(mContext),
                new AddToMyBoxRequestBody(
                        new AddToMyBoxRequestBody.Item(boxItems.get(position).getId()),
                        new AddToMyBoxRequestBody.ItemConfig(boxItems.get(position).getSelectedItemConfig().getId())))
                .enqueue(new Callback<AddToMyBoxResponse>() {
                    @Override
                    public void onResponse(Call<AddToMyBoxResponse> call, Response<AddToMyBoxResponse> response) {
                        dialog.dismiss();
                        if (response.body() != null) {
                            if (response.body().isSuccess()) {
                                Toast.makeText(mContext, response.body().getInfo(), Toast.LENGTH_SHORT).show();
                                boxItems.get(position).setUserItemId(response.body().getUserItem().getId());
                                boxItems.get(position).setQuantity(boxItems.get(position).getQuantity() + 1);
                                RealmList<Category> suggestedCategories = new RealmList<Category>();
                                suggestedCategories.addAll(response.body().getRestOfTheCategoriesInTheBox());
                                suggestedCategories.addAll(response.body().getRestOfTheCategoriesInOtherBox());
                                boxItems.get(position).setSuggestedCategory(suggestedCategories);
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
                                notifyItemChanged(position);
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case VIEW_TYPE_MY_ITEM: {
                View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_user_item, parent, false);
                return new MyItemViewHolder(itemView);
            }
            case VIEW_TYPE_SEARCH_ITEM: {
                View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_search_detail_items, parent, false);
                return new SearchedItemViewHolder(itemView);
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SearchedItemViewHolder) {
            bindSearchViewHolder(holder, position);
        } else {
            bindMyItemViewHolder(holder, position - boxItems.size() + 1);
        }
    }

    private void bindMyItemViewHolder(final RecyclerView.ViewHolder holder, int position) {
        MyItemViewHolder itemViewHolder = (MyItemViewHolder) holder;
        userItems.get(position).getBoxItem().setSelectedItemConfig(
                userItems.get(position).getBoxItem().getItemConfigById(userItems.get(position).getSelectedConfigId()
                ));
        itemViewHolder.setViews(userItems.get(position));
    }

    private void bindSearchViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final SearchedItemViewHolder searchedItemViewHolder = (SearchedItemViewHolder) holder;
        if (boxItems.get(position).getSelectedItemConfig() == null) {
            boxItems.get(position).setSelectedItemConfig(boxItems.get(position).getItemConfigById(0));
        }
        searchedItemViewHolder.setViews(boxItems.get(position), position, false);
        searchedItemViewHolder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (boxItems.get(position).getUserItemId() == 0) {
                    addItemToBox(position);
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
                final SizeAndFrequencyBottomSheetDialogFragment dialogFragment = SizeAndFrequencyBottomSheetDialogFragment.newInstance(boxItems.get(position));
                dialogFragment.show(((AppCompatActivity) mContext).getSupportFragmentManager()
                        , SizeAndFrequencyBottomSheetDialogFragment.TAG);
                dialogFragment.attachListener(new SizeAndFrequencyBottomSheetDialogFragment.OnSizeAndFrequencySelected() {
                    @Override
                    public void onSizeAndFrequencySelected(ItemConfig selectedItemConfig) {
                        dialogFragment.dismiss();
                        if (boxItems.get(position).getUserItemId() == 0) {
                            boxItems.get(position).setSelectedItemConfig(selectedItemConfig);
                            searchedItemViewHolder.setViews(boxItems.get(position), position, true);
                        } else {
                            changeConfig(position, boxItems.get(position).getSelectedItemConfig().getId());
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return boxItems.size() + userItems.size() - 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position < boxItems.size() ? VIEW_TYPE_SEARCH_ITEM : VIEW_TYPE_MY_ITEM;
    }

    public class SearchedItemViewHolder extends RecyclerView.ViewHolder {

        private RecyclerView recyclerViewSavings;
        private RecyclerView recyclerViewFrequency;
        private MyBoxRecyclerAdapter.RemainingCategoryAdapter remainingCategoryAdapter;
        private TextView addButton, subtractButton;
        private TextView changeButton, noOfItemSelected;
        private LinearLayout savingHolder, savingAmountHolder;
        private TextView productName, productBrand, size, savings;
        private ImageView productImage;
        private FrequencyAndPriceAdapter frequencyAndPriceAdapter;

        public SearchedItemViewHolder(View itemView) {
            super(itemView);
            recyclerViewSavings = (RecyclerView) itemView.findViewById(R.id.relatedCategories);
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

        private void setupRecyclerViewFrequency(final BoxItem boxItem, final int position, boolean shouldScrollToPosition) {
            // hash map of frequency and corresponding PriceSizeAndSizeUnit ArrayList.
            RealmList<ItemConfig> itemConfigs = boxItem.getItemConfigsBySelectedItemConfig();
            int selectedPosition = 0;
            for (int i = 0; i < itemConfigs.size(); i++) {
                if (boxItem.getSelectedItemConfig().equals(itemConfigs.get(i))) {
                    selectedPosition = i;
                    break;
                }
            }

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
            if (!shouldScrollToPosition) {
                linearLayoutManager.scrollToPositionWithOffset(0, -boxItems.get(position).getHorizontalOffsetOfRecyclerView());
            }
            recyclerViewFrequency.setLayoutManager(linearLayoutManager);
            frequencyAndPriceAdapter = new FrequencyAndPriceAdapter(mContext, selectedPosition, new FrequencyAndPriceAdapter.OnItemConfigChange() {
                @Override
                public void onItemConfigItemChange(ItemConfig selectedItemConfig) {
                    boxItems.get(position).setSelectedItemConfig(selectedItemConfig);
                    if (boxItems.get(position).getUserItemId() != 0) {
                        changeConfig(position, selectedItemConfig.getId());
                    }
                }
            });
            frequencyAndPriceAdapter.setItemConfigs(itemConfigs);
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
            remainingCategoryAdapter = new MyBoxRecyclerAdapter.RemainingCategoryAdapter(mContext, suggestedCategories);
            remainingCategoryAdapter.setSearchDetailItemFragment(true);
            recyclerViewSavings.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            recyclerViewSavings.setAdapter(remainingCategoryAdapter);
        }

        public void setViews(BoxItem boxItem, int position, boolean shouldScrollToPosition) {
            Picasso.with(mContext).load(boxItem.getPhotoUrl()).into(productImage);
            setupRecyclerViewFrequency(boxItem, position, shouldScrollToPosition);
            noOfItemSelected.setText(String.valueOf(boxItem.getQuantity()));
            if (boxItem.getQuantity() > 0) {
                setupRecyclerViewSuggestedCategories(boxItem.getSuggestedCategory());
                savingHolder.setVisibility(View.VISIBLE);
            } else {
                savingHolder.setVisibility(View.GONE);
            }
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

    }

    public class MyItemViewHolder extends RecyclerView.ViewHolder {
        private TextView adjustButton, productName, brand, deliveryTime,
                arrivingTime, config, savings, addButton, subtractButton, noOfItemSelected;
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
            addButton = (TextView) itemView.findViewById(R.id.button_add);
            subtractButton = (TextView) itemView.findViewById(R.id.button_subtract);
            noOfItemSelected = (TextView) itemView.findViewById(R.id.no_of_item_selected);
        }

        public void setViews(final UserItem userItem) {
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (userItem.getQuantity() == 0) {
                        addItemToBox(getAdapterPosition());
                    } else {
                        updateQuantity(getAdapterPosition(), userItems.get(getAdapterPosition()).getQuantity() + 1);
                    }
                }
            });
            subtractButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (userItems.get(getAdapterPosition()).getQuantity() > 0) {
                        updateQuantity(getAdapterPosition(), userItems.get(getAdapterPosition()).getQuantity() - 1);
                    } else {
                        Toast.makeText(mContext, "Item count could not be negative", Toast.LENGTH_SHORT).show();
                    }

                }
            });
            noOfItemSelected.setText(String.valueOf(userItem.getQuantity()));
            ItemConfig itemConfig = userItem.getBoxItem().getSelectedItemConfig();
            adjustButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new BottomSheet.Builder((Activity) mContext).sheet(R.menu.menu_adjust).listener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case R.id.change_size: {
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
                               /* case R.id.change_quantity: {
                                    break;
                                }*/
                                case R.id.change_frequency: {
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
                               /* case R.id.swap_with_similar_product: {
                                    openSwipeBottomSheet();
                                    break;
                                }*/
                                case R.id.delay_delivery: {
                                    new DelayDeliveryBottomSheet((Activity) mContext, new DelayDeliveryBottomSheet.OnDelayActionCompleted() {
                                        @Override
                                        public void onDelayActionCompleted(UserItem userItem) {
                                            if (userItem == null) {
                                                userItems.remove(getAdapterPosition());
                                                notifyItemRemoved(getAdapterPosition());
                                            } else {
                                                userItems.set(getAdapterPosition(), userItem);
                                                notifyItemChanged(getAdapterPosition());
                                            }
                                        }
                                    }).show(userItem);
                                    break;
                                }
                            }
                        }

                    }).show();
                }
            });


            productName.setText(userItem.getBoxItem().getTitle());
            config.setText(NumberWordConverter.convert(userItem.getQuantity()) + " " +
                    itemConfig.getSize() + itemConfig.getSizeUnit() + ", " + itemConfig.getPrice() + " Rs " + itemConfig.getSubscriptionType());
            brand.setText(userItem.getBoxItem().getBrand());
            savings.setText(userItem.getBoxItem().getSavings() + " Rs saved per month");
            if (userItem.getNextDeliveryScheduledAt() == null || userItem.getNextDeliveryScheduledAt().isEmpty()) {
                deliveryTime.setText("Delivered to you on frequency of every " + itemConfig.getSubscriptionType());
                arrivingTime.setText("Item has added to your cart");
            } else {
                deliveryTime.setText("Delivered to you on frequency of every " + itemConfig.getSubscriptionType());
                try {
                    arrivingTime.setText("Arriving in " + DateTimeUtil.getDifferenceAsDay(Calendar.getInstance().getTime(), DateTimeUtil.convertStringToDate(userItem.getNextDeliveryScheduledAt())) + " days");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            Picasso.with(mContext).load(userItem.getBoxItem().getPhotoUrl()).into(productImageView);
        }

        public void addItemToBox(final int position) {
            final MaterialDialog dialog = new MaterialDialog.Builder(mContext).progressIndeterminateStyle(true).progress(true, 0).show();
            MyApplication.getAPIService().addToMyBox(PrefUtils.getToken(mContext),
                    new AddToMyBoxRequestBody(
                            new AddToMyBoxRequestBody.Item(userItems.get(position).getId()),
                            new AddToMyBoxRequestBody.ItemConfig(userItems.get(position).getBoxItem().getSelectedItemConfig().getId())))
                    .enqueue(new Callback<AddToMyBoxResponse>() {
                        @Override
                        public void onResponse(Call<AddToMyBoxResponse> call, Response<AddToMyBoxResponse> response) {
                            dialog.dismiss();
                            if (response.body() != null) {
                                if (response.body().isSuccess()) {
                                    Toast.makeText(mContext, response.body().getInfo(), Toast.LENGTH_SHORT).show();
                                    notifyItemChanged(position);
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
            MyApplication.getAPIService().updateQuantity(PrefUtils.getToken(mContext), new UpdateItemQuantityRequestBody(new UpdateItemQuantityRequestBody.UserItem(userItems.get(position).getId(), quantity)))
                    .enqueue(new Callback<UpdateItemConfigResponse>() {
                        @Override
                        public void onResponse(Call<UpdateItemConfigResponse> call, Response<UpdateItemConfigResponse> response) {
                            dialog.cancel();
                            if (response.body() != null) {
                                if (response.body().isSuccess()) {
                                    userItems.get(position).setQuantity(quantity);
                                    notifyItemChanged(getAdapterPosition());
                                    int count = 0;
                                    for (int i = 0; i < userItems.size(); i++) {
                                        if (userItems.get(i).getQuantity() > 0) {
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
    }
}
