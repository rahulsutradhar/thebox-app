package one.thebox.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import io.realm.RealmList;
import one.thebox.android.Models.Address;
import one.thebox.android.Models.User;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.AddressBottomSheet;
import one.thebox.android.util.PrefUtils;

/**
 * Created by Ajeet Kumar Meena on 15-04-2016.
 */
public class AddressesAdapter extends BaseRecyclerAdapter {

    private RealmList<Address> addresses = new RealmList<>();

    public AddressesAdapter(Context context) {
        super(context);
    }

    public void addAddress(Address address) {
        addresses.add(address);
    }

    public RealmList<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(RealmList<Address> addresses) {
        this.addresses = addresses;
    }

    @Override
    protected ItemHolder getItemHolder(View view) {
        return new ItemAddressViewHolder(view);
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
        ItemAddressViewHolder itemAddressViewHolder = (ItemAddressViewHolder) holder;
        itemAddressViewHolder.setView(addresses.get(position));
        itemAddressViewHolder.editAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddAddressBottomSheet(addresses.get(position), position);
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
    protected int getItemLayoutId(int position) {
        return 0;
    }

    @Override
    protected int getFooterLayoutId() {
        return 0;
    }

    private void openAddAddressBottomSheet(Address address, final int position) {
        new AddressBottomSheet((Activity) mContext, new AddressBottomSheet.OnAddressAdded() {
            @Override
            public void onAddressAdded(Address address) {
                if (address.isCurrentAddress()) {
                    for (int i = 0; i < addresses.size(); i++) {
                        if (addresses.get(i).isCurrentAddress() && i != position) {
                            addresses.get(i).setCurrentAddress(false);
                            notifyItemChanged(i);
                        }
                    }
                }
                addresses.set(position, address);
                User user = PrefUtils.getUser(mContext);
                user.setAddresses(addresses);
                PrefUtils.saveUser(mContext, user);
                notifyItemChanged(position);
            }
        }, address).show();
    }

    class ItemAddressViewHolder extends ItemHolder {
        private TextView typeNameTextView, addressTextView;
        private ImageView editAddressButton;

        public ItemAddressViewHolder(View itemView) {
            super(itemView);
            editAddressButton = (ImageView) itemView.findViewById(R.id.edit_address_button);
            typeNameTextView = (TextView) itemView.findViewById(R.id.type_name_text_view);
            addressTextView = (TextView) itemView.findViewById(R.id.address_text_view);
        }

        public void setView(Address address) {
            if (address.isCurrentAddress()) {
                typeNameTextView.setText(Address.getAddressTypeName(address.getLabel()) + " (primary)");
                addressTextView.setText(address.getFlat() + ", " + address.getStreet());
            } else {
                typeNameTextView.setText(Address.getAddressTypeName(address.getLabel()));
                addressTextView.setText(address.getFlat() + ", " + address.getStreet());
            }

        }
    }
}
