package one.thebox.android.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import java.util.ArrayList;

import one.thebox.android.Models.Box;
import one.thebox.android.R;
import one.thebox.android.util.DisplayUtil;

/**
 * Created by Ajeet Kumar Meena on 11-04-2016.
 */
public class MyBoxRecyclerAdapter extends BaseRecyclerAdapter {

    ArrayList<Box> boxes;

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
    protected int getFooterLayoutId() {
        return 0;
    }

    public static class SmartItemAdapter extends BaseRecyclerAdapter {

        ArrayList<Box.SmartItem> smartItems;

        public SmartItemAdapter(Context context, ArrayList<Box.SmartItem> smartItems) {
            super(context);
            this.smartItems = smartItems;
        }

        public ArrayList<Box.SmartItem> getSmartItems() {
            return smartItems;
        }

        public void setSmartItems(ArrayList<Box.SmartItem> smartItems) {
            this.smartItems = smartItems;
        }

        @Override
        protected ItemHolder getItemHolder(View view) {
            return new ItemViewHolder(view);
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
            return smartItems.size();
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

    public static class ExpandedListAdapter extends BaseRecyclerAdapter {

        ArrayList<Box.ExpandedListItem> expandedListItems;

        public ExpandedListAdapter(Context context, ArrayList<Box.ExpandedListItem> expandedListItems) {
            super(context);
            this.expandedListItems = expandedListItems;
        }

        @Override
        protected ItemHolder getItemHolder(View view) {
            return new ItemViewHolder(view);
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
            return expandedListItems.size();
        }

        @Override
        protected int getItemLayoutId() {
            return R.layout.item_expanded_list;
        }

        @Override
        protected int getHeaderLayoutId() {
            return R.layout.empty_space_header;
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

    public class ItemViewHolder extends ItemHolder {
        private MyBoxRecyclerAdapter.SmartItemAdapter smartItemAdapter;
        private MyBoxRecyclerAdapter.ExpandedListAdapter expandedListAdapter;
        private RecyclerView recyclerViewSmartItems;
        private RecyclerView recyclerViewExpandedList;
        private LinearLayoutManager horizontalLinearLayoutManager;
        private LinearLayoutManager verticalLinearLayoutManager;

        public ItemViewHolder(View itemView) {
            super(itemView);
            this.recyclerViewSmartItems = (RecyclerView) itemView.findViewById(R.id.smart_item_recycler_view);
            this.recyclerViewExpandedList = (RecyclerView) itemView.findViewById(R.id.expanded_list_recycler_view);
            this.horizontalLinearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
            this.verticalLinearLayoutManager = new LinearLayoutManager(mContext);
        }

        public void setViews(Box box) {
            this.recyclerViewSmartItems.setLayoutManager(horizontalLinearLayoutManager);
            this.smartItemAdapter = new MyBoxRecyclerAdapter.SmartItemAdapter(mContext, box.getSmartItems());
            this.recyclerViewSmartItems.setAdapter(smartItemAdapter);
            if (box.isExpandedListVisible()) {
                recyclerViewExpandedList.setVisibility(View.VISIBLE);
                this.recyclerViewExpandedList.setLayoutManager(verticalLinearLayoutManager);
                this.expandedListAdapter = new ExpandedListAdapter(mContext, box.getExpandedListItems());
                this.recyclerViewExpandedList.setAdapter(expandedListAdapter);
            } else {
                recyclerViewExpandedList.setVisibility(View.GONE);
            }
        }
    }
}
