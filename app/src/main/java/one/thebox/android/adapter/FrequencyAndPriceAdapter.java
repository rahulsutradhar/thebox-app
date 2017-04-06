package one.thebox.android.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import io.realm.RealmList;
import one.thebox.android.Models.ItemConfig;
import one.thebox.android.R;
import one.thebox.android.adapter.base.BaseRecyclerAdapter;
import one.thebox.android.app.Constants;
import one.thebox.android.app.TheBox;

class FrequencyAndPriceAdapter extends BaseRecyclerAdapter {

    private RealmList<ItemConfig> itemConfigs = new RealmList<>();
    private int currentSelectedPosition = 0;
    private OnItemConfigChange onItemConfigChange;
    private int quantity;

    public FrequencyAndPriceAdapter(Context context, int currentSelectedPosition, OnItemConfigChange onItemConfigChange) {
        super(context);
        this.currentSelectedPosition = currentSelectedPosition;
        this.onItemConfigChange = onItemConfigChange;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public RealmList<ItemConfig> getItemConfigs() {
        return itemConfigs;
    }

    protected void setItemConfigs(RealmList<ItemConfig> itemConfigs) {
        this.itemConfigs = itemConfigs;
    }

    @Override
    protected ItemHolder getItemHolder(View view) {
        return new ItemFrequencyViewHolder(view);
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
    public void onBindViewItemHolder(ItemHolder holder, final int position) {
        ItemFrequencyViewHolder itemFrequencyViewHolder = (ItemFrequencyViewHolder) holder;
        itemFrequencyViewHolder.setView(itemConfigs.get(position));

        if (itemConfigs.get(position).is_in_stock()) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int temp = currentSelectedPosition;
                    currentSelectedPosition = position;
                    onItemConfigChange.onItemConfigItemChange(itemConfigs.get(currentSelectedPosition));
                    notifyItemChanged(temp);
                    notifyItemChanged(position);
                }
            });
        }
    }

    @Override
    public void onBindViewHeaderHolder(HeaderHolder holder, int position) {

    }

    @Override
    public void onBindViewFooterHolder(FooterHolder holder, int position) {

    }

    @Override
    public int getItemsCount() {
        return itemConfigs.size();
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_size_and_price;
    }

    @Override
    protected int getHeaderLayoutId() {
        return 0;
    }

    @Override
    protected int getItemLayoutId(int position) {
        return 0;
    }

    @Override
    protected int getFooterLayoutId() {
        return 0;
    }

    interface OnItemConfigChange {
        void onItemConfigItemChange(ItemConfig selectedItemConfig);
    }

    private class ItemFrequencyViewHolder extends ItemHolder {

        View selector;
        private TextView textViewSize, textViewPrice;

        private ItemFrequencyViewHolder(View itemView) {
            super(itemView);
            textViewSize = (TextView) itemView.findViewById(R.id.text_view_frequency);
            textViewPrice = (TextView) itemView.findViewById(R.id.text_view_price);
            selector = itemView.findViewById(R.id.selector);
        }

        public void setView(ItemConfig itemConfig) {
            try {

                if (quantity != 0) {
                    textViewPrice.setText(Constants.RUPEE_SYMBOL + " " + (itemConfig.getPrice() * quantity));
                } else {
                    textViewPrice.setText(Constants.RUPEE_SYMBOL + " " + itemConfig.getPrice());
                }
                textViewSize.setText(itemConfig.getSubscriptionText());

                if (getAdapterPosition() == currentSelectedPosition) {
                    textViewPrice.setTextColor(TheBox.getInstance().getResources().getColor(R.color.black));
                    textViewSize.setTextColor(TheBox.getInstance().getResources().getColor(R.color.black));
                    textViewPrice.setTextSize(TypedValue.COMPLEX_UNIT_PX, TheBox.getInstance().getResources().getDimension(R.dimen.text_small1));
                    textViewSize.setTextSize(TypedValue.COMPLEX_UNIT_PX, TheBox.getInstance().getResources().getDimension(R.dimen.text_small1));
                    selector.setVisibility(View.VISIBLE);
                } else {
                    textViewSize.setTextColor(TheBox.getInstance().getResources().getColor(R.color.md_grey_600));
                    textViewPrice.setTextColor(TheBox.getInstance().getResources().getColor(R.color.md_grey_600));
                    textViewPrice.setTextSize(TypedValue.COMPLEX_UNIT_PX, TheBox.getInstance().getResources().getDimension(R.dimen.text_extra_small3));
                    textViewSize.setTextSize(TypedValue.COMPLEX_UNIT_PX, TheBox.getInstance().getResources().getDimension(R.dimen.text_extra_small3));
                    selector.setVisibility(View.INVISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }
}