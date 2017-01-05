package one.thebox.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import io.realm.RealmList;
import one.thebox.android.Events.OnCategorySelectEvent;
import one.thebox.android.Models.Box;
import one.thebox.android.Models.Category;
import one.thebox.android.Models.ExploreItem;
import one.thebox.android.Models.UserCategory;
import one.thebox.android.Models.UserItem;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.Montserrat;
import one.thebox.android.ViewHelper.MontserratTextView;
import one.thebox.android.ViewHelper.ShowcaseHelper;
import one.thebox.android.activity.MainActivity;
import one.thebox.android.api.RestClient;
import one.thebox.android.app.MyApplication;
import one.thebox.android.fragment.SearchDetailFragment;
import one.thebox.android.util.CoreGsonUtils;
import one.thebox.android.util.DisplayUtil;
import one.thebox.android.util.PrefUtils;

/**
 * Created by Ajeet Kumar Meena on 11-04-2016.
 */
public class MyBoxRecyclerAdapter extends BaseRecyclerAdapter {

    private String monthly_bill;
    private String total_no_of_items;
    private RealmList<Box> boxes;
    private int stickyHeaderHeight = 0;
    private SparseIntArray boxHeights = new SparseIntArray();

    public MyBoxRecyclerAdapter(Context context) {
        super(context);
    }

    public void addBox(Box box) {
        boxes.add(box);
    }

    public RealmList<Box> getBoxes() {
        return boxes;
    }

    public void setBoxes(RealmList<Box> boxes) {
        this.boxes = boxes;
    }

    public String getMonthly_bill() {
        return monthly_bill;
    }

    public void setMonthly_bill(String monthly_bill) {
        this.monthly_bill = monthly_bill;
    }

    public String getTotal_no_of_items() {
        return total_no_of_items;
    }

    public void setTotal_no_of_items(String total_no_of_items) {
        this.total_no_of_items = total_no_of_items;
    }

    public int getStickyHeaderHeight() {
        return stickyHeaderHeight;
    }

    public void setStickyHeaderHeight(int stickyHeaderHeight) {
        this.stickyHeaderHeight = stickyHeaderHeight;
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
        itemViewHolder.setViews(boxes.get(position));

        if (PrefUtils.getBoolean(MyApplication.getInstance(), "home_tutorial", true) && (!RestClient.is_in_development)) {
            new ShowcaseHelper((Activity) mContext, 0).show("Search", "Search for an item, brand or category", ((MainActivity) mContext).getSearchView())
                    .setOnCompleteListener(new ShowcaseHelper.OnCompleteListener() {
                        @Override
                        public void onComplete() {
                            PrefUtils.putBoolean(MyApplication.getInstance(), "home_tutorial", false);
                            new ShowcaseHelper((Activity) mContext, 3)
                                    .show("My Boxes", "Edit and keep track of all items being delivered to you regularly", holder.itemView);
                        }
                    });
        }
    }

    @Override
    public void onBindViewHeaderHolder(BaseRecyclerAdapter.HeaderHolder holder, int position) {

        HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
        headerViewHolder.setViews(monthly_bill, total_no_of_items);

    }


    @Override
    public void onBindViewFooterHolder(FooterHolder holder, int position) {

    }

    @Override
    public int getItemsCount() {
        return boxes.size();
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_my_boxes;
    }

    @Override
    protected int getHeaderLayoutId() {
        return R.layout.item_my_boxes_header;
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
        private SearchDetailAdapter userItemRecyclerAdapter;
        private RecyclerView recyclerViewUserItems;
        private MontserratTextView title;

        private LinearLayoutManager verticalLinearLayoutManager;
        private View.OnClickListener openBoxListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String exploreItemString = CoreGsonUtils.toJson(new ExploreItem(boxes.get(getAdapterPosition()).getBoxId(), boxes.get(getAdapterPosition()).getBoxDetail().getTitle()));
                mContext.startActivity(new Intent(mContext, MainActivity.class)
                        .putExtra(MainActivity.EXTRA_ATTACH_FRAGMENT_DATA, exploreItemString)
                        .putExtra(MainActivity.EXTRA_ATTACH_FRAGMENT_NO, 5));
            }
        };

        private View.OnClickListener viewItemsListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyItemChanged(getAdapterPosition());
            }
        };

        public ItemViewHolder(View itemView) {
            super(itemView);
            this.recyclerViewUserItems = (RecyclerView) itemView.findViewById(R.id.useritem_list_recycler_view);
            this.title = (MontserratTextView) itemView.findViewById(R.id.title);
            recyclerViewUserItems.setNestedScrollingEnabled(false);
            recyclerViewUserItems.setItemViewCacheSize(20);
            recyclerViewUserItems.setDrawingCacheEnabled(true);
            recyclerViewUserItems.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

            this.verticalLinearLayoutManager = new LinearLayoutManager(mContext);

        }

        public void setViews(Box box) {
            if (box.getAllItemInTheBox() == null || box.getAllItemInTheBox().isEmpty()) {
                this.recyclerViewUserItems.setVisibility(View.GONE);
                this.title.setVisibility(View.GONE);
            } else {
                this.recyclerViewUserItems.setVisibility(View.VISIBLE);
                this.recyclerViewUserItems.setLayoutManager(verticalLinearLayoutManager);

                this.title.setText(box.getBoxDetail().getTitle());

                this.userItemRecyclerAdapter = new SearchDetailAdapter(mContext);
                this.userItemRecyclerAdapter.setBoxItems(null, box.getAllItemInTheBox());
                this.userItemRecyclerAdapter.addOnUserItemChangeListener(new SearchDetailAdapter.OnUserItemChange() {
                    @Override
                    public void onUserItemChange(RealmList<UserItem> userItems) {
                        boxes.get(getAdapterPosition()).setAllItemsInTheBox(userItems);
                        setViews(boxes.get(getAdapterPosition()));
                    }
                });
                this.recyclerViewUserItems.setAdapter(userItemRecyclerAdapter);
            }
        }
    }

    public class HeaderViewHolder extends BaseRecyclerAdapter.HeaderHolder {

        private MontserratTextView monthly_bill;
        private MontserratTextView total_no_of_items;


        public HeaderViewHolder(View itemView) {
            super(itemView);

            this.monthly_bill = (MontserratTextView) itemView.findViewById(R.id.monthly_bill);
            this.total_no_of_items = (MontserratTextView) itemView.findViewById(R.id.total_no_of_items);


        }

        public void setViews(String monthly_bill, String total_no_of_items) {

            this.monthly_bill.setText(monthly_bill);
            this.total_no_of_items.setText(total_no_of_items);

        }
    }
}



