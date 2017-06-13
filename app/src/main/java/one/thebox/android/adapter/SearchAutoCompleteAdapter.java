package one.thebox.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import one.thebox.android.Models.search.SearchResult;
import one.thebox.android.R;
import one.thebox.android.activity.MainActivity;
import one.thebox.android.adapter.base.BaseRecyclerAdapter;
import one.thebox.android.app.Constants;
import one.thebox.android.util.CoreGsonUtils;

/**
 * Created by Ajeet Kumar Meena on 13-04-2016.
 */
public class SearchAutoCompleteAdapter extends BaseRecyclerAdapter {

    private ArrayList<SearchResult> searchResults = new ArrayList<>();
    private Context context;
    private String searchQuery;

    public SearchAutoCompleteAdapter(Context context) {
        super(context);
        this.context = context;
    }

    public ArrayList<SearchResult> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(ArrayList<SearchResult> searchResults) {
        this.searchResults = searchResults;
        notifyDataSetChanged();
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
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

        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.setViews(searchResults.get(position), position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra(MainActivity.EXTRA_ATTACH_FRAGMENT_NO, 4);
                intent.putExtra(Constants.EXTRA_SEARCH_RESULT_DATA, CoreGsonUtils.toJson(searchResults.get(position)));
                ((Activity) mContext).setResult(1, intent);
                ((Activity) mContext).finish();
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

        private TextView searchResultText, textViewResultType;

        public ItemViewHolder(View itemView) {
            super(itemView);
            searchResultText = (TextView) itemView.findViewById(R.id.text_search_result);
            textViewResultType = (TextView) itemView.findViewById(R.id.text_result_type);
        }

        public void setViews(SearchResult searchResult, int position) {
            try {
                searchResultText.setTextColor(context.getResources().getColor(R.color.md_green_800));
                searchResultText.setText(Html.fromHtml(searchResult.getTitle()));
                if (searchResult.isCategory()) {
                    textViewResultType.setText("Category");
                } else {
                    textViewResultType.setText("Product");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
