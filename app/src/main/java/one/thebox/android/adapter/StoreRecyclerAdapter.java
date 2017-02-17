package one.thebox.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import one.thebox.android.Events.OnCategorySelectEvent;
import one.thebox.android.Models.Box;
import one.thebox.android.Models.Category;
import one.thebox.android.Models.ExploreItem;
import one.thebox.android.Models.UserCategory;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.ShowcaseHelper;
import one.thebox.android.activity.MainActivity;
import one.thebox.android.api.RestClient;
import one.thebox.android.app.MyApplication;
import one.thebox.android.fragment.SearchDetailFragment;
import one.thebox.android.util.CoreGsonUtils;
import one.thebox.android.util.DisplayUtil;
import one.thebox.android.util.PrefUtils;

/**
 * Created by vaibhav on 17/08/16.
 */
public class StoreRecyclerAdapter extends BaseRecyclerAdapter {

    private RealmList<Box> boxes;
    private int stickyHeaderHeight = 0;
    private SparseIntArray boxHeights = new SparseIntArray();

    public StoreRecyclerAdapter(Context context) {
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
        notifyDataSetChanged();
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
        return new ItemHeaderHolder(view);
    }

    @Override
    protected BaseRecyclerAdapter.FooterHolder getFooterHolder(View view) {
        return null;
    }

