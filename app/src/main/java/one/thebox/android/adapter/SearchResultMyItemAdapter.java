package one.thebox.android.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

import one.thebox.android.Models.Box;
import one.thebox.android.Models.Category;
import one.thebox.android.R;
import one.thebox.android.util.DisplayUtil;

public class SearchResultMyItemAdapter extends BaseRecyclerAdapter {

    ArrayList<Box> boxItems = new ArrayList<>();

    public SearchResultMyItemAdapter(Context context, ArrayList<Box> boxItems) {
        super(context);
        this.boxItems = boxItems;
    }

    public SearchResultMyItemAdapter(Context context) {
        super(context);
    }


    public void addBoxItem(Box boxItem) {
        boxItems.add(boxItem);
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
        return new ItemHeaderHolder(view);
    }

    @Override
    protected FooterHolder getFooterHolder(View view) {
        return null;
    }

    @Override
    public void onBindViewItemHolder(ItemHolder holder, int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.setViews(boxItems.get(position));
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
        return R.layout.item_search_result_my_items;
    }

    @Override
    protected int getHeaderLayoutId() {
        return R.layout.empty_space_header;
    }

    @Override
    protected int getItemLayoutId(int position) {
        return 0;
    }

    @Override
    protected int getFooterLayoutId() {
        return 0;
    }


    public class ItemHeaderHolder extends BaseRecyclerAdapter.HeaderHolder {

        private LinearLayout linearLayout;

        public ItemHeaderHolder(View itemView) {
            super(itemView);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linear_layout);
        }

        public void setHeight(int heightInDp) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
            layoutParams.height = DisplayUtil.dpToPx(mContext, heightInDp);
            linearLayout.setLayoutParams(layoutParams);

        }
    }

    public class ItemViewHolder extends BaseRecyclerAdapter.ItemHolder {

        private RecyclerView recyclerView;
        private MyBoxRecyclerAdapter.RemainingCategoryAdapter remainingCategoryAdapter;
        private ArrayList<Category> categories = new ArrayList<>();

        public ItemViewHolder(View itemView) {
            super(itemView);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.smart_item_recycler_view);
            setupRecyclerView();
        }

        public void setViews(Box boxItem) {

        }

        private void setupRecyclerView() {
  /*         *//* for (int i = 0; i < 10; i++) {
                categories.add(new Box.);
            }*//*
            remainingCategoryAdapter = new MyBoxRecyclerAdapter.RemainingCategoryAdapter(mContext, categories);
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            recyclerView.setAdapter(remainingCategoryAdapter);*/
        }
    }
}
