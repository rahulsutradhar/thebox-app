package one.thebox.android.adapter;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.concurrent.ThreadPoolExecutor;

import one.thebox.android.Models.Box;
import one.thebox.android.Models.SearchResult;
import one.thebox.android.Models.SizeAndFrequency;
import one.thebox.android.R;
import one.thebox.android.util.CustomToast;

public class SearchDetailAdapter extends BaseRecyclerAdapter {

    private static final float selectedTextSize = 16;
    private static final float unselectedTextSize = 14;
    private static int selectedTextColor;
    private static int unSelectedTextColor;
    ArrayList<Box.BoxItem> boxItems = new ArrayList<>();

    public SearchDetailAdapter(Context context, ArrayList<Box.BoxItem> boxItems) {
        super(context);
        this.boxItems = boxItems;
        selectedTextColor = mContext.getResources().getColor(R.color.black);
        unSelectedTextColor = mContext.getResources().getColor(R.color.dim_gray);

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
    public void onBindViewItemHolder(final ItemHolder holder, final int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.setViews(boxItems.get(position));
        itemViewHolder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boxItems.get(position).setNoOfItemsSelected(boxItems.get(position).getNoOfItemsSelected() + 1);
                CustomToast.show(mContext,"Total Savings: 300 Rs per month");
                notifyItemChanged(holder.getAdapterPosition());
            }
        });
        itemViewHolder.subtractButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (boxItems.get(position).getNoOfItemsSelected() > 0) {
                    boxItems.get(position).setNoOfItemsSelected(boxItems.get(position).getNoOfItemsSelected() - 1);
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

        private RecyclerView recyclerView;
        private MyBoxRecyclerAdapter.SmartItemAdapter smartItemAdapter;
        private ArrayList<Box.SmartItem> smartItems = new ArrayList<>();
        private TextView addButton, subtractButton;
        private ArrayList<View> sliders = new ArrayList<>();
        private ArrayList<TextView> titles = new ArrayList<>();
        private ArrayList<TextView> prices = new ArrayList<>();
        private ArrayList<LinearLayout> linearLayouts = new ArrayList<>();
        private TextView changeButton, noOfItemSelected;
        private LinearLayout savingHolder;

        private View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                int selectedPosition = 0;
                switch (id) {
                    case R.id.layout1: {
                        selectedPosition = 0;
                        break;
                    }
                    case R.id.layout2: {
                        selectedPosition = 1;
                        break;
                    }
                    case R.id.layout3: {
                        selectedPosition = 2;
                        break;
                    }
                }
                boxItems.get(getAdapterPosition()).setFrequency(selectedPosition);
                selectPosition(selectedPosition);
            }
        };

        public void selectPosition(int selectedPosition) {
            for (int i = 0; i < linearLayouts.size(); i++) {
                if (i == selectedPosition) {
                    sliders.get(i).setVisibility(View.VISIBLE);
                    titles.get(i).setTextColor(mContext.getResources().getColor(R.color.black));
                    prices.get(i).setTextColor(mContext.getResources().getColor(R.color.black));
                    titles.get(i).setTextSize(selectedTextSize);
                    prices.get(i).setTextSize(selectedTextSize);
                } else {
                    sliders.get(i).setVisibility(View.GONE);
                    titles.get(i).setTextColor(mContext.getResources().getColor(R.color.dim_gray));
                    prices.get(i).setTextColor(mContext.getResources().getColor(R.color.dim_gray));
                    titles.get(i).setTextSize(unselectedTextSize);
                    prices.get(i).setTextSize(unselectedTextSize);
                }
            }
        }

        public ItemViewHolder(View itemView) {
            super(itemView);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.smart_item_recycler_view);
            addButton = (TextView) itemView.findViewById(R.id.button_add);
            subtractButton = (TextView) itemView.findViewById(R.id.button_subtract);
            titles.add((TextView) itemView.findViewById(R.id.monthly));
            titles.add((TextView) itemView.findViewById(R.id.twice_a_month));
            titles.add((TextView) itemView.findViewById(R.id.weekly));
            prices.add((TextView) itemView.findViewById(R.id.price1));
            prices.add((TextView) itemView.findViewById(R.id.price2));
            prices.add((TextView) itemView.findViewById(R.id.price3));
            linearLayouts.add((LinearLayout) itemView.findViewById(R.id.layout1));
            linearLayouts.add((LinearLayout) itemView.findViewById(R.id.layout2));
            linearLayouts.add((LinearLayout) itemView.findViewById(R.id.layout3));
            sliders.add(itemView.findViewById(R.id.selector1));
            sliders.add(itemView.findViewById(R.id.selector2));
            sliders.add(itemView.findViewById(R.id.selector3));
            changeButton = (TextView) itemView.findViewById(R.id.button_change);
            noOfItemSelected = (TextView) itemView.findViewById(R.id.no_of_item_selected);
            savingHolder = (LinearLayout) itemView.findViewById(R.id.saving_holder);
            for (int i = 0; i < sliders.size(); i++) {
                linearLayouts.get(i).setOnClickListener(onClickListener);
            }
            setupRecyclerView();
        }

        public void setViews(Box.BoxItem boxItem) {
            selectPosition(boxItem.getFrequency());
            noOfItemSelected.setText(String.valueOf(boxItem.getNoOfItemsSelected()));
            if (boxItem.getNoOfItemsSelected() > 0) {
                savingHolder.setVisibility(View.VISIBLE);
            } else {
                savingHolder.setVisibility(View.GONE);
            }
        }

        private void setupRecyclerView() {
            for (int i = 0; i < 6; i++) {
                smartItems.add(new Box.SmartItem());
            }
            smartItemAdapter = new MyBoxRecyclerAdapter.SmartItemAdapter(mContext, smartItems);
            recyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
            recyclerView.setAdapter(smartItemAdapter);
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
