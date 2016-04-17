package one.thebox.android.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import one.thebox.android.Models.SizeAndFrequency;
import one.thebox.android.R;

/**
 * Created by Ajeet Kumar Meena on 12-04-2016.
 */
public class SizeAndFrequencyAdapter extends BaseRecyclerAdapter {

    ArrayList<SizeAndFrequency> sizeAndFrequencies;

    public SizeAndFrequencyAdapter(Context context) {
        super(context);
        sizeAndFrequencies = new ArrayList<>();
    }

    public void addSizeAndFrequency(SizeAndFrequency sizeAndFrequency) {
        sizeAndFrequencies.add(sizeAndFrequency);
    }

    public ArrayList<SizeAndFrequency> getSizeAndFrequencies() {
        return sizeAndFrequencies;
    }

    public void setSizeAndFrequencies(ArrayList<SizeAndFrequency> sizeAndFrequencies) {
        this.sizeAndFrequencies = sizeAndFrequencies;
    }

    @Override
    protected ItemHolder getItemHolder(View view) {
        return new ItemViewHolder(view);
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
                sizeAndFrequencies.get(position).setSelected(true);
                notifyItemChanged(position);
                for (int i = 0; i < sizeAndFrequencies.size(); i++) {
                    if (sizeAndFrequencies.get(i).isSelected() && i != position) {
                        sizeAndFrequencies.get(i).setSelected(false);
                        notifyItemChanged(i);
                    }
                }
            }
        });
        itemViewHolder.setViewHolder(sizeAndFrequencies.get(position));
    }

    @Override
    public void onBindViewHeaderHolder(HeaderHolder holder, int position) {

    }

    @Override
    public void onBindViewFooterHolder(FooterHolder holder, int position) {

    }

    @Override
    public int getItemsCount() {
        return sizeAndFrequencies.size();
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
            colorDimGray = mContext.getResources().getColor(R.color.dim_gray);
            colorRose = mContext.getResources().getColor(R.color.brilliant_rose);
        }

        public void setViewHolder(SizeAndFrequency sizeAndFrequency) {
            if (sizeAndFrequency.isSelected()) {
                sizeTextView.setTextColor(colorRose);
                costTextView.setTextColor(colorRose);
            } else {
                sizeTextView.setTextColor(colorDimGray);
                costTextView.setTextColor(colorDimGray);
            }
        }
    }
}
