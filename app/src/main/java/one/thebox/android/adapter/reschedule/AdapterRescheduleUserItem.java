package one.thebox.android.adapter.reschedule;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import one.thebox.android.Models.reschedule.Delivery;
import one.thebox.android.R;
import one.thebox.android.adapter.BaseRecyclerAdapter;
import one.thebox.android.app.TheBox;

/**
 * Created by developers on 03/04/17.
 */

public class AdapterRescheduleUserItem extends BaseRecyclerAdapter {

    private ArrayList<Delivery> deliveries;
    private Context context;

    /**
     * Constructor
     */
    public AdapterRescheduleUserItem(Context context, ArrayList<Delivery> deliveries) {
        super(context);
        this.context = context;
        this.deliveries = deliveries;
        notifyDataSetChanged();
    }

    public ArrayList<Delivery> getDeliveries() {
        return deliveries;
    }

    public void setDeliveries(ArrayList<Delivery> deliveries) {
        this.deliveries = deliveries;
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
        return new ItemHeaderViewHolder(view);
    }

    @Override
    protected FooterHolder getFooterHolder(View view) {
        return null;
    }

    @Override
    public void onBindViewItemHolder(ItemHolder holder, int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.setView(deliveries.get(position));
    }

    @Override
    public void onBindViewHeaderHolder(HeaderHolder holder, int position) {
        ItemHeaderViewHolder itemHeaderViewHolder = (ItemHeaderViewHolder) holder;


    }

    @Override
    public void onBindViewFooterHolder(FooterHolder holder, int position) {

    }

    @Override
    public int getItemsCount() {
        return deliveries.size();
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.card_item_reschedule_date;
    }

    @Override
    protected int getItemLayoutId(int position) {
        return 0;
    }

    @Override
    protected int getHeaderLayoutId() {
        return R.layout.card_item_reschudule_header;
    }

    @Override
    protected int getFooterLayoutId() {
        return 0;
    }

    private class ItemViewHolder extends BaseRecyclerAdapter.ItemHolder {

        private TextView textViewDeliveryDate, textViewArrivingAt;
        private RelativeLayout merge;


        public ItemViewHolder(View itemView) {
            super(itemView);
            merge = (RelativeLayout) itemView.findViewById(R.id.holder_add_button);
            textViewArrivingAt = (TextView) itemView.findViewById(R.id.arriving_at_text);
            textViewDeliveryDate = (TextView) itemView.findViewById(R.id.delivery_date_text);
        }

        public void setView(Delivery delivery) {
            
            if (delivery.getDeliveryDate() != null) {
                textViewDeliveryDate.setText(delivery.getDeliveryDate());
            }

            if (delivery.getArrivingAt() != null) {
                textViewArrivingAt.setText(delivery.getArrivingAt());
            }

            //Holder merge click event
            merge.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {



                }
            });

        }
    }

    private class ItemHeaderViewHolder extends BaseRecyclerAdapter.HeaderHolder {

        public ItemHeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

}
