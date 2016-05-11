package one.thebox.android.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.util.ArrayList;

import one.thebox.android.Models.Order;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.ViewPagerAdapter;
import one.thebox.android.activity.MainActivity;
import one.thebox.android.api.Responses.OrdersApiResponse;
import one.thebox.android.app.MyApplication;
import one.thebox.android.util.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderTabFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private View rootView;
    private ProgressBar progressBar;
    private LinearLayout holder;
    private ArrayList<Order> cartOrders = new ArrayList<>();
    private ArrayList<Order> subscriptionOrders = new ArrayList<>();

    public OrderTabFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity) getActivity()).getToolbar().setTitle("My Orders");
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_order_tab, container, false);
            initViews();
            getAllOrders();
        }
        return rootView;
    }

    private void initViews() {
        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        holder = (LinearLayout) rootView.findViewById(R.id.holder);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void setupViewPagerAndTabs() {
        if (getActivity() == null) {
            return;
        }
        ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(), getActivity());
        adapter.addFragment(CartFragment.newInstance(cartOrders), "Cart");
        adapter.addFragment(UpComingOrderFragment.newInstance(subscriptionOrders), "Upcoming Order");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorHeight(0);
    }

    @Override
    public void onStart() {
        MainActivity.isSearchFragmentIsAttached = true;
        super.onStart();
    }

    @Override
    public void onStop() {
        MainActivity.isSearchFragmentIsAttached = false;
        super.onStop();
    }

    public void getAllOrders() {
        holder.setVisibility(View.GONE);
        MyApplication.getAPIService().getMyOrders(PrefUtils.getToken(getActivity()))
                .enqueue(new Callback<OrdersApiResponse>() {
                    @Override
                    public void onResponse(Call<OrdersApiResponse> call, Response<OrdersApiResponse> response) {
                        progressBar.setVisibility(View.GONE);
                        holder.setVisibility(View.VISIBLE);
                        if (response.body() != null) {
                            if (response.body().isSuccess()) {
                                if (response.body().getOrders() == null || response.body().getOrders().isEmpty()) {
                                } else {
                                    for (Order order : response.body().getOrders()) {
                                        if (order.isCart()) {
                                            cartOrders.add(order);
                                        } else {
                                            subscriptionOrders.add(order);
                                        }
                                    }
                                    setupViewPagerAndTabs();
                                }
                            } else {

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<OrdersApiResponse> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        holder.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getToolbar().setSubtitle(null);
    }
}
