package one.thebox.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

import one.thebox.android.Models.Address;
import one.thebox.android.R;

/**
 * Created by Ajeet Kumar Meena on 15-04-2016.
 */
public class AddressesAdapter extends BaseRecyclerAdapter {

    private ArrayList<Address> addresses = new ArrayList<>();

    public AddressesAdapter(Context context) {
        super(context);
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
        return new ItemAddressViewHolder(view);
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
        return addresses.size();
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_addresses_activity;
    }

    @Override
    protected int getHeaderLayoutId() {
        return 0;
    }

    @Override
    protected int getFooterLayoutId() {
        return 0;
    }

    class ItemAddressViewHolder extends ItemHolder {

        private ImageView editAddressButton;

        public ItemAddressViewHolder(View itemView) {
            super(itemView);
            editAddressButton = (ImageView) itemView.findViewById(R.id.edit_address_button);
            editAddressButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openAddAddressBottomSheet();
                }
            });
        }

        private void openAddAddressBottomSheet() {
            View bottomSheet = ((Activity)mContext).getLayoutInflater().inflate(R.layout.layout_add_address, null);
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
            bottomSheetDialog.setContentView(bottomSheet);
            bottomSheetDialog.show();
        }
    }
}
