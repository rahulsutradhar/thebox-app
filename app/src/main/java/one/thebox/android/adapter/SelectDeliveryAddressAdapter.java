package one.thebox.android.adapter;

import android.content.Context;
import android.view.View;
import android.widget.RadioButton;

import java.util.ArrayList;

import one.thebox.android.Models.Address;
import one.thebox.android.R;

/**
 * Created by Ajeet Kumar Meena on 15-04-2016.
 */
public class SelectDeliveryAddressAdapter extends BaseRecyclerAdapter {

    private ArrayList<Address> addresses = new ArrayList<>();

    public SelectDeliveryAddressAdapter(Context context) {
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
        itemAddressViewHolder.getRadioButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addresses.get(position).setSelected(true);
                notifyItemChanged(position);
                for (int i = 0; i < addresses.size(); i++) {
                    if (addresses.get(i).isSelected() && i != position) {
                        addresses.get(i).setSelected(false);
                        notifyItemChanged(i);
                    }
                }
            }
        });
        itemAddressViewHolder.setViews(addresses.get(position));
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
        return R.layout.item_select_delivery_address;
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

    class ItemAddressViewHolder extends ItemHolder {

        private RadioButton radioButton;


        public ItemAddressViewHolder(View itemView) {
            super(itemView);
            radioButton = (RadioButton) itemView.findViewById(R.id.radio_button);
        }

        public RadioButton getRadioButton() {
            return radioButton;
        }

        public void setRadioButton(RadioButton radioButton) {
            this.radioButton = radioButton;
        }

        public void setViews(Address address) {
            if (address.isSelected()) {
                radioButton.setChecked(true);
            } else {
                radioButton.setChecked(false);
            }
        }
    }
}
