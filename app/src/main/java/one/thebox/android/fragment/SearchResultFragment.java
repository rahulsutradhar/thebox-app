package one.thebox.android.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import one.thebox.android.R;
import one.thebox.android.activity.MainActivity;


public class SearchResultFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private View rootView;

    public SearchResultFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_search_result, container, false);
            initViews();
            setupViewPagerAndTabs();
        }
        return rootView;
    }

    private void initViews() {
        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);
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
        ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        adapter.addFragment(new AllItemsFragment(), "All Items");
        adapter.addFragment(new MyItemsFragment(), "My Items");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }


    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        @Override
        public int getItemPosition(Object object) {
            return mFragmentList.indexOf(object);
        }
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
}
