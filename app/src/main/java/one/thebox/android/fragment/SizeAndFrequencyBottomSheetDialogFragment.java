package one.thebox.android.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import one.thebox.android.Models.BoxItem;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.ViewPagerAdapter;
import one.thebox.android.util.CoreGsonUtils;

/**
 * Created by Ajeet Kumar Meena on 09-05-2016.
 */
public class SizeAndFrequencyBottomSheetDialogFragment extends BottomSheetDialogFragment {
    public static final String TAG = "Choose size and frequency";
    private static final String EXTRA_BOX_ITEM = "extra_box_item";
    HashMap<String, ArrayList<BoxItem.ItemConfig>> hashMap = new HashMap<>();
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private View rootView;
    private BoxItem boxItem;
    private OnSizeAndFrequencySelected onSizeAndFrequencySelected;

    public static SizeAndFrequencyBottomSheetDialogFragment newInstance(BoxItem boxItem) {

        Bundle args = new Bundle();
        args.putString(EXTRA_BOX_ITEM, CoreGsonUtils.toJson(boxItem));
        SizeAndFrequencyBottomSheetDialogFragment fragment = new SizeAndFrequencyBottomSheetDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void attachListener(OnSizeAndFrequencySelected onSizeAndFrequencySelected) {
        this.onSizeAndFrequencySelected = onSizeAndFrequencySelected;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_change_size_and_frequency, container, false);
        initVariables();
        initViews();
        setupViewPager();
        return rootView;
    }

    private void setupViewPager() {
        int defaultSelectedPosition = 0;
        String keySelectedPosition = boxItem.getSelectedItemConfig().getSubscriptionType();
        Set<String> keySet = hashMap.keySet();
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager(), getActivity());
        int i = 0;
        for (String key : keySet) {

            PriceAndSizeFragment priceAndSizeFragment = PriceAndSizeFragment.newInstance(hashMap.get(key));
            priceAndSizeFragment.addListener(onSizeAndFrequencySelected);
            if (key.equals(keySelectedPosition)) {
                defaultSelectedPosition = i;
                priceAndSizeFragment.setSelectedItemConfig(boxItem.getSelectedItemConfig());
            }
            adapter.addFragment(priceAndSizeFragment, key);
            i++;
        }
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(defaultSelectedPosition, true);
    }

    private void initViews() {
        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);
    }

    private void initVariables() {
        boxItem = CoreGsonUtils.fromJson(getArguments().getString(EXTRA_BOX_ITEM), BoxItem.class);
        hashMap = boxItem.getFrequencyItemConfigHashMap();
    }

    public interface OnSizeAndFrequencySelected {
        void onSizeAndFrequencySelected(BoxItem.ItemConfig selectedItemConfig);
    }
}
