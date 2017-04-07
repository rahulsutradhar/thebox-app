package one.thebox.android.ViewHelper;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;

import one.thebox.android.Helpers.OrderHelper;
import one.thebox.android.Models.UserItem;
import one.thebox.android.Models.reschedule.Reschedule;
import one.thebox.android.R;
import one.thebox.android.adapter.viewpager.ViewPagerAdapterReschedule;
import one.thebox.android.api.Responses.RescheduleResponse;
import one.thebox.android.api.RequestBodies.CancelSubscriptionRequest;
import one.thebox.android.api.Responses.CancelSubscriptionResponse;
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

    private TextView header, arrivingAtText, deliveryDate;
    private Reschedule rescheduleSkip;
    private View rootView;
    private UserItem userItem;
    private RelativeLayout loader, skipLayout;
    private Dialog dialog;

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

    public void initVariable() {
        this.userItem = CoreGsonUtils.fromJson(getArguments().getString(EXTRA_USER_ITEM), UserItem.class);
    }


    public void initViews() {
        loader = (RelativeLayout) rootView.findViewById(R.id.loader);
        header = (TextView) rootView.findViewById(R.id.header_title);
        arrivingAtText = (TextView) rootView.findViewById(R.id.arriving_at_text);
        deliveryDate = (TextView) rootView.findViewById(R.id.delivery_date_text);
        skipLayout = (RelativeLayout) rootView.findViewById(R.id.holder_skip_button);
        tabLayout = (TabLayout) rootView.findViewById(R.id.tabLayout);
        viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);


        skipLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSkipDeliveryDailog();
            }
        });
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
                FragmentRescheduleUserItem fragmentRescheduleUserItem =
                        FragmentRescheduleUserItem.getInstance(getActivity(), tabsList.get(i).getDeliveries(), userItem, i);
                fragmentRescheduleUserItem.addListener(onDelayActionCompleted);
                pagerAdapterReschedule.addFragment(fragmentRescheduleUserItem);
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
                openDeliverInNextCycleDialog();
                dialog.dismiss();
            }
        });


        dismiss();
    }


    public interface OnDelayActionCompleted {
        void onDelayActionCompleted(UserItem userItem);
    }


    /**
     * Skip Delivery
     */
    private void openDeliverInNextCycleDialog() {
        MaterialDialog dialogMaterial = new MaterialDialog.Builder(dialog.getContext())
                .title("Skip Delivery")
                .customView(R.layout.layout_skip_delivery, true)
                .positiveText("Submit")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                        View customView = dialog.getCustomView();
                        RadioGroup radioGroup = (RadioGroup) customView.findViewById(R.id.radio_group);
                        int radioButtonID = radioGroup.getCheckedRadioButtonId();
                        View radioButton = radioGroup.findViewById(radioButtonID);
                        int idx = radioGroup.indexOfChild(radioButton);
                        RadioButton r = (RadioButton) radioGroup.getChildAt(idx);
                        if (r == null) {
                            Toast.makeText(dialog.getContext(), "Select at least one option", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String selectedtext = r.getText().toString();

                        final BoxLoader loader = new BoxLoader(dialog.getContext()).show();
                        TheBox.getAPIService().delayDeliveryByOneCycle(PrefUtils.getToken(TheBox.getAppContext())
                                , new CancelSubscriptionRequest(userItem.getId(), selectedtext))
                                .enqueue(new Callback<CancelSubscriptionResponse>() {
                                    @Override
                                    public void onResponse(Call<CancelSubscriptionResponse> call, Response<CancelSubscriptionResponse> response) {
                                        loader.dismiss();
                                        if (response.body() != null) {
                                            if (response.body().isSuccess()) {
                                                onDelayActionCompleted.onDelayActionCompleted(response.body().getUserItem());
                                                OrderHelper.updateUserItem(response.body().getUserItem());
                                                Toast.makeText(dialog.getContext(), response.body().getInfo(), Toast.LENGTH_SHORT).show();
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
        dialogMaterial.getWindow().getAttributes().windowAnimations = R.style.MyAnimation_Window;
        dialogMaterial.show();
    }
}
