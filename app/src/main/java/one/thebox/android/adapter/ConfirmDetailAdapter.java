package one.thebox.android.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import one.thebox.android.Models.BillItem;
import one.thebox.android.R;

/**
 * Created by Ajeet Kumar Meena on 15-04-2016.
 */
public class ConfirmDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_HOLDER_PAYMENT_DETAIL = 1;
    private static final int VIEW_HOLDER_DELIVERY_ADDRESS = 2;
    private static final int VIEW_HOLDER_TIME_SLOTS = 3;
    private Context context;

    public ConfirmDetailAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_HOLDER_PAYMENT_DETAIL: {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_payment_detail, parent, false);
                return new PaymentDetailViewHolder(itemView);
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {

        }
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_HOLDER_PAYMENT_DETAIL;
        }
        return super.getItemViewType(position);
    }

    public class PaymentDetailViewHolder extends RecyclerView.ViewHolder {
        private RecyclerView recyclerView;
        private PaymentDetailAdapter paymentDetailAdapter;

        public PaymentDetailViewHolder(View itemView) {
            super(itemView);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.recycler_view);
            paymentDetailAdapter = new PaymentDetailAdapter(context);
            for (int i = 0; i < 10; i++)
                paymentDetailAdapter.addSubBillItem(new BillItem.SubBillItem());
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setAdapter(paymentDetailAdapter);
            recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
                @Override
                public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                    int action = e.getAction();
                    switch (action) {
                        case MotionEvent.ACTION_MOVE:
                            rv.getParent().requestDisallowInterceptTouchEvent(true);
                            break;
                    }
                    return false;
                }

                @Override
                public void onTouchEvent(RecyclerView rv, MotionEvent e) {

                }

                @Override
                public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

                }
            });
        }
    }

    class PaymentDetailAdapter extends BaseRecyclerAdapter {

        private ArrayList<BillItem.SubBillItem> subBillItems = new ArrayList<>();

        public PaymentDetailAdapter(Context context) {
            super(context);
            mViewType = RECYCLER_VIEW_TYPE_FOOTER;
        }

        public void addSubBillItem(BillItem.SubBillItem subBillItem) {
            subBillItems.add(subBillItem);
        }

        public ArrayList<BillItem.SubBillItem> getSubBillItems() {
            return subBillItems;
        }

        public void setSubBillItems(ArrayList<BillItem.SubBillItem> subBillItems) {
            this.subBillItems = subBillItems;
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
            return new FooterViewHolder(view);
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
            return subBillItems.size();
        }

        @Override
        protected int getItemLayoutId() {
            return R.layout.item_payment_detail;
        }

        @Override
        protected int getHeaderLayoutId() {
            return 0;
        }

        @Override
        protected int getFooterLayoutId() {
            return R.layout.footer_payment_detail;
        }

        class ItemViewHolder extends ItemHolder {

            public ItemViewHolder(View itemView) {
                super(itemView);
            }
        }

        class FooterViewHolder extends FooterHolder {

            public FooterViewHolder(View itemView) {
                super(itemView);
            }
        }
    }


}
