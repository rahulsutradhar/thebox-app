package one.thebox.android.adapter.cart;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.RequestManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.realm.RealmList;
import one.thebox.android.Events.UpdateCartEvent;
import one.thebox.android.Helpers.CartHelper;
import one.thebox.android.Models.BoxItem;
import one.thebox.android.Models.ItemConfig;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.WrapContentLinearLayoutManager;
import one.thebox.android.activity.FullImageActivity;
import one.thebox.android.adapter.FrequencyAndPriceAdapter;
import one.thebox.android.adapter.RemainingCategoryAdapter;
import one.thebox.android.adapter.base.BaseRecyclerAdapter;
import one.thebox.android.app.TheBox;
import one.thebox.android.fragment.SizeAndFrequencyBottomSheetDialogFragment;

/**
 * Created by developers on 31/05/17.
 */

public class CartAdapter extends BaseRecyclerAdapter {

    private Context context;
    private List<BoxItem> boxItems = new ArrayList<>();
    /**
     * GLide Request Manager
     */
    private RequestManager glideRequestManager;


    public CartAdapter(Context context, RequestManager glideRequestManager) {
        super(context);
        this.context = context;
        this.glideRequestManager = glideRequestManager;
    }

    public List<BoxItem> getBoxItems() {
        return boxItems;
    }

    public void setBoxItems(List<BoxItem> boxItems) {
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
        return null;
    }

    @Override
    protected FooterHolder getFooterHolder(View view) {
        return null;
    }

    @Override
    public void onBindViewItemHolder(ItemHolder holder, int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.setViews(boxItems.get(position), position);
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
        return R.layout.item_search_detail_items;
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

        public ItemViewHolder(View itemView) {
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

        public void setViews(final BoxItem boxItem, final int position) {
            try {
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

                        Toast.makeText(TheBox.getAppContext(), "Called on Option Click " + boxItem.getItemConfigs().size(), Toast.LENGTH_SHORT).show();
                        displayNumberOfOption(boxItem, position);

                    }
                });

                //Show item with quantity more than 0
                holderSubscribeButton.setVisibility(View.GONE);
                out_of_stock.setVisibility(View.GONE);
                updateQuantityViewHolder.setVisibility(View.VISIBLE);
                repeat_every.setVisibility(View.VISIBLE);
                savingHolder.setVisibility(View.GONE);
                no_of_options_holder.setTextColor(TheBox.getInstance().getResources().getColor(R.color.dim_gray));

                noOfItemSelected.setText(String.valueOf(boxItem.getQuantity()));


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

            } catch (Exception e) {
                e.printStackTrace();
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

        private void displayNumberOfOption(final BoxItem boxItem, final int position) {
            final SizeAndFrequencyBottomSheetDialogFragment dialogFragment = SizeAndFrequencyBottomSheetDialogFragment.newInstance(boxItem);
            dialogFragment.show(((AppCompatActivity) mContext).getSupportFragmentManager()
                    , SizeAndFrequencyBottomSheetDialogFragment.TAG);
            dialogFragment.attachListener(new SizeAndFrequencyBottomSheetDialogFragment.OnSizeAndFrequencySelected() {
                @Override
                public void onSizeAndFrequencySelected(ItemConfig selectedItemConfig) {
                    dialogFragment.dismiss();

                    if (!boxItem.getUuid().isEmpty() && boxItem.getQuantity() > 0) {
                        updateItemConfigInCart(boxItem, selectedItemConfig, position);
                    }
                }
            });
        }

        /**
         * Update Quantity of BoxItem in Cart
         */
        private void updateQuantityInCart(BoxItem boxItem, int quantity, int position) {
            Toast.makeText(TheBox.getAppContext(), "Update Initiated", Toast.LENGTH_SHORT).show();
            boxItem.setQuantity(quantity);
            boxItems.get(position).setQuantity(quantity);
            notifyItemChanged(position);
            CartHelper.updateQuantityInCart(boxItem, quantity);

        }

        /**
         * Remove BoxItem from Cart
         */
        private void removeItemFromCart(BoxItem boxItem, int position) {
            boxItem.setQuantity(0);
            CartHelper.removeItemFromCart(boxItem);
            boxItems.remove(position);
            notifyItemRemoved(position);
            sendBroadcastToCartFragment();
        }

        /**
         * Update ItemConfig in Cart
         */
        private void updateItemConfigInCart(BoxItem boxItem, ItemConfig selectedItemConfig, int position) {
            Toast.makeText(TheBox.getAppContext(), "Update ItemConfig CALLED", Toast.LENGTH_SHORT).show();
            boxItem.setSelectedItemConfig(selectedItemConfig);
            CartHelper.updateItemConfigInCart(boxItem);
            boxItems.get(position).setSelectedItemConfig(selectedItemConfig);
            notifyItemChanged(position);
        }

        public void sendBroadcastToCartFragment() {
            EventBus.getDefault().post(new UpdateCartEvent());
        }

    }
}
