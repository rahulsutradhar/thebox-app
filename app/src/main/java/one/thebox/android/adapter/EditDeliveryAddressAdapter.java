package one.thebox.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import io.realm.RealmList;
import one.thebox.android.Models.Address;
import one.thebox.android.Models.AddressAndOrder;
import one.thebox.android.Models.Order;
import one.thebox.android.Models.User;
import one.thebox.android.R;
import one.thebox.android.util.PrefUtils;

/**
 * Created by Ajeet Kumar Meena on 15-04-2016.
 */
public class EditDeliveryAddressAdapter extends BaseRecyclerAdapter {

    private RealmList<Order> orders = new RealmList<>();
    private ArrayList<AddressAndOrder> addressAndOrders = new ArrayList<>();
    private Address primaryAddress;
    private User user;
    private RealmList<Address> addresses;

    public EditDeliveryAddressAdapter(Context context, RealmList<Order> orders) {
        super(context);
        user = PrefUtils.getUser(mContext);
        addresses = user.getAddresses();
        for (Address address : addresses) {
            if (address.isCurrentAddress()) {
                primaryAddress = address;
                break;
            }
        }
        if(primaryAddress == null) {
            primaryAddress = addresses.get(0);
        }
        this.orders = orders;
        for (Order order : orders) {
            addressAndOrders.add(new AddressAndOrder(primaryAddress.getId(), order.getId()));
        }
    }

    public RealmList<Order> getOrders() {
        return orders;
    }

    public void setOrders(RealmList<Order> orders) {
        this.orders = orders;
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
    public void onBindViewItemHolder(ItemHolder holder, int position) {
        ItemAddressViewHolder itemAddressViewHolder = (ItemAddressViewHolder) holder;
        itemAddressViewHolder.setViews(addressAndOrders.get(position).getOrder(), addressAndOrders.get(position).getAddress());
    }

    @Override
    public void onBindViewHeaderHolder(HeaderHolder holder, int position) {

    }

    @Override
    public void onBindViewFooterHolder(FooterHolder holder, int position) {

    }

    @Override
    public int getItemsCount() {
        return orders.size();
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_edit_delivery_address;
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

    private void openAddressChangeBottomSheet(final int position) {
        final View bottomSheet = ((Activity) mContext).getLayoutInflater().inflate(R.layout.layout_change_address, null);
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
        bottomSheetDialog.setContentView(bottomSheet);
        bottomSheetDialog.show();
        RecyclerView recyclerView = (RecyclerView) bottomSheet.findViewById(R.id.recycler_view);
        SelectDeliveryAddressAdapter selectDeliveryAddressAdapter = new SelectDeliveryAddressAdapter(mContext, addresses, new SelectDeliveryAddressAdapter.OnAddressSelectListener() {
            @Override
            public void onAddressSelect(Address address) {
                bottomSheetDialog.dismiss();
                addressAndOrders.get(position).setAddressId(address.getId());
                notifyItemChanged(position);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(selectDeliveryAddressAdapter);
    }

    public ArrayList<AddressAndOrder> getAddressAndOrders() {
        return addressAndOrders;
    }

    public void setAddressAndOrders(ArrayList<AddressAndOrder> addressAndOrders) {
        this.addressAndOrders = addressAndOrders;
    }

    class ItemAddressViewHolder extends ItemHolder {

        private TextView changeAddressButton, itemText, orderNoText, addressText;

        public ItemAddressViewHolder(View itemView) {
            super(itemView);
            changeAddressButton = (TextView) itemView.findViewById(R.id.change_button);
            itemText = (TextView) itemView.findViewById(R.id.text_view_item);
            orderNoText = (TextView) itemView.findViewById(R.id.text_view_order_no);
            addressText = (TextView) itemView.findViewById(R.id.text_view_address);
            changeAddressButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openAddressChangeBottomSheet(getAdapterPosition());
                }
            });
        }

        public void setViews(Order order, Address address) {
            itemText.setText(order.getItemString());
            orderNoText.setVisibility(View.GONE);
            if (address.isCurrentAddress()) {
                addressText.setText(address.getFlat() + ", " + address.getStreet() + ", " + address.getSociety() + " (primary)");
            } else {
                addressText.setText(address.getFlat() + ", " + address.getStreet() + ", " + address.getSociety());
            }
        }
    }
}
