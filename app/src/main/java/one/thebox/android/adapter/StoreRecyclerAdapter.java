package one.thebox.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import one.thebox.android.Events.OnCategorySelectEvent;
import one.thebox.android.Models.Box;
import one.thebox.android.Models.Category;
import one.thebox.android.Models.ExploreItem;
import one.thebox.android.Models.UserCategory;
import one.thebox.android.Models.carousel.Offer;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.ShowcaseHelper;
import one.thebox.android.activity.MainActivity;
import one.thebox.android.adapter.base.BaseRecyclerAdapter;
import one.thebox.android.adapter.carousel.AdapterCarousel;
import one.thebox.android.api.RestClient;
import one.thebox.android.app.TheBox;
import one.thebox.android.fragment.SearchDetailFragment;
import one.thebox.android.util.CoreGsonUtils;
import one.thebox.android.util.PrefUtils;

/**
 * Created by vaibhav on 17/08/16.
 */
public class StoreRecyclerAdapter extends BaseRecyclerAdapter {

    public static final int RECYCLER_VIEW_TYPE_NORMAL = 300;
    private RealmList<Box> boxes;
    private ArrayList<Offer> carousel;
    private int stickyHeaderHeight = 0;
    private Context mContext;

    /**
     * Glide Request Manager
     */
    private RequestManager glideRequestManager;

    public StoreRecyclerAdapter(Context context, RequestManager glideRequestManager) {
        super(context);
        this.mContext = context;
        this.glideRequestManager = glideRequestManager;
    }


    public RealmList<Box> getBoxes() {
        return boxes;
    }

    public void setBoxes(RealmList<Box> boxes) {
        this.boxes = boxes;
        notifyDataSetChanged();
    }

    public ArrayList<Offer> getCarousel() {
        return carousel;
    }

    public void setCarousel(ArrayList<Offer> carousel) {
        this.carousel = carousel;
    }

    public int getStickyHeaderHeight() {
        return stickyHeaderHeight;
    }

    public void setStickyHeaderHeight(int stickyHeaderHeight) {
        this.stickyHeaderHeight = stickyHeaderHeight;
    }

    @Override
    protected BaseRecyclerAdapter.ItemHolder getItemHolder(View view) {
        return new ItemViewHolder(view);
    }

    @Override
    protected BaseRecyclerAdapter.ItemHolder getItemHolder(View view, int position) {
        return null;
    }

    @Override
    protected BaseRecyclerAdapter.HeaderHolder getHeaderHolder(View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    protected BaseRecyclerAdapter.FooterHolder getFooterHolder(View view) {
        return null;
    }

    @Override
    public void onBindViewItemHolder(final BaseRecyclerAdapter.ItemHolder holder, final int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.setViews(boxes.get(position), position);

        if (PrefUtils.getBoolean(TheBox.getInstance(), "home_tutorial", true) && (!RestClient.is_in_development)) {
            new ShowcaseHelper((Activity) mContext, 3)
                    .show("My Boxes", "Edit and keep track of all items being delivered to you regularly", holder.itemView)
                    .setOnCompleteListener(new ShowcaseHelper.OnCompleteListener() {
                        @Override
                        public void onComplete() {
                            PrefUtils.putBoolean(TheBox.getInstance(), "home_tutorial", false);
                            new ShowcaseHelper((Activity) mContext, 3)
                                    .show("My Boxes", "Edit and keep track of all items being delivered to you regularly", holder.itemView);
                        }
                    });
        }
    }

    @Override
    public void onBindViewHeaderHolder(BaseRecyclerAdapter.HeaderHolder holder, int position) {
        HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
        headerViewHolder.setViews(carousel);
    }


    @Override
    public void onBindViewFooterHolder(BaseRecyclerAdapter.FooterHolder holder, int position) {

    }

    @Override
    public int getItemsCount() {
        return boxes.size();
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_store;
    }

    @Override
    protected int getHeaderLayoutId() {
        return R.layout.carousel_banner_header;
    }

    @Override
    protected int getItemLayoutId(int position) {
        return 0;
    }

    @Override
    protected int getFooterLayoutId() {
        return 0;
    }

    public class HeaderViewHolder extends BaseRecyclerAdapter.HeaderHolder {

        private RecyclerView recyclerView;
        private AdapterCarousel adapterCarousel;
        private LinearLayoutManager linearLayoutManager;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            this.recyclerView = (RecyclerView) itemView.findViewById(R.id.carousel_list);
        }

        public void setViews(final ArrayList<Offer> carousel) {
            if (adapterCarousel == null || null == recyclerView.getAdapter()) {
                linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
                this.recyclerView.setLayoutManager(linearLayoutManager);
                adapterCarousel = new AdapterCarousel(mContext, glideRequestManager);
                adapterCarousel.setViewType(RECYCLER_VIEW_TYPE_NORMAL);
                adapterCarousel.setCarousel(carousel);
                this.recyclerView.setAdapter(adapterCarousel);
                SnapHelper snapHelper = new PagerSnapHelper();
                snapHelper.attachToRecyclerView(recyclerView);
                linearLayoutManager.scrollToPosition(100);

            } else {
                adapterCarousel.setViewType(RECYCLER_VIEW_TYPE_NORMAL);
                adapterCarousel.setCarousel(carousel);
                linearLayoutManager.scrollToPosition(100);
            }

        }
    }

