package one.thebox.android.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import one.thebox.android.Models.BoxItem;
import one.thebox.android.R;

class FrequencyAndPriceAdapter extends BaseRecyclerAdapter {

    ArrayList<BoxItem.ItemConfig> itemConfigs = new ArrayList<>();
    int currentSelectedPosition = 0;

    public FrequencyAndPriceAdapter(Context context) {
        super(context);
    }

    public FrequencyAndPriceAdapter(Context context, ArrayList<BoxItem.ItemConfig> itemConfigs, int currentSelectedPosition) {
        super(context);
        this.itemConfigs = itemConfigs;
        this.currentSelectedPosition = currentSelectedPosition;
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

    class ItemFrequencyViewHolder extends ItemHolder {

        private TextView textViewSize, textViewPrice;
        View selector;

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
                textViewSize.setTextColor(mContext.getResources().getColor(R.color.dim_gray));
                textViewPrice.setTextColor(mContext.getResources().getColor(R.color.dim_gray));
                selector.setVisibility(View.INVISIBLE);
            }
        }
    }
}