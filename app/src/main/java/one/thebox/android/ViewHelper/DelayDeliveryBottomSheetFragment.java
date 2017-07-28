package one.thebox.android.ViewHelper;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.HashMap;

import one.thebox.android.Models.items.UserItem;
import one.thebox.android.Models.items.SubscribeItem;
import one.thebox.android.Models.reschedule.Delivery;
import one.thebox.android.Models.reschedule.Reschedule;
import one.thebox.android.Models.reschedule.RescheduleReason;
import one.thebox.android.R;
import one.thebox.android.adapter.reschedule.RescheduleReasonAdapter;
import one.thebox.android.adapter.viewpager.ViewPagerAdapterReschedule;
import one.thebox.android.api.RequestBodies.MergeSubscriptionRequest;
import one.thebox.android.api.Responses.MergeSubscriptionResponse;
import one.thebox.android.api.Responses.RescheduleResponse;
import one.thebox.android.app.Constants;
import one.thebox.android.app.TheBox;
import one.thebox.android.fragment.reshedule.FragmentRescheduleSubscribeItem;
import one.thebox.android.services.AuthenticationService;
import one.thebox.android.util.CoreGsonUtils;
import one.thebox.android.util.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Ajeet Kumar Meena on 03-05-2016.
 * <p>
 * Updated by Developers on 05/06/2017.
 */
public class DelayDeliveryBottomSheetFragment extends BottomSheetDialogFragment {

    public static final String TAG = "Reschulde Subscribe Item";

    private OnDelayActionCompleted onDelayActionCompleted;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapterReschedule pagerAdapterReschedule;

    private TextView header, arrivingAtText, deliveryDate, skipText;
    private Reschedule rescheduleSkip;
    private View rootView;
    private SubscribeItem subscribeItem;
    private RelativeLayout loader, skipLayout;
    private Dialog dialog;
    private ArrayList<RescheduleReason> rescheduleReasons = new ArrayList<>();
    private RescheduleReason rescheduleReason;
    private int requestCount = 0;

    public DelayDeliveryBottomSheetFragment() {

    }

    public static DelayDeliveryBottomSheetFragment newInstance(SubscribeItem subscribeItem) {

        Bundle args = new Bundle();
        args.putString(Constants.EXTRA_SUBSCRIBE_ITEM, CoreGsonUtils.toJson(subscribeItem));
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
        //fetch Reschedule data from server
        getRescheduleOption();
        return rootView;
    }

    public void initVariable() {
        this.subscribeItem = CoreGsonUtils.fromJson(getArguments().getString(Constants.EXTRA_SUBSCRIBE_ITEM), SubscribeItem.class);
    }


    public void initViews() {
        loader = (RelativeLayout) rootView.findViewById(R.id.loader);
        header = (TextView) rootView.findViewById(R.id.header_title);
        arrivingAtText = (TextView) rootView.findViewById(R.id.arriving_at_text);
        deliveryDate = (TextView) rootView.findViewById(R.id.delivery_date_text);
        skipLayout = (RelativeLayout) rootView.findViewById(R.id.holder_skip_button);
        tabLayout = (TabLayout) rootView.findViewById(R.id.tabLayout);
        viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);
        skipText = (TextView) rootView.findViewById(R.id.skip);


        skipLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * Set Clevertap Event Reschedule Delivery
                 */
                setCleverTapEventRescheduleDelivery();
                //open skip dailog
                openSkipDeliveryDailog();
            }
        });
    }

    public void getRescheduleOption() {
        requestCount++;
        loader.setVisibility(View.VISIBLE);
        TheBox.getAPIService().getRescheduleOption(PrefUtils.getToken(getActivity()), subscribeItem.getUuid())
                .enqueue(new Callback<RescheduleResponse>() {
                    @Override
                    public void onResponse(Call<RescheduleResponse> call, Response<RescheduleResponse> response) {
                        loader.setVisibility(View.GONE);
                        try {
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    //parse response data
                                    parseData(response.body());
                                }
                            } else {
                                if (response.code() == Constants.UNAUTHORIZED) {
                                    new AuthenticationService().navigateToLogin(getActivity());
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            setUIDataWhenFailed();
                            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<RescheduleResponse> call, Throwable t) {
                        if (requestCount == 1) {
                            getRescheduleOption();
                        } else {
                            loader.setVisibility(View.GONE);
                            setUIDataWhenFailed();
                            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public void parseData(RescheduleResponse rescheduleResponse) {
        try {
            ArrayList<Reschedule> tabsList = new ArrayList<>();
            this.rescheduleReasons = rescheduleResponse.getRescheduleReasons();

            for (Reschedule reschedule : rescheduleResponse.getReschedules()) {
                if (reschedule.getPriority() == 1) {
                    this.rescheduleSkip = reschedule;
                } else {
                    tabsList.add(reschedule);
                }
            }

            setUIData(rescheduleResponse);
            setupViewPagerWithTab(tabsList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUIData(RescheduleResponse rescheduleResponse) {
        try {

            final int sdk = android.os.Build.VERSION.SDK_INT;
            if (rescheduleSkip.isVisible()) {
                skipLayout.setClickable(true);
                skipLayout.setEnabled(true);
                skipLayout.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.green_background));
                skipText.setTextColor(getActivity().getResources().getColor(R.color.white));

            } else {
                skipLayout.setClickable(false);
                skipLayout.setEnabled(false);
                skipLayout.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.grey_background));
                skipText.setTextColor(getActivity().getResources().getColor(R.color.divide_color));
            }

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

    public void setUIDataWhenFailed() {
        try {

            header.setText("Reschedule");
            arrivingAtText.setText("Item is " + subscribeItem.getArrivingAt());
            deliveryDate.setText("");

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
                FragmentRescheduleSubscribeItem fragmentRescheduleSubscribeItem =
                        FragmentRescheduleSubscribeItem.getInstance(getActivity(), tabsList.get(i).getMergeDescription(), tabsList.get(i).getDeliveries(), subscribeItem, i);
                fragmentRescheduleSubscribeItem.addListener(onDelayActionCompleted);
                pagerAdapterReschedule.addFragment(fragmentRescheduleSubscribeItem);
            }

            viewPager.setAdapter(pagerAdapterReschedule);
            tabLayout.setupWithViewPager(viewPager);
            viewPager.setOffscreenPageLimit(tabsList.size());
            tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
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

    public void openSkipDeliveryDailog() {
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.dailog_skip_reschedule_delivery);
        dialog.getWindow().getAttributes().width = LinearLayout.LayoutParams.FILL_PARENT;
        dialog.show();

        TextView header = (TextView) dialog.findViewById(R.id.header_title);
        TextView description = (TextView) dialog.findViewById(R.id.text_description);
        TextView cancel = (TextView) dialog.findViewById(R.id.cancel);
        RelativeLayout skip = (RelativeLayout) dialog.findViewById(R.id.holder_skip_button);

        if (rescheduleSkip != null) {
            header.setText(rescheduleSkip.getTitle());

            if (!rescheduleSkip.getDescription().isEmpty()) {
                description.setText(rescheduleSkip.getDescription());
            } else {
                description.setVisibility(View.GONE);
            }
        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rescheduleSkip.getDeliveries().size() > 0) {
                    openDeliverInNextCycleDialog(subscribeItem, rescheduleSkip.getDeliveries().get(0));
                    dialog.dismiss();
                } else {
                    //something went wrong
                }
            }
        });


        dismiss();
    }


    public interface OnDelayActionCompleted {
        void onDelayActionCompleted(SubscribeItem subscribeItem);
    }


    /**
     * Skip Delivery
     */
    private void openDeliverInNextCycleDialog(final SubscribeItem subscribeItem, final Delivery delivery) {
        final Context mContext = dialog.getContext();
        MaterialDialog dialogMaterial = new MaterialDialog.Builder(dialog.getContext())
                .title("Skip Delivery")
                .customView(R.layout.layout_skip_delivery, true)
                .positiveText("Submit")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                        prooceedToReschedule(mContext, delivery, subscribeItem);
                    }
                }).build();


        View customView = dialogMaterial.getCustomView();
        RecyclerView recyclerView = (RecyclerView) customView.findViewById(R.id.reschedule_reason_list);
        LinearLayoutManager manager = new LinearLayoutManager(dialog.getContext());
        recyclerView.setLayoutManager(manager);
        RescheduleReasonAdapter rescheduleReasonAdapter = new RescheduleReasonAdapter(dialog.getContext());
        rescheduleReasonAdapter.setRescheduleReasons(rescheduleReasons);
        rescheduleReasonAdapter.attachListener(new RescheduleReasonAdapter.OnRescheduleReasonActionCompleted() {
            @Override
            public void onRescheduleReason(RescheduleReason rescheduleReason) {
                setRescheduleReason(rescheduleReason);
            }
        });

        recyclerView.setAdapter(rescheduleReasonAdapter);


        dialogMaterial.getWindow().getAttributes().windowAnimations = R.style.MyAnimation_Window;
        dialogMaterial.show();
    }

    public void prooceedToReschedule(Context context, Delivery delivery, SubscribeItem subscribeItem) {
        String rescheduleReasonUuid;
        if (getRescheduleReason() != null) {
            rescheduleReasonUuid = getRescheduleReason().getUuid();
        } else {
            rescheduleReasonUuid = rescheduleReasons.get(0).getUuid();
        }

        final BoxLoader dialog = new BoxLoader(context).show();
        //do network call for reschedule
        TheBox.getAPIService()
                .mergeSubscribeItemWithOrder(PrefUtils.getToken(TheBox.getAppContext()), subscribeItem.getUuid(),
                        new MergeSubscriptionRequest(delivery.getOrderUuid(), rescheduleReasonUuid))
                .enqueue(new Callback<MergeSubscriptionResponse>() {
                    @Override
                    public void onResponse(Call<MergeSubscriptionResponse> call, Response<MergeSubscriptionResponse> response) {
                        dialog.dismiss();
                        try {
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    onDelayActionCompleted.onDelayActionCompleted(response.body().getSubscribeItem());

                                    //setClevertap Event
                                    //setCleverTapEventRescheduleDelivery(response.body().getUserItem(), delivery);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(TheBox.getAppContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<MergeSubscriptionResponse> call, Throwable t) {
                        dialog.dismiss();
                        Toast.makeText(TheBox.getAppContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    /**
     * Clever Tap Event
     */
    public void setCleverTapEventRescheduleDelivery() {
        try {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("reschedule_type", "skip");

            TheBox.getCleverTap().event.push("reschedule_delivery", hashMap);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RescheduleReason getRescheduleReason() {
        return rescheduleReason;
    }

    public void setRescheduleReason(RescheduleReason rescheduleReason) {
        this.rescheduleReason = rescheduleReason;
    }
}
