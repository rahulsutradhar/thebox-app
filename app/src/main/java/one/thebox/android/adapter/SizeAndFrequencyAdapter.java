package one.thebox.android.adapter;

import android.content.Context;
import android.view.View;

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
    public void onBindViewItemHolder(ItemHolder holder, int position) {

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

        public ItemViewHolder(View itemView) {
            super(itemView);
        }
    }
}
