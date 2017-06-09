package one.thebox.android.holders;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;

import java.util.Calendar;

import one.thebox.android.Models.items.ItemConfig;
import one.thebox.android.Models.items.UserItem;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.Announcement;
import one.thebox.android.ViewHelper.BoxLoader;
import one.thebox.android.activity.MainActivity;
import one.thebox.android.api.RequestBodies.CancelSubscriptionRequest;
import one.thebox.android.api.RequestBodies.UpdateItemConfigurationRequest;
import one.thebox.android.api.RequestBodies.UpdateItemQuantityRequestBody;
import one.thebox.android.api.Responses.CancelSubscriptionResponse;
import one.thebox.android.api.Responses.UpdateItemConfigResponse;
import one.thebox.android.app.TheBox;
import one.thebox.android.fragment.EditItemFragment;
import one.thebox.android.fragment.SizeAndFrequencyBottomSheetDialogFragment;
import one.thebox.android.util.DateTimeUtil;
import one.thebox.android.util.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by nbansal2211 on 17/12/16.
 */

public class UserItemHolder extends BaseHolder {
    private Context mContext;

    private TextView productName, brand,
            arrivingTime, config, addButton, subtractButton, noOfItemSelected, frequency, price;
    private ImageView productImageView, edit;
    private LinearLayout quantityHolder;
    private UserItem item;

