package one.thebox.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cocosw.bottomsheet.BottomSheet;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import one.thebox.android.Models.Box;
import one.thebox.android.Models.BoxItem;
import one.thebox.android.Models.Category;
import one.thebox.android.Models.DeliverySlot;
import one.thebox.android.Models.UserCategory;
import one.thebox.android.Models.UserItem;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.ChangeSizeDialogViewHelper;
import one.thebox.android.util.DisplayUtil;
import one.thebox.android.util.NumberWordConverter;

/**
 * Created by Ajeet Kumar Meena on 11-04-2016.
 */
public class MyBoxRecyclerAdapter extends BaseRecyclerAdapter implements View.OnClickListener {

    private ArrayList<Box> boxes;


    public MyBoxRecyclerAdapter(Context context) {
        super(context);
    }

    public void addBox(Box box) {
        boxes.add(box);
    }

    public ArrayList<Box> getBoxes() {
        return boxes;
    }

    public void setBoxes(ArrayList<Box> boxes) {
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

    @Override
    public void onClick(View v) {

    }

    public static class RemainingCategoryAdapter extends BaseRecyclerAdapter {

        ArrayList<Category> categories;

        public RemainingCategoryAdapter(Context context, ArrayList<Category> categories) {
            super(context);
            this.categories = categories;
        }

        public ArrayList<Category> getCategories() {
            return categories;
        }

        public void setCategories(ArrayList<Category> categories) {
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
            public ItemViewHolder(View itemView) {
                super(itemView);
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

        public ItemViewHolder(View itemView) {
            super(itemView);
            this.recyclerViewCategories = (RecyclerView) itemView.findViewById(R.id.smart_item_recycler_view);
            this.recyclerViewUserItems = (RecyclerView) itemView.findViewById(R.id.expanded_list_recycler_view);
            this.title = (TextView) itemView.findViewById(R.id.title);
            this.subTitle = (TextView) itemView.findViewById(R.id.sub_title);
            this.savings = (TextView) itemView.findViewById(R.id.saving_text_view);
            this.boxImageView = (ImageView) itemView.findViewById(R.id.box_image_view);
            this.horizontalLinearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
            this.verticalLinearLayoutManager = new LinearLayoutManager(mContext);
        }

        public void setViews(Box box) {
            this.title.setText(box.getBoxDetail().getTitle());
            this.subTitle.setText(box.getSubTitle());
            Picasso.with(mContext).load(box.getBoxDetail().getPhotoUrl()).into(boxImageView);
            this.recyclerViewCategories.setLayoutManager(horizontalLinearLayoutManager);
            this.remainingCategoryAdapter = new RemainingCategoryAdapter(mContext, new ArrayList<>(box.getRemainingCategories()));
            this.recyclerViewCategories.setAdapter(remainingCategoryAdapter);
            if (box.isExpandedListVisible()) {
                recyclerViewUserItems.setVisibility(View.VISIBLE);
                this.recyclerViewUserItems.setLayoutManager(verticalLinearLayoutManager);
                this.userItemRecyclerAdapter = new UserItemRecyclerAdapter(mContext, box.getAllItemInTheBox());
                this.recyclerViewUserItems.setAdapter(userItemRecyclerAdapter);
            } else {
                recyclerViewUserItems.setVisibility(View.GONE);
            }
        }
    }
}
