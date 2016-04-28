package one.thebox.android.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;

import one.thebox.android.Models.DeliverySlot;
import one.thebox.android.R;

/**
 * Created by Ajeet Kumar Meena on 12-04-2016.
 */
public class DeliverySlotsAdapter extends BaseRecyclerAdapter {

    ArrayList<DeliverySlot> deliverySlots = new ArrayList<>();

    public DeliverySlotsAdapter(Context context) {
        super(context);
        mViewType = RECYCLER_VIEW_TYPE_HEADER_FOOTER;
    }

    public void addDeliveryItems(DeliverySlot deliverySlot) {
        deliverySlots.add(deliverySlot);
    }

    public ArrayList<DeliverySlot> getDeliverySlots() {
        return deliverySlots;
    }

    public void setDeliverySlots(ArrayList<DeliverySlot> deliverySlots) {
        this.deliverySlots = deliverySlots;
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
        return new HeaderViewHolder(view);
    }

    @Override
    protected FooterHolder getFooterHolder(View view) {
        return new FooterViewHolder(view);
    }

    @Override
    public void onBindViewItemHolder(ItemHolder holder, int position) {

    }

    @Override
    public void onBindViewHeaderHolder(HeaderHolder holder, int position) {

    }

    @Override
    public void onBindViewFooterHolder(FooterHolder holder, int position) {
        FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
        footerViewHolder.setViews();
    }

    @Override
    public int getItemsCount() {
        return deliverySlots.size();
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_delivery_slots;
    }

    @Override
    protected int getHeaderLayoutId() {
        return R.layout.header_delay_delivery;
    }

    @Override
    protected int getItemLayoutId(int position) {
        return 0;
    }

    @Override
    protected int getFooterLayoutId() {
        return R.layout.footer_deliver_slots;
    }

    public class ItemViewHolder extends ItemHolder {

        public ItemViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class HeaderViewHolder extends HeaderHolder {

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }

    }

    public class FooterViewHolder extends FooterHolder {
        private TextView buttonCancel;
        private TextView buttonDeliverInNextCycle;

        public FooterViewHolder(View itemView) {
            super(itemView);
            buttonCancel = (TextView) itemView.findViewById(R.id.button_cancel);
            buttonDeliverInNextCycle = (TextView) itemView.findViewById(R.id.button_deliver_in_next_cycle);
        }


        public void setViews() {
            buttonCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openCancelDialog();
                }
            });
            buttonDeliverInNextCycle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDeliverInNextCycleDialog();
                }
            });

        }

        private void openDeliverInNextCycleDialog() {
            MaterialDialog dialog = new MaterialDialog.Builder(mContext).
                    title("Skip Delivery").
                    customView(R.layout.layout_skip_delivery, true).
                    positiveText("Submit").onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                }
            }).build();
            dialog.getWindow().getAttributes().windowAnimations = R.style.MyAnimation_Window;
            dialog.show();
        }

        private void openCancelDialog() {
            MaterialDialog dialog = new MaterialDialog.Builder(mContext).
                    title("Cancel Subscription").
                    customView(R.layout.layout_cancel_subscription, true).
                    positiveText("Submit").onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                }
            }).build();
            dialog.getWindow().getAttributes().windowAnimations = R.style.MyAnimation_Window;
            dialog.show();
        }
    }

}
