package one.thebox.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cocosw.bottomsheet.BottomSheet;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import one.thebox.android.Models.BoxItem;
import one.thebox.android.Models.DeliverySlot;
import one.thebox.android.Models.UserItem;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.ChangeSizeDialogViewHelper;
import one.thebox.android.util.DisplayUtil;
import one.thebox.android.util.NumberWordConverter;

public class UserItemRecyclerAdapter extends BaseRecyclerAdapter {

    ArrayList<UserItem> boxItems;

    public UserItemRecyclerAdapter(Context context, ArrayList<UserItem> boxItems) {
        super(context);
        this.boxItems = boxItems;
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
    public void onBindViewItemHolder(ItemHolder holder, int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.setViews(boxItems.get(position));
    }

    @Override
    public void onBindViewHeaderHolder(HeaderHolder holder, int position) {

    }

    @Override
    public void onBindViewFooterHolder(FooterHolder holder, int position) {

    }

    @Override
    public int getItemsCount() {
        return boxItems.size();
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_expanded_list;
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
        private TextView adjustButton, productName, brand, deliveryTime, arrivingTime, config, savings;
        private ImageView productImageView;

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
                                    boxItem.getBoxItem().setSelectedFrequency(itemConfig.getSubscriptionType());
                                    boxItem.getBoxItem().setSelectedPriceAndSize(new BoxItem.PriceAndSize(itemConfig.getPrice(), itemConfig.getSize(), itemConfig.getSizeUnit()));
                                    new ChangeSizeDialogViewHelper(mContext, new ChangeSizeDialogViewHelper.OnSizeAndFrequencySelected() {
                                        @Override
                                        public void onSizeAndFrequencySelected(String frequency, BoxItem.PriceAndSize priceAndSize) {

                                        }
                                    }).show(boxItem.getBoxItem());
                                    break;
                                }
                                case R.id.change_quantity: {
                                    break;
                                }
                                case R.id.change_frequency: {
                                    break;
                                }
                                case R.id.swap_with_similar_product: {
                                    openSwipeBottomSheet();
                                    break;
                                }
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