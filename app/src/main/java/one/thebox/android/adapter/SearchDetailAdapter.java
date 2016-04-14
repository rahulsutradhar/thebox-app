package one.thebox.android.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

import one.thebox.android.Models.Box;
import one.thebox.android.Models.SearchResult;
import one.thebox.android.R;

public class SearchDetailAdapter extends BaseRecyclerAdapter {

    ArrayList<Box.BoxItem> boxItems = new ArrayList<>();

    public SearchDetailAdapter(Context context, ArrayList<Box.BoxItem> boxItems) {
        super(context);
        this.boxItems = boxItems;
        mViewType = RECYCLER_VIEW_TYPE_HEADER;
    }

    public SearchDetailAdapter(Context context) {
        super(context);
        mViewType = RECYCLER_VIEW_TYPE_HEADER;
    }


    public void addBoxItem(Box.BoxItem boxItem) {
        boxItems.add(boxItem);
    }

    @Override
    protected ItemHolder getItemHolder(View view) {
        return new ItemViewHolder(view);
    }

    @Override
    protected HeaderHolder getHeaderHolder(View view) {
        return new ItemViewHeaderHolder(view);
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
        ItemViewHeaderHolder itemViewHeaderHolder = (ItemViewHeaderHolder) holder;
        itemViewHeaderHolder.setViewHolder();
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
        return R.layout.header_search_detail;
    }

    @Override
    protected int getFooterLayoutId() {
        return 0;
    }


    public class ItemViewHeaderHolder extends HeaderHolder {

        private RecyclerView recyclerView;
        private SearchSuggestionAdapter searchSuggestionAdapter;


        public ItemViewHeaderHolder(View itemView) {
            super(itemView);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.recycler_view);
        }

        public void setViewHolder() {
            searchSuggestionAdapter = new SearchSuggestionAdapter(mContext);
            for (int i = 0; i < 10; i++) {
                searchSuggestionAdapter.addSearchSuggestions(new SearchResult.SearchSuggestion());
            }
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            recyclerView.setAdapter(searchSuggestionAdapter);
        }

        class SearchSuggestionAdapter extends BaseRecyclerAdapter {

            ArrayList<SearchResult.SearchSuggestion> searchSuggestions = new ArrayList<>();

            public SearchSuggestionAdapter(Context context) {
                super(context);
            }

            public void addSearchSuggestions(SearchResult.SearchSuggestion searchSuggestion) {
                searchSuggestions.add(searchSuggestion);
            }

            public ArrayList<SearchResult.SearchSuggestion> getSearchSuggestions() {
                return searchSuggestions;
            }

            public void setSearchSuggestions(ArrayList<SearchResult.SearchSuggestion> searchSuggestions) {
                this.searchSuggestions = searchSuggestions;
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
                return searchSuggestions.size();
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
            protected int getFooterLayoutId() {
                return 0;
            }

            class ItemViewHolder extends ItemHolder {

                public ItemViewHolder(View itemView) {
                    super(itemView);
                }
            }

        }
    }

    public class ItemViewHolder extends ItemHolder {

        private RecyclerView recyclerView;
        private MyBoxRecyclerAdapter.SmartItemAdapter smartItemAdapter;
        private ArrayList<Box.SmartItem> smartItems = new ArrayList<>();

        public ItemViewHolder(View itemView) {
            super(itemView);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.smart_item_recycler_view);
            setupRecyclerView();
        }

        public void setViews(Box.BoxItem boxItem) {

        }

        private void setupRecyclerView() {
            for (int i = 0; i < 10; i++) {
                smartItems.add(new Box.SmartItem());
            }
            smartItemAdapter = new MyBoxRecyclerAdapter.SmartItemAdapter(mContext, smartItems);
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            recyclerView.setAdapter(smartItemAdapter);
        }
    }
}
