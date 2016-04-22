package one.thebox.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import one.thebox.android.Models.SearchResult;
import one.thebox.android.R;
import one.thebox.android.fragment.SearchDetailFragment;
import one.thebox.android.fragment.SearchResultFragment;

/**
 * Created by Ajeet Kumar Meena on 13-04-2016.
 */
public class SearchResultAllItemAdapter extends BaseRecyclerAdapter {

    private ArrayList<SearchResult> searchResults = new ArrayList<>();

    public SearchResultAllItemAdapter(Context context) {
        super(context);
        mViewType = RECYCLER_VIEW_TYPE_HEADER;
    }

    public void addSearchResult(SearchResult searchResult) {
        searchResults.add(searchResult);
    }

    public ArrayList<SearchResult> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(ArrayList<SearchResult> searchResults) {
        this.searchResults = searchResults;
    }

    @Override
    protected ItemHolder getItemHolder(View view) {
        return new ItemViewHolder(view);
    }

    @Override
    protected HeaderHolder getHeaderHolder(View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    protected FooterHolder getFooterHolder(View view) {
        return null;
    }

    @Override
    public void onBindViewItemHolder(ItemHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachSearchDetailFragment(searchResults.get(position).getResult());
            }
        });
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.setViews(searchResults.get(position));
    }

    @Override
    public void onBindViewHeaderHolder(HeaderHolder holder, int position) {

    }

    @Override
    public void onBindViewFooterHolder(FooterHolder holder, int position) {

    }

    @Override
    public int getItemsCount() {
        return searchResults.size();
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_search_result;
    }

    @Override
    protected int getHeaderLayoutId() {
        return R.layout.header_search_result;
    }

    @Override
    protected int getFooterLayoutId() {
        return 0;
    }

    class HeaderViewHolder extends HeaderHolder {

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    class ItemViewHolder extends ItemHolder {

        private TextView searchResultText;

        public ItemViewHolder(View itemView) {
            super(itemView);
            searchResultText = (TextView) itemView.findViewById(R.id.text_search_result);
        }

        public void setViews(SearchResult searchResult) {
            searchResultText.setText(searchResult.getResult());
        }
    }

    private void attachSearchDetailFragment(String query) {
        SearchDetailFragment fragment = SearchDetailFragment.getInstance(query);
        FragmentTransaction fragmentTransaction = ((AppCompatActivity) mContext).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();
    }
}
