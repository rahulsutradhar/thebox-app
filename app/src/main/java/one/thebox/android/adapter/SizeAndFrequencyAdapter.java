package one.thebox.android.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import io.realm.RealmList;
import one.thebox.android.Models.ItemConfig;
import one.thebox.android.R;
import one.thebox.android.fragment.SizeAndFrequencyBottomSheetDialogFragment;

/**
 * Created by Ajeet Kumar Meena on 12-04-2016.
 */
public class SizeAndFrequencyAdapter extends BaseRecyclerAdapter {

    private RealmList<ItemConfig> itemConfigs;
    private int currentItemSelected = -1;
    private int prevItemSelected = -1;
    private SizeAndFrequencyBottomSheetDialogFragment.OnSizeAndFrequencySelected onSizeAndFrequencySelected;

    public SizeAndFrequencyAdapter(Context context, SizeAndFrequencyBottomSheetDialogFragment.OnSizeAndFrequencySelected onSizeAndFrequencySelected) {
        super(context);
        itemConfigs = new RealmList<>();
        this.onSizeAndFrequencySelected = onSizeAndFrequencySelected;
    }

    public int getCurrentPositionSelected() {
        return currentItemSelected;
    }

    public void setCurrentItemSelected(int currentItemSelected) {
        this.currentItemSelected = currentItemSelected;
    }

    public RealmList<ItemConfig> getItemConfigs() {
        return itemConfigs;
    }


    public void setItemConfigs(RealmList<ItemConfig> itemConfigs) {
        this.itemConfigs.addAll(itemConfigs);
    }

    public int getPrevItemSelected() {
        return prevItemSelected;
    }

    public void setPrevItemSelected(int prevItemSelected) {
        this.prevItemSelected = prevItemSelected;
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
    public void onBindViewItemHolder(ItemHolder holder, final int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevItemSelected = currentItemSelected;
                currentItemSelected = position;
                notifyItemChanged(prevItemSelected);
                notifyItemChanged(currentItemSelected);
                onSizeAndFrequencySelected.onSizeAndFrequencySelected(itemConfigs.get(currentItemSelected));
            }
        });
        itemViewHolder.setViewHolder(itemConfigs.get(position));
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
        return R.layout.item_size_and_frequency;
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

    public class ItemViewHolder extends BaseRecyclerAdapter.ItemHolder {

        private TextView sizeTextView;
        private TextView costTextView;
        private int colorDimGray, colorRose;

        public ItemViewHolder(View itemView) {
            super(itemView);
            sizeTextView = (TextView) itemView.findViewById(R.id.size_text_view);
            costTextView = (TextView) itemView.findViewById(R.id.cost_text_view);
            colorDimGray = mContext.getResources().getColor(R.color.primary_text_color);
            colorRose = mContext.getResources().getColor(R.color.brilliant_rose);
        }

        public void setViewHolder(ItemConfig itemConfig) {
            sizeTextView.setText(itemConfig.getSize() + " " + itemConfig.getSizeUnit() + " " + itemConfig.getItemType());
            costTextView.setText(itemConfig.getPrice() + " Rs");
            if (getAdapterPosition() == currentItemSelected) {
                sizeTextView.setTextColor(colorRose);
                costTextView.setTextColor(colorRose);
            } else if (getAdapterPosition() == prevItemSelected) {
                sizeTextView.setTextColor(colorDimGray);
                costTextView.setTextColor(colorDimGray);
            }

        }
    }
}
