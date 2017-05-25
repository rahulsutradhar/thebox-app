package one.thebox.android.adapter.checkout;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import one.thebox.android.Models.checkout.ProductDetail;
import one.thebox.android.Models.checkout.PurchaseData;
import one.thebox.android.R;
import one.thebox.android.adapter.base.BaseRecyclerAdapter;

/**
 * Created by developers on 25/05/17.
 */

public class PaymentDetailsAdapter extends BaseRecyclerAdapter {

    private Context context;
    private ArrayList<PurchaseData> purchaseDatas;

    public PaymentDetailsAdapter(Context context, ArrayList<PurchaseData> purchaseDatas) {
        super(context);
        this.context = context;
        this.purchaseDatas = purchaseDatas;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
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
    public void onBindViewItemHolder(ItemHolder holder, int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.setViewHolder(purchaseDatas.get(position));
    }

    @Override
    public void onBindViewHeaderHolder(HeaderHolder holder, int position) {

    }

    @Override
    public void onBindViewFooterHolder(FooterHolder holder, int position) {

    }

    @Override
    public int getItemsCount() {
        return purchaseDatas.size();
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_payment_details;
    }

    @Override
    protected int getItemLayoutId(int position) {
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

    class ItemViewHolder extends BaseRecyclerAdapter.ItemHolder {

        private TextView titleSummary;
        private RecyclerView recyclerView;
        private PaymentProductPriceAdapter paymentProductPriceAdapter;

        public ItemViewHolder(View itemView) {
            super(itemView);
            this.titleSummary = (TextView) itemView.findViewById(R.id.title);
            this.recyclerView = (RecyclerView) itemView.findViewById(R.id.product_price);

        }

        public void setViewHolder(PurchaseData purchaseData) {

            if (purchaseData.getTitle() != null) {
                titleSummary.setText(purchaseData.getTitle());
            }
            setUpRecyclerViewAdapter(purchaseData.getProductDetails());
        }

        public void setUpRecyclerViewAdapter(ArrayList<ProductDetail> productDetails) {

            if (recyclerView != null) {
                final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(linearLayoutManager);
                paymentProductPriceAdapter = new PaymentProductPriceAdapter(context, productDetails);
                recyclerView.setAdapter(paymentProductPriceAdapter);

                recyclerView.setItemViewCacheSize(productDetails.size());
                recyclerView.setDrawingCacheEnabled(true);
                recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);
            }
        }
    }
}
