package one.thebox.android.fragment.order;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import one.thebox.android.Models.order.Calender;
import one.thebox.android.Models.order.Order;
import one.thebox.android.R;
import one.thebox.android.adapter.orders.UpcomingOrderAdapter;
import one.thebox.android.api.Responses.order.OrdersResponse;
import one.thebox.android.app.Constants;
import one.thebox.android.app.TheBox;
import one.thebox.android.util.CoreGsonUtils;
import one.thebox.android.util.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by developers on 10/07/17.
 */

public class OrderHistoryFragment extends Fragment {

    private View rootView;
    private Calender calender;
    private int positionInViewPager;
    private RecyclerView recyclerView;
    private UpcomingOrderAdapter adapter;
    private TextView emptyState;

    public static OrderHistoryFragment getInstance(Calender calender, int position) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.EXTRA_CALENDER, CoreGsonUtils.toJson(calender));
        bundle.putInt(Constants.EXTRA_VIEWPAGER_POSITION, position);
        OrderHistoryFragment orderHistoryFragment = new OrderHistoryFragment();
        orderHistoryFragment.setArguments(bundle);
        return orderHistoryFragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_order_history, container, false);
        initView();
        initVariable();
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fetchDataFromServer();
    }

    public void initView() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.list_orders);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        emptyState = (TextView) rootView.findViewById(R.id.empty_state);

    }

    public void initVariable() {
        calender = CoreGsonUtils.fromJson(getArguments().getString(Constants.EXTRA_CALENDER), Calender.class);
        positionInViewPager = getArguments().getInt(Constants.EXTRA_VIEWPAGER_POSITION);
    }

    /**
     * Fetch Calender from Server
     */
    public void fetchDataFromServer() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("month", calender.getPriority());
        params.put("year", "2017");

        TheBox.getAPIService()
                .getOrders(PrefUtils.getToken(getActivity()), params)
                .enqueue(new Callback<OrdersResponse>() {
                    @Override
                    public void onResponse(Call<OrdersResponse> call, Response<OrdersResponse> response) {
                        try {
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    if (response.body().getOrders() != null) {
                                        setUpRecyclerView(response.body().getOrders());
                                    } else {
                                        emptyState.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<OrdersResponse> call, Throwable t) {
                    }
                });
    }

    public void setUpRecyclerView(ArrayList<Order> orders) {
        if (orders.size() > 0) {
            emptyState.setVisibility(View.GONE);
            adapter = new UpcomingOrderAdapter(getActivity(), orders, this);
            final LinearLayoutManager manager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(manager);
            recyclerView.setAdapter(adapter);
        } else {
            emptyState.setVisibility(View.VISIBLE);
        }


    }
}
