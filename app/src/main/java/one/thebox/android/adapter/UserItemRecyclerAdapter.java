package one.thebox.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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
import one.thebox.android.util.DisplayUtil;
import one.thebox.android.util.NumberWordConverter;
import one.thebox.android.util.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserItemRecyclerAdapter extends BaseRecyclerAdapter {

    private RealmList<UserItem> userItems;
    private boolean isUpComingOrderAdapter;

    public UserItemRecyclerAdapter(Context context, RealmList<UserItem> userItems, boolean isUpComingOrderAdapter) {
        super(context);
        this.userItems = userItems;
        this.isUpComingOrderAdapter = isUpComingOrderAdapter;
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
        return new ItemHeaderHolder(view);
    }

    @Override
    protected FooterHolder getFooterHolder(View view) {
        return null;
    }

    @Override
    public void onBindViewItemHolder(final ItemHolder holder, final int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        userItems.get(position).getBoxItem().setSelectedItemConfig(
                userItems.get(position).getBoxItem().getItemConfigById(userItems.get(position).getSelectedConfigId()
                ));
        itemViewHolder.setViews(userItems.get(position));
    }

    @Override
    public void onBindViewHeaderHolder(HeaderHolder holder, int position) {

    }

    @Override
    public void onBindViewFooterHolder(FooterHolder holder, int position) {

    }

    @Override
    public int getItemsCount() {
        return userItems.size();
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_user_item;
    }

    @Override
    protected int getHeaderLayoutId() {
        return R.layout.empty_space_header;
    }

    @Override
    protected int getItemLayoutId(int position) {
        return 0;
    }

    @Override
    protected int getFooterLayoutId() {
        return 0;
    }

    public void changeConfig(final int position, final int itemConfigId) {
        final ItemConfig itemConfig = userItems.get(position).getBoxItem().getItemConfigById(
                itemConfigId
        );
        final MaterialDialog dialog = new MaterialDialog.Builder(mContext).progressIndeterminateStyle(true).progress(true, 0).show();
        MyApplication.getAPIService().updateItemConfig(PrefUtils.getToken(mContext), new UpdateItemConfigurationRequest
                (new UpdateItemConfigurationRequest.UserItem(userItems.get(position).getId()), new UpdateItemConfigurationRequest.ItemConfig(itemConfigId)))
                .enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        dialog.cancel();
                        userItems.get(position).setSelectedConfigId(itemConfigId);
                        userItems.get(position).getBoxItem().setSelectedItemConfig(itemConfig);

                        notifyItemChanged(position);
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        dialog.cancel();
                    }
                });
    }

    public class ItemHeaderHolder extends BaseRecyclerAdapter.HeaderHolder {

        private LinearLayout linearLayout;

        public ItemHeaderHolder(View itemView) {
            super(itemView);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linear_layout);
        }

        public void setHeight(int heightInDp) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
            layoutParams.height = DisplayUtil.dpToPx(mContext, heightInDp);
            linearLayout.setLayoutParams(layoutParams);

        }
    }

    public class ItemViewHolder extends BaseRecyclerAdapter.ItemHolder {

        private TextView adjustButton, productName, brand, deliveryTime,
                arrivingTime, config, savings, addButton, subtractButton, noOfItemSelected;
        private ImageView productImageView;
        private LinearLayout quantityHolder;

        public ItemViewHolder(View itemView) {
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
            quantityHolder = (LinearLayout) itemView.findViewById(R.id.layout_quantity_holder);
        }

        public void setViews(final UserItem userItem) {
            if (isUpComingOrderAdapter) {
                quantityHolder.setVisibility(View.GONE);
                adjustButton.setVisibility(View.GONE);
            } else {
                quantityHolder.setVisibility(View.VISIBLE);
                adjustButton.setVisibility(View.VISIBLE);
            }
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
            ItemConfig itemConfig = userItem.getBoxItem().getItemConfigById(userItem.getSelectedConfigId());
            //userItem.getBoxItem().getSelectedItemConfig();
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
                                    break;
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
                                    break;
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
            Picasso.with(mContext).load(itemConfig.getPhotoUrl()).into(productImageView);
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
                                    int prevQuantity = userItems.get(position).getQuantity();
                                    userItems.get(position).setQuantity(quantity);
                                    if (quantity >= 1) {
                                        RealmList<ItemConfig> itemConfigs = userItems.get(position).getBoxItem().getItemConfigs();
                                        for (int i = 0; i < itemConfigs.size(); i++) {
                                            itemConfigs.get(i).setPrice((int) (itemConfigs.get(i).getPrice() * ((float) quantity / (float) prevQuantity)));
                                        }
                                        BoxItem boxItem = userItems.get(position).getBoxItem();
                                        boxItem.setItemConfigs(itemConfigs);
                                        userItems.get(position).setBoxItem(boxItem);
                                    }
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