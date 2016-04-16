package one.thebox.android.adapter;

import android.content.Context;
import android.view.View;

import java.util.ArrayList;

import one.thebox.android.Models.Address;

/**
 * Created by Ajeet Kumar Meena on 15-04-2016.
 */
public class DeliveryAddressAdapter extends BaseRecyclerAdapter {

    private ArrayList<Address> addresses = new ArrayList<>();

    public DeliveryAddressAdapter(Context context) {
        super(context);
        mViewType = RECYCLER_VIEW_TYPE_HEADER;
    }

    public void addAddress(Address address) {
        addresses.add(address);
    }

    public ArrayList<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(ArrayList<Address> addresses) {
        this.addresses = addresses;
    }

    @Override
    protected ItemHolder getItemHolder(View view) {
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

    }

    @Override
    public void onBindViewHeaderHolder(HeaderHolder holder, int position) {

    }

    @Override
    public void onBindViewFooterHolder(FooterHolder holder, int position) {

    }

    @Override
    public int getItemsCount() {
        return 0;
    }

    @Override
    protected int getItemLayoutId() {
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

    class ItemViewHolder extends ItemHolder {

        public ItemViewHolder(View itemView) {
            super(itemView);
        }
    }

    class HeaderViewHolder extends HeaderHolder {

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }
}