    public class ItemViewHolder extends BaseRecyclerAdapter.ItemHolder {
        private RemainingCategoryAdapter remainingCategoryAdapter;
        private SearchDetailAdapter userItemRecyclerAdapter;
        private RecyclerView recyclerViewCategories;
        private TextView title, add_more_items, savingsTitle;
        private ImageView boxImageView;
        private LinearLayoutManager horizontalLinearLayoutManager;
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
            this.recyclerViewCategories = (RecyclerView) itemView.findViewById(R.id.relatedCategories);

            recyclerViewCategories.setItemViewCacheSize(20);
            recyclerViewCategories.setDrawingCacheEnabled(true);
            recyclerViewCategories.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            recyclerViewCategories.setNestedScrollingEnabled(false);

            this.title = (TextView) itemView.findViewById(R.id.title);
            this.savingsTitle = (TextView) itemView.findViewById(R.id.text_view_savings);

            this.boxImageView = (ImageView) itemView.findViewById(R.id.box_image_view);
            this.horizontalLinearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
            this.verticalLinearLayoutManager = new LinearLayoutManager(mContext);
            this.add_more_items = (TextView) itemView.findViewById(R.id.add_more_items);
        }

        public void setViews(Box box, int position) {
            this.title.setText(box.getTitle());
            this.title.setOnClickListener(openBoxListener);
            this.boxImageView.setOnClickListener(openBoxListener);
            this.add_more_items.setOnClickListener(openBoxListener);

            //savings
            if (box.getSavingTitle() != null) {
                if (!box.getSavingTitle().isEmpty()) {
                    this.savingsTitle.setVisibility(View.VISIBLE);
                    this.savingsTitle.setText(box.getSavingTitle());
                } else {
                    this.savingsTitle.setText("");
                    this.savingsTitle.setVisibility(View.GONE);
                }
            } else {
                this.savingsTitle.setText("");
                this.savingsTitle.setVisibility(View.GONE);
            }

            glideRequestManager.load(box.getBoxImage())
                    .centerCrop()
                    .crossFade()
                    .into(boxImageView);

            this.recyclerViewCategories.setVisibility(View.VISIBLE);
            this.recyclerViewCategories.setLayoutManager(horizontalLinearLayoutManager);
            if (box.getCategories() != null) {
                this.remainingCategoryAdapter = new RemainingCategoryAdapter(mContext, box.getCategories(), glideRequestManager);
                this.remainingCategoryAdapter.setBox(boxes.get(position));
                this.recyclerViewCategories.setAdapter(remainingCategoryAdapter);
            }

        }
    }


}
