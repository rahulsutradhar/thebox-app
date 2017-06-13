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

import com.bumptech.glide.RequestManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import io.realm.RealmList;
import one.thebox.android.Events.SyncCartItemEvent;
import one.thebox.android.Helpers.cart.CartHelper;
import one.thebox.android.Models.items.BoxItem;
import one.thebox.android.Models.items.ItemConfig;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.WrapContentLinearLayoutManager;
import one.thebox.android.activity.FullImageActivity;
import one.thebox.android.adapter.FrequencyAndPriceAdapter;
import one.thebox.android.adapter.RemainingCategoryAdapter;
import one.thebox.android.adapter.base.BaseRecyclerAdapter;
import one.thebox.android.app.Constants;
import one.thebox.android.app.TheBox;
import one.thebox.android.fragment.CartFragment;
import one.thebox.android.fragment.SizeAndFrequencyBottomSheetDialogFragment;
import one.thebox.android.services.cart.CartHelperService;

/**
 * Created by developers on 31/05/17.
 */

public class CartAdapter extends BaseRecyclerAdapter {

    private Context context;
    private ArrayList<BoxItem> boxItems = new ArrayList<>();
    private CartFragment cartFragment;
    /**
     * GLide Request Manager
     */
    private RequestManager glideRequestManager;


    public CartAdapter(Context context, RequestManager glideRequestManager, CartFragment cartFragment) {
        super(context);
        this.context = context;
        this.glideRequestManager = glideRequestManager;
        this.cartFragment = cartFragment;
    }

    public ArrayList<BoxItem> getBoxItems() {
        return boxItems;
    }

    public void setBoxItems(ArrayList<BoxItem> boxItems) {
        this.boxItems = boxItems;
    }

    public CartFragment getCartFragment() {
        return cartFragment;
    }

    public void setCartFragment(CartFragment cartFragment) {
        this.cartFragment = cartFragment;
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
                if (boxItem.getQuantity() > 1) {
                    savingsTitle.setVisibility(View.VISIBLE);
                    savingsTitle.setText("Save " + Constants.RUPEE_SYMBOL + " " + String.valueOf(boxItem.getSelectedItemConfig().getMonthlySavingsValue() * boxItem.getQuantity()) + " every month");
                } else {
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
                        updateItemConfigInCart(boxItem, selectedItemConfig, position, true);

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
            final SizeAndFrequencyBottomSheetDialogFragment dialogFragment = new
                    SizeAndFrequencyBottomSheetDialogFragment(boxItem.getItemConfigs(), boxItem.getSelectedItemConfig());
            dialogFragment.show(((AppCompatActivity) mContext).getSupportFragmentManager()
                    , SizeAndFrequencyBottomSheetDialogFragment.TAG);
            dialogFragment.attachListener(new SizeAndFrequencyBottomSheetDialogFragment.OnSizeAndFrequencySelected() {
                @Override
                public void onSizeAndFrequencySelected(ItemConfig selectedItemConfig) {
                    dialogFragment.dismiss();

                    if (!boxItem.getUuid().isEmpty() && boxItem.getQuantity() > 0) {
                        updateItemConfigInCart(boxItem, selectedItemConfig, position, false);
                    }
                }
            });
        }

        /**
         * Update Quantity of BoxItem in Cart
         */
        private void updateQuantityInCart(BoxItem boxItem, int quantity, int position) {
            try {
                BoxItem updatedBoxItem = CartHelper.updateQuantityInsideCart(boxItem, quantity);
                boxItems.set(position, updatedBoxItem);
                notifyItemChanged(position);
                //send broadcast
                sendBroadcastToCartFragment();

                //check for background service
                CartHelperService.checkServiceRunningWhenAdded(mContext);

                /**
                 * Pass Event to Update the Search Items
                 */
                EventBus.getDefault().post(new SyncCartItemEvent(Constants.UPDATE_QUANTITY_EVENT, updatedBoxItem));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * Remove BoxItem from Cart
         */
        private void removeItemFromCart(BoxItem boxItem, int position) {
            /**
             * Pass Event to Update the Search Items
             */
            EventBus.getDefault().post(new SyncCartItemEvent(Constants.REMOVE_ITEM_EVENT, boxItem));

            /**
             * SetCleverTapEventRemoveItem
             */
            setCleverTapEventItemRemoveFromCart(boxItem);

            CartHelper.removeItemFromCart(boxItem);
            boxItems.remove(position);
            notifyDataSetChanged();
            sendBroadcastToCartFragment();

            //check for background service
            CartHelperService.checkServiceRunningWhenRemoved(mContext, true);
        }

        /**
         * Update ItemConfig in Cart
         */
        private void updateItemConfigInCart(BoxItem boxItem, ItemConfig selectedItemConfig, int position, boolean isFrequency) {
            try {
                BoxItem updatedBoxItem;
                updatedBoxItem = CartHelper.updateItemConfigInsideCart(boxItem, selectedItemConfig);
                boxItems.set(position, updatedBoxItem);
                notifyItemChanged(position);
                if (!isFrequency) {
                    sendBroadcastToCartFragment();
                }

                //check for background service
                CartHelperService.checkServiceRunningWhenAdded(mContext);

                /**
                 * Pass Event to Update the Search Items
                 */
                EventBus.getDefault().post(new SyncCartItemEvent(Constants.UPDATE_ITEMCONFIG_EVENT, updatedBoxItem));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void sendBroadcastToCartFragment() {
            if (cartFragment != null) {
                cartFragment.initVariables(false);
            }
        }

        /**
         * CleverTap Event;
         * <p>
         * Remove Item from Cart;
         */
        public void setCleverTapEventItemRemoveFromCart(BoxItem boxItem) {
            try {
                TheBox.getCleverTap().event.push("item_remove_from_cart", getParam(boxItem));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public HashMap getParam(BoxItem boxItem) {
            HashMap<String, Object> hashMap = new HashMap<>();
            try {
                hashMap.put("title", boxItem.getTitle());
                hashMap.put("brand", boxItem.getBrand());
                hashMap.put("item_config_name", boxItem.getSelectedItemConfig().getSize() + " " +
                        boxItem.getSelectedItemConfig().getSizeUnit() + ", " + boxItem.getSelectedItemConfig().getItemType());
                hashMap.put("item_config_subscription", boxItem.getSelectedItemConfig().getSubscriptionText());
                hashMap.put("item_config_uuid", boxItem.getSelectedItemConfig().getUuid());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return hashMap;
        }

    }
}
