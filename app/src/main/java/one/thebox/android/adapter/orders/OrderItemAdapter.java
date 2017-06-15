package one.thebox.android.adapter.orders;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.RequestManager;

import java.util.ArrayList;

import one.thebox.android.Models.items.ItemConfig;
import one.thebox.android.Models.order.OrderItem;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.BoxLoader;
import one.thebox.android.activity.OrderItemsActivity;
import one.thebox.android.adapter.base.BaseRecyclerAdapter;
import one.thebox.android.api.RequestBodies.order.UpdateQuantityOrderItemRequest;
import one.thebox.android.api.Responses.order.UpdateQuantityOrderItemResponse;
import one.thebox.android.app.Constants;
import one.thebox.android.app.TheBox;
import one.thebox.android.util.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by developers on 06/06/17.
 */

public class OrderItemAdapter extends BaseRecyclerAdapter {

    private Context context;
    /**
     * Parent Acitvity
     */
    private OrderItemsActivity orderItemsActivity;

    /**
     * GLide Request Manager
     */
    private RequestManager glideRequestManager;

    private ArrayList<OrderItem> orderItems;
    private boolean isChangesApplicable;

    public OrderItemAdapter(Context context, OrderItemsActivity orderItemsActivity, RequestManager glideRequestManager,
                            ArrayList<OrderItem> orderItems, boolean isChangesApplicable) {
        super(context);
        this.context = context;
        this.orderItemsActivity = orderItemsActivity;
        this.glideRequestManager = glideRequestManager;
        this.orderItems = orderItems;
        this.isChangesApplicable = isChangesApplicable;
    }

    public RequestManager getGlideRequestManager() {
        return glideRequestManager;
    }

    public void setGlideRequestManager(RequestManager glideRequestManager) {
        this.glideRequestManager = glideRequestManager;
    }

    public ArrayList<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(ArrayList<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }


    @Override
    protected ItemHolder getItemHolder(View view) {
        return new OrderItemViewHolder(view);
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
        OrderItemViewHolder orderItemViewHolder = (OrderItemViewHolder) holder;
        orderItemViewHolder.setView(orderItems.get(position), position);
    }

    @Override
    public void onBindViewHeaderHolder(HeaderHolder holder, int position) {

    }

    @Override
    public void onBindViewFooterHolder(FooterHolder holder, int position) {

    }

