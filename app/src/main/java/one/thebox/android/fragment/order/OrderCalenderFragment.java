package one.thebox.android.fragment.order;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Vector;

import one.thebox.android.Models.order.CalenderMonth;
import one.thebox.android.Models.order.CalenderYear;
import one.thebox.android.R;
import one.thebox.android.adapter.orders.ViewPagerCalenderAdapter;
import one.thebox.android.api.Responses.order.OrdersResponse;
import one.thebox.android.app.Constants;
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
    private TextView selectedYearText;
    private int currentYear, currentMonth;
    private Vector<CalenderYear> calenderYears;
    private ViewPagerCalenderAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_order_calender, container, false);
        initView();
        initVariable();
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fetchDataFromServer(true);
        setYear(String.valueOf(currentYear));

    }

    public void initVariable() {
        currentYear = getArguments().getInt(Constants.EXTRA_CALENDER_SELECTED_YEAR);
        currentMonth = getArguments().getInt(Constants.EXTRA_CALENDER_SELECTED_MONTH);
    }

    public void initView() {
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        toolbar.setTitle("Calender");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getActivity().getSupportFragmentManager().popBackStackImmediate();
                }*/
                getActivity().onBackPressed();
            }
        });

        tabLayout = (TabLayout) rootView.findViewById(R.id.tabLayout);
        viewPager = (ViewPager) rootView.findViewById(R.id.pager);
        selectedYearText = (TextView) rootView.findViewById(R.id.selected_year);

        //click event on year
        selectedYearText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open bottom sheet and show year list
                openYearBottomSheet();
            }
        });

    }

    /**
     * Open Year Selection Bottom Sheet
     */
    public void openYearBottomSheet() {

        final SelectYearBottomSheetDialogFragment fragmentSheet = new
                SelectYearBottomSheetDialogFragment(calenderYears, String.valueOf(currentYear));
        fragmentSheet.show(((AppCompatActivity) getActivity()).getSupportFragmentManager()
                , SelectYearBottomSheetDialogFragment.TAG);
        fragmentSheet.attachListener(new SelectYearBottomSheetDialogFragment.OnYearSelected() {
            @Override
            public void onYearSelected(CalenderYear calenderYear) {
                //TODO get the selected Year
                if (!calenderYear.getName().equalsIgnoreCase(String.valueOf(currentYear))) {
                    destroyTabs();
                    setYear(calenderYear.getName());
                    currentYear = Integer.parseInt(calenderYear.getName());
                    fetchDataFromServer(false);
                }
                fragmentSheet.dismiss();
            }
        });

    }

    /**
     * Fetch CalenderMonth from Server
     */
    public void fetchDataFromServer(final boolean isFirst) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("month", currentMonth);
        params.put("year", currentYear);

        TheBox.getAPIService()
                .getOrders(PrefUtils.getToken(getActivity()), params)
                .enqueue(new Callback<OrdersResponse>() {
                    @Override
                    public void onResponse(Call<OrdersResponse> call, Response<OrdersResponse> response) {
                        try {
                            if (response.isSuccessful()) {
                                if (response.body() != null) {

                                    if (response.body().getCalenderMonths() != null) {
                                        if (isFirst) {
                                            setUpViewPagerAndTab(response.body().getCalenderMonths());
                                            calenderYears = response.body().getCalenderYears();
                                        } else {
                                            //update the list
                                            updateViewPagerAndTab(response.body().getCalenderMonths());
                                        }
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

    public void setUpViewPagerAndTab(Vector<CalenderMonth> calenderMonths) {
        if (calenderMonths.size() > 0) {
            int position = 0;
            adapter = new ViewPagerCalenderAdapter(getChildFragmentManager(), getActivity());
            for (int i = 0; i < calenderMonths.size(); i++) {
                Fragment fragment = OrderHistoryFragment.getInstance(calenderMonths.get(i), i);
                adapter.addFragment(fragment, calenderMonths.get(i));
                if (currentMonth == calenderMonths.get(i).getPriority()) {
                    position = i;
                }
            }
            viewPager.setAdapter(adapter);
            tabLayout.setupWithViewPager(viewPager);
            viewPager.setOffscreenPageLimit(6);
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
            for (int i = 0; i < calenderMonths.size(); i++) {
                if (i == position) {
                    tabLayout.getTabAt(i).setCustomView(adapter.getTabView(i, true));
                } else {
                    tabLayout.getTabAt(i).setCustomView(adapter.getTabView(i, false));
                }
            }
            viewPager.setCurrentItem(position);
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

        }
    }

    public void updateViewPagerAndTab(Vector<CalenderMonth> calenderMonths) {
        if (calenderMonths.size() > 0) {
            int position = 0;
            adapter = new ViewPagerCalenderAdapter(getChildFragmentManager(), getActivity());
            for (int i = 0; i < calenderMonths.size(); i++) {
                Fragment fragment = OrderHistoryFragment.getInstance(calenderMonths.get(i), i);
                adapter.addFragment(fragment, calenderMonths.get(i));

                if (currentMonth == calenderMonths.get(i).getPriority()) {
                    position = i;
                }
            }
            tabLayout.setupWithViewPager(viewPager);
            viewPager.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            for (int i = 0; i < calenderMonths.size(); i++) {
                if (i == position) {
                    tabLayout.getTabAt(i).setCustomView(adapter.getTabView(i, true));
                } else {
                    tabLayout.getTabAt(i).setCustomView(adapter.getTabView(i, false));
                }
            }
            viewPager.setCurrentItem(position);
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


        }
    }

    public void destroyTabs() {
        //tabLayout.removeAllTabs();
        adapter.removeAll();
        adapter = null;
    }

    public void setYear(String year) {
        selectedYearText.setText(year);
        selectedYearText.setPaintFlags(selectedYearText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

    }


}
