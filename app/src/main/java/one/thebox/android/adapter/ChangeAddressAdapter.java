package one.thebox.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;

import one.thebox.android.Models.Address;
import one.thebox.android.Models.DeliverySlot;
import one.thebox.android.R;

/**
 * Created by Ajeet Kumar Meena on 15-04-2016.
 */
public class ChangeAddressAdapter extends BaseRecyclerAdapter {

    private ArrayList<Address> addresses = new ArrayList<>();

    public ChangeAddressAdapter(Context context) {
        super(context);
        mViewType = RECYCLER_VIEW_TYPE_FOOTER;
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
        return new FooterViewHolder(view);
    }

    @Override
    public void onBindViewItemHolder(ItemHolder holder, final int position) {
        if(position<0) {
            return;
        }
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
        return R.layout.item_change_address;
    }

    @Override
    protected int getHeaderLayoutId() {
        return 0;
    }

    @Override
    protected int getFooterLayoutId() {
        return R.layout.footer_change_address;
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

    class FooterViewHolder extends FooterHolder {
        private TextView createNewButton;

        public FooterViewHolder(View itemView) {
            super(itemView);
            createNewButton = (TextView) itemView.findViewById(R.id.create_new_button);
            createNewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openAddAddressBottomSheet();
                }
            });
        }

        private void openAddAddressBottomSheet() {
            View bottomSheet = ((Activity) mContext).getLayoutInflater().inflate(R.layout.layout_add_address, null);
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
            bottomSheetDialog.setContentView(bottomSheet);
            bottomSheetDialog.show();
        }
    }
}
