package one.thebox.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
    protected ItemHolder getItemHolder(View view, int position) {
        return null;
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
    public void onBindViewItemHolder(final ItemHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachSearchDetailFragment(searchResults.get(position));
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(holder.itemView.getWindowToken(), 0);
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
    protected int getItemLayoutId(int position) {
        return 0;
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
            if (searchResult.getId() == 0) {
                searchResultText.setText(searchResult.getResult());
            } else {
                SpannableStringBuilder str = new SpannableStringBuilder("See All " + searchResult.getResult());
                str.setSpan(new android.text.style.StyleSpan(Typeface.ITALIC), 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                searchResultText.setText(str);
            }
        }
    }

    private void attachSearchDetailFragment(SearchResult query) {
        SearchDetailFragment fragment = SearchDetailFragment.getInstance(query);
        FragmentTransaction fragmentTransaction = ((AppCompatActivity) mContext).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment).addToBackStack("Search Details");
        fragmentTransaction.commit();
    }
}
