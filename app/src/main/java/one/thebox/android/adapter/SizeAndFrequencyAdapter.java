package one.thebox.android.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import io.realm.RealmList;
import one.thebox.android.Models.items.ItemConfig;
import one.thebox.android.R;
import one.thebox.android.adapter.base.BaseRecyclerAdapter;
import one.thebox.android.app.Constants;
import one.thebox.android.app.TheBox;
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
                if (itemConfigs.get(position).isInStock()) {
                    prevItemSelected = currentItemSelected;
                    currentItemSelected = position;
                    notifyItemChanged(prevItemSelected);
                    notifyItemChanged(currentItemSelected);
                    onSizeAndFrequencySelected.onSizeAndFrequencySelected(itemConfigs.get(currentItemSelected));

                } else {
                    Toast.makeText(TheBox.getAppContext(), "This product is out of stock.", Toast.LENGTH_SHORT).show();
                }
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
        private TextView savingsTextView, mrpTextView;
        private TextView costTextView;
        private int colorDimGray, colorRose;

        public ItemViewHolder(View itemView) {
            super(itemView);
            sizeTextView = (TextView) itemView.findViewById(R.id.size_text_view);
            mrpTextView = (TextView) itemView.findViewById(R.id.mrp_text_view);
            costTextView = (TextView) itemView.findViewById(R.id.cost_text_view);
            savingsTextView = (TextView) itemView.findViewById(R.id.text_view_savings);
            colorDimGray = mContext.getResources().getColor(R.color.primary_text_color);
            colorRose = mContext.getResources().getColor(R.color.brilliant_rose);
        }

        public void setViewHolder(ItemConfig itemConfig) {

            sizeTextView.setText(itemConfig.getSize() + " " + itemConfig.getSizeUnit() + " " + itemConfig.getItemType());

            costTextView.setText(Constants.RUPEE_SYMBOL + " " + itemConfig.getPrice());

            //savings text
            if (itemConfig.getSavingsText() != null) {
                if (!itemConfig.getSavingsText().isEmpty()) {
                    savingsTextView.setVisibility(View.VISIBLE);
                    savingsTextView.setText(itemConfig.getSavingsText());
                } else {
                    savingsTextView.setText("");
                    savingsTextView.setVisibility(View.GONE);
                }
            } else {
                savingsTextView.setText("");
                savingsTextView.setVisibility(View.GONE);
            }

            //mrp textView
            if (itemConfig.getMrpText() != null) {
                if (!itemConfig.getMrpText().isEmpty()) {
                    mrpTextView.setVisibility(View.VISIBLE);
                    mrpTextView.setText(itemConfig.getMrpText());
                } else {
                    mrpTextView.setText("");
                    mrpTextView.setVisibility(View.GONE);
                }
            } else {
                mrpTextView.setText("");
                mrpTextView.setVisibility(View.GONE);
            }

            if (itemConfig.isInStock()) {
                if (getAdapterPosition() == currentItemSelected) {
                    sizeTextView.setTextColor(colorRose);
                    costTextView.setTextColor(colorRose);
                } else if (getAdapterPosition() == prevItemSelected) {
                    sizeTextView.setTextColor(colorDimGray);
                    costTextView.setTextColor(colorDimGray);
                }
            } else {
                sizeTextView.setTextColor(TheBox.getInstance().getResources().getColor(R.color.manatee));
                costTextView.setTextColor(TheBox.getInstance().getResources().getColor(R.color.manatee));
                sizeTextView.setPaintFlags(sizeTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                costTextView.setPaintFlags(costTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }

        }
    }
}
