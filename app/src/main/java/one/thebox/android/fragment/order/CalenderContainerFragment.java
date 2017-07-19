package one.thebox.android.fragment.order;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Vector;

import one.thebox.android.Models.order.CalenderMonth;
import one.thebox.android.R;
import one.thebox.android.adapter.orders.ViewPagerCalenderAdapter;

/**
 * Created by developers on 18/07/17.
 */

public class CalenderContainerFragment extends Fragment {

    private Vector<CalenderMonth> calenderMonths;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerCalenderAdapter adapter;
    private View rootView;
    private int currentMonth, curretnYear;

    /**
     * Constructor
     */
    public CalenderContainerFragment() {

    }

    @SuppressLint("ValidFragment")
    public CalenderContainerFragment(Vector<CalenderMonth> calenderMonths, int currentMonth, int curretnYear) {
        this.calenderMonths = calenderMonths;
        this.currentMonth = currentMonth;
        this.curretnYear = curretnYear;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_calender_container, container, false);
        initView();
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setUpViewPagerAndTab();
    }

    public void initView() {
        tabLayout = (TabLayout) rootView.findViewById(R.id.tabLayout);
        viewPager = (ViewPager) rootView.findViewById(R.id.pager);

    }

    public void setUpViewPagerAndTab() {
        if (calenderMonths.size() > 0) {
            int position = 0;
            adapter = new ViewPagerCalenderAdapter(getChildFragmentManager(), getActivity());
            for (int i = 0; i < calenderMonths.size(); i++) {
                Fragment fragment = OrderHistoryFragment.getInstance(calenderMonths.get(i), curretnYear, i);
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

}