    @Override
    public int getItemsCount() {
        return orderItems.size();
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.card_order_item;
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

    private class OrderItemViewHolder extends BaseRecyclerAdapter.ItemHolder {

        private TextView productName, brand,
                config, addButton, subtractButton, quantitySelectedText, frequency, price, monthlySavings;
        private ImageView productImageView;
        private LinearLayout quantityHolder;
        private RelativeLayout holderTitle;
        private CardView cardViewPaid;


        public OrderItemViewHolder(View itemView) {
            super(itemView);
            productName = (TextView) itemView.findViewById(R.id.product_name);
            brand = (TextView) itemView.findViewById(R.id.product_brand);
            config = (TextView) itemView.findViewById(R.id.config);
            productImageView = (ImageView) itemView.findViewById(R.id.product_image_view);
            addButton = (TextView) itemView.findViewById(R.id.button_add);
            subtractButton = (TextView) itemView.findViewById(R.id.button_subtract);
            quantitySelectedText = (TextView) itemView.findViewById(R.id.no_of_item_selected);
            price = (TextView) itemView.findViewById(R.id.price);
            frequency = (TextView) itemView.findViewById(R.id.frequency);
            monthlySavings = (TextView) itemView.findViewById(R.id.savings_monthly);

            holderTitle = (RelativeLayout) itemView.findViewById(R.id.holder_title);
            quantityHolder = (LinearLayout) itemView.findViewById(R.id.layout_quantity_holder);
            cardViewPaid = (CardView) itemView.findViewById(R.id.layout_item_paid);
        }

        public void setView(final OrderItem orderItem, final int position) {

            try {
                productName.setText(orderItem.getBoxItem().getTitle());
                brand.setText(orderItem.getBoxItem().getBrand());
                if (orderItem.getBoxItem().getBrand() != null) {
                    if (!orderItem.getBoxItem().getBrand().isEmpty()) {
                        brand.setVisibility(View.VISIBLE);
                        brand.setText(orderItem.getBoxItem().getBrand());
                    } else {
                        brand.setVisibility(View.GONE);
                    }
                } else {
                    brand.setVisibility(View.GONE);
                }

                ItemConfig selectedItemConfig = orderItem.getSelectedItemConfig();

                frequency.setText("Repeat delivery " + selectedItemConfig.getSubscriptionText().toLowerCase());

                if (selectedItemConfig.getSize() == 0) {
                    config.setText(selectedItemConfig.getQuantity() + " " + selectedItemConfig.getSizeUnit() + " " + selectedItemConfig.getItemType());
                } else {
                    config.setText(selectedItemConfig.getSize() + " " + selectedItemConfig.getSizeUnit() + " " + selectedItemConfig.getItemType());
                }

                //show savings text if exist
                if (orderItem.getSavingsText() != null) {
                    if (!orderItem.getSavingsText().isEmpty()) {
                        monthlySavings.setText(orderItem.getSavingsText());
                        monthlySavings.setVisibility(View.VISIBLE);
                    } else {
                        monthlySavings.setText("");
                        monthlySavings.setVisibility(View.GONE);
                    }
                } else {
                    monthlySavings.setText("");
                    monthlySavings.setVisibility(View.GONE);
                }

                if (isChangesApplicable) {

                    // check if item is paid or not
                    if (orderItem.isPaid()) {
                        cardViewPaid.setVisibility(View.VISIBLE);
                        quantityHolder.setVisibility(View.GONE);
                        addButton.setVisibility(View.GONE);
                        subtractButton.setVisibility(View.GONE);
                        quantitySelectedText.setVisibility(View.GONE);

                        //when not edittable or paid
                        if (orderItem.getQuantity() == 1) {
                            price.setText(orderItem.getQuantity() + " piece for " + Constants.RUPEE_SYMBOL + " " + orderItem.getPrice());
                        } else {
                            price.setText(orderItem.getQuantity() + " pieces for " + Constants.RUPEE_SYMBOL + " " + orderItem.getPrice());
                        }
                    } else {
                        price.setText(Constants.RUPEE_SYMBOL + " " + orderItem.getPrice());
                        quantityHolder.setVisibility(View.VISIBLE);
                        addButton.setVisibility(View.VISIBLE);
                        subtractButton.setVisibility(View.VISIBLE);
                        quantitySelectedText.setVisibility(View.VISIBLE);
                        cardViewPaid.setVisibility(View.GONE);
                        quantitySelectedText.setText(String.valueOf(orderItem.getQuantity()));

                        LinearLayout.LayoutParams params = new
                                LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        holderTitle.setLayoutParams(params);
                    }

                } else {
                    //when not edittable
                    if (orderItem.getQuantity() == 1) {
                        price.setText(orderItem.getQuantity() + " piece for " + Constants.RUPEE_SYMBOL + " " + orderItem.getPrice());
                    } else {
                        price.setText(orderItem.getQuantity() + " pieces for " + Constants.RUPEE_SYMBOL + " " + orderItem.getPrice());
                    }

                    quantityHolder.setVisibility(View.GONE);
                    cardViewPaid.setVisibility(View.GONE);
                    addButton.setVisibility(View.GONE);
                    subtractButton.setVisibility(View.GONE);
                    quantitySelectedText.setVisibility(View.GONE);
                    LinearLayout.LayoutParams params = new
                            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    holderTitle.setLayoutParams(params);
                }


                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Update Quantity; from server
                        updateQuantity(orderItem, (orderItem.getQuantity() + 1), position);
                    }
                });

                subtractButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (orderItem.getQuantity() > 1) {
                            //Update Quantity; from server
                            updateQuantity(orderItem, (orderItem.getQuantity() - 1), position);
                        } else if (orderItem.getQuantity() == 1) {
                            MaterialDialog dialog = new MaterialDialog.Builder(mContext).
                                    title("Remove " + orderItem.getBoxItem().getTitle())
                                    .positiveText("Cancel")
                                    .negativeText("Remove")
                                    .content(orderItem.getBoxItem().getTitle() + " will be removed from this order. Are you sure?")
                                    .callback(new MaterialDialog.ButtonCallback() {
                                        @Override
                                        public void onPositive(MaterialDialog materialDialog) {
                                            // Dismiss the dialog
                                            materialDialog.cancel();
                                        }

                                        @Override
                                        public void onNegative(MaterialDialog materialDialog) {

                                            //update quantity; Remove Item
                                            updateQuantity(orderItem, 0, position);

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

                //image loading
                glideRequestManager.load(selectedItemConfig.getItemImage())
                        .centerCrop()
                        .crossFade()
                        .into(productImageView);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * Update Ordered Item to Server
         *
         * @param orderItem
         * @param quantity
         * @param position
         */
        public void updateQuantity(final OrderItem orderItem, int quantity, final int position) {

            final BoxLoader dialog = new BoxLoader(context).show();
            TheBox.getAPIService()
                    .updateQuantityOrderItem(PrefUtils.getToken(context),
                            orderItem.getUuid(), new UpdateQuantityOrderItemRequest(quantity))
                    .enqueue(new Callback<UpdateQuantityOrderItemResponse>() {
                        @Override
                        public void onResponse(Call<UpdateQuantityOrderItemResponse> call, Response<UpdateQuantityOrderItemResponse> response) {
                            dialog.dismiss();
                            try {
                                if (response.isSuccessful()) {
                                    if (response.body() != null) {

                                        if (response.body().isDeleted()) {
                                            //remove item
                                            orderItems.remove(position);
                                            notifyItemRemoved(position);
                                            notifyDataSetChanged();
                                            Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                        } else {
                                            //update item quantity
                                            orderItem.setQuantity(response.body().getOrderItem().getQuantity());
                                            orderItem.setPrice(response.body().getOrderItem().getPrice());
                                            if (response.body().getOrderItem().getSavingsText() != null) {
                                                if (!response.body().getOrderItem().getSavingsText().isEmpty()) {
                                                    orderItem.setSavingsText(response.body().getOrderItem().getSavingsText());
                                                }
                                            }
                                            orderItems.set(position, orderItem);
                                            notifyItemChanged(position);
                                        }

                                        if (orderItemsActivity != null) {
                                            if (response.body().getOrder() != null) {
                                                //update the button text
                                                orderItemsActivity.updateView(response.body().getOrder());
                                            }
                                        }


                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<UpdateQuantityOrderItemResponse> call, Throwable t) {
                            dialog.dismiss();
                            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    });


        }
    }
}
