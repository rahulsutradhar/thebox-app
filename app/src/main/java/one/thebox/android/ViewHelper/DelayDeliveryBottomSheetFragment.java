package one.thebox.android.ViewHelper;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import one.thebox.android.Models.UserItem;
import one.thebox.android.Models.reschedule.Reschedule;
import one.thebox.android.R;
import one.thebox.android.adapter.viewpager.ViewPagerAdapterReschedule;
import one.thebox.android.api.Responses.RescheduleResponse;
import one.thebox.android.app.TheBox;
import one.thebox.android.fragment.reshedule.FragmentRescheduleUserItem;
import one.thebox.android.util.CoreGsonUtils;
import one.thebox.android.util.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Ajeet Kumar Meena on 03-05-2016.
 */
public class DelayDeliveryBottomSheetFragment extends BottomSheetDialogFragment {

    private static final String EXTRA_USER_ITEM = "extra_user_item";
    public static final String TAG = "Reschulde User item";

    private OnDelayActionCompleted onDelayActionCompleted;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapterReschedule pagerAdapterReschedule;

    private TextView header, arrivingAtText, deliveryDate, skip;
    private Reschedule rescheduleSkip;
    private View rootView;
    private UserItem userItem;
    private RelativeLayout loader;

    public DelayDeliveryBottomSheetFragment() {

    }

    public static DelayDeliveryBottomSheetFragment newInstance(UserItem userItem) {

        Bundle args = new Bundle();
        args.putString(EXTRA_USER_ITEM, CoreGsonUtils.toJson(userItem));
        DelayDeliveryBottomSheetFragment delayDeliveryBottomSheetFragment = new DelayDeliveryBottomSheetFragment();
        delayDeliveryBottomSheetFragment.setArguments(args);
        return delayDeliveryBottomSheetFragment;
    }

    public void attachListener(OnDelayActionCompleted onDelayActionCompleted) {
        this.onDelayActionCompleted = onDelayActionCompleted;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_bottom_sheet, container, false);
        initViews();
        initVariable();
        getRescheduleOption();
        return rootView;
    }


    /*public void show(UserItem userItem, FragmentManager fragmentManager) {
            this.userItem = userItem;
            this.fragmentManager = fragmentManager;
            bottomSheet = (context).getLayoutInflater().inflate(R.layout.layout_bottom_sheet, null);
            bottomSheetDialog.setContentView(bottomSheet);
            bottomSheetDialog.show();
            initViews();
            getRescheduleOption();
        }
    */

    public void initVariable() {
        this.userItem = CoreGsonUtils.fromJson(getArguments().getString(EXTRA_USER_ITEM), UserItem.class);
    }


    public void initViews() {
        loader = (RelativeLayout) rootView.findViewById(R.id.loader);
        header = (TextView) rootView.findViewById(R.id.header_title);
        arrivingAtText = (TextView) rootView.findViewById(R.id.arriving_at_text);
        deliveryDate = (TextView) rootView.findViewById(R.id.delivery_date_text);
        skip = (TextView) rootView.findViewById(R.id.skip);
        tabLayout = (TabLayout) rootView.findViewById(R.id.tabLayout);
        viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);


    }

    public void getRescheduleOption() {

        loader.setVisibility(View.VISIBLE);
        TheBox.getAPIService().getRescheduleOption(PrefUtils.getToken(getActivity()), userItem.getId())
                .enqueue(new Callback<RescheduleResponse>() {
                    @Override
                    public void onResponse(Call<RescheduleResponse> call, Response<RescheduleResponse> response) {
                        loader.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                parseData(response.body());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<RescheduleResponse> call, Throwable t) {
                        loader.setVisibility(View.GONE);
                    }
                });

    }

    public void parseData(RescheduleResponse rescheduleResponse) {
        ArrayList<Reschedule> tabsList = new ArrayList<>();

        for (Reschedule reschedule : rescheduleResponse.getReschedules()) {
            if (reschedule.getPriority() == 1) {
                this.rescheduleSkip = reschedule;
            } else {
                tabsList.add(reschedule);
            }
        }

        setData(rescheduleResponse);
        setupViewPagerWithTab(tabsList);
    }

    public void setData(RescheduleResponse rescheduleResponse) {
        try {

            if (rescheduleResponse.getTitle() != null) {
                if (!rescheduleResponse.getTitle().isEmpty()) {
                    header.setText(rescheduleResponse.getTitle());
                }
            }

            if (rescheduleResponse.getNextArrivingAt() != null) {
                if (!rescheduleResponse.getNextArrivingAt().isEmpty()) {
                    arrivingAtText.setText(rescheduleResponse.getNextArrivingAt());
                }
            }

            if (rescheduleResponse.getNextDeliveryAt() != null) {
                if (!rescheduleResponse.getNextDeliveryAt().isEmpty()) {
                    deliveryDate.setText(rescheduleResponse.getNextDeliveryAt());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setupViewPagerWithTab(ArrayList<Reschedule> tabsList) {
        if (getActivity() == null) {
            return;
        }
        try {
            pagerAdapterReschedule = new ViewPagerAdapterReschedule(getChildFragmentManager(), getActivity(), tabsList);
            for (int i = 0; i < tabsList.size(); i++) {
                pagerAdapterReschedule.addFragment(FragmentRescheduleUserItem.getInstance(getActivity(), tabsList.get(i).getDeliveries(), i));
            }

            viewPager.setAdapter(pagerAdapterReschedule);
            tabLayout.setupWithViewPager(viewPager);
            viewPager.setOffscreenPageLimit(tabsList.size());
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
            if (tabsList.size() > 2) {
                tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
            } else {
                tabLayout.setTabMode(TabLayout.MODE_FIXED);
            }


            if (tabsList.size() > 0) {
                for (int i = 0; i < tabLayout.getTabCount(); i++) {
                    TabLayout.Tab newTab = tabLayout.getTabAt(i);
                    newTab.setCustomView(pagerAdapterReschedule.getTabView(i));
                }
            }

            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
            viewPager.setCurrentItem(0);
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        } catch (NullPointerException n) {
            n.printStackTrace();
        }

    }


    public interface OnDelayActionCompleted {
        void onDelayActionCompleted(UserItem userItem);
    }

   /* class AdjustDeliverySlotAdapter extends BaseRecyclerAdapter {

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
         //   itemViewHolder.setupViews(reasonString.get(position).contains("early") ? beforeNextDeliveryOrders : nextOrder, reasonString.get(position));
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
    }*/

}
