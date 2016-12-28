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

        Integer default_position = getArguments().getInt("default_position");
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_my_box_tabs, container, false);
            initViews();
            setupViewPagerAndTabs(default_position);
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

    private void setupViewPagerAndTabs(int default_position) {
        if (getActivity() == null) {
            return;
        }
        progressBar.setVisibility(View.GONE);
        holder.setVisibility(View.VISIBLE);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(), getActivity());
        adapter.addFragment(new MyBoxesFragment(), "My Items");
        adapter.addFragment(new StoreFragment(), "Store");
        adapter.addFragment(UpComingOrderFragment.newInstance(), "My Deliveries");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(default_position);
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
        ((MainActivity) getActivity()).getButtonSearch().setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).getButtonSpecialAction().setOnClickListener(null);
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
