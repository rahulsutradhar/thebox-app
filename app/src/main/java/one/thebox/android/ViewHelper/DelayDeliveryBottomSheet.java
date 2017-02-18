package one.thebox.android.ViewHelper;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import one.thebox.android.Helpers.OrderHelper;
import one.thebox.android.Models.ItemConfig;
import one.thebox.android.Models.Order;
import one.thebox.android.Models.UserItem;
import one.thebox.android.R;
import one.thebox.android.adapter.BaseRecyclerAdapter;
import one.thebox.android.adapter.DeliverySlotsAdapter;
import one.thebox.android.api.RequestBodies.CancelSubscriptionRequest;
import one.thebox.android.api.Responses.AdjustDeliveryResponse;
import one.thebox.android.api.Responses.CancelSubscriptionResponse;
import one.thebox.android.app.TheBox;
import one.thebox.android.util.DateTimeUtil;
import one.thebox.android.util.PrefUtils;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Ajeet Kumar Meena on 03-05-2016.
 */
public class DelayDeliveryBottomSheet {
    private Activity context;
    private BottomSheetDialog bottomSheetDialog;
    private View bottomSheet;
    private RecyclerView recyclerView;
    private GifImageView progressBar;
    private AdjustDeliverySlotAdapter adjustDeliverySlotAdapter;
    private ArrayList<Order> nextOrder = new ArrayList<>();
    private ArrayList<Order> beforeNextDeliveryOrders = new ArrayList<>();
    private UserItem userItem;
    private OnDelayActionCompleted onDelayActionCompleted;

    public DelayDeliveryBottomSheet(Activity context, OnDelayActionCompleted onDelayActionCompleted) {
        this.context = context;
        bottomSheetDialog = new BottomSheetDialog(context);
        this.onDelayActionCompleted = onDelayActionCompleted;
    }

    public void show(UserItem userItem) {
        this.userItem = userItem;
        bottomSheet = (context).getLayoutInflater().inflate(R.layout.layout_bottom_sheet, null);
        bottomSheetDialog.setContentView(bottomSheet);
        bottomSheetDialog.show();
        initViews();
        getAllOrders();
    }

    public void initViews() {
        recyclerView = (RecyclerView) bottomSheet.findViewById(R.id.recycler_view);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        progressBar = (GifImageView) bottomSheet.findViewById(R.id.progress_bar);
    }

