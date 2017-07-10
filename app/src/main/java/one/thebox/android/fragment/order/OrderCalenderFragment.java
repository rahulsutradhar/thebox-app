package one.thebox.android.fragment.order;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Vector;

import one.thebox.android.Models.order.Calender;
import one.thebox.android.R;
import one.thebox.android.adapter.orders.ViewPagerCalenderAdapter;
import one.thebox.android.api.Responses.order.OrdersResponse;
import one.thebox.android.app.TheBox;
import one.thebox.android.util.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by developers on 10/07/17.
 */

public class OrderCalenderFragment extends Fragment {

    private Toolbar toolbar;
    private View rootView;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_order_calender, container, false);
        initView();
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fetchDataFromServer();
    }

    public void initView() {
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        toolbar.setTitle("Order Calender");
        tabLayout = (TabLayout) rootView.findViewById(R.id.tabLayout);
        viewPager = (ViewPager) rootView.findViewById(R.id.pager);
    }

    /**
     * Fetch Calender from Server
     */
    public void fetchDataFromServer() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("month", "1");
        params.put("year", "2017");

        TheBox.getAPIService()
                .getOrders(PrefUtils.getToken(getActivity()), params)
                .enqueue(new Callback<OrdersResponse>() {
                    @Override
                    public void onResponse(Call<OrdersResponse> call, Response<OrdersResponse> response) {
                        try {
                            if (response.isSuccessful()) {
                                if (response.body() != null) {

                                    if (response.body().getCalenders() != null) {
                                        setUpViewPagerAndTab(response.body().getCalenders());
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

    public void setUpViewPagerAndTab(Vector<Calender> calenders) {
        if (calenders.size() > 0) {
            final ViewPagerCalenderAdapter adapter = new ViewPagerCalenderAdapter(getChildFragmentManager(), getActivity());
            for (int i = 0; i < calenders.size(); i++) {
                Fragment fragment = OrderHistoryFragment.getInstance(calenders.get(i), i);
                adapter.addFragment(fragment, calenders.get(i));
            }
            viewPager.setAdapter(adapter);
            tabLayout.setupWithViewPager(viewPager);
            viewPager.setOffscreenPageLimit(6);
            int length = tabLayout.getTabCount();
            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    tab.setCustomView(adapter.getTabView(tab.getCustomView(), tab.getPosition(), true));
                    viewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    tab.setCustomView(adapter.getTabView(tab.getCustomView(), tab.getPosition(), false));
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
            for (int i = 0; i < length; i++) {
                if (i == 0) {
                    tabLayout.getTabAt(i).setCustomView(adapter.getTabView(i, true));
                } else {
                    tabLayout.getTabAt(i).setCustomView(adapter.getTabView(i, false));
                }
            }
            viewPager.setCurrentItem(0);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

        } else {
            Toast.makeText(getActivity(), "Empty", Toast.LENGTH_SHORT).show();
        }

    }
}
