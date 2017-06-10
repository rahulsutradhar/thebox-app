package one.thebox.android.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Set;
import java.util.TreeMap;

import io.realm.RealmList;
import one.thebox.android.Models.items.BoxItem;
import one.thebox.android.Models.IntStringObject;
import one.thebox.android.Models.items.ItemConfig;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.ViewPagerAdapter;
import one.thebox.android.app.Constants;
import one.thebox.android.util.CoreGsonUtils;
import one.thebox.android.util.IntStringComparator;

/**
 * Created by Ajeet Kumar Meena on 09-05-2016.
 */
public class SizeAndFrequencyBottomSheetDialogFragment extends BottomSheetDialogFragment {
    public static final String TAG = "Choose size and frequency";
    TreeMap<IntStringObject, RealmList<ItemConfig>> hashMap = new TreeMap<>();

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private View rootView;
    private BoxItem boxItem;
    private OnSizeAndFrequencySelected onSizeAndFrequencySelected;
    private RealmList<ItemConfig> itemConfigs = new RealmList<>();
    private ItemConfig selectedItemConfig;


    @SuppressLint("ValidFragment")
    public SizeAndFrequencyBottomSheetDialogFragment(RealmList<ItemConfig> itemConfigs, ItemConfig selectedItemConfig) {
        this.itemConfigs = itemConfigs;
        this.selectedItemConfig = selectedItemConfig;
    }

    public SizeAndFrequencyBottomSheetDialogFragment() {

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
        String keySelectedPosition = selectedItemConfig.getSubscriptionText();
        Set<IntStringObject> keySet = hashMap.keySet();

        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager(), getActivity());
        int i = 0;
        for (IntStringObject key : keySet) {

            PriceAndSizeFragment priceAndSizeFragment = new PriceAndSizeFragment(hashMap.get(key));
            priceAndSizeFragment.addListener(onSizeAndFrequencySelected);
            if (key.getString().equals(keySelectedPosition)) {
                defaultSelectedPosition = i;
                priceAndSizeFragment.setSelectedItemConfig(selectedItemConfig);
            }
            adapter.addFragment(priceAndSizeFragment, key.getString());
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
        //parse the items
        hashMap = getFrequencyItemConfigHashMap();
    }

    public interface OnSizeAndFrequencySelected {
        void onSizeAndFrequencySelected(
                ItemConfig selectedItemConfig);
    }

    /**
     * Group Item Configs
     */
    public TreeMap<IntStringObject, RealmList<ItemConfig>> getFrequencyItemConfigHashMap() {

        TreeMap<IntStringObject, RealmList<ItemConfig>> frequencyItemConfigHashMap = new TreeMap<>(new IntStringComparator());

        for (int i = 0; i < itemConfigs.size(); i++) {
            String subscriptionText = itemConfigs.get(i).getSubscriptionText();
            int subscriptionType = itemConfigs.get(i).getSubscriptionType();
            IntStringObject key = new IntStringObject(subscriptionType, subscriptionText);

            if (frequencyItemConfigHashMap.get(key) == null || frequencyItemConfigHashMap.get(key).isEmpty()) {
                RealmList<ItemConfig> tempItemConfigs = new RealmList<>();
                tempItemConfigs.add(itemConfigs.get(i));
                frequencyItemConfigHashMap.put(key, tempItemConfigs);
            } else {
                RealmList<ItemConfig> tempItemConfig = frequencyItemConfigHashMap.get(key);
                tempItemConfig.add(itemConfigs.get(i));
                frequencyItemConfigHashMap.put(key, tempItemConfig);
            }
        }
        return frequencyItemConfigHashMap;
    }

}
