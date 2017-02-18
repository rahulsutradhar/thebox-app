package one.thebox.android.adapter;

import android.content.Context;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import one.thebox.android.Helpers.OrderHelper;
import one.thebox.android.Models.AddressAndOrder;
import one.thebox.android.Models.Order;
import one.thebox.android.Models.UserItem;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.BoxLoader;
import one.thebox.android.ViewHelper.DelayDeliveryBottomSheet;
import one.thebox.android.api.RequestBodies.MergeSubscriptionRequest;
import one.thebox.android.api.Responses.MergeSubscriptionResponse;
import one.thebox.android.app.TheBox;
import one.thebox.android.util.DateTimeUtil;
import one.thebox.android.util.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Ajeet Kumar Meena on 12-04-2016.
 */
public class DeliverySlotsAdapter extends BaseRecyclerAdapter {

    private ArrayList<Order> orders = new ArrayList<>();
    private UserItem userItem;
    private BottomSheetDialog bottomSheetDialog;
    private DelayDeliveryBottomSheet.OnDelayActionCompleted onDelayActionCompleted;

    public DeliverySlotsAdapter(Context context, UserItem userItem, BottomSheetDialog bottomSheetDialog, DelayDeliveryBottomSheet.OnDelayActionCompleted onDelayActionCompleted) {
        super(context);
        this.userItem = userItem;
        this.bottomSheetDialog = bottomSheetDialog;
        this.onDelayActionCompleted = onDelayActionCompleted;
    }

    public ArrayList<Order> getOrders() {
        return orders;
    }

    public void setOrders(ArrayList<Order> orders) {
        this.orders = orders;
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
    protected int getFooterLayoutId() {
        return R.layout.footer_deliver_slots;
    }

    @Override
    protected int getHeaderLayoutId() {
        return R.layout.header_delay_delivery;
    }

    @Override
    public void onBindViewFooterHolder(FooterHolder holder, int position) {

    }

    @Override
    public void onBindViewHeaderHolder(HeaderHolder holder, int position) {

    }

    @Override
    public void onBindViewItemHolder(ItemHolder holder, final int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.setViews(orders.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BoxLoader loader =   new BoxLoader(mContext).show();
                TheBox.getAPIService().mergeUserItemWithOrder(PrefUtils.getToken(mContext),
                        new MergeSubscriptionRequest(userItem.getId(), orders.get(position).getId()))
                        .enqueue(new Callback<MergeSubscriptionResponse>() {
                            @Override
                            public void onResponse(Call<MergeSubscriptionResponse> call, Response<MergeSubscriptionResponse> response) {
                                loader.dismiss();
                                if (response.body() != null) {
                                    Toast.makeText(mContext, response.body().getInfo(), Toast.LENGTH_SHORT).show();
                                    if (response.body().isSuccess()) {
                                        bottomSheetDialog.dismiss();
                                        onDelayActionCompleted.onDelayActionCompleted(response.body().getUserItem());
                                        OrderHelper.addAndNotify(response.body().getOrders());

                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<MergeSubscriptionResponse> call, Throwable t) {
                                loader.dismiss();
                            }
                        });
            }
        });
    }


    @Override
    public int getItemsCount() {
        return orders.size();
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_delivery_slots;
    }


    @Override
    protected int getItemLayoutId(int position) {
        return 0;
    }


    public class ItemViewHolder extends ItemHolder {
        private TextView timeTextView, arrivingTextView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            timeTextView = (TextView) itemView.findViewById(R.id.time_text_view);
            arrivingTextView = (TextView) itemView.findViewById(R.id.arriving_time_text_view);
        }

        public void setViews(Order order) {
            Date orderDate = null;
            orderDate = DateTimeUtil.convertStringToDate(order.getDeliveryScheduleAt());
            timeTextView.setText(AddressAndOrder.getDateString(orderDate));
            arrivingTextView.setText("Arriving in " + DateTimeUtil.getDifferenceAsDay(
                    Calendar.getInstance().getTime(), orderDate
            ) + " days");
        }
    }


}
