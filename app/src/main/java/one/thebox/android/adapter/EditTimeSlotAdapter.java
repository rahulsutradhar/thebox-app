package one.thebox.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import one.thebox.android.Models.AddressAndOrder;
import one.thebox.android.Models.Order;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.TimeSlotBottomSheet;

/**
 * Created by Ajeet Kumar Meena on 30-04-2016.
 */
public class EditTimeSlotAdapter extends BaseRecyclerAdapter {

    private ArrayList<AddressAndOrder> addressAndOrders;
    private boolean isCart;

    public EditTimeSlotAdapter(Context context, boolean isCart) {
        super(context);
    }

    public ArrayList<AddressAndOrder> getAddressAndOrders() {
        return addressAndOrders;
    }

    public void setAddressAndOrders(ArrayList<AddressAndOrder> addressAndOrders) {
        this.addressAndOrders = addressAndOrders;
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
    public void onBindViewItemHolder(ItemHolder holder, final int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.setViewHolder(addressAndOrders.get(position));
        if (isCart) {
            itemViewHolder.timeSlotHolderTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new TimeSlotBottomSheet((Activity) mContext,addressAndOrders.get(position).getOderDate(), new TimeSlotBottomSheet.OnTimePicked() {
                        @Override
                        public void onTimePicked(Date date, Order order) {
                            addressAndOrders.get(position).setOderDate(date);
                            notifyItemChanged(position);
                            Toast.makeText(mContext, date.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }).showTimeSlotBottomSheet();
                }
            });
        } else {
            itemViewHolder.timeSlotHolderTextView.setOnClickListener(null);
        }
    }

    @Override
    public void onBindViewHeaderHolder(HeaderHolder holder, int position) {

    }

    @Override
    public void onBindViewFooterHolder(FooterHolder holder, int position) {

    }

    @Override
    public int getItemsCount() {
        return addressAndOrders.size();
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_edit_time_slot;
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

    class ItemViewHolder extends ItemHolder {

        private TextView timeSlotTextView, orderNoTextView, itemNamesTextView;
        private LinearLayout timeSlotHolderTextView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            timeSlotTextView = (TextView) itemView.findViewById(R.id.text_view_time_slot);
            orderNoTextView = (TextView) itemView.findViewById(R.id.text_view_order_no);
            itemNamesTextView = (TextView) itemView.findViewById(R.id.text_view_item_names);
            timeSlotHolderTextView = (LinearLayout) itemView.findViewById(R.id.holder_time_slot);
        }

        public void setViewHolder(AddressAndOrder addressAndOrder) {
            itemNamesTextView.setText(addressAndOrder.getOrder().getItemString());
            orderNoTextView.setText("Order No. " + addressAndOrder.getOrder().getId());
            timeSlotTextView.setText(addressAndOrder.getDateString());
        }
    }
}
