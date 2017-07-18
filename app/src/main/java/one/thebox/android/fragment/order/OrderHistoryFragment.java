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
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

import one.thebox.android.Models.order.CalenderMonth;
import one.thebox.android.Models.order.Order;
import one.thebox.android.R;
import one.thebox.android.adapter.orders.UpcomingOrderAdapter;
import one.thebox.android.api.Responses.order.OrdersResponse;
import one.thebox.android.app.Constants;
import one.thebox.android.app.TheBox;
import one.thebox.android.util.CoreGsonUtils;
import one.thebox.android.util.PrefUtils;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by developers on 10/07/17.
 */

public class OrderHistoryFragment extends Fragment {

    private View rootView;
    private CalenderMonth calenderMonth;
    private int positionInViewPager;
    private RecyclerView recyclerView;
    private UpcomingOrderAdapter adapter;
    private TextView emptyState, noInternet;
    private int currentYear;
    private GifImageView loader;

    public static OrderHistoryFragment getInstance(CalenderMonth calenderMonth, int currentYear, int position) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.EXTRA_CALENDER, CoreGsonUtils.toJson(calenderMonth));
        bundle.putInt(Constants.EXTRA_VIEWPAGER_POSITION, position);
        bundle.putInt(Constants.EXTRA_CALENDER_SELECTED_YEAR, currentYear);
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
        loader = (GifImageView) rootView.findViewById(R.id.loader_gif);
        noInternet = (TextView) rootView.findViewById(R.id.no_internet);

        noInternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noInternet.setVisibility(View.GONE);
                fetchDataFromServer();
            }
        });

    }

    public void initVariable() {
        calenderMonth = CoreGsonUtils.fromJson(getArguments().getString(Constants.EXTRA_CALENDER), CalenderMonth.class);
        positionInViewPager = getArguments().getInt(Constants.EXTRA_VIEWPAGER_POSITION);
        currentYear = getArguments().getInt(Constants.EXTRA_CALENDER_SELECTED_YEAR);
    }

    /**
     * Fetch CalenderMonth from Server
     */
    public void fetchDataFromServer() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("month", calenderMonth.getPriority());
        params.put("year", String.valueOf(currentYear));

        loader.setVisibility(View.VISIBLE);
        TheBox.getAPIService()
                .getOrders(PrefUtils.getToken(getActivity()), params)
                .enqueue(new Callback<OrdersResponse>() {
                    @Override
                    public void onResponse(Call<OrdersResponse> call, Response<OrdersResponse> response) {
                        loader.setVisibility(View.GONE);
                        try {
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    if (response.body().getOrders() != null) {
                                        setUpRecyclerView(response.body().getOrders());
                                    } else {
                                        emptyState.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    emptyState.setVisibility(View.VISIBLE);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<OrdersResponse> call, Throwable t) {
                        loader.setVisibility(View.GONE);
                        noInternet.setVisibility(View.VISIBLE);
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
