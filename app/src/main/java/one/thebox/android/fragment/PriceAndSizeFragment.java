package one.thebox.android.fragment;

import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import one.thebox.android.Models.BoxItem;
import one.thebox.android.R;
import one.thebox.android.adapter.SizeAndFrequencyAdapter;
import one.thebox.android.util.CoreGsonUtils;


public class PriceAndSizeFragment extends BottomSheetDialogFragment {

    private final static String EXTRA_ITEM_CONFIG_ARRAY_LIST = "item_config_array_list";
    private View rootView;
    private ArrayList<BoxItem.ItemConfig> itemConfigs = new ArrayList<>();
    private RecyclerView recyclerView;
    private BoxItem.ItemConfig selectedItemConfig;
    private SizeAndFrequencyAdapter sizeAndFrequencyAdapter;
    private SizeAndFrequencyBottomSheetDialogFragment.OnSizeAndFrequencySelected onSizeAndFrequencySelected;

    public PriceAndSizeFragment() {
    }

    public static PriceAndSizeFragment newInstance(ArrayList<BoxItem.ItemConfig> itemConfigs) {
        PriceAndSizeFragment fragment = new PriceAndSizeFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_ITEM_CONFIG_ARRAY_LIST, CoreGsonUtils.toJson(itemConfigs));
        fragment.setArguments(args);
        return fragment;
    }

    public void setSelectedItemConfig(BoxItem.ItemConfig itemConfig) {
        this.selectedItemConfig = itemConfig;
    }

    public void addListener(SizeAndFrequencyBottomSheetDialogFragment.OnSizeAndFrequencySelected onSizeAndFrequencySelected) {
        this.onSizeAndFrequencySelected = onSizeAndFrequencySelected;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_price_and_size, container, false);
            initVariable();
            initViews();
            setupRecyclerView();
        }
        return rootView;
    }

    private void initVariable() {
        itemConfigs = CoreGsonUtils.fromJsontoArrayList(
                getArguments().getString(EXTRA_ITEM_CONFIG_ARRAY_LIST), BoxItem.ItemConfig.class
        );
    }

    private void setupRecyclerView() {
        sizeAndFrequencyAdapter = new SizeAndFrequencyAdapter(getActivity(), onSizeAndFrequencySelected);
        sizeAndFrequencyAdapter.setItemConfigs(itemConfigs);
        if (selectedItemConfig != null) {
            for (int i = 0; i < itemConfigs.size(); i++) {
                sizeAndFrequencyAdapter.setCurrentItemSelected(i);
                break;
            }
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(sizeAndFrequencyAdapter);
    }

    private void initViews() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
