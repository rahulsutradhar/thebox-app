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

import one.thebox.android.R;
import one.thebox.android.ViewHelper.ViewPagerAdapter;
import one.thebox.android.activity.MainActivity;
import pl.droidsonroids.gif.GifImageView;

public class OrderTabFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private View rootView;
    private GifImageView progressBar;
    private LinearLayout holder;


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
            setupViewPagerAndTabs();
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

    private void setupViewPagerAndTabs() {
        if (getActivity() == null) {
            return;
        }
        progressBar.setVisibility(View.GONE);
        holder.setVisibility(View.VISIBLE);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(), getActivity());
        adapter.addFragment(CartFragment.newInstance(), "Cart");
        adapter.addFragment(UpComingOrderFragment.newInstance(), "Upcoming Order");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
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

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getToolbar().setSubtitle(null);
        ((MainActivity) getActivity()).getSearchViewHolder().setVisibility(View.GONE);
        ((MainActivity) getActivity()).getButtonSpecialAction().setVisibility(View.GONE);
        ((MainActivity) getActivity()).getButtonSpecialAction().setOnClickListener(null);
    }
}
