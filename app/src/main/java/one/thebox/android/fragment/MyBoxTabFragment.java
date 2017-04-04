package one.thebox.android.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import one.thebox.android.Events.OnCategorySelectEvent;
import one.thebox.android.Events.OnHomeTabChangeEvent;
import one.thebox.android.Models.SearchResult;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.ViewPagerAdapter;
import one.thebox.android.activity.BaseActivity;
import one.thebox.android.activity.MainActivity;
import one.thebox.android.util.CoreGsonUtils;
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
        ((MainActivity) getActivity()).getToolbar().setTitle("My Box");

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
        if (getActivity() == null) {
            return;
        }
        progressBar.setVisibility(View.GONE);
        holder.setVisibility(View.VISIBLE);


        ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(), getActivity());
        Bundle args = new Bundle();
        args.putBoolean("show_loader", show_loader);

        //Attaching "MyItems"
        MyBoxesFragment my_box_fragment = new MyBoxesFragment();
        my_box_fragment.setArguments(args);
        adapter.addFragment(my_box_fragment, "Subscription");

        //Attaching Store
        StoreFragment store_fragment = new StoreFragment();
        store_fragment.setArguments(args);
        adapter.addFragment(store_fragment, "Store");

        //Attaching Deliveries
        UpComingOrderFragment upcoming_deliveries_fragment = new UpComingOrderFragment();
        upcoming_deliveries_fragment.setArguments(args);
        adapter.addFragment(upcoming_deliveries_fragment, "Deliveries");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(default_position);
        viewPager.setOffscreenPageLimit(3);
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
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
        ((MainActivity) getActivity()).getToolbar().setTitle("My Box");
        ((MainActivity) getActivity()).getToolbar().setSubtitle(null);

        ((MainActivity) getActivity()).getSearchViewHolder().setVisibility(View.GONE);

        ((MainActivity) getActivity()).getButtonSpecialAction().setVisibility(View.GONE);
        ((MainActivity) getActivity()).getButtonSpecialAction().setOnClickListener(null);

        ((MainActivity) getActivity()).getButtonSearch().setVisibility(View.VISIBLE);

        ((MainActivity) getActivity()).getChatbutton().setVisibility(View.VISIBLE);

        ((MainActivity) getActivity()).getSearchView().getText().clear();
        ((MainActivity) getActivity()).getSearchAction().setVisibility(View.GONE);
        ((MainActivity) getActivity()).getSearchAction().setOnClickListener(null);

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(((BaseActivity) getActivity()).getContentView().getWindowToken(), 0);
    }

    @Subscribe
    public void OnHomeTabChangeEvent(OnHomeTabChangeEvent onHomeTabChangeEvent) {
        viewPager.setCurrentItem(onHomeTabChangeEvent.getPosition());
    }
}
