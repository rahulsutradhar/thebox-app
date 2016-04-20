package one.thebox.android.adapter;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import one.thebox.android.Models.SearchResult;
import one.thebox.android.R;

public class SearchSuggestionAdapter extends BaseRecyclerAdapter {

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
            public void onBindViewItemHolder(ItemHolder holder, final int position) {
                ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
                itemViewHolder.setView(searchSuggestions.get(position));
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        searchSuggestions.get(position).setSelected(true);
                        notifyItemChanged(position);
                        for (int i = 0; i < searchSuggestions.size(); i++) {
                            if (searchSuggestions.get(i).isSelected() && i != position) {
                                searchSuggestions.get(i).setSelected(false);
                                notifyItemChanged(i);
                            }
                        }
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
                private LinearLayout parentLinearLayout;
                private TextView textView;

                public ItemViewHolder(View itemView) {
                    super(itemView);
                    parentLinearLayout = (LinearLayout) itemView.findViewById(R.id.linear_layout);
                    textView = (TextView) itemView.findViewById(R.id.text_view);
                }

                public void setView(SearchResult.SearchSuggestion searchSuggestion) {
                    if (searchSuggestion.isSelected()) {
                        textView.setTextSize(20);
                        textView.setTextColor(mContext.getResources().getColor(R.color.black));
                    } else {
                        textView.setTextSize(16);
                        textView.setTextColor(mContext.getResources().getColor(R.color.dim_gray));
                    }
                }
            }

        }