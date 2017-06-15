package one.thebox.android.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import one.thebox.android.Events.OnHomeTabChangeEvent;
import one.thebox.android.Events.UpdateUpcomingDeliveriesEvent;
import one.thebox.android.Models.order.Order;
import one.thebox.android.R;
import one.thebox.android.adapter.orders.UpcomingOrderAdapter;
import one.thebox.android.api.Responses.order.OrdersResponse;
import one.thebox.android.app.Constants;
import one.thebox.android.app.Keys;
import one.thebox.android.app.TheBox;
import one.thebox.android.util.CoreGsonUtils;
import one.thebox.android.util.PrefUtils;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UpComingOrderFragment extends Fragment implements View.OnClickListener {

    private View rootView;
    private RecyclerView recyclerView;
    private UpcomingOrderAdapter upcomingOrderAdapter;
    private LinearLayout no_orders_subscribed_view_holder;
    private GifImageView progress_bar;

    public UpComingOrderFragment() {
    }

    public static UpComingOrderFragment newInstance() {
        UpComingOrderFragment fragment = new UpComingOrderFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_up_coming_order, container, false);
        initViews();

        //request Serve and fetch Order
        getOrdersFromServer();

        return rootView;
    }

    private void initViews() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        no_orders_subscribed_view_holder = (LinearLayout) rootView.findViewById(R.id.no_orders_subscribed_view_holder);
        progress_bar = (GifImageView) rootView.findViewById(R.id.progress_bar);

        no_orders_subscribed_view_holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new OnHomeTabChangeEvent(1));
            }
        });
    }


    private void setupRecyclerView(ArrayList<Order> orders) {
        if (orders.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            no_orders_subscribed_view_holder.setVisibility(View.GONE);

            upcomingOrderAdapter = new UpcomingOrderAdapter(getActivity(), this, orders);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(upcomingOrderAdapter);
        } else {
            recyclerView.setVisibility(View.GONE);
            no_orders_subscribed_view_holder.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * Fetch Orders from Server
     */
    public void getOrdersFromServer() {
        progress_bar.setVisibility(View.VISIBLE);
        TheBox.getAPIService()
                .getOrders(PrefUtils.getToken(getActivity()))
                .enqueue(new Callback<OrdersResponse>() {
                    @Override
                    public void onResponse(Call<OrdersResponse> call, Response<OrdersResponse> response) {
                        progress_bar.setVisibility(View.GONE);
                        try {
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    setupRecyclerView(response.body().getOrders());
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<OrdersResponse> call, Throwable t) {
                        progress_bar.setVisibility(View.GONE);
                    }
                });

    }

    /**
     * Called from Subscription tab to update order
     */
    @Subscribe
    public void onUpdateUpcomingDeliveries(final UpdateUpcomingDeliveriesEvent updateUpcomingDeliveriesEvent) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //update orders as SUbscribe Item has been unsubscribed
                    getOrdersFromServer();
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            //Order Item Activtiy or Confirm TimeSlot Activity
            if (requestCode == 4) {
                if (data.getExtras() != null) {
                    //post event Bus
                    Order order = CoreGsonUtils.fromJson(data.getStringExtra(Constants.EXTRA_ORDER), Order.class);
                    int position = data.getIntExtra(Constants.EXTRA_CLICK_POSITION, -1);

                    if (order != null) {
                        //all item has been removed, so refetch orders
                        if (order.getAmountToPay() == 0 && !order.isPaymentComplete()) {
                            getOrdersFromServer();
                        } else {
                            //partial update the order
                            if (upcomingOrderAdapter != null) {
                                //updates order list item if you update the items
                                upcomingOrderAdapter.updateOrder(order, position);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
