package one.thebox.android.fragment;

import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.realm.RealmList;
import one.thebox.android.Models.ItemConfig;
import one.thebox.android.R;
import one.thebox.android.adapter.SizeAndFrequencyAdapter;
import one.thebox.android.util.CoreGsonUtils;


public class PriceAndSizeFragment extends BottomSheetDialogFragment {

    private final static String EXTRA_ITEM_CONFIG_ARRAY_LIST = "item_config_array_list";
    private View rootView;
    private RealmList<ItemConfig> itemConfigs = new RealmList<>();
    private RecyclerView recyclerView;
    private ItemConfig selectedItemConfig;
    private SizeAndFrequencyAdapter sizeAndFrequencyAdapter;
    private SizeAndFrequencyBottomSheetDialogFragment.OnSizeAndFrequencySelected onSizeAndFrequencySelected;

    public PriceAndSizeFragment() {
    }

    public static PriceAndSizeFragment newInstance(RealmList<ItemConfig> itemConfigs) {
        PriceAndSizeFragment fragment = new PriceAndSizeFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_ITEM_CONFIG_ARRAY_LIST, CoreGsonUtils.toJson(itemConfigs));
        fragment.setArguments(args);
        return fragment;
    }

    public void setSelectedItemConfig(ItemConfig itemConfig) {
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
        itemConfigs = CoreGsonUtils.fromJsontoRealmList(
                getArguments().getString(EXTRA_ITEM_CONFIG_ARRAY_LIST), ItemConfig.class
        );
    }

    private void setupRecyclerView() {
        sizeAndFrequencyAdapter = new SizeAndFrequencyAdapter(getActivity(), onSizeAndFrequencySelected);
        sizeAndFrequencyAdapter.setItemConfigs(itemConfigs);
        if (selectedItemConfig != null) {
            for (int i = 0; i < itemConfigs.size(); i++) {
                if(itemConfigs.get(i).equals(selectedItemConfig)) {
                    sizeAndFrequencyAdapter.setCurrentItemSelected(i);
                    break;
                }
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
