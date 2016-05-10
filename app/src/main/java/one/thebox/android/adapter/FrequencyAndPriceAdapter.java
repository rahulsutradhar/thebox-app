package one.thebox.android.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import one.thebox.android.Models.BoxItem;
import one.thebox.android.R;

class FrequencyAndPriceAdapter extends BaseRecyclerAdapter {

    private ArrayList<BoxItem.ItemConfig> itemConfigs = new ArrayList<>();
    private int currentSelectedPosition = 0;
    private OnItemConfigChange onItemConfigChange;

    public FrequencyAndPriceAdapter(Context context, int currentSelectedPosition, OnItemConfigChange onItemConfigChange) {
        super(context);
        this.currentSelectedPosition = currentSelectedPosition;
        this.onItemConfigChange = onItemConfigChange;
    }

    public ArrayList<BoxItem.ItemConfig> getItemConfigs() {
        return itemConfigs;
    }

    public void setItemConfigs(ArrayList<BoxItem.ItemConfig> itemConfigs) {
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
        void onItemConfigItemChange(BoxItem.ItemConfig selectedItemConfig);
    }

    class ItemFrequencyViewHolder extends ItemHolder {

        View selector;
        private TextView textViewSize, textViewPrice;

        public ItemFrequencyViewHolder(View itemView) {
            super(itemView);
            textViewSize = (TextView) itemView.findViewById(R.id.text_view_frequency);
            textViewPrice = (TextView) itemView.findViewById(R.id.text_view_price);
            selector = itemView.findViewById(R.id.selector);
        }

        public void setView(BoxItem.ItemConfig itemConfig) {
            textViewPrice.setText("Rs " + itemConfig.getPrice());
            textViewSize.setText(itemConfig.getSubscriptionType());
            if (getAdapterPosition() == currentSelectedPosition) {
                textViewPrice.setTextColor(mContext.getResources().getColor(R.color.black));
                textViewSize.setTextColor(mContext.getResources().getColor(R.color.black));
                selector.setVisibility(View.VISIBLE);
            } else {
                textViewSize.setTextColor(mContext.getResources().getColor(R.color.primary_text_color));
                textViewPrice.setTextColor(mContext.getResources().getColor(R.color.primary_text_color));
                selector.setVisibility(View.INVISIBLE);
            }
        }
    }
}