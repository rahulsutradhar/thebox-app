package one.thebox.android.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import one.thebox.android.Events.ItemAddEvent;
import one.thebox.android.Models.Box;
import one.thebox.android.Models.BoxItem;
import one.thebox.android.Models.SearchResult;
import one.thebox.android.Models.SizeAndFrequency;
import one.thebox.android.R;
import one.thebox.android.util.CustomToast;

public class SearchDetailAdapter extends BaseRecyclerAdapter {

    private static final float selectedTextSize = 16;
    private static final float unselectedTextSize = 14;
    private static int selectedTextColor;
    private static int unSelectedTextColor;
    ArrayList<BoxItem> boxItems = new ArrayList<>();

    public SearchDetailAdapter(Context context, ArrayList<BoxItem> boxItems) {
        super(context);
        this.boxItems = boxItems;
        selectedTextColor = mContext.getResources().getColor(R.color.black);
        unSelectedTextColor = mContext.getResources().getColor(R.color.dim_gray);

    }

    public SearchDetailAdapter(Context context) {
        super(context);
        mViewType = RECYCLER_VIEW_TYPE_HEADER;
    }


    public void addBoxItem(BoxItem boxItem) {
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
    public void onBindViewItemHolder(final ItemHolder holder, final int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.setViews(boxItems.get(position));
        itemViewHolder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boxItems.get(position).setNoOfItemsSelected(boxItems.get(position).getNoOfItemsSelected() + 1);
                CustomToast.show(mContext,"Total Savings: 300 Rs per month");
                notifyItemChanged(holder.getAdapterPosition());
                int count = 0;
                for(int i=0; i<boxItems.size();i++) {
                    if(boxItems.get(i).getNoOfItemsSelected()>0){
                        count++;
                    }
                }
                EventBus.getDefault().post(new ItemAddEvent(count));
            }
        });
        itemViewHolder.subtractButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (boxItems.get(position).getNoOfItemsSelected() > 0) {
                    boxItems.get(position).setNoOfItemsSelected(boxItems.get(position).getNoOfItemsSelected() - 1);
                    int count = 0;
                    for(int i=0; i<boxItems.size();i++) {
                        if(boxItems.get(i).getNoOfItemsSelected()>0){
                            count++;
                        }
                    }
                    EventBus.getDefault().post(new ItemAddEvent(count));
                    notifyItemChanged(holder.getAdapterPosition());
                } else {
                    Toast.makeText(mContext, "CTA", Toast.LENGTH_SHORT).show();
                }

            }
        });
        itemViewHolder.changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChangeSizeDialog();
            }
        });
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
    }

    public class ItemViewHolder extends ItemHolder {

        private RecyclerView recyclerViewSavings;
        private RecyclerView recyclerViewFrequency;
        private MyBoxRecyclerAdapter.SavingsAdapter savingsAdapter;
        private ArrayList<Box.SmartItem> smartItems = new ArrayList<>();
        private TextView addButton, subtractButton;
        private TextView changeButton, noOfItemSelected;
        private LinearLayout savingHolder;
        



        public ItemViewHolder(View itemView) {
            super(itemView);
            recyclerViewSavings = (RecyclerView) itemView.findViewById(R.id.smart_item_recycler_view);
            addButton = (TextView) itemView.findViewById(R.id.button_add);
            subtractButton = (TextView) itemView.findViewById(R.id.button_subtract);
            changeButton = (TextView) itemView.findViewById(R.id.button_change);
            noOfItemSelected = (TextView) itemView.findViewById(R.id.no_of_item_selected);
            savingHolder = (LinearLayout) itemView.findViewById(R.id.saving_holder);
            recyclerViewFrequency = (RecyclerView) itemView.findViewById(R.id.recycler_view_frequency);
            setupRecyclerViewSavings();
            setupRecyclerViewFrequency();
        }

        private void setupRecyclerViewFrequency() {

        }

        public void setViews(BoxItem boxItem) {
            noOfItemSelected.setText(String.valueOf(boxItem.getNoOfItemsSelected()));
            if (boxItem.getNoOfItemsSelected() > 0) {
                savingHolder.setVisibility(View.VISIBLE);
            } else {
                savingHolder.setVisibility(View.GONE);
            }
        }

        private void setupRecyclerViewSavings() {
            for (int i = 0; i < 6; i++) {
                smartItems.add(new Box.SmartItem());
            }
            savingsAdapter = new MyBoxRecyclerAdapter.SavingsAdapter(mContext, smartItems);
            recyclerViewSavings.setLayoutManager(new GridLayoutManager(mContext, 3));
            recyclerViewSavings.setAdapter(savingsAdapter);
        }

        class FrequencyAdapter extends BaseRecyclerAdapter{



            public FrequencyAdapter(Context context) {
                super(context);
            }

            @Override
            protected ItemHolder getItemHolder(View view) {
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
                return 0;
            }

            @Override
            protected int getItemLayoutId() {
                return 0;
            }

            @Override
            protected int getHeaderLayoutId() {
                return 0;
            }

            @Override
            protected int getFooterLayoutId() {
                return 0;
            }
        }
    }

    private void openChangeSizeDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(mContext)
                .title("Change Size and Frequency")
                .customView(R.layout.layout_change_size_and_frequency, false)
                .build();
        View customView = dialog.getCustomView();
        if (customView != null) {
            customView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {

                }
            });
            final int colorDimGray = mContext.getResources().getColor(R.color.dim_gray);
            final int colorRose = mContext.getResources().getColor(R.color.brilliant_rose);
            final TextView buttonMonthly = (TextView) customView.findViewById(R.id.button_monthly);
            final TextView buttonTwiceAMonth = (TextView) customView.findViewById(R.id.button_twice_a_month);
            final TextView buttonWeekly = (TextView) customView.findViewById(R.id.button_weekly);
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = v.getId();
                    switch (id) {
                        case R.id.button_monthly: {
                            buttonMonthly.setTextColor(colorRose);
                            buttonTwiceAMonth.setTextColor(colorDimGray);
                            buttonWeekly.setTextColor(colorDimGray);
                            break;
                        }
                        case R.id.button_twice_a_month: {
                            buttonMonthly.setTextColor(colorDimGray);
                            buttonTwiceAMonth.setTextColor(colorRose);
                            buttonWeekly.setTextColor(colorDimGray);
                            break;
                        }
                        case R.id.button_weekly: {
                            buttonMonthly.setTextColor(colorDimGray);
                            buttonTwiceAMonth.setTextColor(colorDimGray);
                            buttonWeekly.setTextColor(colorRose);
                            break;
                        }
                    }
                }
            };
            buttonMonthly.setOnClickListener(onClickListener);
            buttonTwiceAMonth.setOnClickListener(onClickListener);
            buttonWeekly.setOnClickListener(onClickListener);
            RecyclerView recyclerView = (RecyclerView) customView.findViewById(R.id.recycler_view);
            SizeAndFrequencyAdapter sizeAndFrequencyAdapter = new SizeAndFrequencyAdapter(mContext);
            for (int i = 0; i < 10; i++) {
                sizeAndFrequencyAdapter.addSizeAndFrequency(new SizeAndFrequency());
            }
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            recyclerView.setAdapter(sizeAndFrequencyAdapter);
            dialog.getWindow().getAttributes().windowAnimations = R.style.MyAnimation_Window;
            dialog.show();
        }
    }
}
