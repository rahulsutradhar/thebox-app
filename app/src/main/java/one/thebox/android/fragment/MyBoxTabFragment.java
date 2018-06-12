package one.thebox.android.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import one.thebox.android.Events.FABVisibilityTabFactorEvent;
import one.thebox.android.Events.OnHomeTabChangeEvent;
import one.thebox.android.R;
import one.thebox.android.activity.BaseActivity;
import one.thebox.android.activity.MainActivity;
import one.thebox.android.adapter.viewpager.ViewPagerAdapterHome;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Ajeet Kumar Meena on 08-06-2016.
 */

public class MyBoxTabFragment extends Fragment {


    private TabLayout tabLayout;
    private ViewPager viewPager;
    private View rootView;
    private GifImageView progressBar;
    private LinearLayout holder;
    private boolean show_loader;
    private int default_position = 1;

    public MyBoxTabFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity) getActivity()).getToolbar().setTitle("The Box");

        default_position = getArguments().getInt("default_position");
        show_loader = getArguments().getBoolean("show_loader");

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_my_box_tabs, container, false);
            initViews();
            setupViewPagerAndTabs(default_position, show_loader);
        }
        return rootView;
    }

    private void initViews() {
        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);
        progressBar = (GifImageView) rootView.findViewById(R.id.progress_bar);
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

    private void setupViewPagerAndTabs(int default_position, boolean show_loader) {
        try {
            if (getActivity() == null) {
                return;
            }
            progressBar.setVisibility(View.GONE);
            holder.setVisibility(View.VISIBLE);

            ArrayList<String> listTitle = new ArrayList<>();
            listTitle.add("Subscriptions");
            listTitle.add("Store");
            listTitle.add("Deliveries");


            ViewPagerAdapterHome adapter = new ViewPagerAdapterHome(getActivity().getSupportFragmentManager(),
                    getActivity(), listTitle);
            Bundle args = new Bundle();
            args.putBoolean("show_loader", show_loader);

            //Attaching "MyItems"
            MyBoxesFragment my_box_fragment = new MyBoxesFragment();
            my_box_fragment.setArguments(args);
            adapter.addFragment(my_box_fragment);

            //Attaching Store
            StoreFragment store_fragment = new StoreFragment();
            store_fragment.setArguments(args);
            adapter.addFragment(store_fragment);

            //Attaching Deliveries
            UpComingOrderFragment upcoming_deliveries_fragment = new UpComingOrderFragment();
            upcoming_deliveries_fragment.setArguments(args);
            adapter.addFragment(upcoming_deliveries_fragment);

            viewPager.setAdapter(adapter);
            tabLayout.setupWithViewPager(viewPager);
            viewPager.setCurrentItem(default_position);
            viewPager.setOffscreenPageLimit(3);
            tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
            tabLayout.setTabMode(TabLayout.MODE_FIXED);

            if (listTitle.size() > 0) {
                for (int i = 0; i < tabLayout.getTabCount(); i++) {
                    TabLayout.Tab newTab = tabLayout.getTabAt(i);
                    newTab.setCustomView(adapter.getTabView(i));
                }
            }

            //initial condition
            if (default_position == 2) {
                EventBus.getDefault().post(new FABVisibilityTabFactorEvent(true));
            } else {
                EventBus.getDefault().post(new FABVisibilityTabFactorEvent(false));
            }

            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());
                    if (tab.getPosition() == 2) {
                        EventBus.getDefault().post(new FABVisibilityTabFactorEvent(true));
                    } else {
                        EventBus.getDefault().post(new FABVisibilityTabFactorEvent(false));
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getToolbar().setTitle("The Box");
        ((MainActivity) getActivity()).getToolbar().setSubtitle(null);
        ((MainActivity) getActivity()).getButtonSpecialAction().setVisibility(View.GONE);
        ((MainActivity) getActivity()).getButtonSpecialAction().setOnClickListener(null);
        ((MainActivity) getActivity()).getButtonSearch().setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).getChatbutton().setVisibility(View.VISIBLE);

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(((BaseActivity) getActivity()).getContentView().getWindowToken(), 0);
    }

    @Subscribe
    public void OnHomeTabChangeEvent(OnHomeTabChangeEvent onHomeTabChangeEvent) {
        viewPager.setCurrentItem(onHomeTabChangeEvent.getPosition());
    }
}
