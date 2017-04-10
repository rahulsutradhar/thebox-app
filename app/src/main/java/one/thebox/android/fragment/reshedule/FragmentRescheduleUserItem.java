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
import android.widget.Toast;

import java.util.ArrayList;

import one.thebox.android.Models.UserItem;
import one.thebox.android.Models.reschedule.Delivery;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.DelayDeliveryBottomSheetFragment;
import one.thebox.android.adapter.reschedule.AdapterRescheduleUserItem;
import one.thebox.android.app.TheBox;
import one.thebox.android.util.CoreGsonUtils;

/**
 * Created by developers on 03/04/17.
 */

public class FragmentRescheduleUserItem extends Fragment {

    private static final String EXTRA_POSITION_IN_VIEW_PAGER = "extra_position_of_fragment_in_tab";
    private static final String EXTRA_DELIVERIES = "extra_delivery_Dates";
    private static final String EXTRA_USER_ITEM = "extra_user_item";
    private static final String EXTRA_MERGE_DESCRIPTION = "extra_merge_description";
    public static final int RECYCLER_VIEW_TYPE_HEADER = 301;

    private View rootView;
    private ArrayList<Delivery> deliveries;
    private UserItem userItem;
    private int positionInViewPager;
    private RecyclerView recyclerView;
    private AdapterRescheduleUserItem adapter;
    private LinearLayout emptyState;
    private RelativeLayout loader;
    private DelayDeliveryBottomSheetFragment.OnDelayActionCompleted onDelayActionCompleted;
    private String mergeDescription;


    public static FragmentRescheduleUserItem getInstance(Context activity, String mergeDescription, ArrayList<Delivery> deliveries, UserItem userItem, int positionInViewPager) {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_MERGE_DESCRIPTION, mergeDescription);
        bundle.putString(EXTRA_DELIVERIES, CoreGsonUtils.toJson(deliveries));
        bundle.putInt(EXTRA_POSITION_IN_VIEW_PAGER, positionInViewPager);
        bundle.putString(EXTRA_USER_ITEM, CoreGsonUtils.toJson(userItem));
        FragmentRescheduleUserItem fragmentRescheduleUserItem = new FragmentRescheduleUserItem();
        fragmentRescheduleUserItem.setArguments(bundle);

        return fragmentRescheduleUserItem;
    }

    /**
     * Constructor
     */
    public FragmentRescheduleUserItem() {

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
        userItem = CoreGsonUtils.fromJson(getArguments().getString(EXTRA_USER_ITEM), UserItem.class);
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
            adapter = new AdapterRescheduleUserItem(getActivity(), this, deliveries, userItem, onDelayActionCompleted, mergeDescription);
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

