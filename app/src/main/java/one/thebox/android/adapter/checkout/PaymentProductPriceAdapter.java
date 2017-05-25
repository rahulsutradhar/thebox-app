package one.thebox.android.adapter.checkout;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import one.thebox.android.Models.checkout.ProductDetail;
import one.thebox.android.R;
import one.thebox.android.adapter.base.BaseRecyclerAdapter;
import one.thebox.android.app.TheBox;

/**
 * Created by developers on 25/05/17.
 */

public class PaymentProductPriceAdapter extends BaseRecyclerAdapter {

    private Context context;
    private ArrayList<ProductDetail> productDetails;


    public PaymentProductPriceAdapter(Context context, ArrayList<ProductDetail> productDetails) {
        super(context);
        this.context = context;
        this.productDetails = productDetails;
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
        itemViewHolder.setViewHolder(productDetails.get(position));
    }

    @Override
    public void onBindViewHeaderHolder(HeaderHolder holder, int position) {

    }

    @Override
    public void onBindViewFooterHolder(FooterHolder holder, int position) {

    }

    @Override
    public int getItemsCount() {
        return productDetails.size();
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_payment_product_price;
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

        private TextView name;
        private TextView price;

        public ItemViewHolder(View itemView) {
            super(itemView);
            this.name = (TextView) itemView.findViewById(R.id.item_text);
            this.price = (TextView) itemView.findViewById(R.id.amount_text);
        }

        public void setViewHolder(ProductDetail productDetail) {

            if (productDetail.getName() != null) {
                name.setText(productDetail.getName());
            }

            if (productDetail.getTotalPrice() != null) {
                price.setText(productDetail.getTotalPrice());
                if (productDetail.getTotalPrice().contains("Free")) {
                    price.setTextColor(context.getResources().getColor(R.color.md_green_500));
                } else {
                    price.setTextColor(context.getResources().getColor(R.color.accent));
                }
            }

        }
    }
}
