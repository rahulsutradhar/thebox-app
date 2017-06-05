package one.thebox.android.fragment.reshedule;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import one.thebox.android.Models.items.SubscribeItem;
import one.thebox.android.Models.reschedule.Delivery;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.DelayDeliveryBottomSheetFragment;
import one.thebox.android.adapter.reschedule.AdapterRescheduleSubscribeItem;
import one.thebox.android.app.Constants;
import one.thebox.android.util.CoreGsonUtils;

/**
 * Created by developers on 03/04/17.
 */

public class FragmentRescheduleSubscribeItem extends Fragment {

    private static final String EXTRA_POSITION_IN_VIEW_PAGER = "extra_position_of_fragment_in_tab";
    private static final String EXTRA_DELIVERIES = "extra_delivery_Dates";
    private static final String EXTRA_USER_ITEM = "extra_user_item";
    private static final String EXTRA_MERGE_DESCRIPTION = "extra_merge_description";
    public static final int RECYCLER_VIEW_TYPE_HEADER = 301;

    private View rootView;
    private ArrayList<Delivery> deliveries;
    private SubscribeItem subscribeItem;
    private int positionInViewPager;
    private RecyclerView recyclerView;
    private AdapterRescheduleSubscribeItem adapter;
    private LinearLayout emptyState;
    private RelativeLayout loader;
    private DelayDeliveryBottomSheetFragment.OnDelayActionCompleted onDelayActionCompleted;
    private String mergeDescription;


    public static FragmentRescheduleSubscribeItem getInstance(Context context, String mergeDescription, ArrayList<Delivery> deliveries, SubscribeItem subscribeItem, int positionInViewPager) {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_MERGE_DESCRIPTION, mergeDescription);
        bundle.putString(EXTRA_DELIVERIES, CoreGsonUtils.toJson(deliveries));
        bundle.putInt(EXTRA_POSITION_IN_VIEW_PAGER, positionInViewPager);
        bundle.putString(Constants.EXTRA_SUBSCRIBE_ITEM, CoreGsonUtils.toJson(subscribeItem));
        FragmentRescheduleSubscribeItem fragmentRescheduleSubscribeItem = new FragmentRescheduleSubscribeItem();
        fragmentRescheduleSubscribeItem.setArguments(bundle);

        return fragmentRescheduleSubscribeItem;
    }

    /**
     * Constructor
     */
    public FragmentRescheduleSubscribeItem() {

    }

    public void addListener(DelayDeliveryBottomSheetFragment.OnDelayActionCompleted onDelayActionCompleted) {
        this.onDelayActionCompleted = onDelayActionCompleted;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_reschedule_user_item, container, false);
            initView();
            initVariable();

            checkForEmptyState();
        }
        return rootView;
    }

    public void initView() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.list_delivery);
        emptyState = (LinearLayout) rootView.findViewById(R.id.empty_state);
        loader = (RelativeLayout) rootView.findViewById(R.id.loader);
    }

    public void initVariable() {
        positionInViewPager = getArguments().getInt(EXTRA_POSITION_IN_VIEW_PAGER);
        deliveries = CoreGsonUtils.fromJsontoArrayList(getArguments().getString(EXTRA_DELIVERIES), Delivery.class);
        subscribeItem = CoreGsonUtils.fromJson(getArguments().getString(Constants.EXTRA_SUBSCRIBE_ITEM), SubscribeItem.class);
        mergeDescription = getArguments().getString(EXTRA_MERGE_DESCRIPTION);
    }

    public void checkForEmptyState() {
        if (deliveries != null) {
            if (deliveries.size() > 0) {
                emptyState.setVisibility(View.GONE);
                setupRecycelerView();
            } else {
                emptyState.setVisibility(View.VISIBLE);
            }
        } else {
            emptyState.setVisibility(View.VISIBLE);
        }
    }

    public void setupRecycelerView() {

        if (recyclerView != null) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(linearLayoutManager);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            adapter = new AdapterRescheduleSubscribeItem(getActivity(), this, deliveries, subscribeItem, onDelayActionCompleted, mergeDescription);
            adapter.setViewType(RECYCLER_VIEW_TYPE_HEADER);
            recyclerView.setAdapter(adapter);
            recyclerView.setNestedScrollingEnabled(true);
        }
    }


    /**
     * Called from Adapter class
     */
    public void showLoader() {
        loader.setVisibility(View.VISIBLE);
    }

    public void hideLoader() {
        loader.setVisibility(View.GONE);
    }

}