    @Override
    public void onBindViewItemHolder(final BaseRecyclerAdapter.ItemHolder holder, final int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.setViews(boxes.get(position));

        if (PrefUtils.getBoolean(MyApplication.getInstance(), "home_tutorial", true) && (!RestClient.is_in_development)) {
            new ShowcaseHelper((Activity) mContext, 3)
                    .show("My Boxes", "Edit and keep track of all items being delivered to you regularly", holder.itemView)
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

    public static class RemainingCategoryAdapter extends BaseRecyclerAdapter {

        private List<Category> categories;
        private boolean isSearchDetailItemFragment;
        private RealmList<UserCategory> my_catIds;
        private Box box;
        private Context context;

        public RemainingCategoryAdapter(Context context, List<Category> categories, RealmList<UserCategory> my_catIds) {
            super(context);
            this.categories = categories;
            this.my_catIds = my_catIds;
            this.context = context;
        }

        public RemainingCategoryAdapter(Context context, List<Category> categories) {
            super(context);
            this.categories = categories;
            this.context = context;
        }

        public void setBox(Box box) {
            this.box = box;
        }

        public boolean isSearchDetailItemFragment() {
            return isSearchDetailItemFragment;
        }

        public void setSearchDetailItemFragment(boolean searchDetailItemFragment) {
            isSearchDetailItemFragment = searchDetailItemFragment;
        }

        public List<Category> getCategories() {
            return categories;
        }

        public void setCategories(RealmList<Category> categories) {
            this.categories = categories;
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
            return null;
        }

        @Override
        protected FooterHolder getFooterHolder(View view) {
            return null;
        }

        @Override
        public void onBindViewItemHolder(ItemHolder holder, final int position) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;

            if (position < categories.size()) {
                itemViewHolder.setViewHolder(categories.get(position));
            } else {
                itemViewHolder.setViewHolder(my_catIds.get(position - categories.size()));
            }

            // When used in Search Detail Fragment
            if (isSearchDetailItemFragment) {
                setAnimation(itemViewHolder.itemView);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isSearchDetailItemFragment) {
                        EventBus.getDefault().post(new OnCategorySelectEvent(categories.get(position)));
                    } else {
                        ArrayList<Integer> catIds = new ArrayList<>();
                        for (Category category : categories) {
                            catIds.add(category.getId());
                        }
                        ArrayList<Integer> user_catIds = new ArrayList<>();
                        if (!my_catIds.isEmpty()) {
                            for (UserCategory usercategory : my_catIds) {
                                user_catIds.add(usercategory.getCategory().getId());
                            }
                        }
                        Intent intent = new Intent(mContext, MainActivity.class)
                                .putExtra(MainActivity.EXTRA_ATTACH_FRAGMENT_NO, 6)
                                .putExtra(SearchDetailFragment.EXTRA_MY_BOX_CATEGORIES_ID, CoreGsonUtils.toJson(
                                        catIds))
                                .putExtra(SearchDetailFragment.EXTRA_MY_BOX_USER_CATEGORIES_ID, CoreGsonUtils.toJson(
                                        user_catIds))
                                .putExtra(SearchDetailFragment.EXTRA_CLICK_POSITION, position)
                                .putExtra(SearchDetailFragment.BOX_NAME, box.getBoxDetail().getTitle());
                        mContext.startActivity(intent);
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

        // Animations
        private void setAnimation(View viewToAnimate) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_right);
            viewToAnimate.startAnimation(animation);
        }

        @Override
        public int getItemsCount() {
            if (my_catIds == null) {
                return categories.size();
            } else {
                return (categories.size() + my_catIds.size());
            }
        }

        @Override
        protected int getItemLayoutId() {
            return R.layout.smart_box_item;
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

        public class ItemViewHolder extends ItemHolder {
            private TextView categoryNameTextView, noOfItems;
            private ImageView categoryIcon;

            public ItemViewHolder(View itemView) {
                super(itemView);
                categoryNameTextView = (TextView) itemView.findViewById(R.id.text_view_category_name);
                noOfItems = (TextView) itemView.findViewById(R.id.number_of_item);
                categoryIcon = (ImageView) itemView.findViewById(R.id.icon);
            }

            public void setViewHolder(Category category) {
                if (isSearchDetailItemFragment) {
                    categoryNameTextView.setText(category.getMinititle());
                } else {
                    categoryNameTextView.setText(category.getMinititle());
                }

                Glide.with(mContext)
                        .load(category.getIconUrl())
                        .centerCrop()
                        .crossFade()
                        .into(categoryIcon);

            }

            public void setViewHolder(UserCategory userCategory) {
                categoryNameTextView.setText(userCategory.getCategory().getMinititle());

                noOfItems.setVisibility(View.VISIBLE);

                if (userCategory.getNo_of_items() == 1) {
                    noOfItems.setText("1 item subscribed");
                } else {
                    noOfItems.setText(userCategory.getNo_of_items() + " items subscribed");
                }

                categoryIcon.setMaxWidth(noOfItems.getWidth());

                Glide.with(mContext)
                        .load(userCategory.getCategory().getIconUrl())
                        .centerCrop()
                        .crossFade()
                        .into(categoryIcon);

            }
        }
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
        private RemainingCategoryAdapter remainingCategoryAdapter;
        private SearchDetailAdapter userItemRecyclerAdapter;
        private RecyclerView recyclerViewCategories;
        private TextView title, add_more_items;
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

            this.boxImageView = (ImageView) itemView.findViewById(R.id.box_image_view);
            this.horizontalLinearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
            this.verticalLinearLayoutManager = new LinearLayoutManager(mContext);
            this.add_more_items = (TextView) itemView.findViewById(R.id.add_more_items);
        }

        public void setViews(Box box) {
            this.title.setText(box.getBoxDetail().getTitle().substring(4));
            this.title.setOnClickListener(openBoxListener);
            this.boxImageView.setOnClickListener(openBoxListener);
            this.add_more_items.setOnClickListener(openBoxListener);

//<!-- TODO:- Add how many saving are waiting for him -->
//            if (box.getAllItemInTheBox().size() == 0) {
//                this.subTitle.setText("Suggestions for you");
//            }
//            else if (box.getRemainingCategories() == null || box.getRemainingCategories().isEmpty()) {
//                this.subTitle.setText("All Box Categories Subscribed");
//            }
//            else {
//                this.subTitle.setText("Remaining Categories");
//            }
//
//            if (box.getAllItemInTheBox() == null || box.getAllItemInTheBox().isEmpty()) {
//                viewItems.setText("Add Items");
//                viewItems.setTextColor(mContext.getResources().getColor(R.color.primary_text_color));
//                noOfItemSubscribed.setText("     Empty Box    ");
//                viewItems.setOnClickListener(openBoxListener);
//                noOfItemSubscribed.setOnClickListener(openBoxListener);
//            } else {
//                if (box.isExpandedListVisible()) {
//                    viewItems.setText("Hide My Items");
//                } else {
//                    viewItems.setText("View My Items");
//                }
//                viewItems.setTextColor(mContext.getResources().getColor(R.color.md_green_500));
//                if (box.getAllItemInTheBox().size() == 1) {
//                    noOfItemSubscribed.setText(box.getAllItemInTheBox().size() + " item subscribed");
//                } else {
//                    noOfItemSubscribed.setText(box.getAllItemInTheBox().size() + " items subscribed");
//                }
//                viewItems.setOnClickListener(viewItemsListener);
//                noOfItemSubscribed.setOnClickListener(viewItemsListener);
//            }


            Glide.with(mContext)
                    .load(box.getBoxDetail().getPhotoUrl())
                    .centerCrop()
                    .crossFade()
                    .into(boxImageView);

            this.recyclerViewCategories.setVisibility(View.VISIBLE);
            this.recyclerViewCategories.setLayoutManager(horizontalLinearLayoutManager);
            RealmList<Category> categories = new RealmList<>();
            categories.addAll(box.getRemainingCategories());
            this.remainingCategoryAdapter = new RemainingCategoryAdapter(mContext, categories, box.getUserCategories());
            this.remainingCategoryAdapter.setBox(boxes.get(getAdapterPosition()));
            this.recyclerViewCategories.setAdapter(remainingCategoryAdapter);

        }
    }


}
