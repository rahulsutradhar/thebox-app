package one.thebox.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;

import one.thebox.android.Models.User;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.AddressBottomSheet;
import one.thebox.android.util.PrefUtils;

/**
 * Created by Ajeet Kumar Meena on 15-04-2016.
 */
public class SelectDeliveryAddressAdapter extends BaseRecyclerAdapter {

    private int currentSelection;

    private ArrayList<User.Address> addresses = new ArrayList<>();

    private OnAddressSelectListener onAddressSelectListener;

    public SelectDeliveryAddressAdapter(Context context, ArrayList<User.Address> addresses) {
        super(context);
        mViewType = RECYCLER_VIEW_TYPE_FOOTER;
        this.addresses = addresses;
        for (int i = 0; i < addresses.size(); i++) {
            if (addresses.get(i).isCurrentAddress()) {
                currentSelection = i;
                break;
            }
        }
    }

    public int getCurrentSelection() {
        return currentSelection;
    }

    public void setCurrentSelection(int currentSelection) {
        this.currentSelection = currentSelection;
    }

    public SelectDeliveryAddressAdapter(Context context, ArrayList<User.Address> addresses, OnAddressSelectListener onAddressSelectListener) {
        super(context);
        mViewType = RECYCLER_VIEW_TYPE_FOOTER;
        this.addresses = addresses;
        for (int i = 0; i < addresses.size(); i++) {
            if (addresses.get(i).isCurrentAddress()) {
                currentSelection = i;
                break;
            }
        }
        this.onAddressSelectListener = onAddressSelectListener;
    }

    public void addAddress(User.Address address) {
        addresses.add(address);
    }

    public ArrayList<User.Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(ArrayList<User.Address> addresses) {
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
        return new FooterViewHolder(view);
    }

    @Override
    public void onBindViewItemHolder(ItemHolder holder, final int position) {
        ItemAddressViewHolder itemAddressViewHolder = (ItemAddressViewHolder) holder;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int temp = currentSelection;
                currentSelection = position;
                notifyItemChanged(position);
                notifyItemChanged(temp);
                if (onAddressSelectListener != null)
                    onAddressSelectListener.onAddressSelect(addresses.get(position));
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
        return R.layout.item_change_address;
    }

    @Override
    protected int getHeaderLayoutId() {
        return 0;
    }

    @Override
    protected int getItemLayoutId(int position) {
        return 0;
    }

    class ItemAddressViewHolder extends ItemHolder {

        private RadioButton radioButton;
        private TextView label, address;

        public ItemAddressViewHolder(View itemView) {
            super(itemView);
            radioButton = (RadioButton) itemView.findViewById(R.id.radio_button);
            label = (TextView) itemView.findViewById(R.id.text_view_address_label);
            address = (TextView) itemView.findViewById(R.id.text_view_address);
        }

        public RadioButton getRadioButton() {
            return radioButton;
        }

        public void setRadioButton(RadioButton radioButton) {
            this.radioButton = radioButton;
        }

        public void setViews(User.Address address) {
            this.address.setText(address.getFlat() + ", " + address.getStreet() + ", " + address.getSociety());
            if (address.isCurrentAddress()) {
                label.setText(address.getLabel() + " (primary)");
            } else {
                label.setText(address.getLabel());
            }
            if (getAdapterPosition() == currentSelection) {
                radioButton.setChecked(true);
            } else {
                radioButton.setChecked(false);
            }
        }
    }

    @Override
    protected int getFooterLayoutId() {
        return R.layout.footer_change_address;
    }

    class FooterViewHolder extends FooterHolder {
        private TextView editAddressButton;

        public FooterViewHolder(View itemView) {
            super(itemView);
            editAddressButton = (TextView) itemView.findViewById(R.id.create_new_button);
            editAddressButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AddressBottomSheet((Activity) mContext, new AddressBottomSheet.OnAddressAdded() {
                        @Override
                        public void onAddressAdded(User.Address address) {
                            User user = PrefUtils.getUser(mContext);
                            ArrayList<User.Address> addresses = user.getAddresses();
                            if (addresses == null || addresses.isEmpty()) {
                                addresses = new ArrayList<>();
                            }
                            addresses.add(address);
                            user.setAddresses(addresses);
                            SelectDeliveryAddressAdapter.this.setAddresses(addresses);
                            notifyDataSetChanged();
                            PrefUtils.saveUser(mContext, user);
                        }
                    }).show();
                }
            });
        }
    }

    public interface OnAddressSelectListener {
        void onAddressSelect(User.Address address);
    }
}
