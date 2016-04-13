package one.thebox.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.cocosw.bottomsheet.BottomSheet;

import java.util.ArrayList;
import java.util.List;

import one.thebox.android.Models.Box;
import one.thebox.android.Models.DeliverySlot;
import one.thebox.android.R;
import one.thebox.android.fragment.SizeAndFrequencyFragment;
import one.thebox.android.util.DisplayUtil;

/**
 * Created by Ajeet Kumar Meena on 11-04-2016.
 */
public class MyBoxRecyclerAdapter extends BaseRecyclerAdapter implements View.OnClickListener {

    private ArrayList<Box> boxes;
    private static FragmentManager childFragmentManager;

    public MyBoxRecyclerAdapter(Context context, FragmentManager childFragmentManager) {
        super(context);
        this.childFragmentManager = childFragmentManager;
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

    @Override
    public void onClick(View v) {

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

    public static class ExpandedListAdapter extends BaseRecyclerAdapter implements View.OnClickListener {

        ArrayList<Box.BoxItem> boxItems;

        public ExpandedListAdapter(Context context, ArrayList<Box.BoxItem> boxItems) {
            super(context);
            this.boxItems = boxItems;
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
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            itemViewHolder.setViews(boxItems.get(position));
        }

        @Override
        public void onBindViewHeaderHolder(HeaderHolder holder, int position) {

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

        @Override
        public void onClick(View v) {

            new BottomSheet.Builder((Activity) mContext).sheet(R.menu.menu_adjust).listener(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case R.id.change_size: {
                            openChangeSizeDialog();
                            break;
                        }
                        case R.id.change_quantity: {
                            break;
                        }
                        case R.id.change_frequency: {
                            break;
                        }
                        case R.id.swap_with_similar_product: {
                            openSwipeBottomSheet();
                            break;
                        }
                        case R.id.delay_delivery: {
                            openDelayDeliveryDialog();
                            break;
                        }
                    }
                }

            }).show();
        }

        private void openDelayDeliveryDialog() {
            View bottomSheet = ((Activity) mContext).getLayoutInflater().inflate(R.layout.layout_bottom_sheet, null);
            DeliverySlotsAdapter deliverySlotsAdapter = new DeliverySlotsAdapter(mContext);
            for (int i = 0; i < 10; i++)
                deliverySlotsAdapter.addDeliveryItems(new DeliverySlot());
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
            RecyclerView recyclerView = (RecyclerView) bottomSheet.findViewById(R.id.recycler_view);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            recyclerView.setAdapter(deliverySlotsAdapter);
            bottomSheetDialog.setContentView(bottomSheet);
            bottomSheetDialog.show();
        }

        private void openSwipeBottomSheet() {
            View bottomSheet = ((Activity) mContext).getLayoutInflater().inflate(R.layout.layout_bottom_sheet, null);
            SwapAdapter swapAdapter = new SwapAdapter(mContext);
            for (int i = 0; i < 10; i++)
                swapAdapter.addBoxItems(new Box.BoxItem());
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
            RecyclerView recyclerView = (RecyclerView) bottomSheet.findViewById(R.id.recycler_view);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            recyclerView.setAdapter(swapAdapter);
            bottomSheetDialog.setContentView(bottomSheet);
            bottomSheetDialog.show();
        }

        private void openChangeSizeDialog() {
            MaterialDialog dialog = new MaterialDialog.Builder(mContext)
                    .title("Change Size and Frequency")
                    .customView(R.layout.layout_change_size_and_frequency, true)
                    .build();
            View customView = dialog.getCustomView();
            if (customView != null) {
                customView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                    }
                });
                TabLayout tabLayout = (TabLayout) customView.findViewById(R.id.tabs);
                ViewPager viewPager = (ViewPager) customView.findViewById(R.id.viewPager);
                ViewPagerAdapter adapter = new ViewPagerAdapter(childFragmentManager);
                adapter.addFragment(new SizeAndFrequencyFragment(), "Monthly");
                adapter.addFragment(new SizeAndFrequencyFragment(), "Twice a Month");
                adapter.addFragment(new SizeAndFrequencyFragment(), "Weekly");
                viewPager.setAdapter(adapter);
                tabLayout.setupWithViewPager(viewPager);
                //viewPager.setVisibility(View.GONE);
                dialog.getWindow().getAttributes().windowAnimations = R.style.MyAnimation_Window;
                dialog.show();
            }
        }


        class ViewPagerAdapter extends FragmentPagerAdapter {
            private final List<Fragment> mFragmentList = new ArrayList<>();
            private final List<String> mFragmentTitleList = new ArrayList<>();

            public ViewPagerAdapter(FragmentManager manager) {
                super(manager);
            }

            @Override
            public Fragment getItem(int position) {
                return mFragmentList.get(position);
            }

            @Override
            public int getCount() {
                return mFragmentList.size();
            }

            public void addFragment(Fragment fragment, String title) {
                mFragmentList.add(fragment);
                mFragmentTitleList.add(title);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mFragmentTitleList.get(position);
            }

            @Override
            public int getItemPosition(Object object) {
                return mFragmentList.indexOf(object);
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
            private TextView adjustButton;

            public ItemViewHolder(View itemView) {
                super(itemView);
                adjustButton = (TextView) itemView.findViewById(R.id.adjust);
            }

            public void setViews(Box.BoxItem boxItem) {
                adjustButton.setOnClickListener(ExpandedListAdapter.this);
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
                this.expandedListAdapter = new ExpandedListAdapter(mContext, box.getBoxItems());
                this.recyclerViewExpandedList.setAdapter(expandedListAdapter);
            } else {
                recyclerViewExpandedList.setVisibility(View.GONE);
            }
        }
    }
}
