package one.thebox.android.adapter;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import one.thebox.android.Models.Category;
import one.thebox.android.R;
import one.thebox.android.adapter.base.BaseRecyclerAdapter;

public class SearchCategoryAdapter extends BaseRecyclerAdapter {

    private ArrayList<Category> categories = new ArrayList<>();
    private int currentSelection = 0;
    private OnHeaderCategoryChange onHeaderCategoryChange;

    public SearchCategoryAdapter(Context context,int currentSelection, OnHeaderCategoryChange onHeaderCategoryChange) {
        super(context);
        this.onHeaderCategoryChange = onHeaderCategoryChange;
        this.currentSelection = currentSelection;
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<Category> categories) {
        this.categories = categories;
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
        itemViewHolder.setView(categories.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int temp = currentSelection;
                currentSelection = position;
                notifyItemChanged(currentSelection);
                notifyItemChanged(temp);
                onHeaderCategoryChange.onHeaderCategoryChange(categories.get(position),position);
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
        return categories.size();
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_search_suggestion;
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

    public interface OnHeaderCategoryChange {
        void onHeaderCategoryChange(Category category, int positionSelected);
    }

    class ItemViewHolder extends ItemHolder {
        private LinearLayout parentLinearLayout;
        private TextView textView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            parentLinearLayout = (LinearLayout) itemView.findViewById(R.id.linear_layout);
            textView = (TextView) itemView.findViewById(R.id.text_view);
        }

        public void setView(Category category) {
            textView.setText(category.getTitle());
            if (getAdapterPosition() == currentSelection) {
                textView.setTextSize(20);
                textView.setTextColor(mContext.getResources().getColor(R.color.black));
            } else {
                textView.setTextSize(16);
                textView.setTextColor(mContext.getResources().getColor(R.color.primary_text_color));
            }
        }
    }

}