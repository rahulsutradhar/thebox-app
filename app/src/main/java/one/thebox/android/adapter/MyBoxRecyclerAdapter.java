package one.thebox.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import io.realm.RealmList;
import one.thebox.android.Events.OnCategorySelectEvent;
import one.thebox.android.Models.Box;
import one.thebox.android.Models.Category;
import one.thebox.android.Models.SearchResult;
import one.thebox.android.R;
import one.thebox.android.activity.MainActivity;
import one.thebox.android.util.CoreGsonUtils;
import one.thebox.android.util.DisplayUtil;

/**
 * Created by Ajeet Kumar Meena on 11-04-2016.
 */
public class MyBoxRecyclerAdapter extends BaseRecyclerAdapter {

    private RealmList<Box> boxes;

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
        return new ItemHeaderHolder(view);
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
                boxes.get(position).setExpandedListVisible(!boxes.get(position).isExpandedListVisible());
                notifyItemChanged(position);
            }
        });
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.setViews(boxes.get(position));
    }

    @Override
    public void onBindViewHeaderHolder(BaseRecyclerAdapter.HeaderHolder holder, int position) {

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

        private RealmList<Category> categories;
        private boolean isSearchDetailItemFragment;

        public RemainingCategoryAdapter(Context context, RealmList<Category> categories) {
            super(context);
            this.categories = categories;
        }

        public boolean isSearchDetailItemFragment() {
            return isSearchDetailItemFragment;
        }

        public void setSearchDetailItemFragment(boolean searchDetailItemFragment) {
            isSearchDetailItemFragment = searchDetailItemFragment;
        }

        public RealmList<Category> getCategories() {
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
            return new ItemHeaderHolder(view);
        }

        @Override
        protected FooterHolder getFooterHolder(View view) {
            return null;
        }

        @Override
        public void onBindViewItemHolder(ItemHolder holder, final int position) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            itemViewHolder.setViewHolder(categories.get(position));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isSearchDetailItemFragment) {
                        EventBus.getDefault().post(new OnCategorySelectEvent(categories.get(position)));
                    } else {
                        mContext.startActivity(new Intent(mContext, MainActivity.class)
                                .putExtra(MainActivity.EXTRA_ATTACH_FRAGMENT_NO, 4)
                                .putExtra(MainActivity.EXTRA_ATTACH_FRAGMENT_DATA, CoreGsonUtils.toJson(
                                        new SearchResult(categories.get(position).getId(), categories.get(position).getTitle()))));
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
            return categories.size();
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
                categoryNameTextView.setText(category.getTitle());
                noOfItems.setText(category.getNoOfItems() + " Items");
                Picasso.with(mContext).load(category.getIconUrl()).into(categoryIcon);
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
        private UserItemRecyclerAdapter userItemRecyclerAdapter;
        private RecyclerView recyclerViewCategories;
        private RecyclerView recyclerViewUserItems;
        private TextView title, subTitle, savings;
        private ImageView boxImageView;
        private LinearLayoutManager horizontalLinearLayoutManager;
        private LinearLayoutManager verticalLinearLayoutManager;
        private LinearLayout emptyBoxLayout;

        public ItemViewHolder(View itemView) {
            super(itemView);
            this.recyclerViewCategories = (RecyclerView) itemView.findViewById(R.id.relatedCategories);
            this.recyclerViewUserItems = (RecyclerView) itemView.findViewById(R.id.expanded_list_recycler_view);
            this.title = (TextView) itemView.findViewById(R.id.title);
            this.subTitle = (TextView) itemView.findViewById(R.id.sub_title);
            this.savings = (TextView) itemView.findViewById(R.id.saving_text_view);
            this.boxImageView = (ImageView) itemView.findViewById(R.id.box_image_view);
            this.horizontalLinearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
            this.verticalLinearLayoutManager = new LinearLayoutManager(mContext);
            this.emptyBoxLayout = (LinearLayout) itemView.findViewById(R.id.empty_box_holder);
        }

        public void setViews(Box box) {
            if (box.getBoxDetail().getTitle() != null)
                this.title.setText(box.getBoxDetail().getTitle());
            if (box.getAllItemInTheBox() == null || box.getAllItemInTheBox().isEmpty()) {
                this.subTitle.setText("Empty Box");
            } else {
                this.subTitle.setText(box.getSubTitle());
            }
            Picasso.with(mContext).load(box.getBoxDetail().getPhotoUrl()).into(boxImageView);
            if (box.getRemainingCategories() == null || box.getRemainingCategories().isEmpty()) {
                this.recyclerViewCategories.setVisibility(View.GONE);
            } else {
                this.recyclerViewCategories.setVisibility(View.VISIBLE);
                this.recyclerViewCategories.setLayoutManager(horizontalLinearLayoutManager);
                RealmList<Category> categories = new RealmList<>();
                categories.addAll(box.getRemainingCategories());
                this.remainingCategoryAdapter = new RemainingCategoryAdapter(mContext, categories);
                this.recyclerViewCategories.setAdapter(remainingCategoryAdapter);
            }

            if (box.isExpandedListVisible()) {
                if (box.getAllItemInTheBox() == null || box.getAllItemInTheBox().isEmpty()) {
                    this.recyclerViewUserItems.setVisibility(View.GONE);
                    this.emptyBoxLayout.setVisibility(View.VISIBLE);
                } else {
                    this.recyclerViewUserItems.setVisibility(View.VISIBLE);
                    this.emptyBoxLayout.setVisibility(View.GONE);
                    this.recyclerViewUserItems.setLayoutManager(verticalLinearLayoutManager);
                    this.userItemRecyclerAdapter = new UserItemRecyclerAdapter(mContext, box.getAllItemInTheBox(), false);
                    this.recyclerViewUserItems.setAdapter(userItemRecyclerAdapter);
                }

            } else {
                this.recyclerViewUserItems.setVisibility(View.GONE);
                this.emptyBoxLayout.setVisibility(View.GONE);
            }
        }
    }
}