    public UserItemHolder(View itemView) {
        super(itemView);
        this.mContext = context;
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
                dialogFragment.show(((AppCompatActivity) context).getSupportFragmentManager()
                        , EditItemFragment.TAG);
                dialogFragment.attachListener(new EditItemFragment.OnEditItemoptionSelected() {
                    @Override
                    public void onEditItemoptionSelected(int actionUserItemSubscription) {

                        dialogFragment.dismiss();

                        // true if change_size was clicked
                        // false otherwise
                        switch (actionUserItemSubscription) {
                            case 1:

                                final SizeAndFrequencyBottomSheetDialogFragment dialogFragment = SizeAndFrequencyBottomSheetDialogFragment.newInstance(userItem.getBoxItem());
                                dialogFragment.show(((AppCompatActivity) context).getSupportFragmentManager()
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

                               /* new DelayDeliveryBottomSheetFragment((Activity) context, new DelayDeliveryBottomSheetFragment.OnDelayActionCompleted() {
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
                                }).show(userItem,((Activity) mContext).getSupportFragmentManager()));*/

                                break;
                            case 3:
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
                                        updateQuantity(item, getAdapterPosition(), userItem.getQuantity() + 1);
                                    }
                                }
                            })
                            .setNegativeButton(confirm_change_is_not_intentded_for_a_particular_order.getNegativeText_button_text_res_id(), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    PrefUtils.putBoolean(TheBox.getInstance(), "update_quantity_announcemnet", false);
                                    Intent intent = new Intent(mContext, MainActivity.class)
                                            .putExtra(MainActivity.EXTRA_ATTACH_FRAGMENT_NO, 11);
                                    context.startActivity(intent);
                                }
                            })
                            .show();

                } else {
                    if (userItem.getQuantity() == 0) {
                        addItemToBox(getAdapterPosition());
                    } else {
                        updateQuantity(item, getAdapterPosition(), userItem.getQuantity() + 1);
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
                                        updateQuantity(item, getAdapterPosition(), userItem.getQuantity() - 1);
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
                        updateQuantity(item, getAdapterPosition(), userItem.getQuantity() - 1);
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
                        Toast.makeText(TheBox.getInstance(), "Item count could not be negative", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
        noOfItemSelected.setText(String.valueOf(userItem.getQuantity()));
        ItemConfig itemConfig = userItem.getBoxItem().getItemConfigById(userItem.getSelectedConfigId());
        price.setText("Rs " + itemConfig.getPrice() * userItem.getQuantity());
        frequency.setText("Repeat every " + itemConfig.getSubscriptionText().toLowerCase());

        productName.setText(userItem.getBoxItem().getTitle());

        if (itemConfig.getCorrectQuantity().equals("NA")) {
            config.setText(itemConfig.getSize() + " " + itemConfig.getSizeUnit());
        } else {
            config.setText(itemConfig.getCorrectQuantity() + " x " +
                    itemConfig.getSize() + " " + itemConfig.getSizeUnit());
        }

        arrivingTime.setVisibility(View.VISIBLE);
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
                .load(itemConfig.getItemImage())
                .centerCrop()
                .crossFade()
                .into(productImageView);

//            Picasso.with(TheBox.getInstance()).load(itemConfig.getPhotoUrl()).fit().into(productImageView);
        //Picasso.with(TheBox.getInstance()).load(itemConfig.getPhotoUrl()).resize(116,116).into(productImageView);
    }

    private void addItemToBox(final int position) throws IllegalStateException {
//        final BoxLoader dialog = new BoxLoader(mContext).show();
//        TheBox.getAPIService().addToMyBox(PrefUtils.getToken(TheBox.getInstance()),
//                new AddToMyBoxRequestBody(
//                        new AddToMyBoxRequestBody.Item(userItems.get(position).getBoxItem().getId()),
//                        new AddToMyBoxRequestBody.ItemConfig(userItems.get(position).getBoxItem().getSelectedItemConfig().getId())))
//                .enqueue(new Callback<AddToMyBoxResponse>() {
//                    @Override
//                    public void onResponse(Call<AddToMyBoxResponse> call, Response<AddToMyBoxResponse> response) {
//                        dialog.dismiss();
//                        if (response.body() != null) {
//                            if (response.body().isSuccess()) {
//                                Toast.makeText(TheBox.getInstance(), response.body().getInfo(), Toast.LENGTH_SHORT).show();
//                                CartHelper.addOrUpdateUserItem(response.body().getUserItem(), null);
//                            } else {
//                                Toast.makeText(TheBox.getInstance(), response.body().getInfo(), Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<AddToMyBoxResponse> call, Throwable t) {
//                        dialog.dismiss();
//                    }
//                });

    }

    private void updateQuantity(UserItem item, final int position, final int quantity) throws IllegalStateException {
        final BoxLoader dialog = new BoxLoader(mContext).show();
        TheBox.getAPIService().updateQuantity(PrefUtils.getToken(TheBox.getInstance()), new UpdateItemQuantityRequestBody(new UpdateItemQuantityRequestBody.UserItem(item.getId(), quantity)))
                .enqueue(new Callback<UpdateItemConfigResponse>() {
                    @Override
                    public void onResponse(Call<UpdateItemConfigResponse> call, Response<UpdateItemConfigResponse> response) {
                        dialog.dismiss();
                        if (response.body() != null) {
                            if (response.body().isSuccess()) {
//                                int prevQuantity = userItems.get(position).getQuantity();
//                                if (quantity >= 1) {
////                                        userItems.get(position).setQuantity(quantity);
//                                    CartHelper.addOrUpdateUserItem(response.body().getUserItem(), null);
//                                    notifyItemChanged(getAdapterPosition());
//                                } else {
//                                    CartHelper.removeUserItem(userItems.get(position).getId(), null);
////                                        userItems.remove(position);
//                                    notifyItemRemoved(getAdapterPosition());
//                                }
//                                if (onUserItemChange != null) {
//                                    onUserItemChange.onUserItemChange(userItems);
//                                }
//                                OrderHelper.addAndNotify(response.body().getOrders());
//                                EventBus.getDefault().post(new UpdateOrderItemEvent());
//                                Toast.makeText(TheBox.getInstance(), response.body().getInfo(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(TheBox.getInstance(), response.body().getInfo(), Toast.LENGTH_SHORT).show();
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
        TheBox.getAPIService().updateItemConfig(PrefUtils.getToken(TheBox.getInstance()), new UpdateItemConfigurationRequest
                (new UpdateItemConfigurationRequest.UserItem(item.getId()), new UpdateItemConfigurationRequest.ItemConfig(itemConfigId)))
                .enqueue(new Callback<UpdateItemConfigResponse>() {
                    @Override
                    public void onResponse(Call<UpdateItemConfigResponse> call, Response<UpdateItemConfigResponse> response) {
                        dialog.dismiss();
                        if (response.body() != null) {
//                            userItems.set(position, response.body().getUserItem());
//                            if (onUserItemChange != null) {
//                                onUserItemChange.onUserItemChange(userItems);
//                            }
//                            notifyItemChanged(getAdapterPosition());
//                            if (response.body().getUserItem().getNextDeliveryScheduledAt() == null
//                                    || response.body().getUserItem().getNextDeliveryScheduledAt().isEmpty()) {
//                                CartHelper.addOrUpdateUserItem(response.body().getUserItem(), null);
//                            }
//                            OrderHelper.addAndNotify(response.body().getOrders());
//                            if (getAdapterPosition() != -1)
//                                EventBus.getDefault().post(new UpdateOrderItemEvent());
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
                                if (response.body() != null) {
//                                    Toast.makeText(TheBox.getInstance(), response.body().getInfo(), Toast.LENGTH_SHORT).show();
//                                    if (response.body().isSuccess()) {
//                                        userItems.remove(getAdapterPosition());
//                                        if (onUserItemChange != null) {
//                                            onUserItemChange.onUserItemChange(userItems);
//                                        }
//                                        notifyItemRemoved(getAdapterPosition());
//                                        dialog.dismiss();
//                                        OrderHelper.addAndNotify(response.body().getOrders());
//                                        EventBus.getDefault().post(new UpdateOrderItemEvent());
//
//                                    }
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

    @Override
    public void onBind(int position, Object obj) {
        super.onBind(position, obj);
        this.item = (UserItem) obj;
        setViews(item, position);
    }
}