    public void setupRecyclerView() {
        ArrayList<String> reasonString = new ArrayList<>();
        if (beforeNextDeliveryOrders != null && !beforeNextDeliveryOrders.isEmpty()) {
            reasonString.add("Item Finished. I want it early");
        }
        if (nextOrder != null && !nextOrder.isEmpty()) {
            reasonString.add("Item Not Finished Yet. I want to delay delivery");
        }
        adjustDeliverySlotAdapter = new AdjustDeliverySlotAdapter(context, reasonString);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adjustDeliverySlotAdapter);
    }

    public void getAllOrders() {
        TheBox.getAPIService().getAdjustDeliveryOrders(PrefUtils.getToken(context), userItem.getId())
                .enqueue(new Callback<AdjustDeliveryResponse>() {
                             @Override
                             public void onResponse(Call<AdjustDeliveryResponse> call, Response<AdjustDeliveryResponse> response) {
                                 progressBar.setVisibility(View.GONE);
                                 if (response.body() != null) {
                                     if (response.body().isSuccess()) {
                                         nextOrder.addAll(response.body().getNextOrder());
                                         beforeNextDeliveryOrders.addAll(response.body().getOrdersBeforeNextOrder());
                                         setupRecyclerView();
                                     } else {
                                         Toast.makeText(context, response.body().getInfo(), Toast.LENGTH_SHORT).show();
                                     }
                                 }
                             }


                             @Override
                             public void onFailure(Call<AdjustDeliveryResponse> call, Throwable t) {
                                 progressBar.setVisibility(View.GONE);
                             }
                         }

                );
    }

    public interface OnDelayActionCompleted {
        void onDelayActionCompleted(UserItem userItem);
    }

    class AdjustDeliverySlotAdapter extends BaseRecyclerAdapter {

        private ArrayList<String> reasonString = new ArrayList<>();
        private int currentSelection = -1;

        public AdjustDeliverySlotAdapter(Context context, ArrayList<String> reasonString) {
            super(context);
            this.reasonString = reasonString;
            mViewType = RECYCLER_VIEW_TYPE_HEADER_FOOTER;
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
        public void onBindViewItemHolder(ItemHolder holder, final int position) {
            final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            itemViewHolder.radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int temp = currentSelection;
                    currentSelection = position;
                    notifyItemChanged(temp + 1);
                    notifyItemChanged(currentSelection + 1);
                }
            });
            itemViewHolder.setupViews(reasonString.get(position).contains("early") ? beforeNextDeliveryOrders : nextOrder, reasonString.get(position));
        }

        @Override
        public int getItemsCount() {
            return reasonString.size();
        }

        @Override
        protected int getItemLayoutId() {
            return R.layout.item_adjust_dilevery;
        }

        @Override
        protected int getItemLayoutId(int position) {
            return 0;
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
        protected int getFooterLayoutId() {
            return R.layout.footer_deliver_slots;
        }

        @Override
        protected int getHeaderLayoutId() {
            return R.layout.header_delay_delivery;
        }

        @Override
        public void onBindViewFooterHolder(FooterHolder holder, int position) {
            FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
            footerViewHolder.setViews();
        }

        @Override
        public void onBindViewHeaderHolder(HeaderHolder holder, int position) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            headerViewHolder.setViewHolder(userItem);
        }

        class ItemViewHolder extends ItemHolder {

            private RadioButton radioButton;
            private RecyclerView recyclerView;
            private DeliverySlotsAdapter deliverySlotsAdapter;

            public ItemViewHolder(View itemView) {
                super(itemView);
                radioButton = (RadioButton) itemView.findViewById(R.id.radio_button);
                recyclerView = (RecyclerView) itemView.findViewById(R.id.recycler_view);
                recyclerView.setItemViewCacheSize(20);
                recyclerView.setDrawingCacheEnabled(true);
                recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            }

            public void setupViews(ArrayList<Order> orders, String reasonString) {
                radioButton.setText(reasonString);
                if (getAdapterPosition() - 1 == currentSelection) {
                    radioButton.setChecked(true);
                    recyclerView.setVisibility(View.VISIBLE);
                    setupRecyclerView(orders);
                } else {
                    radioButton.setChecked(false);
                    recyclerView.setVisibility(View.GONE);
                }
            }

            public void setupRecyclerView(ArrayList<Order> orders) {
                deliverySlotsAdapter = new DeliverySlotsAdapter(mContext, userItem, bottomSheetDialog, onDelayActionCompleted);
                deliverySlotsAdapter.setOrders(orders);
                recyclerView.setAdapter(deliverySlotsAdapter);
            }
        }

        public class HeaderViewHolder extends HeaderHolder {
            TextView deliveryTextView, arrivingTextView;

            public HeaderViewHolder(View itemView) {
                super(itemView);
                deliveryTextView = (TextView) itemView.findViewById(R.id.delivery_schedule_text_view);
                arrivingTextView = (TextView) itemView.findViewById(R.id.arriving_text_view);
            }

            public void setViewHolder(UserItem userItem) {
                Date orderDate = null;
                orderDate = DateTimeUtil.convertStringToDate(userItem.getNextDeliveryScheduledAt());
                if (userItem.getNextDeliveryScheduledAt() == null) {
                    arrivingTextView.setText("This item is in cart. Order this item now.");
                } else {
                    int days = (int) DateTimeUtil.getDifferenceAsDay(Calendar.getInstance().getTime(), orderDate);
                    if(days > 1) {
                        arrivingTextView.setText("Arriving in " + days+" days");
                    }else {
                        int hours = (int) DateTimeUtil.getDifferenceAsHours(Calendar.getInstance().getTime(), orderDate);
                        if (DateTimeUtil.isArrivingToday(hours)) {
                            arrivingTextView.setText("Arriving Today");
                        } else {
                            arrivingTextView.setText("Arriving Tomorrow");
                        }
                    }
                }
                ItemConfig itemConfig = userItem.getBoxItem().getItemConfigById(userItem.getSelectedConfigId());
                deliveryTextView
                        .setText("Repeats every " + itemConfig.getSubscriptionType());

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
                    public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                        View customView = dialog.getCustomView();
                        RadioGroup radioGroup = (RadioGroup) customView.findViewById(R.id.radio_group);
                        int radioButtonID = radioGroup.getCheckedRadioButtonId();
                        View radioButton = radioGroup.findViewById(radioButtonID);
                        int idx = radioGroup.indexOfChild(radioButton);
                        RadioButton r = (RadioButton) radioGroup.getChildAt(idx);
                        if (r == null) {
                            Toast.makeText(mContext, "Select at least one option", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String selectedtext = r.getText().toString();

                        final BoxLoader loader = new BoxLoader(mContext).show();
                        TheBox.getAPIService().delayDeliveryByOneCycle(PrefUtils.getToken(mContext)
                                , new CancelSubscriptionRequest(userItem.getId(), selectedtext))
                                .enqueue(new Callback<CancelSubscriptionResponse>() {
                                    @Override
                                    public void onResponse(Call<CancelSubscriptionResponse> call, Response<CancelSubscriptionResponse> response) {
                                        loader.dismiss();
                                        if (response.body() != null) {
                                            Toast.makeText(mContext, response.body().getInfo(), Toast.LENGTH_SHORT).show();
                                            if (response.body().isSuccess()) {
                                                OrderHelper.addAndNotify(response.body().getOrders());
                                                bottomSheetDialog.dismiss();
                                                onDelayActionCompleted.onDelayActionCompleted(response.body().getUserItem());
                                                dialog.dismiss();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<CancelSubscriptionResponse> call, Throwable t) {
                                        loader.dismiss();
                                    }
                                });
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
                    public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                        View customView = dialog.getCustomView();
                        RadioGroup radioGroup = (RadioGroup) customView.findViewById(R.id.radio_group);
                        int radioButtonID = radioGroup.getCheckedRadioButtonId();
                        View radioButton = radioGroup.findViewById(radioButtonID);
                        int idx = radioGroup.indexOfChild(radioButton);
                        RadioButton r = (RadioButton) radioGroup.getChildAt(idx);
                        if (r == null) {
                            Toast.makeText(mContext, "Select at least one option", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String selectedtext = r.getText().toString();

                        final BoxLoader loader = new BoxLoader(context).show();
                        TheBox.getAPIService().cancelSubscription(PrefUtils.getToken(mContext)
                                , new CancelSubscriptionRequest(userItem.getId(), selectedtext))
                                .enqueue(new Callback<CancelSubscriptionResponse>() {
                                    @Override
                                    public void onResponse(Call<CancelSubscriptionResponse> call, Response<CancelSubscriptionResponse> response) {
                                        loader.dismiss();
                                        if (response.body() != null) {
                                            Toast.makeText(mContext, response.body().getInfo(), Toast.LENGTH_SHORT).show();
                                            if (response.body().isSuccess()) {
                                                OrderHelper.addAndNotify(response.body().getOrders());
                                                bottomSheetDialog.dismiss();
                                                onDelayActionCompleted.onDelayActionCompleted(null);
                                                dialog.dismiss();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<CancelSubscriptionResponse> call, Throwable t) {
                                        loader.dismiss();
                                    }
                                });
                    }
                }).autoDismiss(false).build();


                dialog.getWindow().getAttributes().windowAnimations = R.style.MyAnimation_Window;
                dialog.show();
            }
        }
    }

}
